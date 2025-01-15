/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.WordEtymologySourceLinkRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class WordEtymologySourceLink extends TableImpl<WordEtymologySourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.word_etymology_source_link</code>
     */
    public static final WordEtymologySourceLink WORD_ETYMOLOGY_SOURCE_LINK = new WordEtymologySourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordEtymologySourceLinkRecord> getRecordType() {
        return WordEtymologySourceLinkRecord.class;
    }

    /**
     * The column <code>public.word_etymology_source_link.id</code>.
     */
    public final TableField<WordEtymologySourceLinkRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.word_etymology_source_link.word_etym_id</code>.
     */
    public final TableField<WordEtymologySourceLinkRecord, Long> WORD_ETYM_ID = createField(DSL.name("word_etym_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_etymology_source_link.source_id</code>.
     */
    public final TableField<WordEtymologySourceLinkRecord, Long> SOURCE_ID = createField(DSL.name("source_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_etymology_source_link.name</code>.
     */
    public final TableField<WordEtymologySourceLinkRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word_etymology_source_link.value</code>.
     */
    public final TableField<WordEtymologySourceLinkRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word_etymology_source_link.order_by</code>.
     */
    public final TableField<WordEtymologySourceLinkRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private WordEtymologySourceLink(Name alias, Table<WordEtymologySourceLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordEtymologySourceLink(Name alias, Table<WordEtymologySourceLinkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.word_etymology_source_link</code> table reference
     */
    public WordEtymologySourceLink(String alias) {
        this(DSL.name(alias), WORD_ETYMOLOGY_SOURCE_LINK);
    }

    /**
     * Create an aliased <code>public.word_etymology_source_link</code> table reference
     */
    public WordEtymologySourceLink(Name alias) {
        this(alias, WORD_ETYMOLOGY_SOURCE_LINK);
    }

    /**
     * Create a <code>public.word_etymology_source_link</code> table reference
     */
    public WordEtymologySourceLink() {
        this(DSL.name("word_etymology_source_link"), null);
    }

    public <O extends Record> WordEtymologySourceLink(Table<O> child, ForeignKey<O, WordEtymologySourceLinkRecord> key) {
        super(child, key, WORD_ETYMOLOGY_SOURCE_LINK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WordEtymologySourceLinkRecord, Long> getIdentity() {
        return (Identity<WordEtymologySourceLinkRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<WordEtymologySourceLinkRecord> getPrimaryKey() {
        return Keys.WORD_ETYMOLOGY_SOURCE_LINK_PKEY;
    }

    @Override
    public List<UniqueKey<WordEtymologySourceLinkRecord>> getKeys() {
        return Arrays.<UniqueKey<WordEtymologySourceLinkRecord>>asList(Keys.WORD_ETYMOLOGY_SOURCE_LINK_PKEY);
    }

    @Override
    public List<ForeignKey<WordEtymologySourceLinkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordEtymologySourceLinkRecord, ?>>asList(Keys.WORD_ETYMOLOGY_SOURCE_LINK__WORD_ETYMOLOGY_SOURCE_LINK_WORD_ETYM_ID_FKEY, Keys.WORD_ETYMOLOGY_SOURCE_LINK__WORD_ETYMOLOGY_SOURCE_LINK_SOURCE_ID_FKEY);
    }

    private transient WordEtymology _wordEtymology;
    private transient Source _source;

    public WordEtymology wordEtymology() {
        if (_wordEtymology == null)
            _wordEtymology = new WordEtymology(this, Keys.WORD_ETYMOLOGY_SOURCE_LINK__WORD_ETYMOLOGY_SOURCE_LINK_WORD_ETYM_ID_FKEY);

        return _wordEtymology;
    }

    public Source source() {
        if (_source == null)
            _source = new Source(this, Keys.WORD_ETYMOLOGY_SOURCE_LINK__WORD_ETYMOLOGY_SOURCE_LINK_SOURCE_ID_FKEY);

        return _source;
    }

    @Override
    public WordEtymologySourceLink as(String alias) {
        return new WordEtymologySourceLink(DSL.name(alias), this);
    }

    @Override
    public WordEtymologySourceLink as(Name alias) {
        return new WordEtymologySourceLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordEtymologySourceLink rename(String name) {
        return new WordEtymologySourceLink(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordEtymologySourceLink rename(Name name) {
        return new WordEtymologySourceLink(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, Long, String, String, Long> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
