/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.LexemeActivityLogIdFdwRecord;

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
public class LexemeActivityLogIdFdw extends TableImpl<LexemeActivityLogIdFdwRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.lexeme_activity_log_id_fdw</code>
     */
    public static final LexemeActivityLogIdFdw LEXEME_ACTIVITY_LOG_ID_FDW = new LexemeActivityLogIdFdw();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexemeActivityLogIdFdwRecord> getRecordType() {
        return LexemeActivityLogIdFdwRecord.class;
    }

    /**
     * The column <code>public.lexeme_activity_log_id_fdw.id</code>.
     */
    public final TableField<LexemeActivityLogIdFdwRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    private LexemeActivityLogIdFdw(Name alias, Table<LexemeActivityLogIdFdwRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexemeActivityLogIdFdw(Name alias, Table<LexemeActivityLogIdFdwRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.lexeme_activity_log_id_fdw</code> table reference
     */
    public LexemeActivityLogIdFdw(String alias) {
        this(DSL.name(alias), LEXEME_ACTIVITY_LOG_ID_FDW);
    }

    /**
     * Create an aliased <code>public.lexeme_activity_log_id_fdw</code> table reference
     */
    public LexemeActivityLogIdFdw(Name alias) {
        this(alias, LEXEME_ACTIVITY_LOG_ID_FDW);
    }

    /**
     * Create a <code>public.lexeme_activity_log_id_fdw</code> table reference
     */
    public LexemeActivityLogIdFdw() {
        this(DSL.name("lexeme_activity_log_id_fdw"), null);
    }

    public <O extends Record> LexemeActivityLogIdFdw(Table<O> child, ForeignKey<O, LexemeActivityLogIdFdwRecord> key) {
        super(child, key, LEXEME_ACTIVITY_LOG_ID_FDW);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public LexemeActivityLogIdFdw as(String alias) {
        return new LexemeActivityLogIdFdw(DSL.name(alias), this);
    }

    @Override
    public LexemeActivityLogIdFdw as(Name alias) {
        return new LexemeActivityLogIdFdw(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeActivityLogIdFdw rename(String name) {
        return new LexemeActivityLogIdFdw(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeActivityLogIdFdw rename(Name name) {
        return new LexemeActivityLogIdFdw(name, null);
    }

    // -------------------------------------------------------------------------
    // Row1 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row1<Long> fieldsRow() {
        return (Row1) super.fieldsRow();
    }
}
