package eki.wordweb.data;

import java.util.List;

import eki.wordweb.data.type.TypeSourceLink;

public interface SourceLinkType {

	Long getOwnerId();

	void setSourceLinks(List<TypeSourceLink> sourceLinks);

	List<TypeSourceLink> getSourceLinks();
}
