package eki.wordweb.web.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.Classifier;
import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.CollocMember;
import eki.wordweb.data.CorpusSource;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.Form;
import eki.wordweb.data.Government;
import eki.wordweb.data.Grammar;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Paradigm;
import eki.wordweb.service.CommonDataService;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.web.bean.SessionBean;

@Component
public class ViewUtil implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private WebUtil webUtil;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LanguageContext languageContext;

	private Pattern ekilexMarkupPattern;

	public ViewUtil() {
		ekilexMarkupPattern = Pattern.compile("<[/]?eki-[^>]*>");
	}

	public LanguageData getLangData(String langIso3) {

		LanguageData langData;
		if (StringUtils.isBlank(langIso3)) {
			langData = new LanguageData();
			langData.setCode(langIso3, "-", "-");
			return langData;
		}
		Map<String, LanguageData> langDataMap = commonDataService.getLangDataMap();
		langData = langDataMap.get(langIso3);
		if (langData == null) {
			langData = new LanguageData();
			langData.setCode(langIso3, "-", "-");
			return langData;
		}
		if (StringUtils.equals(langIso3, lANGUAGE_CODE_MUL)) {
			langData.setImageName(LANGUAGE_MUL_IMAGE_PATH);
		}
		return langData;
	}

	public LanguageData getLangData(String langIso3, List<String> selectedLangs) {
		// Let the template know that the lang code should not be shown if only one lang is selected
		if (selectedLangs != null && selectedLangs.size() == 1 && Objects.equals(selectedLangs.get(0), langIso3)) {
			return null;
		}
		return getLangData(langIso3);
	}

	public String getTooltipHtml(DisplayColloc displayColloc) {

		List<CollocMemberGroup> memberGroupOrder = displayColloc.getMemberGroupOrder();
		List<CollocMember> collocMembers;
		StringBuilder htmlBuf = new StringBuilder();
		htmlBuf.append("<span style='white-space:nowrap;'>");
		for (CollocMemberGroup collocMemGr : memberGroupOrder) {
			if (CollocMemberGroup.HEADWORD.equals(collocMemGr)) {
				CollocMember collocMember = displayColloc.getHeadwordMember();
				String conjunct = collocMember.getConjunct();
				if (StringUtils.isNotBlank(conjunct) && collocMember.isPreConjunct()) {
					htmlBuf.append(conjunct);
					htmlBuf.append("&nbsp;");
				}
				htmlBuf.append("<span class='text-green'>");
				htmlBuf.append(collocMember.getFormValue());
				htmlBuf.append("</span>");
				if (StringUtils.isNotBlank(conjunct) && collocMember.isPostConjunct()) {
					htmlBuf.append("&nbsp;");
					htmlBuf.append(conjunct);
				}
			} else if (CollocMemberGroup.PRIMARY.equals(collocMemGr)) {
				collocMembers = displayColloc.getPrimaryMembers();
				int collocMemberCount = collocMembers.size();
				int collocMemberIndex = 0;
				for (CollocMember collocMember : collocMembers) {
					String conjunct = collocMember.getConjunct();
					if (StringUtils.isNotBlank(conjunct) && collocMember.isPreConjunct()) {
						htmlBuf.append(conjunct);
						htmlBuf.append("&nbsp;");
					}
					htmlBuf.append(collocMember.getFormValue());
					if (StringUtils.isNotBlank(conjunct) && collocMember.isPostConjunct()) {
						htmlBuf.append("&nbsp;");
						htmlBuf.append(conjunct);
					}
					if (collocMemberIndex < collocMemberCount - 1) {
						htmlBuf.append("&nbsp;");
					}
					collocMemberIndex++;
				}
			} else if (CollocMemberGroup.CONTEXT.equals(collocMemGr)) {
				collocMembers = displayColloc.getContextMembers();
				int collocMemberCount = collocMembers.size();
				int collocMemberIndex = 0;
				for (CollocMember collocMember : collocMembers) {
					htmlBuf.append("<i>");
					htmlBuf.append(collocMember.getFormValue());
					if (collocMemberIndex < collocMemberCount - 1) {
						htmlBuf.append(", ");
					}
					htmlBuf.append("</i>");
					collocMemberIndex++;
				}
			}
			htmlBuf.append("&nbsp;");
		}
		htmlBuf.append("</span>");
		return htmlBuf.toString();
	}

	public String getWordDataTooltipHtml(LexemeWord lexemeWord) {

		StringBuilder htmlBuf = new StringBuilder();
		if (CollectionUtils.isNotEmpty(lexemeWord.getPoses())) {
			for (Classifier pos : lexemeWord.getPoses()) {
				htmlBuf.append("<div>");
				htmlBuf.append(pos.getValue());
				htmlBuf.append("</div>");
			}
		}
		if (lexemeWord.getGender() != null) {
			htmlBuf.append("<div>");
			htmlBuf.append(lexemeWord.getGender().getValue());
			htmlBuf.append("</div>");
		}
		List<Grammar> grammars = lexemeWord.getGrammars();
		if (CollectionUtils.isNotEmpty(grammars)) {
			String grammarValues = grammars.stream()
					.map(Grammar::getValue)
					.collect(Collectors.joining(", "));
			htmlBuf.append("<div>");
			htmlBuf.append(grammarValues);
			htmlBuf.append("</div>");
		}
		return htmlBuf.toString();
	}

	public String getGovernmentTooltipText(Government government, List<String> lexemePosCodes) {

		String tooltipText = "";

		boolean isVerb = CollectionUtils.isNotEmpty(lexemePosCodes) && lexemePosCodes.contains(LEXEME_POS_CODE_VERB);
		if (!isVerb) {
			return tooltipText;
		}

		Locale locale = languageContext.getDisplayLocale();
		String governmentValue = government.getValue();
		String[] messageArgs = {governmentValue};

		if (ArrayUtils.contains(GOVERNMENT_VALUES_MULTIPLE_CASE, governmentValue)) {
			tooltipText = messageSource.getMessage("label.government.multiple.case", messageArgs, locale);
		} else if (ArrayUtils.contains(GOVERNMENT_VALUES_PARTITIVE_CASE, governmentValue)) {
			tooltipText = messageSource.getMessage("label.government.partitive.case", messageArgs, locale);
		}
		return tooltipText;
	}

	public String getCorpusSourceTooltipHtml(CorpusSource corpusSource) {

		StringBuilder buf = new StringBuilder();
		buf.append(corpusSource.getDisplayName());
		String sentenceTitle = corpusSource.getSentenceTitle();
		if (StringUtils.isNotBlank(sentenceTitle)) {
			buf.append("<br />");
			buf.append(sentenceTitle);
		}
		String sentenceUrl = corpusSource.getSentenceUrl();
		if (StringUtils.isNotBlank(sentenceUrl)) {
			buf.append("<br />");
			buf.append("<a href='");
			buf.append(sentenceUrl);
			buf.append("' target='_blank'>");
			buf.append(sentenceUrl);
			buf.append("</a>");
		}
		return buf.toString();
	}

	public List<Form> getForms(Paradigm paradigm, String morphCode) {
		if (paradigm == null) {
			return Collections.emptyList();
		}
		Map<String, List<Form>> formMorphCodeMap = paradigm.getFormMorphCodeMap();
		List<Form> forms = formMorphCodeMap.get(morphCode);
		if (CollectionUtils.isEmpty(forms)) {
			return Collections.emptyList();
		}
		return forms;
	}

	public String wrapDecorations(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		String[] tokens = StringUtils.split(value, ' ');
		tokens = Arrays.stream(tokens).map(token -> {
			boolean tokenContainsEkilexMarkup = ekilexMarkupPattern.matcher(token).find();
			if (tokenContainsEkilexMarkup) {
				return "<span class='text-nowrap'>" + token + "</span>";
			}
			return token;
		}).toArray(String[]::new);
		value = StringUtils.join(tokens, ' ');
		return value;
	}

	public String getSearchUri(SessionBean sessionBean, String searchMode, String wordValue, Integer homonymNr, String lang) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			List<String> datasetCodes = sessionBean.getDatasetCodes();
			String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
			return webUtil.composeAndEncodeDetailSearchUri(destinLangsStr, datasetCodesStr, wordValue, homonymNr, lang);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			return webUtil.composeAndEncodeSimpleSearchUri(destinLangsStr, wordValue, homonymNr, lang);
		}
		return null;
	}

	public String getSearchUri(SessionBean sessionBean, String searchMode, String wordValue) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			List<String> datasetCodes = sessionBean.getDatasetCodes();
			String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
			return webUtil.composeAndEncodeDetailSearchUri(destinLangsStr, datasetCodesStr, wordValue, null, null);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			return webUtil.composeAndEncodeSimpleSearchUri(destinLangsStr, wordValue, null, null);
		}
		return null;
	}

	public String getDetailSearchUri(String wordValue) {
		String uri = webUtil.composeAndEncodeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, wordValue, null, null);
		return uri;
	}

	public String getDetailSearchUri(String wordValue, Integer homonymNr) {
		String uri = webUtil.composeAndEncodeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, wordValue, homonymNr, LANGUAGE_CODE_EST);
		return uri;
	}

	public String getDetailSearchUri(String wordValue, String datasetCode) {
		String uri = webUtil.composeAndEncodeDetailSearchUri(DESTIN_LANG_ALL, datasetCode, wordValue, null, null);
		return uri;
	}

	public String getSimpleSearchUri(String wordValue) {
		String uri = webUtil.composeAndEncodeSimpleSearchUri(DESTIN_LANG_ALL, wordValue, null, null);
		return uri;
	}

	public String getOsSearchUri(String wordValue, Integer homonymNr) {
		String uri = webUtil.composeOsSearchUri(wordValue, homonymNr);
		return uri;
	}

	public String getDatasetFirstLetterSearchUri(String datasetCode, Character firstLetter) {
		String uri = webUtil.composeDatasetFirstLetterSearchUri(datasetCode, firstLetter);
		return uri;
	}

	public String getIateSearchUrl(String wordValue, String langIso3) {
		Map<String, LanguageData> langDataMap = commonDataService.getLangDataMap();
		LanguageData langData = langDataMap.get(langIso3);
		String langIso2;
		if (langData == null) {
			langIso2 = LANGUAGE_CODE_EST;
		} else {
			langIso2 = langData.getCodeIso2();
			if (StringUtils.length(langIso2) != 2) {
				langIso2 = LANGUAGE_CODE_EST;
			}
		}
		return webUtil.composeIateSearchUrl(wordValue, langIso2);
	}

	public String getEkiOldskoolRusDictUrl(String wordValue) {
		return webUtil.composeEkiOldskoolRusDictUrl(wordValue);
	}

	public String getSkellSearchUrl(String wordValue, String langIso3) {
		String langIso2 = StringUtils.left(langIso3, 2);
		return webUtil.composeSkellSearchUrl(wordValue, langIso2);
	}

	public boolean enumEquals(Enum<?> enum1, Enum<?> enum2) {
		if (enum1 == null) {
			return false;
		}
		if (enum2 == null) {
			return false;
		}
		return StringUtils.equals(enum1.name(), enum2.name());
	}

}
