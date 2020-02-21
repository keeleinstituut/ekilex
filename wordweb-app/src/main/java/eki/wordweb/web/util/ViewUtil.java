package eki.wordweb.web.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
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

	public String getSearchUri(SessionBean sessionBean, String word, Integer homonymNr) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
		String searchMode = sessionBean.getSearchMode();
		String uri = webUtil.composeSearchUri(destinLangsStr, datasetCodesStr, searchMode, word, homonymNr);
		return uri;
	}

	public String getSearchUri(SessionBean sessionBean, String word) {
		List<String> destinLangs = sessionBean.getDestinLangs();
		List<String> datasetCodes = sessionBean.getDatasetCodes();
		String destinLangsStr = StringUtils.join(destinLangs, UI_FILTER_VALUES_SEPARATOR);
		String datasetCodesStr = StringUtils.join(datasetCodes, UI_FILTER_VALUES_SEPARATOR);
		String searchMode = sessionBean.getSearchMode();
		String uri = webUtil.composeSearchUri(destinLangsStr, datasetCodesStr, searchMode, word, null);
		return uri;
	}

	public String getDetailSearchUri(String word) {
		String uri = webUtil.composeSearchUri(DESTIN_LANG_ALL, DATASET_ALL, SEARCH_MODE_DETAIL, word, null);
		return uri;
	}
}
