package eki.ekilex.data.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.data.util.AbstractRowMapper;
import eki.ekilex.data.transform.LexemeLifecycleLog;

public class LexemeLifecycleLogRowMapper extends AbstractRowMapper implements RowMapper<LexemeLifecycleLog> {

	@Override
	public LexemeLifecycleLog mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long lexemeId = rs.getObject("lexeme_id", Long.class);
		Long entityId = rs.getObject("entity_id", Long.class);
		String entityStr = rs.getString("entity_name");
		LifecycleEntity entity = null;
		if (StringUtils.isNotBlank(entityStr)) {
			entity = LifecycleEntity.valueOf(entityStr);
		}
		String propertyStr = rs.getString("entity_prop");
		LifecycleProperty property = null;
		if (StringUtils.isNotBlank(propertyStr)) {
			property = LifecycleProperty.valueOf(propertyStr);
		}
		String eventTypeStr = rs.getString("event_type");
		LifecycleEventType eventType = null;
		if (StringUtils.isNotBlank(eventTypeStr)) {
			eventType = LifecycleEventType.valueOf(eventTypeStr);
		}
		String eventBy = rs.getString("event_by");
		Timestamp eventOn = rs.getTimestamp("event_on");
		String recent = rs.getString("recent");
		String entry = rs.getString("entry");

		LexemeLifecycleLog lexemeLifecycleLog = new LexemeLifecycleLog();
		lexemeLifecycleLog.setLexemeId(lexemeId);
		lexemeLifecycleLog.setEntityId(entityId);
		lexemeLifecycleLog.setEntity(entity);
		lexemeLifecycleLog.setProperty(property);
		lexemeLifecycleLog.setEventType(eventType);
		lexemeLifecycleLog.setEventBy(eventBy);
		lexemeLifecycleLog.setEventOn(eventOn);
		lexemeLifecycleLog.setRecent(recent);
		lexemeLifecycleLog.setEntry(entry);
		return lexemeLifecycleLog;
	}

}