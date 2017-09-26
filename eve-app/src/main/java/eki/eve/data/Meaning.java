package eki.eve.data;

import javax.persistence.Column;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Meaning extends DomainData {

	private Long id;

	private Integer level1;

	private Integer level2;

	private List<String> words;

	private List<String> datasets;

	private List<String> definitions;

	public Meaning() {
	}

	public Meaning(Consumer<Meaning> builder) {
		builder.accept(this);
	}

	public Long getId() {
		return id;
	}

	@Column(name = "meaning_id")
	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLevel1() {
		return level1;
	}

	@Column(name = "level1")
	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}

	public Integer getLevel2() {
		return level2;
	}

	@Column(name = "level2")
	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	@Column(name = "words")
	public void setWords(String[] words) {
		this.words = Arrays.asList(words);
	}

	public List<String> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<String> datasets) {
		this.datasets = datasets;
	}

	@Column(name = "datasets")
	public void setDatasets(String[] datasets) {
		this.datasets = Arrays.asList(datasets);
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

	@Column(name = "definitions")
	public void setDefinitions(String[] definitions) {
		this.definitions = Arrays.asList(definitions);
	}
}
