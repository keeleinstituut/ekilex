/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.MeaningNrRecord;

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
public class MeaningNr extends TableImpl<MeaningNrRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.meaning_nr</code>
     */
    public static final MeaningNr MEANING_NR = new MeaningNr();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningNrRecord> getRecordType() {
        return MeaningNrRecord.class;
    }

    /**
     * The column <code>public.meaning_nr.id</code>.
     */
    public final TableField<MeaningNrRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.meaning_nr.meaning_id</code>.
     */
    public final TableField<MeaningNrRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_nr.mnr</code>.
     */
    public final TableField<MeaningNrRecord, String> MNR = createField(DSL.name("mnr"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_nr.dataset_code</code>.
     */
    public final TableField<MeaningNrRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    private MeaningNr(Name alias, Table<MeaningNrRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningNr(Name alias, Table<MeaningNrRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.meaning_nr</code> table reference
     */
    public MeaningNr(String alias) {
        this(DSL.name(alias), MEANING_NR);
    }

    /**
     * Create an aliased <code>public.meaning_nr</code> table reference
     */
    public MeaningNr(Name alias) {
        this(alias, MEANING_NR);
    }

    /**
     * Create a <code>public.meaning_nr</code> table reference
     */
    public MeaningNr() {
        this(DSL.name("meaning_nr"), null);
    }

    public <O extends Record> MeaningNr(Table<O> child, ForeignKey<O, MeaningNrRecord> key) {
        super(child, key, MEANING_NR);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MeaningNrRecord, Long> getIdentity() {
        return (Identity<MeaningNrRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<MeaningNrRecord> getPrimaryKey() {
        return Keys.MEANING_NR_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningNrRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningNrRecord>>asList(Keys.MEANING_NR_PKEY, Keys.MEANING_NR_MEANING_ID_MNR_DATASET_CODE_KEY);
    }

    @Override
    public List<ForeignKey<MeaningNrRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningNrRecord, ?>>asList(Keys.MEANING_NR__MEANING_NR_MEANING_ID_FKEY, Keys.MEANING_NR__MEANING_NR_DATASET_CODE_FKEY);
    }

    private transient Meaning _meaning;
    private transient Dataset _dataset;

    public Meaning meaning() {
        if (_meaning == null)
            _meaning = new Meaning(this, Keys.MEANING_NR__MEANING_NR_MEANING_ID_FKEY);

        return _meaning;
    }

    public Dataset dataset() {
        if (_dataset == null)
            _dataset = new Dataset(this, Keys.MEANING_NR__MEANING_NR_DATASET_CODE_FKEY);

        return _dataset;
    }

    @Override
    public MeaningNr as(String alias) {
        return new MeaningNr(DSL.name(alias), this);
    }

    @Override
    public MeaningNr as(Name alias) {
        return new MeaningNr(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningNr rename(String name) {
        return new MeaningNr(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningNr rename(Name name) {
        return new MeaningNr(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
