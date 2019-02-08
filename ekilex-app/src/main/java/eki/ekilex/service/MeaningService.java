package eki.ekilex.service;

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
public class MeaningService {

	private static final Logger logger = LoggerFactory.getLogger(MeaningService.class);

	private final LifecycleLogDbService lifecycleLogDbService;

	private final MeaningDbService meaningDbService;

	private final DefinitionDbService definitionDbService;

	private final LexemeDbService lexemeDbService;

	public MeaningService(
			LifecycleLogDbService lifecycleLogDbService,
			MeaningDbService meaningDbService,
			DefinitionDbService definitionDbService,
			LexemeDbService lexemeDbService) {
		this.lifecycleLogDbService = lifecycleLogDbService;
		this.meaningDbService = meaningDbService;
		this.definitionDbService = definitionDbService;
		this.lexemeDbService = lexemeDbService;
	}

	@Transactional
	public Optional<Long> duplicateMeaning(Long meaningId) {

		try {
			Long duplicateMeaningId = meaningDbService.cloneMeaning(meaningId);
			meaningDbService.cloneMeaningDomains(meaningId, duplicateMeaningId);
			meaningDbService.cloneMeaningRelations(meaningId, duplicateMeaningId);
			meaningDbService.cloneMeaningFreeforms(meaningId, duplicateMeaningId);
			duplicateMeaningDefinitions(meaningId, duplicateMeaningId);
			duplicateMeaningLexemes(meaningId, duplicateMeaningId);
			return Optional.of(duplicateMeaningId);
		} catch (Exception e) {
			logger.error("duplicate meaning", e);
			return Optional.empty();
		}
	}

	private void duplicateMeaningLexemes(Long meaningId, Long duplicateMeaningId) {
		List<LexemeRecord> meaningLexemes = lexemeDbService.findMeaningLexemes(meaningId);
		meaningLexemes.forEach(meaningLexeme -> {
			Long duplicateLexemeId = lexemeDbService.cloneMeaningLexeme(meaningLexeme.getId(), duplicateMeaningId);
			lexemeDbService.cloneLexemeDerivatives(meaningLexeme.getId(), duplicateLexemeId);
			lexemeDbService.cloneLexemeFreeforms(meaningLexeme.getId(), duplicateLexemeId);
			lexemeDbService.cloneLexemePoses(meaningLexeme.getId(), duplicateLexemeId);
			lexemeDbService.cloneLexemeRegisters(meaningLexeme.getId(), duplicateLexemeId);
			lexemeDbService.cloneLexemeSoureLinks(meaningLexeme.getId(), duplicateLexemeId);
		});
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
