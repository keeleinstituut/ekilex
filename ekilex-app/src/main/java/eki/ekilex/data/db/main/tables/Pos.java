/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.PosRecord;

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
public class Pos extends TableImpl<PosRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.pos</code>
     */
    public static final Pos POS = new Pos();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PosRecord> getRecordType() {
        return PosRecord.class;
    }

    /**
     * The column <code>public.pos.code</code>.
     */
    public final TableField<PosRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.pos.datasets</code>.
     */
    public final TableField<PosRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.pos.order_by</code>.
     */
    public final TableField<PosRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private Pos(Name alias, Table<PosRecord> aliased) {
        this(alias, aliased, null);
    }

    private Pos(Name alias, Table<PosRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.pos</code> table reference
     */
    public Pos(String alias) {
        this(DSL.name(alias), POS);
    }

    /**
     * Create an aliased <code>public.pos</code> table reference
     */
    public Pos(Name alias) {
        this(alias, POS);
    }

    /**
     * Create a <code>public.pos</code> table reference
     */
    public Pos() {
        this(DSL.name("pos"), null);
    }

    public <O extends Record> Pos(Table<O> child, ForeignKey<O, PosRecord> key) {
        super(child, key, POS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<PosRecord, Long> getIdentity() {
        return (Identity<PosRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<PosRecord> getPrimaryKey() {
        return Keys.POS_PKEY;
    }

    @Override
    public List<UniqueKey<PosRecord>> getKeys() {
        return Arrays.<UniqueKey<PosRecord>>asList(Keys.POS_PKEY);
    }

    @Override
    public Pos as(String alias) {
        return new Pos(DSL.name(alias), this);
    }

    @Override
    public Pos as(Name alias) {
        return new Pos(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Pos rename(String name) {
        return new Pos(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Pos rename(Name name) {
        return new Pos(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
