/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ViewWwCollocPosGroupRecord;

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
public class ViewWwCollocPosGroup extends TableImpl<ViewWwCollocPosGroupRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_colloc_pos_group</code>
     */
    public static final ViewWwCollocPosGroup VIEW_WW_COLLOC_POS_GROUP = new ViewWwCollocPosGroup();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwCollocPosGroupRecord> getRecordType() {
        return ViewWwCollocPosGroupRecord.class;
    }

    /**
     * The column <code>public.view_ww_colloc_pos_group.lexeme_id</code>.
     */
    public final TableField<ViewWwCollocPosGroupRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_colloc_pos_group.word_id</code>.
     */
    public final TableField<ViewWwCollocPosGroupRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_colloc_pos_group.pos_groups</code>.
     */
    public final TableField<ViewWwCollocPosGroupRecord, JSON> POS_GROUPS = createField(DSL.name("pos_groups"), SQLDataType.JSON, this, "");

    private ViewWwCollocPosGroup(Name alias, Table<ViewWwCollocPosGroupRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwCollocPosGroup(Name alias, Table<ViewWwCollocPosGroupRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_colloc_pos_group\" as  SELECT l.id AS lexeme_id,\n    w.id AS word_id,\n    ( SELECT json_agg(json_build_object('posGroupCode', pg.code, 'relGroups', ( SELECT json_agg(json_build_object('relGroupCode', rg.code, 'collocations', ( SELECT json_agg(json_build_object('lexemeId', cl.id, 'wordId', cw.id, 'wordValue', cw.value, 'usages', ( SELECT json_agg(json_build_object('id', u.id, 'value', u.value, 'valuePrese', u.value_prese, 'lang', u.lang, 'complexity', u.complexity, 'orderBy', u.order_by) ORDER BY u.order_by) AS json_agg\n                                   FROM usage u\n                                  WHERE (u.lexeme_id = cl.id)), 'members', ( SELECT json_agg(json_build_object('conjunct', cm2.conjunct, 'lexemeId', ml.id, 'wordId', mw.id, 'wordValue', mw.value, 'homonymNr', mw.homonym_nr, 'lang', mw.lang, 'formId', mf.id, 'formValue', mf.value, 'morphCode', mf.morph_code, 'weight', cm2.weight, 'memberOrder', cm2.member_order) ORDER BY cm2.member_order) AS json_agg\n                                   FROM form mf,\n                                    word mw,\n                                    lexeme ml,\n                                    collocation_member cm2\n                                  WHERE ((cm2.colloc_lexeme_id = cl.id) AND (cm2.member_lexeme_id = ml.id) AND (ml.word_id = mw.id) AND (cm2.member_form_id = mf.id))), 'groupOrder', cm1.group_order) ORDER BY cm1.group_order) AS json_agg\n                           FROM word cw,\n                            lexeme cl,\n                            collocation_member cm1\n                          WHERE ((cm1.member_lexeme_id = l.id) AND ((cm1.pos_group_code)::text = (pg.code)::text) AND ((cm1.rel_group_code)::text = (rg.code)::text) AND (cm1.colloc_lexeme_id = cl.id) AND (cl.word_id = cw.id)))) ORDER BY rg.order_by) AS json_agg\n                   FROM rel_group rg\n                  WHERE (EXISTS ( SELECT cm.id\n                           FROM collocation_member cm\n                          WHERE ((cm.member_lexeme_id = l.id) AND ((cm.pos_group_code)::text = (pg.code)::text) AND ((cm.rel_group_code)::text = (rg.code)::text)))))) ORDER BY pg.order_by) AS json_agg\n           FROM pos_group pg) AS pos_groups\n   FROM word w,\n    lexeme l\n  WHERE ((l.word_id = w.id) AND (EXISTS ( SELECT cm.id\n           FROM collocation_member cm\n          WHERE ((cm.member_lexeme_id = l.id) AND (cm.pos_group_code IS NOT NULL)))))\n  ORDER BY w.id, l.id;"));
    }

    /**
     * Create an aliased <code>public.view_ww_colloc_pos_group</code> table reference
     */
    public ViewWwCollocPosGroup(String alias) {
        this(DSL.name(alias), VIEW_WW_COLLOC_POS_GROUP);
    }

    /**
     * Create an aliased <code>public.view_ww_colloc_pos_group</code> table reference
     */
    public ViewWwCollocPosGroup(Name alias) {
        this(alias, VIEW_WW_COLLOC_POS_GROUP);
    }

    /**
     * Create a <code>public.view_ww_colloc_pos_group</code> table reference
     */
    public ViewWwCollocPosGroup() {
        this(DSL.name("view_ww_colloc_pos_group"), null);
    }

    public <O extends Record> ViewWwCollocPosGroup(Table<O> child, ForeignKey<O, ViewWwCollocPosGroupRecord> key) {
        super(child, key, VIEW_WW_COLLOC_POS_GROUP);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwCollocPosGroup as(String alias) {
        return new ViewWwCollocPosGroup(DSL.name(alias), this);
    }

    @Override
    public ViewWwCollocPosGroup as(Name alias) {
        return new ViewWwCollocPosGroup(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwCollocPosGroup rename(String name) {
        return new ViewWwCollocPosGroup(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwCollocPosGroup rename(Name name) {
        return new ViewWwCollocPosGroup(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, JSON> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
