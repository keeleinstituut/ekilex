/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ValueStateRecord;

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
public class ValueState extends TableImpl<ValueStateRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.value_state</code>
     */
    public static final ValueState VALUE_STATE = new ValueState();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ValueStateRecord> getRecordType() {
        return ValueStateRecord.class;
    }

    /**
     * The column <code>public.value_state.code</code>.
     */
    public final TableField<ValueStateRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.value_state.datasets</code>.
     */
    public final TableField<ValueStateRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.value_state.order_by</code>.
     */
    public final TableField<ValueStateRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private ValueState(Name alias, Table<ValueStateRecord> aliased) {
        this(alias, aliased, null);
    }

    private ValueState(Name alias, Table<ValueStateRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.value_state</code> table reference
     */
    public ValueState(String alias) {
        this(DSL.name(alias), VALUE_STATE);
    }

    /**
     * Create an aliased <code>public.value_state</code> table reference
     */
    public ValueState(Name alias) {
        this(alias, VALUE_STATE);
    }

    /**
     * Create a <code>public.value_state</code> table reference
     */
    public ValueState() {
        this(DSL.name("value_state"), null);
    }

    public <O extends Record> ValueState(Table<O> child, ForeignKey<O, ValueStateRecord> key) {
        super(child, key, VALUE_STATE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<ValueStateRecord, Long> getIdentity() {
        return (Identity<ValueStateRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ValueStateRecord> getPrimaryKey() {
        return Keys.VALUE_STATE_PKEY;
    }

    @Override
    public List<UniqueKey<ValueStateRecord>> getKeys() {
        return Arrays.<UniqueKey<ValueStateRecord>>asList(Keys.VALUE_STATE_PKEY);
    }

    @Override
    public ValueState as(String alias) {
        return new ValueState(DSL.name(alias), this);
    }

    @Override
    public ValueState as(Name alias) {
        return new ValueState(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ValueState rename(String name) {
        return new ValueState(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ValueState rename(Name name) {
        return new ValueState(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}