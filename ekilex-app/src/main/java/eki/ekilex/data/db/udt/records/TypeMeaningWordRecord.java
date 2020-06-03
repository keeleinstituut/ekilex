/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeMeaningWord;

import java.math.BigDecimal;

import org.jooq.Field;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeMeaningWordRecord extends UDTRecordImpl<TypeMeaningWordRecord> implements Record15<Long, Long, Long, String, BigDecimal, TypeFreeformRecord[], String[], String, Long, String, String, Integer, String, String[], String> {

    private static final long serialVersionUID = 1696041688;

    /**
     * Setter for <code>public.type_meaning_word.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.type_meaning_word.meaning_id</code>.
     */
    public void setMeaningId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.meaning_id</code>.
     */
    public Long getMeaningId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.type_meaning_word.mw_lexeme_id</code>.
     */
    public void setMwLexemeId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.mw_lexeme_id</code>.
     */
    public Long getMwLexemeId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.type_meaning_word.mw_lex_complexity</code>.
     */
    public void setMwLexComplexity(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.mw_lex_complexity</code>.
     */
    public String getMwLexComplexity() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.type_meaning_word.mw_lex_weight</code>.
     */
    public void setMwLexWeight(BigDecimal value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.mw_lex_weight</code>.
     */
    public BigDecimal getMwLexWeight() {
        return (BigDecimal) get(4);
    }

    /**
     * Setter for <code>public.type_meaning_word.mw_lex_governments</code>.
     */
    public void setMwLexGovernments(TypeFreeformRecord[] value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.mw_lex_governments</code>.
     */
    public TypeFreeformRecord[] getMwLexGovernments() {
        return (TypeFreeformRecord[]) get(5);
    }

    /**
     * Setter for <code>public.type_meaning_word.mw_lex_register_codes</code>.
     */
    public void setMwLexRegisterCodes(String[] value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.mw_lex_register_codes</code>.
     */
    public String[] getMwLexRegisterCodes() {
        return (String[]) get(6);
    }

    /**
     * Setter for <code>public.type_meaning_word.mw_lex_value_state_code</code>.
     */
    public void setMwLexValueStateCode(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.mw_lex_value_state_code</code>.
     */
    public String getMwLexValueStateCode() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.type_meaning_word.word_id</code>.
     */
    public void setWordId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>public.type_meaning_word.word</code>.
     */
    public void setWord(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.word</code>.
     */
    public String getWord() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.type_meaning_word.word_prese</code>.
     */
    public void setWordPrese(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.word_prese</code>.
     */
    public String getWordPrese() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.type_meaning_word.homonym_nr</code>.
     */
    public void setHomonymNr(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.homonym_nr</code>.
     */
    public Integer getHomonymNr() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>public.type_meaning_word.lang</code>.
     */
    public void setLang(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.lang</code>.
     */
    public String getLang() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.type_meaning_word.word_type_codes</code>.
     */
    public void setWordTypeCodes(String[] value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.word_type_codes</code>.
     */
    public String[] getWordTypeCodes() {
        return (String[]) get(13);
    }

    /**
     * Setter for <code>public.type_meaning_word.aspect_code</code>.
     */
    public void setAspectCode(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.type_meaning_word.aspect_code</code>.
     */
    public String getAspectCode() {
        return (String) get(14);
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, Long, Long, String, BigDecimal, TypeFreeformRecord[], String[], String, Long, String, String, Integer, String, String[], String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<Long, Long, Long, String, BigDecimal, TypeFreeformRecord[], String[], String, Long, String, String, Integer, String, String[], String> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TypeMeaningWord.LEXEME_ID;
    }

    @Override
    public Field<Long> field2() {
        return TypeMeaningWord.MEANING_ID;
    }

    @Override
    public Field<Long> field3() {
        return TypeMeaningWord.MW_LEXEME_ID;
    }

    @Override
    public Field<String> field4() {
        return TypeMeaningWord.MW_LEX_COMPLEXITY;
    }

    @Override
    public Field<BigDecimal> field5() {
        return TypeMeaningWord.MW_LEX_WEIGHT;
    }

    @Override
    public Field<TypeFreeformRecord[]> field6() {
        return TypeMeaningWord.MW_LEX_GOVERNMENTS;
    }

    @Override
    public Field<String[]> field7() {
        return TypeMeaningWord.MW_LEX_REGISTER_CODES;
    }

    @Override
    public Field<String> field8() {
        return TypeMeaningWord.MW_LEX_VALUE_STATE_CODE;
    }

    @Override
    public Field<Long> field9() {
        return TypeMeaningWord.WORD_ID;
    }

    @Override
    public Field<String> field10() {
        return TypeMeaningWord.WORD;
    }

    @Override
    public Field<String> field11() {
        return TypeMeaningWord.WORD_PRESE;
    }

    @Override
    public Field<Integer> field12() {
        return TypeMeaningWord.HOMONYM_NR;
    }

    @Override
    public Field<String> field13() {
        return TypeMeaningWord.LANG;
    }

    @Override
    public Field<String[]> field14() {
        return TypeMeaningWord.WORD_TYPE_CODES;
    }

    @Override
    public Field<String> field15() {
        return TypeMeaningWord.ASPECT_CODE;
    }

    @Override
    public Long component1() {
        return getLexemeId();
    }

    @Override
    public Long component2() {
        return getMeaningId();
    }

    @Override
    public Long component3() {
        return getMwLexemeId();
    }

    @Override
    public String component4() {
        return getMwLexComplexity();
    }

    @Override
    public BigDecimal component5() {
        return getMwLexWeight();
    }

    @Override
    public TypeFreeformRecord[] component6() {
        return getMwLexGovernments();
    }

    @Override
    public String[] component7() {
        return getMwLexRegisterCodes();
    }

    @Override
    public String component8() {
        return getMwLexValueStateCode();
    }

    @Override
    public Long component9() {
        return getWordId();
    }

    @Override
    public String component10() {
        return getWord();
    }

    @Override
    public String component11() {
        return getWordPrese();
    }

    @Override
    public Integer component12() {
        return getHomonymNr();
    }

    @Override
    public String component13() {
        return getLang();
    }

    @Override
    public String[] component14() {
        return getWordTypeCodes();
    }

    @Override
    public String component15() {
        return getAspectCode();
    }

    @Override
    public Long value1() {
        return getLexemeId();
    }

    @Override
    public Long value2() {
        return getMeaningId();
    }

    @Override
    public Long value3() {
        return getMwLexemeId();
    }

    @Override
    public String value4() {
        return getMwLexComplexity();
    }

    @Override
    public BigDecimal value5() {
        return getMwLexWeight();
    }

    @Override
    public TypeFreeformRecord[] value6() {
        return getMwLexGovernments();
    }

    @Override
    public String[] value7() {
        return getMwLexRegisterCodes();
    }

    @Override
    public String value8() {
        return getMwLexValueStateCode();
    }

    @Override
    public Long value9() {
        return getWordId();
    }

    @Override
    public String value10() {
        return getWord();
    }

    @Override
    public String value11() {
        return getWordPrese();
    }

    @Override
    public Integer value12() {
        return getHomonymNr();
    }

    @Override
    public String value13() {
        return getLang();
    }

    @Override
    public String[] value14() {
        return getWordTypeCodes();
    }

    @Override
    public String value15() {
        return getAspectCode();
    }

    @Override
    public TypeMeaningWordRecord value1(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value2(Long value) {
        setMeaningId(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value3(Long value) {
        setMwLexemeId(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value4(String value) {
        setMwLexComplexity(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value5(BigDecimal value) {
        setMwLexWeight(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value6(TypeFreeformRecord[] value) {
        setMwLexGovernments(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value7(String[] value) {
        setMwLexRegisterCodes(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value8(String value) {
        setMwLexValueStateCode(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value9(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value10(String value) {
        setWord(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value11(String value) {
        setWordPrese(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value12(Integer value) {
        setHomonymNr(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value13(String value) {
        setLang(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value14(String[] value) {
        setWordTypeCodes(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord value15(String value) {
        setAspectCode(value);
        return this;
    }

    @Override
    public TypeMeaningWordRecord values(Long value1, Long value2, Long value3, String value4, BigDecimal value5, TypeFreeformRecord[] value6, String[] value7, String value8, Long value9, String value10, String value11, Integer value12, String value13, String[] value14, String value15) {
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
     * Create a detached TypeMeaningWordRecord
     */
    public TypeMeaningWordRecord() {
        super(TypeMeaningWord.TYPE_MEANING_WORD);
    }

    /**
     * Create a detached, initialised TypeMeaningWordRecord
     */
    public TypeMeaningWordRecord(Long lexemeId, Long meaningId, Long mwLexemeId, String mwLexComplexity, BigDecimal mwLexWeight, TypeFreeformRecord[] mwLexGovernments, String[] mwLexRegisterCodes, String mwLexValueStateCode, Long wordId, String word, String wordPrese, Integer homonymNr, String lang, String[] wordTypeCodes, String aspectCode) {
        super(TypeMeaningWord.TYPE_MEANING_WORD);

        set(0, lexemeId);
        set(1, meaningId);
        set(2, mwLexemeId);
        set(3, mwLexComplexity);
        set(4, mwLexWeight);
        set(5, mwLexGovernments);
        set(6, mwLexRegisterCodes);
        set(7, mwLexValueStateCode);
        set(8, wordId);
        set(9, word);
        set(10, wordPrese);
        set(11, homonymNr);
        set(12, lang);
        set(13, wordTypeCodes);
        set(14, aspectCode);
    }
}
