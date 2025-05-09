/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.UsageDefinition;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsageDefinitionRecord extends UpdatableRecordImpl<UsageDefinitionRecord> implements Record11<Long, Long, Long, String, String, String, String, LocalDateTime, String, LocalDateTime, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.usage_definition.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.usage_definition.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.usage_definition.original_freeform_id</code>.
     */
    public void setOriginalFreeformId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.usage_definition.original_freeform_id</code>.
     */
    public Long getOriginalFreeformId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.usage_definition.usage_id</code>.
     */
    public void setUsageId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.usage_definition.usage_id</code>.
     */
    public Long getUsageId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.usage_definition.value</code>.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.usage_definition.value</code>.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.usage_definition.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.usage_definition.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.usage_definition.lang</code>.
     */
    public void setLang(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.usage_definition.lang</code>.
     */
    public String getLang() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.usage_definition.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.usage_definition.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.usage_definition.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.usage_definition.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>public.usage_definition.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.usage_definition.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.usage_definition.modified_on</code>.
     */
    public void setModifiedOn(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.usage_definition.modified_on</code>.
     */
    public LocalDateTime getModifiedOn() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>public.usage_definition.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.usage_definition.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, Long, Long, String, String, String, String, LocalDateTime, String, LocalDateTime, Long> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Long, Long, Long, String, String, String, String, LocalDateTime, String, LocalDateTime, Long> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return UsageDefinition.USAGE_DEFINITION.ID;
    }

    @Override
    public Field<Long> field2() {
        return UsageDefinition.USAGE_DEFINITION.ORIGINAL_FREEFORM_ID;
    }

    @Override
    public Field<Long> field3() {
        return UsageDefinition.USAGE_DEFINITION.USAGE_ID;
    }

    @Override
    public Field<String> field4() {
        return UsageDefinition.USAGE_DEFINITION.VALUE;
    }

    @Override
    public Field<String> field5() {
        return UsageDefinition.USAGE_DEFINITION.VALUE_PRESE;
    }

    @Override
    public Field<String> field6() {
        return UsageDefinition.USAGE_DEFINITION.LANG;
    }

    @Override
    public Field<String> field7() {
        return UsageDefinition.USAGE_DEFINITION.CREATED_BY;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return UsageDefinition.USAGE_DEFINITION.CREATED_ON;
    }

    @Override
    public Field<String> field9() {
        return UsageDefinition.USAGE_DEFINITION.MODIFIED_BY;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return UsageDefinition.USAGE_DEFINITION.MODIFIED_ON;
    }

    @Override
    public Field<Long> field11() {
        return UsageDefinition.USAGE_DEFINITION.ORDER_BY;
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
        return getUsageId();
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
        return getCreatedBy();
    }

    @Override
    public LocalDateTime component8() {
        return getCreatedOn();
    }

    @Override
    public String component9() {
        return getModifiedBy();
    }

    @Override
    public LocalDateTime component10() {
        return getModifiedOn();
    }

    @Override
    public Long component11() {
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
        return getUsageId();
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
        return getCreatedBy();
    }

    @Override
    public LocalDateTime value8() {
        return getCreatedOn();
    }

    @Override
    public String value9() {
        return getModifiedBy();
    }

    @Override
    public LocalDateTime value10() {
        return getModifiedOn();
    }

    @Override
    public Long value11() {
        return getOrderBy();
    }

    @Override
    public UsageDefinitionRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value2(Long value) {
        setOriginalFreeformId(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value3(Long value) {
        setUsageId(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value4(String value) {
        setValue(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value5(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value6(String value) {
        setLang(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value7(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value8(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value9(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value10(LocalDateTime value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord value11(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public UsageDefinitionRecord values(Long value1, Long value2, Long value3, String value4, String value5, String value6, String value7, LocalDateTime value8, String value9, LocalDateTime value10, Long value11) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UsageDefinitionRecord
     */
    public UsageDefinitionRecord() {
        super(UsageDefinition.USAGE_DEFINITION);
    }

    /**
     * Create a detached, initialised UsageDefinitionRecord
     */
    public UsageDefinitionRecord(Long id, Long originalFreeformId, Long usageId, String value, String valuePrese, String lang, String createdBy, LocalDateTime createdOn, String modifiedBy, LocalDateTime modifiedOn, Long orderBy) {
        super(UsageDefinition.USAGE_DEFINITION);

        setId(id);
        setOriginalFreeformId(originalFreeformId);
        setUsageId(usageId);
        setValue(value);
        setValuePrese(valuePrese);
        setLang(lang);
        setCreatedBy(createdBy);
        setCreatedOn(createdOn);
        setModifiedBy(modifiedBy);
        setModifiedOn(modifiedOn);
        setOrderBy(orderBy);
    }
}
