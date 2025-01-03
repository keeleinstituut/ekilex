/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.WordWordTypeRecord;

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
public class WordWordType extends TableImpl<WordWordTypeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.word_word_type</code>
     */
    public static final WordWordType WORD_WORD_TYPE = new WordWordType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordWordTypeRecord> getRecordType() {
        return WordWordTypeRecord.class;
    }

    /**
     * The column <code>public.word_word_type.id</code>.
     */
    public final TableField<WordWordTypeRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.word_word_type.word_id</code>.
     */
    public final TableField<WordWordTypeRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.word_word_type.word_type_code</code>.
     */
    public final TableField<WordWordTypeRecord, String> WORD_TYPE_CODE = createField(DSL.name("word_type_code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.word_word_type.order_by</code>.
     */
    public final TableField<WordWordTypeRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private WordWordType(Name alias, Table<WordWordTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordWordType(Name alias, Table<WordWordTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.word_word_type</code> table reference
     */
    public WordWordType(String alias) {
        this(DSL.name(alias), WORD_WORD_TYPE);
    }

    /**
     * Create an aliased <code>public.word_word_type</code> table reference
     */
    public WordWordType(Name alias) {
        this(alias, WORD_WORD_TYPE);
    }

    /**
     * Create a <code>public.word_word_type</code> table reference
     */
    public WordWordType() {
        this(DSL.name("word_word_type"), null);
    }

    public <O extends Record> WordWordType(Table<O> child, ForeignKey<O, WordWordTypeRecord> key) {
        super(child, key, WORD_WORD_TYPE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WordWordTypeRecord, Long> getIdentity() {
        return (Identity<WordWordTypeRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<WordWordTypeRecord> getPrimaryKey() {
        return Keys.WORD_WORD_TYPE_PKEY;
    }

    @Override
    public List<UniqueKey<WordWordTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<WordWordTypeRecord>>asList(Keys.WORD_WORD_TYPE_PKEY, Keys.WORD_WORD_TYPE_WORD_ID_WORD_TYPE_CODE_KEY);
    }

    @Override
    public List<ForeignKey<WordWordTypeRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordWordTypeRecord, ?>>asList(Keys.WORD_WORD_TYPE__WORD_WORD_TYPE_WORD_ID_FKEY, Keys.WORD_WORD_TYPE__WORD_WORD_TYPE_WORD_TYPE_CODE_FKEY);
    }

    private transient Word _word;
    private transient WordType _wordType;

    public Word word() {
        if (_word == null)
            _word = new Word(this, Keys.WORD_WORD_TYPE__WORD_WORD_TYPE_WORD_ID_FKEY);

        return _word;
    }

    public WordType wordType() {
        if (_wordType == null)
            _wordType = new WordType(this, Keys.WORD_WORD_TYPE__WORD_WORD_TYPE_WORD_TYPE_CODE_FKEY);

        return _wordType;
    }

    @Override
    public WordWordType as(String alias) {
        return new WordWordType(DSL.name(alias), this);
    }

    @Override
    public WordWordType as(Name alias) {
        return new WordWordType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordWordType rename(String name) {
        return new WordWordType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordWordType rename(Name name) {
        return new WordWordType(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
