/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.Tag;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TagRecord extends UpdatableRecordImpl<TagRecord> implements Record5<String, Boolean, Boolean, Long, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.tag.name</code>.
     */
    public void setName(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.tag.name</code>.
     */
    public String getName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.tag.set_automatically</code>.
     */
    public void setSetAutomatically(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.tag.set_automatically</code>.
     */
    public Boolean getSetAutomatically() {
        return (Boolean) get(1);
    }

    /**
     * Setter for <code>public.tag.remove_to_complete</code>.
     */
    public void setRemoveToComplete(Boolean value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.tag.remove_to_complete</code>.
     */
    public Boolean getRemoveToComplete() {
        return (Boolean) get(2);
    }

    /**
     * Setter for <code>public.tag.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.tag.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>public.tag.type</code>.
     */
    public void setType(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.tag.type</code>.
     */
    public String getType() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, Boolean, Boolean, Long, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<String, Boolean, Boolean, Long, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Tag.TAG.NAME;
    }

    @Override
    public Field<Boolean> field2() {
        return Tag.TAG.SET_AUTOMATICALLY;
    }

    @Override
    public Field<Boolean> field3() {
        return Tag.TAG.REMOVE_TO_COMPLETE;
    }

    @Override
    public Field<Long> field4() {
        return Tag.TAG.ORDER_BY;
    }

    @Override
    public Field<String> field5() {
        return Tag.TAG.TYPE;
    }

    @Override
    public String component1() {
        return getName();
    }

    @Override
    public Boolean component2() {
        return getSetAutomatically();
    }

    @Override
    public Boolean component3() {
        return getRemoveToComplete();
    }

    @Override
    public Long component4() {
        return getOrderBy();
    }

    @Override
    public String component5() {
        return getType();
    }

    @Override
    public String value1() {
        return getName();
    }

    @Override
    public Boolean value2() {
        return getSetAutomatically();
    }

    @Override
    public Boolean value3() {
        return getRemoveToComplete();
    }

    @Override
    public Long value4() {
        return getOrderBy();
    }

    @Override
    public String value5() {
        return getType();
    }

    @Override
    public TagRecord value1(String value) {
        setName(value);
        return this;
    }

    @Override
    public TagRecord value2(Boolean value) {
        setSetAutomatically(value);
        return this;
    }

    @Override
    public TagRecord value3(Boolean value) {
        setRemoveToComplete(value);
        return this;
    }

    @Override
    public TagRecord value4(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public TagRecord value5(String value) {
        setType(value);
        return this;
    }

    @Override
    public TagRecord values(String value1, Boolean value2, Boolean value3, Long value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TagRecord
     */
    public TagRecord() {
        super(Tag.TAG);
    }

    /**
     * Create a detached, initialised TagRecord
     */
    public TagRecord(String name, Boolean setAutomatically, Boolean removeToComplete, Long orderBy, String type) {
        super(Tag.TAG);

        setName(name);
        setSetAutomatically(setAutomatically);
        setRemoveToComplete(removeToComplete);
        setOrderBy(orderBy);
        setType(type);
    }
}