package eki.wordweb.web.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.TextDecoration;
import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.DecoratedWordType;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.service.CommonDataService;
import eki.wordweb.web.bean.SessionBean;

@Component
public class ViewUtil implements WebConstant, SystemConstant {

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private WebUtil webUtil;

	private Map<String, LanguageData> langDataMap = null;

	public LanguageData getLangData(String langIso3) {

		if (StringUtils.isBlank(langIso3)) {
			return new LanguageData(langIso3, "-", "-");
		}
		if (langDataMap == null) {
			langDataMap = commonDataService.getLangDataMap();
		}
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

	public String getSearchUri(SessionBean sessionBean, String searchMode, String word, Integer homonymNr) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			List<String> datasetCodes = sessionBean.getDatasetCodes();
			String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
			return webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, word, homonymNr);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			return webUtil.composeSimpleSearchUri(destinLangsStr, word, homonymNr);
		}
		return null;
	}

	public String getSearchUri(SessionBean sessionBean, String searchMode, String word) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			List<String> datasetCodes = sessionBean.getDatasetCodes();
			String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
			return webUtil.composeDetailSearchUri(destinLangsStr, datasetCodesStr, word, null);
		} else if (StringUtils.equals(SEARCH_MODE_SIMPLE, searchMode)) {
			return webUtil.composeSimpleSearchUri(destinLangsStr, word, null);
		}
		return null;
	}

	public String getDetailSearchUri(String word) {
		String uri = webUtil.composeDetailSearchUri(DESTIN_LANG_ALL, DATASET_ALL, word, null);
		return uri;
	}
}
