/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.GovernmentTypeRecord;

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
public class GovernmentType extends TableImpl<GovernmentTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.government_type</code>
     */
    public static final GovernmentType GOVERNMENT_TYPE = new GovernmentType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GovernmentTypeRecord> getRecordType() {
        return GovernmentTypeRecord.class;
    }

    /**
     * The column <code>public.government_type.code</code>.
     */
    public final TableField<GovernmentTypeRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.government_type.datasets</code>.
     */
    public final TableField<GovernmentTypeRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.government_type.order_by</code>.
     */
    public final TableField<GovernmentTypeRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private GovernmentType(Name alias, Table<GovernmentTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private GovernmentType(Name alias, Table<GovernmentTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.government_type</code> table reference
     */
    public GovernmentType(String alias) {
        this(DSL.name(alias), GOVERNMENT_TYPE);
    }

    /**
     * Create an aliased <code>public.government_type</code> table reference
     */
    public GovernmentType(Name alias) {
        this(alias, GOVERNMENT_TYPE);
    }

    /**
     * Create a <code>public.government_type</code> table reference
     */
    public GovernmentType() {
        this(DSL.name("government_type"), null);
    }

    public <O extends Record> GovernmentType(Table<O> child, ForeignKey<O, GovernmentTypeRecord> key) {
        super(child, key, GOVERNMENT_TYPE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<GovernmentTypeRecord, Long> getIdentity() {
        return (Identity<GovernmentTypeRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<GovernmentTypeRecord> getPrimaryKey() {
        return Keys.GOVERNMENT_TYPE_PKEY;
    }

    @Override
    public List<UniqueKey<GovernmentTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<GovernmentTypeRecord>>asList(Keys.GOVERNMENT_TYPE_PKEY);
    }

    @Override
    public GovernmentType as(String alias) {
        return new GovernmentType(DSL.name(alias), this);
    }

    @Override
    public GovernmentType as(Name alias) {
        return new GovernmentType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentType rename(String name) {
        return new GovernmentType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentType rename(Name name) {
        return new GovernmentType(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}