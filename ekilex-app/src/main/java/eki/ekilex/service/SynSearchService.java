package eki.ekilex.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.SynRelation;
import eki.ekilex.data.Relation;
import eki.ekilex.data.RelationParam;
import eki.ekilex.data.WordDetails;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	@Autowired
	private SynSearchDbService synSearchDbService;

	public WordDetails getWordDetailsSynonyms(Long wordId, List<String> selectedDatasetCodes) {
		WordDetails details = getWordDetails(wordId, selectedDatasetCodes);
		populateSynRelations(details);


		return details;

	}

	private void populateSynRelations(WordDetails wordDetails) {

		wordDetails.setWordSynRelations(new ArrayList<>());

		List<Relation> rawRelations = wordDetails.getWordRelations()
				.stream()
				.filter(r-> RAW_RELATION_CODE.equals(r.getRelationTypeCode()))
				.collect(toList());

		if (CollectionUtils.isNotEmpty(rawRelations)) {
			rawRelations.forEach(relation -> {
				SynRelation synRelation = new SynRelation();
				synRelation.setWordRelation(relation);

				Relation oppositeRelation = synSearchDbService.
						getOppositeRelation(wordDetails.getWord().getWordId(), relation.getWordId(), RAW_RELATION_CODE, classifierLabelLang, classifierLabelTypeDescrip);

				synRelation.setOppositeRelation(oppositeRelation);

				List<RelationParam> relationParams = synSearchDbService.getRelationParameters(relation.getId());
				synRelation.setRelationParams(relationParams);

				wordDetails.getWordSynRelations().add(synRelation);

			});
		}
	}
}
