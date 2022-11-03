/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.DisplayMorphRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DisplayMorph extends TableImpl<DisplayMorphRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.display_morph</code>
     */
    public static final DisplayMorph DISPLAY_MORPH = new DisplayMorph();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DisplayMorphRecord> getRecordType() {
        return DisplayMorphRecord.class;
    }

    /**
     * The column <code>public.display_morph.code</code>.
     */
    public final TableField<DisplayMorphRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.display_morph.datasets</code>.
     */
    public final TableField<DisplayMorphRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.display_morph.order_by</code>.
     */
    public final TableField<DisplayMorphRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private DisplayMorph(Name alias, Table<DisplayMorphRecord> aliased) {
        this(alias, aliased, null);
    }

    private DisplayMorph(Name alias, Table<DisplayMorphRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.display_morph</code> table reference
     */
    public DisplayMorph(String alias) {
        this(DSL.name(alias), DISPLAY_MORPH);
    }

    /**
     * Create an aliased <code>public.display_morph</code> table reference
     */
    public DisplayMorph(Name alias) {
        this(alias, DISPLAY_MORPH);
    }

    /**
     * Create a <code>public.display_morph</code> table reference
     */
    public DisplayMorph() {
        this(DSL.name("display_morph"), null);
    }

    public <O extends Record> DisplayMorph(Table<O> child, ForeignKey<O, DisplayMorphRecord> key) {
        super(child, key, DISPLAY_MORPH);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<DisplayMorphRecord, Long> getIdentity() {
        return (Identity<DisplayMorphRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<DisplayMorphRecord> getPrimaryKey() {
        return Keys.DISPLAY_MORPH_PKEY;
    }

    @Override
    public List<UniqueKey<DisplayMorphRecord>> getKeys() {
        return Arrays.<UniqueKey<DisplayMorphRecord>>asList(Keys.DISPLAY_MORPH_PKEY);
    }

    @Override
    public DisplayMorph as(String alias) {
        return new DisplayMorph(DSL.name(alias), this);
    }

    @Override
    public DisplayMorph as(Name alias) {
        return new DisplayMorph(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DisplayMorph rename(String name) {
        return new DisplayMorph(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DisplayMorph rename(Name name) {
        return new DisplayMorph(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
