/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.Source;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SourceRecord extends UpdatableRecordImpl<SourceRecord> implements Record8<Long, String, String, String, String, String, Boolean, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.source.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.source.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.source.type</code>.
     */
    public void setType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.source.type</code>.
     */
    public String getType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.source.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.source.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.source.value</code>.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.source.value</code>.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.source.value_prese</code>.
     */
    public void setValuePrese(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.source.value_prese</code>.
     */
    public String getValuePrese() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.source.comment</code>.
     */
    public void setComment(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.source.comment</code>.
     */
    public String getComment() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.source.is_public</code>.
     */
    public void setIsPublic(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.source.is_public</code>.
     */
    public Boolean getIsPublic() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.source.dataset_code</code>.
     */
    public void setDatasetCode(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.source.dataset_code</code>.
     */
    public String getDatasetCode() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, String, String, String, String, String, Boolean, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, String, String, String, String, String, Boolean, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Source.SOURCE.ID;
    }

    @Override
    public Field<String> field2() {
        return Source.SOURCE.TYPE;
    }

    @Override
    public Field<String> field3() {
        return Source.SOURCE.NAME;
    }

    @Override
    public Field<String> field4() {
        return Source.SOURCE.VALUE;
    }

    @Override
    public Field<String> field5() {
        return Source.SOURCE.VALUE_PRESE;
    }

    @Override
    public Field<String> field6() {
        return Source.SOURCE.COMMENT;
    }

    @Override
    public Field<Boolean> field7() {
        return Source.SOURCE.IS_PUBLIC;
    }

    @Override
    public Field<String> field8() {
        return Source.SOURCE.DATASET_CODE;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getType();
    }

    @Override
    public String component3() {
        return getName();
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
        return getComment();
    }

    @Override
    public Boolean component7() {
        return getIsPublic();
    }

    @Override
    public String component8() {
        return getDatasetCode();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getType();
    }

    @Override
    public String value3() {
        return getName();
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
        return getComment();
    }

    @Override
    public Boolean value7() {
        return getIsPublic();
    }

    @Override
    public String value8() {
        return getDatasetCode();
    }

    @Override
    public SourceRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public SourceRecord value2(String value) {
        setType(value);
        return this;
    }

    @Override
    public SourceRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public SourceRecord value4(String value) {
        setValue(value);
        return this;
    }

    @Override
    public SourceRecord value5(String value) {
        setValuePrese(value);
        return this;
    }

    @Override
    public SourceRecord value6(String value) {
        setComment(value);
        return this;
    }

    @Override
    public SourceRecord value7(Boolean value) {
        setIsPublic(value);
        return this;
    }

    @Override
    public SourceRecord value8(String value) {
        setDatasetCode(value);
        return this;
    }

    @Override
    public SourceRecord values(Long value1, String value2, String value3, String value4, String value5, String value6, Boolean value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SourceRecord
     */
    public SourceRecord() {
        super(Source.SOURCE);
    }

    /**
     * Create a detached, initialised SourceRecord
     */
    public SourceRecord(Long id, String type, String name, String value, String valuePrese, String comment, Boolean isPublic, String datasetCode) {
        super(Source.SOURCE);

        setId(id);
        setType(type);
        setName(name);
        setValue(value);
        setValuePrese(valuePrese);
        setComment(comment);
        setIsPublic(isPublic);
        setDatasetCode(datasetCode);
    }
}
