package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DATASET_PERMISSION;
import static eki.ekilex.data.db.Tables.EKI_USER;
import static eki.ekilex.data.db.Tables.EKI_USER_APPLICATION;
import static eki.ekilex.data.db.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.Tables.TERMS_OF_USE;

import java.util.List;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.service.db.AbstractDbService;
import eki.ekilex.constant.ApplicationStatus;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserApplication;
import eki.ekilex.data.db.tables.records.EkiUserRecord;

@Component
public class UserDbService extends AbstractDbService implements SystemConstant {

	@Autowired
	private DSLContext create;

	public Long createUser(String email, String name, String password, String activationKey, String termsVer) {
		EkiUserRecord ekiUser = create.newRecord(EKI_USER);
		ekiUser.setEmail(email);
		ekiUser.setName(name);
		ekiUser.setPassword(password);
		ekiUser.setActivationKey(activationKey);
		ekiUser.setTermsVer(termsVer);
		ekiUser.store();
		return ekiUser.getId();
	}

	public EkiUser getUserById(Long id) {

		Condition where = EKI_USER.ID.eq(id);
		return getUser(where);
	}

	public EkiUser getUserByEmail(String email) {

		Condition where = EKI_USER.EMAIL.eq(email);
		return getUser(where);
	}

	public EkiUser getUserByApiKey(String apiKey) {

		Condition where = EKI_USER.API_KEY.eq(apiKey);
		return getUser(where);
	}

	private EkiUser getUser(Condition where) {

		Field<Boolean> termsAgreed = DSL
				.select(DSL.field(DSL.count(TERMS_OF_USE.ID).eq(1)))
				.from(TERMS_OF_USE)
				.where(TERMS_OF_USE.VERSION.eq(EKI_USER.TERMS_VER).and(TERMS_OF_USE.IS_ACTIVE.isTrue()))
				.asField();

		return create
				.select(
						EKI_USER.ID,
						EKI_USER.NAME,
						EKI_USER.EMAIL,
						EKI_USER.PASSWORD,
						EKI_USER.ACTIVATION_KEY,
						EKI_USER.RECOVERY_KEY,
						EKI_USER.API_KEY,
						EKI_USER.IS_API_CRUD.as("api_crud"),
						EKI_USER.IS_ADMIN.as("admin"),
						EKI_USER.IS_MASTER.as("master"),
						EKI_USER.IS_ENABLED.as("enabled"),
						termsAgreed.as("active_terms_agreed"))
				.from(EKI_USER)
				.where(where)
				.fetchOptionalInto(EkiUser.class)
				.orElse(null);
	}

