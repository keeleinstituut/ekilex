/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.GenderLabel;

import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GenderLabelRecord extends TableRecordImpl<GenderLabelRecord> implements Record4<String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.gender_label.code</code>.
     */
    public void setCode(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.gender_label.code</code>.
     */
    public String getCode() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.gender_label.value</code>.
     */
    public void setValue(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.gender_label.value</code>.
     */
    public String getValue() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.gender_label.lang</code>.
     */
    public void setLang(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.gender_label.lang</code>.
     */
    public String getLang() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.gender_label.type</code>.
     */
    public void setType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.gender_label.type</code>.
     */
    public String getType() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<String, String, String, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return GenderLabel.GENDER_LABEL.CODE;
    }

    @Override
    public Field<String> field2() {
        return GenderLabel.GENDER_LABEL.VALUE;
    }

    @Override
    public Field<String> field3() {
        return GenderLabel.GENDER_LABEL.LANG;
    }

    @Override
    public Field<String> field4() {
        return GenderLabel.GENDER_LABEL.TYPE;
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
    public String component3() {
        return getLang();
    }

    @Override
    public String component4() {
        return getType();
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
    public String value3() {
        return getLang();
    }

    @Override
    public String value4() {
        return getType();
    }

    @Override
    public GenderLabelRecord value1(String value) {
        setCode(value);
        return this;
    }

    @Override
    public GenderLabelRecord value2(String value) {
        setValue(value);
        return this;
    }

    @Override
    public GenderLabelRecord value3(String value) {
        setLang(value);
        return this;
    }

    @Override
    public GenderLabelRecord value4(String value) {
        setType(value);
        return this;
    }

    @Override
    public GenderLabelRecord values(String value1, String value2, String value3, String value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GenderLabelRecord
     */
    public GenderLabelRecord() {
        super(GenderLabel.GENDER_LABEL);
    }

    /**
     * Create a detached, initialised GenderLabelRecord
     */
    public GenderLabelRecord(String code, String value, String lang, String type) {
        super(GenderLabel.GENDER_LABEL);

        setCode(code);
        setValue(value);
        setLang(lang);
        setType(type);
    }
}