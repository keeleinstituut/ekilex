/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.MeaningImage;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MeaningImageRecord extends UpdatableRecordImpl<MeaningImageRecord> implements Record12<Long, Long, Long, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.meaning_image.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.meaning_image.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.meaning_image.original_freeform_id</code>.
     */
    public void setOriginalFreeformId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.meaning_image.original_freeform_id</code>.
     */
    public Long getOriginalFreeformId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.meaning_image.meaning_id</code>.
     */
    public void setMeaningId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.meaning_image.meaning_id</code>.
     */
    public Long getMeaningId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.meaning_image.title</code>.
     */
    public void setTitle(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.meaning_image.title</code>.
     */
    public String getTitle() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.meaning_image.url</code>.
     */
    public void setUrl(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.meaning_image.url</code>.
     */
    public String getUrl() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.meaning_image.complexity</code>.
     */
    public void setComplexity(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.meaning_image.complexity</code>.
     */
    public String getComplexity() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.meaning_image.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.meaning_image.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.meaning_image.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.meaning_image.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.meaning_image.created_on</code>.
     */
    public void setCreatedOn(Timestamp value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.meaning_image.created_on</code>.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(8);
    }

    /**
     * Setter for <code>public.meaning_image.modified_by</code>.
     */
    public void setModifiedBy(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.meaning_image.modified_by</code>.
     */
    public String getModifiedBy() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.meaning_image.modified_on</code>.
     */
    public void setModifiedOn(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.meaning_image.modified_on</code>.
     */
    public Timestamp getModifiedOn() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>public.meaning_image.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.meaning_image.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(11);
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
    public Row12<Long, Long, Long, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, Long, Long, String, String, String, Boolean, String, Timestamp, String, Timestamp, Long> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MeaningImage.MEANING_IMAGE.ID;
    }

    @Override
    public Field<Long> field2() {
        return MeaningImage.MEANING_IMAGE.ORIGINAL_FREEFORM_ID;
    }

    @Override
    public Field<Long> field3() {
        return MeaningImage.MEANING_IMAGE.MEANING_ID;
    }

    @Override
    public Field<String> field4() {
        return MeaningImage.MEANING_IMAGE.TITLE;
    }

    @Override
    public Field<String> field5() {
        return MeaningImage.MEANING_IMAGE.URL;
    }

    @Override
    public Field<String> field6() {
        return MeaningImage.MEANING_IMAGE.COMPLEXITY;
    }

    @Override
    public Field<Boolean> field7() {
        return MeaningImage.MEANING_IMAGE.IS_PUBLIC;
    }

    @Override
    public Field<String> field8() {
        return MeaningImage.MEANING_IMAGE.CREATED_BY;
    }

    @Override
    public Field<Timestamp> field9() {
        return MeaningImage.MEANING_IMAGE.CREATED_ON;
    }

    @Override
    public Field<String> field10() {
        return MeaningImage.MEANING_IMAGE.MODIFIED_BY;
    }

    @Override
    public Field<Timestamp> field11() {
        return MeaningImage.MEANING_IMAGE.MODIFIED_ON;
    }

    @Override
    public Field<Long> field12() {
        return MeaningImage.MEANING_IMAGE.ORDER_BY;
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
        return getTitle();
    }

    @Override
    public String component5() {
        return getUrl();
    }

    @Override
    public String component6() {
        return getComplexity();
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
    public Timestamp component9() {
        return getCreatedOn();
    }

    @Override
    public String component10() {
        return getModifiedBy();
    }

    @Override
    public Timestamp component11() {
        return getModifiedOn();
    }

    @Override
    public Long component12() {
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
        return getTitle();
    }

    @Override
    public String value5() {
        return getUrl();
    }

    @Override
    public String value6() {
        return getComplexity();
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
    public Timestamp value9() {
        return getCreatedOn();
    }

    @Override
    public String value10() {
        return getModifiedBy();
    }

    @Override
    public Timestamp value11() {
        return getModifiedOn();
    }

    @Override
    public Long value12() {
        return getOrderBy();
    }

    @Override
    public MeaningImageRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public MeaningImageRecord value2(Long value) {
        setOriginalFreeformId(value);
        return this;
    }

    @Override
    public MeaningImageRecord value3(Long value) {
        setMeaningId(value);
        return this;
    }

    @Override
    public MeaningImageRecord value4(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public MeaningImageRecord value5(String value) {
        setUrl(value);
        return this;
    }

    @Override
    public MeaningImageRecord value6(String value) {
        setComplexity(value);
        return this;
    }

    @Override
    public MeaningImageRecord value7(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public MeaningImageRecord value8(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public MeaningImageRecord value9(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public MeaningImageRecord value10(String value) {
        setModifiedBy(value);
        return this;
    }

    @Override
    public MeaningImageRecord value11(Timestamp value) {
        setModifiedOn(value);
        return this;
    }

    @Override
    public MeaningImageRecord value12(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public MeaningImageRecord values(Long value1, Long value2, Long value3, String value4, String value5, String value6, Boolean value7, String value8, Timestamp value9, String value10, Timestamp value11, Long value12) {
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
     * Create a detached MeaningImageRecord
     */
    public MeaningImageRecord() {
        super(MeaningImage.MEANING_IMAGE);
    }

    /**
     * Create a detached, initialised MeaningImageRecord
     */
    public MeaningImageRecord(Long id, Long originalFreeformId, Long meaningId, String title, String url, String complexity, Boolean isPublic, String createdBy, Timestamp createdOn, String modifiedBy, Timestamp modifiedOn, Long orderBy) {
        super(MeaningImage.MEANING_IMAGE);

        setId(id);
        setOriginalFreeformId(originalFreeformId);
        setMeaningId(meaningId);
        setTitle(title);
        setUrl(url);
        setComplexity(complexity);
        setIsPublic(isPublic);
        setCreatedBy(createdBy);
        setCreatedOn(createdOn);
        setModifiedBy(modifiedBy);
        setModifiedOn(modifiedOn);
        setOrderBy(orderBy);
    }
}