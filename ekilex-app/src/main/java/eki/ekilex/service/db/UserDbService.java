package eki.ekilex.service.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.mapper.EkiUserRowMapper;

@Component
public class UserDbService extends AbstractDbService {

	public EkiUser getUserByName(String name) throws Exception {

		String sqlQueryStr = "select id, name, password from " + EKI_USER_TBL + " where name = :name";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", name);
		EkiUserRowMapper rowMapper = new EkiUserRowMapper();
		EkiUser user = getSingleResult(sqlQueryStr, paramMap, rowMapper);
		return user;
	}
}
