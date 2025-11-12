package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.CollocWeight;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.CollocationDbService;

@Component
public class CollocationService implements SystemConstant {

	@Autowired
	private CollocationDbService collocationDbService;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private MessageSource messageSource;

	@Transactional(rollbackFor = Exception.class)
	public void moveCollocMember(
			List<Long> collocLexemeIds,
			Long sourceCollocMemberLexemeId,
			Long targetCollocMemberLexemeId,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		if (CollectionUtils.isEmpty(collocLexemeIds)) {
			return;
		}
		if (sourceCollocMemberLexemeId.equals(targetCollocMemberLexemeId)) {
			return;
		}

		ActivityLogData sourceLexemeActivityLog = activityLogService.prepareActivityLog("moveCollocMember", sourceCollocMemberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		ActivityLogData targetLexemeActivityLog = activityLogService.prepareActivityLog("moveCollocMember", targetCollocMemberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		collocationDbService.moveCollocMember(collocLexemeIds, sourceCollocMemberLexemeId, targetCollocMemberLexemeId);
		activityLogService.createActivityLog(sourceLexemeActivityLog, sourceCollocMemberLexemeId, ActivityEntity.LEXEME);
		activityLogService.createActivityLog(targetLexemeActivityLog, targetCollocMemberLexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public List<CollocMemberForm> getCollocMemberForms(String formValue, String lang, String datasetCode) {

		List<CollocMemberForm> collocMemberForms = collocationDbService.getCollocMemberForms(formValue, lang, datasetCode, CLASSIF_LABEL_LANG_EST);

		if (CollectionUtils.isNotEmpty(collocMemberForms)) {
			CollocMemberForm firstCollocMemberForm = collocMemberForms.get(0);
			firstCollocMemberForm.setSelected(true);
		}
		return collocMemberForms;
	}

	public List<CollocWeight> getCollocWeights() {

		Locale locale = LocaleContextHolder.getLocale();
		List<CollocWeight> collocWeights = new ArrayList<>();
		CollocWeight collocWeight;
		String label;
		label = messageSource.getMessage("colloc.weight.10", new Object[0], locale);
		collocWeight = new CollocWeight(new BigDecimal("1.0"), label);
		collocWeights.add(collocWeight);
		label = messageSource.getMessage("colloc.weight.08", new Object[0], locale);
		collocWeight = new CollocWeight(new BigDecimal("0.8"), label);
		collocWeights.add(collocWeight);
		label = messageSource.getMessage("colloc.weight.05", new Object[0], locale);
		collocWeight = new CollocWeight(new BigDecimal("0.5"), label);
		collocWeights.add(collocWeight);
		
		return collocWeights;
	}
}
