/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.Freeform;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FreeformRecord extends UpdatableRecordImpl<FreeformRecord> implements Record15<Long, Long, String, String, String, Timestamp, BigDecimal, String, String, Long, Boolean, String, Timestamp, String, Timestamp> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.freeform.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.freeform.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.freeform.parent_id</code>.
     */
    public void setParentId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.freeform.parent_id</code>.
     */
    public Long getParentId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.freeform.freeform_type_code</code>.
     */
    public void setFreeformTypeCode(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.freeform.freeform_type_code</code>.
     */
    public String getFreeformTypeCode() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.freeform.value_text</code>.
     */
    public void setValueText(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.freeform.value_text</code>.
     */
    public String getValueText() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.freeform.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.freeform.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.freeform.value_date</code>.
     */
    public void setValueDate(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.freeform.value_date</code>.
     */
    public Timestamp getValueDate() {
        return (Timestamp) get(5);
    }

    /**
     * Setter for <code>public.freeform.value_number</code>.
     */
    public void setValueNumber(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.freeform.value_number</code>.
     */
    public BigDecimal getValueNumber() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>public.freeform.lang</code>.
     */
    public void setLang(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.freeform.lang</code>.
     */
    public String getLang() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.freeform.complexity</code>.
     */
    public void setComplexity(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.freeform.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.freeform.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.freeform.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>public.freeform.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.freeform.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(10);
    }

    /**
     * Setter for <code>public.freeform.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.freeform.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.freeform.created_on</code>.
     */
    public void setCreatedOn(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.freeform.created_on</code>.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(12);
    }

    /**
     * Setter for <code>public.freeform.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.freeform.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(13);
    }

    /**
     * Setter for <code>public.freeform.modified_on</code>.
     */
    public void setModifiedOn(Timestamp value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.freeform.modified_on</code>.
     */
    public Timestamp getModifiedOn() {
        return (Timestamp) get(14);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, Long, String, String, String, Timestamp, BigDecimal, String, String, Long, Boolean, String, Timestamp, String, Timestamp> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<Long, Long, String, String, String, Timestamp, BigDecimal, String, String, Long, Boolean, String, Timestamp, String, Timestamp> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Freeform.FREEFORM.ID;
    }

    @Override
    public Field<Long> field2() {
        return Freeform.FREEFORM.PARENT_ID;
    }

    @Override
    public Field<String> field3() {
        return Freeform.FREEFORM.FREEFORM_TYPE_CODE;
    }

    @Override
    public Field<String> field4() {
        return Freeform.FREEFORM.VALUE_TEXT;
    }

    @Override
    public Field<String> field5() {
        return Freeform.FREEFORM.VALUE_PRESE;
    }

    @Override
    public Field<Timestamp> field6() {
        return Freeform.FREEFORM.VALUE_DATE;
    }

    @Override
    public Field<BigDecimal> field7() {
        return Freeform.FREEFORM.VALUE_NUMBER;
    }

    @Override
    public Field<String> field8() {
        return Freeform.FREEFORM.LANG;
    }

    @Override
    public Field<String> field9() {
        return Freeform.FREEFORM.COMPLEXITY;
    }

    @Override
    public Field<Long> field10() {
        return Freeform.FREEFORM.ORDER_BY;
    }

    @Override
    public Field<Boolean> field11() {
        return Freeform.FREEFORM.IS_PUBLIC;
    }

    @Override
    public Field<String> field12() {
        return Freeform.FREEFORM.CREATED_BY;
    }

    @Override
    public Field<Timestamp> field13() {
        return Freeform.FREEFORM.CREATED_ON;
    }

    @Override
    public Field<String> field14() {
        return Freeform.FREEFORM.MODIFIED_BY;
    }

    @Override
    public Field<Timestamp> field15() {
        return Freeform.FREEFORM.MODIFIED_ON;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getParentId();
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
    public Timestamp component6() {
        return getValueDate();
    }

    @Override
    public BigDecimal component7() {
        return getValueNumber();
    }

    @Override
    public String component8() {
        return getLang();
    }

    @Override
    public String component9() {
        return getComplexity();
    }

    @Override
    public Long component10() {
        return getOrderBy();
    }

    @Override
    public Boolean component11() {
        return getIsPublic();
    }

    @Override
    public String component12() {
        return getCreatedBy();
    }

    @Override
    public Timestamp component13() {
        return getCreatedOn();
    }

    @Override
    public String component14() {
        return getModifiedBy();
    }

    @Override
    public Timestamp component15() {
        return getModifiedOn();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getParentId();
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
    public Timestamp value6() {
        return getValueDate();
    }

    @Override
    public BigDecimal value7() {
        return getValueNumber();
    }

    @Override
    public String value8() {
        return getLang();
    }

    @Override
    public String value9() {
        return getComplexity();
    }

    @Override
    public Long value10() {
        return getOrderBy();
    }

    @Override
    public Boolean value11() {
        return getIsPublic();
    }

    @Override
    public String value12() {
        return getCreatedBy();
    }

    @Override
    public Timestamp value13() {
        return getCreatedOn();
    }

    @Override
    public String value14() {
        return getModifiedBy();
    }

    @Override
    public Timestamp value15() {
        return getModifiedOn();
    }

    @Override
    public FreeformRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public FreeformRecord value2(Long value) {
        setParentId(value);
        return this;
    }

    @Override
    public FreeformRecord value3(String value) {
        setFreeformTypeCode(value);
        return this;
    }

    @Override
    public FreeformRecord value4(String value) {
        setValueText(value);
        return this;
    }

    @Override
    public FreeformRecord value5(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public FreeformRecord value6(Timestamp value) {
        setValueDate(value);
        return this;
    }

    @Override
    public FreeformRecord value7(BigDecimal value) {
        setValueNumber(value);
        return this;
    }

    @Override
    public FreeformRecord value8(String value) {
        setLang(value);
        return this;
    }

    @Override
    public FreeformRecord value9(String value) {
        setComplexity(value);
        return this;
    }

    @Override
    public FreeformRecord value10(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public FreeformRecord value11(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public FreeformRecord value12(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public FreeformRecord value13(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public FreeformRecord value14(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public FreeformRecord value15(Timestamp value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public FreeformRecord values(Long value1, Long value2, String value3, String value4, String value5, Timestamp value6, BigDecimal value7, String value8, String value9, Long value10, Boolean value11, String value12, Timestamp value13, String value14, Timestamp value15) {
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
     * Create a detached FreeformRecord
     */
    public FreeformRecord() {
        super(Freeform.FREEFORM);
    }

    /**
     * Create a detached, initialised FreeformRecord
     */
    public FreeformRecord(Long id, Long parentId, String freeformTypeCode, String valueText, String valuePrese, Timestamp valueDate, BigDecimal valueNumber, String lang, String complexity, Long orderBy, Boolean isPublic, String createdBy, Timestamp createdOn, String modifiedBy, Timestamp modifiedOn) {
        super(Freeform.FREEFORM);

        setId(id);
        setParentId(parentId);
        setFreeformTypeCode(freeformTypeCode);
        setValueText(valueText);
        setValuePrese(valuePrese);
        setValueDate(valueDate);
        setValueNumber(valueNumber);
        setLang(lang);
        setComplexity(complexity);
        setOrderBy(orderBy);
        setIsPublic(isPublic);
        setCreatedBy(createdBy);
        setCreatedOn(createdOn);
        setModifiedBy(modifiedBy);
        setModifiedOn(modifiedOn);
    }
}
