/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.MeaningLastActivityLogRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class MeaningLastActivityLog extends TableImpl<MeaningLastActivityLogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.meaning_last_activity_log</code>
     */
    public static final MeaningLastActivityLog MEANING_LAST_ACTIVITY_LOG = new MeaningLastActivityLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningLastActivityLogRecord> getRecordType() {
        return MeaningLastActivityLogRecord.class;
    }

    /**
     * The column <code>public.meaning_last_activity_log.id</code>.
     */
    public final TableField<MeaningLastActivityLogRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.meaning_last_activity_log.meaning_id</code>.
     */
    public final TableField<MeaningLastActivityLogRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_last_activity_log.activity_log_id</code>.
     */
    public final TableField<MeaningLastActivityLogRecord, Long> ACTIVITY_LOG_ID = createField(DSL.name("activity_log_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_last_activity_log.type</code>.
     */
    public final TableField<MeaningLastActivityLogRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    private MeaningLastActivityLog(Name alias, Table<MeaningLastActivityLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningLastActivityLog(Name alias, Table<MeaningLastActivityLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.meaning_last_activity_log</code> table reference
     */
    public MeaningLastActivityLog(String alias) {
        this(DSL.name(alias), MEANING_LAST_ACTIVITY_LOG);
    }

    /**
     * Create an aliased <code>public.meaning_last_activity_log</code> table reference
     */
    public MeaningLastActivityLog(Name alias) {
        this(alias, MEANING_LAST_ACTIVITY_LOG);
    }

    /**
     * Create a <code>public.meaning_last_activity_log</code> table reference
     */
    public MeaningLastActivityLog() {
        this(DSL.name("meaning_last_activity_log"), null);
    }

    public <O extends Record> MeaningLastActivityLog(Table<O> child, ForeignKey<O, MeaningLastActivityLogRecord> key) {
        super(child, key, MEANING_LAST_ACTIVITY_LOG);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MeaningLastActivityLogRecord, Long> getIdentity() {
        return (Identity<MeaningLastActivityLogRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<MeaningLastActivityLogRecord> getPrimaryKey() {
        return Keys.MEANING_LAST_ACTIVITY_LOG_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningLastActivityLogRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningLastActivityLogRecord>>asList(Keys.MEANING_LAST_ACTIVITY_LOG_PKEY, Keys.MEANING_LAST_ACTIVITY_LOG_MEANING_ID_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<MeaningLastActivityLogRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningLastActivityLogRecord, ?>>asList(Keys.MEANING_LAST_ACTIVITY_LOG__MEANING_LAST_ACTIVITY_LOG_MEANING_ID_FKEY, Keys.MEANING_LAST_ACTIVITY_LOG__MEANING_LAST_ACTIVITY_LOG_ACTIVITY_LOG_ID_FKEY);
    }

    private transient Meaning _meaning;
    private transient ActivityLog _activityLog;

    public Meaning meaning() {
        if (_meaning == null)
            _meaning = new Meaning(this, Keys.MEANING_LAST_ACTIVITY_LOG__MEANING_LAST_ACTIVITY_LOG_MEANING_ID_FKEY);

        return _meaning;
    }

    public ActivityLog activityLog() {
        if (_activityLog == null)
            _activityLog = new ActivityLog(this, Keys.MEANING_LAST_ACTIVITY_LOG__MEANING_LAST_ACTIVITY_LOG_ACTIVITY_LOG_ID_FKEY);

        return _activityLog;
    }

    @Override
    public MeaningLastActivityLog as(String alias) {
        return new MeaningLastActivityLog(DSL.name(alias), this);
    }

    @Override
    public MeaningLastActivityLog as(Name alias) {
        return new MeaningLastActivityLog(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningLastActivityLog rename(String name) {
        return new MeaningLastActivityLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningLastActivityLog rename(Name name) {
        return new MeaningLastActivityLog(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, Long, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
