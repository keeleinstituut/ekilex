package eki.wordweb.data;

import java.util.List;

public interface SourceLinkType {

	Long getOwnerId();

	void setSourceLinks(List<TypeSourceLink> sourceLinks);

	List<TypeSourceLink> getSourceLinks();
}
