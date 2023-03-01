package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.WORD;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionDataset;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Word;

@Component
public class TermMeaningDbService {

	@Autowired
	private DSLContext create;

	public TermMeaning getTermMeaning(Long meaningId, String datasetCode) {

		Meaning m = MEANING.as("m");
		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<JSON> df = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("definitionId").value(d.ID),
										DSL.key("value").value(d.VALUE),
										DSL.key("lang").value(d.LANG),
										DSL.key("definitionTypeCode").value(d.DEFINITION_TYPE_CODE)))
						.orderBy(d.ORDER_BY))
				.from(d)
				.where(
						d.MEANING_ID.eq(m.ID)
								.andExists(DSL
										.select(dd.DEFINITION_ID)
										.from(dd)
										.where(
												dd.DEFINITION_ID.eq(d.ID)
														.and(dd.DATASET_CODE.eq(datasetCode)))))
				.asField();

		Field<JSON> wf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(w.ID),
										DSL.key("value").value(w.VALUE),
										DSL.key("lang").value(w.LANG)))
						.orderBy(l.ORDER_BY))
				.from(w, l)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(l.WORD_ID.eq(w.ID))
								.and(l.DATASET_CODE.eq(datasetCode)))
				.asField();

		return create
				.select(
						m.ID.as("meaning_id"),
						DSL.val(datasetCode).as("dataset_code"),
						df.as("definitions"),
						wf.as("words"))
				.from(m)
				.where(m.ID.eq(meaningId))
				.fetchOptionalInto(TermMeaning.class)
				.orElse(null);
	}
}
