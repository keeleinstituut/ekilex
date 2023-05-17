package eki.ekilex.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.MeaningTableRow;
import eki.ekilex.data.MeaningTableSearchResult;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TypeMtDefinition;
import eki.ekilex.data.TypeMtLexeme;
import eki.ekilex.data.TypeMtLexemeFreeform;
import eki.ekilex.data.TypeMtWord;
import eki.ekilex.service.db.MeaningTableDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class MeaningTableService extends AbstractSearchService {

	@Autowired
	private MeaningTableDbService meaningTableDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Autowired
	private TextDecorationService textDecorationService;

	@Transactional
	public MeaningTableSearchResult getMeaningTableSearchResult(
			String searchFilter, List<String> selectedDatasetCodes, String resultLang, EkiUser user) {

		int offset = DEFAULT_OFFSET;
		boolean noLimit = false;

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		MeaningTableSearchResult meaningTableSearchResult = meaningTableDbService.getMeaningTableSearchResult(searchFilter, searchDatasetsRestriction, resultLang, offset, noLimit);
		aggregate(meaningTableSearchResult);
		applyCrud(meaningTableSearchResult, user);

		return meaningTableSearchResult;
	}

	@Transactional
	public MeaningTableSearchResult getMeaningTableSearchResult(
			SearchFilter searchFilter, List<String> selectedDatasetCodes, String resultLang, EkiUser user) throws Exception {

		int offset = DEFAULT_OFFSET;
		boolean noLimit = false;

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		MeaningTableSearchResult meaningTableSearchResult = meaningTableDbService.getMeaningTableSearchResult(searchFilter, searchDatasetsRestriction, resultLang, offset, noLimit);
		aggregate(meaningTableSearchResult);
		applyCrud(meaningTableSearchResult, user);

		return meaningTableSearchResult;
	}

	private void aggregate(MeaningTableSearchResult meaningTableSearchResult) {

		List<MeaningTableRow> results = meaningTableSearchResult.getResults();

		for (MeaningTableRow meaningTableRow : results) {
			aggregateMeaningTableRow(meaningTableRow);
		}
	}

	private void aggregateMeaningTableRow(MeaningTableRow meaningTableRow) {

		List<TypeMtLexeme> lexemes = meaningTableRow.getLexemes();
		List<TypeMtWord> words = meaningTableRow.getWords();
		List<TypeMtLexemeFreeform> usages = meaningTableRow.getUsages();
		Map<IdPair, TypeMtWord> wordMap = words.stream().collect(Collectors.toMap(word -> new IdPair(word.getWordId(), word.getLexemeId()), word -> word));
		Map<Long, List<TypeMtLexemeFreeform>> lexemeUsagesMap = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(usages)) {
			lexemeUsagesMap = usages.stream().collect(Collectors.groupingBy(TypeMtLexemeFreeform::getLexemeId));
		}
		int usageIndex = 0;
		for (TypeMtLexeme lexeme : lexemes) {

			Long lexemeId = lexeme.getLexemeId();
			Long wordId = lexeme.getWordId();
			IdPair wordKey = new IdPair(wordId, lexemeId);
			TypeMtWord lexemeWord = wordMap.get(wordKey);
			List<TypeMtLexemeFreeform> lexemeUsages = lexemeUsagesMap.get(lexemeId);
			if (CollectionUtils.isNotEmpty(lexemeUsages)) {
				for (TypeMtLexemeFreeform lexemeUsage : lexemeUsages) {
					lexemeUsage.setIndex(usageIndex++);
				}
			}
			lexeme.setWord(lexemeWord);
			lexeme.setUsages(lexemeUsages);
		}
	}

	@Transactional
	public MeaningTableRow getMeaningTableRow(Long meaningId, EkiUser user) {

		DatasetPermission userRole = user.getRecentRole();
		MeaningTableRow meaningTableRow = meaningTableDbService.getMeaningTableRow(meaningId);
		aggregateMeaningTableRow(meaningTableRow);
		applyCrud(meaningTableRow, userRole);
		return meaningTableRow;
	}

	private void applyCrud(MeaningTableSearchResult meaningTableSearchResult, EkiUser user) {

		List<MeaningTableRow> results = meaningTableSearchResult.getResults();
		DatasetPermission userRole = user.getRecentRole();

		for (MeaningTableRow meaningTableRow : results) {
			applyCrud(meaningTableRow, userRole);
		}
	}

	private void applyCrud(MeaningTableRow meaningTableRow, DatasetPermission userRole) {

		List<TypeMtLexeme> lexemes = ListUtils.emptyIfNull(meaningTableRow.getLexemes());
		List<TypeMtDefinition> definitions = ListUtils.emptyIfNull(meaningTableRow.getDefinitions());
		List<TypeMtLexemeFreeform> usages = ListUtils.emptyIfNull(meaningTableRow.getUsages());

		permCalculator.applyCrud(userRole, lexemes);
		permCalculator.applyCrud(userRole, definitions);
		permCalculator.applyCrud(userRole, usages);
	}

	@Transactional
	public void updateTermMeaningTableMeaning(MeaningTableRow meaning, EkiUser user, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = user.getName();
		Long meaningId = meaning.getMeaningId();
		List<TypeMtDefinition> definitions = ListUtils.emptyIfNull(meaning.getDefinitions());
		List<TypeMtLexeme> lexemes = ListUtils.emptyIfNull(meaning.getLexemes());
		List<TypeMtLexemeFreeform> usages = ListUtils.emptyIfNull(meaning.getUsages());

		for (TypeMtDefinition definition : definitions) {
			Long definitionId = definition.getDefinitionId();
			String valuePrese = definition.getValuePrese();
			boolean isPublic = definition.isPublic();
			boolean isDefinitionUpdate = meaningTableDbService.isDefinitionUpdate(definitionId, valuePrese, isPublic);
			if (isDefinitionUpdate) {
				String value = textDecorationService.removeEkiElementMarkup(valuePrese);
				ActivityLogData activityLog = activityLogService.prepareActivityLog("updateTermMeaningTableMeaning", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
				meaningTableDbService.updateDefinition(definitionId, value, valuePrese, isPublic);
				activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
			}
		}

		for (TypeMtLexeme lexeme : lexemes) {
			Long lexemeId = lexeme.getLexemeId();
			boolean isPublic = lexeme.isPublic();
			boolean isLexemeUpdate = meaningTableDbService.isLexemeUpdate(lexemeId, isPublic);
			if (isLexemeUpdate) {
				ActivityLogData activityLog = activityLogService.prepareActivityLog("updateTermMeaningTableMeaning", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
				meaningTableDbService.updateLexeme(lexemeId, isPublic);
				activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
			}
		}

		for (TypeMtLexemeFreeform usage : usages) {
			Long usageId = usage.getFreeformId();
			String valuePrese = usage.getValuePrese();
			boolean isPublic = usage.isPublic();
			boolean isUsageUpdate = meaningTableDbService.isUsageUpdate(usageId, valuePrese, isPublic);
			if (isUsageUpdate) {
				String value = textDecorationService.removeEkiElementMarkup(valuePrese);
				Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
				ActivityLogData activityLog = activityLogService.prepareActivityLog("updateTermMeaningTableMeaning", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
				meaningTableDbService.updateUsage(usageId, value, valuePrese, isPublic, userName);
				activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
			}
		}
	}

	@Transactional
	public void updateDefinitionsPublicity(List<Long> definitionIds, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (CollectionUtils.isEmpty(definitionIds)) {
			return;
		}

		for (Long definitionId : definitionIds) {
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionsPublicity", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			meaningTableDbService.updateDefinitionPublicity(definitionId, isPublic);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		}
	}

	@Transactional
	public void updateLexemesPublicity(List<Long> lexemeIds, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (CollectionUtils.isEmpty(lexemeIds)) {
			return;
		}

		for (Long lexemeId : lexemeIds) {
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemesPublicity", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			meaningTableDbService.updateLexemePublicity(lexemeId, isPublic);
			activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateUsagesPublicity(List<Long> usageIds, boolean isPublic, EkiUser user, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (CollectionUtils.isEmpty(usageIds)) {
			return;
		}

		String userName = user.getName();
		for (Long usageId : usageIds) {
			Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsagesPublicity", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			meaningTableDbService.updateUsagePublicity(usageId, isPublic, userName);
			activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		}
	}
}
