package eki.ekilex.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
@Schema(description = "Meaning definitions that are grouped by language of origin")

public class DefinitionLangGroup extends LangGroup {

	private static final long serialVersionUID = 1L;

	private List<Definition> definitions;

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<Definition> definitions) {
		this.definitions = definitions;
	}

}
