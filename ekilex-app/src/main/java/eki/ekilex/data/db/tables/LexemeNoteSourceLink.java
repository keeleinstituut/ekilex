/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.LexemeNoteSourceLinkRecord;

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
public class LexemeNoteSourceLink extends TableImpl<LexemeNoteSourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.lexeme_note_source_link</code>
     */
    public static final LexemeNoteSourceLink LEXEME_NOTE_SOURCE_LINK = new LexemeNoteSourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexemeNoteSourceLinkRecord> getRecordType() {
        return LexemeNoteSourceLinkRecord.class;
    }

    /**
     * The column <code>public.lexeme_note_source_link.id</code>.
     */
    public final TableField<LexemeNoteSourceLinkRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.lexeme_note_source_link.lexeme_note_id</code>.
     */
    public final TableField<LexemeNoteSourceLinkRecord, Long> LEXEME_NOTE_ID = createField(DSL.name("lexeme_note_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lexeme_note_source_link.source_id</code>.
     */
    public final TableField<LexemeNoteSourceLinkRecord, Long> SOURCE_ID = createField(DSL.name("source_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lexeme_note_source_link.type</code>.
     */
    public final TableField<LexemeNoteSourceLinkRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.lexeme_note_source_link.name</code>.
     */
    public final TableField<LexemeNoteSourceLinkRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.lexeme_note_source_link.order_by</code>.
     */
    public final TableField<LexemeNoteSourceLinkRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private LexemeNoteSourceLink(Name alias, Table<LexemeNoteSourceLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexemeNoteSourceLink(Name alias, Table<LexemeNoteSourceLinkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.lexeme_note_source_link</code> table reference
     */
    public LexemeNoteSourceLink(String alias) {
        this(DSL.name(alias), LEXEME_NOTE_SOURCE_LINK);
    }

    /**
     * Create an aliased <code>public.lexeme_note_source_link</code> table reference
     */
    public LexemeNoteSourceLink(Name alias) {
        this(alias, LEXEME_NOTE_SOURCE_LINK);
    }

    /**
     * Create a <code>public.lexeme_note_source_link</code> table reference
     */
    public LexemeNoteSourceLink() {
        this(DSL.name("lexeme_note_source_link"), null);
    }

    public <O extends Record> LexemeNoteSourceLink(Table<O> child, ForeignKey<O, LexemeNoteSourceLinkRecord> key) {
        super(child, key, LEXEME_NOTE_SOURCE_LINK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<LexemeNoteSourceLinkRecord, Long> getIdentity() {
        return (Identity<LexemeNoteSourceLinkRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<LexemeNoteSourceLinkRecord> getPrimaryKey() {
        return Keys.LEXEME_NOTE_SOURCE_LINK_PKEY;
    }

    @Override
    public List<UniqueKey<LexemeNoteSourceLinkRecord>> getKeys() {
        return Arrays.<UniqueKey<LexemeNoteSourceLinkRecord>>asList(Keys.LEXEME_NOTE_SOURCE_LINK_PKEY);
    }

    @Override
    public List<ForeignKey<LexemeNoteSourceLinkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LexemeNoteSourceLinkRecord, ?>>asList(Keys.LEXEME_NOTE_SOURCE_LINK__LEXEME_NOTE_SOURCE_LINK_LEXEME_NOTE_ID_FKEY, Keys.LEXEME_NOTE_SOURCE_LINK__LEXEME_NOTE_SOURCE_LINK_SOURCE_ID_FKEY);
    }

    private transient LexemeNote _lexemeNote;
    private transient Source _source;

    public LexemeNote lexemeNote() {
        if (_lexemeNote == null)
            _lexemeNote = new LexemeNote(this, Keys.LEXEME_NOTE_SOURCE_LINK__LEXEME_NOTE_SOURCE_LINK_LEXEME_NOTE_ID_FKEY);

        return _lexemeNote;
    }

    public Source source() {
        if (_source == null)
            _source = new Source(this, Keys.LEXEME_NOTE_SOURCE_LINK__LEXEME_NOTE_SOURCE_LINK_SOURCE_ID_FKEY);

        return _source;
    }

    @Override
    public LexemeNoteSourceLink as(String alias) {
        return new LexemeNoteSourceLink(DSL.name(alias), this);
    }

    @Override
    public LexemeNoteSourceLink as(Name alias) {
        return new LexemeNoteSourceLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeNoteSourceLink rename(String name) {
        return new LexemeNoteSourceLink(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeNoteSourceLink rename(Name name) {
        return new LexemeNoteSourceLink(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, Long, String, String, Long> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
