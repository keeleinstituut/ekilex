/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeMtLexemeFreeform;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeMtLexemeFreeformRecord extends UDTRecordImpl<TypeMtLexemeFreeformRecord> implements Record12<Long, Long, String, String, String, String, String, Boolean, String, Timestamp, String, Timestamp> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.freeform_id</code>.
     */
    public void setFreeformId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.freeform_id</code>.
     */
    public Long getFreeformId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.freeform_type_code</code>.
     */
    public void setFreeformTypeCode(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.freeform_type_code</code>.
     */
    public String getFreeformTypeCode() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.value_text</code>.
     */
    public void setValueText(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.value_text</code>.
     */
    public String getValueText() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.lang</code>.
     */
    public void setLang(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.lang</code>.
     */
    public String getLang() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.complexity</code>.
     */
    public void setComplexity(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.created_on</code>.
     */
    public void setCreatedOn(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.created_on</code>.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.type_mt_lexeme_freeform.modified_on</code>.
     */
    public void setModifiedOn(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.type_mt_lexeme_freeform.modified_on</code>.
     */
    public Timestamp getModifiedOn() {
        return (Timestamp) get(11);
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, String, String, String, String, String, Boolean, String, Timestamp, String, Timestamp> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, Long, String, String, String, String, String, Boolean, String, Timestamp, String, Timestamp> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TypeMtLexemeFreeform.LEXEME_ID;
    }

    @Override
    public Field<Long> field2() {
        return TypeMtLexemeFreeform.FREEFORM_ID;
    }

    @Override
    public Field<String> field3() {
        return TypeMtLexemeFreeform.FREEFORM_TYPE_CODE;
    }

    @Override
    public Field<String> field4() {
        return TypeMtLexemeFreeform.VALUE_TEXT;
    }

    @Override
    public Field<String> field5() {
        return TypeMtLexemeFreeform.VALUE_PRESE;
    }

    @Override
    public Field<String> field6() {
        return TypeMtLexemeFreeform.LANG;
    }

    @Override
    public Field<String> field7() {
        return TypeMtLexemeFreeform.COMPLEXITY;
    }

    @Override
    public Field<Boolean> field8() {
        return TypeMtLexemeFreeform.IS_PUBLIC;
    }

    @Override
    public Field<String> field9() {
        return TypeMtLexemeFreeform.CREATED_BY;
    }

    @Override
    public Field<Timestamp> field10() {
        return TypeMtLexemeFreeform.CREATED_ON;
    }

    @Override
    public Field<String> field11() {
        return TypeMtLexemeFreeform.MODIFIED_BY;
    }

    @Override
    public Field<Timestamp> field12() {
        return TypeMtLexemeFreeform.MODIFIED_ON;
    }

    @Override
    public Long component1() {
        return getLexemeId();
    }

    @Override
    public Long component2() {
        return getFreeformId();
    }

    @Override
    public String component3() {
        return getFreeformTypeCode();
    }

    @Override
    public String component4() {
        return getValueText();
    }

    @Override
    public String component5() {
        return getValuePrese();
    }

    @Override
    public String component6() {
        return getLang();
    }

    @Override
    public String component7() {
        return getComplexity();
    }

    @Override
    public Boolean component8() {
        return getIsPublic();
    }

    @Override
    public String component9() {
        return getCreatedBy();
    }

    @Override
    public Timestamp component10() {
        return getCreatedOn();
    }

    @Override
    public String component11() {
        return getModifiedBy();
    }

    @Override
    public Timestamp component12() {
        return getModifiedOn();
    }

    @Override
    public Long value1() {
        return getLexemeId();
    }

    @Override
    public Long value2() {
        return getFreeformId();
    }

    @Override
    public String value3() {
        return getFreeformTypeCode();
    }

    @Override
    public String value4() {
        return getValueText();
    }

    @Override
    public String value5() {
        return getValuePrese();
    }

    @Override
    public String value6() {
        return getLang();
    }

    @Override
    public String value7() {
        return getComplexity();
    }

    @Override
    public Boolean value8() {
        return getIsPublic();
    }

    @Override
    public String value9() {
        return getCreatedBy();
    }

    @Override
    public Timestamp value10() {
        return getCreatedOn();
    }

    @Override
    public String value11() {
        return getModifiedBy();
    }

    @Override
    public Timestamp value12() {
        return getModifiedOn();
    }

    @Override
    public TypeMtLexemeFreeformRecord value1(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value2(Long value) {
        setFreeformId(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value3(String value) {
        setFreeformTypeCode(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value4(String value) {
        setValueText(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value5(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value6(String value) {
        setLang(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value7(String value) {
        setComplexity(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value8(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value9(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value10(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value11(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord value12(Timestamp value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public TypeMtLexemeFreeformRecord values(Long value1, Long value2, String value3, String value4, String value5, String value6, String value7, Boolean value8, String value9, Timestamp value10, String value11, Timestamp value12) {
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
     * Create a detached TypeMtLexemeFreeformRecord
     */
    public TypeMtLexemeFreeformRecord() {
        super(TypeMtLexemeFreeform.TYPE_MT_LEXEME_FREEFORM);
    }

    /**
     * Create a detached, initialised TypeMtLexemeFreeformRecord
     */
    public TypeMtLexemeFreeformRecord(Long lexemeId, Long freeformId, String freeformTypeCode, String valueText, String valuePrese, String lang, String complexity, Boolean isPublic, String createdBy, Timestamp createdOn, String modifiedBy, Timestamp modifiedOn) {
        super(TypeMtLexemeFreeform.TYPE_MT_LEXEME_FREEFORM);

        setLexemeId(lexemeId);
        setFreeformId(freeformId);
        setFreeformTypeCode(freeformTypeCode);
        setValueText(valueText);
        setValuePrese(valuePrese);
        setLang(lang);
        setComplexity(complexity);
        setIsPublic(isPublic);
        setCreatedBy(createdBy);
        setCreatedOn(createdOn);
        setModifiedBy(modifiedBy);
        setModifiedOn(modifiedOn);
    }
}
