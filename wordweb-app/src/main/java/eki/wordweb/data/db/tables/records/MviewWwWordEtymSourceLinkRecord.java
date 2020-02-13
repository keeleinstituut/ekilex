/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables.records;


import eki.wordweb.data.db.tables.MviewWwWordEtymSourceLink;
import eki.wordweb.data.db.udt.records.TypeSourceLinkRecord;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MviewWwWordEtymSourceLinkRecord extends TableRecordImpl<MviewWwWordEtymSourceLinkRecord> implements Record2<Long, TypeSourceLinkRecord[]> {

    private static final long serialVersionUID = 2008312254;

    /**
     * Setter for <code>public.mview_ww_word_etym_source_link.word_id</code>.
     */
    public void setWordId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.mview_ww_word_etym_source_link.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.mview_ww_word_etym_source_link.source_links</code>.
     */
    public void setSourceLinks(TypeSourceLinkRecord... value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.mview_ww_word_etym_source_link.source_links</code>.
     */
    public TypeSourceLinkRecord[] getSourceLinks() {
        return (TypeSourceLinkRecord[]) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Long, TypeSourceLinkRecord[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Long, TypeSourceLinkRecord[]> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return MviewWwWordEtymSourceLink.MVIEW_WW_WORD_ETYM_SOURCE_LINK.WORD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<TypeSourceLinkRecord[]> field2() {
        return MviewWwWordEtymSourceLink.MVIEW_WW_WORD_ETYM_SOURCE_LINK.SOURCE_LINKS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getWordId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSourceLinkRecord[] component2() {
        return getSourceLinks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getWordId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSourceLinkRecord[] value2() {
        return getSourceLinks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MviewWwWordEtymSourceLinkRecord value1(Long value) {
        setWordId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MviewWwWordEtymSourceLinkRecord value2(TypeSourceLinkRecord... value) {
        setSourceLinks(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MviewWwWordEtymSourceLinkRecord values(Long value1, TypeSourceLinkRecord[] value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MviewWwWordEtymSourceLinkRecord
     */
    public MviewWwWordEtymSourceLinkRecord() {
        super(MviewWwWordEtymSourceLink.MVIEW_WW_WORD_ETYM_SOURCE_LINK);
    }

    /**
     * Create a detached, initialised MviewWwWordEtymSourceLinkRecord
     */
    public MviewWwWordEtymSourceLinkRecord(Long wordId, TypeSourceLinkRecord[] sourceLinks) {
        super(MviewWwWordEtymSourceLink.MVIEW_WW_WORD_ETYM_SOURCE_LINK);

        set(0, wordId);
        set(1, sourceLinks);
    }
}
