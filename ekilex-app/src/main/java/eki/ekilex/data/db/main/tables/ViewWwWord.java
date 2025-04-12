/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwWordRecord;
import eki.ekilex.data.db.main.udt.records.TypeLangComplexityRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwWord extends TableImpl<ViewWwWordRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_word</code>
     */
    public static final ViewWwWord VIEW_WW_WORD = new ViewWwWord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwWordRecord> getRecordType() {
        return ViewWwWordRecord.class;
    }

    /**
     * The column <code>public.view_ww_word.word_id</code>.
     */
    public final TableField<ViewWwWordRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word.word</code>.
     */
    public final TableField<ViewWwWordRecord, String> WORD = createField(DSL.name("word"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word.word_prese</code>.
     */
    public final TableField<ViewWwWordRecord, String> WORD_PRESE = createField(DSL.name("word_prese"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word.as_word</code>.
     */
    public final TableField<ViewWwWordRecord, String> AS_WORD = createField(DSL.name("as_word"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word.lang</code>.
     */
    public final TableField<ViewWwWordRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), this, "");

    /**
     * The column <code>public.view_ww_word.lang_filt</code>.
     */
    public final TableField<ViewWwWordRecord, String> LANG_FILT = createField(DSL.name("lang_filt"), SQLDataType.CHAR, this, "");

    /**
     * The column <code>public.view_ww_word.lang_order_by</code>.
     */
    public final TableField<ViewWwWordRecord, Long> LANG_ORDER_BY = createField(DSL.name("lang_order_by"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word.homonym_nr</code>.
     */
    public final TableField<ViewWwWordRecord, Integer> HOMONYM_NR = createField(DSL.name("homonym_nr"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.view_ww_word.display_morph_code</code>.
     */
    public final TableField<ViewWwWordRecord, String> DISPLAY_MORPH_CODE = createField(DSL.name("display_morph_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_word.gender_code</code>.
     */
    public final TableField<ViewWwWordRecord, String> GENDER_CODE = createField(DSL.name("gender_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_word.aspect_code</code>.
     */
    public final TableField<ViewWwWordRecord, String> ASPECT_CODE = createField(DSL.name("aspect_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_word.vocal_form</code>.
     */
    public final TableField<ViewWwWordRecord, String> VOCAL_FORM = createField(DSL.name("vocal_form"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word.morph_comment</code>.
     */
    public final TableField<ViewWwWordRecord, String> MORPH_COMMENT = createField(DSL.name("morph_comment"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word.manual_event_on</code>.
     */
    public final TableField<ViewWwWordRecord, LocalDateTime> MANUAL_EVENT_ON = createField(DSL.name("manual_event_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.view_ww_word.last_activity_event_on</code>.
     */
    public final TableField<ViewWwWordRecord, LocalDateTime> LAST_ACTIVITY_EVENT_ON = createField(DSL.name("last_activity_event_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.view_ww_word.word_type_codes</code>.
     */
    public final TableField<ViewWwWordRecord, String[]> WORD_TYPE_CODES = createField(DSL.name("word_type_codes"), SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_word.lang_complexities</code>.
     */
    public final TableField<ViewWwWordRecord, TypeLangComplexityRecord[]> LANG_COMPLEXITIES = createField(DSL.name("lang_complexities"), eki.ekilex.data.db.main.udt.TypeLangComplexity.TYPE_LANG_COMPLEXITY.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_word.meaning_words</code>.
     */
    public final TableField<ViewWwWordRecord, JSON> MEANING_WORDS = createField(DSL.name("meaning_words"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_word.definitions</code>.
     */
    public final TableField<ViewWwWordRecord, JSON> DEFINITIONS = createField(DSL.name("definitions"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_word.word_od_recommendation</code>.
     */
    public final TableField<ViewWwWordRecord, JSON> WORD_OD_RECOMMENDATION = createField(DSL.name("word_od_recommendation"), SQLDataType.JSON, this, "");

    /**
     * The column <code>public.view_ww_word.freq_value</code>.
     */
    public final TableField<ViewWwWordRecord, BigDecimal> FREQ_VALUE = createField(DSL.name("freq_value"), SQLDataType.NUMERIC(12, 7), this, "");

    /**
     * The column <code>public.view_ww_word.freq_rank</code>.
     */
    public final TableField<ViewWwWordRecord, Long> FREQ_RANK = createField(DSL.name("freq_rank"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word.forms_exist</code>.
     */
    public final TableField<ViewWwWordRecord, Boolean> FORMS_EXIST = createField(DSL.name("forms_exist"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.view_ww_word.min_ds_order_by</code>.
     */
    public final TableField<ViewWwWordRecord, Long> MIN_DS_ORDER_BY = createField(DSL.name("min_ds_order_by"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word.word_type_order_by</code>.
     */
    public final TableField<ViewWwWordRecord, Long> WORD_TYPE_ORDER_BY = createField(DSL.name("word_type_order_by"), SQLDataType.BIGINT, this, "");

    private ViewWwWord(Name alias, Table<ViewWwWordRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwWord(Name alias, Table<ViewWwWordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_word\" as  SELECT w.word_id,\n    w.word,\n    w.word_prese,\n    w.as_word,\n    w.lang,\n        CASE\n            WHEN (w.lang = ANY (ARRAY['est'::bpchar, 'rus'::bpchar, 'eng'::bpchar, 'ukr'::bpchar, 'fra'::bpchar, 'mul'::bpchar])) THEN w.lang\n            ELSE 'other'::bpchar\n        END AS lang_filt,\n    w.lang_order_by,\n    w.homonym_nr,\n    w.display_morph_code,\n    w.gender_code,\n    w.aspect_code,\n    w.vocal_form,\n    w.morph_comment,\n    w.manual_event_on,\n    w.last_activity_event_on,\n    wt.word_type_codes,\n    lc.lang_complexities,\n    mw.meaning_words,\n    wd.definitions,\n    wor.word_od_recommendation,\n    wf.freq_value,\n    wf.freq_rank,\n    w.forms_exist,\n    w.min_ds_order_by,\n    w.word_type_order_by\n   FROM ((((((( SELECT w_1.id AS word_id,\n            w_1.value AS word,\n            w_1.value_prese AS word_prese,\n            w_1.value_as_word AS as_word,\n            w_1.lang,\n            ( SELECT lc_1.order_by\n                   FROM language lc_1\n                  WHERE (lc_1.code = w_1.lang)\n                 LIMIT 1) AS lang_order_by,\n            w_1.homonym_nr,\n            w_1.display_morph_code,\n            w_1.gender_code,\n            w_1.aspect_code,\n            w_1.vocal_form,\n            w_1.morph_comment,\n            w_1.manual_event_on,\n            ( SELECT al.event_on\n                   FROM word_last_activity_log wlal,\n                    activity_log al\n                  WHERE ((wlal.word_id = w_1.id) AND (wlal.activity_log_id = al.id))) AS last_activity_event_on,\n            ( SELECT (count(f.id) > 0)\n                   FROM paradigm p,\n                    paradigm_form pf,\n                    form f\n                  WHERE ((p.word_id = w_1.id) AND (pf.paradigm_id = p.id) AND (pf.form_id = f.id))) AS forms_exist,\n            ( SELECT min(ds.order_by) AS min\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.word_id = w_1.id) AND (l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true))) AS min_ds_order_by,\n            ( SELECT count(wt_1.id) AS count\n                   FROM word_word_type wt_1\n                  WHERE ((wt_1.word_id = w_1.id) AND ((wt_1.word_type_code)::text = 'pf'::text))) AS word_type_order_by\n           FROM word w_1\n          WHERE ((w_1.is_public = true) AND (EXISTS ( SELECT l.id\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.word_id = w_1.id) AND (l.is_public = true) AND (l.is_word = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true)))))\n          GROUP BY w_1.id) w\n     LEFT JOIN ( SELECT wt_1.word_id,\n            array_agg(wt_1.word_type_code ORDER BY wt_1.order_by) AS word_type_codes\n           FROM word_word_type wt_1\n          WHERE ((wt_1.word_type_code)::text <> ALL ((ARRAY['vv'::character varying, 'yv'::character varying, 'vvar'::character varying])::text[]))\n          GROUP BY wt_1.word_id) wt ON ((wt.word_id = w.word_id)))\n     LEFT JOIN ( SELECT mw_1.word_id,\n            json_agg(ROW(mw_1.lexeme_id, mw_1.meaning_id, mw_1.mw_lex_id, mw_1.mw_lex_complexity, mw_1.mw_lex_weight, NULL::json, NULL::character varying(100)[], NULL::character varying(100), mw_1.mw_word_id, mw_1.mw_word, mw_1.mw_word_prese, mw_1.mw_homonym_nr, mw_1.mw_lang, mw_1.mw_aspect_code, (mw_1.mw_word_type_codes)::character varying(100)[])::type_meaning_word ORDER BY mw_1.hw_lex_level1, mw_1.hw_lex_level2, mw_1.hw_lex_order_by, mw_1.mw_lex_order_by) AS meaning_words\n           FROM ( SELECT DISTINCT l1.word_id,\n                    l1.id AS lexeme_id,\n                    l1.meaning_id,\n                    l1.level1 AS hw_lex_level1,\n                    l1.level2 AS hw_lex_level2,\n                    l1.order_by AS hw_lex_order_by,\n                    l2.id AS mw_lex_id,\n                    l2.complexity AS mw_lex_complexity,\n                    l2.weight AS mw_lex_weight,\n                    w2.id AS mw_word_id,\n                    w2.value AS mw_word,\n                    w2.value_prese AS mw_word_prese,\n                    w2.homonym_nr AS mw_homonym_nr,\n                    w2.lang AS mw_lang,\n                    ( SELECT array_agg(wt_1.word_type_code ORDER BY wt_1.order_by) AS array_agg\n                           FROM word_word_type wt_1\n                          WHERE ((wt_1.word_id = w2.id) AND ((wt_1.word_type_code)::text <> ALL ((ARRAY['vv'::character varying, 'yv'::character varying, 'vvar'::character varying])::text[])))\n                          GROUP BY wt_1.word_id) AS mw_word_type_codes,\n                    w2.aspect_code AS mw_aspect_code,\n                    l2.order_by AS mw_lex_order_by\n                   FROM ((((lexeme l1\n                     JOIN dataset l1ds ON ((((l1ds.code)::text = (l1.dataset_code)::text) AND (l1ds.is_public = true))))\n                     JOIN lexeme l2 ON (((l2.meaning_id = l1.meaning_id) AND (l2.word_id <> l1.word_id) AND (l2.is_public = true) AND (l2.is_word = true) AND ((COALESCE(l2.value_state_code, 'anything'::character varying))::text <> 'vigane'::text))))\n                     JOIN dataset l2ds ON ((((l2ds.code)::text = (l2.dataset_code)::text) AND (l2ds.is_public = true))))\n                     JOIN word w2 ON (((w2.id = l2.word_id) AND (w2.is_public = true))))\n                  WHERE ((l1.is_public = true) AND (l1.is_word = true))) mw_1\n          GROUP BY mw_1.word_id) mw ON ((mw.word_id = w.word_id)))\n     JOIN ( SELECT lc_1.word_id,\n            array_agg(DISTINCT ROW((\n                CASE\n                    WHEN (lc_1.lang = ANY (ARRAY['est'::bpchar, 'rus'::bpchar, 'eng'::bpchar, 'ukr'::bpchar, 'fra'::bpchar, 'mul'::bpchar])) THEN lc_1.lang\n                    ELSE 'other'::bpchar\n                END)::character varying(10), lc_1.dataset_code, lc_1.lex_complexity, (TRIM(TRAILING '12'::text FROM lc_1.data_complexity))::character varying(100))::type_lang_complexity) AS lang_complexities\n           FROM ( SELECT l1.word_id,\n                    w2.lang,\n                    l1.dataset_code,\n                    l1.complexity AS lex_complexity,\n                    l2.complexity AS data_complexity\n                   FROM ((((lexeme l1\n                     JOIN dataset l1ds ON ((((l1ds.code)::text = (l1.dataset_code)::text) AND (l1ds.is_public = true))))\n                     JOIN lexeme l2 ON (((l2.meaning_id = l1.meaning_id) AND ((l2.dataset_code)::text = (l1.dataset_code)::text) AND (l2.word_id <> l1.word_id) AND (l2.is_public = true) AND (l2.is_word = true))))\n                     JOIN dataset l2ds ON ((((l2ds.code)::text = (l2.dataset_code)::text) AND (l2ds.is_public = true))))\n                     JOIN word w2 ON (((w2.id = l2.word_id) AND (w2.is_public = true))))\n                  WHERE ((l1.is_public = true) AND (l1.is_word = true))\n                UNION ALL\n                 SELECT l.word_id,\n                    COALESCE(ff.lang, w_1.lang) AS lang,\n                    l.dataset_code,\n                    l.complexity AS lex_complexity,\n                    ff.complexity AS data_complexity\n                   FROM word w_1,\n                    lexeme l,\n                    lexeme_freeform lff,\n                    freeform ff,\n                    dataset ds\n                  WHERE ((l.is_public = true) AND (l.is_word = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true) AND (l.word_id = w_1.id) AND (w_1.is_public = true) AND (lff.lexeme_id = l.id) AND (lff.freeform_id = ff.id) AND (ff.is_public = true) AND ((ff.freeform_type_code)::text = ANY ((ARRAY['GRAMMAR'::character varying, 'GOVERNMENT'::character varying])::text[])))\n                UNION ALL\n                 SELECT l.word_id,\n                    COALESCE(ln.lang, w_1.lang) AS lang,\n                    l.dataset_code,\n                    l.complexity AS lex_complexity,\n                    ln.complexity AS data_complexity\n                   FROM word w_1,\n                    lexeme l,\n                    lexeme_note ln,\n                    dataset ds\n                  WHERE ((l.is_public = true) AND (l.is_word = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true) AND (l.word_id = w_1.id) AND (w_1.is_public = true) AND (ln.lexeme_id = l.id) AND (ln.is_public = true))\n                UNION ALL\n                 SELECT l.word_id,\n                    u.lang,\n                    l.dataset_code,\n                    l.complexity AS lex_complexity,\n                    u.complexity AS data_complexity\n                   FROM lexeme l,\n                    usage u,\n                    dataset ds\n                  WHERE ((l.is_public = true) AND (l.is_word = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true) AND (u.lexeme_id = l.id) AND (u.is_public = true))\n                UNION ALL\n                 SELECT l.word_id,\n                    ut.lang,\n                    l.dataset_code,\n                    l.complexity AS lex_complexity,\n                    u.complexity AS data_complexity\n                   FROM lexeme l,\n                    usage u,\n                    usage_translation ut,\n                    dataset ds\n                  WHERE ((l.is_public = true) AND (l.is_word = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true) AND (u.lexeme_id = l.id) AND (u.is_public = true) AND (ut.usage_id = u.id))\n                UNION ALL\n                 SELECT l.word_id,\n                    d.lang,\n                    l.dataset_code,\n                    l.complexity AS lex_complexity,\n                    d.complexity AS data_complexity\n                   FROM lexeme l,\n                    definition d,\n                    dataset ds\n                  WHERE ((l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true) AND (l.meaning_id = d.meaning_id) AND (d.is_public = true))\n                UNION ALL\n                 SELECT l1.word_id,\n                    w1.lang,\n                    l1.dataset_code,\n                    l1.complexity AS lex_complexity,\n                    l1.complexity AS data_complexity\n                   FROM lexeme l1,\n                    word w1,\n                    dataset l1ds\n                  WHERE ((l1.is_public = true) AND (l1.is_word = true) AND ((l1ds.code)::text = (l1.dataset_code)::text) AND (l1ds.is_public = true) AND (w1.id = l1.word_id) AND (w1.is_public = true) AND (NOT (EXISTS ( SELECT l2.id\n                           FROM lexeme l2,\n                            dataset l2ds\n                          WHERE ((l2.meaning_id = l1.meaning_id) AND ((l2.dataset_code)::text = (l1.dataset_code)::text) AND (l2.id <> l1.id) AND (l2.is_public = true) AND ((l2ds.code)::text = (l2.dataset_code)::text) AND (l2ds.is_public = true))))) AND (NOT (EXISTS ( SELECT d.id\n                           FROM definition d\n                          WHERE ((d.meaning_id = l1.meaning_id) AND (d.is_public = true))))) AND (NOT (EXISTS ( SELECT ln.id\n                           FROM lexeme_note ln\n                          WHERE ((ln.lexeme_id = l1.id) AND (ln.is_public = true))))) AND (NOT (EXISTS ( SELECT u.id\n                           FROM usage u\n                          WHERE ((u.lexeme_id = l1.id) AND (u.is_public = true))))) AND (NOT (EXISTS ( SELECT ff.id\n                           FROM lexeme_freeform lff,\n                            freeform ff\n                          WHERE ((lff.lexeme_id = l1.id) AND (lff.freeform_id = ff.id) AND ((ff.freeform_type_code)::text = ANY ((ARRAY['GRAMMAR'::character varying, 'GOVERNMENT'::character varying])::text[])))))))) lc_1\n          GROUP BY lc_1.word_id) lc ON ((lc.word_id = w.word_id)))\n     LEFT JOIN ( SELECT wd_1.word_id,\n            json_agg(ROW(wd_1.lexeme_id, wd_1.meaning_id, wd_1.definition_id, wd_1.value, wd_1.value_prese, wd_1.lang, wd_1.complexity, NULL::json, NULL::json)::type_definition ORDER BY wd_1.ds_order_by, wd_1.level1, wd_1.level2, wd_1.lex_order_by, wd_1.def_order_by) AS definitions\n           FROM ( SELECT l.word_id,\n                    l.id AS lexeme_id,\n                    l.meaning_id,\n                    l.level1,\n                    l.level2,\n                    l.order_by AS lex_order_by,\n                    d.id AS definition_id,\n                    \"substring\"(d.value, 1, 200) AS value,\n                    \"substring\"(d.value_prese, 1, 200) AS value_prese,\n                    d.lang,\n                    d.complexity,\n                    d.order_by AS def_order_by,\n                    ds.order_by AS ds_order_by\n                   FROM ((lexeme l\n                     JOIN dataset ds ON (((ds.code)::text = (l.dataset_code)::text)))\n                     JOIN definition d ON (((d.meaning_id = l.meaning_id) AND (d.is_public = true))))\n                  WHERE ((l.is_public = true) AND (ds.is_public = true))) wd_1\n          GROUP BY wd_1.word_id) wd ON ((wd.word_id = w.word_id)))\n     LEFT JOIN ( SELECT wor_1.word_id,\n            json_build_object('id', wor_1.id, 'wordId', wor_1.word_id, 'value', wor_1.value, 'valuePrese', wor_1.value_prese, 'optValue', wor_1.opt_value, 'optValuePrese', wor_1.opt_value_prese, 'createdBy', wor_1.created_by, 'createdOn', wor_1.created_on, 'modifiedBy', wor_1.modified_by, 'modifiedOn', wor_1.modified_on) AS word_od_recommendation\n           FROM word_od_recommendation wor_1) wor ON ((wor.word_id = w.word_id)))\n     LEFT JOIN ( SELECT wf_1.word_id,\n            wf_1.value AS freq_value,\n            wf_1.rank AS freq_rank,\n            fc.corp_date\n           FROM word_freq wf_1,\n            freq_corp fc\n          WHERE ((wf_1.freq_corp_id = fc.id) AND (fc.is_public = true))) wf ON (((wf.word_id = w.word_id) AND (wf.corp_date = ( SELECT max(fcc.corp_date) AS max\n           FROM word_freq wff,\n            freq_corp fcc\n          WHERE ((wff.freq_corp_id = fcc.id) AND (fcc.is_public = true) AND (wff.word_id = w.word_id)))))));"));
    }

    /**
     * Create an aliased <code>public.view_ww_word</code> table reference
     */
    public ViewWwWord(String alias) {
        this(DSL.name(alias), VIEW_WW_WORD);
    }

    /**
     * Create an aliased <code>public.view_ww_word</code> table reference
     */
    public ViewWwWord(Name alias) {
        this(alias, VIEW_WW_WORD);
    }

    /**
     * Create a <code>public.view_ww_word</code> table reference
     */
    public ViewWwWord() {
        this(DSL.name("view_ww_word"), null);
    }

    public <O extends Record> ViewWwWord(Table<O> child, ForeignKey<O, ViewWwWordRecord> key) {
        super(child, key, VIEW_WW_WORD);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwWord as(String alias) {
        return new ViewWwWord(DSL.name(alias), this);
    }

    @Override
    public ViewWwWord as(Name alias) {
        return new ViewWwWord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwWord rename(String name) {
        return new ViewWwWord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwWord rename(Name name) {
        return new ViewWwWord(name, null);
    }
}
