/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.FreeformSourceLink;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FreeformSourceLinkRecord extends UpdatableRecordImpl<FreeformSourceLinkRecord> implements Record7<Long, Long, Long, String, String, String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.freeform_source_link.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.freeform_source_link.freeform_id</code>.
     */
    public void setFreeformId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.freeform_id</code>.
     */
    public Long getFreeformId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.freeform_source_link.source_id</code>.
     */
    public void setSourceId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.source_id</code>.
     */
    public Long getSourceId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.freeform_source_link.type</code>.
     */
    public void setType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.type</code>.
     */
    public String getType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.freeform_source_link.name</code>.
     */
    public void setName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.name</code>.
     */
    public String getName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.freeform_source_link.value</code>.
     */
    public void setValue(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.value</code>.
     */
    public String getValue() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.freeform_source_link.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.freeform_source_link.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, Long, Long, String, String, String, Long> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, Long, Long, String, String, String, Long> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.ID;
    }

    @Override
    public Field<Long> field2() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.FREEFORM_ID;
    }

    @Override
    public Field<Long> field3() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.SOURCE_ID;
    }

    @Override
    public Field<String> field4() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.TYPE;
    }

    @Override
    public Field<String> field5() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.NAME;
    }

    @Override
    public Field<String> field6() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.VALUE;
    }

    @Override
    public Field<Long> field7() {
        return FreeformSourceLink.FREEFORM_SOURCE_LINK.ORDER_BY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getFreeformId();
    }

    @Override
    public Long component3() {
        return getSourceId();
    }

    @Override
    public String component4() {
        return getType();
    }

    @Override
    public String component5() {
        return getName();
    }

    @Override
    public String component6() {
        return getValue();
    }

    @Override
    public Long component7() {
        return getOrderBy();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getFreeformId();
    }

    @Override
    public Long value3() {
        return getSourceId();
    }

    @Override
    public String value4() {
        return getType();
    }

    @Override
    public String value5() {
        return getName();
    }

    @Override
    public String value6() {
        return getValue();
    }

    @Override
    public Long value7() {
        return getOrderBy();
    }

    @Override
    public FreeformSourceLinkRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord value2(Long value) {
        setFreeformId(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord value3(Long value) {
        setSourceId(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord value4(String value) {
        setType(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord value5(String value) {
        setName(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord value6(String value) {
        setValue(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord value7(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public FreeformSourceLinkRecord values(Long value1, Long value2, Long value3, String value4, String value5, String value6, Long value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FreeformSourceLinkRecord
     */
    public FreeformSourceLinkRecord() {
        super(FreeformSourceLink.FREEFORM_SOURCE_LINK);
    }

    /**
     * Create a detached, initialised FreeformSourceLinkRecord
     */
    public FreeformSourceLinkRecord(Long id, Long freeformId, Long sourceId, String type, String name, String value, Long orderBy) {
        super(FreeformSourceLink.FREEFORM_SOURCE_LINK);

        setId(id);
        setFreeformId(freeformId);
        setSourceId(sourceId);
        setType(type);
        setName(name);
        setValue(value);
        setOrderBy(orderBy);
    }
}
