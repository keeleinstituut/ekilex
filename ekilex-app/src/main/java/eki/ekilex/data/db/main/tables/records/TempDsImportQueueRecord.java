/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.TempDsImportQueue;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TempDsImportQueueRecord extends UpdatableRecordImpl<TempDsImportQueueRecord> implements Record5<Long, String, LocalDateTime, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.temp_ds_import_queue.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.temp_ds_import_queue.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.temp_ds_import_queue.import_code</code>.
     */
    public void setImportCode(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.temp_ds_import_queue.import_code</code>.
     */
    public String getImportCode() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.temp_ds_import_queue.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.temp_ds_import_queue.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>public.temp_ds_import_queue.table_name</code>.
     */
    public void setTableName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.temp_ds_import_queue.table_name</code>.
     */
    public String getTableName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.temp_ds_import_queue.content</code>.
     */
    public void setContent(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.temp_ds_import_queue.content</code>.
     */
    public String getContent() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, LocalDateTime, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, String, LocalDateTime, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TempDsImportQueue.TEMP_DS_IMPORT_QUEUE.ID;
    }

    @Override
    public Field<String> field2() {
        return TempDsImportQueue.TEMP_DS_IMPORT_QUEUE.IMPORT_CODE;
    }

    @Override
    public Field<LocalDateTime> field3() {
        return TempDsImportQueue.TEMP_DS_IMPORT_QUEUE.CREATED_ON;
    }

    @Override
    public Field<String> field4() {
        return TempDsImportQueue.TEMP_DS_IMPORT_QUEUE.TABLE_NAME;
    }

    @Override
    public Field<String> field5() {
        return TempDsImportQueue.TEMP_DS_IMPORT_QUEUE.CONTENT;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getImportCode();
    }

    @Override
    public LocalDateTime component3() {
        return getCreatedOn();
    }

    @Override
    public String component4() {
        return getTableName();
    }

    @Override
    public String component5() {
        return getContent();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getImportCode();
    }

    @Override
    public LocalDateTime value3() {
        return getCreatedOn();
    }

    @Override
    public String value4() {
        return getTableName();
    }

    @Override
    public String value5() {
        return getContent();
    }

    @Override
    public TempDsImportQueueRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public TempDsImportQueueRecord value2(String value) {
        setImportCode(value);
        return this;
    }

    @Override
    public TempDsImportQueueRecord value3(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public TempDsImportQueueRecord value4(String value) {
        setTableName(value);
        return this;
    }

    @Override
    public TempDsImportQueueRecord value5(String value) {
        setContent(value);
        return this;
    }

    @Override
    public TempDsImportQueueRecord values(Long value1, String value2, LocalDateTime value3, String value4, String value5) {
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
     * Create a detached TempDsImportQueueRecord
     */
    public TempDsImportQueueRecord() {
        super(TempDsImportQueue.TEMP_DS_IMPORT_QUEUE);
    }

    /**
     * Create a detached, initialised TempDsImportQueueRecord
     */
    public TempDsImportQueueRecord(Long id, String importCode, LocalDateTime createdOn, String tableName, String content) {
        super(TempDsImportQueue.TEMP_DS_IMPORT_QUEUE);

        setId(id);
        setImportCode(importCode);
        setCreatedOn(createdOn);
        setTableName(tableName);
        setContent(content);
    }
}
