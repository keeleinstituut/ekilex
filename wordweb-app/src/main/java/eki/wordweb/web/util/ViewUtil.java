package eki.wordweb.web.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import eki.common.constant.DatasetType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.TextDecoration;
import eki.common.data.Classifier;
import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.DecoratedWordType;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.type.TypeCollocMember;
import eki.wordweb.data.type.TypeFreeform;
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

	public boolean isLexData(LexemeWord lexemeWord) {
		return DatasetType.LEX.equals(lexemeWord.getDatasetType());
	}

	public boolean isAnyTermData(LexemeWord lexemeWord) {
		return DatasetType.TERM.equals(lexemeWord.getDatasetType());
	}

	public boolean isProTermData(LexemeWord lexemeWord) {
		return DatasetType.TERM.equals(lexemeWord.getDatasetType()) && !StringUtils.equals(DATASET_LIMITED, lexemeWord.getDatasetCode());
	}

	public boolean isLimTermData(LexemeWord lexemeWord) {
		return DatasetType.TERM.equals(lexemeWord.getDatasetType()) && StringUtils.equals(DATASET_LIMITED, lexemeWord.getDatasetCode());
	}

	public LanguageData getLangData(String langIso3) {

		if (StringUtils.isBlank(langIso3)) {
			return new LanguageData(langIso3, "-", "-");
		}
		Map<String, LanguageData> langDataMap = commonDataService.getLangDataMap();
		LanguageData langData = langDataMap.get(langIso3);
		if (langData == null) {
			return new LanguageData(langIso3, "?", "?");
		}
		return langData;
	}

	public String getWordValueMarkup(DecoratedWordType word) {

		String wordPrese = new String(word.getWordPrese());
		if (word.isSuffixoid()) {
			wordPrese = "-" + wordPrese;
		} else if (word.isPrefixoid()) {
			wordPrese = wordPrese + "-";
		}
		StringBuilder htmlBuf = new StringBuilder();
		htmlBuf.append("<span>");
		String foreignMarkupCode = TextDecoration.FOREIGN.getCode();
		if (word.isForeignWord() && !StringUtils.contains(wordPrese, foreignMarkupCode)) {
			htmlBuf.append('<');
			htmlBuf.append(foreignMarkupCode);
			htmlBuf.append('>');
			htmlBuf.append(wordPrese);
			htmlBuf.append("</");
			htmlBuf.append(foreignMarkupCode);
			htmlBuf.append('>');
		} else {
			htmlBuf.append(wordPrese);
		}
		htmlBuf.append("</span>");
		return htmlBuf.toString();
	}

	public String getTooltipHtml(DisplayColloc colloc) {

		List<CollocMemberGroup> memberGroupOrder = colloc.getMemberGroupOrder();
		List<TypeCollocMember> collocMembers;
		StringBuilder htmlBuf = new StringBuilder();
		htmlBuf.append("<span style='white-space:nowrap;'>");
		for (CollocMemberGroup collocMemGr : memberGroupOrder) {
			if (CollocMemberGroup.HEADWORD.equals(collocMemGr)) {
				TypeCollocMember collocMember = colloc.getHeadwordMember();
				String conjunct = collocMember.getConjunct();
				if (StringUtils.isNotBlank(conjunct) && collocMember.isPreConjunct()) {
					htmlBuf.append(conjunct);
					htmlBuf.append("&nbsp;");
				}
				htmlBuf.append("<span class='text-green'>");
				htmlBuf.append(collocMember.getForm());
				htmlBuf.append("</span>");
				if (StringUtils.isNotBlank(conjunct) && collocMember.isPostConjunct()) {
					htmlBuf.append("&nbsp;");
					htmlBuf.append(conjunct);
				}
			} else if (CollocMemberGroup.PRIMARY.equals(collocMemGr)) {
				collocMembers = colloc.getPrimaryMembers();
				int collocMemberCount = collocMembers.size();
				int collocMemberIndex = 0;
				for (TypeCollocMember collocMember : collocMembers) {
					String conjunct = collocMember.getConjunct();
					if (StringUtils.isNotBlank(conjunct) && collocMember.isPreConjunct()) {
						htmlBuf.append(conjunct);
						htmlBuf.append("&nbsp;");
					}
					htmlBuf.append(collocMember.getForm());
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
				collocMembers = colloc.getContextMembers();
				int collocMemberCount = collocMembers.size();
				int collocMemberIndex = 0;
				for (TypeCollocMember collocMember : collocMembers) {
					htmlBuf.append("<i>");
					htmlBuf.append(collocMember.getForm());
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

	public String getPosesAndGenderTooltipHtml(List<Classifier> poses, Classifier gender) {

		StringBuilder htmlBuf = new StringBuilder();
		if (CollectionUtils.isNotEmpty(poses)) {
			for (Classifier pos : poses) {
				htmlBuf.append("<div>");
				htmlBuf.append(pos.getValue());
				htmlBuf.append("</div>");
			}
		}
		if (gender != null) {
			htmlBuf.append("<div>");
			htmlBuf.append(gender.getValue());
			htmlBuf.append("</div>");
		}
		return htmlBuf.toString();
	}

	public String getGovernmentTooltipText(TypeFreeform government, List<String> lexemePosCodes) {

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

	public String getSearchUri(SessionBean sessionBean, String searchMode, String word, Integer homonymNr, String lang) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			List<String> datasetCodes = sessionBean.getDatasetCodes();
			String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
			return webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, word, homonymNr, lang);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			return webUtil.composeSimpleSearchUri(destinLangsStr, word, homonymNr, lang);
		}
		return null;
	}

	public String getSearchUri(SessionBean sessionBean, String searchMode, String word) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			List<String> datasetCodes = sessionBean.getDatasetCodes();
			String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
			return webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, word, null, null);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			return webUtil.composeSimpleSearchUri(destinLangsStr, word, null, null);
		}
		return null;
	}

	public String getDetailSearchUri(String word) {
		String uri = webUtil.composeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, word, null, null);
		return uri;
	}

	public String getSimpleSearchUri(String word) {
		String uri = webUtil.composeSimpleSearchUri(DESTIN_LANG_ALL, word, null, null);
		return uri;
	}

	public String getDetailSearchUri(String word, String datasetCode) {
		String uri = webUtil.composeDetailSearchUri(DESTIN_LANG_ALL, datasetCode, word, null, null);
		return uri;
	}

	public String getDatasetFirstLetterSearchUri(String datasetCode, Character firstLetter) {
		String uri = webUtil.composeDatasetFirstLetterSearchUri(datasetCode, firstLetter);
		return uri;
	}

	public String getEkilexLimTermMeaningDetailsUrl(Long meaningId) {
		return webUtil.composeEkilexLimTermDetailsUrl(meaningId);
	}

	public String getRusCorpWordUrl(String word) {
		return webUtil.composeRusCorpWordUrl(word);
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
