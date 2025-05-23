/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.MeaningNoteSourceLinkRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
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
public class MeaningNoteSourceLink extends TableImpl<MeaningNoteSourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.meaning_note_source_link</code>
     */
    public static final MeaningNoteSourceLink MEANING_NOTE_SOURCE_LINK = new MeaningNoteSourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningNoteSourceLinkRecord> getRecordType() {
        return MeaningNoteSourceLinkRecord.class;
    }

    /**
     * The column <code>public.meaning_note_source_link.id</code>.
     */
    public final TableField<MeaningNoteSourceLinkRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.meaning_note_source_link.meaning_note_id</code>.
     */
    public final TableField<MeaningNoteSourceLinkRecord, Long> MEANING_NOTE_ID = createField(DSL.name("meaning_note_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_note_source_link.source_id</code>.
     */
    public final TableField<MeaningNoteSourceLinkRecord, Long> SOURCE_ID = createField(DSL.name("source_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_note_source_link.name</code>.
     */
    public final TableField<MeaningNoteSourceLinkRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_note_source_link.order_by</code>.
     */
    public final TableField<MeaningNoteSourceLinkRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private MeaningNoteSourceLink(Name alias, Table<MeaningNoteSourceLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningNoteSourceLink(Name alias, Table<MeaningNoteSourceLinkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.meaning_note_source_link</code> table reference
     */
    public MeaningNoteSourceLink(String alias) {
        this(DSL.name(alias), MEANING_NOTE_SOURCE_LINK);
    }

    /**
     * Create an aliased <code>public.meaning_note_source_link</code> table reference
     */
    public MeaningNoteSourceLink(Name alias) {
        this(alias, MEANING_NOTE_SOURCE_LINK);
    }

    /**
     * Create a <code>public.meaning_note_source_link</code> table reference
     */
    public MeaningNoteSourceLink() {
        this(DSL.name("meaning_note_source_link"), null);
    }

    public <O extends Record> MeaningNoteSourceLink(Table<O> child, ForeignKey<O, MeaningNoteSourceLinkRecord> key) {
        super(child, key, MEANING_NOTE_SOURCE_LINK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MeaningNoteSourceLinkRecord, Long> getIdentity() {
        return (Identity<MeaningNoteSourceLinkRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<MeaningNoteSourceLinkRecord> getPrimaryKey() {
        return Keys.MEANING_NOTE_SOURCE_LINK_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningNoteSourceLinkRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningNoteSourceLinkRecord>>asList(Keys.MEANING_NOTE_SOURCE_LINK_PKEY);
    }

    @Override
    public List<ForeignKey<MeaningNoteSourceLinkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningNoteSourceLinkRecord, ?>>asList(Keys.MEANING_NOTE_SOURCE_LINK__MEANING_NOTE_SOURCE_LINK_MEANING_NOTE_ID_FKEY, Keys.MEANING_NOTE_SOURCE_LINK__MEANING_NOTE_SOURCE_LINK_SOURCE_ID_FKEY);
    }

    private transient MeaningNote _meaningNote;
    private transient Source _source;

    public MeaningNote meaningNote() {
        if (_meaningNote == null)
            _meaningNote = new MeaningNote(this, Keys.MEANING_NOTE_SOURCE_LINK__MEANING_NOTE_SOURCE_LINK_MEANING_NOTE_ID_FKEY);

        return _meaningNote;
    }

    public Source source() {
        if (_source == null)
            _source = new Source(this, Keys.MEANING_NOTE_SOURCE_LINK__MEANING_NOTE_SOURCE_LINK_SOURCE_ID_FKEY);

        return _source;
    }

    @Override
    public MeaningNoteSourceLink as(String alias) {
        return new MeaningNoteSourceLink(DSL.name(alias), this);
    }

    @Override
    public MeaningNoteSourceLink as(Name alias) {
        return new MeaningNoteSourceLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningNoteSourceLink rename(String name) {
        return new MeaningNoteSourceLink(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningNoteSourceLink rename(Name name) {
        return new MeaningNoteSourceLink(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, Long, String, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
