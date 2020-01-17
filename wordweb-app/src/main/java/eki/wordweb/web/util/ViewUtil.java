package eki.wordweb.web.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.service.CommonDataService;
import eki.wordweb.web.bean.SessionBean;

@Component
public class ViewUtil implements WebConstant, SystemConstant {

	@Autowired
	private CommonDataService commonDataService;

	private Map<String, String> languageIso2Map = null;

	public String getLangIso2(String langIso3) {
		if (StringUtils.isBlank(langIso3)) {
			return "-";
		}
		if (languageIso2Map == null) {
			languageIso2Map = commonDataService.getLangIso2Map();
		}
		String langIso2 = languageIso2Map.get(langIso3);
		if (StringUtils.isBlank(langIso2)) {
			return "?";
		}
		return langIso2;
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
		
		String sourceLang = sessionBean.getSourceLang();
		String destinLang = sessionBean.getDestinLang();
		String searchMode = sessionBean.getSearchMode();
		String uri = composeSearchUri(sessionBean, word, sourceLang, destinLang, homonymNr, searchMode);
		return uri;
	}

	public String getSearchUri(SessionBean sessionBean, String word) {
		String sourceLang = sessionBean.getSourceLang();
		String destinLang = sessionBean.getDestinLang();
		String searchMode = sessionBean.getSearchMode();
		String uri = composeSearchUri(sessionBean, word, sourceLang, destinLang, null, searchMode);
		return uri;
	}

	public String getSearchUriForDestinationLanguage(SessionBean sessionBean, String word, Integer homonymNr) {
		String sourceLang = sessionBean.getDestinLang();
		String destinLang = sessionBean.getSourceLang();
		String searchMode = sessionBean.getSearchMode();
		String uri = composeSearchUri(sessionBean, word, sourceLang, destinLang, homonymNr, searchMode);
		return uri;
	}

	private String composeSearchUri(SessionBean sessionBean, String word, String sourceLang, String destinLang, Integer homonymNr, String searchMode) {

		String encodedWord = UriUtils.encode(word, SystemConstant.UTF_8);
		String searchUri = SEARCH_URI + UNIF_URI + "/" + sourceLang + LANGUAGE_PAIR_SEPARATOR + destinLang + "/" + searchMode + "/" + encodedWord;
		if (homonymNr != null) {
			searchUri += "/" + homonymNr;
		}
		return searchUri;
	}
}
