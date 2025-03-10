/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.EtymologyTypeRecord;

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
public class EtymologyType extends TableImpl<EtymologyTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.etymology_type</code>
     */
    public static final EtymologyType ETYMOLOGY_TYPE = new EtymologyType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EtymologyTypeRecord> getRecordType() {
        return EtymologyTypeRecord.class;
    }

    /**
     * The column <code>public.etymology_type.code</code>.
     */
    public final TableField<EtymologyTypeRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.etymology_type.datasets</code>.
     */
    public final TableField<EtymologyTypeRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.etymology_type.order_by</code>.
     */
    public final TableField<EtymologyTypeRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private EtymologyType(Name alias, Table<EtymologyTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private EtymologyType(Name alias, Table<EtymologyTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.etymology_type</code> table reference
     */
    public EtymologyType(String alias) {
        this(DSL.name(alias), ETYMOLOGY_TYPE);
    }

    /**
     * Create an aliased <code>public.etymology_type</code> table reference
     */
    public EtymologyType(Name alias) {
        this(alias, ETYMOLOGY_TYPE);
    }

    /**
     * Create a <code>public.etymology_type</code> table reference
     */
    public EtymologyType() {
        this(DSL.name("etymology_type"), null);
    }

    public <O extends Record> EtymologyType(Table<O> child, ForeignKey<O, EtymologyTypeRecord> key) {
        super(child, key, ETYMOLOGY_TYPE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<EtymologyTypeRecord, Long> getIdentity() {
        return (Identity<EtymologyTypeRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<EtymologyTypeRecord> getPrimaryKey() {
        return Keys.ETYMOLOGY_TYPE_PKEY;
    }

    @Override
    public List<UniqueKey<EtymologyTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<EtymologyTypeRecord>>asList(Keys.ETYMOLOGY_TYPE_PKEY);
    }

    @Override
    public EtymologyType as(String alias) {
        return new EtymologyType(DSL.name(alias), this);
    }

    @Override
    public EtymologyType as(Name alias) {
        return new EtymologyType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EtymologyType rename(String name) {
        return new EtymologyType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EtymologyType rename(Name name) {
        return new EtymologyType(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
