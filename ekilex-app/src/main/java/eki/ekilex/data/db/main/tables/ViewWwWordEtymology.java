/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwWordEtymologyRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row13;
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
public class ViewWwWordEtymology extends TableImpl<ViewWwWordEtymologyRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_word_etymology</code>
     */
    public static final ViewWwWordEtymology VIEW_WW_WORD_ETYMOLOGY = new ViewWwWordEtymology();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwWordEtymologyRecord> getRecordType() {
        return ViewWwWordEtymologyRecord.class;
    }

    /**
     * The column <code>public.view_ww_word_etymology.word_id</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_etym_id</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, Long> WORD_ETYM_ID = createField(DSL.name("word_etym_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_etym_word_id</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, Long> WORD_ETYM_WORD_ID = createField(DSL.name("word_etym_word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, String> WORD = createField(DSL.name("word"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_lang</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, String> WORD_LANG = createField(DSL.name("word_lang"), SQLDataType.CHAR(3), this, "");

    /**
     * The column <code>public.view_ww_word_etymology.meaning_words</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, String[]> MEANING_WORDS = createField(DSL.name("meaning_words"), SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.view_ww_word_etymology.etymology_type_code</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, String> ETYMOLOGY_TYPE_CODE = createField(DSL.name("etymology_type_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.view_ww_word_etymology.etymology_year</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, String> ETYMOLOGY_YEAR = createField(DSL.name("etymology_year"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_etym_comment</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, String> WORD_ETYM_COMMENT = createField(DSL.name("word_etym_comment"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_etym_is_questionable</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, Boolean> WORD_ETYM_IS_QUESTIONABLE = createField(DSL.name("word_etym_is_questionable"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_etym_order_by</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, Long> WORD_ETYM_ORDER_BY = createField(DSL.name("word_etym_order_by"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.word_etym_relations</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, JSONB> WORD_ETYM_RELATIONS = createField(DSL.name("word_etym_relations"), SQLDataType.JSONB, this, "");

    /**
     * The column <code>public.view_ww_word_etymology.source_links</code>.
     */
    public final TableField<ViewWwWordEtymologyRecord, JSONB> SOURCE_LINKS = createField(DSL.name("source_links"), SQLDataType.JSONB, this, "");

    private ViewWwWordEtymology(Name alias, Table<ViewWwWordEtymologyRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwWordEtymology(Name alias, Table<ViewWwWordEtymologyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_word_etymology\" as  WITH RECURSIVE word_etym_recursion(word_id, word_etym_word_id, word_etym_id, related_word_id, related_word_ids) AS (\n        ( SELECT we_1.word_id,\n            we_1.word_id AS word_etym_word_id,\n            we_1.id AS word_etym_id,\n            wer_1.related_word_id,\n            ARRAY[we_1.word_id] AS related_word_ids\n           FROM (word_etymology we_1\n             LEFT JOIN word_etymology_relation wer_1 ON ((wer_1.word_etym_id = we_1.id)))\n          WHERE (EXISTS ( SELECT l.id\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.word_id = we_1.word_id) AND (l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND ((ds.type)::text = 'LEX'::text))))\n          ORDER BY we_1.order_by, wer_1.order_by)\n        UNION ALL\n        ( SELECT rec_1.word_id,\n            we_1.word_id AS word_etym_word_id,\n            we_1.id AS word_etym_id,\n            wer_1.related_word_id,\n            (rec_1.related_word_ids || we_1.word_id) AS related_word_ids\n           FROM ((word_etym_recursion rec_1\n             JOIN word_etymology we_1 ON ((we_1.word_id = rec_1.related_word_id)))\n             LEFT JOIN word_etymology_relation wer_1 ON ((wer_1.word_etym_id = we_1.id)))\n          WHERE (rec_1.related_word_id <> ANY (rec_1.related_word_ids))\n          ORDER BY we_1.order_by, wer_1.order_by)\n        )\n SELECT rec.word_id,\n    rec.word_etym_id,\n    rec.word_etym_word_id,\n    w.word,\n    w.lang AS word_lang,\n    mw2.meaning_words,\n    we.etymology_type_code,\n    we.etymology_year,\n    we.comment_prese AS word_etym_comment,\n    we.is_questionable AS word_etym_is_questionable,\n    we.order_by AS word_etym_order_by,\n    wer.word_etym_relations,\n    wesl.source_links\n   FROM (((((word_etym_recursion rec\n     JOIN word_etymology we ON ((we.id = rec.word_etym_id)))\n     JOIN ( SELECT w_1.id,\n            w_1.lang,\n            w_1.value AS word\n           FROM word w_1\n          WHERE (EXISTS ( SELECT l.id\n                   FROM lexeme l,\n                    dataset ds\n                  WHERE ((l.word_id = w_1.id) AND (l.is_public = true) AND ((ds.code)::text = (l.dataset_code)::text) AND ((ds.type)::text = 'LEX'::text))))\n          GROUP BY w_1.id) w ON ((w.id = rec.word_etym_word_id)))\n     LEFT JOIN ( SELECT wer_1.word_etym_id,\n            wer_1.related_word_id,\n            jsonb_agg(ROW(wer_1.id, wer_1.comment_prese, wer_1.is_questionable, wer_1.is_compound, wer_1.related_word_id)::type_word_etym_relation ORDER BY wer_1.order_by) AS word_etym_relations\n           FROM word_etymology_relation wer_1\n          GROUP BY wer_1.word_etym_id, wer_1.related_word_id) wer ON (((wer.word_etym_id = rec.word_etym_id) AND (wer.related_word_id <> rec.word_id))))\n     LEFT JOIN ( SELECT wesl_1.word_etym_id,\n            jsonb_agg(ROW(wesl_1.id, wesl_1.name, wesl_1.order_by, s.id, s.name, s.value, s.value_prese, s.is_public)::type_source_link ORDER BY wesl_1.word_etym_id, wesl_1.order_by) AS source_links\n           FROM word_etymology_source_link wesl_1,\n            source s\n          WHERE (wesl_1.source_id = s.id)\n          GROUP BY wesl_1.word_etym_id) wesl ON ((wesl.word_etym_id = rec.word_etym_id)))\n     LEFT JOIN ( SELECT l1.word_id,\n            array_agg(w2.value) AS meaning_words\n           FROM lexeme l1,\n            meaning m,\n            lexeme l2,\n            word w2\n          WHERE ((l1.meaning_id = m.id) AND (l2.meaning_id = m.id) AND (l1.word_id <> l2.word_id) AND (l1.is_public = true) AND ((l2.dataset_code)::text = 'ety'::text) AND (l2.is_public = true) AND (l2.word_id = w2.id) AND (w2.lang = 'est'::bpchar))\n          GROUP BY l1.word_id) mw2 ON ((mw2.word_id = rec.word_etym_word_id)))\n  GROUP BY rec.word_id, rec.word_etym_id, rec.word_etym_word_id, mw2.meaning_words, we.id, w.id, w.word, w.lang, wer.word_etym_relations, wesl.source_links\n  ORDER BY rec.word_id, we.order_by;"));
    }

    /**
     * Create an aliased <code>public.view_ww_word_etymology</code> table reference
     */
    public ViewWwWordEtymology(String alias) {
        this(DSL.name(alias), VIEW_WW_WORD_ETYMOLOGY);
    }

    /**
     * Create an aliased <code>public.view_ww_word_etymology</code> table reference
     */
    public ViewWwWordEtymology(Name alias) {
        this(alias, VIEW_WW_WORD_ETYMOLOGY);
    }

    /**
     * Create a <code>public.view_ww_word_etymology</code> table reference
     */
    public ViewWwWordEtymology() {
        this(DSL.name("view_ww_word_etymology"), null);
    }

    public <O extends Record> ViewWwWordEtymology(Table<O> child, ForeignKey<O, ViewWwWordEtymologyRecord> key) {
        super(child, key, VIEW_WW_WORD_ETYMOLOGY);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwWordEtymology as(String alias) {
        return new ViewWwWordEtymology(DSL.name(alias), this);
    }

    @Override
    public ViewWwWordEtymology as(Name alias) {
        return new ViewWwWordEtymology(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwWordEtymology rename(String name) {
        return new ViewWwWordEtymology(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwWordEtymology rename(Name name) {
        return new ViewWwWordEtymology(name, null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, Long, Long, String, String, String[], String, String, String, Boolean, Long, JSONB, JSONB> fieldsRow() {
        return (Row13) super.fieldsRow();
    }
}
