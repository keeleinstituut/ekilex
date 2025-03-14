/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.LabelType;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LabelTypeRecord extends UpdatableRecordImpl<LabelTypeRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.label_type.code</code>.
     */
    public void setCode(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.label_type.code</code>.
     */
    public String getCode() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.label_type.value</code>.
     */
    public void setValue(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.label_type.value</code>.
     */
    public String getValue() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return LabelType.LABEL_TYPE.CODE;
    }

    @Override
    public Field<String> field2() {
        return LabelType.LABEL_TYPE.VALUE;
    }

    @Override
    public String component1() {
        return getCode();
    }

    @Override
    public String component2() {
        return getValue();
    }

    @Override
    public String value1() {
        return getCode();
    }

    @Override
    public String value2() {
        return getValue();
    }

    @Override
    public LabelTypeRecord value1(String value) {
        setCode(value);
        return this;
    }

    @Override
    public LabelTypeRecord value2(String value) {
        setValue(value);
        return this;
    }

    @Override
    public LabelTypeRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LabelTypeRecord
     */
    public LabelTypeRecord() {
        super(LabelType.LABEL_TYPE);
    }

    /**
     * Create a detached, initialised LabelTypeRecord
     */
    public LabelTypeRecord(String code, String value) {
        super(LabelType.LABEL_TYPE);

        setCode(code);
        setValue(value);
    }
}
