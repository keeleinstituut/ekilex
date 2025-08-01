/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ViewOdWord;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewOdWordRecord extends TableRecordImpl<ViewOdWordRecord> implements Record7<Long, String, String, String, Integer, String, String[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.view_od_word.word_id</code>.
     */
    public void setWordId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.view_od_word.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.view_od_word.value</code>.
     */
    public void setValue(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.view_od_word.value</code>.
     */
    public String getValue() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.view_od_word.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.view_od_word.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.view_od_word.value_as_word</code>.
     */
    public void setValueAsWord(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.view_od_word.value_as_word</code>.
     */
    public String getValueAsWord() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.view_od_word.homonym_nr</code>.
     */
    public void setHomonymNr(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.view_od_word.homonym_nr</code>.
     */
    public Integer getHomonymNr() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.view_od_word.display_morph_code</code>.
     */
    public void setDisplayMorphCode(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.view_od_word.display_morph_code</code>.
     */
    public String getDisplayMorphCode() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.view_od_word.word_type_codes</code>.
     */
    public void setWordTypeCodes(String[] value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.view_od_word.word_type_codes</code>.
     */
    public String[] getWordTypeCodes() {
        return (String[]) get(6);
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, String, String, String, Integer, String, String[]> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, String, String, String, Integer, String, String[]> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ViewOdWord.VIEW_OD_WORD.WORD_ID;
    }

    @Override
    public Field<String> field2() {
        return ViewOdWord.VIEW_OD_WORD.VALUE;
    }

    @Override
    public Field<String> field3() {
        return ViewOdWord.VIEW_OD_WORD.VALUE_PRESE;
    }

    @Override
    public Field<String> field4() {
        return ViewOdWord.VIEW_OD_WORD.VALUE_AS_WORD;
    }

    @Override
    public Field<Integer> field5() {
        return ViewOdWord.VIEW_OD_WORD.HOMONYM_NR;
    }

    @Override
    public Field<String> field6() {
        return ViewOdWord.VIEW_OD_WORD.DISPLAY_MORPH_CODE;
    }

    @Override
    public Field<String[]> field7() {
        return ViewOdWord.VIEW_OD_WORD.WORD_TYPE_CODES;
    }

    @Override
    public Long component1() {
        return getWordId();
    }

    @Override
    public String component2() {
        return getValue();
    }

    @Override
    public String component3() {
        return getValuePrese();
    }

    @Override
    public String component4() {
        return getValueAsWord();
    }

    @Override
    public Integer component5() {
        return getHomonymNr();
    }

    @Override
    public String component6() {
        return getDisplayMorphCode();
    }

    @Override
    public String[] component7() {
        return getWordTypeCodes();
    }

    @Override
    public Long value1() {
        return getWordId();
    }

    @Override
    public String value2() {
        return getValue();
    }

    @Override
    public String value3() {
        return getValuePrese();
    }

    @Override
    public String value4() {
        return getValueAsWord();
    }

    @Override
    public Integer value5() {
        return getHomonymNr();
    }

    @Override
    public String value6() {
        return getDisplayMorphCode();
    }

    @Override
    public String[] value7() {
        return getWordTypeCodes();
    }

    @Override
    public ViewOdWordRecord value1(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public ViewOdWordRecord value2(String value) {
        setValue(value);
        return this;
    }

    @Override
    public ViewOdWordRecord value3(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public ViewOdWordRecord value4(String value) {
        setValueAsWord(value);
        return this;
    }

    @Override
    public ViewOdWordRecord value5(Integer value) {
        setHomonymNr(value);
        return this;
    }

    @Override
    public ViewOdWordRecord value6(String value) {
        setDisplayMorphCode(value);
        return this;
    }

    @Override
    public ViewOdWordRecord value7(String[] value) {
        setWordTypeCodes(value);
        return this;
    }

    @Override
    public ViewOdWordRecord values(Long value1, String value2, String value3, String value4, Integer value5, String value6, String[] value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewOdWordRecord
     */
    public ViewOdWordRecord() {
        super(ViewOdWord.VIEW_OD_WORD);
    }

    /**
     * Create a detached, initialised ViewOdWordRecord
     */
    public ViewOdWordRecord(Long wordId, String value, String valuePrese, String valueAsWord, Integer homonymNr, String displayMorphCode, String[] wordTypeCodes) {
        super(ViewOdWord.VIEW_OD_WORD);

        setWordId(wordId);
        setValue(value);
        setValuePrese(valuePrese);
        setValueAsWord(valueAsWord);
        setHomonymNr(homonymNr);
        setDisplayMorphCode(displayMorphCode);
        setWordTypeCodes(wordTypeCodes);
    }
}
