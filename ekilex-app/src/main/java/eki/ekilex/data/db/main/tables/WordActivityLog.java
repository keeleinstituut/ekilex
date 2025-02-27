/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.WordActivityLogRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WordActivityLog extends TableImpl<WordActivityLogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.word_activity_log</code>
     */
    public static final WordActivityLog WORD_ACTIVITY_LOG = new WordActivityLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordActivityLogRecord> getRecordType() {
        return WordActivityLogRecord.class;
    }

    /**
     * The column <code>public.word_activity_log.id</code>.
     */
    public final TableField<WordActivityLogRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.word_activity_log.word_id</code>.
     */
    public final TableField<WordActivityLogRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_activity_log.activity_log_id</code>.
     */
    public final TableField<WordActivityLogRecord, Long> ACTIVITY_LOG_ID = createField(DSL.name("activity_log_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private WordActivityLog(Name alias, Table<WordActivityLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordActivityLog(Name alias, Table<WordActivityLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.word_activity_log</code> table reference
     */
    public WordActivityLog(String alias) {
        this(DSL.name(alias), WORD_ACTIVITY_LOG);
    }

    /**
     * Create an aliased <code>public.word_activity_log</code> table reference
     */
    public WordActivityLog(Name alias) {
        this(alias, WORD_ACTIVITY_LOG);
    }

    /**
     * Create a <code>public.word_activity_log</code> table reference
     */
    public WordActivityLog() {
        this(DSL.name("word_activity_log"), null);
    }

    public <O extends Record> WordActivityLog(Table<O> child, ForeignKey<O, WordActivityLogRecord> key) {
        super(child, key, WORD_ACTIVITY_LOG);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WordActivityLogRecord, Long> getIdentity() {
        return (Identity<WordActivityLogRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<WordActivityLogRecord> getPrimaryKey() {
        return Keys.WORD_ACTIVITY_LOG_PKEY;
    }

    @Override
    public List<UniqueKey<WordActivityLogRecord>> getKeys() {
        return Arrays.<UniqueKey<WordActivityLogRecord>>asList(Keys.WORD_ACTIVITY_LOG_PKEY, Keys.WORD_ACTIVITY_LOG_WORD_ID_ACTIVITY_LOG_ID_KEY);
    }

    @Override
    public List<ForeignKey<WordActivityLogRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordActivityLogRecord, ?>>asList(Keys.WORD_ACTIVITY_LOG__WORD_ACTIVITY_LOG_WORD_ID_FKEY, Keys.WORD_ACTIVITY_LOG__WORD_ACTIVITY_LOG_ACTIVITY_LOG_ID_FKEY);
    }

    private transient Word _word;
    private transient ActivityLog _activityLog;

    public Word word() {
        if (_word == null)
            _word = new Word(this, Keys.WORD_ACTIVITY_LOG__WORD_ACTIVITY_LOG_WORD_ID_FKEY);

        return _word;
    }

    public ActivityLog activityLog() {
        if (_activityLog == null)
            _activityLog = new ActivityLog(this, Keys.WORD_ACTIVITY_LOG__WORD_ACTIVITY_LOG_ACTIVITY_LOG_ID_FKEY);

        return _activityLog;
    }

    @Override
    public WordActivityLog as(String alias) {
        return new WordActivityLog(DSL.name(alias), this);
    }

    @Override
    public WordActivityLog as(Name alias) {
        return new WordActivityLog(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordActivityLog rename(String name) {
        return new WordActivityLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordActivityLog rename(Name name) {
        return new WordActivityLog(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
