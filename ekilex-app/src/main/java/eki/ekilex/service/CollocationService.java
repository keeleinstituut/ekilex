package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.CollocConjunct;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.CollocMemberMeaning;
import eki.ekilex.data.CollocWeight;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.CollocationDbService;

@Component
public class CollocationService implements SystemConstant, GlobalConstant {

	@Value("${collocation.conjunct.lexemeid.and}")
	private Long collocationConjunctLexemeIdAnd;

	@Value("${collocation.conjunct.lexemeid.or}")
	private Long collocationConjunctLexemeIdOr;

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

	@Transactional(rollbackFor = Exception.class)
	public void createCollocMember(
			Long collocLexemeId,
			Long collocMemberLexemeId,
			Long collocMemberFormId,
			Long conjunctLexemeId,
			String posGroupCode,
			String relGroupCode,
			BigDecimal weight) {

		if (conjunctLexemeId != null) {
			relGroupCode = COLLOCATION_REL_GROUP_CODE_CONJUNCT;
		}
		Integer memberOrder = collocationDbService.getMaxMemberOrder(collocLexemeId);
		if (memberOrder == null) {
			memberOrder = 1;
		} else {
			memberOrder = memberOrder + 1;
		}
		Integer groupOrder = null;
		if (StringUtils.isNotBlank(posGroupCode) && StringUtils.isNotBlank(relGroupCode)) {
			groupOrder = collocationDbService.getMaxGroupOrder(collocMemberLexemeId, posGroupCode, relGroupCode);
			if (groupOrder == null) {
				groupOrder = 1;
			} else {
				groupOrder = groupOrder + 1;
			}
		}

		CollocMember collocMember = new CollocMember();
		collocMember.setCollocLexemeId(collocLexemeId);
		collocMember.setMemberLexemeId(collocMemberLexemeId);
		collocMember.setMemberFormId(collocMemberFormId);
		collocMember.setConjunctLexemeId(conjunctLexemeId);
		collocMember.setPosGroupCode(posGroupCode);
		collocMember.setRelGroupCode(relGroupCode);
		collocMember.setWeight(weight);
		collocMember.setMemberOrder(memberOrder);
		collocMember.setGroupOrder(groupOrder);

		collocationDbService.createCollocMember(collocMember);
	}

	public List<CollocMemberForm> getCollocMemberForms(String formValue, String lang, String datasetCode) {

		List<CollocMemberForm> collocMemberForms = collocationDbService.getCollocMemberForms(formValue, lang, datasetCode, CLASSIF_LABEL_LANG_EST);

		if (CollectionUtils.isNotEmpty(collocMemberForms)) {
			collocMemberForms = collocMemberForms.stream().distinct().collect(Collectors.toList());
			CollocMemberForm firstCollocMemberForm = collocMemberForms.get(0);
			firstCollocMemberForm.setSelected(true);
			if (collocMemberForms.size() == 1) {
				List<CollocMemberMeaning> collocMemberMeanings = firstCollocMemberForm.getCollocMemberMeanings();
				if (CollectionUtils.isNotEmpty(collocMemberMeanings)) {
					if (collocMemberMeanings.size() == 1) {
						CollocMemberMeaning onlyCollocMemberMeaning = collocMemberMeanings.get(0);
						onlyCollocMemberMeaning.setSelected(true);
					}
				}
			}
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

	@Transactional
	public List<CollocConjunct> getCollocConjuncts() {

		List<CollocConjunct> collocConjuncts = new ArrayList<>();
		CollocConjunct collocConjunct;
		String label;
		label = collocationDbService.getWordValueByLexemeId(collocationConjunctLexemeIdAnd);
		collocConjunct = new CollocConjunct(collocationConjunctLexemeIdAnd, label);
		collocConjuncts.add(collocConjunct);
		label = collocationDbService.getWordValueByLexemeId(collocationConjunctLexemeIdOr);
		collocConjunct = new CollocConjunct(collocationConjunctLexemeIdOr, label);
		collocConjuncts.add(collocConjunct);

		return collocConjuncts;
	}

}
