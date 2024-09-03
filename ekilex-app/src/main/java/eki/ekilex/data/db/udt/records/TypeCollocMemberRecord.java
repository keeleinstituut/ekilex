/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeCollocMember;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeCollocMemberRecord extends UDTRecordImpl<TypeCollocMemberRecord> implements Record8<Long, Long, String, String, Integer, String, String, BigDecimal> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_colloc_member.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.type_colloc_member.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.type_colloc_member.word</code>.
     */
    public void setWord(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.word</code>.
     */
    public String getWord() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.type_colloc_member.form</code>.
     */
    public void setForm(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.form</code>.
     */
    public String getForm() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.type_colloc_member.homonym_nr</code>.
     */
    public void setHomonymNr(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.homonym_nr</code>.
     */
    public Integer getHomonymNr() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.type_colloc_member.lang</code>.
     */
    public void setLang(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.lang</code>.
     */
    public String getLang() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.type_colloc_member.conjunct</code>.
     */
    public void setConjunct(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.conjunct</code>.
     */
    public String getConjunct() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.type_colloc_member.weight</code>.
     */
    public void setWeight(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.type_colloc_member.weight</code>.
     */
    public BigDecimal getWeight() {
        return (BigDecimal) get(7);
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, String, String, Integer, String, String, BigDecimal> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Long, String, String, Integer, String, String, BigDecimal> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TypeCollocMember.LEXEME_ID;
    }

    @Override
    public Field<Long> field2() {
        return TypeCollocMember.WORD_ID;
    }

    @Override
    public Field<String> field3() {
        return TypeCollocMember.WORD;
    }

    @Override
    public Field<String> field4() {
        return TypeCollocMember.FORM;
    }

    @Override
    public Field<Integer> field5() {
        return TypeCollocMember.HOMONYM_NR;
    }

    @Override
    public Field<String> field6() {
        return TypeCollocMember.LANG;
    }

    @Override
    public Field<String> field7() {
        return TypeCollocMember.CONJUNCT;
    }

    @Override
    public Field<BigDecimal> field8() {
        return TypeCollocMember.WEIGHT;
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
    public String component3() {
        return getWord();
    }

    @Override
    public String component4() {
        return getForm();
    }

    @Override
    public Integer component5() {
        return getHomonymNr();
    }

    @Override
    public String component6() {
        return getLang();
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
    public Long value1() {
        return getLexemeId();
    }

    @Override
    public Long value2() {
        return getWordId();
    }

    @Override
    public String value3() {
        return getWord();
    }

    @Override
    public String value4() {
        return getForm();
    }

    @Override
    public Integer value5() {
        return getHomonymNr();
    }

    @Override
    public String value6() {
        return getLang();
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
    public TypeCollocMemberRecord value1(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value3(String value) {
        setWord(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value4(String value) {
        setForm(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value5(Integer value) {
        setHomonymNr(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value6(String value) {
        setLang(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value7(String value) {
        setConjunct(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord value8(BigDecimal value) {
        setWeight(value);
        return this;
    }

    @Override
    public TypeCollocMemberRecord values(Long value1, Long value2, String value3, String value4, Integer value5, String value6, String value7, BigDecimal value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeCollocMemberRecord
     */
    public TypeCollocMemberRecord() {
        super(TypeCollocMember.TYPE_COLLOC_MEMBER);
    }

    /**
     * Create a detached, initialised TypeCollocMemberRecord
     */
    public TypeCollocMemberRecord(Long lexemeId, Long wordId, String word, String form, Integer homonymNr, String lang, String conjunct, BigDecimal weight) {
        super(TypeCollocMember.TYPE_COLLOC_MEMBER);

        setLexemeId(lexemeId);
        setWordId(wordId);
        setWord(word);
        setForm(form);
        setHomonymNr(homonymNr);
        setLang(lang);
        setConjunct(conjunct);
        setWeight(weight);
    }
}
