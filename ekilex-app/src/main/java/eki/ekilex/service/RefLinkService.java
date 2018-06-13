package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ReferenceOwner;
import eki.ekilex.data.SourceLink;
import eki.ekilex.service.db.RefLinkDbService;

@Component
public class RefLinkService {

	@Autowired
	private RefLinkDbService refLinkDbService;

	@Transactional
	public SourceLink getSourceLink(Long refLinkId, ReferenceOwner referenceOwner) {

		SourceLink sourceLink = null;

		if (ReferenceOwner.FREEFORM.equals(referenceOwner)) {
			sourceLink = refLinkDbService.getFreeformSourceLink(refLinkId).into(SourceLink.class);
		} else if (ReferenceOwner.DEFINITION.equals(referenceOwner)) {
			sourceLink = refLinkDbService.getDefinitionSourceLink(refLinkId).into(SourceLink.class);
		} else if (ReferenceOwner.LEXEME.equals(referenceOwner)) {
			sourceLink = refLinkDbService.getLexemeSourceLink(refLinkId).into(SourceLink.class);
		}
		return sourceLink;
	}
}
