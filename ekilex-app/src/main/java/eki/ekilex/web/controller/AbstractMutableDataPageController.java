package eki.ekilex.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("@permEval.isMutableDataPageAccessPermitted(authentication)")
public abstract class AbstractMutableDataPageController extends AbstractAuthActionController {

}
