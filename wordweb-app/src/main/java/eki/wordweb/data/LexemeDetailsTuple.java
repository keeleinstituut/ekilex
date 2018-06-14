package eki.wordweb.data;

import java.util.List;

import eki.common.data.AbstractDataObject;

public class LexemeDetailsTuple extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private Long lexemeId;

	private Long meaningId;

	private List<String> adviceNotes;

	private List<String> publicNotes;

	private List<String> grammars;

	private List<String> governments;

	private List<TypeUsage> usages;

	private List<TypeLexemeRelation> relatedLexemes;

	private List<TypeMeaningRelation> relatedMeanings;

	public Long getLexemeId() {
		return lexemeId;
	}

	public void setLexemeId(Long lexemeId) {
		this.lexemeId = lexemeId;
	}

	public Long getMeaningId() {
		return meaningId;
	}

	public void setMeaningId(Long meaningId) {
		this.meaningId = meaningId;
	}

	public List<String> getAdviceNotes() {
		return adviceNotes;
	}

	public void setAdviceNotes(List<String> adviceNotes) {
		this.adviceNotes = adviceNotes;
	}

	public List<String> getPublicNotes() {
		return publicNotes;
	}

	public void setPublicNotes(List<String> publicNotes) {
		this.publicNotes = publicNotes;
	}

	public List<String> getGrammars() {
		return grammars;
	}

	public void setGrammars(List<String> grammars) {
		this.grammars = grammars;
	}

	public List<String> getGovernments() {
		return governments;
	}

	public void setGovernments(List<String> governments) {
		this.governments = governments;
	}

	public List<TypeUsage> getUsages() {
		return usages;
	}

	public void setUsages(List<TypeUsage> usages) {
		this.usages = usages;
	}

	public List<TypeLexemeRelation> getRelatedLexemes() {
		return relatedLexemes;
	}

	public void setRelatedLexemes(List<TypeLexemeRelation> relatedLexemes) {
		this.relatedLexemes = relatedLexemes;
	}

	public List<TypeMeaningRelation> getRelatedMeanings() {
		return relatedMeanings;
	}

	public void setRelatedMeanings(List<TypeMeaningRelation> relatedMeanings) {
		this.relatedMeanings = relatedMeanings;
	}

}
