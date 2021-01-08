package eki.ekilex.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("@permEval.isPrivatePageAccessPermitted(authentication)")
public abstract class AbstractPrivateSearchController extends AbstractSearchController {

}
