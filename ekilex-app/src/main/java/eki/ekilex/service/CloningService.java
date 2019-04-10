package eki.ekilex.service;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.DefinitionDbService;
import eki.ekilex.service.db.LexemeDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.MeaningDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CloningService {

	private static final Logger logger = LoggerFactory.getLogger(CloningService.class);

	private final LifecycleLogDbService lifecycleLogDbService;

	private final MeaningDbService meaningDbService;

	private final DefinitionDbService definitionDbService;

	private final LexemeDbService lexemeDbService;

	private final UserService userService;

	public CloningService(
			LifecycleLogDbService lifecycleLogDbService,
			MeaningDbService meaningDbService,
			DefinitionDbService definitionDbService,
			LexemeDbService lexemeDbService,
			UserService userService) {
		this.lifecycleLogDbService = lifecycleLogDbService;
		this.meaningDbService = meaningDbService;
		this.definitionDbService = definitionDbService;
		this.lexemeDbService = lexemeDbService;
		this.userService = userService;
	}

	@Transactional
	public Optional<Long> cloneMeaning(Long meaningId) {
		return Optional.of(duplicateMeaningWithLexemes(meaningId));
	}

	@Transactional
	public Optional<Long> cloneLexeme(Long lexemeId) {
		return Optional.of(duplicateLexemeAndMeaning(lexemeId));
	}

	private Long duplicateMeaningWithLexemes(Long meaningId) {

		Long duplicateMeaningId = duplicateMeaning(meaningId);
		duplicateMeaningLexemes(meaningId, duplicateMeaningId);
		return duplicateMeaningId;
	}

	private Long duplicateMeaning(Long meaningId) {

		Long duplicateMeaningId = meaningDbService.cloneMeaning(meaningId);
		meaningDbService.cloneMeaningDomains(meaningId, duplicateMeaningId);
		meaningDbService.cloneMeaningRelations(meaningId, duplicateMeaningId);
		meaningDbService.cloneMeaningFreeforms(meaningId, duplicateMeaningId);
		duplicateMeaningDefinitions(meaningId, duplicateMeaningId);
		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.addLog(
				userName,
				LifecycleEventType.CLONE,
				LifecycleEntity.MEANING,
				LifecycleProperty.VALUE,
				duplicateMeaningId,
				null,
				definitionDbService.getCombinedMeaningDefinitions(duplicateMeaningId));
		return duplicateMeaningId;
	}

	private Long duplicateLexemeAndMeaning(Long lexemeId) {

		LexemeRecord lexeme = lexemeDbService.findLexeme(lexemeId);
		Long meaningId = duplicateMeaning(lexeme.getMeaningId());
		return duplicateLexeme(lexemeId, meaningId);
	}

	private Long duplicateLexeme(Long lexemeId, Long meaningId) {

		Long duplicateLexemeId = lexemeDbService.cloneLexeme(lexemeId, meaningId);
		lexemeDbService.cloneLexemeDerivatives(lexemeId, duplicateLexemeId);
		lexemeDbService.cloneLexemeFreeforms(lexemeId, duplicateLexemeId);
		lexemeDbService.cloneLexemePoses(lexemeId, duplicateLexemeId);
		lexemeDbService.cloneLexemeRegisters(lexemeId, duplicateLexemeId);
		lexemeDbService.cloneLexemeSoureLinks(lexemeId, duplicateLexemeId);
		lexemeDbService.cloneLexemeRelations(lexemeId, duplicateLexemeId);
		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.addLog(
				userName,
				LifecycleEventType.CLONE,
				LifecycleEntity.LEXEME,
				LifecycleProperty.VALUE,
				duplicateLexemeId,
				lexemeDbService.getLogStringForLexemeShort(lexemeId),
				lexemeDbService.getLogStringForLexemeLong(duplicateLexemeId));
		return duplicateLexemeId;
	}

	private void duplicateMeaningLexemes(Long meaningId, Long duplicateMeaningId) {

		List<LexemeRecord> meaningLexemes = lexemeDbService.findMeaningLexemes(meaningId);
		meaningLexemes.forEach(meaningLexeme -> duplicateLexeme(meaningLexeme.getId(), duplicateMeaningId));
	}

	private void duplicateMeaningDefinitions(Long meaningId, Long duplicateMeaningId) {

		List<DefinitionRecord> meaningDefinitions = definitionDbService.findMeaningDefinitions(meaningId);
		meaningDefinitions.forEach(meaningDefinition -> {
			Long duplicateDefinintionId = definitionDbService.cloneMeaningDefinition(meaningDefinition.getId(), duplicateMeaningId);
			definitionDbService.cloneDefinitionFreeforms(meaningDefinition.getId(), duplicateDefinintionId);
			definitionDbService.cloneDefinitionDatasets(meaningDefinition.getId(), duplicateDefinintionId);
			definitionDbService.cloneDefinitionSourceLinks(meaningDefinition.getId(), duplicateDefinintionId);
		});
	}

}
