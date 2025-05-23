/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.LexemeRegion;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexemeRegionRecord extends UpdatableRecordImpl<LexemeRegionRecord> implements Record4<Long, Long, String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.lexeme_region.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lexeme_region.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.lexeme_region.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lexeme_region.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.lexeme_region.region_code</code>.
     */
    public void setRegionCode(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lexeme_region.region_code</code>.
     */
    public String getRegionCode() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.lexeme_region.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lexeme_region.order_by</code>.
     */
    public Long getOrderBy() {
        return (Long) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, Long, String, Long> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return LexemeRegion.LEXEME_REGION.ID;
    }

    @Override
    public Field<Long> field2() {
        return LexemeRegion.LEXEME_REGION.LEXEME_ID;
    }

    @Override
    public Field<String> field3() {
        return LexemeRegion.LEXEME_REGION.REGION_CODE;
    }

    @Override
    public Field<Long> field4() {
        return LexemeRegion.LEXEME_REGION.ORDER_BY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getLexemeId();
    }

    @Override
    public String component3() {
        return getRegionCode();
    }

    @Override
    public Long component4() {
        return getOrderBy();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getLexemeId();
    }

    @Override
    public String value3() {
        return getRegionCode();
    }

    @Override
    public Long value4() {
        return getOrderBy();
    }

    @Override
    public LexemeRegionRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LexemeRegionRecord value2(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public LexemeRegionRecord value3(String value) {
        setRegionCode(value);
        return this;
    }

    @Override
    public LexemeRegionRecord value4(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public LexemeRegionRecord values(Long value1, Long value2, String value3, Long value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LexemeRegionRecord
     */
    public LexemeRegionRecord() {
        super(LexemeRegion.LEXEME_REGION);
    }

    /**
     * Create a detached, initialised LexemeRegionRecord
     */
    public LexemeRegionRecord(Long id, Long lexemeId, String regionCode, Long orderBy) {
        super(LexemeRegion.LEXEME_REGION);

        setId(id);
        setLexemeId(lexemeId);
        setRegionCode(regionCode);
        setOrderBy(orderBy);
    }
}
