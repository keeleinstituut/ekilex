/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwLexicalDecisionDataRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class ViewWwLexicalDecisionData extends TableImpl<ViewWwLexicalDecisionDataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_lexical_decision_data</code>
     */
    public static final ViewWwLexicalDecisionData VIEW_WW_LEXICAL_DECISION_DATA = new ViewWwLexicalDecisionData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwLexicalDecisionDataRecord> getRecordType() {
        return ViewWwLexicalDecisionDataRecord.class;
    }

    /**
     * The column <code>public.view_ww_lexical_decision_data.word</code>.
     */
    public final TableField<ViewWwLexicalDecisionDataRecord, String> WORD = createField(DSL.name("word"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_lexical_decision_data.lang</code>.
     */
    public final TableField<ViewWwLexicalDecisionDataRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), this, "");

    /**
     * The column <code>public.view_ww_lexical_decision_data.is_word</code>.
     */
    public final TableField<ViewWwLexicalDecisionDataRecord, Boolean> IS_WORD = createField(DSL.name("is_word"), SQLDataType.BOOLEAN, this, "");

    private ViewWwLexicalDecisionData(Name alias, Table<ViewWwLexicalDecisionDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwLexicalDecisionData(Name alias, Table<ViewWwLexicalDecisionDataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_lexical_decision_data\" as  SELECT word,\n    lang,\n    is_word\n   FROM (( SELECT w_1.word,\n            w_1.lang,\n            true AS is_word\n           FROM ( SELECT DISTINCT w_2.value AS word,\n                    w_2.lang\n                   FROM word w_2\n                  WHERE ((EXISTS ( SELECT l.id\n                           FROM lexeme l,\n                            dataset ds\n                          WHERE ((l.word_id = w_2.id) AND ((l.complexity)::text = 'SIMPLE'::text) AND ((l.dataset_code)::text = 'eki'::text) AND (l.is_public = true) AND (l.is_word = true) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true)))) AND (w_2.value !~~ '% %'::text) AND (length(w_2.value) > 2) AND (w_2.is_public = true))) w_1\n          ORDER BY (random()))\n        UNION ALL\n        ( SELECT nw.word,\n            nw.lang,\n            false AS is_word\n           FROM game_nonword nw\n          ORDER BY (random()))) w\n  ORDER BY (random());"));
    }

    /**
     * Create an aliased <code>public.view_ww_lexical_decision_data</code> table reference
     */
    public ViewWwLexicalDecisionData(String alias) {
        this(DSL.name(alias), VIEW_WW_LEXICAL_DECISION_DATA);
    }

    /**
     * Create an aliased <code>public.view_ww_lexical_decision_data</code> table reference
     */
    public ViewWwLexicalDecisionData(Name alias) {
        this(alias, VIEW_WW_LEXICAL_DECISION_DATA);
    }

    /**
     * Create a <code>public.view_ww_lexical_decision_data</code> table reference
     */
    public ViewWwLexicalDecisionData() {
        this(DSL.name("view_ww_lexical_decision_data"), null);
    }

    public <O extends Record> ViewWwLexicalDecisionData(Table<O> child, ForeignKey<O, ViewWwLexicalDecisionDataRecord> key) {
        super(child, key, VIEW_WW_LEXICAL_DECISION_DATA);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwLexicalDecisionData as(String alias) {
        return new ViewWwLexicalDecisionData(DSL.name(alias), this);
    }

    @Override
    public ViewWwLexicalDecisionData as(Name alias) {
        return new ViewWwLexicalDecisionData(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwLexicalDecisionData rename(String name) {
        return new ViewWwLexicalDecisionData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwLexicalDecisionData rename(Name name) {
        return new ViewWwLexicalDecisionData(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, Boolean> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
