package eki.ekilex.service.api;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.ActivityLogOwnerEntityDescr;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.LexemeFreeform;
import eki.ekilex.data.MeaningFreeform;
import eki.ekilex.data.WordFreeform;
import eki.ekilex.service.AbstractService;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.CudDbService;

@Component
public class FreeformService extends AbstractService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private CudDbService cudDbService;

	@Transactional
	public Freeform getFreeform(Long freeformId) {
		return commonDataDbService.getFreeform(freeformId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createWordFreeform(WordFreeform freeform, String roleDatasetCode) throws Exception {

		setValueAndPrese(freeform);
		applyCreateUpdate(freeform);

		Long wordId = freeform.getWordId();
		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeform", wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long freeformId = cudDbService.createWordFreeform(wordId, freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);

		return freeformId;
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeFreeform(LexemeFreeform freeform, String roleDatasetCode) throws Exception {

		setValueAndPrese(freeform);
		applyCreateUpdate(freeform);

		Long lexemeId = freeform.getLexemeId();
		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long freeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);

		return freeformId;
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningFreeform(MeaningFreeform freeform, String roleDatasetCode) throws Exception {

		setValueAndPrese(freeform);
		applyCreateUpdate(freeform);

		Long meaningId = freeform.getMeaningId();
		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long freeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);

		return freeformId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateFreeform(Freeform freeform, String roleDatasetCode) throws Exception {

		setValueAndPrese(freeform);
		applyUpdate(freeform);

		Long freeformId = freeform.getId();
		String userName = userContext.getUserName();
		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformOwnerDescr(freeformId);
		Long ownerId = freeformOwnerDescr.getOwnerId();
		ActivityOwner owner = freeformOwnerDescr.getOwnerName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeform", ownerId, owner, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		cudDbService.updateFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteFreeform(Long freeformId, String roleDatasetCode) throws Exception {

		ActivityLogOwnerEntityDescr freeformOwnerDescr = activityLogService.getFreeformOwnerDescr(freeformId);
		Long ownerId = freeformOwnerDescr.getOwnerId();
		ActivityOwner owner = freeformOwnerDescr.getOwnerName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteFreeform", ownerId, owner, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
	}
}
