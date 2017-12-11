package eki.ekilex.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class MarkdownRenderer implements InitializingBean {

	private Parser parser;

	private HtmlRenderer renderer;

	@Override
	public void afterPropertiesSet() throws Exception {

		parser = Parser.builder().build();
		renderer = HtmlRenderer.builder().escapeHtml(true).build();
	}

	public String toHtml(String markdownText) {

		Node document = parser.parse(markdownText);
		String html = renderer.render(document);
		html = StringUtils.trim(html);
		html = StringUtils.removeStart(html, "<p>");
		html = StringUtils.removeEnd(html, "</p>");

		return html;
	}
}
