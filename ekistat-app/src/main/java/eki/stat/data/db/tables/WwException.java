/*
 * This file is generated by jOOQ.
 */
package eki.stat.data.db.tables;


import eki.stat.data.db.Keys;
import eki.stat.data.db.Public;
import eki.stat.data.db.tables.records.WwExceptionRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WwException extends TableImpl<WwExceptionRecord> {

    private static final long serialVersionUID = -296618000;

    /**
     * The reference instance of <code>public.ww_exception</code>
     */
    public static final WwException WW_EXCEPTION = new WwException();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WwExceptionRecord> getRecordType() {
        return WwExceptionRecord.class;
    }

    /**
     * The column <code>public.ww_exception.id</code>.
     */
    public final TableField<WwExceptionRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('ww_exception_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.ww_exception.exception_name</code>.
     */
    public final TableField<WwExceptionRecord, String> EXCEPTION_NAME = createField(DSL.name("exception_name"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_exception.exception_message</code>.
     */
    public final TableField<WwExceptionRecord, String> EXCEPTION_MESSAGE = createField(DSL.name("exception_message"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_exception.event_on</code>.
     */
    public final TableField<WwExceptionRecord, Timestamp> EVENT_ON = createField(DSL.name("event_on"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("statement_timestamp()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>public.ww_exception</code> table reference
     */
    public WwException() {
        this(DSL.name("ww_exception"), null);
    }

    /**
     * Create an aliased <code>public.ww_exception</code> table reference
     */
    public WwException(String alias) {
        this(DSL.name(alias), WW_EXCEPTION);
    }

    /**
     * Create an aliased <code>public.ww_exception</code> table reference
     */
    public WwException(Name alias) {
        this(alias, WW_EXCEPTION);
    }

    private WwException(Name alias, Table<WwExceptionRecord> aliased) {
        this(alias, aliased, null);
    }

    private WwException(Name alias, Table<WwExceptionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> WwException(Table<O> child, ForeignKey<O, WwExceptionRecord> key) {
        super(child, key, WW_EXCEPTION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WwExceptionRecord, Long> getIdentity() {
        return Keys.IDENTITY_WW_EXCEPTION;
    }

    @Override
    public UniqueKey<WwExceptionRecord> getPrimaryKey() {
        return Keys.WW_EXCEPTION_PKEY;
    }

    @Override
    public List<UniqueKey<WwExceptionRecord>> getKeys() {
        return Arrays.<UniqueKey<WwExceptionRecord>>asList(Keys.WW_EXCEPTION_PKEY);
    }

    @Override
    public WwException as(String alias) {
        return new WwException(DSL.name(alias), this);
    }

    @Override
    public WwException as(Name alias) {
        return new WwException(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WwException rename(String name) {
        return new WwException(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WwException rename(Name name) {
        return new WwException(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, String, Timestamp> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
