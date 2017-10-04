package eki.ekilex.service.db;

import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.MorphLabel;
import eki.ekilex.data.db.tables.Paradigm;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.conf.RenderNameStyle;
import org.jooq.tools.StopWatchListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;

@Service
public class SearchDbService implements InitializingBean {

	private static final String SELECT_WORDS_OF_MEANING = "sql/select_words_of_meaning.sql";

	private static final int MAX_RESULTS_LIMIT = 50;

	private DSLContext create;

	private String selectWordsOfMeaning;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SELECT_WORDS_OF_MEANING);
		selectWordsOfMeaning = getContent(resourceFileInputStream);
	}

	@Autowired
	public SearchDbService(DSLContext context) {
		create = context;
	}

	public Result<Record4<Long, String, Integer, String>> findWords(String wordWithMetaCharacters) {

		String theFilter = wordWithMetaCharacters.toLowerCase().replace("*", "%").replace("?", "_");
		return create
				.select(FORM.ID, FORM.VALUE, WORD.HOMONYM_NR, WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.lower().likeIgnoreCase(theFilter)
						.and(FORM.IS_WORD.isTrue())
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.orderBy(FORM.VALUE, WORD.HOMONYM_NR)
				.limit(MAX_RESULTS_LIMIT)
				.fetch();
	}

	public Record4<Long, String, Integer, String> getWord(Long id) {

		return create
				.select(FORM.ID, FORM.VALUE, WORD.HOMONYM_NR, WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.ID.eq(id)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.fetchOne();
	}

	public Result<Record6<Long,String,String,String,String,String>> findConnectedForms(Long formId) {

		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");
		Paradigm p = PARADIGM.as("p");
		MorphLabel m = MORPH_LABEL.as("m");
		return create
				.select(f2.ID, f2.VALUE, f2.DISPLAY_FORM, f2.VOCAL_FORM, f2.MORPH_CODE, m.VALUE.as("morph_value"))
				.from(f1 , f2, p, m)
				.where(f1.ID.eq(formId)
						.and(f1.PARADIGM_ID.eq(p.ID))
						.and(f2.PARADIGM_ID.eq(p.ID))
						.and(m.CODE.eq(f2.MORPH_CODE))
						.and(m.LANG.eq("est"))
						.and(m.TYPE.eq("descrip")))
				.fetch();
	}

	public Result<Record> findFormMeanings(Long formId) {
		return create.fetch(selectWordsOfMeaning, formId);
	}

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	private String getContent(InputStream resourceInputStream) throws Exception {
		String content = IOUtils.toString(resourceInputStream, StandardCharsets.UTF_8.name());
		resourceInputStream.close();
		return content;
	}
}
