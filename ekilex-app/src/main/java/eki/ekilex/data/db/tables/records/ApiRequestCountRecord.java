/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.ApiRequestCount;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ApiRequestCountRecord extends UpdatableRecordImpl<ApiRequestCountRecord> implements Record4<Long, String, String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.api_request_count.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.api_request_count.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.api_request_count.auth_name</code>.
     */
    public void setAuthName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.api_request_count.auth_name</code>.
     */
    public String getAuthName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.api_request_count.generic_path</code>.
     */
    public void setGenericPath(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.api_request_count.generic_path</code>.
     */
    public String getGenericPath() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.api_request_count.count</code>.
     */
    public void setCount(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.api_request_count.count</code>.
     */
    public Long getCount() {
        return (Long) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, String, String, Long> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ApiRequestCount.API_REQUEST_COUNT.ID;
    }

    @Override
    public Field<String> field2() {
        return ApiRequestCount.API_REQUEST_COUNT.AUTH_NAME;
    }

    @Override
    public Field<String> field3() {
        return ApiRequestCount.API_REQUEST_COUNT.GENERIC_PATH;
    }

    @Override
    public Field<Long> field4() {
        return ApiRequestCount.API_REQUEST_COUNT.COUNT;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getAuthName();
    }

    @Override
    public String component3() {
        return getGenericPath();
    }

    @Override
    public Long component4() {
        return getCount();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getAuthName();
    }

    @Override
    public String value3() {
        return getGenericPath();
    }

    @Override
    public Long value4() {
        return getCount();
    }

    @Override
    public ApiRequestCountRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public ApiRequestCountRecord value2(String value) {
        setAuthName(value);
        return this;
    }

    @Override
    public ApiRequestCountRecord value3(String value) {
        setGenericPath(value);
        return this;
    }

    @Override
    public ApiRequestCountRecord value4(Long value) {
        setCount(value);
        return this;
    }

    @Override
    public ApiRequestCountRecord values(Long value1, String value2, String value3, Long value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ApiRequestCountRecord
     */
    public ApiRequestCountRecord() {
        super(ApiRequestCount.API_REQUEST_COUNT);
    }

    /**
     * Create a detached, initialised ApiRequestCountRecord
     */
    public ApiRequestCountRecord(Long id, String authName, String genericPath, Long count) {
        super(ApiRequestCount.API_REQUEST_COUNT);

        setId(id);
        setAuthName(authName);
        setGenericPath(genericPath);
        setCount(count);
    }
}
