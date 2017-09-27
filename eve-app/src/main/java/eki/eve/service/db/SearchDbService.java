package eki.eve.service.db;

import static eki.eve.data.db.Tables.DATASET;
import static eki.eve.data.db.Tables.FORM;
import static eki.eve.data.db.Tables.MORPH_LABEL;
import static eki.eve.data.db.Tables.PARADIGM;
import static eki.eve.data.db.Tables.WORD;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.conf.RenderNameStyle;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.eve.constant.SystemConstant;
import eki.eve.data.db.tables.Form;
import eki.eve.data.db.tables.MorphLabel;
import eki.eve.data.db.tables.Paradigm;

@Service
public class SearchDbService implements InitializingBean, SystemConstant {

	private static final String SELECT_WORDS_OF_MEANING = "sql/select_words_of_meaning.sql";

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
		create.settings().setRenderSchema(false);
		create.settings().setRenderFormatted(true);
		create.settings().setRenderNameStyle(RenderNameStyle.AS_IS);
	}

	public Result<Record3<Long, String, Integer>> findWords(String searchFilter) {
		String theFilter = searchFilter.toLowerCase().replace("*", "%").replace("?", "_");
		return create
				.select(FORM.ID, FORM.VALUE, WORD.HOMONYM_NR)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.lower().like(theFilter)
						.and(FORM.IS_WORD.isTrue())
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.orderBy(FORM.VALUE, WORD.HOMONYM_NR)
				.fetch();
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

		String sql = new String(selectWordsOfMeaning);
		sql = StringUtils.replace(sql, ":formId", formId.toString());

		return create.fetch(sql);
	}

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	private String getContent(InputStream resourceInputStream) throws Exception {
		String content = IOUtils.toString(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return content;
	}
}
