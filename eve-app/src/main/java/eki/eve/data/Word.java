package eki.eve.data;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.function.Consumer;

public class Word implements Serializable {

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
