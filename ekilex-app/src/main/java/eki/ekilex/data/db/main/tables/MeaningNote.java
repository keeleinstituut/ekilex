/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.MeaningNoteRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row13;
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
public class MeaningNote extends TableImpl<MeaningNoteRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.meaning_note</code>
     */
    public static final MeaningNote MEANING_NOTE = new MeaningNote();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningNoteRecord> getRecordType() {
        return MeaningNoteRecord.class;
    }

    /**
     * The column <code>public.meaning_note.id</code>.
     */
    public final TableField<MeaningNoteRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.meaning_note.original_freeform_id</code>.
     */
    public final TableField<MeaningNoteRecord, Long> ORIGINAL_FREEFORM_ID = createField(DSL.name("original_freeform_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.meaning_note.meaning_id</code>.
     */
    public final TableField<MeaningNoteRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_note.value</code>.
     */
    public final TableField<MeaningNoteRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.meaning_note.value_prese</code>.
     */
    public final TableField<MeaningNoteRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.meaning_note.lang</code>.
     */
    public final TableField<MeaningNoteRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.meaning_note.complexity</code>.
     */
    public final TableField<MeaningNoteRecord, String> COMPLEXITY = createField(DSL.name("complexity"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_note.is_public</code>.
     */
    public final TableField<MeaningNoteRecord, Boolean> IS_PUBLIC = createField(DSL.name("is_public"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("true", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.meaning_note.created_by</code>.
     */
    public final TableField<MeaningNoteRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_note.created_on</code>.
     */
    public final TableField<MeaningNoteRecord, Timestamp> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.meaning_note.modified_by</code>.
     */
    public final TableField<MeaningNoteRecord, String> MODIFIED_BY = createField(DSL.name("modified_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_note.modified_on</code>.
     */
    public final TableField<MeaningNoteRecord, Timestamp> MODIFIED_ON = createField(DSL.name("modified_on"), SQLDataType.TIMESTAMP(6), this, "");

    /**
     * The column <code>public.meaning_note.order_by</code>.
     */
    public final TableField<MeaningNoteRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private MeaningNote(Name alias, Table<MeaningNoteRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningNote(Name alias, Table<MeaningNoteRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.meaning_note</code> table reference
     */
    public MeaningNote(String alias) {
        this(DSL.name(alias), MEANING_NOTE);
    }

    /**
     * Create an aliased <code>public.meaning_note</code> table reference
     */
    public MeaningNote(Name alias) {
        this(alias, MEANING_NOTE);
    }

    /**
     * Create a <code>public.meaning_note</code> table reference
     */
    public MeaningNote() {
        this(DSL.name("meaning_note"), null);
    }

    public <O extends Record> MeaningNote(Table<O> child, ForeignKey<O, MeaningNoteRecord> key) {
        super(child, key, MEANING_NOTE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MeaningNoteRecord, Long> getIdentity() {
        return (Identity<MeaningNoteRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<MeaningNoteRecord> getPrimaryKey() {
        return Keys.MEANING_NOTE_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningNoteRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningNoteRecord>>asList(Keys.MEANING_NOTE_PKEY);
    }

    @Override
    public List<ForeignKey<MeaningNoteRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningNoteRecord, ?>>asList(Keys.MEANING_NOTE__MEANING_NOTE_MEANING_ID_FKEY, Keys.MEANING_NOTE__MEANING_NOTE_LANG_FKEY);
    }

    private transient Meaning _meaning;
    private transient Language _language;

    public Meaning meaning() {
        if (_meaning == null)
            _meaning = new Meaning(this, Keys.MEANING_NOTE__MEANING_NOTE_MEANING_ID_FKEY);

        return _meaning;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.MEANING_NOTE__MEANING_NOTE_LANG_FKEY);

        return _language;
    }

    @Override
    public MeaningNote as(String alias) {
        return new MeaningNote(DSL.name(alias), this);
    }

    @Override
    public MeaningNote as(Name alias) {
        return new MeaningNote(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningNote rename(String name) {
        return new MeaningNote(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningNote rename(Name name) {
        return new MeaningNote(name, null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, Long, Long, String, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> fieldsRow() {
        return (Row13) super.fieldsRow();
    }
}