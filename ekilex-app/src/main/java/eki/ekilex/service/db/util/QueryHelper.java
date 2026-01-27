package eki.ekilex.service.db.util;

import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.main.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_ATTR;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_COMMENT;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FREQ_CORP;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_POS;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.main.Tables.POS_GROUP_LABEL;
import static eki.ekilex.data.db.main.Tables.POS_LABEL;
import static eki.ekilex.data.db.main.Tables.PROFICIENCY_LEVEL_LABEL;
import static eki.ekilex.data.db.main.Tables.PUBLISHING;
import static eki.ekilex.data.db.main.Tables.REGION;
import static eki.ekilex.data.db.main.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.main.Tables.REL_GROUP_LABEL;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.main.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_FREQ;
import static eki.ekilex.data.db.main.Tables.WORD_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD_SUGGESTION;
import static eki.ekilex.data.db.main.Tables.WORD_TAG;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Param;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FeedbackType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LastActivityType;
import eki.common.constant.PublishingConstant;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.DerivLabel;
import eki.ekilex.data.db.main.tables.DomainLabel;
import eki.ekilex.data.db.main.tables.FeedbackLog;
import eki.ekilex.data.db.main.tables.FeedbackLogAttr;
import eki.ekilex.data.db.main.tables.FeedbackLogComment;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.FreqCorp;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeDeriv;
import eki.ekilex.data.db.main.tables.LexemeNote;
import eki.ekilex.data.db.main.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.main.tables.LexemePos;
import eki.ekilex.data.db.main.tables.LexemeRegion;
import eki.ekilex.data.db.main.tables.LexemeRegister;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.MeaningDomain;
import eki.ekilex.data.db.main.tables.MeaningLastActivityLog;
import eki.ekilex.data.db.main.tables.MorphLabel;
import eki.ekilex.data.db.main.tables.PosGroupLabel;
import eki.ekilex.data.db.main.tables.PosLabel;
import eki.ekilex.data.db.main.tables.ProficiencyLevelLabel;
import eki.ekilex.data.db.main.tables.Publishing;
import eki.ekilex.data.db.main.tables.Region;
import eki.ekilex.data.db.main.tables.RegisterLabel;
import eki.ekilex.data.db.main.tables.RelGroupLabel;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.Usage;
import eki.ekilex.data.db.main.tables.UsageSourceLink;
import eki.ekilex.data.db.main.tables.UsageTranslation;
import eki.ekilex.data.db.main.tables.ValueStateLabel;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordFreq;
import eki.ekilex.data.db.main.tables.WordLastActivityLog;
import eki.ekilex.data.db.main.tables.WordSuggestion;

@Component
public class QueryHelper implements GlobalConstant, PublishingConstant {

