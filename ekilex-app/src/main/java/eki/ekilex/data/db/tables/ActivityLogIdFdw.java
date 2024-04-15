/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ActivityLogIdFdwRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row1;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActivityLogIdFdw extends TableImpl<ActivityLogIdFdwRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.activity_log_id_fdw</code>
     */
    public static final ActivityLogIdFdw ACTIVITY_LOG_ID_FDW = new ActivityLogIdFdw();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActivityLogIdFdwRecord> getRecordType() {
        return ActivityLogIdFdwRecord.class;
    }

    /**
     * The column <code>public.activity_log_id_fdw.id</code>.
     */
    public final TableField<ActivityLogIdFdwRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    private ActivityLogIdFdw(Name alias, Table<ActivityLogIdFdwRecord> aliased) {
        this(alias, aliased, null);
    }

    private ActivityLogIdFdw(Name alias, Table<ActivityLogIdFdwRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.activity_log_id_fdw</code> table reference
     */
    public ActivityLogIdFdw(String alias) {
        this(DSL.name(alias), ACTIVITY_LOG_ID_FDW);
    }

    /**
     * Create an aliased <code>public.activity_log_id_fdw</code> table reference
     */
    public ActivityLogIdFdw(Name alias) {
        this(alias, ACTIVITY_LOG_ID_FDW);
    }

    /**
     * Create a <code>public.activity_log_id_fdw</code> table reference
     */
    public ActivityLogIdFdw() {
        this(DSL.name("activity_log_id_fdw"), null);
    }

    public <O extends Record> ActivityLogIdFdw(Table<O> child, ForeignKey<O, ActivityLogIdFdwRecord> key) {
        super(child, key, ACTIVITY_LOG_ID_FDW);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ActivityLogIdFdw as(String alias) {
        return new ActivityLogIdFdw(DSL.name(alias), this);
    }

    @Override
    public ActivityLogIdFdw as(Name alias) {
        return new ActivityLogIdFdw(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityLogIdFdw rename(String name) {
        return new ActivityLogIdFdw(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityLogIdFdw rename(Name name) {
        return new ActivityLogIdFdw(name, null);
    }

    // -------------------------------------------------------------------------
    // Row1 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row1<Long> fieldsRow() {
        return (Row1) super.fieldsRow();
    }
}
