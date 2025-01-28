package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.main.Tables.EKI_USER;
import static eki.ekilex.data.db.main.Tables.EKI_USER_APPLICATION;
import static eki.ekilex.data.db.main.Tables.EKI_USER_PROFILE;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_FORUM;
import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Optional;

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
import eki.common.constant.GlobalConstant;
import eki.common.constant.OrderingField;
import eki.common.constant.PermConstant;
import eki.ekilex.constant.ApplicationStatus;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.data.db.main.tables.DatasetPermission;
import eki.ekilex.data.db.main.tables.EkiUser;
import eki.ekilex.data.db.main.tables.EkiUserApplication;
import eki.ekilex.data.db.main.tables.EkiUserProfile;
import eki.ekilex.data.db.main.tables.records.DatasetPermissionRecord;

@Component
public class PermissionDbService implements SystemConstant, GlobalConstant, PermConstant {

	@Autowired
	private DSLContext mainDb;

	public List<EkiUserPermData> getUsers(
			String userNameFilter, String userPermDatasetCodeFilter, Boolean userEnablePendingFilter, OrderingField orderBy) {

		EkiUser eu = EKI_USER.as("eu");
		EkiUserApplication eua = EKI_USER_APPLICATION.as("eua");
		DatasetPermission dsp = DATASET_PERMISSION.as("dsp");

		Condition enablePendingCond = DSL.exists(DSL
				.select(eua.ID)
				.from(eua)
				.where(
						eua.USER_ID.eq(eu.ID)
								.and(eua.STATUS.eq(ApplicationStatus.NEW.name()))));

		Condition permissionDatasetCond = DSL.exists(DSL
				.select(dsp.ID)
				.from(dsp)
				.where(
						dsp.USER_ID.eq(eu.ID)
								.and(dsp.DATASET_CODE.eq(userPermDatasetCodeFilter))));

		Condition applicationDatasetCond = DSL.exists(DSL
				.select(eua.ID)
				.from(eua)
				.where(
						eua.USER_ID.eq(eu.ID)
								.and(eua.DATASET_CODE.eq(userPermDatasetCodeFilter))));

		Condition where = DSL.noCondition();
		if (StringUtils.isNotBlank(userNameFilter)) {
			String userNameFilterLike = "%" + StringUtils.lowerCase(userNameFilter) + "%";
			where = where.and(DSL.or(DSL.lower(eu.NAME).like(userNameFilterLike), DSL.lower(eu.EMAIL).like(userNameFilterLike)));
		}
		if (StringUtils.isNotBlank(userPermDatasetCodeFilter)) {
			where = where.and(DSL.or(permissionDatasetCond, applicationDatasetCond));
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

		return mainDb
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

		return mainDb
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.USER_ID,
						DATASET_PERMISSION.DATASET_CODE,
						DATASET.NAME.as("dataset_name"),
						DATASET.IS_SUPERIOR.as("is_superior_dataset"),
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

		return mainDb
				.select(
						DATASET_PERMISSION.ID,
						DATASET_PERMISSION.USER_ID,
						DATASET.NAME.as("dataset_name"),
						DATASET.IS_SUPERIOR.as("is_superior_dataset"),
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
		return mainDb
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DSL.or(DATASET.IS_VISIBLE.isTrue(), userIsAdminCond, userIsMasterCond, datasetPermCond))
				.orderBy(DATASET.NAME)
				.fetchInto(Dataset.class);
	}

	public List<Dataset> userVisibleNonPublicDatasets(Long userId) {
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
		return mainDb
				.select(DATASET.CODE, DATASET.NAME, DATASET.IS_PUBLIC)
				.from(DATASET)
				.where(
						DSL.or(DATASET.IS_VISIBLE.isTrue(), userIsAdminCond, userIsMasterCond, datasetPermCond)
								.and(DATASET.IS_PUBLIC.isFalse()))
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
		return mainDb
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
		return mainDb
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DSL.or(userIsAdminCond, datasetPermCond))
				.orderBy(DATASET.NAME)
				.fetchInto(Dataset.class);
	}

