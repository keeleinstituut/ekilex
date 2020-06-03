/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.LifecycleLog;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LifecycleLogRecord extends UpdatableRecordImpl<LifecycleLogRecord> implements Record9<Long, Long, String, String, String, String, Timestamp, String, String> {

    private static final long serialVersionUID = -683373144;

    /**
     * Setter for <code>public.lifecycle_log.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.lifecycle_log.entity_id</code>.
     */
    public void setEntityId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.entity_id</code>.
     */
    public Long getEntityId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.lifecycle_log.entity_name</code>.
     */
    public void setEntityName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.entity_name</code>.
     */
    public String getEntityName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.lifecycle_log.entity_prop</code>.
     */
    public void setEntityProp(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.entity_prop</code>.
     */
    public String getEntityProp() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.lifecycle_log.event_type</code>.
     */
    public void setEventType(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.event_type</code>.
     */
    public String getEventType() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.lifecycle_log.event_by</code>.
     */
    public void setEventBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.event_by</code>.
     */
    public String getEventBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.lifecycle_log.event_on</code>.
     */
    public void setEventOn(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.event_on</code>.
     */
    public Timestamp getEventOn() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>public.lifecycle_log.recent</code>.
     */
    public void setRecent(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.recent</code>.
     */
    public String getRecent() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.lifecycle_log.entry</code>.
     */
    public void setEntry(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.lifecycle_log.entry</code>.
     */
    public String getEntry() {
        return (String) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, String, String, String, String, Timestamp, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<Long, Long, String, String, String, String, Timestamp, String, String> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return LifecycleLog.LIFECYCLE_LOG.ID;
    }

    @Override
    public Field<Long> field2() {
        return LifecycleLog.LIFECYCLE_LOG.ENTITY_ID;
    }

    @Override
    public Field<String> field3() {
        return LifecycleLog.LIFECYCLE_LOG.ENTITY_NAME;
    }

    @Override
    public Field<String> field4() {
        return LifecycleLog.LIFECYCLE_LOG.ENTITY_PROP;
    }

    @Override
    public Field<String> field5() {
        return LifecycleLog.LIFECYCLE_LOG.EVENT_TYPE;
    }

    @Override
    public Field<String> field6() {
        return LifecycleLog.LIFECYCLE_LOG.EVENT_BY;
    }

    @Override
    public Field<Timestamp> field7() {
        return LifecycleLog.LIFECYCLE_LOG.EVENT_ON;
    }

    @Override
    public Field<String> field8() {
        return LifecycleLog.LIFECYCLE_LOG.RECENT;
    }

    @Override
    public Field<String> field9() {
        return LifecycleLog.LIFECYCLE_LOG.ENTRY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getEntityId();
    }

    @Override
    public String component3() {
        return getEntityName();
    }

    @Override
    public String component4() {
        return getEntityProp();
    }

    @Override
    public String component5() {
        return getEventType();
    }

    @Override
    public String component6() {
        return getEventBy();
    }

    @Override
    public Timestamp component7() {
        return getEventOn();
    }

    @Override
    public String component8() {
        return getRecent();
    }

    @Override
    public String component9() {
        return getEntry();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getEntityId();
    }

    @Override
    public String value3() {
        return getEntityName();
    }

    @Override
    public String value4() {
        return getEntityProp();
    }

    @Override
    public String value5() {
        return getEventType();
    }

    @Override
    public String value6() {
        return getEventBy();
    }

    @Override
    public Timestamp value7() {
        return getEventOn();
    }

    @Override
    public String value8() {
        return getRecent();
    }

    @Override
    public String value9() {
        return getEntry();
    }

    @Override
    public LifecycleLogRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value2(Long value) {
        setEntityId(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value3(String value) {
        setEntityName(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value4(String value) {
        setEntityProp(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value5(String value) {
        setEventType(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value6(String value) {
        setEventBy(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value7(Timestamp value) {
        setEventOn(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value8(String value) {
        setRecent(value);
        return this;
    }

    @Override
    public LifecycleLogRecord value9(String value) {
        setEntry(value);
        return this;
    }

    @Override
    public LifecycleLogRecord values(Long value1, Long value2, String value3, String value4, String value5, String value6, Timestamp value7, String value8, String value9) {
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
     * Create a detached LifecycleLogRecord
     */
    public LifecycleLogRecord() {
        super(LifecycleLog.LIFECYCLE_LOG);
    }

    /**
     * Create a detached, initialised LifecycleLogRecord
     */
    public LifecycleLogRecord(Long id, Long entityId, String entityName, String entityProp, String eventType, String eventBy, Timestamp eventOn, String recent, String entry) {
        super(LifecycleLog.LIFECYCLE_LOG);

        set(0, id);
        set(1, entityId);
        set(2, entityName);
        set(3, entityProp);
        set(4, eventType);
        set(5, eventBy);
        set(6, eventOn);
        set(7, recent);
        set(8, entry);
    }
}
