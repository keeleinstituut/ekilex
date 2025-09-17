package eki.ekilex.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.CollocationService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class CollocationEditController extends AbstractPrivatePageController {

	private static final Logger logger = LoggerFactory.getLogger(CollocationEditController.class);

	@Autowired
	private CollocationService collocationService;

	@ResponseBody
	@PostMapping(COLLOC_MEMBER_MOVE_URI)
	public String collocMemberMove(
			@RequestParam("collocLexemeIds") List<Long> collocLexemeIds,
			@RequestParam("sourceCollocMemberLexemeId") Long sourceCollocMemberLexemeId,
			@RequestParam("targetCollocMemberLexemeId") Long targetCollocMemberLexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();

		collocationService.moveCollocMember(collocLexemeIds, sourceCollocMemberLexemeId, targetCollocMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}
}
