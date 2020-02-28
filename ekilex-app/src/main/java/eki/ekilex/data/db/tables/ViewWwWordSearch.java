/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ViewWwWordSearchRecord;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwWordSearch extends TableImpl<ViewWwWordSearchRecord> {

    private static final long serialVersionUID = -539559561;

    /**
     * The reference instance of <code>public.view_ww_word_search</code>
     */
    public static final ViewWwWordSearch VIEW_WW_WORD_SEARCH = new ViewWwWordSearch();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwWordSearchRecord> getRecordType() {
        return ViewWwWordSearchRecord.class;
    }

    /**
     * The column <code>public.view_ww_word_search.sgroup</code>.
     */
    public final TableField<ViewWwWordSearchRecord, String> SGROUP = createField("sgroup", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_search.word</code>.
     */
    public final TableField<ViewWwWordSearchRecord, String> WORD = createField("word", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_search.crit</code>.
     */
    public final TableField<ViewWwWordSearchRecord, String> CRIT = createField("crit", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_search.unacrit</code>.
     */
    public final TableField<ViewWwWordSearchRecord, String> UNACRIT = createField("unacrit", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.view_ww_word_search.lang_order_by</code>.
     */
    public final TableField<ViewWwWordSearchRecord, Long> LANG_ORDER_BY = createField("lang_order_by", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>public.view_ww_word_search</code> table reference
     */
    public ViewWwWordSearch() {
        this(DSL.name("view_ww_word_search"), null);
    }

    /**
     * Create an aliased <code>public.view_ww_word_search</code> table reference
     */
    public ViewWwWordSearch(String alias) {
        this(DSL.name(alias), VIEW_WW_WORD_SEARCH);
    }

    /**
     * Create an aliased <code>public.view_ww_word_search</code> table reference
     */
    public ViewWwWordSearch(Name alias) {
        this(alias, VIEW_WW_WORD_SEARCH);
    }

    private ViewWwWordSearch(Name alias, Table<ViewWwWordSearchRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwWordSearch(Name alias, Table<ViewWwWordSearchRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> ViewWwWordSearch(Table<O> child, ForeignKey<O, ViewWwWordSearchRecord> key) {
        super(child, key, VIEW_WW_WORD_SEARCH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewWwWordSearch as(String alias) {
        return new ViewWwWordSearch(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewWwWordSearch as(Name alias) {
        return new ViewWwWordSearch(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwWordSearch rename(String name) {
        return new ViewWwWordSearch(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwWordSearch rename(Name name) {
        return new ViewWwWordSearch(name, null);
    }
}
