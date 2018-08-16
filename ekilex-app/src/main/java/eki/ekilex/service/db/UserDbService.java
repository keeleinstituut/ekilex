package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.EKI_USER;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import eki.common.service.db.AbstractDbService;
import eki.ekilex.data.EkiUser;

@Component
public class UserDbService extends AbstractDbService {

	private DSLContext create;

	public UserDbService(DSLContext context) {
		create = context;
	}

	public EkiUser getUserByEmail(String email) {

		return create
				.select(
						EKI_USER.ID,
						EKI_USER.NAME,
						EKI_USER.EMAIL,
						EKI_USER.PASSWORD,
						EKI_USER.ROLES)
				.from(EKI_USER)
				.where(EKI_USER.EMAIL.eq(email))
				.fetchSingle()
				.into(EkiUser.class);
	}
}
