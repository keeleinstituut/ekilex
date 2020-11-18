package eki.ekilex.data.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.data.util.AbstractRowMapper;
import eki.ekilex.data.transform.Freeform;

public class FreeformRowMapper extends AbstractRowMapper implements RowMapper<Freeform> {

	@Override
	public Freeform mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long freeformId = rs.getObject("id", Long.class);
		Long parentId = rs.getObject("parent_id", Long.class);
		String typeStr = rs.getString("type");
		FreeformType type = null;
		if (StringUtils.isNotBlank(typeStr)) {
			type = FreeformType.valueOf(typeStr);
		}
		String valueText = rs.getString("value_text");
		String valuePrese = rs.getString("value_prese");
		Timestamp valueDate = rs.getTimestamp("value_date");
		Float valueNumber = getFloat(rs, "value_number");
		Array valueArrayObj = rs.getArray("value_array");
		String[] valueArray = null;
		if (valueArrayObj != null) {
			valueArray = (String[]) valueArrayObj.getArray();
		}
		String classifName = rs.getString("classif_name");
		String classifCode = rs.getString("classif_code");
		String langCode = rs.getString("lang");
		String complexityStr = rs.getString("complexity");
		Complexity complexity = null;
		if (StringUtils.isNotBlank(complexityStr)) {
			complexity = Complexity.valueOf(complexityStr);
		}
		Long orderBy = rs.getObject("order_by", Long.class);
		boolean childrenExist = false;
		if (columnExists(rs, "children_exist")) {
			childrenExist = rs.getBoolean("children_exist");
		}
		boolean sourceLinksExist = false;
		if (columnExists(rs, "source_links_exist")) {
			sourceLinksExist = rs.getBoolean("source_links_exist");
		}

		Freeform freeform = new Freeform();
		freeform.setFreeformId(freeformId);
		freeform.setParentId(parentId);
		freeform.setType(type);
		freeform.setValueText(valueText);
		freeform.setValuePrese(valuePrese);
		freeform.setValueDate(valueDate);
		freeform.setValueNumber(valueNumber);
		freeform.setValueArray(valueArray);
		freeform.setClassifName(classifName);
		freeform.setClassifCode(classifCode);
		freeform.setLangCode(langCode);
		freeform.setComplexity(complexity);
		freeform.setOrderBy(orderBy);
		freeform.setChildrenExist(childrenExist);
		freeform.setSourceLinksExist(sourceLinksExist);
		return freeform;
	}

	private boolean columnExists(ResultSet rs, String columnName) throws SQLException {
	    ResultSetMetaData meta = rs.getMetaData();
	    int columnCount = meta.getColumnCount();
	    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	        if (columnName.equals(meta.getColumnName(columnIndex))) {
	            return true;
	        }
	    }
	    return false;
	}
}
