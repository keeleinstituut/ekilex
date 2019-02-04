package eki.wordweb.web.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.web.bean.SessionBean;

@Component
public class ViewUtil implements WebConstant {

	public String getTooltipHtml(DisplayColloc colloc) {

		List<CollocMemberGroup> memberGroupOrder = colloc.getMemberGroupOrder();
		List<TypeCollocMember> collocMembers;
		StringBuffer htmlBuf = new StringBuffer();
		htmlBuf.append("<span style='white-space:nowrap;'>");
		for (CollocMemberGroup collocMemGr : memberGroupOrder) {
			if (CollocMemberGroup.HEADWORD.equals(collocMemGr)) {
				TypeCollocMember collocMember = colloc.getHeadwordMember();
				String conjunct = collocMember.getConjunct();
				if (StringUtils.isNotBlank(conjunct) && collocMember.isPreConjunct()) {
					htmlBuf.append(conjunct);
					htmlBuf.append("&nbsp;");
				}
				htmlBuf.append("<span class='text-info'>");
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
		String uri = SEARCH_URI + "/" + sourceLang + "-" + destinLang + "/" + searchMode + "/" + word + "/" + homonymNr;
		return uri;
	}

	public String getSearchUri(SessionBean sessionBean, String word) {
		String sourceLang = sessionBean.getSourceLang();
		String destinLang = sessionBean.getDestinLang();
		String searchMode = sessionBean.getSearchMode();
		String uri = SEARCH_URI + "/" + sourceLang + "-" + destinLang + "/" + searchMode + "/" + word;
		return uri;
	}

	public String getSearchUriForDestinationLanguage(SessionBean sessionBean, String word) {
		String sourceLang = sessionBean.getDestinLang();
		String destinLang = sessionBean.getSourceLang();
		String searchMode = sessionBean.getSearchMode();
		String uri = SEARCH_URI + "/" + sourceLang + "-" + destinLang + "/" + searchMode + "/" + word;
		return uri;
	}
}
