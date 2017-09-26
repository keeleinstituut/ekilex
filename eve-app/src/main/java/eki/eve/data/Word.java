package eki.eve.data;

import javax.persistence.Column;
import java.util.function.Consumer;

public class Word extends DomainData {

	@Column(name = "id")
	private Long id;

	@Column(name = "value")
	private String value;

	@Column(name = "homonym_nr")
	private Integer homonymNumber;

	@Column(name = "lang")
	private String language;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
