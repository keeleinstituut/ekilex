/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.udt.records;


import eki.wordweb.data.db.udt.TypeDomain;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeDomainRecord extends UDTRecordImpl<TypeDomainRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_domain.origin</code>.
     */
    public void setOrigin(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_domain.origin</code>.
     */
    public String getOrigin() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.type_domain.code</code>.
     */
    public void setCode(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_domain.code</code>.
     */
    public String getCode() {
        return (String) get(1);
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
        return TypeDomain.ORIGIN;
    }

    @Override
    public Field<String> field2() {
        return TypeDomain.CODE;
    }

    @Override
    public String component1() {
        return getOrigin();
    }

    @Override
    public String component2() {
        return getCode();
    }

    @Override
    public String value1() {
        return getOrigin();
    }

    @Override
    public String value2() {
        return getCode();
    }

    @Override
    public TypeDomainRecord value1(String value) {
        setOrigin(value);
        return this;
    }

    @Override
    public TypeDomainRecord value2(String value) {
        setCode(value);
        return this;
    }

    @Override
    public TypeDomainRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeDomainRecord
     */
    public TypeDomainRecord() {
        super(TypeDomain.TYPE_DOMAIN);
    }

    /**
     * Create a detached, initialised TypeDomainRecord
     */
    public TypeDomainRecord(String origin, String code) {
        super(TypeDomain.TYPE_DOMAIN);

        setOrigin(origin);
        setCode(code);
    }
}
