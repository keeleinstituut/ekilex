/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables.records;


import eki.wordweb.data.db.tables.LexicalDecisionData;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexicalDecisionDataRecord extends UpdatableRecordImpl<LexicalDecisionDataRecord> implements Record4<Long, String, String, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.lexical_decision_data.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lexical_decision_data.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.lexical_decision_data.word</code>.
     */
    public void setWord(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lexical_decision_data.word</code>.
     */
    public String getWord() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.lexical_decision_data.lang</code>.
     */
    public void setLang(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lexical_decision_data.lang</code>.
     */
    public String getLang() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.lexical_decision_data.is_word</code>.
     */
    public void setIsWord(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lexical_decision_data.is_word</code>.
     */
    public Boolean getIsWord() {
        return (Boolean) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, String, Boolean> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, String, String, Boolean> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return LexicalDecisionData.LEXICAL_DECISION_DATA.ID;
    }

    @Override
    public Field<String> field2() {
        return LexicalDecisionData.LEXICAL_DECISION_DATA.WORD;
    }

    @Override
    public Field<String> field3() {
        return LexicalDecisionData.LEXICAL_DECISION_DATA.LANG;
    }

    @Override
    public Field<Boolean> field4() {
        return LexicalDecisionData.LEXICAL_DECISION_DATA.IS_WORD;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getWord();
    }

    @Override
    public String component3() {
        return getLang();
    }

    @Override
    public Boolean component4() {
        return getIsWord();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getWord();
    }

    @Override
    public String value3() {
        return getLang();
    }

    @Override
    public Boolean value4() {
        return getIsWord();
    }

    @Override
    public LexicalDecisionDataRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LexicalDecisionDataRecord value2(String value) {
        setWord(value);
        return this;
    }

    @Override
    public LexicalDecisionDataRecord value3(String value) {
        setLang(value);
        return this;
    }

    @Override
    public LexicalDecisionDataRecord value4(Boolean value) {
        setIsWord(value);
        return this;
    }

    @Override
    public LexicalDecisionDataRecord values(Long value1, String value2, String value3, Boolean value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LexicalDecisionDataRecord
     */
    public LexicalDecisionDataRecord() {
        super(LexicalDecisionData.LEXICAL_DECISION_DATA);
    }

    /**
     * Create a detached, initialised LexicalDecisionDataRecord
     */
    public LexicalDecisionDataRecord(Long id, String word, String lang, Boolean isWord) {
        super(LexicalDecisionData.LEXICAL_DECISION_DATA);

        setId(id);
        setWord(word);
        setLang(lang);
        setIsWord(isWord);
    }
}
