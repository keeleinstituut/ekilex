/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeSourceLink;

import org.jooq.Field;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeSourceLinkRecord extends UDTRecordImpl<TypeSourceLinkRecord> implements Record9<String, Long, Long, String, String, String, Long, Long, String[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_source_link.ref_owner</code>.
     */
    public void setRefOwner(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_source_link.ref_owner</code>.
     */
    public String getRefOwner() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.type_source_link.owner_id</code>.
     */
    public void setOwnerId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_source_link.owner_id</code>.
     */
    public Long getOwnerId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.type_source_link.source_link_id</code>.
     */
    public void setSourceLinkId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.type_source_link.source_link_id</code>.
     */
    public Long getSourceLinkId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.type_source_link.source_link_type</code>.
     */
    public void setSourceLinkType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.type_source_link.source_link_type</code>.
     */
    public String getSourceLinkType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.type_source_link.name</code>.
     */
    public void setName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.type_source_link.name</code>.
     */
    public String getName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.type_source_link.value</code>.
     */
    public void setValue(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.type_source_link.value</code>.
     */
    public String getValue() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.type_source_link.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.type_source_link.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>public.type_source_link.source_id</code>.
     */
    public void setSourceId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.type_source_link.source_id</code>.
     */
    public Long getSourceId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>public.type_source_link.source_props</code>.
     */
    public void setSourceProps(String[] value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.type_source_link.source_props</code>.
     */
    public String[] getSourceProps() {
        return (String[]) get(8);
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, Long, Long, String, String, String, Long, Long, String[]> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<String, Long, Long, String, String, String, Long, Long, String[]> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TypeSourceLink.REF_OWNER;
    }

    @Override
    public Field<Long> field2() {
        return TypeSourceLink.OWNER_ID;
    }

    @Override
    public Field<Long> field3() {
        return TypeSourceLink.SOURCE_LINK_ID;
    }

    @Override
    public Field<String> field4() {
        return TypeSourceLink.SOURCE_LINK_TYPE;
    }

    @Override
    public Field<String> field5() {
        return TypeSourceLink.NAME;
    }

    @Override
    public Field<String> field6() {
        return TypeSourceLink.VALUE;
    }

    @Override
    public Field<Long> field7() {
        return TypeSourceLink.ORDER_BY;
    }

    @Override
    public Field<Long> field8() {
        return TypeSourceLink.SOURCE_ID;
    }

    @Override
    public Field<String[]> field9() {
        return TypeSourceLink.SOURCE_PROPS;
    }

    @Override
    public String component1() {
        return getRefOwner();
    }

    @Override
    public Long component2() {
        return getOwnerId();
    }

    @Override
    public Long component3() {
        return getSourceLinkId();
    }

    @Override
    public String component4() {
        return getSourceLinkType();
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
    public Long component8() {
        return getSourceId();
    }

    @Override
    public String[] component9() {
        return getSourceProps();
    }

    @Override
    public String value1() {
        return getRefOwner();
    }

    @Override
    public Long value2() {
        return getOwnerId();
    }

    @Override
    public Long value3() {
        return getSourceLinkId();
    }

    @Override
    public String value4() {
        return getSourceLinkType();
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
    public Long value8() {
        return getSourceId();
    }

    @Override
    public String[] value9() {
        return getSourceProps();
    }

    @Override
    public TypeSourceLinkRecord value1(String value) {
        setRefOwner(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value2(Long value) {
        setOwnerId(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value3(Long value) {
        setSourceLinkId(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value4(String value) {
        setSourceLinkType(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value5(String value) {
        setName(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value6(String value) {
        setValue(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value7(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value8(Long value) {
        setSourceId(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord value9(String[] value) {
        setSourceProps(value);
        return this;
    }

    @Override
    public TypeSourceLinkRecord values(String value1, Long value2, Long value3, String value4, String value5, String value6, Long value7, Long value8, String[] value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeSourceLinkRecord
     */
    public TypeSourceLinkRecord() {
        super(TypeSourceLink.TYPE_SOURCE_LINK);
    }

    /**
     * Create a detached, initialised TypeSourceLinkRecord
     */
    public TypeSourceLinkRecord(String refOwner, Long ownerId, Long sourceLinkId, String sourceLinkType, String name, String value, Long orderBy, Long sourceId, String[] sourceProps) {
        super(TypeSourceLink.TYPE_SOURCE_LINK);

        setRefOwner(refOwner);
        setOwnerId(ownerId);
        setSourceLinkId(sourceLinkId);
        setSourceLinkType(sourceLinkType);
        setName(name);
        setValue(value);
        setOrderBy(orderBy);
        setSourceId(sourceId);
        setSourceProps(sourceProps);
    }
}
