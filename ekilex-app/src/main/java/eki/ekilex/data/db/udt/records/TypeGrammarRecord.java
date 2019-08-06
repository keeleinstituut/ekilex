/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeGrammar;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeGrammarRecord extends UDTRecordImpl<TypeGrammarRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1044972256;

    /**
     * Setter for <code>public.type_grammar.value</code>.
     */
    public void setValue(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_grammar.value</code>.
     */
    public String getValue() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.type_grammar.complexity</code>.
     */
    public void setComplexity(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_grammar.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return TypeGrammar.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return TypeGrammar.COMPLEXITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getComplexity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getComplexity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeGrammarRecord value1(String value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeGrammarRecord value2(String value) {
        setComplexity(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeGrammarRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeGrammarRecord
     */
    public TypeGrammarRecord() {
        super(TypeGrammar.TYPE_GRAMMAR);
    }

    /**
     * Create a detached, initialised TypeGrammarRecord
     */
    public TypeGrammarRecord(String value, String complexity) {
        super(TypeGrammar.TYPE_GRAMMAR);

        set(0, value);
        set(1, complexity);
    }
}
