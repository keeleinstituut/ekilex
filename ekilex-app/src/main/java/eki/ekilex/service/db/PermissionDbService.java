package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.FreeformType;

@Component
public class PermissionDbService {

	private DSLContext create;

	public PermissionDbService(DSLContext context) {
		create = context;
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
