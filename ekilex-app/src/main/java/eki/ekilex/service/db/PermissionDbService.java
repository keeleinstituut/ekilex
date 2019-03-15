package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.EKI_USER;
import static eki.ekilex.data.db.Tables.EKI_USER_APPLICATION;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record5;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.FreeformType;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserPermData;

@Component
public class PermissionDbService {

	private DSLContext create;

	public PermissionDbService(DSLContext context) {
		create = context;
	}

	public List<EkiUserPermData> getUsers() {

		Field<Boolean> enablePendingField = DSL.field(
				EKI_USER.IS_ENABLED.isNull()
				.andExists(DSL
						.select(EKI_USER_APPLICATION.ID)
						.from(EKI_USER_APPLICATION)
						.where(EKI_USER_APPLICATION.USER_ID.eq(EKI_USER.ID))));

		return create
				.select(
						EKI_USER.ID,
						EKI_USER.NAME,
						EKI_USER.EMAIL,
						EKI_USER.IS_ADMIN.as("admin"),
						EKI_USER.IS_ENABLED.as("enabled"),
						enablePendingField.as("enable_pending")
						)
				.from(EKI_USER)
				.orderBy(EKI_USER.ID)
				.fetchInto(EkiUserPermData.class);
	}

	public List<DatasetPermission> getUserDatasetPermissions(Long userId) {

		return create
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.DATASET_CODE,
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM,
						DATASET_PERMISSION.AUTH_LANG
						)
				.from(DATASET_PERMISSION, DATASET)
				.where(
						DATASET_PERMISSION.USER_ID.eq(userId)
						.and(DATASET_PERMISSION.DATASET_CODE.eq(DATASET.CODE)))
				.orderBy(
						DATASET.ORDER_BY,
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM,
						DATASET_PERMISSION.AUTH_LANG)
				.fetchInto(DatasetPermission.class);
	}

	public void createDatasetPermission(Long userId, String datasetCode, AuthorityItem authItem, AuthorityOperation authOp, String authLang) {

		eki.ekilex.data.db.tables.DatasetPermission edp = DATASET_PERMISSION.as("edp");
		SelectSelectStep<Record5<Long, String, String, String, String>> select = DSL.select(
				DSL.field(DSL.val(userId)),
				DSL.field(DSL.val(datasetCode)),
				DSL.field(DSL.val(authItem.name())),
				DSL.field(DSL.val(authOp.name())),
				DSL.field(DSL.val(authLang)));

		create
			.insertInto(
				DATASET_PERMISSION,
				DATASET_PERMISSION.USER_ID,
				DATASET_PERMISSION.DATASET_CODE,
				DATASET_PERMISSION.AUTH_ITEM,
				DATASET_PERMISSION.AUTH_OPERATION,
				DATASET_PERMISSION.AUTH_LANG)
			.select(
					select
					.whereNotExists(DSL
							.select(edp.ID)
							.from(edp)
							.where(
									edp.USER_ID.eq(userId)
									.and(edp.DATASET_CODE.eq(datasetCode))
									.and(edp.AUTH_ITEM.eq(authItem.name()))
									.and(edp.AUTH_OPERATION.eq(authOp.name()))
									)
							)
					)
			.execute();
	}

	public void deleteDatasetPermission(Long datasetPermissionId) {

		create.deleteFrom(DATASET_PERMISSION).where(DATASET_PERMISSION.ID.eq(datasetPermissionId)).execute();
	}

	public boolean isGrantedForLexeme(Long userId, Long lexemeId, AuthorityOperation authOp, AuthorityItem authItem) {

		return create
				.select(DSL.field((DSL.count(LEXEME.ID).gt(0))).as("is_granted"))
				.from(LEXEME)
				.where(
						LEXEME.ID.eq(lexemeId)
						.andExists(DSL
								.select(DATASET_PERMISSION.ID)
								.from(DATASET_PERMISSION)
								.where(
										DATASET_PERMISSION.USER_ID.eq(userId)
										.and(DATASET_PERMISSION.AUTH_OPERATION.eq(authOp.name()))
										.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem.name()))
										.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
										)
								)
						)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForLexeme(Long userId, Long lexemeId, AuthorityOperation authOp, AuthorityItem authItem, String authLang) {

		return create
				.select(DSL.field((DSL.count(LEXEME.ID).gt(0))).as("is_granted"))
				.from(LEXEME)
				.where(
						LEXEME.ID.eq(lexemeId)
						.andExists(DSL
								.select(DATASET_PERMISSION.ID)
								.from(DATASET_PERMISSION)
								.where(
										DATASET_PERMISSION.USER_ID.eq(userId)
										.and(DATASET_PERMISSION.AUTH_OPERATION.eq(authOp.name()))
										.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem.name()))
										.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
										.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(authLang)))
										)
								)
						)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForDefinition(Long userId, Long definitionId, AuthorityOperation authOp, AuthorityItem authItem) {

		return create
				.select(DSL.field((DSL.count(DEFINITION.ID).gt(0))).as("is_granted"))
				.from(DEFINITION)
				.where(
						DEFINITION.ID.eq(definitionId)
						.andExists(DSL
								.select(DEFINITION_DATASET.DEFINITION_ID)
								.from(DEFINITION_DATASET)
								.where(
										DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION.ID)
										.andExists(DSL
												.select(DATASET_PERMISSION.ID)
												.from(DATASET_PERMISSION)
												.where(
														DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.eq(authOp.name()))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem.name()))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(DEFINITION_DATASET.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(DEFINITION.LANG)))
														)
												)
										)
								)
						)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForUsage(Long userId, Long usageId, AuthorityOperation authOp, AuthorityItem authItem) {

		return create
				.select(DSL.field((DSL.count(FREEFORM.ID).gt(0))).as("is_granted"))
				.from(FREEFORM)
				.where(
						FREEFORM.ID.eq(usageId)
						.and(FREEFORM.TYPE.eq(FreeformType.USAGE.name()))
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME, LEXEME_FREEFORM)
								.where(
										LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)
										.and(LEXEME_FREEFORM.LEXEME_ID.eq(LEXEME.ID))
										.andExists(DSL
												.select(DATASET_PERMISSION.ID)
												.from(DATASET_PERMISSION)
												.where(
														DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.eq(authOp.name()))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem.name()))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(FREEFORM.LANG)))
														)
												)
										)
								)
						)
				.fetchSingleInto(Boolean.class);
	}
}
