/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.MviewWwCollocPosGroupRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class MviewWwCollocPosGroup extends TableImpl<MviewWwCollocPosGroupRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.mview_ww_colloc_pos_group</code>
     */
    public static final MviewWwCollocPosGroup MVIEW_WW_COLLOC_POS_GROUP = new MviewWwCollocPosGroup();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MviewWwCollocPosGroupRecord> getRecordType() {
        return MviewWwCollocPosGroupRecord.class;
    }

    /**
     * The column <code>public.mview_ww_colloc_pos_group.lexeme_id</code>.
     */
    public final TableField<MviewWwCollocPosGroupRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_ww_colloc_pos_group.word_id</code>.
     */
    public final TableField<MviewWwCollocPosGroupRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_ww_colloc_pos_group.pos_groups</code>.
     */
    public final TableField<MviewWwCollocPosGroupRecord, JSON> POS_GROUPS = createField(DSL.name("pos_groups"), SQLDataType.JSON, this, "");

    private MviewWwCollocPosGroup(Name alias, Table<MviewWwCollocPosGroupRecord> aliased) {
        this(alias, aliased, null);
    }

    private MviewWwCollocPosGroup(Name alias, Table<MviewWwCollocPosGroupRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.materializedView());
    }

    /**
     * Create an aliased <code>public.mview_ww_colloc_pos_group</code> table reference
     */
    public MviewWwCollocPosGroup(String alias) {
        this(DSL.name(alias), MVIEW_WW_COLLOC_POS_GROUP);
    }

    /**
     * Create an aliased <code>public.mview_ww_colloc_pos_group</code> table reference
     */
    public MviewWwCollocPosGroup(Name alias) {
        this(alias, MVIEW_WW_COLLOC_POS_GROUP);
    }

    /**
     * Create a <code>public.mview_ww_colloc_pos_group</code> table reference
     */
    public MviewWwCollocPosGroup() {
        this(DSL.name("mview_ww_colloc_pos_group"), null);
    }

    public <O extends Record> MviewWwCollocPosGroup(Table<O> child, ForeignKey<O, MviewWwCollocPosGroupRecord> key) {
        super(child, key, MVIEW_WW_COLLOC_POS_GROUP);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public MviewWwCollocPosGroup as(String alias) {
        return new MviewWwCollocPosGroup(DSL.name(alias), this);
    }

    @Override
    public MviewWwCollocPosGroup as(Name alias) {
        return new MviewWwCollocPosGroup(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewWwCollocPosGroup rename(String name) {
        return new MviewWwCollocPosGroup(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewWwCollocPosGroup rename(Name name) {
        return new MviewWwCollocPosGroup(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, JSON> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
