package eki.ekilex.service.db;

import eki.common.service.db.AbstractDbService;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.db.tables.records.EkiUserRecord;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static eki.ekilex.data.db.Tables.EKI_USER;

@Component
public class UserDbService extends AbstractDbService {

	private DSLContext create;

	public UserDbService(DSLContext context) {
		create = context;
	}

	public EkiUser getUserByEmail(String email) {

		Optional<Record6<Long, String, String, String, String[], String>> optionalResult = create
				.select(
						EKI_USER.ID,
						EKI_USER.NAME,
						EKI_USER.EMAIL,
						EKI_USER.PASSWORD,
						EKI_USER.ROLES,
						EKI_USER.ACTIVATION_KEY.as("activationKey"))
				.from(EKI_USER)
				.where(EKI_USER.EMAIL.eq(email))
				.fetchOptional();
		if (optionalResult.isPresent()) {
			return optionalResult.get().into(EkiUser.class);
		}
		return null;
	}

	public EkiUser getUserByName(String name) {

		Optional<Record6<Long, String, String, String, String[], String>> optionalResult = create
				.select(
						EKI_USER.ID,
						EKI_USER.NAME,
						EKI_USER.EMAIL,
						EKI_USER.PASSWORD,
						EKI_USER.ROLES,
						EKI_USER.ACTIVATION_KEY.as("activationKey"))
				.from(EKI_USER)
				.where(EKI_USER.NAME.equalIgnoreCase(name))
				.fetchOptional();
		if (optionalResult.isPresent()) {
			return optionalResult.get().into(EkiUser.class);
		}
		return null;
	}

	public Long addUser(String email, String name, String password, String[] roles, String activationKey) {
		EkiUserRecord ekiUser = create.newRecord(EKI_USER);
		ekiUser.setEmail(email);
		ekiUser.setName(name);
		ekiUser.setPassword(password);
		ekiUser.setRoles(roles);
		ekiUser.setActivationKey(activationKey);
		ekiUser.store();
		return ekiUser.getId();
	}

	public EkiUser activateUser(String activationKey) {
		Optional<EkiUserRecord> ekiUser = create.selectFrom(EKI_USER).where(EKI_USER.ACTIVATION_KEY.eq(activationKey)).fetchOptional();
		if (ekiUser.isPresent()) {
			ekiUser.get().setActivationKey(null);
			ekiUser.get().store();
			return ekiUser.get().into(EkiUser.class);
		} else {
			return null;
		}
	}

}
