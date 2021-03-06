/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Indexes;
import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.WordFreqRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WordFreq extends TableImpl<WordFreqRecord> {

    private static final long serialVersionUID = 982881434;

    /**
     * The reference instance of <code>public.word_freq</code>
     */
    public static final WordFreq WORD_FREQ = new WordFreq();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordFreqRecord> getRecordType() {
        return WordFreqRecord.class;
    }

    /**
     * The column <code>public.word_freq.id</code>.
     */
    public final TableField<WordFreqRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('word_freq_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.word_freq.freq_corp_id</code>.
     */
    public final TableField<WordFreqRecord, Long> FREQ_CORP_ID = createField(DSL.name("freq_corp_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_freq.word_id</code>.
     */
    public final TableField<WordFreqRecord, Long> WORD_ID = createField(DSL.name("word_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_freq.value</code>.
     */
    public final TableField<WordFreqRecord, BigDecimal> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.NUMERIC(12, 7).nullable(false), this, "");

    /**
     * The column <code>public.word_freq.rank</code>.
     */
    public final TableField<WordFreqRecord, Long> RANK = createField(DSL.name("rank"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>public.word_freq</code> table reference
     */
    public WordFreq() {
        this(DSL.name("word_freq"), null);
    }

    /**
     * Create an aliased <code>public.word_freq</code> table reference
     */
    public WordFreq(String alias) {
        this(DSL.name(alias), WORD_FREQ);
    }

    /**
     * Create an aliased <code>public.word_freq</code> table reference
     */
    public WordFreq(Name alias) {
        this(alias, WORD_FREQ);
    }

    private WordFreq(Name alias, Table<WordFreqRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordFreq(Name alias, Table<WordFreqRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> WordFreq(Table<O> child, ForeignKey<O, WordFreqRecord> key) {
        super(child, key, WORD_FREQ);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.WORD_FREQ_CORP_ID_IDX, Indexes.WORD_FREQ_RANK_IDX, Indexes.WORD_FREQ_VALUE_IDX, Indexes.WORD_FREQ_WORD_ID_IDX);
    }

    @Override
    public Identity<WordFreqRecord, Long> getIdentity() {
        return Keys.IDENTITY_WORD_FREQ;
    }

    @Override
    public UniqueKey<WordFreqRecord> getPrimaryKey() {
        return Keys.WORD_FREQ_PKEY;
    }

    @Override
    public List<UniqueKey<WordFreqRecord>> getKeys() {
        return Arrays.<UniqueKey<WordFreqRecord>>asList(Keys.WORD_FREQ_PKEY, Keys.WORD_FREQ_FREQ_CORP_ID_WORD_ID_KEY);
    }

    @Override
    public List<ForeignKey<WordFreqRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordFreqRecord, ?>>asList(Keys.WORD_FREQ__WORD_FREQ_FREQ_CORP_ID_FKEY, Keys.WORD_FREQ__WORD_FREQ_WORD_ID_FKEY);
    }

    public FreqCorp freqCorp() {
        return new FreqCorp(this, Keys.WORD_FREQ__WORD_FREQ_FREQ_CORP_ID_FKEY);
    }

    public Word word() {
        return new Word(this, Keys.WORD_FREQ__WORD_FREQ_WORD_ID_FKEY);
    }

    @Override
    public WordFreq as(String alias) {
        return new WordFreq(DSL.name(alias), this);
    }

    @Override
    public WordFreq as(Name alias) {
        return new WordFreq(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordFreq rename(String name) {
        return new WordFreq(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordFreq rename(Name name) {
        return new WordFreq(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, Long, BigDecimal, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
