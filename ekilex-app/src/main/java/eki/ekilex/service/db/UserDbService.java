package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.EKI_USER;

import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record5;
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

		Optional<Record5<Long, String, String, String, String[]>> optionalResult = create
				.select(
						EKI_USER.ID,
						EKI_USER.NAME,
						EKI_USER.EMAIL,
						EKI_USER.PASSWORD,
						EKI_USER.ROLES)
				.from(EKI_USER)
				.where(EKI_USER.EMAIL.eq(email))
				.fetchOptional();
		if (optionalResult.isPresent()) {
			return optionalResult.get().into(EkiUser.class);
		}
		return null;
	}
}
