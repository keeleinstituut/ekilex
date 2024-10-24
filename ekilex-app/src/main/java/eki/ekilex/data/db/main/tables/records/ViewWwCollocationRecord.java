/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ViewWwCollocation;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwCollocationRecord extends TableRecordImpl<ViewWwCollocationRecord> implements Record15<Long, Long, Long, String, Long, Long, String, Long, Integer, Long, String, String, String[], JSON, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_ww_collocation.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.view_ww_collocation.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.view_ww_collocation.pos_group_id</code>.
     */
    public void setPosGroupId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.pos_group_id</code>.
     */
    public Long getPosGroupId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.view_ww_collocation.pos_group_code</code>.
     */
    public void setPosGroupCode(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.pos_group_code</code>.
     */
    public String getPosGroupCode() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.view_ww_collocation.pos_group_order_by</code>.
     */
    public void setPosGroupOrderBy(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.pos_group_order_by</code>.
     */
    public Long getPosGroupOrderBy() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>public.view_ww_collocation.rel_group_id</code>.
     */
    public void setRelGroupId(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.rel_group_id</code>.
     */
    public Long getRelGroupId() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>public.view_ww_collocation.rel_group_name</code>.
     */
    public void setRelGroupName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.rel_group_name</code>.
     */
    public String getRelGroupName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.view_ww_collocation.rel_group_order_by</code>.
     */
    public void setRelGroupOrderBy(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.rel_group_order_by</code>.
     */
    public Long getRelGroupOrderBy() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>public.view_ww_collocation.colloc_group_order</code>.
     */
    public void setCollocGroupOrder(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.colloc_group_order</code>.
     */
    public Integer getCollocGroupOrder() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>public.view_ww_collocation.colloc_id</code>.
     */
    public void setCollocId(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.colloc_id</code>.
     */
    public Long getCollocId() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>public.view_ww_collocation.colloc_value</code>.
     */
    public void setCollocValue(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.colloc_value</code>.
     */
    public String getCollocValue() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.view_ww_collocation.colloc_definition</code>.
     */
    public void setCollocDefinition(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.colloc_definition</code>.
     */
    public String getCollocDefinition() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.view_ww_collocation.colloc_usages</code>.
     */
    public void setCollocUsages(String[] value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.colloc_usages</code>.
     */
    public String[] getCollocUsages() {
        return (String[]) get(12);
    }

    /**
     * Setter for <code>public.view_ww_collocation.colloc_members</code>.
     */
    public void setCollocMembers(JSON value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.colloc_members</code>.
     */
    public JSON getCollocMembers() {
        return (JSON) get(13);
    }

    /**
     * Setter for <code>public.view_ww_collocation.complexity</code>.
     */
    public void setComplexity(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.view_ww_collocation.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(14);
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, Long, Long, String, Long, Long, String, Long, Integer, Long, String, String, String[], JSON, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<Long, Long, Long, String, Long, Long, String, Long, Integer, Long, String, String, String[], JSON, String> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.LEXEME_ID;
    }

    @Override
    public Field<Long> field2() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.WORD_ID;
    }

    @Override
    public Field<Long> field3() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.POS_GROUP_ID;
    }

    @Override
    public Field<String> field4() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.POS_GROUP_CODE;
    }

    @Override
    public Field<Long> field5() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.POS_GROUP_ORDER_BY;
    }

    @Override
    public Field<Long> field6() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.REL_GROUP_ID;
    }

    @Override
    public Field<String> field7() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.REL_GROUP_NAME;
    }

    @Override
    public Field<Long> field8() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.REL_GROUP_ORDER_BY;
    }

    @Override
    public Field<Integer> field9() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COLLOC_GROUP_ORDER;
    }

    @Override
    public Field<Long> field10() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COLLOC_ID;
    }

    @Override
    public Field<String> field11() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COLLOC_VALUE;
    }

    @Override
    public Field<String> field12() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COLLOC_DEFINITION;
    }

    @Override
    public Field<String[]> field13() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COLLOC_USAGES;
    }

    @Override
    public Field<JSON> field14() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COLLOC_MEMBERS;
    }

    @Override
    public Field<String> field15() {
        return ViewWwCollocation.VIEW_WW_COLLOCATION.COMPLEXITY;
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
    public Long component3() {
        return getPosGroupId();
    }

    @Override
    public String component4() {
        return getPosGroupCode();
    }

    @Override
    public Long component5() {
        return getPosGroupOrderBy();
    }

    @Override
    public Long component6() {
        return getRelGroupId();
    }

    @Override
    public String component7() {
        return getRelGroupName();
    }

    @Override
    public Long component8() {
        return getRelGroupOrderBy();
    }

    @Override
    public Integer component9() {
        return getCollocGroupOrder();
    }

    @Override
    public Long component10() {
        return getCollocId();
    }

    @Override
    public String component11() {
        return getCollocValue();
    }

    @Override
    public String component12() {
        return getCollocDefinition();
    }

    @Override
    public String[] component13() {
        return getCollocUsages();
    }

    @Override
    public JSON component14() {
        return getCollocMembers();
    }

    @Override
    public String component15() {
        return getComplexity();
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
    public Long value3() {
        return getPosGroupId();
    }

    @Override
    public String value4() {
        return getPosGroupCode();
    }

    @Override
    public Long value5() {
        return getPosGroupOrderBy();
    }

    @Override
    public Long value6() {
        return getRelGroupId();
    }

    @Override
    public String value7() {
        return getRelGroupName();
    }

    @Override
    public Long value8() {
        return getRelGroupOrderBy();
    }

    @Override
    public Integer value9() {
        return getCollocGroupOrder();
    }

    @Override
    public Long value10() {
        return getCollocId();
    }

    @Override
    public String value11() {
        return getCollocValue();
    }

    @Override
    public String value12() {
        return getCollocDefinition();
    }

    @Override
    public String[] value13() {
        return getCollocUsages();
    }

    @Override
    public JSON value14() {
        return getCollocMembers();
    }

    @Override
    public String value15() {
        return getComplexity();
    }

    @Override
    public ViewWwCollocationRecord value1(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value3(Long value) {
        setPosGroupId(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value4(String value) {
        setPosGroupCode(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value5(Long value) {
        setPosGroupOrderBy(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value6(Long value) {
        setRelGroupId(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value7(String value) {
        setRelGroupName(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value8(Long value) {
        setRelGroupOrderBy(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value9(Integer value) {
        setCollocGroupOrder(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value10(Long value) {
        setCollocId(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value11(String value) {
        setCollocValue(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value12(String value) {
        setCollocDefinition(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value13(String[] value) {
        setCollocUsages(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value14(JSON value) {
        setCollocMembers(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord value15(String value) {
        setComplexity(value);
        return this;
    }

    @Override
    public ViewWwCollocationRecord values(Long value1, Long value2, Long value3, String value4, Long value5, Long value6, String value7, Long value8, Integer value9, Long value10, String value11, String value12, String[] value13, JSON value14, String value15) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewWwCollocationRecord
     */
    public ViewWwCollocationRecord() {
        super(ViewWwCollocation.VIEW_WW_COLLOCATION);
    }

    /**
     * Create a detached, initialised ViewWwCollocationRecord
     */
    public ViewWwCollocationRecord(Long lexemeId, Long wordId, Long posGroupId, String posGroupCode, Long posGroupOrderBy, Long relGroupId, String relGroupName, Long relGroupOrderBy, Integer collocGroupOrder, Long collocId, String collocValue, String collocDefinition, String[] collocUsages, JSON collocMembers, String complexity) {
        super(ViewWwCollocation.VIEW_WW_COLLOCATION);

        setLexemeId(lexemeId);
        setWordId(wordId);
        setPosGroupId(posGroupId);
        setPosGroupCode(posGroupCode);
        setPosGroupOrderBy(posGroupOrderBy);
        setRelGroupId(relGroupId);
        setRelGroupName(relGroupName);
        setRelGroupOrderBy(relGroupOrderBy);
        setCollocGroupOrder(collocGroupOrder);
        setCollocId(collocId);
        setCollocValue(collocValue);
        setCollocDefinition(collocDefinition);
        setCollocUsages(collocUsages);
        setCollocMembers(collocMembers);
        setComplexity(complexity);
    }
}
