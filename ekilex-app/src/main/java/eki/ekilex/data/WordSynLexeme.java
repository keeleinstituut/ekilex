package eki.ekilex.data;

import java.util.List;

import eki.common.constant.LexemeType;
import eki.common.data.LexemeLevel;

public class WordSynLexeme extends AbstractCrudEntity implements LexemeLevel, LexemeTag {

	private static final long serialVersionUID = 1L;

	private Long meaningId;

	private Long wordId;

	private Long lexemeId;

	private LexemeType type;

	private String datasetCode;

	private Integer level1;

	private Integer level2;

	private String levels;

	private Float weight;

	private List<String> tags;

	private List<MeaningWordLangGroup> meaningWordLangGroups;

	private List<Definition> definitions;

	private List<Classifier> pos;

	private List<Usage> usages;

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public LexemeType getType() {
		return type;
	}

	public void setType(LexemeType type) {
		this.type = type;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public Integer getLevel1() {
		return level1;
	}

	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}

	public Integer getLevel2() {
		return level2;
	}

	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<MeaningWordLangGroup> getMeaningWordLangGroups() {
		return meaningWordLangGroups;
	}

	public void setMeaningWordLangGroups(List<MeaningWordLangGroup> meaningWordLangGroups) {
		this.meaningWordLangGroups = meaningWordLangGroups;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

	public List<Classifier> getPos() {
		return pos;
	}

	public void setPos(List<Classifier> pos) {
		this.pos = pos;
	}

	public List<Usage> getUsages() {
		return usages;
	}

	public void setUsages(List<Usage> usages) {
		this.usages = usages;
	}

}
