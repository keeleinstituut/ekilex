package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PermConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class PermissionGrantService implements PermConstant {

	@Autowired
	private PermissionDbService permissionDbService;

	@Transactional
	public boolean isMeaningCrudGrantedByAnyLexeme(Long userId, Long meaningId) {
		return permissionDbService.isGrantedForMeaningByAnyLexeme(userId, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
	}

	@Transactional
	public boolean isWordCrudGranted(Long userId, DatasetPermission userRole, Long wordId) {
		return permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
	}
}
