/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.DerivRecord;

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
public class Deriv extends TableImpl<DerivRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.deriv</code>
     */
    public static final Deriv DERIV = new Deriv();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DerivRecord> getRecordType() {
        return DerivRecord.class;
    }

    /**
     * The column <code>public.deriv.code</code>.
     */
    public final TableField<DerivRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.deriv.datasets</code>.
     */
    public final TableField<DerivRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.deriv.order_by</code>.
     */
    public final TableField<DerivRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private Deriv(Name alias, Table<DerivRecord> aliased) {
        this(alias, aliased, null);
    }

    private Deriv(Name alias, Table<DerivRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.deriv</code> table reference
     */
    public Deriv(String alias) {
        this(DSL.name(alias), DERIV);
    }

    /**
     * Create an aliased <code>public.deriv</code> table reference
     */
    public Deriv(Name alias) {
        this(alias, DERIV);
    }

    /**
     * Create a <code>public.deriv</code> table reference
     */
    public Deriv() {
        this(DSL.name("deriv"), null);
    }

    public <O extends Record> Deriv(Table<O> child, ForeignKey<O, DerivRecord> key) {
        super(child, key, DERIV);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<DerivRecord, Long> getIdentity() {
        return (Identity<DerivRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<DerivRecord> getPrimaryKey() {
        return Keys.DERIV_PKEY;
    }

    @Override
    public List<UniqueKey<DerivRecord>> getKeys() {
        return Arrays.<UniqueKey<DerivRecord>>asList(Keys.DERIV_PKEY);
    }

    @Override
    public Deriv as(String alias) {
        return new Deriv(DSL.name(alias), this);
    }

    @Override
    public Deriv as(Name alias) {
        return new Deriv(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Deriv rename(String name) {
        return new Deriv(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Deriv rename(Name name) {
        return new Deriv(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