	public Long getUserIdByEmail(String email) {

		return create
				.select(EKI_USER.ID)
				.from(EKI_USER)
				.where(EKI_USER.EMAIL.eq(email))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public String getUserEmailByRecoveryKey(String recoveryKey) {

		return create
				.select(EKI_USER.EMAIL)
				.from(EKI_USER)
				.where(EKI_USER.RECOVERY_KEY.eq(recoveryKey))
				.fetchOptionalInto(String.class)
				.orElse(null);
	}

	public String getActiveTermsValue() {

		return create
				.select(TERMS_OF_USE.VALUE)
				.from(TERMS_OF_USE)
				.where(TERMS_OF_USE.IS_ACTIVE.isTrue())
				.fetchSingleInto(String.class);
	}

	public String getActiveTermsVersion() {

		return create
				.select(TERMS_OF_USE.VERSION)
				.from(TERMS_OF_USE)
				.where(TERMS_OF_USE.IS_ACTIVE.isTrue())
				.fetchSingleInto(String.class);
	}

	public void agreeActiveTerms(Long userId) {

		create
				.update(EKI_USER)
				.set(EKI_USER.TERMS_VER, TERMS_OF_USE.VERSION)
				.from(TERMS_OF_USE)
				.where(
						EKI_USER.ID.eq(userId)
								.and(TERMS_OF_USE.IS_ACTIVE.isTrue()))
				.execute();
	}

	public void setUserRecoveryKey(Long userId, String recoveryKey) {

		create
				.update(EKI_USER)
				.set(EKI_USER.RECOVERY_KEY, recoveryKey)
				.where(EKI_USER.ID.eq(userId)).execute();
	}

	public void setUserPassword(String email, String encodedPassword) {

		EkiUserRecord ekiUser = create.selectFrom(EKI_USER).where(EKI_USER.EMAIL.eq(email)).fetchOne();
		ekiUser.setRecoveryKey(null);
		ekiUser.setPassword(encodedPassword);
		ekiUser.store();
	}

	public EkiUser activateUser(String activationKey) {
		Optional<EkiUserRecord> ekiUser = create.selectFrom(EKI_USER).where(EKI_USER.ACTIVATION_KEY.eq(activationKey)).fetchOptional();
		if (ekiUser.isPresent()) {
			ekiUser.get().setActivationKey(null);
			ekiUser.get().store();
			return ekiUser.get().into(EkiUser.class);
		}
		return null;
	}

	public void enableUser(Long userId, boolean enable) {
		create.update(EKI_USER).set(EKI_USER.IS_ENABLED, enable).where(EKI_USER.ID.eq(userId)).execute();
	}

	public void setApiCrud(Long userId, boolean isApiCrud) {
		create.update(EKI_USER).set(EKI_USER.IS_API_CRUD, isApiCrud).where(EKI_USER.ID.eq(userId)).execute();
	}

	public void setAdmin(Long userId, boolean isAdmin) {
		create.update(EKI_USER).set(EKI_USER.IS_ADMIN, isAdmin).where(EKI_USER.ID.eq(userId)).execute();
	}

	public void setMaster(Long userId, boolean isMaster) {
		create.update(EKI_USER).set(EKI_USER.IS_MASTER, isMaster).where(EKI_USER.ID.eq(userId)).execute();
	}

	public void updateReviewComment(Long userId, String reviewComment) {
		create.update(EKI_USER).set(EKI_USER.REVIEW_COMMENT, reviewComment).where(EKI_USER.ID.eq(userId)).execute();
	}

	public List<String> getAdminEmails() {

		return create
				.select(EKI_USER.EMAIL)
				.from(EKI_USER)
				.where(EKI_USER.IS_ADMIN.isTrue())
				.fetchInto(String.class);
	}

	public List<String> getDatasetOwnerEmails(String datasetCode) {

		return create
				.select(EKI_USER.EMAIL)
				.from(EKI_USER, DATASET_PERMISSION)
				.where(
						EKI_USER.ID.eq(DATASET_PERMISSION.USER_ID)
								.and(DATASET_PERMISSION.DATASET_CODE.eq(datasetCode))
								.and(DATASET_PERMISSION.AUTH_ITEM.eq(AuthorityItem.DATASET.name()))
								.and(DATASET_PERMISSION.AUTH_OPERATION.eq(AuthorityOperation.OWN.name())))
				.fetchInto(String.class);
	}

	public void createUserApplication(Long userId, String datasetCode, AuthorityOperation authOp, String lang, String comment, ApplicationStatus status) {

		create
				.insertInto(
						EKI_USER_APPLICATION,
						EKI_USER_APPLICATION.USER_ID,
						EKI_USER_APPLICATION.DATASET_CODE,
						EKI_USER_APPLICATION.AUTH_OPERATION,
						EKI_USER_APPLICATION.LANG,
						EKI_USER_APPLICATION.COMMENT,
						EKI_USER_APPLICATION.STATUS)
				.values(userId, datasetCode, authOp.name(), lang, comment, status.name())
				.execute();
	}

	public List<EkiUserApplication> getUserApplications(Long userId) {

		return create
				.select(
						EKI_USER_APPLICATION.ID,
						EKI_USER_APPLICATION.USER_ID,
						EKI_USER_APPLICATION.DATASET_CODE,
						DATASET.NAME.as("dataset_name"),
						EKI_USER_APPLICATION.AUTH_OPERATION,
						EKI_USER_APPLICATION.LANG,
						LANGUAGE_LABEL.VALUE.as("lang_value"),
						EKI_USER_APPLICATION.COMMENT,
						EKI_USER_APPLICATION.STATUS,
						EKI_USER_APPLICATION.CREATED)
				.from(EKI_USER_APPLICATION
						.innerJoin(DATASET).on(DATASET.CODE.eq(EKI_USER_APPLICATION.DATASET_CODE))
						.leftOuterJoin(LANGUAGE_LABEL).on(LANGUAGE_LABEL.CODE.eq(EKI_USER_APPLICATION.LANG)
								.and(LANGUAGE_LABEL.LANG.eq(CLASSIF_LABEL_LANG_EST))
								.and(LANGUAGE_LABEL.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(EKI_USER_APPLICATION.USER_ID.eq(userId))
				.orderBy(EKI_USER_APPLICATION.CREATED.desc())
				.fetchInto(EkiUserApplication.class);
	}

	public EkiUserApplication getUserApplication(Long userApplicationId) {

		return create
				.select(
						EKI_USER_APPLICATION.ID,
						EKI_USER_APPLICATION.USER_ID,
						EKI_USER_APPLICATION.DATASET_CODE,
						DATASET.NAME.as("dataset_name"),
						EKI_USER_APPLICATION.AUTH_OPERATION,
						EKI_USER_APPLICATION.LANG,
						EKI_USER_APPLICATION.COMMENT,
						EKI_USER_APPLICATION.STATUS,
						EKI_USER_APPLICATION.CREATED)
				.from(EKI_USER_APPLICATION
						.innerJoin(DATASET).on(DATASET.CODE.eq(EKI_USER_APPLICATION.DATASET_CODE)))
				.where(EKI_USER_APPLICATION.ID.eq(userApplicationId))
				.fetchOneInto(EkiUserApplication.class);
	}

	public void updateApiKey(Long userId, String apiKey, boolean isApiCrud) {

		create.update(EKI_USER).set(EKI_USER.API_KEY, apiKey).set(EKI_USER.IS_API_CRUD, isApiCrud).where(EKI_USER.ID.eq(userId)).execute();
	}

	public void updateApplicationStatus(Long userApplicationId, ApplicationStatus status) {

		create
				.update(EKI_USER_APPLICATION)
				.set(EKI_USER_APPLICATION.STATUS, status.name())
				.where(EKI_USER_APPLICATION.ID.eq(userApplicationId))
				.execute();
	}
}
