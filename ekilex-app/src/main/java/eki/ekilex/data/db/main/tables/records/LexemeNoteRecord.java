/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.LexemeNote;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexemeNoteRecord extends UpdatableRecordImpl<LexemeNoteRecord> implements Record13<Long, Long, Long, String, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.lexeme_note.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lexeme_note.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.lexeme_note.original_freeform_id</code>.
     */
    public void setOriginalFreeformId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lexeme_note.original_freeform_id</code>.
     */
    public Long getOriginalFreeformId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.lexeme_note.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lexeme_note.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.lexeme_note.value</code>.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lexeme_note.value</code>.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.lexeme_note.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.lexeme_note.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.lexeme_note.lang</code>.
     */
    public void setLang(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.lexeme_note.lang</code>.
     */
    public String getLang() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.lexeme_note.complexity</code>.
     */
    public void setComplexity(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.lexeme_note.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.lexeme_note.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.lexeme_note.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>public.lexeme_note.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.lexeme_note.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.lexeme_note.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.lexeme_note.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>public.lexeme_note.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.lexeme_note.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.lexeme_note.modified_on</code>.
     */
    public void setModifiedOn(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.lexeme_note.modified_on</code>.
     */
    public LocalDateTime getModifiedOn() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>public.lexeme_note.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.lexeme_note.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, Long, Long, String, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime, Long> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<Long, Long, Long, String, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime, Long> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return LexemeNote.LEXEME_NOTE.ID;
    }

    @Override
    public Field<Long> field2() {
        return LexemeNote.LEXEME_NOTE.ORIGINAL_FREEFORM_ID;
    }

    @Override
    public Field<Long> field3() {
        return LexemeNote.LEXEME_NOTE.LEXEME_ID;
    }

    @Override
    public Field<String> field4() {
        return LexemeNote.LEXEME_NOTE.VALUE;
    }

    @Override
    public Field<String> field5() {
        return LexemeNote.LEXEME_NOTE.VALUE_PRESE;
    }

    @Override
    public Field<String> field6() {
        return LexemeNote.LEXEME_NOTE.LANG;
    }

    @Override
    public Field<String> field7() {
        return LexemeNote.LEXEME_NOTE.COMPLEXITY;
    }

    @Override
    public Field<Boolean> field8() {
        return LexemeNote.LEXEME_NOTE.IS_PUBLIC;
    }

    @Override
    public Field<String> field9() {
        return LexemeNote.LEXEME_NOTE.CREATED_BY;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return LexemeNote.LEXEME_NOTE.CREATED_ON;
    }

    @Override
    public Field<String> field11() {
        return LexemeNote.LEXEME_NOTE.MODIFIED_BY;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return LexemeNote.LEXEME_NOTE.MODIFIED_ON;
    }

    @Override
    public Field<Long> field13() {
        return LexemeNote.LEXEME_NOTE.ORDER_BY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getOriginalFreeformId();
    }

    @Override
    public Long component3() {
        return getLexemeId();
    }

    @Override
    public String component4() {
        return getValue();
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
    public LocalDateTime component10() {
        return getCreatedOn();
    }

    @Override
    public String component11() {
        return getModifiedBy();
    }

    @Override
    public LocalDateTime component12() {
        return getModifiedOn();
    }

    @Override
    public Long component13() {
        return getOrderBy();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getOriginalFreeformId();
    }

    @Override
    public Long value3() {
        return getLexemeId();
    }

    @Override
    public String value4() {
        return getValue();
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
    public LocalDateTime value10() {
        return getCreatedOn();
    }

    @Override
    public String value11() {
        return getModifiedBy();
    }

    @Override
    public LocalDateTime value12() {
        return getModifiedOn();
    }

    @Override
    public Long value13() {
        return getOrderBy();
    }

    @Override
    public LexemeNoteRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value2(Long value) {
        setOriginalFreeformId(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value3(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value4(String value) {
        setValue(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value5(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value6(String value) {
        setLang(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value7(String value) {
        setComplexity(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value8(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value9(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value10(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value11(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value12(LocalDateTime value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public LexemeNoteRecord value13(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public LexemeNoteRecord values(Long value1, Long value2, Long value3, String value4, String value5, String value6, String value7, Boolean value8, String value9, LocalDateTime value10, String value11, LocalDateTime value12, Long value13) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LexemeNoteRecord
     */
    public LexemeNoteRecord() {
        super(LexemeNote.LEXEME_NOTE);
    }

    /**
     * Create a detached, initialised LexemeNoteRecord
     */
    public LexemeNoteRecord(Long id, Long originalFreeformId, Long lexemeId, String value, String valuePrese, String lang, String complexity, Boolean isPublic, String createdBy, LocalDateTime createdOn, String modifiedBy, LocalDateTime modifiedOn, Long orderBy) {
        super(LexemeNote.LEXEME_NOTE);

        setId(id);
        setOriginalFreeformId(originalFreeformId);
        setLexemeId(lexemeId);
        setValue(value);
        setValuePrese(valuePrese);
        setLang(lang);
        setComplexity(complexity);
        setIsPublic(isPublic);
        setCreatedBy(createdBy);
        setCreatedOn(createdOn);
        setModifiedBy(modifiedBy);
        setModifiedOn(modifiedOn);
        setOrderBy(orderBy);
    }
}
