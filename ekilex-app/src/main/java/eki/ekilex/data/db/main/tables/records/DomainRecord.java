/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.Domain;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DomainRecord extends UpdatableRecordImpl<DomainRecord> implements Record6<String, String, String, String, String[], Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.domain.code</code>.
     */
    public void setCode(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.domain.code</code>.
     */
    public String getCode() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.domain.origin</code>.
     */
    public void setOrigin(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.domain.origin</code>.
     */
    public String getOrigin() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.domain.parent_code</code>.
     */
    public void setParentCode(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.domain.parent_code</code>.
     */
    public String getParentCode() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.domain.parent_origin</code>.
     */
    public void setParentOrigin(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.domain.parent_origin</code>.
     */
    public String getParentOrigin() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.domain.datasets</code>.
     */
    public void setDatasets(String[] value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.domain.datasets</code>.
     */
    public String[] getDatasets() {
        return (String[]) get(4);
    }

    /**
     * Setter for <code>public.domain.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.domain.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String, String[], Long> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<String, String, String, String, String[], Long> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Domain.DOMAIN.CODE;
    }

    @Override
    public Field<String> field2() {
        return Domain.DOMAIN.ORIGIN;
    }

    @Override
    public Field<String> field3() {
        return Domain.DOMAIN.PARENT_CODE;
    }

    @Override
    public Field<String> field4() {
        return Domain.DOMAIN.PARENT_ORIGIN;
    }

    @Override
    public Field<String[]> field5() {
        return Domain.DOMAIN.DATASETS;
    }

    @Override
    public Field<Long> field6() {
        return Domain.DOMAIN.ORDER_BY;
    }

    @Override
    public String component1() {
        return getCode();
    }

    @Override
    public String component2() {
        return getOrigin();
    }

    @Override
    public String component3() {
        return getParentCode();
    }

    @Override
    public String component4() {
        return getParentOrigin();
    }

    @Override
    public String[] component5() {
        return getDatasets();
    }

    @Override
    public Long component6() {
        return getOrderBy();
    }

    @Override
    public String value1() {
        return getCode();
    }

    @Override
    public String value2() {
        return getOrigin();
    }

    @Override
    public String value3() {
        return getParentCode();
    }

    @Override
    public String value4() {
        return getParentOrigin();
    }

    @Override
    public String[] value5() {
        return getDatasets();
    }

    @Override
    public Long value6() {
        return getOrderBy();
    }

    @Override
    public DomainRecord value1(String value) {
        setCode(value);
        return this;
    }

    @Override
    public DomainRecord value2(String value) {
        setOrigin(value);
        return this;
    }

    @Override
    public DomainRecord value3(String value) {
        setParentCode(value);
        return this;
    }

    @Override
    public DomainRecord value4(String value) {
        setParentOrigin(value);
        return this;
    }

    @Override
    public DomainRecord value5(String[] value) {
        setDatasets(value);
        return this;
    }

    @Override
    public DomainRecord value6(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public DomainRecord values(String value1, String value2, String value3, String value4, String[] value5, Long value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DomainRecord
     */
    public DomainRecord() {
        super(Domain.DOMAIN);
    }

    /**
     * Create a detached, initialised DomainRecord
     */
    public DomainRecord(String code, String origin, String parentCode, String parentOrigin, String[] datasets, Long orderBy) {
        super(Domain.DOMAIN);

        setCode(code);
        setOrigin(origin);
        setParentCode(parentCode);
        setParentOrigin(parentOrigin);
        setDatasets(datasets);
        setOrderBy(orderBy);
    }
}