	public List<Classifier> getUserDatasetLanguages(Long userId, String datasetCode, String classifierLabelLang) {

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

		return mainDb
				.select(
						field(DSL.value(ClassifierName.LANGUAGE.name())).as("name"),
						LANGUAGE_LABEL.CODE,
						LANGUAGE_LABEL.VALUE)
				.from(LANGUAGE, LANGUAGE_LABEL)
				.where(
						LANGUAGE.CODE.eq(LANGUAGE_LABEL.CODE)
								.and(LANGUAGE_LABEL.LANG.eq(classifierLabelLang))
								.and(LANGUAGE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
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

		Optional<DatasetPermissionRecord> optionalDatasetPermission = mainDb
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
				.fetchOptional();

		if (optionalDatasetPermission.isPresent()) {
			DatasetPermissionRecord datasetPermission = optionalDatasetPermission.get();
			return datasetPermission.getId();
		} else {
			return null;
		}
	}

	public void deleteDatasetPermissions(String datasetCode) {

		EkiUserProfile up = EKI_USER_PROFILE.as("up");
		DatasetPermission dp = DATASET_PERMISSION.as("dp");
		mainDb
				.update(up)
				.set(up.RECENT_DATASET_PERMISSION_ID, (Long) null)
				.whereExists(DSL
						.select(dp.ID)
						.from(dp)
						.where(dp.ID.eq(up.RECENT_DATASET_PERMISSION_ID).and(dp.DATASET_CODE.eq(datasetCode))))
				.execute();

		mainDb.deleteFrom(DATASET_PERMISSION).where(DATASET_PERMISSION.DATASET_CODE.eq(datasetCode)).execute();
	}

	public void deleteDatasetPermission(Long datasetPermissionId) {

		mainDb
				.update(EKI_USER_PROFILE)
				.set(EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID, (Long) null)
				.where(EKI_USER_PROFILE.RECENT_DATASET_PERMISSION_ID.eq(datasetPermissionId))
				.execute();

		mainDb.deleteFrom(DATASET_PERMISSION).where(DATASET_PERMISSION.ID.eq(datasetPermissionId)).execute();
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

		return mainDb
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

		return mainDb
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

		return mainDb
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
				.from(MEANING
						.leftOuterJoin(LEXEME).on(
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

		return mainDb
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

		return mainDb
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

		return mainDb
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

		return mainDb
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

		return mainDb
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

	public boolean isGrantedForLexemeNote(Long userId, eki.ekilex.data.DatasetPermission userRole, Long lexemeNoteId, String requiredAuthItem, List<String> requiredAuthOps) {

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
			providedLangCond = LEXEME_NOTE.LANG.eq(providedAuthLang);
		}

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return mainDb
				.select(field(DSL.count(LEXEME_NOTE.ID).gt(0)).as("is_granted"))
				.from(LEXEME_NOTE)
				.where(LEXEME_NOTE.ID.eq(lexemeNoteId)
						.and(providedLangCond)
						.and(providedRequiredAuthCond)
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME)
								.where(
										LEXEME.ID.eq(LEXEME_NOTE.LEXEME_ID)
												.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
												.andExists(DSL
														.select(DATASET_PERMISSION.ID)
														.from(DATASET_PERMISSION)
														.where(
																DATASET_PERMISSION.USER_ID.eq(userId)
																		.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
																		.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
																		.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
																		.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(LEXEME_NOTE.LANG))))))))
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
			providedLangCond = USAGE.LANG.eq(providedAuthLang);
		}

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return mainDb
				.select(field(DSL.count(USAGE.ID).gt(0)).as("is_granted"))
				.from(USAGE)
				.where(USAGE.ID.eq(usageId)
						.and(providedLangCond)
						.and(providedRequiredAuthCond)
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME)
								.where(
										LEXEME.ID.eq(USAGE.LEXEME_ID)
												.and(LEXEME.DATASET_CODE.eq(providedDatasetCode))
												.andExists(DSL
														.select(DATASET_PERMISSION.ID)
														.from(DATASET_PERMISSION)
														.where(
																DATASET_PERMISSION.USER_ID.eq(userId)
																		.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
																		.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
																		.and(DATASET_PERMISSION.DATASET_CODE.eq(LEXEME.DATASET_CODE))
																		.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(USAGE.LANG))))))))
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

		return mainDb
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

	public boolean isGrantedForDefinitionNote(Long userId, eki.ekilex.data.DatasetPermission userRole, Long definitionNoteId, String requiredAuthItem, List<String> requiredAuthOps) {

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
			providedLangCond = DEFINITION_NOTE.LANG.eq(providedAuthLang);
		}

		Condition providedRequiredAuthCond = DSL.val(providedAuthItem).eq(requiredAuthItem).and(DSL.val(providedAuthOp).in(requiredAuthOps));

		return mainDb
				.select(field(DSL.count(DEFINITION_NOTE.ID).gt(0)).as("is_granted"))
				.from(DEFINITION_NOTE)
				.where(DEFINITION_NOTE.ID.eq(definitionNoteId)
						.and(providedLangCond)
						.and(providedRequiredAuthCond)
						.andExists(DSL
								.select(DEFINITION_DATASET.DEFINITION_ID)
								.from(DEFINITION_DATASET)
								.where(
										DEFINITION_DATASET.DEFINITION_ID.eq(DEFINITION_NOTE.DEFINITION_ID)
												.and(DEFINITION_DATASET.DATASET_CODE.eq(providedDatasetCode))
												.andExists(DSL
														.select(DATASET_PERMISSION.ID)
														.from(DATASET_PERMISSION)
														.where(
																DATASET_PERMISSION.USER_ID.eq(userId)
																		.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
																		.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
																		.and(DATASET_PERMISSION.DATASET_CODE.eq(DEFINITION_DATASET.DATASET_CODE))
																		.and(DSL.or(DATASET_PERMISSION.AUTH_LANG.isNull(), DATASET_PERMISSION.AUTH_LANG.eq(DEFINITION_NOTE.LANG))))))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isGrantedForSource(Long userId, eki.ekilex.data.DatasetPermission userRole, Long sourceId, String requiredAuthItem, List<String> requiredAuthOps) {

		if (userRole == null) {
			return false;
		}

		String providedDatasetCode = userRole.getDatasetCode();
		String providedAuthItem = userRole.getAuthItem().name();
		String providedAuthOp = userRole.getAuthOperation().name();

		if (!StringUtils.equals(requiredAuthItem, providedAuthItem)) {
			return false;
		}
		if (!requiredAuthOps.contains(providedAuthOp)) {
			return false;
		}

		return mainDb
				.fetchExists(DSL
						.select(DATASET_PERMISSION.ID)
						.from(DATASET_PERMISSION, SOURCE)
						.where(
								SOURCE.ID.eq(sourceId)
										.and(DATASET_PERMISSION.USER_ID.eq(userId))
										.and(DATASET_PERMISSION.DATASET_CODE.eq(providedDatasetCode))
										.and(DATASET_PERMISSION.AUTH_OPERATION.in(requiredAuthOps))
										.and(DATASET_PERMISSION.AUTH_ITEM.eq(requiredAuthItem))
										.and(DATASET_PERMISSION.DATASET_CODE.eq(SOURCE.DATASET_CODE))));
	}

	public boolean isGrantedForWordForum(Long userId, Long wordForumId) {

		return mainDb
				.select(field(DSL.count(WORD_FORUM.ID).eq(1)).as("is_granted"))
				.from(WORD_FORUM)
				.where(WORD_FORUM.ID.eq(wordForumId).and(WORD_FORUM.CREATOR_ID.eq(userId)))
				.fetchSingleInto(Boolean.class);

	}

	public boolean isGrantedForMeaningForum(Long userId, Long meaningForumId) {

		return mainDb
				.select(field(DSL.count(MEANING_FORUM.ID).eq(1)).as("is_granted"))
				.from(MEANING_FORUM)
				.where(MEANING_FORUM.ID.eq(meaningForumId).and(MEANING_FORUM.CREATOR_ID.eq(userId)))
				.fetchSingleInto(Boolean.class);
	}
}
