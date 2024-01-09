package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.PARADIGM_FORM;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.Form;
import eki.ekilex.data.api.Paradigm;

@Component
public class MorphologyDbService {

	@Autowired
	private DSLContext create;

	public Long createParadigm(Paradigm paradigm) {

		return create
				.insertInto(PARADIGM,
						PARADIGM.WORD_ID,
						PARADIGM.WORD_CLASS,
						PARADIGM.COMMENT,
						PARADIGM.INFLECTION_TYPE_NR,
						PARADIGM.INFLECTION_TYPE,
						PARADIGM.IS_SECONDARY)
				.values(
						paradigm.getWordId(),
						paradigm.getWordClass(),
						paradigm.getComment(),
						paradigm.getInflectionTypeNr(),
						paradigm.getInflectionType(),
						paradigm.isSecondary())
				.returning(PARADIGM.ID)
				.fetchOne()
				.getId();

	}

	public Long createForm(Form form, Long wordId) {

		Long formId = create
				.selectDistinct(FORM.ID)
				.from(FORM, PARADIGM_FORM, PARADIGM)
				.where(
						FORM.DISPLAY_LEVEL.eq(form.getDisplayLevel())
								.and(FORM.MORPH_CODE.eq(form.getMorphCode()))
								.and(FORM.VALUE.eq(form.getValue()))
								.and(FORM.DISPLAY_FORM.eq(form.getDisplayForm()))
								.and(PARADIGM_FORM.FORM_ID.eq(FORM.ID))
								.and(PARADIGM_FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(PARADIGM.WORD_ID.eq(wordId)))
				.fetchOptionalInto(Long.class)
				.orElse(null);

		if (formId == null) {
			formId = create
					.insertInto(FORM,
							FORM.MORPH_GROUP1,
							FORM.MORPH_GROUP2,
							FORM.MORPH_GROUP3,
							FORM.DISPLAY_LEVEL,
							FORM.MORPH_CODE,
							FORM.MORPH_EXISTS,
							FORM.IS_QUESTIONABLE,
							FORM.VALUE,
							FORM.VALUE_PRESE,
							FORM.COMPONENTS,
							FORM.DISPLAY_FORM,
							FORM.AUDIO_FILE)
					.values(
							form.getMorphGroup1(),
							form.getMorphGroup2(),
							form.getMorphGroup3(),
							form.getDisplayLevel(),
							form.getMorphCode(),
							form.isMorphExists(),
							form.isQuestionable(),
							form.getValue(),
							form.getValuePrese(),
							form.getComponents(),
							form.getDisplayForm(),
							form.getAudioFile())
					.returning(FORM.ID)
					.fetchOne()
					.getId();
		}

		create
				.insertInto(PARADIGM_FORM,
						PARADIGM_FORM.FORM_ID,
						PARADIGM_FORM.PARADIGM_ID)
				.values(
						formId, form.getParadigmId())
				.execute();

			return formId;
	}

	public void deleteParadigmsForWord(Long wordId) {

		create.deleteFrom(PARADIGM).where(PARADIGM.WORD_ID.eq(wordId)).execute();
	}

	public int deleteFloatingForms() {

		return create
				.deleteFrom(FORM)
				.whereNotExists(DSL
						.select(PARADIGM_FORM.ID)
						.from(PARADIGM_FORM)
						.where(PARADIGM_FORM.FORM_ID.eq(FORM.ID)))
				.execute();
	}

	public List<Paradigm> getParadigms(Long wordId) {

		return create
				.selectFrom(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(wordId))
				.orderBy(PARADIGM.ID)
				.fetchInto(Paradigm.class);
	}

	public List<Form> getForms(Long paradigmId) {

		return create
				.select(
						FORM.ID,
						PARADIGM_FORM.PARADIGM_ID,
						FORM.MORPH_GROUP1,
						FORM.MORPH_GROUP2,
						FORM.MORPH_GROUP2,
						FORM.MORPH_GROUP3,
						FORM.DISPLAY_LEVEL,
						FORM.MORPH_CODE,
						FORM.MORPH_EXISTS,
						FORM.IS_QUESTIONABLE,
						FORM.VALUE,
						FORM.VALUE_PRESE,
						FORM.COMPONENTS,
						FORM.DISPLAY_FORM,
						FORM.AUDIO_FILE,
						PARADIGM_FORM.ORDER_BY)
				.from(FORM, PARADIGM_FORM)
				.where(FORM.ID.eq(PARADIGM_FORM.FORM_ID).and(PARADIGM_FORM.PARADIGM_ID.eq(paradigmId)))
				.orderBy(PARADIGM_FORM.ORDER_BY)
				.fetchInto(Form.class);
	}
}
