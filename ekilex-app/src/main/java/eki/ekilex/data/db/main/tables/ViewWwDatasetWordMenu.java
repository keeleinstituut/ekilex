/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwDatasetWordMenuRecord;

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
public class ViewWwDatasetWordMenu extends TableImpl<ViewWwDatasetWordMenuRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_dataset_word_menu</code>
     */
    public static final ViewWwDatasetWordMenu VIEW_WW_DATASET_WORD_MENU = new ViewWwDatasetWordMenu();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwDatasetWordMenuRecord> getRecordType() {
        return ViewWwDatasetWordMenuRecord.class;
    }

    /**
     * The column <code>public.view_ww_dataset_word_menu.dataset_code</code>.
     */
    public final TableField<ViewWwDatasetWordMenuRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.view_ww_dataset_word_menu.first_letter</code>.
     */
    public final TableField<ViewWwDatasetWordMenuRecord, String> FIRST_LETTER = createField(DSL.name("first_letter"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_dataset_word_menu.words</code>.
     */
    public final TableField<ViewWwDatasetWordMenuRecord, String[]> WORDS = createField(DSL.name("words"), SQLDataType.CLOB.getArrayDataType(), this, "");

    private ViewWwDatasetWordMenu(Name alias, Table<ViewWwDatasetWordMenuRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwDatasetWordMenu(Name alias, Table<ViewWwDatasetWordMenuRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_dataset_word_menu\" as  SELECT dataset_code,\n    first_letter,\n    array_agg(word ORDER BY word) AS words\n   FROM ( SELECT \"left\"(w_1.value, 1) AS first_letter,\n            w_1.value AS word,\n            l.dataset_code\n           FROM word w_1,\n            lexeme l,\n            dataset ds\n          WHERE ((w_1.value <> ''::text) AND (w_1.is_public = true) AND (w_1.is_word = true) AND (l.word_id = w_1.id) AND (l.is_public = true) AND ((l.dataset_code)::text = (ds.code)::text) AND (ds.is_public = true) AND ((ds.code)::text <> ALL ((ARRAY['ety'::character varying, 'eki'::character varying])::text[])))) w\n  GROUP BY dataset_code, first_letter\n  ORDER BY dataset_code, first_letter;"));
    }

    /**
     * Create an aliased <code>public.view_ww_dataset_word_menu</code> table reference
     */
    public ViewWwDatasetWordMenu(String alias) {
        this(DSL.name(alias), VIEW_WW_DATASET_WORD_MENU);
    }

    /**
     * Create an aliased <code>public.view_ww_dataset_word_menu</code> table reference
     */
    public ViewWwDatasetWordMenu(Name alias) {
        this(alias, VIEW_WW_DATASET_WORD_MENU);
    }

    /**
     * Create a <code>public.view_ww_dataset_word_menu</code> table reference
     */
    public ViewWwDatasetWordMenu() {
        this(DSL.name("view_ww_dataset_word_menu"), null);
    }

    public <O extends Record> ViewWwDatasetWordMenu(Table<O> child, ForeignKey<O, ViewWwDatasetWordMenuRecord> key) {
        super(child, key, VIEW_WW_DATASET_WORD_MENU);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwDatasetWordMenu as(String alias) {
        return new ViewWwDatasetWordMenu(DSL.name(alias), this);
    }

    @Override
    public ViewWwDatasetWordMenu as(Name alias) {
        return new ViewWwDatasetWordMenu(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwDatasetWordMenu rename(String name) {
        return new ViewWwDatasetWordMenu(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwDatasetWordMenu rename(Name name) {
        return new ViewWwDatasetWordMenu(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String[]> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
