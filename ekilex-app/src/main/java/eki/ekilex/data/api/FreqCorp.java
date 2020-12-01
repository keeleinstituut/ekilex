package eki.ekilex.data.api;

import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import eki.common.data.AbstractDataObject;

public class FreqCorp extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private String name;

	//yyyy-MM-dd
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private Date corpDate;

	private boolean isPublic;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCorpDate() {
		return corpDate;
	}

	public void setCorpDate(Date corpDate) {
		this.corpDate = corpDate;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

}
