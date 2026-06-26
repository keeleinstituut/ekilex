package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.TEXT_CONTENT;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.db.main.tables.TextContent;

@Component
public class TextContentDbService {

	@Autowired
	protected DSLContext mainDb;

	public eki.ekilex.data.TextContent getTextContent(String textName, String lang) {

		TextContent tc = TEXT_CONTENT.as("tc");

		Field<String> textNameLower = DSL.lower(textName);

		return mainDb
				.selectFrom(tc)
				.where(tc.NAME.eq(textNameLower)
						.and(tc.LANG.eq(lang)))
				.fetchOptionalInto(eki.ekilex.data.TextContent.class)
				.orElse(null);
	}
}
