/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.MviewWwDatasetRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
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
public class MviewWwDataset extends TableImpl<MviewWwDatasetRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.mview_ww_dataset</code>
     */
    public static final MviewWwDataset MVIEW_WW_DATASET = new MviewWwDataset();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MviewWwDatasetRecord> getRecordType() {
        return MviewWwDatasetRecord.class;
    }

    /**
     * The column <code>public.mview_ww_dataset.code</code>.
     */
    public final TableField<MviewWwDatasetRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.mview_ww_dataset.type</code>.
     */
    public final TableField<MviewWwDatasetRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(10), this, "");

    /**
     * The column <code>public.mview_ww_dataset.name</code>.
     */
    public final TableField<MviewWwDatasetRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_ww_dataset.description</code>.
     */
    public final TableField<MviewWwDatasetRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_ww_dataset.contact</code>.
     */
    public final TableField<MviewWwDatasetRecord, String> CONTACT = createField(DSL.name("contact"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_ww_dataset.image_url</code>.
     */
    public final TableField<MviewWwDatasetRecord, String> IMAGE_URL = createField(DSL.name("image_url"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_ww_dataset.is_superior</code>.
     */
    public final TableField<MviewWwDatasetRecord, Boolean> IS_SUPERIOR = createField(DSL.name("is_superior"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.mview_ww_dataset.order_by</code>.
     */
    public final TableField<MviewWwDatasetRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT, this, "");

    private MviewWwDataset(Name alias, Table<MviewWwDatasetRecord> aliased) {
        this(alias, aliased, null);
    }

    private MviewWwDataset(Name alias, Table<MviewWwDatasetRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.materializedView());
    }

    /**
     * Create an aliased <code>public.mview_ww_dataset</code> table reference
     */
    public MviewWwDataset(String alias) {
        this(DSL.name(alias), MVIEW_WW_DATASET);
    }

    /**
     * Create an aliased <code>public.mview_ww_dataset</code> table reference
     */
    public MviewWwDataset(Name alias) {
        this(alias, MVIEW_WW_DATASET);
    }

    /**
     * Create a <code>public.mview_ww_dataset</code> table reference
     */
    public MviewWwDataset() {
        this(DSL.name("mview_ww_dataset"), null);
    }

    public <O extends Record> MviewWwDataset(Table<O> child, ForeignKey<O, MviewWwDatasetRecord> key) {
        super(child, key, MVIEW_WW_DATASET);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public MviewWwDataset as(String alias) {
        return new MviewWwDataset(DSL.name(alias), this);
    }

    @Override
    public MviewWwDataset as(Name alias) {
        return new MviewWwDataset(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewWwDataset rename(String name) {
        return new MviewWwDataset(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewWwDataset rename(Name name) {
        return new MviewWwDataset(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<String, String, String, String, String, String, Boolean, Long> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
