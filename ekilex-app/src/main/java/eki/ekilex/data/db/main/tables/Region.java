/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.RegionRecord;

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
public class Region extends TableImpl<RegionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.region</code>
     */
    public static final Region REGION = new Region();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RegionRecord> getRecordType() {
        return RegionRecord.class;
    }

    /**
     * The column <code>public.region.code</code>.
     */
    public final TableField<RegionRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.region.datasets</code>.
     */
    public final TableField<RegionRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.region.order_by</code>.
     */
    public final TableField<RegionRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private Region(Name alias, Table<RegionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Region(Name alias, Table<RegionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.region</code> table reference
     */
    public Region(String alias) {
        this(DSL.name(alias), REGION);
    }

    /**
     * Create an aliased <code>public.region</code> table reference
     */
    public Region(Name alias) {
        this(alias, REGION);
    }

    /**
     * Create a <code>public.region</code> table reference
     */
    public Region() {
        this(DSL.name("region"), null);
    }

    public <O extends Record> Region(Table<O> child, ForeignKey<O, RegionRecord> key) {
        super(child, key, REGION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<RegionRecord, Long> getIdentity() {
        return (Identity<RegionRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<RegionRecord> getPrimaryKey() {
        return Keys.REGION_PKEY;
    }

    @Override
    public List<UniqueKey<RegionRecord>> getKeys() {
        return Arrays.<UniqueKey<RegionRecord>>asList(Keys.REGION_PKEY);
    }

    @Override
    public Region as(String alias) {
        return new Region(DSL.name(alias), this);
    }

    @Override
    public Region as(Name alias) {
        return new Region(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Region rename(String name) {
        return new Region(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Region rename(Name name) {
        return new Region(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}