/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.MviewWwMeaningFreeformSourceLinkRecord;
import eki.wordweb.data.db.udt.records.TypeSourceLinkRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MviewWwMeaningFreeformSourceLink extends TableImpl<MviewWwMeaningFreeformSourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.mview_ww_meaning_freeform_source_link</code>
     */
    public static final MviewWwMeaningFreeformSourceLink MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK = new MviewWwMeaningFreeformSourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MviewWwMeaningFreeformSourceLinkRecord> getRecordType() {
        return MviewWwMeaningFreeformSourceLinkRecord.class;
    }

    /**
     * The column <code>public.mview_ww_meaning_freeform_source_link.meaning_id</code>.
     */
    public final TableField<MviewWwMeaningFreeformSourceLinkRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_ww_meaning_freeform_source_link.source_links</code>.
     */
    public final TableField<MviewWwMeaningFreeformSourceLinkRecord, TypeSourceLinkRecord[]> SOURCE_LINKS = createField(DSL.name("source_links"), eki.wordweb.data.db.udt.TypeSourceLink.TYPE_SOURCE_LINK.getDataType().getArrayDataType(), this, "");

    private MviewWwMeaningFreeformSourceLink(Name alias, Table<MviewWwMeaningFreeformSourceLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private MviewWwMeaningFreeformSourceLink(Name alias, Table<MviewWwMeaningFreeformSourceLinkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.materializedView());
    }

    /**
     * Create an aliased <code>public.mview_ww_meaning_freeform_source_link</code> table reference
     */
    public MviewWwMeaningFreeformSourceLink(String alias) {
        this(DSL.name(alias), MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK);
    }

    /**
     * Create an aliased <code>public.mview_ww_meaning_freeform_source_link</code> table reference
     */
    public MviewWwMeaningFreeformSourceLink(Name alias) {
        this(alias, MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK);
    }

    /**
     * Create a <code>public.mview_ww_meaning_freeform_source_link</code> table reference
     */
    public MviewWwMeaningFreeformSourceLink() {
        this(DSL.name("mview_ww_meaning_freeform_source_link"), null);
    }

    public <O extends Record> MviewWwMeaningFreeformSourceLink(Table<O> child, ForeignKey<O, MviewWwMeaningFreeformSourceLinkRecord> key) {
        super(child, key, MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public MviewWwMeaningFreeformSourceLink as(String alias) {
        return new MviewWwMeaningFreeformSourceLink(DSL.name(alias), this);
    }

    @Override
    public MviewWwMeaningFreeformSourceLink as(Name alias) {
        return new MviewWwMeaningFreeformSourceLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewWwMeaningFreeformSourceLink rename(String name) {
        return new MviewWwMeaningFreeformSourceLink(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewWwMeaningFreeformSourceLink rename(Name name) {
        return new MviewWwMeaningFreeformSourceLink(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, TypeSourceLinkRecord[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
