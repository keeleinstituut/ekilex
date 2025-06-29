/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.MviewOdWordOdMorphRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class MviewOdWordOdMorph extends TableImpl<MviewOdWordOdMorphRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.mview_od_word_od_morph</code>
     */
    public static final MviewOdWordOdMorph MVIEW_OD_WORD_OD_MORPH = new MviewOdWordOdMorph();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MviewOdWordOdMorphRecord> getRecordType() {
        return MviewOdWordOdMorphRecord.class;
    }

    /**
     * The column <code>public.mview_od_word_od_morph.word_id</code>.
     */
    public final TableField<MviewOdWordOdMorphRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_od_word_od_morph.word_od_morph_id</code>.
     */
    public final TableField<MviewOdWordOdMorphRecord, Long> WORD_OD_MORPH_ID = createField(DSL.name("word_od_morph_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_od_word_od_morph.value</code>.
     */
    public final TableField<MviewOdWordOdMorphRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_od_word_od_morph.value_prese</code>.
     */
    public final TableField<MviewOdWordOdMorphRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB, this, "");

    private MviewOdWordOdMorph(Name alias, Table<MviewOdWordOdMorphRecord> aliased) {
        this(alias, aliased, null);
    }

    private MviewOdWordOdMorph(Name alias, Table<MviewOdWordOdMorphRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.materializedView());
    }

    /**
     * Create an aliased <code>public.mview_od_word_od_morph</code> table reference
     */
    public MviewOdWordOdMorph(String alias) {
        this(DSL.name(alias), MVIEW_OD_WORD_OD_MORPH);
    }

    /**
     * Create an aliased <code>public.mview_od_word_od_morph</code> table reference
     */
    public MviewOdWordOdMorph(Name alias) {
        this(alias, MVIEW_OD_WORD_OD_MORPH);
    }

    /**
     * Create a <code>public.mview_od_word_od_morph</code> table reference
     */
    public MviewOdWordOdMorph() {
        this(DSL.name("mview_od_word_od_morph"), null);
    }

    public <O extends Record> MviewOdWordOdMorph(Table<O> child, ForeignKey<O, MviewOdWordOdMorphRecord> key) {
        super(child, key, MVIEW_OD_WORD_OD_MORPH);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public MviewOdWordOdMorph as(String alias) {
        return new MviewOdWordOdMorph(DSL.name(alias), this);
    }

    @Override
    public MviewOdWordOdMorph as(Name alias) {
        return new MviewOdWordOdMorph(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewOdWordOdMorph rename(String name) {
        return new MviewOdWordOdMorph(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewOdWordOdMorph rename(Name name) {
        return new MviewOdWordOdMorph(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
