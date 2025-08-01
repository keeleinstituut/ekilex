package eki.ekilex.web.controller;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.InexactSynMeaning;
import eki.ekilex.data.InexactSynMeaningRequest;
import eki.ekilex.data.Response;
import eki.ekilex.data.Word;
import eki.ekilex.service.NearSynService;

// TODO rename inexact -> near everywhere
@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class NearSynController extends AbstractPrivateSearchController {

	private static final Logger logger = LoggerFactory.getLogger(NearSynController.class);

	@Autowired
	private NearSynService nearSynService;

	@PostMapping(INEXACT_SYN_INIT_URI)
	@PreAuthorize("@permEval.isUserRoleSelected(authentication)")
	public String initInexactSynSearch(
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("targetLang") String targetLang,
			@RequestParam("wordRelationId") Long wordRelationId,
			Model model) throws Exception {

		String datasetCode = getRoleDatasetCode();
		Word translationLangWord = nearSynService.getSynCandidateWord(wordRelationId);
		String translationLangWordValue = translationLangWord.getWordValue();
		String translationLang = translationLangWord.getLang();

		InexactSynMeaningRequest requestData = new InexactSynMeaningRequest();
		requestData.setDatasetCode(datasetCode);
		requestData.setTargetMeaningId(targetMeaningId);
		requestData.setTargetLang(targetLang);
		requestData.setWordRelationId(wordRelationId);
		requestData.setTranslationLangWordValue(translationLangWordValue);
		requestData.setTranslationLang(translationLang);
		requestData.setSearchEnabled(true);

		model.addAttribute("data", requestData);

		return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_meaning_select";
	}

	@PostMapping(INEXACT_SYN_SEARCH_MEANINGS_URI)
	public String searchInexactSynMeanings(InexactSynMeaningRequest requestData, Model model) {

		Long wordRelationId = requestData.getWordRelationId();
		String targetLang = requestData.getTargetLang();
		String targetLangWordValue = requestData.getTargetLangWordValue();
		String datasetCode = requestData.getDatasetCode();
		String translationLangWordValue = requestData.getTranslationLangWordValue();
		String translationLang = requestData.getTranslationLang();
		boolean revertToPreviousStep = requestData.isRevertToPreviousStep();

		EkiUser user = userContext.getUser();
		List<InexactSynMeaning> meaningCandidates = nearSynService.getInexactSynMeaningCandidates(wordRelationId, targetLang, targetLangWordValue, datasetCode);

		requestData.setSearchEnabled(false);
		model.addAttribute("meaningCandidates", meaningCandidates);
		model.addAttribute("data", requestData);

		if (meaningCandidates.isEmpty()) {
			if (revertToPreviousStep) {
				requestData.setSearchEnabled(true);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_meaning_select";
			}
			InexactSynMeaning newMeaning = nearSynService
					.initNewInexactSynMeaning(targetLangWordValue, targetLang, translationLangWordValue, translationLang, user);
			boolean isMeaningComplete = newMeaning.isComplete();
			if (isMeaningComplete) {
				InexactSynMeaningRequest completedInexactSynMeaningData = nearSynService.initCompletedInexactSynMeaning(requestData);
				model.addAttribute("data", completedInexactSynMeaningData);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
			} else {
				model.addAttribute("meaning", newMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			}
		}

		return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_meaning_select";
	}

	@PostMapping(INEXACT_SYN_MEANING_URI)
	public String updateInexactSynMeaning(InexactSynMeaningRequest requestData, Model model) {

		String targetLang = requestData.getTargetLang();
		String targetLangWordValue = requestData.getTargetLangWordValue();
		String translationLang = requestData.getTranslationLang();
		String translationLangWordValue = requestData.getTranslationLangWordValue();
		Long inexactSynMeaningId = requestData.getInexactSynMeaningId();

		model.addAttribute("data", requestData);

		EkiUser user = userContext.getUser();
		boolean createNewMeaning = inexactSynMeaningId == null;
		boolean isTargetLangWordSearch = StringUtils.isNotBlank(targetLangWordValue);

		if (isTargetLangWordSearch) {
			if (createNewMeaning) {
				InexactSynMeaning newMeaning = nearSynService
						.initNewInexactSynMeaning(targetLangWordValue, targetLang, translationLangWordValue, translationLang, user);
				boolean isMeaningComplete = newMeaning.isComplete();
				if (isMeaningComplete) {
					InexactSynMeaningRequest completedInexactSynMeaningData = nearSynService.initCompletedInexactSynMeaning(requestData);
					model.addAttribute("data", completedInexactSynMeaningData);
					return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
				} else {
					model.addAttribute("meaning", newMeaning);
					return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
				}
			}

			boolean meaningHasTargetLangWord = lookupService.meaningHasWord(inexactSynMeaningId, targetLangWordValue, targetLang);
			boolean meaningHasTranslationLangWord = lookupService.meaningHasWord(inexactSynMeaningId, translationLangWordValue, translationLang);

			if (meaningHasTargetLangWord && meaningHasTranslationLangWord) {
				InexactSynMeaningRequest completedInexactSynMeaningData = nearSynService.initCompletedInexactSynMeaning(requestData);
				model.addAttribute("data", completedInexactSynMeaningData);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
			} else {
				InexactSynMeaning existingMeaning = nearSynService
						.initExistingInexactSynMeaning(inexactSynMeaningId, targetLangWordValue, targetLang, translationLangWordValue, translationLang, user);

				boolean isMeaningComplete = existingMeaning.isComplete();
				if (isMeaningComplete) {
					InexactSynMeaningRequest completedInexactSynMeaningData = nearSynService.initCompletedInexactSynMeaning(requestData);
					model.addAttribute("data", completedInexactSynMeaningData);
					return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
				} else {
					model.addAttribute("meaning", existingMeaning);
					return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
				}
			}

		} else { // inexact syn def is required

			if (createNewMeaning) {
				InexactSynMeaning newMeaning = nearSynService
						.initNewInexactSynMeaning(null, targetLang, translationLangWordValue, translationLang, user);
				model.addAttribute("meaning", newMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			} else {
				InexactSynMeaning existingMeaning = nearSynService
						.initExistingInexactSynMeaning(inexactSynMeaningId, targetLangWordValue, targetLang, translationLangWordValue, translationLang, user);
				model.addAttribute("meaning", existingMeaning);
				return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_word_select";
			}
		}
	}

	@PostMapping(INEXACT_SYN_WORD_URI)
	public String initInexactSynMeaningAndRelationCreate(InexactSynMeaningRequest requestData, Model model) {

		InexactSynMeaningRequest completedInexactSynMeaningData = nearSynService.initCompletedInexactSynMeaning(requestData);
		model.addAttribute("data", completedInexactSynMeaningData);

		return INEXACT_SYN_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "inexact_syn_relation_select";
	}

	@ResponseBody
	@PostMapping(INEXACT_SYN_MEANING_RELATION_URI)
	@PreAuthorize("@permEval.isUserRoleSelected(authentication)")
	public Response createInexactSynMeaningAndRelation(InexactSynMeaningRequest requestData) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		String roleDatasetCode = getRoleDatasetCode();
		try {
			Response response = nearSynService.saveInexactSynMeaningAndRelation(requestData, roleDatasetCode);
			return response;
		} catch (Exception e) {
			logger.error("Failed to create inexact syn meaning and relation: ", e);

			Response response = new Response();
			response.setStatus(ResponseStatus.ERROR);
			String message = messageSource.getMessage("inexactsyn.meaning.and.relation.create.fail", new Object[0], locale);
			response.setMessage(message);
			return response;
		}
	}

}
