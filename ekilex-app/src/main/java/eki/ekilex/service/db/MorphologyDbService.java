package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.PARADIGM;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.Form;
import eki.ekilex.data.api.Paradigm;

@Component
public class MorphologyDbService {

	private DSLContext create;

	public MorphologyDbService(DSLContext context) {
		create = context;
	}

	public Long createParadigm(Paradigm paradigm) {

		return create
				.insertInto(PARADIGM,
						PARADIGM.WORD_ID,
						PARADIGM.EXAMPLE,
						PARADIGM.INFLECTION_TYPE_NR,
						PARADIGM.INFLECTION_TYPE,
						PARADIGM.IS_SECONDARY)
				.values(
						paradigm.getWordId(),
						paradigm.getExample(),
						paradigm.getInflectionTypeNr(),
						paradigm.getInflectionType(),
						paradigm.isSecondary())
				.returning(PARADIGM.ID)
				.fetchOne()
				.getId();

	}

	public Long createForm(Form form) {

		return create
				.insertInto(FORM,
						FORM.PARADIGM_ID,
						FORM.MODE,
						FORM.MORPH_GROUP1,
						FORM.MORPH_GROUP2,
						FORM.MORPH_GROUP3,
						FORM.DISPLAY_LEVEL,
						FORM.MORPH_CODE,
						FORM.MORPH_EXISTS,
						FORM.VALUE,
						FORM.VALUE_PRESE,
						FORM.COMPONENTS,
						FORM.DISPLAY_FORM,
						FORM.VOCAL_FORM,
						FORM.AUDIO_FILE)
				.values(
						form.getParadigmId(),
						form.getMode().name(),
						form.getMorphGroup1(),
						form.getMorphGroup2(),
						form.getMorphGroup3(),
						form.getDisplayLevel(),
						form.getMorphCode(),
						form.getMorphExists(),
						form.getValue(),
						form.getValuePrese(),
						form.getComponents(),
						form.getDisplayForm(),
						form.getVocalForm(),
						form.getAudioFile())
				.returning(FORM.ID)
				.fetchOne()
				.getId();
	}

	public void deleteParadigmsForWords(List<Long> wordIds) {

		create.deleteFrom(PARADIGM).where(PARADIGM.WORD_ID.in(wordIds)).execute();
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
				.selectFrom(FORM)
				.where(FORM.PARADIGM_ID.eq(paradigmId))
				.orderBy(FORM.ORDER_BY)
				.fetchInto(Form.class);
	}
}
