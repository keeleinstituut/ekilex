package eki.ekilex.data;

import eki.common.constant.FreeformType;
import eki.common.data.AbstractDataObject;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FreeForm extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "type")
	private FreeformType type;

	@Column(name = "value_text")
	private String valueText;

	@Column(name = "value_date")
	private LocalDateTime valueDate;

	private List<FreeForm> childs = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FreeformType getType() {
		return type;
	}

	public void setType(FreeformType type) {
		this.type = type;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	public LocalDateTime getValueDate() {
		return valueDate;
	}

	public void setValueDate(LocalDateTime valueDate) {
		this.valueDate = valueDate;
	}

	public List<FreeForm> getChilds() {
		return childs;
	}

	public void setChilds(List<FreeForm> childs) {
		this.childs = childs;
	}

	public String getDisplayString() {
		String textStr = getValueText() != null ? getValueText() : "";
		String dateStr = getValueDate() != null ? getValueDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
		return textStr + dateStr;
	}
}
