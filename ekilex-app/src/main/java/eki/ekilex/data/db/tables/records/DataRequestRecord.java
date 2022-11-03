/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.DataRequest;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DataRequestRecord extends UpdatableRecordImpl<DataRequestRecord> implements Record6<Long, Long, String, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.data_request.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.data_request.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.data_request.user_id</code>.
     */
    public void setUserId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.data_request.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.data_request.request_key</code>.
     */
    public void setRequestKey(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.data_request.request_key</code>.
     */
    public String getRequestKey() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.data_request.content</code>.
     */
    public void setContent(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.data_request.content</code>.
     */
    public String getContent() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.data_request.accessed</code>.
     */
    public void setAccessed(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.data_request.accessed</code>.
     */
    public Timestamp getAccessed() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>public.data_request.created</code>.
     */
    public void setCreated(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.data_request.created</code>.
     */
    public Timestamp getCreated() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, String, Timestamp, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, Long, String, String, Timestamp, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return DataRequest.DATA_REQUEST.ID;
    }

    @Override
    public Field<Long> field2() {
        return DataRequest.DATA_REQUEST.USER_ID;
    }

    @Override
    public Field<String> field3() {
        return DataRequest.DATA_REQUEST.REQUEST_KEY;
    }

    @Override
    public Field<String> field4() {
        return DataRequest.DATA_REQUEST.CONTENT;
    }

    @Override
    public Field<Timestamp> field5() {
        return DataRequest.DATA_REQUEST.ACCESSED;
    }

    @Override
    public Field<Timestamp> field6() {
        return DataRequest.DATA_REQUEST.CREATED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getUserId();
    }

    @Override
    public String component3() {
        return getRequestKey();
    }

    @Override
    public String component4() {
        return getContent();
    }

    @Override
    public Timestamp component5() {
        return getAccessed();
    }

    @Override
    public Timestamp component6() {
        return getCreated();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getUserId();
    }

    @Override
    public String value3() {
        return getRequestKey();
    }

    @Override
    public String value4() {
        return getContent();
    }

    @Override
    public Timestamp value5() {
        return getAccessed();
    }

    @Override
    public Timestamp value6() {
        return getCreated();
    }

    @Override
    public DataRequestRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public DataRequestRecord value2(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public DataRequestRecord value3(String value) {
        setRequestKey(value);
        return this;
    }

    @Override
    public DataRequestRecord value4(String value) {
        setContent(value);
        return this;
    }

    @Override
    public DataRequestRecord value5(Timestamp value) {
        setAccessed(value);
        return this;
    }

    @Override
    public DataRequestRecord value6(Timestamp value) {
        setCreated(value);
        return this;
    }

    @Override
    public DataRequestRecord values(Long value1, Long value2, String value3, String value4, Timestamp value5, Timestamp value6) {
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
     * Create a detached DataRequestRecord
     */
    public DataRequestRecord() {
        super(DataRequest.DATA_REQUEST);
    }

    /**
     * Create a detached, initialised DataRequestRecord
     */
    public DataRequestRecord(Long id, Long userId, String requestKey, String content, Timestamp accessed, Timestamp created) {
        super(DataRequest.DATA_REQUEST);

        setId(id);
        setUserId(userId);
        setRequestKey(requestKey);
        setContent(content);
        setAccessed(accessed);
        setCreated(created);
    }
}
