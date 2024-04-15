/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.WordLastActivityLogFdwRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class WordLastActivityLogFdw extends TableImpl<WordLastActivityLogFdwRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.word_last_activity_log_fdw</code>
     */
    public static final WordLastActivityLogFdw WORD_LAST_ACTIVITY_LOG_FDW = new WordLastActivityLogFdw();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordLastActivityLogFdwRecord> getRecordType() {
        return WordLastActivityLogFdwRecord.class;
    }

    /**
     * The column <code>public.word_last_activity_log_fdw.id</code>.
     */
    public final TableField<WordLastActivityLogFdwRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.field("word_last_activity_log_id_func()", SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.word_last_activity_log_fdw.word_id</code>.
     */
    public final TableField<WordLastActivityLogFdwRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_last_activity_log_fdw.activity_log_id</code>.
     */
    public final TableField<WordLastActivityLogFdwRecord, Long> ACTIVITY_LOG_ID = createField(DSL.name("activity_log_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private WordLastActivityLogFdw(Name alias, Table<WordLastActivityLogFdwRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordLastActivityLogFdw(Name alias, Table<WordLastActivityLogFdwRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.word_last_activity_log_fdw</code> table reference
     */
    public WordLastActivityLogFdw(String alias) {
        this(DSL.name(alias), WORD_LAST_ACTIVITY_LOG_FDW);
    }

    /**
     * Create an aliased <code>public.word_last_activity_log_fdw</code> table reference
     */
    public WordLastActivityLogFdw(Name alias) {
        this(alias, WORD_LAST_ACTIVITY_LOG_FDW);
    }

    /**
     * Create a <code>public.word_last_activity_log_fdw</code> table reference
     */
    public WordLastActivityLogFdw() {
        this(DSL.name("word_last_activity_log_fdw"), null);
    }

    public <O extends Record> WordLastActivityLogFdw(Table<O> child, ForeignKey<O, WordLastActivityLogFdwRecord> key) {
        super(child, key, WORD_LAST_ACTIVITY_LOG_FDW);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public WordLastActivityLogFdw as(String alias) {
        return new WordLastActivityLogFdw(DSL.name(alias), this);
    }

    @Override
    public WordLastActivityLogFdw as(Name alias) {
        return new WordLastActivityLogFdw(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordLastActivityLogFdw rename(String name) {
        return new WordLastActivityLogFdw(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordLastActivityLogFdw rename(Name name) {
        return new WordLastActivityLogFdw(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
