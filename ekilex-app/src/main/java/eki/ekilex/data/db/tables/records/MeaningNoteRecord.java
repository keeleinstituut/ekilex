/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.MeaningNote;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MeaningNoteRecord extends UpdatableRecordImpl<MeaningNoteRecord> implements Record13<Long, Long, Long, String, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.meaning_note.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.meaning_note.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.meaning_note.original_freeform_id</code>.
     */
    public void setOriginalFreeformId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.meaning_note.original_freeform_id</code>.
     */
    public Long getOriginalFreeformId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.meaning_note.meaning_id</code>.
     */
    public void setMeaningId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.meaning_note.meaning_id</code>.
     */
    public Long getMeaningId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.meaning_note.value</code>.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.meaning_note.value</code>.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.meaning_note.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.meaning_note.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.meaning_note.lang</code>.
     */
    public void setLang(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.meaning_note.lang</code>.
     */
    public String getLang() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.meaning_note.complexity</code>.
     */
    public void setComplexity(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.meaning_note.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.meaning_note.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.meaning_note.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>public.meaning_note.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.meaning_note.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.meaning_note.created_on</code>.
     */
    public void setCreatedOn(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.meaning_note.created_on</code>.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>public.meaning_note.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.meaning_note.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.meaning_note.modified_on</code>.
     */
    public void setModifiedOn(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.meaning_note.modified_on</code>.
     */
    public Timestamp getModifiedOn() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>public.meaning_note.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.meaning_note.order_by</code>.
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
    public Row13<Long, Long, Long, String, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<Long, Long, Long, String, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MeaningNote.MEANING_NOTE.ID;
    }

    @Override
    public Field<Long> field2() {
        return MeaningNote.MEANING_NOTE.ORIGINAL_FREEFORM_ID;
    }

    @Override
    public Field<Long> field3() {
        return MeaningNote.MEANING_NOTE.MEANING_ID;
    }

    @Override
    public Field<String> field4() {
        return MeaningNote.MEANING_NOTE.VALUE;
    }

    @Override
    public Field<String> field5() {
        return MeaningNote.MEANING_NOTE.VALUE_PRESE;
    }

    @Override
    public Field<String> field6() {
        return MeaningNote.MEANING_NOTE.LANG;
    }

    @Override
    public Field<String> field7() {
        return MeaningNote.MEANING_NOTE.COMPLEXITY;
    }

    @Override
    public Field<Boolean> field8() {
        return MeaningNote.MEANING_NOTE.IS_PUBLIC;
    }

    @Override
    public Field<String> field9() {
        return MeaningNote.MEANING_NOTE.CREATED_BY;
    }

    @Override
    public Field<Timestamp> field10() {
        return MeaningNote.MEANING_NOTE.CREATED_ON;
    }

    @Override
    public Field<String> field11() {
        return MeaningNote.MEANING_NOTE.MODIFIED_BY;
    }

    @Override
    public Field<Timestamp> field12() {
        return MeaningNote.MEANING_NOTE.MODIFIED_ON;
    }

    @Override
    public Field<Long> field13() {
        return MeaningNote.MEANING_NOTE.ORDER_BY;
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
        return getMeaningId();
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
        return getMeaningId();
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
    public Long value13() {
        return getOrderBy();
    }

    @Override
    public MeaningNoteRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value2(Long value) {
        setOriginalFreeformId(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value3(Long value) {
        setMeaningId(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value4(String value) {
        setValue(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value5(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value6(String value) {
        setLang(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value7(String value) {
        setComplexity(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value8(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value9(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value10(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value11(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value12(Timestamp value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public MeaningNoteRecord value13(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public MeaningNoteRecord values(Long value1, Long value2, Long value3, String value4, String value5, String value6, String value7, Boolean value8, String value9, Timestamp value10, String value11, Timestamp value12, Long value13) {
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
     * Create a detached MeaningNoteRecord
     */
    public MeaningNoteRecord() {
        super(MeaningNote.MEANING_NOTE);
    }

    /**
     * Create a detached, initialised MeaningNoteRecord
     */
    public MeaningNoteRecord(Long id, Long originalFreeformId, Long meaningId, String value, String valuePrese, String lang, String complexity, Boolean isPublic, String createdBy, Timestamp createdOn, String modifiedBy, Timestamp modifiedOn, Long orderBy) {
        super(MeaningNote.MEANING_NOTE);

        setId(id);
        setOriginalFreeformId(originalFreeformId);
        setMeaningId(meaningId);
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
