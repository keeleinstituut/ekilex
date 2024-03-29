/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.FreeformRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row18;
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
public class Freeform extends TableImpl<FreeformRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.freeform</code>
     */
    public static final Freeform FREEFORM = new Freeform();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FreeformRecord> getRecordType() {
        return FreeformRecord.class;
    }

    /**
     * The column <code>public.freeform.id</code>.
     */
    public final TableField<FreeformRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.freeform.parent_id</code>.
     */
    public final TableField<FreeformRecord, Long> PARENT_ID = createField(DSL.name("parent_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.freeform.type</code>.
     */
    public final TableField<FreeformRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.freeform.value_text</code>.
     */
    public final TableField<FreeformRecord, String> VALUE_TEXT = createField(DSL.name("value_text"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.freeform.value_prese</code>.
     */
    public final TableField<FreeformRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.freeform.value_date</code>.
     */
    public final TableField<FreeformRecord, Timestamp> VALUE_DATE = createField(DSL.name("value_date"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.freeform.value_number</code>.
     */
    public final TableField<FreeformRecord, BigDecimal> VALUE_NUMBER = createField(DSL.name("value_number"), SQLDataType.NUMERIC(14, 4), this, "");

    /**
     * The column <code>public.freeform.value_array</code>.
     */
    public final TableField<FreeformRecord, String[]> VALUE_ARRAY = createField(DSL.name("value_array"), SQLDataType.CLOB.getArrayDataType(), this, "");

    /**
     * The column <code>public.freeform.classif_name</code>.
     */
    public final TableField<FreeformRecord, String> CLASSIF_NAME = createField(DSL.name("classif_name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.freeform.classif_code</code>.
     */
    public final TableField<FreeformRecord, String> CLASSIF_CODE = createField(DSL.name("classif_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.freeform.lang</code>.
     */
    public final TableField<FreeformRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), this, "");

    /**
     * The column <code>public.freeform.complexity</code>.
     */
    public final TableField<FreeformRecord, String> COMPLEXITY = createField(DSL.name("complexity"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.freeform.order_by</code>.
     */
    public final TableField<FreeformRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.freeform.is_public</code>.
     */
    public final TableField<FreeformRecord, Boolean> IS_PUBLIC = createField(DSL.name("is_public"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("true", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.freeform.created_by</code>.
     */
    public final TableField<FreeformRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.freeform.created_on</code>.
     */
    public final TableField<FreeformRecord, Timestamp> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.freeform.modified_by</code>.
     */
    public final TableField<FreeformRecord, String> MODIFIED_BY = createField(DSL.name("modified_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.freeform.modified_on</code>.
     */
    public final TableField<FreeformRecord, Timestamp> MODIFIED_ON = createField(DSL.name("modified_on"), SQLDataType.TIMESTAMP(6), this, "");

    private Freeform(Name alias, Table<FreeformRecord> aliased) {
        this(alias, aliased, null);
    }

    private Freeform(Name alias, Table<FreeformRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.freeform</code> table reference
     */
    public Freeform(String alias) {
        this(DSL.name(alias), FREEFORM);
    }

    /**
     * Create an aliased <code>public.freeform</code> table reference
     */
    public Freeform(Name alias) {
        this(alias, FREEFORM);
    }

    /**
     * Create a <code>public.freeform</code> table reference
     */
    public Freeform() {
        this(DSL.name("freeform"), null);
    }

    public <O extends Record> Freeform(Table<O> child, ForeignKey<O, FreeformRecord> key) {
        super(child, key, FREEFORM);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<FreeformRecord, Long> getIdentity() {
        return (Identity<FreeformRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<FreeformRecord> getPrimaryKey() {
        return Keys.FREEFORM_PKEY;
    }

    @Override
    public List<UniqueKey<FreeformRecord>> getKeys() {
        return Arrays.<UniqueKey<FreeformRecord>>asList(Keys.FREEFORM_PKEY);
    }

    @Override
    public List<ForeignKey<FreeformRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<FreeformRecord, ?>>asList(Keys.FREEFORM__FREEFORM_PARENT_ID_FKEY, Keys.FREEFORM__FREEFORM_LANG_FKEY);
    }

    private transient Freeform _freeform;
    private transient Language _language;

    public Freeform freeform() {
        if (_freeform == null)
            _freeform = new Freeform(this, Keys.FREEFORM__FREEFORM_PARENT_ID_FKEY);

        return _freeform;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.FREEFORM__FREEFORM_LANG_FKEY);

        return _language;
    }

    @Override
    public Freeform as(String alias) {
        return new Freeform(DSL.name(alias), this);
    }

    @Override
    public Freeform as(Name alias) {
        return new Freeform(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Freeform rename(String name) {
        return new Freeform(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Freeform rename(Name name) {
        return new Freeform(name, null);
    }

    // -------------------------------------------------------------------------
    // Row18 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row18<Long, Long, String, String, String, Timestamp, BigDecimal, String[], String, String, String, String, Long, Boolean, String, Timestamp, String, Timestamp> fieldsRow() {
        return (Row18) super.fieldsRow();
    }
}
