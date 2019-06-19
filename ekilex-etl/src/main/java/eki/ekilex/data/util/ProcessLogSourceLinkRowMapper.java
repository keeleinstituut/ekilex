package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.ReferenceType;
import eki.ekilex.data.transform.ProcessLogSourceLink;

public class ProcessLogSourceLinkRowMapper implements RowMapper<ProcessLogSourceLink> {

	@Override
	public ProcessLogSourceLink mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long processLogId = rs.getObject("process_log_id", Long.class);
		Long sourceId = rs.getObject("source_id", Long.class);
		String typeStr = rs.getString("type");
		ReferenceType type = null;
		if (StringUtils.isNotBlank(typeStr)) {
			type = ReferenceType.valueOf(typeStr);
		}
		String name = rs.getString("name");
		String value = rs.getString("value");

		ProcessLogSourceLink sourceLink = new ProcessLogSourceLink();
		sourceLink.setProcessLogId(processLogId);
		sourceLink.setSourceId(sourceId);
		sourceLink.setType(type);
		sourceLink.setName(name);
		sourceLink.setValue(value);
		return sourceLink;
	}

}
