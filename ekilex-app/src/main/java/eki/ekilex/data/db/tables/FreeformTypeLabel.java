/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.FreeformTypeLabelRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class FreeformTypeLabel extends TableImpl<FreeformTypeLabelRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.freeform_type_label</code>
     */
    public static final FreeformTypeLabel FREEFORM_TYPE_LABEL = new FreeformTypeLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FreeformTypeLabelRecord> getRecordType() {
        return FreeformTypeLabelRecord.class;
    }

    /**
     * The column <code>public.freeform_type_label.code</code>.
     */
    public final TableField<FreeformTypeLabelRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.freeform_type_label.value</code>.
     */
    public final TableField<FreeformTypeLabelRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.freeform_type_label.lang</code>.
     */
    public final TableField<FreeformTypeLabelRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.freeform_type_label.type</code>.
     */
    public final TableField<FreeformTypeLabelRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    private FreeformTypeLabel(Name alias, Table<FreeformTypeLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private FreeformTypeLabel(Name alias, Table<FreeformTypeLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.freeform_type_label</code> table reference
     */
    public FreeformTypeLabel(String alias) {
        this(DSL.name(alias), FREEFORM_TYPE_LABEL);
    }

    /**
     * Create an aliased <code>public.freeform_type_label</code> table reference
     */
    public FreeformTypeLabel(Name alias) {
        this(alias, FREEFORM_TYPE_LABEL);
    }

    /**
     * Create a <code>public.freeform_type_label</code> table reference
     */
    public FreeformTypeLabel() {
        this(DSL.name("freeform_type_label"), null);
    }

    public <O extends Record> FreeformTypeLabel(Table<O> child, ForeignKey<O, FreeformTypeLabelRecord> key) {
        super(child, key, FREEFORM_TYPE_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<FreeformTypeLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<FreeformTypeLabelRecord>>asList(Keys.FREEFORM_TYPE_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<FreeformTypeLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<FreeformTypeLabelRecord, ?>>asList(Keys.FREEFORM_TYPE_LABEL__FREEFORM_TYPE_LABEL_CODE_FKEY, Keys.FREEFORM_TYPE_LABEL__FREEFORM_TYPE_LABEL_LANG_FKEY, Keys.FREEFORM_TYPE_LABEL__FREEFORM_TYPE_LABEL_TYPE_FKEY);
    }

    private transient FreeformType _freeformType;
    private transient Language _language;
    private transient LabelType _labelType;

    public FreeformType freeformType() {
        if (_freeformType == null)
            _freeformType = new FreeformType(this, Keys.FREEFORM_TYPE_LABEL__FREEFORM_TYPE_LABEL_CODE_FKEY);

        return _freeformType;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.FREEFORM_TYPE_LABEL__FREEFORM_TYPE_LABEL_LANG_FKEY);

        return _language;
    }

    public LabelType labelType() {
        if (_labelType == null)
            _labelType = new LabelType(this, Keys.FREEFORM_TYPE_LABEL__FREEFORM_TYPE_LABEL_TYPE_FKEY);

        return _labelType;
    }

    @Override
    public FreeformTypeLabel as(String alias) {
        return new FreeformTypeLabel(DSL.name(alias), this);
    }

    @Override
    public FreeformTypeLabel as(Name alias) {
        return new FreeformTypeLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FreeformTypeLabel rename(String name) {
        return new FreeformTypeLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FreeformTypeLabel rename(Name name) {
        return new FreeformTypeLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
