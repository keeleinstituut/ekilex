/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.DefinitionTypeRecord;

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
public class DefinitionType extends TableImpl<DefinitionTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.definition_type</code>
     */
    public static final DefinitionType DEFINITION_TYPE = new DefinitionType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefinitionTypeRecord> getRecordType() {
        return DefinitionTypeRecord.class;
    }

    /**
     * The column <code>public.definition_type.code</code>.
     */
    public final TableField<DefinitionTypeRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.definition_type.datasets</code>.
     */
    public final TableField<DefinitionTypeRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.definition_type.order_by</code>.
     */
    public final TableField<DefinitionTypeRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private DefinitionType(Name alias, Table<DefinitionTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private DefinitionType(Name alias, Table<DefinitionTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.definition_type</code> table reference
     */
    public DefinitionType(String alias) {
        this(DSL.name(alias), DEFINITION_TYPE);
    }

    /**
     * Create an aliased <code>public.definition_type</code> table reference
     */
    public DefinitionType(Name alias) {
        this(alias, DEFINITION_TYPE);
    }

    /**
     * Create a <code>public.definition_type</code> table reference
     */
    public DefinitionType() {
        this(DSL.name("definition_type"), null);
    }

    public <O extends Record> DefinitionType(Table<O> child, ForeignKey<O, DefinitionTypeRecord> key) {
        super(child, key, DEFINITION_TYPE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<DefinitionTypeRecord, Long> getIdentity() {
        return (Identity<DefinitionTypeRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<DefinitionTypeRecord> getPrimaryKey() {
        return Keys.DEFINITION_TYPE_PKEY;
    }

    @Override
    public List<UniqueKey<DefinitionTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<DefinitionTypeRecord>>asList(Keys.DEFINITION_TYPE_PKEY);
    }

    @Override
    public DefinitionType as(String alias) {
        return new DefinitionType(DSL.name(alias), this);
    }

    @Override
    public DefinitionType as(Name alias) {
        return new DefinitionType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DefinitionType rename(String name) {
        return new DefinitionType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefinitionType rename(Name name) {
        return new DefinitionType(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
