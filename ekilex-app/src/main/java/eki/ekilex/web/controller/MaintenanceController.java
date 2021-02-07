package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.MaintenanceService;

@ConditionalOnWebApplication
@Controller
@PreAuthorize("principal.admin")
public class MaintenanceController implements WebConstant {

	@Autowired
	private MaintenanceService maintenanceService;

	@ResponseBody
	@GetMapping(MAINTENANCE_URI + "/clearcache")
	public String clearCache() {

		maintenanceService.clearCache();

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@GetMapping(MAINTENANCE_URI + "/mergehomonyms")
	public String mergeHomonyms() {

		maintenanceService.mergeHomonyms();

		return RESPONSE_OK_VER1;
	}

	@ResponseBody
	@GetMapping(MAINTENANCE_URI + "/recalcaccents")
	public String recalcAccents() {

		maintenanceService.unifyApostrophesAndRecalcAccents();

		return RESPONSE_OK_VER1;
	}
}
