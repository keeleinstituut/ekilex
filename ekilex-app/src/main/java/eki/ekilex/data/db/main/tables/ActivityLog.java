/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ActivityLogRecord;
import eki.ekilex.data.db.main.udt.records.TypeActivityLogDiffRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row11;
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
public class ActivityLog extends TableImpl<ActivityLogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.activity_log</code>
     */
    public static final ActivityLog ACTIVITY_LOG = new ActivityLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActivityLogRecord> getRecordType() {
        return ActivityLogRecord.class;
    }

    /**
     * The column <code>public.activity_log.id</code>.
     */
    public final TableField<ActivityLogRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.activity_log.event_by</code>.
     */
    public final TableField<ActivityLogRecord, String> EVENT_BY = createField(DSL.name("event_by"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.activity_log.event_on</code>.
     */
    public final TableField<ActivityLogRecord, LocalDateTime> EVENT_ON = createField(DSL.name("event_on"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("statement_timestamp()", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>public.activity_log.funct_name</code>.
     */
    public final TableField<ActivityLogRecord, String> FUNCT_NAME = createField(DSL.name("funct_name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.activity_log.owner_id</code>.
     */
    public final TableField<ActivityLogRecord, Long> OWNER_ID = createField(DSL.name("owner_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.activity_log.owner_name</code>.
     */
    public final TableField<ActivityLogRecord, String> OWNER_NAME = createField(DSL.name("owner_name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.activity_log.entity_id</code>.
     */
    public final TableField<ActivityLogRecord, Long> ENTITY_ID = createField(DSL.name("entity_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.activity_log.entity_name</code>.
     */
    public final TableField<ActivityLogRecord, String> ENTITY_NAME = createField(DSL.name("entity_name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.activity_log.prev_diffs</code>.
     */
    public final TableField<ActivityLogRecord, TypeActivityLogDiffRecord[]> PREV_DIFFS = createField(DSL.name("prev_diffs"), eki.ekilex.data.db.main.udt.TypeActivityLogDiff.TYPE_ACTIVITY_LOG_DIFF.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.activity_log.curr_diffs</code>.
     */
    public final TableField<ActivityLogRecord, TypeActivityLogDiffRecord[]> CURR_DIFFS = createField(DSL.name("curr_diffs"), eki.ekilex.data.db.main.udt.TypeActivityLogDiff.TYPE_ACTIVITY_LOG_DIFF.getDataType().getArrayDataType(), this, "");

    /**
     * The column <code>public.activity_log.dataset_code</code>.
     */
    public final TableField<ActivityLogRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), SQLDataType.VARCHAR(10), this, "");

    private ActivityLog(Name alias, Table<ActivityLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private ActivityLog(Name alias, Table<ActivityLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.activity_log</code> table reference
     */
    public ActivityLog(String alias) {
        this(DSL.name(alias), ACTIVITY_LOG);
    }

    /**
     * Create an aliased <code>public.activity_log</code> table reference
     */
    public ActivityLog(Name alias) {
        this(alias, ACTIVITY_LOG);
    }

    /**
     * Create a <code>public.activity_log</code> table reference
     */
    public ActivityLog() {
        this(DSL.name("activity_log"), null);
    }

    public <O extends Record> ActivityLog(Table<O> child, ForeignKey<O, ActivityLogRecord> key) {
        super(child, key, ACTIVITY_LOG);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<ActivityLogRecord, Long> getIdentity() {
        return (Identity<ActivityLogRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ActivityLogRecord> getPrimaryKey() {
        return Keys.ACTIVITY_LOG_PKEY;
    }

    @Override
    public List<UniqueKey<ActivityLogRecord>> getKeys() {
        return Arrays.<UniqueKey<ActivityLogRecord>>asList(Keys.ACTIVITY_LOG_PKEY);
    }

    @Override
    public ActivityLog as(String alias) {
        return new ActivityLog(DSL.name(alias), this);
    }

    @Override
    public ActivityLog as(Name alias) {
        return new ActivityLog(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityLog rename(String name) {
        return new ActivityLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityLog rename(Name name) {
        return new ActivityLog(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, String, LocalDateTime, String, Long, String, Long, String, TypeActivityLogDiffRecord[], TypeActivityLogDiffRecord[], String> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
