/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.FreqCorpRecord;

import java.time.LocalDate;
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
public class FreqCorp extends TableImpl<FreqCorpRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.freq_corp</code>
     */
    public static final FreqCorp FREQ_CORP = new FreqCorp();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FreqCorpRecord> getRecordType() {
        return FreqCorpRecord.class;
    }

    /**
     * The column <code>public.freq_corp.id</code>.
     */
    public final TableField<FreqCorpRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.freq_corp.name</code>.
     */
    public final TableField<FreqCorpRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.freq_corp.corp_date</code>.
     */
    public final TableField<FreqCorpRecord, LocalDate> CORP_DATE = createField(DSL.name("corp_date"), SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>public.freq_corp.is_public</code>.
     */
    public final TableField<FreqCorpRecord, Boolean> IS_PUBLIC = createField(DSL.name("is_public"), SQLDataType.BOOLEAN.nullable(false), this, "");

    private FreqCorp(Name alias, Table<FreqCorpRecord> aliased) {
        this(alias, aliased, null);
    }

    private FreqCorp(Name alias, Table<FreqCorpRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.freq_corp</code> table reference
     */
    public FreqCorp(String alias) {
        this(DSL.name(alias), FREQ_CORP);
    }

    /**
     * Create an aliased <code>public.freq_corp</code> table reference
     */
    public FreqCorp(Name alias) {
        this(alias, FREQ_CORP);
    }

    /**
     * Create a <code>public.freq_corp</code> table reference
     */
    public FreqCorp() {
        this(DSL.name("freq_corp"), null);
    }

    public <O extends Record> FreqCorp(Table<O> child, ForeignKey<O, FreqCorpRecord> key) {
        super(child, key, FREQ_CORP);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<FreqCorpRecord, Long> getIdentity() {
        return (Identity<FreqCorpRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<FreqCorpRecord> getPrimaryKey() {
        return Keys.FREQ_CORP_PKEY;
    }

    @Override
    public List<UniqueKey<FreqCorpRecord>> getKeys() {
        return Arrays.<UniqueKey<FreqCorpRecord>>asList(Keys.FREQ_CORP_PKEY, Keys.FREQ_CORP_NAME_KEY);
    }

    @Override
    public FreqCorp as(String alias) {
        return new FreqCorp(DSL.name(alias), this);
    }

    @Override
    public FreqCorp as(Name alias) {
        return new FreqCorp(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FreqCorp rename(String name) {
        return new FreqCorp(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FreqCorp rename(Name name) {
        return new FreqCorp(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, LocalDate, Boolean> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
