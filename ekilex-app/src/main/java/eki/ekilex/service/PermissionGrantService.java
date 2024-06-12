package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PermConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class PermissionGrantService implements PermConstant {

	@Autowired
	private PermissionDbService permissionDbService;

	@Transactional
	public boolean isMeaningPairCrudGranted(EkiUser user, Long meaningId1, Long meaningId2) {

		Long userId = user.getId();
		if (user.isMaster()) {
			return true;
		}
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		boolean isGrantedForMeaning1 = permissionDbService.isGrantedForMeaning(userId, userRole, meaningId1, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		boolean isGrantedForMeaning2 = permissionDbService.isGrantedForMeaning(userId, userRole, meaningId2, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isGrantedForMeaning1 && isGrantedForMeaning2) {
			return true;
		}
		isGrantedForMeaning1 = permissionDbService.isGrantedForSuperiorMeaning(userId, userRole, meaningId1, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		isGrantedForMeaning2 = permissionDbService.isGrantedForSuperiorMeaning(userId, userRole, meaningId2, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isGrantedForMeaning1 || isGrantedForMeaning2) {
			return true;
		}
		return false;
	}

	@Transactional
	public boolean isWordPairCrudGranted(EkiUser user, Long wordId1, Long wordId2) {

		Long userId = user.getId();
		if (user.isMaster()) {
			return true;
		}
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		boolean isGrantedForWord1 = permissionDbService.isGrantedForWord(userId, userRole, wordId1, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		boolean isGrantedForWord2 = permissionDbService.isGrantedForWord(userId, userRole, wordId2, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isGrantedForWord1 && isGrantedForWord2) {
			return true;
		}
		isGrantedForWord1 = permissionDbService.isGrantedForSuperiorWord(userId, userRole, wordId1, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		isGrantedForWord2 = permissionDbService.isGrantedForSuperiorWord(userId, userRole, wordId2, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isGrantedForWord1 || isGrantedForWord2) {
			return true;
		}
		return false;
	}
}
