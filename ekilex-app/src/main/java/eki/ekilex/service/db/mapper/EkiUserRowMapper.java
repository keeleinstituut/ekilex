package eki.ekilex.service.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eki.ekilex.data.EkiUser;

public class EkiUserRowMapper implements RowMapper<EkiUser> {

	@Override
	public EkiUser mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		EkiUser user = new EkiUser();
		Long id = resultSet.getLong("id");
		String name = resultSet.getString("name");
		String password = resultSet.getString("password");
		user.setId(id);
		user.setName(name);
		user.setPassword(password);
		return user;
	}
}
