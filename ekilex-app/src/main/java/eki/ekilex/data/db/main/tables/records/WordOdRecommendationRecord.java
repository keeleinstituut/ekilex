/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.WordOdRecommendation;

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
public class WordOdRecommendationRecord extends UpdatableRecordImpl<WordOdRecommendationRecord> implements Record11<Long, Long, String, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.word_od_recommendation.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.word_od_recommendation.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.word_od_recommendation.value</code>.
     */
    public void setValue(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.value</code>.
     */
    public String getValue() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.word_od_recommendation.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.word_od_recommendation.opt_value</code>.
     */
    public void setOptValue(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.opt_value</code>.
     */
    public String getOptValue() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.word_od_recommendation.opt_value_prese</code>.
     */
    public void setOptValuePrese(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.opt_value_prese</code>.
     */
    public String getOptValuePrese() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.word_od_recommendation.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.word_od_recommendation.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.word_od_recommendation.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>public.word_od_recommendation.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.word_od_recommendation.modified_on</code>.
     */
    public void setModifiedOn(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.word_od_recommendation.modified_on</code>.
     */
    public LocalDateTime getModifiedOn() {
        return (LocalDateTime) get(10);
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
    public Row11<Long, Long, String, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Long, Long, String, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.ID;
    }

    @Override
    public Field<Long> field2() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.WORD_ID;
    }

    @Override
    public Field<String> field3() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.VALUE;
    }

    @Override
    public Field<String> field4() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.VALUE_PRESE;
    }

    @Override
    public Field<String> field5() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.OPT_VALUE;
    }

    @Override
    public Field<String> field6() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.OPT_VALUE_PRESE;
    }

    @Override
    public Field<Boolean> field7() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.IS_PUBLIC;
    }

    @Override
    public Field<String> field8() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.CREATED_BY;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.CREATED_ON;
    }

    @Override
    public Field<String> field10() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.MODIFIED_BY;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return WordOdRecommendation.WORD_OD_RECOMMENDATION.MODIFIED_ON;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getWordId();
    }

    @Override
    public String component3() {
        return getValue();
    }

    @Override
    public String component4() {
        return getValuePrese();
    }

    @Override
    public String component5() {
        return getOptValue();
    }

    @Override
    public String component6() {
        return getOptValuePrese();
    }

    @Override
    public Boolean component7() {
        return getIsPublic();
    }

    @Override
    public String component8() {
        return getCreatedBy();
    }

    @Override
    public LocalDateTime component9() {
        return getCreatedOn();
    }

    @Override
    public String component10() {
        return getModifiedBy();
    }

    @Override
    public LocalDateTime component11() {
        return getModifiedOn();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getWordId();
    }

    @Override
    public String value3() {
        return getValue();
    }

    @Override
    public String value4() {
        return getValuePrese();
    }

    @Override
    public String value5() {
        return getOptValue();
    }

    @Override
    public String value6() {
        return getOptValuePrese();
    }

    @Override
    public Boolean value7() {
        return getIsPublic();
    }

    @Override
    public String value8() {
        return getCreatedBy();
    }

    @Override
    public LocalDateTime value9() {
        return getCreatedOn();
    }

    @Override
    public String value10() {
        return getModifiedBy();
    }

    @Override
    public LocalDateTime value11() {
        return getModifiedOn();
    }

    @Override
    public WordOdRecommendationRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value3(String value) {
        setValue(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value4(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value5(String value) {
        setOptValue(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value6(String value) {
        setOptValuePrese(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value7(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value8(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value9(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value10(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord value11(LocalDateTime value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public WordOdRecommendationRecord values(Long value1, Long value2, String value3, String value4, String value5, String value6, Boolean value7, String value8, LocalDateTime value9, String value10, LocalDateTime value11) {
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
     * Create a detached WordOdRecommendationRecord
     */
    public WordOdRecommendationRecord() {
        super(WordOdRecommendation.WORD_OD_RECOMMENDATION);
    }

    /**
     * Create a detached, initialised WordOdRecommendationRecord
     */
    public WordOdRecommendationRecord(Long id, Long wordId, String value, String valuePrese, String optValue, String optValuePrese, Boolean isPublic, String createdBy, LocalDateTime createdOn, String modifiedBy, LocalDateTime modifiedOn) {
        super(WordOdRecommendation.WORD_OD_RECOMMENDATION);

        setId(id);
        setWordId(wordId);
        setValue(value);
        setValuePrese(valuePrese);
        setOptValue(optValue);
        setOptValuePrese(optValuePrese);
        setIsPublic(isPublic);
        setCreatedBy(createdBy);
        setCreatedOn(createdOn);
        setModifiedBy(modifiedBy);
        setModifiedOn(modifiedOn);
    }
}
