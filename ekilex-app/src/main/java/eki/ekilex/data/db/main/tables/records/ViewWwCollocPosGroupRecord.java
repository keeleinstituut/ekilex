/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ViewWwCollocPosGroup;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwCollocPosGroupRecord extends TableRecordImpl<ViewWwCollocPosGroupRecord> implements Record3<Long, Long, JSON> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_ww_colloc_pos_group.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_ww_colloc_pos_group.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.view_ww_colloc_pos_group.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_ww_colloc_pos_group.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.view_ww_colloc_pos_group.pos_groups</code>.
     */
    public void setPosGroups(JSON value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_ww_colloc_pos_group.pos_groups</code>.
     */
    public JSON getPosGroups() {
        return (JSON) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, JSON> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, JSON> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ViewWwCollocPosGroup.VIEW_WW_COLLOC_POS_GROUP.LEXEME_ID;
    }

    @Override
    public Field<Long> field2() {
        return ViewWwCollocPosGroup.VIEW_WW_COLLOC_POS_GROUP.WORD_ID;
    }

    @Override
    public Field<JSON> field3() {
        return ViewWwCollocPosGroup.VIEW_WW_COLLOC_POS_GROUP.POS_GROUPS;
    }

    @Override
    public Long component1() {
        return getLexemeId();
    }

    @Override
    public Long component2() {
        return getWordId();
    }

    @Override
    public JSON component3() {
        return getPosGroups();
    }

    @Override
    public Long value1() {
        return getLexemeId();
    }

    @Override
    public Long value2() {
        return getWordId();
    }

    @Override
    public JSON value3() {
        return getPosGroups();
    }

    @Override
    public ViewWwCollocPosGroupRecord value1(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public ViewWwCollocPosGroupRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public ViewWwCollocPosGroupRecord value3(JSON value) {
        setPosGroups(value);
        return this;
    }

    @Override
    public ViewWwCollocPosGroupRecord values(Long value1, Long value2, JSON value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewWwCollocPosGroupRecord
     */
    public ViewWwCollocPosGroupRecord() {
        super(ViewWwCollocPosGroup.VIEW_WW_COLLOC_POS_GROUP);
    }

    /**
     * Create a detached, initialised ViewWwCollocPosGroupRecord
     */
    public ViewWwCollocPosGroupRecord(Long lexemeId, Long wordId, JSON posGroups) {
        super(ViewWwCollocPosGroup.VIEW_WW_COLLOC_POS_GROUP);

        setLexemeId(lexemeId);
        setWordId(wordId);
        setPosGroups(posGroups);
    }
}