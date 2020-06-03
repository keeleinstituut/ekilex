/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ValueStateLabelRecord;

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
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ValueStateLabel extends TableImpl<ValueStateLabelRecord> {

    private static final long serialVersionUID = 1859713320;

    /**
     * The reference instance of <code>public.value_state_label</code>
     */
    public static final ValueStateLabel VALUE_STATE_LABEL = new ValueStateLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ValueStateLabelRecord> getRecordType() {
        return ValueStateLabelRecord.class;
    }

    /**
     * The column <code>public.value_state_label.code</code>.
     */
    public final TableField<ValueStateLabelRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.value_state_label.value</code>.
     */
    public final TableField<ValueStateLabelRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.value_state_label.lang</code>.
     */
    public final TableField<ValueStateLabelRecord, String> LANG = createField(DSL.name("lang"), org.jooq.impl.SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.value_state_label.type</code>.
     */
    public final TableField<ValueStateLabelRecord, String> TYPE = createField(DSL.name("type"), org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>public.value_state_label</code> table reference
     */
    public ValueStateLabel() {
        this(DSL.name("value_state_label"), null);
    }

    /**
     * Create an aliased <code>public.value_state_label</code> table reference
     */
    public ValueStateLabel(String alias) {
        this(DSL.name(alias), VALUE_STATE_LABEL);
    }

    /**
     * Create an aliased <code>public.value_state_label</code> table reference
     */
    public ValueStateLabel(Name alias) {
        this(alias, VALUE_STATE_LABEL);
    }

    private ValueStateLabel(Name alias, Table<ValueStateLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private ValueStateLabel(Name alias, Table<ValueStateLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ValueStateLabel(Table<O> child, ForeignKey<O, ValueStateLabelRecord> key) {
        super(child, key, VALUE_STATE_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<ValueStateLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<ValueStateLabelRecord>>asList(Keys.VALUE_STATE_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<ValueStateLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ValueStateLabelRecord, ?>>asList(Keys.VALUE_STATE_LABEL__VALUE_STATE_LABEL_CODE_FKEY, Keys.VALUE_STATE_LABEL__VALUE_STATE_LABEL_LANG_FKEY, Keys.VALUE_STATE_LABEL__VALUE_STATE_LABEL_TYPE_FKEY);
    }

    public ValueState valueState() {
        return new ValueState(this, Keys.VALUE_STATE_LABEL__VALUE_STATE_LABEL_CODE_FKEY);
    }

    public Language language() {
        return new Language(this, Keys.VALUE_STATE_LABEL__VALUE_STATE_LABEL_LANG_FKEY);
    }

    public LabelType labelType() {
        return new LabelType(this, Keys.VALUE_STATE_LABEL__VALUE_STATE_LABEL_TYPE_FKEY);
    }

    @Override
    public ValueStateLabel as(String alias) {
        return new ValueStateLabel(DSL.name(alias), this);
    }

    @Override
    public ValueStateLabel as(Name alias) {
        return new ValueStateLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ValueStateLabel rename(String name) {
        return new ValueStateLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ValueStateLabel rename(Name name) {
        return new ValueStateLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
