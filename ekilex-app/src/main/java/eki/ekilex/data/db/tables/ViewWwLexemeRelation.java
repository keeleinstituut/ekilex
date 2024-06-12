/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ViewWwLexemeRelationRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
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
public class ViewWwLexemeRelation extends TableImpl<ViewWwLexemeRelationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_lexeme_relation</code>
     */
    public static final ViewWwLexemeRelation VIEW_WW_LEXEME_RELATION = new ViewWwLexemeRelation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwLexemeRelationRecord> getRecordType() {
        return ViewWwLexemeRelationRecord.class;
    }

    /**
     * The column <code>public.view_ww_lexeme_relation.lexeme_id</code>.
     */
    public final TableField<ViewWwLexemeRelationRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_lexeme_relation.related_lexemes</code>.
     */
    public final TableField<ViewWwLexemeRelationRecord, JSON> RELATED_LEXEMES = createField(DSL.name("related_lexemes"), SQLDataType.JSON, this, "");

    private ViewWwLexemeRelation(Name alias, Table<ViewWwLexemeRelationRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwLexemeRelation(Name alias, Table<ViewWwLexemeRelationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_lexeme_relation\" as  SELECT r.lexeme1_id AS lexeme_id,\n    json_agg(ROW(l2.lexeme_id, w2.word_id, w2.word, w2.word_prese, w2.homonym_nr, w2.lang, (w2.word_type_codes)::character varying(100)[], l2.complexity, r.lex_rel_type_code)::type_lexeme_relation ORDER BY r.order_by) AS related_lexemes\n   FROM ((lex_relation r\n     JOIN ( SELECT l.id AS lexeme_id,\n            l.word_id,\n            l.complexity\n           FROM lexeme l,\n            dataset lds\n          WHERE ((l.is_public = true) AND ((lds.code)::text = (l.dataset_code)::text) AND (lds.is_public = true))) l2 ON ((l2.lexeme_id = r.lexeme2_id)))\n     JOIN ( SELECT w.id AS word_id,\n            w.value AS word,\n            w.value_prese AS word_prese,\n            w.homonym_nr,\n            w.lang,\n            array_agg(wt.word_type_code ORDER BY wt.order_by) AS word_type_codes\n           FROM (word w\n             LEFT JOIN word_word_type wt ON (((wt.word_id = w.id) AND ((wt.word_type_code)::text <> ALL ((ARRAY['vv'::character varying, 'yv'::character varying, 'vvar'::character varying])::text[])))))\n          GROUP BY w.id) w2 ON ((w2.word_id = l2.word_id)))\n  WHERE (EXISTS ( SELECT l1.id\n           FROM lexeme l1,\n            dataset l1ds\n          WHERE ((l1.id = r.lexeme1_id) AND (l1.is_public = true) AND ((l1ds.code)::text = (l1.dataset_code)::text) AND (l1ds.is_public = true))))\n  GROUP BY r.lexeme1_id;"));
    }

    /**
     * Create an aliased <code>public.view_ww_lexeme_relation</code> table reference
     */
    public ViewWwLexemeRelation(String alias) {
        this(DSL.name(alias), VIEW_WW_LEXEME_RELATION);
    }

    /**
     * Create an aliased <code>public.view_ww_lexeme_relation</code> table reference
     */
    public ViewWwLexemeRelation(Name alias) {
        this(alias, VIEW_WW_LEXEME_RELATION);
    }

    /**
     * Create a <code>public.view_ww_lexeme_relation</code> table reference
     */
    public ViewWwLexemeRelation() {
        this(DSL.name("view_ww_lexeme_relation"), null);
    }

    public <O extends Record> ViewWwLexemeRelation(Table<O> child, ForeignKey<O, ViewWwLexemeRelationRecord> key) {
        super(child, key, VIEW_WW_LEXEME_RELATION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwLexemeRelation as(String alias) {
        return new ViewWwLexemeRelation(DSL.name(alias), this);
    }

    @Override
    public ViewWwLexemeRelation as(Name alias) {
        return new ViewWwLexemeRelation(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwLexemeRelation rename(String name) {
        return new ViewWwLexemeRelation(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwLexemeRelation rename(Name name) {
        return new ViewWwLexemeRelation(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, JSON> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
