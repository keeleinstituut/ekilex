package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.InexactSynMeaning;
import eki.ekilex.data.InexactSynMeaningCandidate;
import eki.ekilex.data.InexactSynMeaningRequest;
import eki.ekilex.data.Word;
import eki.ekilex.service.InexactSynService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class InexactSynController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(InexactSynController.class);

	@Autowired
	private InexactSynService inexactSynService;

	@PostMapping(INEXACT_SYN_INIT_URI)
	public String initInexactSynSearch(
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("targetLang") String targetLang,
			@RequestParam("wordRelationId") Long wordRelationId,
			Model model) {

		Word translationLangWord = inexactSynService.getSynCandidateWord(wordRelationId);

		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetLang", targetLang);
		model.addAttribute("wordRelationId", wordRelationId);
		model.addAttribute("inexactTranslationWord", translationLangWord);
		model.addAttribute("isSearchEnabled", true);

		return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_meaning_select";
	}

	@PostMapping(INEXACT_SYN_SEARCH_MEANINGS_URI)
	public String searchInexactSynMeanings(
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("targetLang") String targetLang,
			@RequestParam("targetLangWordValue") String targetLangWordValue,
			@RequestParam("wordRelationId") Long wordRelationId,
			Model model) {

		String datasetCode = getDatasetCodeFromRole();
		Word translationLangWord = inexactSynService.getSynCandidateWord(wordRelationId);
		List<InexactSynMeaningCandidate> meaningCandidates = inexactSynService.getInexactSynMeaningCandidates(wordRelationId, targetLang, targetLangWordValue, datasetCode);

		if (meaningCandidates.isEmpty()) {
			// TODO create new meaning, return word select
		}

		// TODO disable selection where translation lang value is inserted but meaning candidate already has inexact syn definition

		// TODO use wrapper?
		model.addAttribute("wordRelationId", wordRelationId);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetLang", targetLang);
		model.addAttribute("targetLangWordValue", targetLangWordValue);
		model.addAttribute("inexactTranslationWord", translationLangWord);
		model.addAttribute("meaningCandidates", meaningCandidates);
		model.addAttribute("isSearchEnabled", false);

		return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_meaning_select";
	}

	@PostMapping(INEXACT_SYN_MEANING_URI)
	public String updateInexactSynMeaning (
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("targetLang") String targetLang,
			@RequestParam("targetLangWordValue") String targetLangWordValue,
			@RequestParam("wordRelationId") Long wordRelationId,
			@RequestParam("inexactSynMeaningId") Long inexactSynMeaningId,
			Model model) {

		model.addAttribute("wordRelationId", wordRelationId);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetLang", targetLang);
		model.addAttribute("targetLangWordValue", targetLangWordValue);

		DatasetPermission userRole = userContext.getUserRole();
		boolean createNewMeaning = inexactSynMeaningId == 0;
		boolean isTargetLangWordSearch = StringUtils.isNotBlank(targetLangWordValue);

		Word translationLangWord = inexactSynService.getSynCandidateWord(wordRelationId);
		String translationLangWordValue = translationLangWord.getWordValue();
		String translationLang = translationLangWord.getLang();

		if (isTargetLangWordSearch) {
			if (createNewMeaning) {
				InexactSynMeaning newMeaning = inexactSynService
						.initNewInexactSynMeaning(targetLangWordValue, targetLang, translationLangWordValue, translationLang, userRole);
				boolean isMeaningComplete = newMeaning.isComplete();
				if (isMeaningComplete) {
					// TODO meaning is complete, select relation
					// InexactSynMeaningRequest completedInexactSynMeaningData = inexactSynService.initCompletedInexactSynMeaning(...);
					// model.addAttribute("data", completedInexactSynMeaningData);
					// return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
				} else {
					model.addAttribute("meaning", newMeaning);
					return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
				}
			}

			boolean meaningHasTargetLangWord = lookupService.meaningHasWord(inexactSynMeaningId, targetLangWordValue, targetLang);
			boolean meaningHasTranslationLangWord = lookupService.meaningHasWord(inexactSynMeaningId, translationLangWordValue, translationLang);

			if (meaningHasTargetLangWord && meaningHasTranslationLangWord) {
				// TODO meaning is complete, select relation
				// InexactSynMeaningRequest completedInexactSynMeaningData = inexactSynService.initCompletedInexactSynMeaning(...);
				// model.addAttribute("data", completedInexactSynMeaningData);
				// return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
			}

			if (meaningHasTargetLangWord) {
				InexactSynMeaning existingMeaning = inexactSynService
						.initExistingInexactSynMeaning(inexactSynMeaningId, targetLangWordValue, targetLang, translationLangWordValue, translationLang, userRole);
				model.addAttribute("meaning", existingMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			}

			if (meaningHasTranslationLangWord) {
				InexactSynMeaning existingMeaning = inexactSynService
						.initExistingInexactSynMeaning(inexactSynMeaningId, targetLangWordValue, targetLang, translationLangWordValue, translationLang, userRole);
				model.addAttribute("meaning", existingMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			}

		} else {

			if (createNewMeaning) {
				InexactSynMeaning newMeaning = inexactSynService
						.initNewInexactSynMeaning(null, targetLang, translationLangWordValue, translationLang, userRole);
				model.addAttribute("meaning", newMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			} else {
				InexactSynMeaning existingMeaning = inexactSynService
						.initExistingInexactSynMeaning(inexactSynMeaningId, targetLangWordValue, targetLang, translationLangWordValue, translationLang, userRole);
				model.addAttribute("meaning", existingMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			}
		}

		return null;
	}

	@PostMapping(INEXACT_SYN_WORD_URI)
	public String initInexactSynMeaningAndRelationCreate(InexactSynMeaningRequest inexactSynMeaningRequest, Model model) {

		InexactSynMeaningRequest completedInexactSynMeaningData = inexactSynService.initCompletedInexactSynMeaning(inexactSynMeaningRequest);
		model.addAttribute("data", completedInexactSynMeaningData);

		return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
	}

	@ResponseBody
	@PostMapping(INEXACT_SYN_MEANING_RELATION_URI)
	public String createInexactSynMeaningAndRelation(InexactSynMeaningRequest inexactSynMeaningRequest) {

		String datasetCode = getDatasetCodeFromRole();
		try {
			inexactSynService.saveInexactSynMeaningAndRelation(inexactSynMeaningRequest, datasetCode);
		} catch (Exception e) {
			// TODO log and return message to user
		}

		// TODO return message
		return RESPONSE_OK_VER1;
	}

}
