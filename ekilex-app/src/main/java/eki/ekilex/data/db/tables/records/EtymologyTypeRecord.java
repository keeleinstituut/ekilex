/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.EtymologyType;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EtymologyTypeRecord extends UpdatableRecordImpl<EtymologyTypeRecord> implements Record3<String, String[], Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.etymology_type.code</code>.
     */
    public void setCode(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.etymology_type.code</code>.
     */
    public String getCode() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.etymology_type.datasets</code>.
     */
    public void setDatasets(String[] value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.etymology_type.datasets</code>.
     */
    public String[] getDatasets() {
        return (String[]) get(1);
    }

    /**
     * Setter for <code>public.etymology_type.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.etymology_type.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String[], Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return EtymologyType.ETYMOLOGY_TYPE.CODE;
    }

    @Override
    public Field<String[]> field2() {
        return EtymologyType.ETYMOLOGY_TYPE.DATASETS;
    }

    @Override
    public Field<Long> field3() {
        return EtymologyType.ETYMOLOGY_TYPE.ORDER_BY;
    }

    @Override
    public String component1() {
        return getCode();
    }

    @Override
    public String[] component2() {
        return getDatasets();
    }

    @Override
    public Long component3() {
        return getOrderBy();
    }

    @Override
    public String value1() {
        return getCode();
    }

    @Override
    public String[] value2() {
        return getDatasets();
    }

    @Override
    public Long value3() {
        return getOrderBy();
    }

    @Override
    public EtymologyTypeRecord value1(String value) {
        setCode(value);
        return this;
    }

    @Override
    public EtymologyTypeRecord value2(String[] value) {
        setDatasets(value);
        return this;
    }

    @Override
    public EtymologyTypeRecord value3(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public EtymologyTypeRecord values(String value1, String[] value2, Long value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EtymologyTypeRecord
     */
    public EtymologyTypeRecord() {
        super(EtymologyType.ETYMOLOGY_TYPE);
    }

    /**
     * Create a detached, initialised EtymologyTypeRecord
     */
    public EtymologyTypeRecord(String code, String[] datasets, Long orderBy) {
        super(EtymologyType.ETYMOLOGY_TYPE);

        setCode(code);
        setDatasets(datasets);
        setOrderBy(orderBy);
    }
}
