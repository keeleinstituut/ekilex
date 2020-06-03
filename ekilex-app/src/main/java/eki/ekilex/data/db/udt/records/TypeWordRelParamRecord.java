/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeWordRelParam;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeWordRelParamRecord extends UDTRecordImpl<TypeWordRelParamRecord> implements Record2<String, BigDecimal> {

    private static final long serialVersionUID = -582175029;

    /**
     * Setter for <code>public.type_word_rel_param.name</code>.
     */
    public void setName(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_word_rel_param.name</code>.
     */
    public String getName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.type_word_rel_param.value</code>.
     */
    public void setValue(BigDecimal value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_word_rel_param.value</code>.
     */
    public BigDecimal getValue() {
        return (BigDecimal) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, BigDecimal> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, BigDecimal> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TypeWordRelParam.NAME;
    }

    @Override
    public Field<BigDecimal> field2() {
        return TypeWordRelParam.VALUE;
    }

    @Override
    public String component1() {
        return getName();
    }

    @Override
    public BigDecimal component2() {
        return getValue();
    }

    @Override
    public String value1() {
        return getName();
    }

    @Override
    public BigDecimal value2() {
        return getValue();
    }

    @Override
    public TypeWordRelParamRecord value1(String value) {
        setName(value);
        return this;
    }

    @Override
    public TypeWordRelParamRecord value2(BigDecimal value) {
        setValue(value);
        return this;
    }

    @Override
    public TypeWordRelParamRecord values(String value1, BigDecimal value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeWordRelParamRecord
     */
    public TypeWordRelParamRecord() {
        super(TypeWordRelParam.TYPE_WORD_REL_PARAM);
    }

    /**
     * Create a detached, initialised TypeWordRelParamRecord
     */
    public TypeWordRelParamRecord(String name, BigDecimal value) {
        super(TypeWordRelParam.TYPE_WORD_REL_PARAM);

        set(0, name);
        set(1, value);
    }
}
