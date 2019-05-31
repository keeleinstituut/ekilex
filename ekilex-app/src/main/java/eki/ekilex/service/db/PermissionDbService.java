package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.EKI_USER;
import static eki.ekilex.data.db.Tables.EKI_USER_APPLICATION;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.SelectSelectStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserPermData;

@Component
public class PermissionDbService implements SystemConstant {

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
						enablePendingField.as("enable_pending"))
				.from(EKI_USER)
				.orderBy(EKI_USER.ID)
				.fetchInto(EkiUserPermData.class);
	}

	public List<DatasetPermission> getDatasetPermissions(Long userId) {

		return create
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.DATASET_CODE,
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM,
						DATASET_PERMISSION.AUTH_LANG)
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

	public List<Dataset> getUserPermDatasets(Long userId) {
		Condition userIsAdminCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_ADMIN.isTrue())));
		Condition datasetPermCond = DSL
				.exists(DSL
						.select(DATASET_PERMISSION.ID)
						.from(DATASET_PERMISSION)
						.where(DATASET_PERMISSION.DATASET_CODE.eq(DATASET.CODE).and(DATASET_PERMISSION.USER_ID.eq(userId))));
		return create
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DATASET.IS_VISIBLE.isTrue().and(DSL.or(userIsAdminCond, datasetPermCond)))
				.orderBy(DATASET.ORDER_BY)
				.fetchInto(Dataset.class);
	}

	public List<Dataset> getUserOwnedDatasets(Long userId) {
		Condition userIsAdminCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_ADMIN.isTrue())));
		Condition datasetPermCond = DSL
				.exists(DSL
						.select(DATASET_PERMISSION.ID)
						.from(DATASET_PERMISSION)
						.where(
								DATASET_PERMISSION.DATASET_CODE.eq(DATASET.CODE)
										.and(DATASET_PERMISSION.USER_ID.eq(userId))
										.and(DATASET_PERMISSION.AUTH_OPERATION.eq(AuthorityOperation.OWN.name()))));
		return create
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DATASET.IS_VISIBLE.isTrue().and(DSL.or(userIsAdminCond, datasetPermCond)))
				.orderBy(DATASET.ORDER_BY)
				.fetchInto(Dataset.class);
	}

	public List<Classifier> getUserDatasetLanguages(Long userId, String datasetCode, String classifierLabelLang, String classifierLabelTypeCode) {
		Condition userIsAdminCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_ADMIN.isTrue())));
		Condition datasetPermCond = DSL
				.exists(DSL
						.select(DATASET_PERMISSION.ID)
						.from(DATASET_PERMISSION)
						.where(
								DATASET_PERMISSION.USER_ID.eq(userId)
										.and(DATASET_PERMISSION.DATASET_CODE.eq(datasetCode))
										.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(LANGUAGE.CODE)))));
		return create
				.select(
						DSL.field(DSL.value(ClassifierName.LANGUAGE.name())).as("name"),
						LANGUAGE_LABEL.CODE,
						LANGUAGE_LABEL.VALUE)
				.from(LANGUAGE, LANGUAGE_LABEL)
				.where(
						LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)
								.and(LANGUAGE_LABEL.LANG.eq(classifierLabelLang))
								.and(LANGUAGE_LABEL.TYPE.eq(classifierLabelTypeCode))
								.and(DSL.or(userIsAdminCond, datasetPermCond)))
				.orderBy(LANGUAGE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public Long createDatasetPermission(Long userId, String datasetCode, AuthorityItem authItem, AuthorityOperation authOp, String authLang) {

		eki.ekilex.data.db.tables.DatasetPermission edp = DATASET_PERMISSION.as("edp");
		SelectSelectStep<Record5<Long, String, String, String, String>> select = DSL.select(
				DSL.field(DSL.val(userId)),
				DSL.field(DSL.val(datasetCode)),
				DSL.field(DSL.val(authItem.name())),
				DSL.field(DSL.val(authOp.name())),
				DSL.field(DSL.val(authLang)));

		Condition authLangCond;
		if (StringUtils.isBlank(authLang)) {
			authLangCond = edp.AUTH_LANG.isNull();
		} else {
			authLangCond = edp.AUTH_LANG.eq(authLang);
		}

		return create
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
														.and(authLangCond))))
				.returning(DATASET_PERMISSION.ID)
				.fetchOne()
				.getId();
	}

	public void deleteDatasetPermission(Long datasetPermissionId) {

		create.deleteFrom(DATASET_PERMISSION).where(DATASET_PERMISSION.ID.eq(datasetPermissionId)).execute();
	}

	public boolean isGrantedForWord(Long userId, Long wordId, String authItem, List<String> authOps) {

		Table<Record1<Integer>> lp = DSL
				.select(DSL.field(DSL.count(LEXEME.ID)).as("lex_count"))
				.from(WORD.leftOuterJoin(LEXEME).on(
						LEXEME.WORD_ID.eq(WORD.ID)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(authOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(WORD.LANG)))))))
				.where(WORD.ID.eq(wordId))
				.groupBy(WORD.ID)
				.asTable("lp");

		Table<Record1<Integer>> la = DSL
				.select(DSL.field(DSL.count(LEXEME.ID)).as("lex_count"))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId))
				.groupBy(LEXEME.WORD_ID)
				.asTable("la");

		return create
				.select(DSL.field(lp.field("lex_count", Integer.class).eq(la.field("lex_count", Integer.class))).as("is_granted"))
				.from(lp, la)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForMeaning(Long userId, Long meaningId, String authItem, List<String> authOps) {

		Table<Record1<Integer>> lp = DSL
				.select(DSL.field(DSL.count(LEXEME.ID)).as("lex_count"))
				.from(MEANING.leftOuterJoin(LEXEME).on(
						LEXEME.MEANING_ID.eq(MEANING.ID)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(authOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))))))
				.where(MEANING.ID.eq(meaningId))
				.groupBy(MEANING.ID)
				.asTable("lp");

		Table<Record1<Integer>> la = DSL
				.select(DSL.field(DSL.count(LEXEME.ID)).as("lex_count"))
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId))
				.groupBy(LEXEME.MEANING_ID)
				.asTable("la");

		return create
				.select(DSL.field(lp.field("lex_count", Integer.class).eq(la.field("lex_count", Integer.class))).as("is_granted"))
				.from(lp, la)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForLexeme(Long userId, Long lexemeId, String authItem, List<String> authOps) {

		return create
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(LEXEME)
				.where(
						LEXEME.ID.eq(lexemeId)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(authOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE)))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForDefinition(Long userId, Long definitionId, String authItem, List<String> authOps) {

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
																				.and(DATASET_PERMISSION.AUTH_OPERATION.in(authOps))
																				.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem))
																				.and(DATASET_PERMISSION.DATASET_CODE.eq(DEFINITION_DATASET.DATASET_CODE))
																				.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(DEFINITION.LANG))))))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForUsage(Long userId, Long usageId, String authItem, List<String> authOps) {

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
																				.and(DATASET_PERMISSION.AUTH_OPERATION.in(authOps))
																				.and(DATASET_PERMISSION.AUTH_ITEM.eq(authItem))
																				.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
																				.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(FREEFORM.LANG))))))))
				.fetchSingleInto(Boolean.class);
	}

	public DatasetPermission getDatasetPermission(Long id) {

		return
			create
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.AUTH_LANG,
						DATASET_PERMISSION.DATASET_CODE,
						DATASET_PERMISSION.USER_ID,
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM)
				.from(DATASET_PERMISSION)
				.where(DATASET_PERMISSION.ID.eq(id))
				.fetchSingleInto(DatasetPermission.class);

	}
}
