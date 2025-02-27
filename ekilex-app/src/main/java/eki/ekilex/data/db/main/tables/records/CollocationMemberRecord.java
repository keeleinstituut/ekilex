/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.CollocationMember;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CollocationMemberRecord extends UpdatableRecordImpl<CollocationMemberRecord> implements Record10<Long, Long, Long, Long, String, String, String, BigDecimal, Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.collocation_member.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.collocation_member.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.collocation_member.colloc_lexeme_id</code>.
     */
    public void setCollocLexemeId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.collocation_member.colloc_lexeme_id</code>.
     */
    public Long getCollocLexemeId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.collocation_member.member_lexeme_id</code>.
     */
    public void setMemberLexemeId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.collocation_member.member_lexeme_id</code>.
     */
    public Long getMemberLexemeId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.collocation_member.member_form_id</code>.
     */
    public void setMemberFormId(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.collocation_member.member_form_id</code>.
     */
    public Long getMemberFormId() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>public.collocation_member.pos_group_code</code>.
     */
    public void setPosGroupCode(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.collocation_member.pos_group_code</code>.
     */
    public String getPosGroupCode() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.collocation_member.rel_group_code</code>.
     */
    public void setRelGroupCode(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.collocation_member.rel_group_code</code>.
     */
    public String getRelGroupCode() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.collocation_member.conjunct</code>.
     */
    public void setConjunct(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.collocation_member.conjunct</code>.
     */
    public String getConjunct() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.collocation_member.weight</code>.
     */
    public void setWeight(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.collocation_member.weight</code>.
     */
    public BigDecimal getWeight() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>public.collocation_member.member_order</code>.
     */
    public void setMemberOrder(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.collocation_member.member_order</code>.
     */
    public Integer getMemberOrder() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>public.collocation_member.group_order</code>.
     */
    public void setGroupOrder(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.collocation_member.group_order</code>.
     */
    public Integer getGroupOrder() {
        return (Integer) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<Long, Long, Long, Long, String, String, String, BigDecimal, Integer, Integer> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<Long, Long, Long, Long, String, String, String, BigDecimal, Integer, Integer> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return CollocationMember.COLLOCATION_MEMBER.ID;
    }

    @Override
    public Field<Long> field2() {
        return CollocationMember.COLLOCATION_MEMBER.COLLOC_LEXEME_ID;
    }

    @Override
    public Field<Long> field3() {
        return CollocationMember.COLLOCATION_MEMBER.MEMBER_LEXEME_ID;
    }

    @Override
    public Field<Long> field4() {
        return CollocationMember.COLLOCATION_MEMBER.MEMBER_FORM_ID;
    }

    @Override
    public Field<String> field5() {
        return CollocationMember.COLLOCATION_MEMBER.POS_GROUP_CODE;
    }

    @Override
    public Field<String> field6() {
        return CollocationMember.COLLOCATION_MEMBER.REL_GROUP_CODE;
    }

    @Override
    public Field<String> field7() {
        return CollocationMember.COLLOCATION_MEMBER.CONJUNCT;
    }

    @Override
    public Field<BigDecimal> field8() {
        return CollocationMember.COLLOCATION_MEMBER.WEIGHT;
    }

    @Override
    public Field<Integer> field9() {
        return CollocationMember.COLLOCATION_MEMBER.MEMBER_ORDER;
    }

    @Override
    public Field<Integer> field10() {
        return CollocationMember.COLLOCATION_MEMBER.GROUP_ORDER;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getCollocLexemeId();
    }

    @Override
    public Long component3() {
        return getMemberLexemeId();
    }

    @Override
    public Long component4() {
        return getMemberFormId();
    }

    @Override
    public String component5() {
        return getPosGroupCode();
    }

    @Override
    public String component6() {
        return getRelGroupCode();
    }

    @Override
    public String component7() {
        return getConjunct();
    }

    @Override
    public BigDecimal component8() {
        return getWeight();
    }

    @Override
    public Integer component9() {
        return getMemberOrder();
    }

    @Override
    public Integer component10() {
        return getGroupOrder();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getCollocLexemeId();
    }

    @Override
    public Long value3() {
        return getMemberLexemeId();
    }

    @Override
    public Long value4() {
        return getMemberFormId();
    }

    @Override
    public String value5() {
        return getPosGroupCode();
    }

    @Override
    public String value6() {
        return getRelGroupCode();
    }

    @Override
    public String value7() {
        return getConjunct();
    }

    @Override
    public BigDecimal value8() {
        return getWeight();
    }

    @Override
    public Integer value9() {
        return getMemberOrder();
    }

    @Override
    public Integer value10() {
        return getGroupOrder();
    }

    @Override
    public CollocationMemberRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value2(Long value) {
        setCollocLexemeId(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value3(Long value) {
        setMemberLexemeId(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value4(Long value) {
        setMemberFormId(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value5(String value) {
        setPosGroupCode(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value6(String value) {
        setRelGroupCode(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value7(String value) {
        setConjunct(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value8(BigDecimal value) {
        setWeight(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value9(Integer value) {
        setMemberOrder(value);
        return this;
    }

    @Override
    public CollocationMemberRecord value10(Integer value) {
        setGroupOrder(value);
        return this;
    }

    @Override
    public CollocationMemberRecord values(Long value1, Long value2, Long value3, Long value4, String value5, String value6, String value7, BigDecimal value8, Integer value9, Integer value10) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CollocationMemberRecord
     */
    public CollocationMemberRecord() {
        super(CollocationMember.COLLOCATION_MEMBER);
    }

    /**
     * Create a detached, initialised CollocationMemberRecord
     */
    public CollocationMemberRecord(Long id, Long collocLexemeId, Long memberLexemeId, Long memberFormId, String posGroupCode, String relGroupCode, String conjunct, BigDecimal weight, Integer memberOrder, Integer groupOrder) {
        super(CollocationMember.COLLOCATION_MEMBER);

        setId(id);
        setCollocLexemeId(collocLexemeId);
        setMemberLexemeId(memberLexemeId);
        setMemberFormId(memberFormId);
        setPosGroupCode(posGroupCode);
        setRelGroupCode(relGroupCode);
        setConjunct(conjunct);
        setWeight(weight);
        setMemberOrder(memberOrder);
        setGroupOrder(groupOrder);
    }
}
