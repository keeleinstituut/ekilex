/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.WordOdUsageRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row10;
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
public class WordOdUsage extends TableImpl<WordOdUsageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.word_od_usage</code>
     */
    public static final WordOdUsage WORD_OD_USAGE = new WordOdUsage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordOdUsageRecord> getRecordType() {
        return WordOdUsageRecord.class;
    }

    /**
     * The column <code>public.word_od_usage.id</code>.
     */
    public final TableField<WordOdUsageRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.word_od_usage.word_id</code>.
     */
    public final TableField<WordOdUsageRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_od_usage.value</code>.
     */
    public final TableField<WordOdUsageRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.word_od_usage.value_prese</code>.
     */
    public final TableField<WordOdUsageRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.word_od_usage.is_public</code>.
     */
    public final TableField<WordOdUsageRecord, Boolean> IS_PUBLIC = createField(DSL.name("is_public"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("true", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.word_od_usage.created_by</code>.
     */
    public final TableField<WordOdUsageRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word_od_usage.created_on</code>.
     */
    public final TableField<WordOdUsageRecord, LocalDateTime> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.word_od_usage.modified_by</code>.
     */
    public final TableField<WordOdUsageRecord, String> MODIFIED_BY = createField(DSL.name("modified_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word_od_usage.modified_on</code>.
     */
    public final TableField<WordOdUsageRecord, LocalDateTime> MODIFIED_ON = createField(DSL.name("modified_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.word_od_usage.order_by</code>.
     */
    public final TableField<WordOdUsageRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private WordOdUsage(Name alias, Table<WordOdUsageRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordOdUsage(Name alias, Table<WordOdUsageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.word_od_usage</code> table reference
     */
    public WordOdUsage(String alias) {
        this(DSL.name(alias), WORD_OD_USAGE);
    }

    /**
     * Create an aliased <code>public.word_od_usage</code> table reference
     */
    public WordOdUsage(Name alias) {
        this(alias, WORD_OD_USAGE);
    }

    /**
     * Create a <code>public.word_od_usage</code> table reference
     */
    public WordOdUsage() {
        this(DSL.name("word_od_usage"), null);
    }

    public <O extends Record> WordOdUsage(Table<O> child, ForeignKey<O, WordOdUsageRecord> key) {
        super(child, key, WORD_OD_USAGE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WordOdUsageRecord, Long> getIdentity() {
        return (Identity<WordOdUsageRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<WordOdUsageRecord> getPrimaryKey() {
        return Keys.WORD_OD_USAGE_PKEY;
    }

    @Override
    public List<UniqueKey<WordOdUsageRecord>> getKeys() {
        return Arrays.<UniqueKey<WordOdUsageRecord>>asList(Keys.WORD_OD_USAGE_PKEY);
    }

    @Override
    public List<ForeignKey<WordOdUsageRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordOdUsageRecord, ?>>asList(Keys.WORD_OD_USAGE__WORD_OD_USAGE_WORD_ID_FKEY);
    }

    private transient Word _word;

    public Word word() {
        if (_word == null)
            _word = new Word(this, Keys.WORD_OD_USAGE__WORD_OD_USAGE_WORD_ID_FKEY);

        return _word;
    }

    @Override
    public WordOdUsage as(String alias) {
        return new WordOdUsage(DSL.name(alias), this);
    }

    @Override
    public WordOdUsage as(Name alias) {
        return new WordOdUsage(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordOdUsage rename(String name) {
        return new WordOdUsage(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordOdUsage rename(Name name) {
        return new WordOdUsage(name, null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Long, Long, String, String, Boolean, String, LocalDateTime, String, LocalDateTime, Long> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
