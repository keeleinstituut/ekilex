/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt.records;


import eki.ekilex.data.db.main.udt.TypeClassifier;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeClassifierRecord extends UDTRecordImpl<TypeClassifierRecord> implements Record3<String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_classifier.name</code>.
     */
    public void setName(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_classifier.name</code>.
     */
    public String getName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.type_classifier.code</code>.
     */
    public void setCode(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_classifier.code</code>.
     */
    public String getCode() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.type_classifier.value</code>.
     */
    public void setValue(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.type_classifier.value</code>.
     */
    public String getValue() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TypeClassifier.NAME;
    }

    @Override
    public Field<String> field2() {
        return TypeClassifier.CODE;
    }

    @Override
    public Field<String> field3() {
        return TypeClassifier.VALUE;
    }

    @Override
    public String component1() {
        return getName();
    }

    @Override
    public String component2() {
        return getCode();
    }

    @Override
    public String component3() {
        return getValue();
    }

    @Override
    public String value1() {
        return getName();
    }

    @Override
    public String value2() {
        return getCode();
    }

    @Override
    public String value3() {
        return getValue();
    }

    @Override
    public TypeClassifierRecord value1(String value) {
        setName(value);
        return this;
    }

    @Override
    public TypeClassifierRecord value2(String value) {
        setCode(value);
        return this;
    }

    @Override
    public TypeClassifierRecord value3(String value) {
        setValue(value);
        return this;
    }

    @Override
    public TypeClassifierRecord values(String value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeClassifierRecord
     */
    public TypeClassifierRecord() {
        super(TypeClassifier.TYPE_CLASSIFIER);
    }

    /**
     * Create a detached, initialised TypeClassifierRecord
     */
    public TypeClassifierRecord(String name, String code, String value) {
        super(TypeClassifier.TYPE_CLASSIFIER);

        setName(name);
        setCode(code);
        setValue(value);
    }
}