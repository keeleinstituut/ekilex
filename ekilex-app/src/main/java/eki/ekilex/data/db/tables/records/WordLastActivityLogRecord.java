/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.WordLastActivityLog;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WordLastActivityLogRecord extends UpdatableRecordImpl<WordLastActivityLogRecord> implements Record3<Long, Long, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.word_last_activity_log.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.word_last_activity_log.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.word_last_activity_log.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.word_last_activity_log.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.word_last_activity_log.activity_log_id</code>.
     */
    public void setActivityLogId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.word_last_activity_log.activity_log_id</code>.
     */
    public Long getActivityLogId() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WordLastActivityLog.WORD_LAST_ACTIVITY_LOG.ID;
    }

    @Override
    public Field<Long> field2() {
        return WordLastActivityLog.WORD_LAST_ACTIVITY_LOG.WORD_ID;
    }

    @Override
    public Field<Long> field3() {
        return WordLastActivityLog.WORD_LAST_ACTIVITY_LOG.ACTIVITY_LOG_ID;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getWordId();
    }

    @Override
    public Long component3() {
        return getActivityLogId();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getWordId();
    }

    @Override
    public Long value3() {
        return getActivityLogId();
    }

    @Override
    public WordLastActivityLogRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public WordLastActivityLogRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public WordLastActivityLogRecord value3(Long value) {
        setActivityLogId(value);
        return this;
    }

    @Override
    public WordLastActivityLogRecord values(Long value1, Long value2, Long value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WordLastActivityLogRecord
     */
    public WordLastActivityLogRecord() {
        super(WordLastActivityLog.WORD_LAST_ACTIVITY_LOG);
    }

    /**
     * Create a detached, initialised WordLastActivityLogRecord
     */
    public WordLastActivityLogRecord(Long id, Long wordId, Long activityLogId) {
        super(WordLastActivityLog.WORD_LAST_ACTIVITY_LOG);

        setId(id);
        setWordId(wordId);
        setActivityLogId(activityLogId);
    }
}
