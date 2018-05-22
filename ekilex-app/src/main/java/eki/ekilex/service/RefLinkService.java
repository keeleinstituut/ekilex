package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ReferenceOwner;
import eki.ekilex.data.RefLink;
import eki.ekilex.service.db.RefLinkDbService;

@Component
public class RefLinkService {

	@Autowired
	private RefLinkDbService refLinkDbService;

	@Transactional
	public RefLink getRefLink(Long refLinkId, ReferenceOwner referenceOwner) {

		RefLink refLink = null;

		if (ReferenceOwner.FREEFORM.equals(referenceOwner)) {
			refLink = refLinkDbService.getFreeformRefLink(refLinkId).into(RefLink.class);
		} else if (ReferenceOwner.DEFINITION.equals(referenceOwner)) {
			refLink = refLinkDbService.getDefinitionRefLink(refLinkId).into(RefLink.class);
		} else if (ReferenceOwner.LEXEME.equals(referenceOwner)) {
			refLink = refLinkDbService.getLexemeRefLink(refLinkId).into(RefLink.class);
		}
		return refLink;
	}
}
