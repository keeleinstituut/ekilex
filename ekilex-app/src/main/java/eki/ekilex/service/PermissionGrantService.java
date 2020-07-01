package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PermConstant;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class PermissionGrantService implements PermConstant {

	@Autowired
	private PermissionDbService permissionDbService;

	@Transactional
	public boolean isMeaningAnyLexemeCrudGranted(Long userId, Long meaningId) {
		return permissionDbService.isMeaningAnyLexemeCrudGranted(userId, meaningId);
	}

	@Transactional
	public boolean isWordCrudGranted(Long userId, Long wordId, String roleDatasetCode) {
		return permissionDbService.isGrantedForWord(userId, wordId, roleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_READ);
	}
}
