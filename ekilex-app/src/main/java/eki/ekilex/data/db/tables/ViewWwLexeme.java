/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ViewWwLexemeRecord;
import eki.ekilex.data.db.udt.records.TypeFreeformRecord;
import eki.ekilex.data.db.udt.records.TypeLangComplexityRecord;
import eki.ekilex.data.db.udt.records.TypeMeaningWordRecord;
import eki.ekilex.data.db.udt.records.TypeUsageRecord;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwLexeme extends TableImpl<ViewWwLexemeRecord> {

    private static final long serialVersionUID = -232186284;

    /**
     * The reference instance of <code>public.view_ww_lexeme</code>
     */
    public static final ViewWwLexeme VIEW_WW_LEXEME = new ViewWwLexeme();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwLexemeRecord> getRecordType() {
        return ViewWwLexemeRecord.class;
    }

    /**
     * The column <code>public.view_ww_lexeme.lexeme_id</code>.
     */
    public final TableField<ViewWwLexemeRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme.word_id</code>.
     */
    public final TableField<ViewWwLexemeRecord, Long> WORD_ID = createField(DSL.name("word_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme.meaning_id</code>.
     */
    public final TableField<ViewWwLexemeRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme.dataset_code</code>.
     */
    public final TableField<ViewWwLexemeRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), org.jooq.impl.SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.view_ww_lexeme.dataset_type</code>.
     */
    public final TableField<ViewWwLexemeRecord, String> DATASET_TYPE = createField(DSL.name("dataset_type"), org.jooq.impl.SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.view_ww_lexeme.dataset_name</code>.
     */
    public final TableField<ViewWwLexemeRecord, String> DATASET_NAME = createField(DSL.name("dataset_name"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_lexeme.value_state_code</code>.
     */
    public final TableField<ViewWwLexemeRecord, String> VALUE_STATE_CODE = createField(DSL.name("value_state_code"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_lexeme.proficiency_level_code</code>.
     */
    public final TableField<ViewWwLexemeRecord, String> PROFICIENCY_LEVEL_CODE = createField(DSL.name("proficiency_level_code"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_lexeme.reliability</code>.
     */
    public final TableField<ViewWwLexemeRecord, Integer> RELIABILITY = createField(DSL.name("reliability"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.view_ww_lexeme.level1</code>.
     */
    public final TableField<ViewWwLexemeRecord, Integer> LEVEL1 = createField(DSL.name("level1"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.view_ww_lexeme.level2</code>.
     */
    public final TableField<ViewWwLexemeRecord, Integer> LEVEL2 = createField(DSL.name("level2"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.view_ww_lexeme.weight</code>.
     */
    public final TableField<ViewWwLexemeRecord, BigDecimal> WEIGHT = createField(DSL.name("weight"), org.jooq.impl.SQLDataType.NUMERIC(5, 4), this, "");

    /**
     * The column <code>public.view_ww_lexeme.complexity</code>.
     */
    public final TableField<ViewWwLexemeRecord, String> COMPLEXITY = createField(DSL.name("complexity"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_lexeme.dataset_order_by</code>.
     */
    public final TableField<ViewWwLexemeRecord, Long> DATASET_ORDER_BY = createField(DSL.name("dataset_order_by"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme.lexeme_order_by</code>.
     */
    public final TableField<ViewWwLexemeRecord, Long> LEXEME_ORDER_BY = createField(DSL.name("lexeme_order_by"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme.value_state_order_by</code>.
     */
    public final TableField<ViewWwLexemeRecord, Long> VALUE_STATE_ORDER_BY = createField(DSL.name("value_state_order_by"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme.lang_complexities</code>.
     */
    public final TableField<ViewWwLexemeRecord, TypeLangComplexityRecord[]> LANG_COMPLEXITIES = createField(DSL.name("lang_complexities"), eki.ekilex.data.db.udt.TypeLangComplexity.TYPE_LANG_COMPLEXITY.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.register_codes</code>.
     */
    public final TableField<ViewWwLexemeRecord, String[]> REGISTER_CODES = createField(DSL.name("register_codes"), org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.pos_codes</code>.
     */
    public final TableField<ViewWwLexemeRecord, String[]> POS_CODES = createField(DSL.name("pos_codes"), org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.region_codes</code>.
     */
    public final TableField<ViewWwLexemeRecord, String[]> REGION_CODES = createField(DSL.name("region_codes"), org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.deriv_codes</code>.
     */
    public final TableField<ViewWwLexemeRecord, String[]> DERIV_CODES = createField(DSL.name("deriv_codes"), org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.meaning_words</code>.
     */
    public final TableField<ViewWwLexemeRecord, TypeMeaningWordRecord[]> MEANING_WORDS = createField(DSL.name("meaning_words"), eki.ekilex.data.db.udt.TypeMeaningWord.TYPE_MEANING_WORD.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.advice_notes</code>.
     */
    public final TableField<ViewWwLexemeRecord, String[]> ADVICE_NOTES = createField(DSL.name("advice_notes"), org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.notes</code>.
     */
    public final TableField<ViewWwLexemeRecord, TypeFreeformRecord[]> NOTES = createField(DSL.name("notes"), eki.ekilex.data.db.udt.TypeFreeform.TYPE_FREEFORM.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.grammars</code>.
     */
    public final TableField<ViewWwLexemeRecord, TypeFreeformRecord[]> GRAMMARS = createField(DSL.name("grammars"), eki.ekilex.data.db.udt.TypeFreeform.TYPE_FREEFORM.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.governments</code>.
     */
    public final TableField<ViewWwLexemeRecord, TypeFreeformRecord[]> GOVERNMENTS = createField(DSL.name("governments"), eki.ekilex.data.db.udt.TypeFreeform.TYPE_FREEFORM.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_lexeme.usages</code>.
     */
    public final TableField<ViewWwLexemeRecord, TypeUsageRecord[]> USAGES = createField(DSL.name("usages"), eki.ekilex.data.db.udt.TypeUsage.TYPE_USAGE.getDataType().getArrayDataType(), this, "");

    /**
     * Create a <code>public.view_ww_lexeme</code> table reference
     */
    public ViewWwLexeme() {
        this(DSL.name("view_ww_lexeme"), null);
    }

    /**
     * Create an aliased <code>public.view_ww_lexeme</code> table reference
     */
    public ViewWwLexeme(String alias) {
        this(DSL.name(alias), VIEW_WW_LEXEME);
    }

    /**
     * Create an aliased <code>public.view_ww_lexeme</code> table reference
     */
    public ViewWwLexeme(Name alias) {
        this(alias, VIEW_WW_LEXEME);
    }

    private ViewWwLexeme(Name alias, Table<ViewWwLexemeRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwLexeme(Name alias, Table<ViewWwLexemeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_lexeme\" as  SELECT l.id AS lexeme_id,\n    l.word_id,\n    l.meaning_id,\n    l.dataset_code,\n    ds.type AS dataset_type,\n    ds.name AS dataset_name,\n    l.value_state_code,\n    l.proficiency_level_code,\n    l.reliability,\n    l.level1,\n    l.level2,\n    l.weight,\n    l.complexity,\n    ds.order_by AS dataset_order_by,\n    l.order_by AS lexeme_order_by,\n    l_vs.order_by AS value_state_order_by,\n    l_lc.lang_complexities,\n    l_reg.register_codes,\n    l_pos.pos_codes,\n    l_rgn.region_codes,\n    l_der.deriv_codes,\n    mw.meaning_words,\n    anote.advice_notes,\n    pnote.notes,\n    gramm.grammars,\n    gov.governments,\n    usg.usages\n   FROM (((((((((((((lexeme l\n     JOIN dataset ds ON (((ds.code)::text = (l.dataset_code)::text)))\n     LEFT JOIN value_state l_vs ON (((l_vs.code)::text = (l.value_state_code)::text)))\n     LEFT JOIN ( SELECT l_reg_1.lexeme_id,\n            array_agg(l_reg_1.register_code ORDER BY l_reg_1.order_by) AS register_codes\n           FROM lexeme_register l_reg_1\n          GROUP BY l_reg_1.lexeme_id) l_reg ON ((l_reg.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT l_pos_1.lexeme_id,\n            array_agg(l_pos_1.pos_code ORDER BY l_pos_1.order_by) AS pos_codes\n           FROM lexeme_pos l_pos_1\n          GROUP BY l_pos_1.lexeme_id) l_pos ON ((l_pos.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT l_rgn_1.lexeme_id,\n            array_agg(l_rgn_1.region_code ORDER BY l_rgn_1.order_by) AS region_codes\n           FROM lexeme_region l_rgn_1\n          GROUP BY l_rgn_1.lexeme_id) l_rgn ON ((l_rgn.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT l_der_1.lexeme_id,\n            array_agg(l_der_1.deriv_code) AS deriv_codes\n           FROM lexeme_deriv l_der_1\n          GROUP BY l_der_1.lexeme_id) l_der ON ((l_der.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT lf.lexeme_id,\n            array_agg(ff.value_text ORDER BY ff.order_by) AS advice_notes\n           FROM lexeme_freeform lf,\n            freeform ff\n          WHERE ((lf.freeform_id = ff.id) AND ((ff.type)::text = 'ADVICE_NOTE'::text))\n          GROUP BY lf.lexeme_id) anote ON ((anote.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT lf.lexeme_id,\n            array_agg(ROW(ff.id, ff.type, (' '::text || ff.value_prese), ff.lang, ff.complexity, NULL::text, NULL::timestamp without time zone, NULL::text, NULL::timestamp without time zone)::type_freeform ORDER BY ff.order_by) AS notes\n           FROM lexeme_freeform lf,\n            freeform ff\n          WHERE ((lf.freeform_id = ff.id) AND ((ff.type)::text = 'NOTE'::text) AND (ff.is_public = true))\n          GROUP BY lf.lexeme_id) pnote ON ((pnote.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT lf.lexeme_id,\n            array_agg(ROW(ff.id, ff.type, (' '::text || ff.value_prese), ff.lang, ff.complexity, NULL::text, NULL::timestamp without time zone, NULL::text, NULL::timestamp without time zone)::type_freeform ORDER BY ff.order_by) AS grammars\n           FROM lexeme_freeform lf,\n            freeform ff\n          WHERE ((lf.freeform_id = ff.id) AND ((ff.type)::text = 'GRAMMAR'::text))\n          GROUP BY lf.lexeme_id) gramm ON ((gramm.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT lf.lexeme_id,\n            array_agg(ROW(ff.id, ff.type, (' '::text || ff.value_prese), ff.lang, ff.complexity, NULL::text, NULL::timestamp without time zone, NULL::text, NULL::timestamp without time zone)::type_freeform ORDER BY ff.order_by) AS governments\n           FROM lexeme_freeform lf,\n            freeform ff\n          WHERE ((lf.freeform_id = ff.id) AND ((ff.type)::text = 'GOVERNMENT'::text))\n          GROUP BY lf.lexeme_id) gov ON ((gov.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT mw_1.lexeme_id,\n            array_agg(ROW(mw_1.lexeme_id, mw_1.meaning_id, mw_1.mw_lex_id, mw_1.mw_lex_complexity, mw_1.mw_lex_weight, mw_1.mw_lex_governments, (mw_1.mw_lex_register_codes)::character varying(100)[], mw_1.mw_lex_value_state_code, mw_1.mw_word_id, (' '::text || mw_1.mw_word), (' '::text || mw_1.mw_word_prese), mw_1.mw_homonym_nr, mw_1.mw_lang, mw_1.mw_aspect_code, (mw_1.mw_word_type_codes)::character varying(100)[])::type_meaning_word ORDER BY mw_1.direct_match_lex_rel_cnt DESC, mw_1.hw_lex_reliability, mw_1.hw_lex_level1, mw_1.hw_lex_level2, mw_1.hw_lex_order_by, mw_1.mw_lex_order_by) AS meaning_words\n           FROM ( SELECT DISTINCT l1.word_id,\n                    l1.id AS lexeme_id,\n                    l1.meaning_id,\n                    l1.level2 AS hw_lex_reliability,\n                    l1.level1 AS hw_lex_level1,\n                    l1.level2 AS hw_lex_level2,\n                    l1.order_by AS hw_lex_order_by,\n                    l2.id AS mw_lex_id,\n                    l2.complexity AS mw_lex_complexity,\n                    l2.weight AS mw_lex_weight,\n                    ( SELECT array_agg(ROW(ff.id, ff.type, replace(ff.value_text, ' '::text, '`'::text), ff.lang, ff.complexity, NULL::text, NULL::timestamp without time zone, NULL::text, NULL::timestamp without time zone)::type_freeform ORDER BY ff.order_by) AS array_agg\n                           FROM lexeme_freeform lf,\n                            freeform ff\n                          WHERE ((lf.lexeme_id = l2.id) AND (lf.freeform_id = ff.id) AND ((ff.type)::text = 'GOVERNMENT'::text))\n                          GROUP BY lf.lexeme_id) AS mw_lex_governments,\n                    ( SELECT array_agg(l_reg_1.register_code ORDER BY l_reg_1.order_by) AS array_agg\n                           FROM lexeme_register l_reg_1\n                          WHERE (l_reg_1.lexeme_id = l2.id)\n                          GROUP BY l_reg_1.lexeme_id) AS mw_lex_register_codes,\n                    l2.value_state_code AS mw_lex_value_state_code,\n                    w2.id AS mw_word_id,\n                    w2.value AS mw_word,\n                    w2.value_prese AS mw_word_prese,\n                    w2.homonym_nr AS mw_homonym_nr,\n                    w2.lang AS mw_lang,\n                    ( SELECT array_agg(wt.word_type_code ORDER BY wt.order_by) AS array_agg\n                           FROM word_word_type wt\n                          WHERE ((wt.word_id = w2.id) AND ((wt.word_type_code)::text <> ALL ((ARRAY['vv'::character varying, 'yv'::character varying])::text[])))\n                          GROUP BY wt.word_id) AS mw_word_type_codes,\n                    w2.aspect_code AS mw_aspect_code,\n                    ( SELECT count(lrel.id) AS count\n                           FROM lex_relation lrel\n                          WHERE ((lrel.lexeme1_id = l1.id) AND (lrel.lexeme2_id = l2.id) AND ((lrel.lex_rel_type_code)::text = 'otse'::text))) AS direct_match_lex_rel_cnt,\n                    l2.order_by AS mw_lex_order_by\n                   FROM ((((lexeme l1\n                     JOIN dataset l1ds ON (((l1ds.code)::text = (l1.dataset_code)::text)))\n                     JOIN lexeme l2 ON ((l2.meaning_id = l1.meaning_id)))\n                     JOIN dataset l2ds ON (((l2ds.code)::text = (l2.dataset_code)::text)))\n                     JOIN word w2 ON ((w2.id = l2.word_id)))\n                  WHERE ((l1.is_public = true) AND (l1ds.is_public = true) AND (l2.is_public = true) AND (l2ds.is_public = true) AND ((COALESCE(l2.value_state_code, 'anything'::character varying))::text <> 'vigane'::text))) mw_1\n          GROUP BY mw_1.lexeme_id) mw ON ((mw.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT u.lexeme_id,\n            array_agg(ROW(u.usage_id, (' '::text || u.usage), (' '::text || u.usage_prese), u.usage_lang, u.complexity, u.usage_type_code, u.usage_translations, u.usage_definitions)::type_usage ORDER BY u.order_by) AS usages\n           FROM ( SELECT lf.lexeme_id,\n                    u_1.id AS usage_id,\n                    u_1.value_text AS usage,\n                    u_1.value_prese AS usage_prese,\n                    u_1.lang AS usage_lang,\n                    u_1.complexity,\n                    u_1.order_by,\n                    utp.classif_code AS usage_type_code,\n                    ut.usage_translations,\n                    ud.usage_definitions\n                   FROM ((((lexeme_freeform lf\n                     JOIN freeform u_1 ON (((lf.freeform_id = u_1.id) AND ((u_1.type)::text = 'USAGE'::text) AND (u_1.is_public = true))))\n                     LEFT JOIN freeform utp ON (((utp.parent_id = u_1.id) AND ((utp.type)::text = 'USAGE_TYPE'::text))))\n                     LEFT JOIN ( SELECT ut_1.parent_id AS usage_id,\n                            array_agg(ut_1.value_prese ORDER BY ut_1.order_by) AS usage_translations\n                           FROM freeform ut_1\n                          WHERE (((ut_1.type)::text = 'USAGE_TRANSLATION'::text) AND (ut_1.lang = 'rus'::bpchar))\n                          GROUP BY ut_1.parent_id) ut ON ((ut.usage_id = u_1.id)))\n                     LEFT JOIN ( SELECT ud_1.parent_id AS usage_id,\n                            array_agg(ud_1.value_prese ORDER BY ud_1.order_by) AS usage_definitions\n                           FROM freeform ud_1\n                          WHERE ((ud_1.type)::text = 'USAGE_DEFINITION'::text)\n                          GROUP BY ud_1.parent_id) ud ON ((ud.usage_id = u_1.id)))) u\n          GROUP BY u.lexeme_id) usg ON ((usg.lexeme_id = l.id)))\n     LEFT JOIN ( SELECT lc.id,\n            array_agg(DISTINCT ROW((\n                CASE\n                    WHEN (lc.lang = ANY (ARRAY['est'::bpchar, 'rus'::bpchar, 'eng'::bpchar, 'ukr'::bpchar, 'fra'::bpchar])) THEN lc.lang\n                    ELSE 'other'::bpchar\n                END)::character varying(10), lc.dataset_code, lc.lex_complexity, (rtrim((lc.data_complexity)::text, '12'::text))::character varying(100))::type_lang_complexity) AS lang_complexities\n           FROM ( SELECT l1.id,\n                    w2.lang,\n                    l1.dataset_code,\n                    l1.complexity AS lex_complexity,\n                    l2.complexity AS data_complexity\n                   FROM ((((lexeme l1\n                     JOIN dataset l1ds ON (((l1ds.code)::text = (l1.dataset_code)::text)))\n                     JOIN lexeme l2 ON (((l2.meaning_id = l1.meaning_id) AND ((l2.dataset_code)::text = (l1.dataset_code)::text) AND (l2.word_id <> l1.word_id))))\n                     JOIN dataset l2ds ON (((l2ds.code)::text = (l2.dataset_code)::text)))\n                     JOIN word w2 ON ((w2.id = l2.word_id)))\n                  WHERE ((l1.is_public = true) AND (l1ds.is_public = true) AND (l2.is_public = true) AND (l2ds.is_public = true))\n                UNION ALL\n                 SELECT l_1.id,\n                    COALESCE(ff.lang, w.lang) AS lang,\n                    l_1.dataset_code,\n                    l_1.complexity AS lex_complexity,\n                    ff.complexity AS data_complexity\n                   FROM word w,\n                    lexeme l_1,\n                    lexeme_freeform lff,\n                    freeform ff,\n                    dataset ds_1\n                  WHERE ((l_1.is_public = true) AND ((ds_1.code)::text = (l_1.dataset_code)::text) AND (ds_1.is_public = true) AND (l_1.word_id = w.id) AND (lff.lexeme_id = l_1.id) AND (lff.freeform_id = ff.id) AND ((ff.type)::text = ANY ((ARRAY['USAGE'::character varying, 'GRAMMAR'::character varying, 'GOVERNMENT'::character varying, 'NOTE'::character varying])::text[])))\n                UNION ALL\n                 SELECT l_1.id,\n                    ut.lang,\n                    l_1.dataset_code,\n                    l_1.complexity AS lex_complexity,\n                    u.complexity AS data_complexity\n                   FROM lexeme l_1,\n                    lexeme_freeform lff,\n                    freeform u,\n                    freeform ut,\n                    dataset ds_1\n                  WHERE ((l_1.is_public = true) AND ((ds_1.code)::text = (l_1.dataset_code)::text) AND (ds_1.is_public = true) AND (lff.lexeme_id = l_1.id) AND (lff.freeform_id = u.id) AND ((u.type)::text = 'USAGE'::text) AND (u.is_public = true) AND (ut.parent_id = u.id) AND ((ut.type)::text = 'USAGE_TRANSLATION'::text))\n                UNION ALL\n                 SELECT l_1.id,\n                    d.lang,\n                    l_1.dataset_code,\n                    l_1.complexity AS lex_complexity,\n                    d.complexity AS data_complexity\n                   FROM lexeme l_1,\n                    definition d,\n                    dataset ds_1\n                  WHERE ((l_1.is_public = true) AND (l_1.meaning_id = d.meaning_id) AND (d.is_public = true) AND ((ds_1.code)::text = (l_1.dataset_code)::text) AND (ds_1.is_public = true))\n                UNION ALL\n                 SELECT l1.id,\n                    w2.lang,\n                    l1.dataset_code,\n                    l1.complexity AS lex_complexity,\n                    l2.complexity AS data_complexity\n                   FROM lex_relation r,\n                    lexeme l1,\n                    lexeme l2,\n                    word w2,\n                    dataset l1ds,\n                    dataset l2ds\n                  WHERE ((l1.is_public = true) AND ((l1ds.code)::text = (l1.dataset_code)::text) AND (l1ds.is_public = true) AND (r.lexeme1_id = l1.id) AND (r.lexeme2_id = l2.id) AND ((l2.dataset_code)::text = (l1.dataset_code)::text) AND (l2.is_public = true) AND ((l2ds.code)::text = (l2.dataset_code)::text) AND (l2ds.is_public = true) AND (w2.id = l2.word_id))\n                UNION ALL\n                 SELECT l1.id,\n                    w1.lang,\n                    l1.dataset_code,\n                    l1.complexity AS lex_complexity,\n                    l1.complexity AS data_complexity\n                   FROM lexeme l1,\n                    word w1,\n                    dataset l1ds\n                  WHERE ((l1.is_public = true) AND ((l1ds.code)::text = (l1.dataset_code)::text) AND (l1ds.is_public = true) AND (w1.id = l1.word_id) AND (NOT (EXISTS ( SELECT l2.id\n                           FROM lexeme l2,\n                            dataset l2ds\n                          WHERE ((l2.meaning_id = l1.meaning_id) AND ((l2.dataset_code)::text = (l1.dataset_code)::text) AND (l2.id <> l1.id) AND (l2.is_public = true) AND ((l2ds.code)::text = (l2.dataset_code)::text) AND (l2ds.is_public = true))))) AND (NOT (EXISTS ( SELECT d.id\n                           FROM definition d\n                          WHERE ((d.meaning_id = l1.meaning_id) AND (d.is_public = true))))) AND (NOT (EXISTS ( SELECT ff.id\n                           FROM lexeme_freeform lff,\n                            freeform ff\n                          WHERE ((lff.lexeme_id = l1.id) AND (lff.freeform_id = ff.id) AND ((ff.type)::text = ANY ((ARRAY['USAGE'::character varying, 'GRAMMAR'::character varying, 'GOVERNMENT'::character varying, 'NOTE'::character varying])::text[])))))))) lc\n          GROUP BY lc.id) l_lc ON ((l_lc.id = l.id)))\n  WHERE ((l.is_public = true) AND (ds.is_public = true))\n  ORDER BY l.id;"));
    }

    public <O extends Record> ViewWwLexeme(Table<O> child, ForeignKey<O, ViewWwLexemeRecord> key) {
        super(child, key, VIEW_WW_LEXEME);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwLexeme as(String alias) {
        return new ViewWwLexeme(DSL.name(alias), this);
    }

    @Override
    public ViewWwLexeme as(Name alias) {
        return new ViewWwLexeme(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwLexeme rename(String name) {
        return new ViewWwLexeme(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwLexeme rename(Name name) {
        return new ViewWwLexeme(name, null);
    }
}
