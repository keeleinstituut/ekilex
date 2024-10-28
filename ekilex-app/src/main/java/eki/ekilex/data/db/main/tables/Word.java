/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.WordRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row14;
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
public class Word extends TableImpl<WordRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.word</code>
     */
    public static final Word WORD = new Word();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordRecord> getRecordType() {
        return WordRecord.class;
    }

    /**
     * The column <code>public.word.id</code>.
     */
    public final TableField<WordRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.word.lang</code>.
     */
    public final TableField<WordRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), this, "");

    /**
     * The column <code>public.word.homonym_nr</code>.
     */
    public final TableField<WordRecord, Integer> HOMONYM_NR = createField(DSL.name("homonym_nr"), SQLDataType.INTEGER.defaultValue(DSL.field("1", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.word.display_morph_code</code>.
     */
    public final TableField<WordRecord, String> DISPLAY_MORPH_CODE = createField(DSL.name("display_morph_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.word.gender_code</code>.
     */
    public final TableField<WordRecord, String> GENDER_CODE = createField(DSL.name("gender_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.word.aspect_code</code>.
     */
    public final TableField<WordRecord, String> ASPECT_CODE = createField(DSL.name("aspect_code"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.word.value</code>.
     */
    public final TableField<WordRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.word.value_prese</code>.
     */
    public final TableField<WordRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.word.value_as_word</code>.
     */
    public final TableField<WordRecord, String> VALUE_AS_WORD = createField(DSL.name("value_as_word"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word.vocal_form</code>.
     */
    public final TableField<WordRecord, String> VOCAL_FORM = createField(DSL.name("vocal_form"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word.manual_event_on</code>.
     */
    public final TableField<WordRecord, Timestamp> MANUAL_EVENT_ON = createField(DSL.name("manual_event_on"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.word.morphophono_form</code>.
     */
    public final TableField<WordRecord, String> MORPHOPHONO_FORM = createField(DSL.name("morphophono_form"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.word.is_public</code>.
     */
    public final TableField<WordRecord, Boolean> IS_PUBLIC = createField(DSL.name("is_public"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("true", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.word.morph_comment</code>.
     */
    public final TableField<WordRecord, String> MORPH_COMMENT = createField(DSL.name("morph_comment"), SQLDataType.CLOB, this, "");

    private Word(Name alias, Table<WordRecord> aliased) {
        this(alias, aliased, null);
    }

    private Word(Name alias, Table<WordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.word</code> table reference
     */
    public Word(String alias) {
        this(DSL.name(alias), WORD);
    }

    /**
     * Create an aliased <code>public.word</code> table reference
     */
    public Word(Name alias) {
        this(alias, WORD);
    }

    /**
     * Create a <code>public.word</code> table reference
     */
    public Word() {
        this(DSL.name("word"), null);
    }

    public <O extends Record> Word(Table<O> child, ForeignKey<O, WordRecord> key) {
        super(child, key, WORD);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WordRecord, Long> getIdentity() {
        return (Identity<WordRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<WordRecord> getPrimaryKey() {
        return Keys.WORD_PKEY;
    }

    @Override
    public List<UniqueKey<WordRecord>> getKeys() {
        return Arrays.<UniqueKey<WordRecord>>asList(Keys.WORD_PKEY);
    }

    @Override
    public List<ForeignKey<WordRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordRecord, ?>>asList(Keys.WORD__WORD_LANG_FKEY, Keys.WORD__WORD_DISPLAY_MORPH_CODE_FKEY, Keys.WORD__WORD_GENDER_CODE_FKEY, Keys.WORD__WORD_ASPECT_CODE_FKEY);
    }

    private transient Language _language;
    private transient DisplayMorph _displayMorph;
    private transient Gender _gender;
    private transient Aspect _aspect;

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.WORD__WORD_LANG_FKEY);

        return _language;
    }

    public DisplayMorph displayMorph() {
        if (_displayMorph == null)
            _displayMorph = new DisplayMorph(this, Keys.WORD__WORD_DISPLAY_MORPH_CODE_FKEY);

        return _displayMorph;
    }

    public Gender gender() {
        if (_gender == null)
            _gender = new Gender(this, Keys.WORD__WORD_GENDER_CODE_FKEY);

        return _gender;
    }

    public Aspect aspect() {
        if (_aspect == null)
            _aspect = new Aspect(this, Keys.WORD__WORD_ASPECT_CODE_FKEY);

        return _aspect;
    }

    @Override
    public Word as(String alias) {
        return new Word(DSL.name(alias), this);
    }

    @Override
    public Word as(Name alias) {
        return new Word(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Word rename(String name) {
        return new Word(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Word rename(Name name) {
        return new Word(name, null);
    }

    // -------------------------------------------------------------------------
    // Row14 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row14<Long, String, Integer, String, String, String, String, String, String, String, Timestamp, String, Boolean, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }
}
