/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.ParadigmForm;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ParadigmFormRecord extends UpdatableRecordImpl<ParadigmFormRecord> implements Record12<Long, Long, Long, Long, String, String, String, Integer, String, String, Boolean, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.paradigm_form.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.paradigm_form.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.paradigm_form.paradigm_id</code>.
     */
    public void setParadigmId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.paradigm_form.paradigm_id</code>.
     */
    public Long getParadigmId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.paradigm_form.form_id</code>.
     */
    public void setFormId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.paradigm_form.form_id</code>.
     */
    public Long getFormId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.paradigm_form.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.paradigm_form.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>public.paradigm_form.morph_group1</code>.
     */
    public void setMorphGroup1(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.paradigm_form.morph_group1</code>.
     */
    public String getMorphGroup1() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.paradigm_form.morph_group2</code>.
     */
    public void setMorphGroup2(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.paradigm_form.morph_group2</code>.
     */
    public String getMorphGroup2() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.paradigm_form.morph_group3</code>.
     */
    public void setMorphGroup3(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.paradigm_form.morph_group3</code>.
     */
    public String getMorphGroup3() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.paradigm_form.display_level</code>.
     */
    public void setDisplayLevel(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.paradigm_form.display_level</code>.
     */
    public Integer getDisplayLevel() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>public.paradigm_form.display_form</code>.
     */
    public void setDisplayForm(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.paradigm_form.display_form</code>.
     */
    public String getDisplayForm() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.paradigm_form.audio_file</code>.
     */
    public void setAudioFile(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.paradigm_form.audio_file</code>.
     */
    public String getAudioFile() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.paradigm_form.morph_exists</code>.
     */
    public void setMorphExists(Boolean value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.paradigm_form.morph_exists</code>.
     */
    public Boolean getMorphExists() {
        return (Boolean) get(10);
    }

    /**
     * Setter for <code>public.paradigm_form.is_questionable</code>.
     */
    public void setIsQuestionable(Boolean value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.paradigm_form.is_questionable</code>.
     */
    public Boolean getIsQuestionable() {
        return (Boolean) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, Long, Long, String, String, String, Integer, String, String, Boolean, Boolean> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, Long, Long, Long, String, String, String, Integer, String, String, Boolean, Boolean> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ParadigmForm.PARADIGM_FORM.ID;
    }

    @Override
    public Field<Long> field2() {
        return ParadigmForm.PARADIGM_FORM.PARADIGM_ID;
    }

    @Override
    public Field<Long> field3() {
        return ParadigmForm.PARADIGM_FORM.FORM_ID;
    }

    @Override
    public Field<Long> field4() {
        return ParadigmForm.PARADIGM_FORM.ORDER_BY;
    }

    @Override
    public Field<String> field5() {
        return ParadigmForm.PARADIGM_FORM.MORPH_GROUP1;
    }

    @Override
    public Field<String> field6() {
        return ParadigmForm.PARADIGM_FORM.MORPH_GROUP2;
    }

    @Override
    public Field<String> field7() {
        return ParadigmForm.PARADIGM_FORM.MORPH_GROUP3;
    }

    @Override
    public Field<Integer> field8() {
        return ParadigmForm.PARADIGM_FORM.DISPLAY_LEVEL;
    }

    @Override
    public Field<String> field9() {
        return ParadigmForm.PARADIGM_FORM.DISPLAY_FORM;
    }

    @Override
    public Field<String> field10() {
        return ParadigmForm.PARADIGM_FORM.AUDIO_FILE;
    }

    @Override
    public Field<Boolean> field11() {
        return ParadigmForm.PARADIGM_FORM.MORPH_EXISTS;
    }

    @Override
    public Field<Boolean> field12() {
        return ParadigmForm.PARADIGM_FORM.IS_QUESTIONABLE;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getParadigmId();
    }

    @Override
    public Long component3() {
        return getFormId();
    }

    @Override
    public Long component4() {
        return getOrderBy();
    }

    @Override
    public String component5() {
        return getMorphGroup1();
    }

    @Override
    public String component6() {
        return getMorphGroup2();
    }

    @Override
    public String component7() {
        return getMorphGroup3();
    }

    @Override
    public Integer component8() {
        return getDisplayLevel();
    }

    @Override
    public String component9() {
        return getDisplayForm();
    }

    @Override
    public String component10() {
        return getAudioFile();
    }

    @Override
    public Boolean component11() {
        return getMorphExists();
    }

    @Override
    public Boolean component12() {
        return getIsQuestionable();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getParadigmId();
    }

    @Override
    public Long value3() {
        return getFormId();
    }

    @Override
    public Long value4() {
        return getOrderBy();
    }

    @Override
    public String value5() {
        return getMorphGroup1();
    }

    @Override
    public String value6() {
        return getMorphGroup2();
    }

    @Override
    public String value7() {
        return getMorphGroup3();
    }

    @Override
    public Integer value8() {
        return getDisplayLevel();
    }

    @Override
    public String value9() {
        return getDisplayForm();
    }

    @Override
    public String value10() {
        return getAudioFile();
    }

    @Override
    public Boolean value11() {
        return getMorphExists();
    }

    @Override
    public Boolean value12() {
        return getIsQuestionable();
    }

    @Override
    public ParadigmFormRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value2(Long value) {
        setParadigmId(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value3(Long value) {
        setFormId(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value4(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value5(String value) {
        setMorphGroup1(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value6(String value) {
        setMorphGroup2(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value7(String value) {
        setMorphGroup3(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value8(Integer value) {
        setDisplayLevel(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value9(String value) {
        setDisplayForm(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value10(String value) {
        setAudioFile(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value11(Boolean value) {
        setMorphExists(value);
        return this;
    }

    @Override
    public ParadigmFormRecord value12(Boolean value) {
        setIsQuestionable(value);
        return this;
    }

    @Override
    public ParadigmFormRecord values(Long value1, Long value2, Long value3, Long value4, String value5, String value6, String value7, Integer value8, String value9, String value10, Boolean value11, Boolean value12) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ParadigmFormRecord
     */
    public ParadigmFormRecord() {
        super(ParadigmForm.PARADIGM_FORM);
    }

    /**
     * Create a detached, initialised ParadigmFormRecord
     */
    public ParadigmFormRecord(Long id, Long paradigmId, Long formId, Long orderBy, String morphGroup1, String morphGroup2, String morphGroup3, Integer displayLevel, String displayForm, String audioFile, Boolean morphExists, Boolean isQuestionable) {
        super(ParadigmForm.PARADIGM_FORM);

        setId(id);
        setParadigmId(paradigmId);
        setFormId(formId);
        setOrderBy(orderBy);
        setMorphGroup1(morphGroup1);
        setMorphGroup2(morphGroup2);
        setMorphGroup3(morphGroup3);
        setDisplayLevel(displayLevel);
        setDisplayForm(displayForm);
        setAudioFile(audioFile);
        setMorphExists(morphExists);
        setIsQuestionable(isQuestionable);
    }
}