package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.common.constant.OrderingField;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUserPermData;
import eki.ekilex.data.WordEtymTree;
import eki.ekilex.data.proto.PermPageModel;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.WordEtymService;

@Controller
public class ProtoController implements WebConstant {

	@Autowired
	private WordEtymService wordEtymService;

	@Autowired
	private PermissionService permissionService;

	@GetMapping(PROTO_URI + "/etym")
	public String initEtymPage() {

		return "proto-etym";
	}

	@GetMapping(PROTO_URI + "/etym/{wordId}")
	public String getEtymPage(@PathVariable("wordId") Long wordId, Model model) {

		WordEtymTree wordEtymTree = wordEtymService.getWordEtymTree(wordId);
		model.addAttribute("wordEtymTree", wordEtymTree);

		return "proto-etym";
	}

	@GetMapping(PROTO_URI + "/wordetymtree/{wordId}")
	@ResponseBody
	public WordEtymTree getWordEtymTree(@PathVariable("wordId") Long wordId) {

		WordEtymTree wordEtymTree = wordEtymService.getWordEtymTree(wordId);

		return wordEtymTree;
	}

	@PreAuthorize("isAuthenticated() && @permEval.isPrivatePageAccessPermitted(authentication)")
	@PostMapping(PROTO_URI + PERMISSIONS_URI + SEARCH_URI)
	@ResponseBody
	public PermPageModel permSearch(
			@RequestParam(name = "userNameFilter", required = false) String userNameFilter,
			@RequestParam(name = "userPermDatasetCodeFilter", required = false) String userPermDatasetCodeFilter,
			@RequestParam(name = "userEnablePendingFilter", required = false) Boolean userEnablePendingFilter) {

		OrderingField orderBy = OrderingField.NAME;

		PermPageModel permPageModel = new PermPageModel();
		permPageModel.setUserNameFilter(userNameFilter);
		permPageModel.setUserPermDatasetCodeFilter(userPermDatasetCodeFilter);
		permPageModel.setUserEnablePendingFilter(userEnablePendingFilter);
		permPageModel.setOrderBy(orderBy);

		if (StringUtils.isBlank(userNameFilter)
				&& StringUtils.isBlank(userPermDatasetCodeFilter)
				&& (userEnablePendingFilter == null)) {
			return permPageModel;
		}

		final int minUserNameLength = 2;
		int userNameFilterLength = StringUtils.length(userNameFilter);

		if ((userNameFilterLength > 0) && (userNameFilterLength < minUserNameLength)) {
			return permPageModel;
		}

		List<EkiUserPermData> ekiUserPermissions = permissionService.getEkiUserPermissions(userNameFilter, userPermDatasetCodeFilter, userEnablePendingFilter, orderBy);

		permPageModel.setEkiUserPermissions(ekiUserPermissions);

		return permPageModel;
	}
}
