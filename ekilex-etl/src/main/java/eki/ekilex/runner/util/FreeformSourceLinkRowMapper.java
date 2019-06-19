package eki.ekilex.runner.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.ReferenceType;
import eki.ekilex.data.transform.FreeformSourceLink;

public class FreeformSourceLinkRowMapper implements RowMapper<FreeformSourceLink> {

	@Override
	public FreeformSourceLink mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long freeformId = rs.getObject("freeform_id", Long.class);
		Long sourceId = rs.getObject("source_id", Long.class);
		String typeStr = rs.getString("type");
		ReferenceType type = null;
		if (StringUtils.isNotBlank(typeStr)) {
			type = ReferenceType.valueOf(typeStr);
		}
		String name = rs.getString("name");
		String value = rs.getString("value");

		FreeformSourceLink sourceLink = new FreeformSourceLink();
		sourceLink.setFreeformId(freeformId);
		sourceLink.setSourceId(sourceId);
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setValue(value);
		return sourceLink;
	}

}
