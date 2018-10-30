package eki.wordweb.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.HtmlEntityDescription;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.web.bean.SessionBean;

@Component
public class ViewUtil implements WebConstant, InitializingBean {

	//TODO this is not viable solution
	@Deprecated
	private List<HtmlEntityDescription> htmlEntityDescriptions;

	@Override
	public void afterPropertiesSet() throws Exception {

		htmlEntityDescriptions = new ArrayList<>();

		final String defaultStyle = "color: red;";
		Pattern entityMatchPattern;
		String preDecoration, postDecoration;
		HtmlEntityDescription htmlEntityDescription;

		entityMatchPattern = Pattern.compile("(&ema;(.+?)&eml;)");
		preDecoration = "<i style='" + defaultStyle + "'>";
		postDecoration = "</i>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&ba;(.+?)&bl;)");
		preDecoration = "<b style='" + defaultStyle + "'>";
		postDecoration = "</b>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&suba;(.+?)&subl;)");
		preDecoration = "<sub style='" + defaultStyle + "'>";
		postDecoration = "</sub>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&supa;(.+?)&supl;)");
		preDecoration = "<sup style='" + defaultStyle + "'>";
		postDecoration = "</sup>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&la;(.+?)&ll;)");
		preDecoration = "<u style='" + defaultStyle + "'>";
		postDecoration = "</u>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&capa;(.+?)&capl;)");
		preDecoration = "<b><i style='" + defaultStyle + "font-variant:small-caps;'>";
		postDecoration = "</i></b>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&(ehk|Hrl|hrl|ja|jne|jt|ka|nt|puudub|v|vm|vms|vrd|vt|напр.|и др.|и т. п.|г.);)");
		preDecoration = "<i style='" + defaultStyle + "'>";
		postDecoration = "</i>";
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);

		entityMatchPattern = Pattern.compile("(&br;)");
		preDecoration = "<br />";
		postDecoration = null;
		htmlEntityDescription = new HtmlEntityDescription(entityMatchPattern, preDecoration, postDecoration);
		htmlEntityDescriptions.add(htmlEntityDescription);
	}

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

	//TODO this is not viable solution
	@Deprecated
	public String getFormattedEntityHtml(String text) {

		if (StringUtils.isBlank(text)) {
			return text;
		}
		String html = new String(text);
		StringBuffer htmlBuf;
		int textLength;
		int textStart;
		int matchStart;
		int matchEnd;
		String cleanText;
		String matchText;
		Pattern pattern;
		Matcher matcher;
		String preDecoration;
		String postDecoration;

		for (HtmlEntityDescription htmlEntityDescription : htmlEntityDescriptions) {
			htmlBuf = new StringBuffer();
			textLength = html.length();
			textStart = 0;
			pattern = htmlEntityDescription.getEntityMatchPattern();
			preDecoration = htmlEntityDescription.getPreDecoration();
			postDecoration = htmlEntityDescription.getPostDecoration();
			matcher = pattern.matcher(html);
			while (matcher.find()) {
				matchStart = matcher.start();
				matchEnd = matcher.end();
				cleanText = StringUtils.substring(html, textStart, matchStart);
				htmlBuf.append(cleanText);
				if (postDecoration == null) {
					htmlBuf.append(preDecoration);
				} else {
					matchText = matcher.group(2);
					htmlBuf.append(preDecoration);
					htmlBuf.append(matchText);
					htmlBuf.append(postDecoration);
				}
				textStart = matchEnd;
			}
			if (textStart < textLength) {
				cleanText = StringUtils.substring(html, textStart, textLength);
				htmlBuf.append(cleanText);
			}
			html = htmlBuf.toString();
		}
		return html;
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
