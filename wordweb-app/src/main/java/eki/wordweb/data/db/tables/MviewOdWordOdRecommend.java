/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.MviewOdWordOdRecommendRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class MviewOdWordOdRecommend extends TableImpl<MviewOdWordOdRecommendRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.mview_od_word_od_recommend</code>
     */
    public static final MviewOdWordOdRecommend MVIEW_OD_WORD_OD_RECOMMEND = new MviewOdWordOdRecommend();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MviewOdWordOdRecommendRecord> getRecordType() {
        return MviewOdWordOdRecommendRecord.class;
    }

    /**
     * The column <code>public.mview_od_word_od_recommend.word_id</code>.
     */
    public final TableField<MviewOdWordOdRecommendRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_od_word_od_recommend.word_od_recommend_id</code>.
     */
    public final TableField<MviewOdWordOdRecommendRecord, Long> WORD_OD_RECOMMEND_ID = createField(DSL.name("word_od_recommend_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.mview_od_word_od_recommend.value</code>.
     */
    public final TableField<MviewOdWordOdRecommendRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_od_word_od_recommend.value_prese</code>.
     */
    public final TableField<MviewOdWordOdRecommendRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_od_word_od_recommend.opt_value</code>.
     */
    public final TableField<MviewOdWordOdRecommendRecord, String> OPT_VALUE = createField(DSL.name("opt_value"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.mview_od_word_od_recommend.opt_value_prese</code>.
     */
    public final TableField<MviewOdWordOdRecommendRecord, String> OPT_VALUE_PRESE = createField(DSL.name("opt_value_prese"), SQLDataType.CLOB, this, "");

    private MviewOdWordOdRecommend(Name alias, Table<MviewOdWordOdRecommendRecord> aliased) {
        this(alias, aliased, null);
    }

    private MviewOdWordOdRecommend(Name alias, Table<MviewOdWordOdRecommendRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.materializedView());
    }

    /**
     * Create an aliased <code>public.mview_od_word_od_recommend</code> table reference
     */
    public MviewOdWordOdRecommend(String alias) {
        this(DSL.name(alias), MVIEW_OD_WORD_OD_RECOMMEND);
    }

    /**
     * Create an aliased <code>public.mview_od_word_od_recommend</code> table reference
     */
    public MviewOdWordOdRecommend(Name alias) {
        this(alias, MVIEW_OD_WORD_OD_RECOMMEND);
    }

    /**
     * Create a <code>public.mview_od_word_od_recommend</code> table reference
     */
    public MviewOdWordOdRecommend() {
        this(DSL.name("mview_od_word_od_recommend"), null);
    }

    public <O extends Record> MviewOdWordOdRecommend(Table<O> child, ForeignKey<O, MviewOdWordOdRecommendRecord> key) {
        super(child, key, MVIEW_OD_WORD_OD_RECOMMEND);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public MviewOdWordOdRecommend as(String alias) {
        return new MviewOdWordOdRecommend(DSL.name(alias), this);
    }

    @Override
    public MviewOdWordOdRecommend as(Name alias) {
        return new MviewOdWordOdRecommend(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewOdWordOdRecommend rename(String name) {
        return new MviewOdWordOdRecommend(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MviewOdWordOdRecommend rename(Name name) {
        return new MviewOdWordOdRecommend(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
