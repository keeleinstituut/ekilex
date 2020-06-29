package eki.ekilex.service;

import java.util.List;

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
	public boolean isGrantedForWord(Long wordId, String roleDatasetCode, List<String> userPermDatasetCodes) {
		return permissionDbService.isGrantedForWord(wordId, roleDatasetCode, userPermDatasetCodes);
	}

	@Transactional
	public boolean isGrantedForSource(Long userId, Long sourceId) {
		return permissionDbService.isGrantedForSource(userId, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
	}
}
