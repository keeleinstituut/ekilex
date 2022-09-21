package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.EKI_USER;
import static eki.ekilex.data.db.Tables.EKI_USER_APPLICATION;
import static eki.ekilex.data.db.Tables.EKI_USER_PROFILE;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.WORD_FORUM;
import static org.jooq.impl.DSL.field;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.SelectSelectStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.OrderingField;
import eki.common.constant.PermConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.data.db.tables.DatasetPermission;
import eki.ekilex.data.db.tables.EkiUser;
import eki.ekilex.data.db.tables.EkiUserApplication;
import eki.ekilex.data.db.tables.EkiUserProfile;

@Component
public class PermissionDbService implements SystemConstant, GlobalConstant, PermConstant {

	@Autowired
	private DSLContext create;

	public List<EkiUserPermData> getUsers(
			String userNameFilter, String userPermDatasetCodeFilter, Boolean userEnablePendingFilter, OrderingField orderBy) {

		EkiUser eu = EKI_USER.as("eu");
		EkiUserApplication eua = EKI_USER_APPLICATION.as("eua");
		DatasetPermission dsp = DATASET_PERMISSION.as("dsp");

		Condition enablePendingCond = DSL.exists(DSL
				.select(eua.ID)
				.from(eua)
				.where(eua.USER_ID.eq(eu.ID)
						.and(eua.IS_REVIEWED.isFalse())));

		Condition where = DSL.noCondition();
		if (StringUtils.isNotBlank(userNameFilter)) {
			String userNameFilterLike = "%" + StringUtils.lowerCase(userNameFilter) + "%";
			where = where.and(DSL.or(DSL.lower(eu.NAME).like(userNameFilterLike), DSL.lower(eu.EMAIL).like(userNameFilterLike)));
		}
		if (StringUtils.isNotBlank(userPermDatasetCodeFilter)) {
			where = where.andExists(DSL
					.select(dsp.ID)
					.from(dsp)
					.where(
							dsp.USER_ID.eq(eu.ID)
							.and(dsp.DATASET_CODE.eq(userPermDatasetCodeFilter))));
		}
		if (Boolean.TRUE.equals(userEnablePendingFilter)) {
			where = where.and(enablePendingCond);
		}

		SortField<?> orderByField;
		if (orderBy == OrderingField.NAME) {
			orderByField = eu.NAME.asc();
		} else if (orderBy == OrderingField.DATE) {
			orderByField = eu.CREATED.asc();
		} else {
			orderByField = eu.NAME.asc();
		}

		Field<Boolean> epf = field(enablePendingCond);
		Field<Boolean> akef = field(eu.API_KEY.isNotNull());

		return create
				.select(
						eu.ID,
						eu.NAME,
						eu.EMAIL,
						akef.as("api_key_exists"),
						eu.IS_API_CRUD.as("api_crud"),
						eu.IS_ADMIN.as("admin"),
						eu.IS_MASTER.as("master"),
						eu.IS_ENABLED.as("enabled"),
						eu.REVIEW_COMMENT,
						eu.CREATED.as("created_on"),
						epf.as("enable_pending"))
				.from(eu)
				.where(where)
				.orderBy(orderByField)
				.fetchInto(EkiUserPermData.class);
	}