	public List<Field<?>> getWordFields(Word w) {

		List<Field<?>> fields = new ArrayList<>();

		FreqCorp fc = FREQ_CORP.as("fc");
		FreqCorp fca = FREQ_CORP.as("fca");
		WordFreq wf = WORD_FREQ.as("wf");
		WordFreq wfa = WORD_FREQ.as("wfa");
		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");

		final Param<String> freqFieldSep = DSL.val(" - ");
		Field<String> wff = DSL
				.select(DSL.concat(fc.NAME, freqFieldSep, wf.RANK, freqFieldSep, wf.VALUE))
				.from(fc, wf)
				.where(
						wf.WORD_ID.eq(w.ID)
								.and(wf.FREQ_CORP_ID.eq(fc.ID))
								.and(fc.ID.eq(DSL
										.select(fca.ID)
										.from(fca, wfa)
										.where(
												wfa.WORD_ID.eq(wf.WORD_ID)
														.and(wfa.FREQ_CORP_ID.eq(fca.ID)))
										.orderBy(fca.CORP_DATE.desc())
										.limit(1))))
				.asField();

		Field<String[]> wtf = getWordTagsField(w.ID);
		Field<String[]> wwtf = getWordTypeCodesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtzf = getWordIsForeignField(w.ID);
		Field<String[]> lxtnf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(DSL.coalesce(lt.TAG_NAME, "!")))
				.from(l.leftOuterJoin(lt).on(lt.LEXEME_ID.eq(l.ID)))
				.where(l.WORD_ID.eq(w.ID))
				.groupBy(w.ID));
		Field<LocalDateTime> wlaeof = getWordLastActivityEventOnField(w.ID);
		Field<String[]> dsf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(l.DATASET_CODE))
				.from(l)
				.where(l.WORD_ID.eq(w.ID)));

		fields.add(w.ID.as("word_id"));
		fields.add(w.VALUE.as("word_value"));
		fields.add(w.VALUE_PRESE.as("word_value_prese"));
		fields.add(w.LANG);
		fields.add(w.HOMONYM_NR);
		fields.add(w.DISPLAY_MORPH_CODE);
		fields.add(w.GENDER_CODE);
		fields.add(w.ASPECT_CODE);
		fields.add(w.VOCAL_FORM);
		fields.add(w.MORPHOPHONO_FORM);
		fields.add(w.MORPH_COMMENT);
		fields.add(w.REG_YEAR);
		fields.add(w.MANUAL_EVENT_ON);
		fields.add(w.IS_PUBLIC.as("is_word_public"));
		fields.add(wtf.as("tags"));
		fields.add(dsf.as("dataset_codes"));
		fields.add(wff.as("word_frequency"));
		fields.add(wwtf.as("word_type_codes"));
		fields.add(wtpf.as("prefixoid"));
		fields.add(wtsf.as("suffixoid"));
		fields.add(wtzf.as("foreign"));
		fields.add(lxtnf.as("lexemes_tag_names"));
		fields.add(wlaeof.as("last_activity_event_on"));

		return fields;
	}

	public Field<String[]> getWordTagsField(Field<Long> wordIdField) {
		Field<String[]> wtf = DSL.field(DSL
				.select(DSL.arrayAgg(WORD_TAG.TAG_NAME))
				.from(WORD_TAG)
				.where(WORD_TAG.WORD_ID.eq(wordIdField))
				.groupBy(wordIdField));
		return wtf;
	}

	public Field<String[]> getWordTypeCodesField(Field<Long> wordIdField) {
		Field<String[]> wwtf = DSL.field(DSL
				.select(DSL.arrayAgg(WORD_WORD_TYPE.WORD_TYPE_CODE))
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordIdField))
				.groupBy(wordIdField));
		return wwtf;
	}

	public Field<Boolean> getWordTypeExists(Field<Long> wordIdField, String wordType) {
		Field<Boolean> wtef = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(wordType)))));
		return wtef;
	}

	public Field<Boolean> getWordIsPrefixoidField(Field<Long> wordIdField) {
		Field<Boolean> wtpf = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_PREFIXOID)))));
		return wtpf;
	}

	public Field<Boolean> getWordIsSuffixoidField(Field<Long> wordIdField) {
		Field<Boolean> wtsf = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_SUFFIXOID)))));
		return wtsf;
	}

	public Field<Boolean> getWordIsForeignField(Field<Long> wordIdField) {
		Field<Boolean> wtz = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODES_FOREIGN)))));
		return wtz;
	}

	public Field<LocalDateTime> getWordLastActivityEventOnField(Field<Long> wordIdField) {
		WordLastActivityLog wlal = WORD_LAST_ACTIVITY_LOG.as("wlal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Field<LocalDateTime> wlaeof = DSL.field(DSL
				.select(al.EVENT_ON)
				.from(wlal, al)
				.where(
						wlal.WORD_ID.eq(wordIdField)
								.and(wlal.ACTIVITY_LOG_ID.eq(al.ID)))
				.limit(1));
		return wlaeof;
	}

	public List<Field<?>> getLexemeFields(Lexeme l, Dataset ds, String classifierLabelLang, String classifierLabelTypeCode) {

		List<Field<?>> fields = new ArrayList<>();

		Field<JSON> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lderf = getLexemeDerivsField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lregf = getLexemeRegistersField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lrgnf = getLexemeRegionsField(l.ID);
		Field<JSON> lvalstf = getLexemeValueStateField(l, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lproflf = getLexemeProficiencyLevelField(l, classifierLabelLang, classifierLabelTypeCode);
		Field<JSON> lslf = getLexemeSourceLinksField(l.ID);
		Field<JSON> uf = getLexemeUsagesField(l.ID);
		Field<JSON> lnf = getLexemeNotesField(l.ID);
		Field<JSON> ltf = getLexemeTagsField(l.ID);
		Field<Boolean> wwupf = getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME, l.ID);
		Field<Boolean> wwlpf = getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_LEXEME, l.ID);
		Field<Boolean> wwopf = getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_LEXEME, l.ID);

		fields.add(l.ID.as("lexeme_id"));
		fields.add(l.WORD_ID);
		fields.add(l.MEANING_ID);
		fields.add(ds.NAME.as("dataset_name"));
		fields.add(l.DATASET_CODE);
		fields.add(l.LEVEL1);
		fields.add(l.LEVEL2);
		fields.add(l.VALUE_STATE_CODE.as("lexeme_value_state_code"));
		fields.add(lvalstf.as("lexeme_value_state"));
		fields.add(l.PROFICIENCY_LEVEL_CODE.as("lexeme_proficiency_level_code"));
		fields.add(lproflf.as("lexeme_proficiency_level"));
		fields.add(l.WEIGHT);
		fields.add(l.RELIABILITY);
		fields.add(l.ORDER_BY);
		fields.add(l.IS_PUBLIC);
		fields.add(l.IS_WORD);
		fields.add(l.IS_COLLOCATION);
		fields.add(lposf.as("pos"));
		fields.add(lderf.as("derivs"));
		fields.add(lregf.as("registers"));
		fields.add(lrgnf.as("regions"));
		fields.add(lslf.as("source_links"));
		fields.add(uf.as("usages"));
		fields.add(lnf.as("notes"));
		fields.add(ltf.as("tags"));
		fields.add(wwupf.as("is_ww_unif"));
		fields.add(wwlpf.as("is_ww_lite"));
		fields.add(wwopf.as("is_ww_os"));

		return fields;
	}

	public Field<JSON> getLexemePosField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		PosLabel pl = POS_LABEL.as("pl");
		LexemePos lp = LEXEME_POS.as("lp");

		Field<JSON> clf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("name").value(ClassifierName.POS.name()),
										DSL.key("code").value(pl.CODE),
										DSL.key("value").value(pl.VALUE))))
				.from(lp, pl)
				.where(
						lp.LEXEME_ID.eq(lexemeIdField)
								.and(pl.CODE.eq(lp.POS_CODE))
								.and(pl.LANG.eq(classifierLabelLang))
								.and(pl.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	public Field<JSON> getLexemeDerivsField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		LexemeDeriv ld = LEXEME_DERIV.as("ld");
		DerivLabel dl = DERIV_LABEL.as("dl");

		Field<JSON> clf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("name").value(ClassifierName.DERIV.name()),
										DSL.key("code").value(dl.CODE),
										DSL.key("value").value(dl.VALUE))))
				.from(ld, dl)
				.where(
						ld.LEXEME_ID.eq(lexemeIdField)
								.and(dl.CODE.eq(ld.DERIV_CODE))
								.and(dl.LANG.eq(classifierLabelLang))
								.and(dl.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	public Field<JSON> getLexemeRegistersField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		LexemeRegister lr = LEXEME_REGISTER.as("lr");
		RegisterLabel rl = REGISTER_LABEL.as("rl");

		Field<JSON> clf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("name").value(ClassifierName.REGISTER.name()),
										DSL.key("code").value(rl.CODE),
										DSL.key("value").value(rl.VALUE))))
				.from(lr, rl)
				.where(
						lr.LEXEME_ID.eq(lexemeIdField)
								.and(rl.CODE.eq(lr.REGISTER_CODE))
								.and(rl.LANG.eq(classifierLabelLang))
								.and(rl.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	public Field<JSON> getLexemeRegionsField(Field<Long> lexemeIdField) {

		LexemeRegion lr = LEXEME_REGION.as("lr");
		Region r = REGION.as("r");

		Field<JSON> clf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("name").value(ClassifierName.REGION.name()),
										DSL.key("code").value(r.CODE),
										DSL.key("value").value(r.CODE))))
				.from(lr, r)
				.where(
						lr.LEXEME_ID.eq(lexemeIdField)
								.and(r.CODE.eq(lr.REGION_CODE)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	public Field<JSON> getLexemeValueStateField(Lexeme l, String classifierLabelLang, String classifierLabelTypeCode) {

		ValueStateLabel vsl = VALUE_STATE_LABEL.as("vsl");

		return DSL
				.select(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.VALUE_STATE.name()),
								DSL.key("code").value(vsl.CODE),
								DSL.key("value").value(vsl.VALUE)))
				.from(vsl)
				.where(
						vsl.CODE.eq(l.VALUE_STATE_CODE)
								.and(vsl.LANG.eq(classifierLabelLang))
								.and(vsl.TYPE.eq(classifierLabelTypeCode)))
				.asField();
	}

	public Field<JSON> getLexemeProficiencyLevelField(Lexeme l, String classifierLabelLang, String classifierLabelTypeCode) {

		ProficiencyLevelLabel pll = PROFICIENCY_LEVEL_LABEL.as("pll");

		return DSL
				.select(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.PROFICIENCY_LEVEL.name()),
								DSL.key("code").value(pll.CODE),
								DSL.key("value").value(pll.VALUE)))
				.from(pll)
				.where(
						pll.CODE.eq(l.PROFICIENCY_LEVEL_CODE)
								.and(pll.LANG.eq(classifierLabelLang))
								.and(pll.TYPE.eq(classifierLabelTypeCode)))
				.asField();
	}

	public Field<JSON> getLexemeSourceLinksField(Field<Long> lexemeIdField) {

		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		Source s = SOURCE.as("s");

		return DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(lsl.ID),
										DSL.key("name").value(lsl.NAME),
										DSL.key("sourceId").value(lsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(lsl.ORDER_BY)))
						.orderBy(lsl.ORDER_BY))
				.from(lsl, s)
				.where(
						lsl.LEXEME_ID.eq(lexemeIdField)
								.and(lsl.SOURCE_ID.eq(s.ID)))
				.asField();
	}

	public Field<JSON> getLexemeUsagesField(Field<Long> lexemeIdField) {

		Usage u = USAGE.as("u");
		UsageTranslation ut = USAGE_TRANSLATION.as("ut");
		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		Source s = SOURCE.as("s");

		Field<Boolean> wwupf = getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_USAGE, u.ID);
		Field<Boolean> wwlpf = getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_USAGE, u.ID);
		Field<Boolean> wwopf = getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_USAGE, u.ID);

		Field<JSON> utf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ut.ID),
										DSL.key("usageId").value(ut.USAGE_ID),
										DSL.key("value").value(ut.VALUE),
										DSL.key("valuePrese").value(ut.VALUE_PRESE),
										DSL.key("lang").value(ut.LANG),
										DSL.key("createdBy").value(ut.CREATED_BY),
										DSL.key("createdOn").value(ut.CREATED_ON),
										DSL.key("modifiedBy").value(ut.MODIFIED_BY),
										DSL.key("modifiedOn").value(ut.MODIFIED_ON),
										DSL.key("orderBy").value(ut.ORDER_BY)))
						.orderBy(ut.ORDER_BY))
				.from(ut)
				.where(ut.USAGE_ID.eq(u.ID))
				.asField();

		Field<JSON> uslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(usl.ID),
										DSL.key("name").value(usl.NAME),
										DSL.key("sourceId").value(usl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(usl.ORDER_BY)))
						.orderBy(usl.ORDER_BY))
				.from(usl, s)
				.where(
						usl.USAGE_ID.eq(u.ID)
								.and(usl.SOURCE_ID.eq(s.ID)))
				.asField();

		return DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(u.ID),
										DSL.key("value").value(u.VALUE),
										DSL.key("valuePrese").value(u.VALUE_PRESE),
										DSL.key("lang").value(u.LANG),
										DSL.key("public").value(u.IS_PUBLIC),
										DSL.key("createdOn").value(u.CREATED_ON),
										DSL.key("createdBy").value(u.CREATED_BY),
										DSL.key("modifiedOn").value(u.MODIFIED_ON),
										DSL.key("modifiedBy").value(u.MODIFIED_BY),
										DSL.key("orderBy").value(u.ORDER_BY),
										DSL.key("wwUnif").value(wwupf),
										DSL.key("wwLite").value(wwlpf),
										DSL.key("wwOs").value(wwopf),
										DSL.key("translations").value(utf),
										DSL.key("sourceLinks").value(uslf)))
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(lexemeIdField))
				.asField();
	}

	public Field<JSON> getSimpleLexemeUsagesField(Field<Long> lexemeId) {

		Usage u = USAGE.as("u");

		Field<JSON> usaf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(u.ID),
										DSL.key("value").value(u.VALUE),
										DSL.key("valuePrese").value(u.VALUE_PRESE),
										DSL.key("lang").value(u.LANG),
										DSL.key("orderBy").value(u.ORDER_BY)))
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(lexemeId))
				.asField();

		return usaf;
	}

	public Field<JSON> getLexemeNotesField(Field<Long> lexemeIdField) {

		LexemeNote ln = LEXEME_NOTE.as("ln");
		LexemeNoteSourceLink lnsl = LEXEME_NOTE_SOURCE_LINK.as("lnsl");
		Source s = SOURCE.as("s");

		Field<Boolean> wwupf = getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME_NOTE, ln.ID);
		Field<Boolean> wwlpf = getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_LEXEME_NOTE, ln.ID);
		Field<Boolean> wwopf = getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_LEXEME_NOTE, ln.ID);

		Field<JSON> lnslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(lnsl.ID),
										DSL.key("name").value(lnsl.NAME),
										DSL.key("sourceId").value(lnsl.SOURCE_ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(lnsl.ORDER_BY)))
						.orderBy(lnsl.ORDER_BY))
				.from(lnsl, s)
				.where(
						lnsl.LEXEME_NOTE_ID.eq(ln.ID)
								.and(lnsl.SOURCE_ID.eq(s.ID)))
				.asField();

		return DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ln.ID),
										DSL.key("lexemeId").value(ln.LEXEME_ID),
										DSL.key("value").value(ln.VALUE),
										DSL.key("valuePrese").value(ln.VALUE_PRESE),
										DSL.key("lang").value(ln.LANG),
										DSL.key("public").value(ln.IS_PUBLIC),
										DSL.key("createdOn").value(ln.CREATED_ON),
										DSL.key("createdBy").value(ln.CREATED_BY),
										DSL.key("modifiedOn").value(ln.MODIFIED_ON),
										DSL.key("modifiedBy").value(ln.MODIFIED_BY),
										DSL.key("orderBy").value(ln.ORDER_BY),
										DSL.key("wwUnif").value(wwupf),
										DSL.key("wwLite").value(wwlpf),
										DSL.key("wwOs").value(wwopf),
										DSL.key("sourceLinks").value(lnslf)))
						.orderBy(ln.ORDER_BY))
				.from(ln)
				.where(ln.LEXEME_ID.eq(lexemeIdField))
				.asField();
	}

	public Field<JSON> getLexemeTagsField(Field<Long> lexemeIdField) {

		LexemeTag lt = LEXEME_TAG.as("lt");

		return DSL
				.select(DSL
						.jsonArrayAgg(lt.TAG_NAME)
						.orderBy(lt.ID))
				.from(lt)
				.where(lt.LEXEME_ID.eq(lexemeIdField))
				.asField();
	}

	public Field<JSON> getCollocationMembersField(Field<Long> collocLexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm2");
		Word mw = WORD.as("mw");
		Word jw = WORD.as("jw");
		Lexeme ml = LEXEME.as("ml");
		Lexeme jl = LEXEME.as("jl");
		Form mf = FORM.as("mf");
		MorphLabel mfl = MORPH_LABEL.as("mfl");
		PosGroupLabel pgl = POS_GROUP_LABEL.as("pgl");
		RelGroupLabel rgl = REL_GROUP_LABEL.as("rgl");

		Field<JSON> memf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(cm.ID),
										DSL.key("datasetCode").value(ml.DATASET_CODE),
										DSL.key("collocLexemeId").value(cm.COLLOC_LEXEME_ID),
										DSL.key("memberLexemeId").value(cm.MEMBER_LEXEME_ID),
										DSL.key("memberMeaningId").value(ml.MEANING_ID),
										DSL.key("memberWordId").value(ml.WORD_ID),
										DSL.key("memberWordValue").value(mw.VALUE),
										DSL.key("homonymNr").value(mw.HOMONYM_NR),
										DSL.key("lang").value(mw.LANG),
										DSL.key("memberFormId").value(cm.MEMBER_FORM_ID),
										DSL.key("memberFormValue").value(mf.VALUE),
										DSL.key("morphCode").value(mf.MORPH_CODE),
										DSL.key("morphValue").value(mfl.VALUE),
										DSL.key("conjunctLexemeId").value(cm.CONJUNCT_LEXEME_ID),
										DSL.key("conjunctValue").value(jw.VALUE),
										DSL.key("posGroupCode").value(cm.POS_GROUP_CODE),
										DSL.key("posGroupValue").value(pgl.VALUE),
										DSL.key("relGroupCode").value(cm.REL_GROUP_CODE),
										DSL.key("relGroupValue").value(rgl.VALUE),
										DSL.key("weight").value(cm.WEIGHT),
										DSL.key("memberOrder").value(cm.MEMBER_ORDER),
										DSL.key("groupOrder").value(cm.GROUP_ORDER)))
						.orderBy(cm.MEMBER_ORDER))
				.from(cm
						.innerJoin(ml).on(ml.ID.eq(cm.MEMBER_LEXEME_ID))
						.innerJoin(mw).on(mw.ID.eq(ml.WORD_ID))
						.innerJoin(mf).on(mf.ID.eq(cm.MEMBER_FORM_ID))
						.leftOuterJoin(mfl).on(
								mfl.CODE.eq(mf.MORPH_CODE)
										.and(mfl.TYPE.eq(classifierLabelTypeCode))
										.and(mfl.LANG.eq(classifierLabelLang)))
						.leftOuterJoin(jl).on(jl.ID.eq(cm.CONJUNCT_LEXEME_ID))
						.leftOuterJoin(jw).on(jw.ID.eq(jl.WORD_ID))
						.leftOuterJoin(pgl).on(
								pgl.CODE.eq(cm.POS_GROUP_CODE)
										.and(pgl.TYPE.eq(classifierLabelTypeCode))
										.and(pgl.LANG.eq(classifierLabelLang)))
						.leftOuterJoin(rgl).on(
								rgl.CODE.eq(cm.REL_GROUP_CODE)
										.and(rgl.TYPE.eq(classifierLabelTypeCode))
										.and(rgl.LANG.eq(classifierLabelLang))))
				.where(cm.COLLOC_LEXEME_ID.eq(collocLexemeIdField))
				.asField();

		return memf;
	}

	public Table<?> getCollocMemberFullJoinTable(CollocationMember cm, Condition where, String classifierLabelLang, String classifierLabelTypeCode) {

		final BigDecimal COLLOC_MEMBER_WEIGHT_LEVEL_1 = new BigDecimal("1.0");
		final BigDecimal COLLOC_MEMBER_WEIGHT_LEVEL_2 = new BigDecimal("0.8");
		final BigDecimal COLLOC_MEMBER_WEIGHT_LEVEL_3 = new BigDecimal("0.5");

		Word mw = WORD.as("mw");
		Word jw = WORD.as("jw");
		Lexeme ml = LEXEME.as("ml");
		Lexeme jl = LEXEME.as("jl");
		Form mf = FORM.as("mf");
		MorphLabel mfl = MORPH_LABEL.as("mfl");
		PosGroupLabel pgrl = POS_GROUP_LABEL.as("pgrl");
		RelGroupLabel rgrl = REL_GROUP_LABEL.as("rgrl");
		Definition md = DEFINITION.as("md");
		DefinitionDataset mdd = DEFINITION_DATASET.as("mdd");

		Field<JSON> mdf = DSL
				.select(DSL
						.arrayAgg(md.VALUE)
						.orderBy(md.ORDER_BY))
				.from(md)
				.where(
						md.MEANING_ID.eq(ml.MEANING_ID)
								.andExists(DSL
										.select(mdd.DEFINITION_ID)
										.from(mdd)
										.where(
												mdd.DEFINITION_ID.eq(md.ID)
														.and(mdd.DATASET_CODE.eq(ml.DATASET_CODE)))))
				.asField();

		Field<Integer> wlf = DSL.field(DSL
				.when(cm.WEIGHT.eq(COLLOC_MEMBER_WEIGHT_LEVEL_1), DSL.value(1))
				.when(cm.WEIGHT.eq(COLLOC_MEMBER_WEIGHT_LEVEL_2), DSL.value(2))
				.when(cm.WEIGHT.eq(COLLOC_MEMBER_WEIGHT_LEVEL_3), DSL.value(3)));

		Table<?> cmst = DSL
				.select(
						cm.ID,
						ml.DATASET_CODE,
						cm.COLLOC_LEXEME_ID,
						cm.MEMBER_LEXEME_ID,
						ml.MEANING_ID.as("member_meaning_id"),
						ml.WORD_ID.as("member_word_id"),
						mw.VALUE.as("member_word_value"),
						mw.HOMONYM_NR,
						mw.LANG,
						cm.MEMBER_FORM_ID,
						mf.VALUE.as("member_form_value"),
						mf.MORPH_CODE,
						mfl.VALUE.as("morph_value"),
						cm.CONJUNCT_LEXEME_ID,
						jw.VALUE.as("conjunct_value"),
						cm.POS_GROUP_CODE,
						pgrl.VALUE.as("pos_group_value"),
						cm.REL_GROUP_CODE,
						rgrl.VALUE.as("rel_group_value"),
						cm.WEIGHT,
						wlf.as("weight_level"),
						cm.MEMBER_ORDER,
						cm.GROUP_ORDER,
						mdf.as("definition_values"))
				.from(cm
						.innerJoin(ml).on(ml.ID.eq(cm.MEMBER_LEXEME_ID))
						.innerJoin(mw).on(mw.ID.eq(ml.WORD_ID))
						.innerJoin(mf).on(mf.ID.eq(cm.MEMBER_FORM_ID))
						.leftOuterJoin(jl).on(jl.ID.eq(cm.CONJUNCT_LEXEME_ID))
						.leftOuterJoin(jw).on(jw.ID.eq(jl.WORD_ID))
						.leftOuterJoin(mfl).on(
								mfl.CODE.eq(mf.MORPH_CODE)
										.and(mfl.TYPE.eq(classifierLabelTypeCode))
										.and(mfl.LANG.eq(classifierLabelLang)))
						.leftOuterJoin(pgrl).on(
								pgrl.CODE.eq(cm.POS_GROUP_CODE)
										.and(pgrl.TYPE.eq(classifierLabelTypeCode))
										.and(pgrl.LANG.eq(classifierLabelLang)))
						.leftOuterJoin(rgrl).on(
								rgrl.CODE.eq(cm.REL_GROUP_CODE)
										.and(rgrl.TYPE.eq(classifierLabelTypeCode))
										.and(rgrl.LANG.eq(classifierLabelLang))))
				.where(where)
				.asTable("cmst");
		return cmst;
	}

	public Field<JSON> getMeaningDomainsField(Field<Long> meaningIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		MeaningDomain md = MEANING_DOMAIN.as("md");
		DomainLabel dl = DOMAIN_LABEL.as("dl");

		Field<JSON> clf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("name").value(ClassifierName.DOMAIN.name()),
										DSL.key("code").value(dl.CODE),
										DSL.key("value").value(dl.VALUE))))
				.from(md, dl)
				.where(
						md.MEANING_ID.eq(meaningIdField)
								.and(dl.ORIGIN.eq(md.DOMAIN_ORIGIN))
								.and(dl.CODE.eq(md.DOMAIN_CODE))
								.and(dl.LANG.eq(classifierLabelLang))
								.and(dl.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(meaningIdField)
				.asField();
		return clf;
	}

	public Field<LocalDateTime> getMeaningLastActivityEventOnField(Field<Long> meaningIdField, LastActivityType lastActivityType) {

		MeaningLastActivityLog mlal = MEANING_LAST_ACTIVITY_LOG.as("mlal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Field<LocalDateTime> wlaeof = DSL.field(DSL
				.select(al.EVENT_ON)
				.from(mlal, al)
				.where(
						mlal.MEANING_ID.eq(meaningIdField)
								.and(mlal.TYPE.eq(lastActivityType.name()))
								.and(mlal.ACTIVITY_LOG_ID.eq(al.ID)))
				.limit(1));
		return wlaeof;
	}

	public Field<Boolean> getPublishingField(String targetName, String entityName, Field<Long> entityId) {

		Publishing p = PUBLISHING.as("p");
		Field<Boolean> pf = DSL.field(DSL
				.exists(DSL
						.select(p.ID)
						.from(p)
						.where(
								p.TARGET_NAME.eq(targetName)
										.and(p.ENTITY_NAME.eq(entityName))
										.and(p.ENTITY_ID.eq(entityId)))));
		return pf;
	}

	public List<Field<?>> getFeedbackLogFields(FeedbackLog fl) {

		FeedbackLogAttr fla = FEEDBACK_LOG_ATTR.as("fla");
		FeedbackLogComment flc = FEEDBACK_LOG_COMMENT.as("flc");
		WordSuggestion ws = WORD_SUGGESTION.as("ws");

		Field<JSON> flcf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(flc.ID),
										DSL.key("feedbackLogId").value(flc.FEEDBACK_LOG_ID),
										DSL.key("createdOn").value(flc.CREATED_ON),
										DSL.key("comment").value(flc.COMMENT),
										DSL.key("userName").value(flc.USER_NAME)))
						.orderBy(flc.CREATED_ON.desc()))
				.from(flc)
				.where(flc.FEEDBACK_LOG_ID.eq(fl.ID))
				.asField();

		Field<JSON> flaf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(fla.ID),
										DSL.key("feedbackLogId").value(fla.FEEDBACK_LOG_ID),
										DSL.key("name").value(fla.NAME),
										DSL.key("value").value(fla.VALUE)))
						.orderBy(fla.ID))
				.from(fla)
				.where(fla.FEEDBACK_LOG_ID.eq(fl.ID))
				.asField();

		Field<JSON> wsf = DSL
				.select(DSL
						.jsonObject(
								DSL.key("id").value(ws.ID),
								DSL.key("feedbackLogId").value(ws.FEEDBACK_LOG_ID),
								DSL.key("created").value(ws.CREATED),
								DSL.key("wordValue").value(ws.WORD_VALUE),
								DSL.key("definitionValue").value(ws.DEFINITION_VALUE),
								DSL.key("usageValue").value(ws.USAGE_VALUE),
								DSL.key("authorName").value(ws.AUTHOR_NAME),
								DSL.key("authorEmail").value(ws.AUTHOR_EMAIL),
								DSL.key("public").value(ws.IS_PUBLIC),
								DSL.key("publicationDate").value(ws.PUBLICATION_DATE)))
				.from(ws)
				.where(
						ws.FEEDBACK_LOG_ID.eq(fl.ID)
								.and(fl.FEEDBACK_TYPE.eq(FeedbackType.WORD_SUGGESTION.name())))
				.limit(1)
				.asField();

		List<Field<?>> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(fl.fields()));
		fields.add(flaf.as("feedback_log_attrs"));
		fields.add(flcf.as("feedback_log_comments"));
		fields.add(wsf.as("word_suggestion"));

		return fields;
	}

	public void replaceNullCollections(eki.ekilex.data.Lexeme pojo) {

		if (pojo.getTags() == null) {
			pojo.setTags(Collections.emptyList());
		}
		if (pojo.getNotes() == null) {
			pojo.setNotes(Collections.emptyList());
		}
		if (pojo.getUsages() == null) {
			pojo.setUsages(Collections.emptyList());
		}
		if (pojo.getSourceLinks() == null) {
			pojo.setSourceLinks(Collections.emptyList());
		}
	}

	public void replaceNullCollections(eki.ekilex.data.Word pojo) {

		if (pojo.getTags() == null) {
			pojo.setTags(Collections.emptyList());
		}
	}
}
