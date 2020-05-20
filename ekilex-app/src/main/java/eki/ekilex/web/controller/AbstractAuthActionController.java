package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserContext;

public abstract class AbstractAuthActionController implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	protected UserContext userContext;

	protected String getDatasetCodeFromRole() {
		EkiUser user = userContext.getUser();
		DatasetPermission role = user.getRecentRole();
		if (role == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		return role.getDatasetCode();
	}
}
