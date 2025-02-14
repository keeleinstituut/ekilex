package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.FormWord;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.Word;

@Component
public class MorphologyDbService {

	@Autowired
	private DSLContext mainDb;

	public List<eki.ekilex.data.api.Paradigm> getParadigms(Long wordId) {

		return mainDb
				.selectFrom(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId))
				.orderBy(PARADIGM.ID)
				.fetchInto(eki.ekilex.data.api.Paradigm.class);
	}

	public List<eki.ekilex.data.api.ParadigmForm> getParadigmForms(Long paradigmId) {

		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");

		return mainDb
				.select(
						f.ID.as("form_id"),
						f.VALUE,
						f.VALUE_PRESE,
						f.MORPH_CODE,
						pf.MORPH_GROUP1,
						pf.MORPH_GROUP2,
						pf.MORPH_GROUP3,
						pf.DISPLAY_LEVEL,
						pf.DISPLAY_FORM,
						pf.AUDIO_FILE,
						pf.MORPH_EXISTS,
						pf.IS_QUESTIONABLE,
						pf.ORDER_BY)
				.from(pf, f)
				.where(
						pf.PARADIGM_ID.eq(paradigmId)
								.and(pf.FORM_ID.eq(f.ID)))
				.orderBy(pf.ORDER_BY)
				.fetchInto(eki.ekilex.data.api.ParadigmForm.class);
	}

	public List<FormWord> getFormWords(String formValue) {

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");

		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.LANG,
						w.HOMONYM_NR)
				.from(w)
				.whereExists(DSL
						.select(f.ID)
						.from(p, pf, f)
						.where(
								p.WORD_ID.eq(w.ID)
										.and(pf.PARADIGM_ID.eq(p.ID))
										.and(pf.FORM_ID.eq(f.ID))
										.and(f.VALUE.eq(formValue))))
				.orderBy(w.VALUE, w.LANG, w.HOMONYM_NR)
				.fetchInto(FormWord.class);
	}

	public List<eki.ekilex.data.api.Form> getForms(Long wordId) {

		Paradigm p = PARADIGM.as("p");
		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");

		return mainDb
				.select(
						f.ID,
						f.VALUE,
						f.VALUE_PRESE,
						f.MORPH_CODE)
				.from(f)
				.whereExists(DSL
						.select(p.ID)
						.from(p, pf)
						.where(
								p.WORD_ID.eq(wordId)
										.and(pf.PARADIGM_ID.eq(p.ID))
										.and(pf.FORM_ID.eq(f.ID))))
				.fetchInto(eki.ekilex.data.api.Form.class);
	}

	public boolean isFormInUse(Long formId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.fetchExists(DSL
						.select(cm.ID)
						.from(cm)
						.where(cm.MEMBER_FORM_ID.eq(formId)));
	}

	public Long createParadigm(Long wordId, eki.ekilex.data.api.Paradigm paradigm) {

		return mainDb
				.insertInto(
						PARADIGM,
						PARADIGM.WORD_ID,
						PARADIGM.WORD_CLASS,
						PARADIGM.COMMENT,
						PARADIGM.INFLECTION_TYPE_NR,
						PARADIGM.INFLECTION_TYPE,
						PARADIGM.IS_SECONDARY)
				.values(
						wordId,
						paradigm.getWordClass(),
						paradigm.getComment(),
						paradigm.getInflectionTypeNr(),
						paradigm.getInflectionType(),
						paradigm.isSecondary())
				.returning(PARADIGM.ID)
				.fetchOne()
				.getId();

	}

	public Long createForm(eki.ekilex.data.api.Form form) {

		return mainDb
				.insertInto(
						FORM,
						FORM.VALUE,
						FORM.VALUE_PRESE,
						FORM.MORPH_CODE)
				.values(
						form.getValue(),
						form.getValuePrese(),
						form.getMorphCode())
				.returning(FORM.ID)
				.fetchOne()
				.getId();
	}

	public Long createParadigmForm(Long paradigmId, Long formId, eki.ekilex.data.api.ParadigmForm paradigmForm) {

		return mainDb
				.insertInto(PARADIGM_FORM,
						PARADIGM_FORM.PARADIGM_ID,
						PARADIGM_FORM.FORM_ID,
						PARADIGM_FORM.MORPH_GROUP1,
						PARADIGM_FORM.MORPH_GROUP2,
						PARADIGM_FORM.MORPH_GROUP3,
						PARADIGM_FORM.DISPLAY_LEVEL,
						PARADIGM_FORM.DISPLAY_FORM,
						PARADIGM_FORM.AUDIO_FILE,
						PARADIGM_FORM.MORPH_EXISTS,
						PARADIGM_FORM.IS_QUESTIONABLE)
				.values(
						paradigmId,
						formId,
						paradigmForm.getMorphGroup1(),
						paradigmForm.getMorphGroup2(),
						paradigmForm.getMorphGroup3(),
						paradigmForm.getDisplayLevel(),
						paradigmForm.getDisplayForm(),
						paradigmForm.getAudioFile(),
						paradigmForm.isMorphExists(),
						paradigmForm.isQuestionable())
				.returning(PARADIGM_FORM.ID)
				.fetchOne()
				.getId();
	}

	public void updateForm(Long formId, String valuePrese) {

		mainDb
				.update(FORM)
				.set(FORM.VALUE_PRESE, valuePrese)
				.where(FORM.ID.eq(formId))
				.execute();
	}

	public void deleteForm(Long formId) {

		mainDb
				.deleteFrom(FORM)
				.where(FORM.ID.eq(formId))
				.execute();
	}

	public void deleteParadigmsForWord(Long wordId) {

		mainDb
			.deleteFrom(PARADIGM)
			.where(PARADIGM.WORD_ID.eq(wordId))
			.execute();
	}

}
