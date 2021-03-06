/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables.records;


import eki.wordweb.data.db.tables.MviewWwMeaningFreeformSourceLink;
import eki.wordweb.data.db.udt.records.TypeSourceLinkRecord;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MviewWwMeaningFreeformSourceLinkRecord extends TableRecordImpl<MviewWwMeaningFreeformSourceLinkRecord> implements Record2<Long, TypeSourceLinkRecord[]> {

    private static final long serialVersionUID = -2106930336;

    /**
     * Setter for <code>public.mview_ww_meaning_freeform_source_link.meaning_id</code>.
     */
    public void setMeaningId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.mview_ww_meaning_freeform_source_link.meaning_id</code>.
     */
    public Long getMeaningId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.mview_ww_meaning_freeform_source_link.source_links</code>.
     */
    public void setSourceLinks(TypeSourceLinkRecord[] value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.mview_ww_meaning_freeform_source_link.source_links</code>.
     */
    public TypeSourceLinkRecord[] getSourceLinks() {
        return (TypeSourceLinkRecord[]) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, TypeSourceLinkRecord[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Long, TypeSourceLinkRecord[]> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MviewWwMeaningFreeformSourceLink.MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK.MEANING_ID;
    }

    @Override
    public Field<TypeSourceLinkRecord[]> field2() {
        return MviewWwMeaningFreeformSourceLink.MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK.SOURCE_LINKS;
    }

    @Override
    public Long component1() {
        return getMeaningId();
    }

    @Override
    public TypeSourceLinkRecord[] component2() {
        return getSourceLinks();
    }

    @Override
    public Long value1() {
        return getMeaningId();
    }

    @Override
    public TypeSourceLinkRecord[] value2() {
        return getSourceLinks();
    }

    @Override
    public MviewWwMeaningFreeformSourceLinkRecord value1(Long value) {
        setMeaningId(value);
        return this;
    }

    @Override
    public MviewWwMeaningFreeformSourceLinkRecord value2(TypeSourceLinkRecord[] value) {
        setSourceLinks(value);
        return this;
    }

    @Override
    public MviewWwMeaningFreeformSourceLinkRecord values(Long value1, TypeSourceLinkRecord[] value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MviewWwMeaningFreeformSourceLinkRecord
     */
    public MviewWwMeaningFreeformSourceLinkRecord() {
        super(MviewWwMeaningFreeformSourceLink.MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK);
    }

    /**
     * Create a detached, initialised MviewWwMeaningFreeformSourceLinkRecord
     */
    public MviewWwMeaningFreeformSourceLinkRecord(Long meaningId, TypeSourceLinkRecord[] sourceLinks) {
        super(MviewWwMeaningFreeformSourceLink.MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK);

        set(0, meaningId);
        set(1, sourceLinks);
    }
}
