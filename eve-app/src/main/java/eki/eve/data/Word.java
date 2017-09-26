package eki.eve.data;

import javax.persistence.Column;

import eki.common.data.AbstractDataObject;

import java.util.function.Consumer;

public class Word extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
	private Long id;

	@Column(name = "value")
	private String value;

	@Column(name = "homonym_nr")
	private Integer homonymNumber;

	public Word() {
	}

	public Word(Consumer<Word> builder) {
		builder.accept(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getHomonymNumber() {
		return homonymNumber;
	}

	public void setHomonymNumber(Integer homonymNumber) {
		this.homonymNumber = homonymNumber;
	}

}
