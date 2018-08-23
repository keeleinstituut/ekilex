package eki.wordweb.web.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Collocation;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.TypeCollocMember;

@Component
public class ViewUtil implements WebConstant {

	public String getTooltipHtml(DisplayColloc colloc) {
		List<String> preContext = colloc.getPreContext();
		List<String> postContext = colloc.getPostContext();
		List<TypeCollocMember> collocMembers = colloc.getCollocMembers();
		int collocMemberCount = collocMembers.size();
		TypeCollocMember collocMember;
		String wrapup;
		StringBuffer htmlBuf = new StringBuffer();
		htmlBuf.append("<span>");
		if (CollectionUtils.isNotEmpty(preContext)) {
			wrapup = StringUtils.join(preContext, ',');
			htmlBuf.append("<i>");
			htmlBuf.append(wrapup);
			htmlBuf.append("</i>");
			htmlBuf.append("&nbsp;");
		}
		htmlBuf.append("<span>");
		for (int collocMemberIndex = 0; collocMemberIndex < collocMemberCount; collocMemberIndex++) {
			collocMember = collocMembers.get(collocMemberIndex);
			String conjunct = collocMember.getConjunct();
			if (StringUtils.isNotBlank(conjunct)) {
				htmlBuf.append(conjunct);
				htmlBuf.append("&nbsp;");
			}
			if (collocMember.isHeadword()) {
				htmlBuf.append("<span class='text-info'>");
				htmlBuf.append(collocMember.getForm());
				htmlBuf.append("</span>");
			} else {
				htmlBuf.append(collocMember.getForm());
			}
			if (collocMemberIndex < collocMemberCount - 1) {
				htmlBuf.append("&nbsp;");
			}
		}
		htmlBuf.append("</span>");
		if (CollectionUtils.isNotEmpty(postContext)) {
			wrapup = StringUtils.join(postContext, ',');
			htmlBuf.append("&nbsp;");
			htmlBuf.append("<i>");
			htmlBuf.append(wrapup);
			htmlBuf.append("</i>");
		}
		htmlBuf.append("</span>");
		return htmlBuf.toString();
	}

	public String getTooltipHtml(Collocation colloc) {
		List<TypeCollocMember> collocMembers = colloc.getCollocMembers();
		int collocMemberCount = collocMembers.size();
		TypeCollocMember collocMember;
		StringBuffer htmlBuf = new StringBuffer();
		htmlBuf.append("<span>");
		for (int collocMemberIndex = 0; collocMemberIndex < collocMemberCount; collocMemberIndex++) {
			collocMember = collocMembers.get(collocMemberIndex);
			String conjunct = collocMember.getConjunct();
			if (StringUtils.isNotBlank(conjunct)) {
				htmlBuf.append(conjunct);
				htmlBuf.append("&nbsp;");
			}
			if (collocMember.isHeadword()) {
				htmlBuf.append("<span class='text-info'>");
				htmlBuf.append(collocMember.getForm());
				htmlBuf.append("</span>");
			} else if (collocMember.isContext()) {
				htmlBuf.append("<i>");
				htmlBuf.append(collocMember.getForm());
				htmlBuf.append("</i>");
			} else {
				htmlBuf.append(collocMember.getForm());
			}
			if (collocMemberIndex < collocMemberCount - 1) {
				htmlBuf.append("&nbsp;");
			}
		}
		htmlBuf.append("</span>");
		return htmlBuf.toString();
	}
}
