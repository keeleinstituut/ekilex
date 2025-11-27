package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.CollocConjunct;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.CollocMemberMeaning;
import eki.ekilex.data.CollocMemberOrder;
import eki.ekilex.data.CollocWeight;
import eki.ekilex.data.Response;
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
	public void deleteCollocMember(Long collocMemberId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(collocMemberId, ActivityEntity.COLLOC_MEMBER);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteCollocMember", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		collocationDbService.deleteCollocMember(collocMemberId);
		activityLogService.createActivityLog(activityLog, collocMemberId, ActivityEntity.COLLOC_MEMBER);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateCollocMemberPosGroup(Long collocMemberId, String posGroupCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(collocMemberId, ActivityEntity.COLLOC_MEMBER);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateCollocMemberPosGroup", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		collocationDbService.updateCollocMemberPosGroup(collocMemberId, posGroupCode);
		activityLogService.createActivityLog(activityLog, collocMemberId, ActivityEntity.COLLOC_MEMBER);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateCollocMemberRelGroup(Long collocMemberId, String relGroupCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(collocMemberId, ActivityEntity.COLLOC_MEMBER);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateCollocMemberRelGroup", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		collocationDbService.updateCollocMemberRelGroup(collocMemberId, relGroupCode);
		activityLogService.createActivityLog(activityLog, collocMemberId, ActivityEntity.COLLOC_MEMBER);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateCollocMemberGroupOrder(
			Long collocLexemeId,
			Long memberLexemeId,
			String direction,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateCollocMemberGroupOrder", memberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		List<CollocMemberOrder> collocMembers = collocationDbService.getCollocMemberOrdersOfRelGroup(collocLexemeId, memberLexemeId);
		int collocMemberCount = collocMembers.size();
		CollocMemberOrder sourceCollocMember = collocMembers.stream()
				.filter(collocMember -> collocMember.getCollocLexemeId().equals(collocLexemeId))
				.findFirst()
				.get();
		int sourceCollocMemberIndex = collocMembers.indexOf(sourceCollocMember);
		if (sourceCollocMemberIndex < 0) {
			return;
		}
		CollocMemberOrder targetCollocMember = null;
		if (StringUtils.equalsIgnoreCase(direction, "up") && (sourceCollocMemberIndex > 0)) {
			targetCollocMember = collocMembers.get(sourceCollocMemberIndex - 1);
		} else if (StringUtils.equalsIgnoreCase(direction, "down") && (sourceCollocMemberIndex < (collocMemberCount - 1))) {
			targetCollocMember = collocMembers.get(sourceCollocMemberIndex + 1);
		}
		if (targetCollocMember != null) {
			Long sourceCollocMemberId = sourceCollocMember.getId();
			Integer sourceCollocGroupOrder = sourceCollocMember.getGroupOrder();
			Long targetCollocMemberId = targetCollocMember.getId();
			Integer targetCollocGroupOrder = targetCollocMember.getGroupOrder();
			collocationDbService.updateLexemeCollocMemberGroupOrder(sourceCollocMemberId, targetCollocGroupOrder);
			collocationDbService.updateLexemeCollocMemberGroupOrder(targetCollocMemberId, sourceCollocGroupOrder);
		}
		activityLogService.createActivityLog(activityLog, memberLexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateCollocMemberOrder(
			Long collocLexemeId,
			Long memberLexemeId,
			String direction,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateCollocMemberOrder", memberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		List<CollocMemberOrder> collocMembers = collocationDbService.getCollocMemberOrders(collocLexemeId);
		int collocMemberCount = collocMembers.size();
		CollocMemberOrder sourceCollocMember = collocMembers.stream()
				.filter(collocMember -> collocMember.getMemberLexemeId().equals(memberLexemeId))
				.findFirst()
				.get();
		int sourceCollocMemberIndex = collocMembers.indexOf(sourceCollocMember);
		if (sourceCollocMemberIndex < 0) {
			return;
		}
		CollocMemberOrder targetCollocMember = null;
		if (StringUtils.equalsIgnoreCase(direction, "up") && (sourceCollocMemberIndex > 0)) {
			targetCollocMember = collocMembers.get(sourceCollocMemberIndex - 1);
		} else if (StringUtils.equalsIgnoreCase(direction, "down") && (sourceCollocMemberIndex < (collocMemberCount - 1))) {
			targetCollocMember = collocMembers.get(sourceCollocMemberIndex + 1);
		}
		if (targetCollocMember != null) {
			Long sourceCollocMemberId = sourceCollocMember.getId();
			Integer sourceCollocMemberOrder = sourceCollocMember.getMemberOrder();
			Long targetCollocMemberId = targetCollocMember.getId();
			Integer targetCollocMemberOrder = targetCollocMember.getMemberOrder();
			collocationDbService.updateLexemeCollocMemberOrder(sourceCollocMemberId, targetCollocMemberOrder);
			collocationDbService.updateLexemeCollocMemberOrder(targetCollocMemberId, sourceCollocMemberOrder);
		}
		activityLogService.createActivityLog(activityLog, memberLexemeId, ActivityEntity.LEXEME);
	}

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
	public Response saveCollocMember(CollocMember collocMember, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long collocMemberId = collocMember.getId();
		Long collocLexemeId = collocMember.getCollocLexemeId();
		Long memberLexemeId = collocMember.getMemberLexemeId();
		Long conjunctLexemeId = collocMember.getConjunctLexemeId();
		String posGroupCode = collocMember.getPosGroupCode();
		String relGroupCode = collocMember.getRelGroupCode();
		BigDecimal weight = collocMember.getWeight();

		Locale locale = LocaleContextHolder.getLocale();
		ResponseStatus responseStatus;
		String message;

		if (weight == null) {
			responseStatus = ResponseStatus.INVALID;
			message = messageSource.getMessage("colloc.message.norole", new Object[0], locale);
		} else if (memberLexemeId == null) {
			responseStatus = ResponseStatus.INVALID;
			message = messageSource.getMessage("colloc.message.nomeaning", new Object[0], locale);
		} else {

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
				groupOrder = collocationDbService.getMaxGroupOrder(memberLexemeId, posGroupCode, relGroupCode);
				if (groupOrder == null) {
					groupOrder = 1;
				} else {
					groupOrder = groupOrder + 1;
				}
			}

			collocMember.setRelGroupCode(relGroupCode);
			collocMember.setMemberOrder(memberOrder);
			collocMember.setGroupOrder(groupOrder);

			ActivityLogData activityLog = activityLogService.prepareActivityLog("saveCollocMember", collocLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);

			if (collocMemberId == null) {
				collocMemberId = collocationDbService.createCollocMember(collocMember);
				responseStatus = ResponseStatus.OK;
				message = messageSource.getMessage("colloc.message.createmember", new Object[0], locale);
			} else {
				collocationDbService.updateCollocMember(collocMember);
				responseStatus = ResponseStatus.OK;
				message = messageSource.getMessage("colloc.message.updatemember", new Object[0], locale);
			}

			activityLogService.createActivityLog(activityLog, collocMemberId, ActivityEntity.COLLOC_MEMBER);
		}

		Response response = new Response();
		response.setStatus(responseStatus);
		response.setMessage(message);

		return response;
	}

	public CollocMember getCollocMember(Long id) {
		CollocMember collocMember = collocationDbService.getCollocMember(id, CLASSIF_LABEL_LANG_EST);
		return collocMember;
	}

	@Transactional
	public List<CollocMemberForm> getCollocMemberForms(String formValue, String lang, String datasetCode) {

		List<CollocMemberForm> collocMemberForms = collocationDbService.getCollocMemberForms(formValue, lang, datasetCode, CLASSIF_LABEL_LANG_EST);

		if (CollectionUtils.isNotEmpty(collocMemberForms)) {
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

	@Transactional
	public List<CollocMemberForm> getCollocMemberForms(CollocMember collocMember) {

		String datasetCode = collocMember.getDatasetCode();
		String lang = collocMember.getLang();
		Long memberMeaningId = collocMember.getMemberMeaningId();
		Long memberFormId = collocMember.getMemberFormId();
		String memberFormValue = collocMember.getMemberFormValue();
		List<CollocMemberForm> collocMemberForms = collocationDbService.getCollocMemberForms(memberFormValue, lang, datasetCode, CLASSIF_LABEL_LANG_EST);

		if (CollectionUtils.isNotEmpty(collocMemberForms)) {
			for (CollocMemberForm collocMemberForm : collocMemberForms) {
				boolean isFormSelected = memberFormId.equals(collocMemberForm.getFormId());
				collocMemberForm.setSelected(isFormSelected);
				List<CollocMemberMeaning> collocMemberMeanings = collocMemberForm.getCollocMemberMeanings();
				if (CollectionUtils.isNotEmpty(collocMemberMeanings)) {
					for (CollocMemberMeaning collocMemberMeaning : collocMemberMeanings) {
						boolean isMeaningSelected = memberMeaningId.equals(collocMemberMeaning.getMeaningId());
						collocMemberMeaning.setSelected(isMeaningSelected);
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