	public List<eki.ekilex.data.DatasetPermission> getDatasetPermissions(Long userId) {

		return create
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.USER_ID,
						DATASET_PERMISSION.DATASET_CODE,
						DATASET.NAME.as("dataset_name"),
						DATASET.IS_SUPERIOR.as("is_superior_dataset"),
						DSL.field(DATASET.CODE.eq(DATASET_XXX)).as("is_superior_permission"),
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM,
						DATASET_PERMISSION.AUTH_LANG,
						LANGUAGE_LABEL.VALUE.as("auth_lang_value"))
				.from(DATASET_PERMISSION
						.innerJoin(DATASET).on(DATASET.CODE.eq(DATASET_PERMISSION.DATASET_CODE))
						.leftOuterJoin(LANGUAGE_LABEL).on(LANGUAGE_LABEL.CODE.eq(DATASET_PERMISSION.AUTH_LANG)
								.and(LANGUAGE_LABEL.LANG.eq(CLASSIF_LABEL_LANG_EST))
								.and(LANGUAGE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(DATASET_PERMISSION.USER_ID.eq(userId))
				.orderBy(
						DATASET.NAME,
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM,
						DATASET_PERMISSION.AUTH_LANG)
				.fetchInto(eki.ekilex.data.DatasetPermission.class);
	}

	public eki.ekilex.data.DatasetPermission getDatasetPermission(Long id) {

		return create
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.USER_ID,
						DATASET.NAME.as("dataset_name"),
						DATASET.IS_SUPERIOR.as("is_superior_dataset"),
						DSL.field(DATASET.CODE.eq(DATASET_XXX)).as("is_superior_permission"),
						DATASET_PERMISSION.AUTH_LANG,
						DATASET_PERMISSION.DATASET_CODE,
						DATASET_PERMISSION.AUTH_OPERATION,
						DATASET_PERMISSION.AUTH_ITEM)
				.from(DATASET_PERMISSION
						.innerJoin(DATASET).on(DATASET.CODE.eq(DATASET_PERMISSION.DATASET_CODE)))
				.where(DATASET_PERMISSION.ID.eq(id))
				.fetchSingleInto(eki.ekilex.data.DatasetPermission.class);

	}

	public List<Dataset> getUserVisibleDatasets(Long userId) {
		Condition userIsAdminCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_ADMIN.isTrue())));
		Condition userIsMasterCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_MASTER.isTrue())));
		Condition datasetPermCond = DSL
				.exists(DSL
						.select(DATASET_PERMISSION.ID)
						.from(DATASET_PERMISSION)
						.where(DATASET_PERMISSION.DATASET_CODE.eq(DATASET.CODE).and(DATASET_PERMISSION.USER_ID.eq(userId))));
		return create
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DSL.or(DATASET.IS_VISIBLE.isTrue(), userIsAdminCond, userIsMasterCond, datasetPermCond))
				.orderBy(DATASET.NAME)
				.fetchInto(Dataset.class);
	}

	public List<Dataset> getUserPermDatasets(Long userId) {
		Condition userIsAdminCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_ADMIN.isTrue())));
		Condition userIsMasterCond = DSL
				.exists(DSL
						.select(EKI_USER.ID)
						.from(EKI_USER)
						.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_MASTER.isTrue())));
		Condition datasetPermCond = DSL
				.exists(DSL
						.select(DATASET_PERMISSION.ID)
						.from(DATASET_PERMISSION)
						.where(DATASET_PERMISSION.DATASET_CODE.eq(DATASET.CODE).and(DATASET_PERMISSION.USER_ID.eq(userId))));
		return create
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DSL.or(userIsAdminCond, userIsMasterCond, datasetPermCond))
				.orderBy(DATASET.NAME)
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
				.where(DSL.or(userIsAdminCond, datasetPermCond))
				.orderBy(DATASET.NAME)
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
						field(DSL.value(ClassifierName.LANGUAGE.name())).as("name"),
						LANGUAGE_LABEL.CODE,
						LANGUAGE_LABEL.VALUE)
				.from(LANGUAGE, LANGUAGE_LABEL)
				.where(
						LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)
								.and(LANGUAGE_LABEL.LANG.eq(classifierLabelLang))
								.and(LANGUAGE_LABEL.TYPE.eq(classifierLabelTypeCode))
								.and(DSL.or(userIsAdminCond, DSL.and(datasetPermCond))))
				.orderBy(LANGUAGE.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	public Long createDatasetPermission(Long userId, String datasetCode, AuthorityItem authItem, AuthorityOperation authOp, String authLang) {

		DatasetPermission edp = DATASET_PERMISSION.as("edp");
		SelectSelectStep<Record5<Long, String, String, String, String>> select = DSL.select(
				field(DSL.val(userId)),
				field(DSL.val(datasetCode)),
				field(DSL.val(authItem.name())),
				field(DSL.val(authOp.name())),
				field(DSL.val(authLang)));

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

	public void deleteDatasetPermissions(String datasetCode) {

		EkiUserProfile up = EKI_USER_PROFILE.as("up");
		DatasetPermission dp = DATASET_PERMISSION.as("dp");
		create
				.update(up)
				.set(up.RECENT_DATASET_PERMISSION_ID, (Long) null)
				.whereExists(DSL
						.select(dp.ID)
						.from(dp)
						.where(dp.ID.eq(up.RECENT_DATASET_PERMISSION_ID).and(dp.DATASET_CODE.eq(datasetCode))))
				.execute();

		create.deleteFrom(DATASET_PERMISSION).where(DATASET_PERMISSION.DATASET_CODE.eq(datasetCode)).execute();
	}

	public void deleteDatasetPermission(Long datasetPermissionId) {

		create
				.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID, (Long) null)
				.where(EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID.eq(datasetPermissionId))
				.execute();

		create.deleteFrom(DATASET_PERMISSION).where(DATASET_PERMISSION.ID.eq(datasetPermissionId)).execute();
	}

	public boolean isGrantedForWord(Long userId, eki.ekilex.data.DatasetPermission userRole, Long wordId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		Table<Record1<Integer>> lsc = DSL
				.select(field(DSL.count(LEXEME.ID)).as("sup_lex_count"))
				.from(WORD.leftOuterJoin(LEXEME).on(
						LEXEME.WORD_ID.eq(WORD.ID)
								.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(WORD.LANG)))))
								.andExists(DSL
										.select(DATASET.CODE)
										.from(DATASET)
										.where(DATASET.CODE.eq(LEXEME.DATASET_CODE)
												.and(DATASET.IS_SUPERIOR.eq(true))))))
				.where(WORD.ID.eq(wordId))
				.asTable("lsc");

		Table<Record1<Integer>> lpc = DSL
				.select(field(DSL.count(LEXEME.ID)).as("perm_lex_count"))
				.from(WORD.leftOuterJoin(LEXEME).on(
						LEXEME.WORD_ID.eq(WORD.ID)
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(WORD.LANG)))))))
				.where(WORD.ID.eq(wordId))
				.groupBy(WORD.ID)
				.asTable("lpc");

		Table<Record1<Integer>> lac = DSL
				.select(field(DSL.count(LEXEME.ID)).as("all_lex_count"))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(wordId))
				.groupBy(LEXEME.WORD_ID)
				.asTable("lac");

		return create
				.select(field(DSL.or(
						lsc.field("sup_lex_count", Integer.class).gt(0),
						lpc.field("perm_lex_count", Integer.class).eq(lac.field("all_lex_count", Integer.class)))).as("is_granted"))
				.from(lsc, lpc, lac)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForSuperiorWord(Long userId, eki.ekilex.data.DatasetPermission userRole, Long wordId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(WORD.leftOuterJoin(LEXEME).on(
						LEXEME.WORD_ID.eq(WORD.ID)
								.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(WORD.LANG)))))
								.andExists(DSL
										.select(DATASET.CODE)
										.from(DATASET)
										.where(DATASET.CODE.eq(LEXEME.DATASET_CODE)
												.and(DATASET.IS_SUPERIOR.eq(true))))))
				.where(WORD.ID.eq(wordId))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForWordByLexeme(Long userId, eki.ekilex.data.DatasetPermission userRole, Long wordId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(WORD.leftOuterJoin(LEXEME).on(
						LEXEME.WORD_ID.eq(WORD.ID)
								.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
														.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(WORD.LANG)))))))
				.where(WORD.ID.eq(wordId))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForMeaning(Long userId, eki.ekilex.data.DatasetPermission userRole, Long meaningId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		Table<Record1<Integer>> lsc = DSL
				.select(field(DSL.count(LEXEME.ID)).as("sup_lex_count"))
				.from(MEANING.leftOuterJoin(LEXEME).on(
						LEXEME.MEANING_ID.eq(MEANING.ID)
								.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))))
								.andExists(DSL
										.select(DATASET.CODE)
										.from(DATASET)
										.where(DATASET.CODE.eq(LEXEME.DATASET_CODE)
												.and(DATASET.IS_SUPERIOR.eq(true))))))
				.where(MEANING.ID.eq(meaningId))
				.asTable("lsc");

		Table<Record1<Integer>> lpc = DSL
				.select(field(DSL.count(LEXEME.ID)).as("perm_lex_count"))
				.from(MEANING.leftOuterJoin(LEXEME).on(
						LEXEME.MEANING_ID.eq(MEANING.ID)
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))))))
				.where(MEANING.ID.eq(meaningId))
				.groupBy(MEANING.ID)
				.asTable("lpc");

		Table<Record1<Integer>> la = DSL
				.select(field(DSL.count(LEXEME.ID)).as("all_lex_count"))
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId))
				.groupBy(LEXEME.MEANING_ID)
				.asTable("la");

		return create
				.select(field(DSL.or(
						lsc.field("sup_lex_count", Integer.class).gt(0),
						lpc.field("perm_lex_count", Integer.class).eq(la.field("all_lex_count", Integer.class)))).as("is_granted"))
				.from(lsc, lpc, la)
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForSuperiorMeaning(Long userId, eki.ekilex.data.DatasetPermission userRole, Long meaningId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(MEANING.leftOuterJoin(LEXEME).on(
						LEXEME.MEANING_ID.eq(MEANING.ID)
								.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))))
								.andExists(DSL
										.select(DATASET.CODE)
										.from(DATASET)
										.where(DATASET.CODE.eq(LEXEME.DATASET_CODE)
												.and(DATASET.IS_SUPERIOR.eq(true))))))
				.where(MEANING.ID.eq(meaningId))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForMeaningByAnyLexeme(Long userId, Long meaningId, String requiredAuthItem, List<String> requiredAuthOps) {

		return create
				.select(field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(MEANING.leftOuterJoin(LEXEME).on(
						LEXEME.MEANING_ID.eq(MEANING.ID)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))))))
				.where(MEANING.ID.eq(meaningId))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForMeaningByLexeme(Long userId, eki.ekilex.data.DatasetPermission userRole, Long meaningId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(MEANING.leftOuterJoin(LEXEME).on(
						LEXEME.MEANING_ID.eq(MEANING.ID)
								.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
								.and(providedRequiredAuthCond)
								.andExists(DSL
										.select(DATASET_PERMISSION.ID)
										.from(DATASET_PERMISSION)
										.where(
												DATASET_PERMISSION.USER_ID.eq(userId)
														.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
														.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
														.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))))))
				.where(MEANING.ID.eq(meaningId))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForLexeme(Long userId, eki.ekilex.data.DatasetPermission userRole, Long lexemeId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(LEXEME.ID).gt(0)).as("is_granted"))
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId)
						.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
						.and(providedRequiredAuthCond)
						.andExists(DSL
								.select(DATASET_PERMISSION.ID)
								.from(DATASET_PERMISSION)
								.where(
										DATASET_PERMISSION.USER_ID.eq(userId)
												.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
												.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
												.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE)))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForDefinition(Long userId, eki.ekilex.data.DatasetPermission userRole, Long definitionId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthLang = userRole.getAuthLang();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedLangCond;
		if (StringUtils.isBlank(providedAuthLang)) {
			providedLangCond = DSL.trueCondition();
		} else {
			providedLangCond = DEFINITION.LANG.eq(providedAuthLang);
		}

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(DEFINITION.ID).gt(0)).as("is_granted"))
				.from(DEFINITION)
				.where(DEFINITION.ID.eq(definitionId)
						.and(providedLangCond)
						.and(providedRequiredAuthCond)
						.andExists(DSL
								.select(DEFINITION_DATASET.DEFINITION_ID)
								.from(DEFINITION_DATASET)
								.where(
										DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION.ID)
												.and(DEFINITION_DATASET.DATASET_CODE.eq(providedDatasetCode))
												.andExists(DSL
														.select(DATASET_PERMISSION.ID)
														.from(DATASET_PERMISSION)
														.where(
																DATASET_PERMISSION.USER_ID.eq(userId)
																		.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
																		.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
																		.and(DATASET_PERMISSION.DATASET_CODE.eq(DEFINITION_DATASET.DATASET_CODE))
																		.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(DEFINITION.LANG))))))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForUsage(Long userId, eki.ekilex.data.DatasetPermission userRole, Long usageId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthLang = userRole.getAuthLang();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		Condition providedLangCond;
		if (StringUtils.isBlank(providedAuthLang)) {
			providedLangCond = DSL.trueCondition();
		} else {
			providedLangCond = FREEFORM.LANG.eq(providedAuthLang);
		}

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return create
				.select(field(DSL.count(FREEFORM.ID).gt(0)).as("is_granted"))
				.from(FREEFORM)
				.where(FREEFORM.ID.eq(usageId)
						.and(FREEFORM.TYPE.eq(FreeformType.USAGE.name()))
						.and(providedLangCond)
						.and(providedRequiredAuthCond)
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME, LEXEME_FREEFORM)
								.where(
										LEXEME.DATASET_CODE.eq(providedDatasetCode)
												.and(LEXEME_FREEFORM.LEXEME_ID.eq(LEXEME.ID))
												.and(LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID))
												.andExists(DSL
														.select(DATASET_PERMISSION.ID)
														.from(DATASET_PERMISSION)
														.where(
																DATASET_PERMISSION.USER_ID.eq(userId)
																		.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
																		.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
																		.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
																		.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(FREEFORM.LANG))))))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForSource(Long userId, eki.ekilex.data.DatasetPermission userRole, Long sourceId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();
		if (!requiredAuthItem.equals(providedAuthItem) || !requiredAuthOps.contains(providedAuthOp)) {
			return false;
		}

		Table<Record1<String>> dffds = DSL
				.select(DEFINITION_DATASET.DATASET_CODE)
				.from(DEFINITION_DATASET, DEFINITION_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION_FREEFORM.DEFINITION_ID)
						.and(DEFINITION_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID))
						.and(FREEFORM_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
				.asTable("dffds");

		Table<Record1<String>> mffds = DSL
				.select(LEXEME.DATASET_CODE)
				.from(LEXEME, MEANING_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(LEXEME.MEANING_ID.eq(MEANING_FREEFORM.MEANING_ID)
						.and(MEANING_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID))
						.and(FREEFORM_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
				.asTable("mffds");

		Table<Record1<String>> lffds = DSL
				.select(LEXEME.DATASET_CODE).from(LEXEME, LEXEME_FREEFORM, FREEFORM_SOURCE_LINK)
				.where(LEXEME.ID.eq(LEXEME_FREEFORM.LEXEME_ID)
						.and(LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM_SOURCE_LINK.FREEFORM_ID))
						.and(FREEFORM_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
				.asTable("lffds");

		Table<Record1<String>> dds = DSL
				.select(DEFINITION_DATASET.DATASET_CODE)
				.from(DEFINITION_DATASET, DEFINITION_SOURCE_LINK)
				.where(DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION_SOURCE_LINK.DEFINITION_ID)
						.and(DEFINITION_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
				.asTable("dds");

		Table<Record1<String>> lds = DSL
				.select(LEXEME.DATASET_CODE)
				.from(LEXEME, LEXEME_SOURCE_LINK)
				.where(LEXEME.ID.eq(LEXEME_SOURCE_LINK.LEXEME_ID)
						.and(LEXEME_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
				.asTable("lds");

		Table<Record1<String>> weds = DSL
				.select(LEXEME.DATASET_CODE)
				.from(LEXEME, WORD_ETYMOLOGY, WORD_ETYMOLOGY_SOURCE_LINK)
				.where(LEXEME.WORD_ID.eq(WORD_ETYMOLOGY.WORD_ID)
						.and(WORD_ETYMOLOGY.ID.eq(WORD_ETYMOLOGY_SOURCE_LINK.WORD_ETYM_ID))
						.and(WORD_ETYMOLOGY_SOURCE_LINK.SOURCE_ID.eq(sourceId)))
				.asTable("weds");

		Table<Record1<String>> sds = DSL
				.selectFrom(dffds)
				.unionAll(DSL.selectFrom(mffds))
				.unionAll(DSL.selectFrom(lffds))
				.unionAll(DSL.selectFrom(dds))
				.unionAll(DSL.selectFrom(lds))
				.unionAll(DSL.selectFrom(weds))
				.asTable("sds");

		List<String> linkedDatasets = create
				.selectDistinct(sds.field("dataset_code", String.class))
				.from(sds).fetchInto(String.class);

		List<String> permittedDatasets = create
				.select(DATASET_PERMISSION.DATASET_CODE)
				.from(DATASET_PERMISSION)
				.where(
						DATASET_PERMISSION.USER_ID.eq(userId)
								.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
								.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem)))
				.fetchInto(String.class);

		return CollectionUtils.containsAll(permittedDatasets, linkedDatasets);
	}

	public boolean isGrantedForWordForum(Long userId, Long wordForumId) {

		return create
				.select(field(DSL.count(WORD_FORUM.ID).eq(1)).as("is_granted"))
				.from(WORD_FORUM)
				.where(WORD_FORUM.ID.eq(wordForumId).and(WORD_FORUM.CREATOR_ID.eq(userId)))
				.fetchSingleInto(Boolean.class);

	}

	public boolean isGrantedForMeaningForum(Long userId, Long meaningForumId) {

		return create
				.select(field(DSL.count(MEANING_FORUM.ID).eq(1)).as("is_granted"))
				.from(MEANING_FORUM)
				.where(MEANING_FORUM.ID.eq(meaningForumId).and(MEANING_FORUM.CREATOR_ID.eq(userId)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isMasterUser(Long userId) {

		return create
				.select(field(DSL.count(EKI_USER.ID).eq(1)).as("is_master"))
				.from(EKI_USER)
				.where(EKI_USER.ID.eq(userId).and(EKI_USER.IS_MASTER.isTrue()))
				.fetchSingleInto(Boolean.class);
	}
}
