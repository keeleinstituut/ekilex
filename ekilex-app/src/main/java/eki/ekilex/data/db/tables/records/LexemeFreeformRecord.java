/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.LexemeFreeform;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexemeFreeformRecord extends UpdatableRecordImpl<LexemeFreeformRecord> implements Record4<Long, Long, Long, Long> {

    private static final long serialVersionUID = 444499297;

    /**
     * Setter for <code>public.lexeme_freeform.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lexeme_freeform.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.lexeme_freeform.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lexeme_freeform.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.lexeme_freeform.freeform_id</code>.
     */
    public void setFreeformId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lexeme_freeform.freeform_id</code>.
     */
    public Long getFreeformId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.lexeme_freeform.order_by</code>.
     */
    public void setOrderBy(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lexeme_freeform.order_by</code>.
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
    public Row4<Long, Long, Long, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, Long, Long, Long> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return LexemeFreeform.LEXEME_FREEFORM.ID;
    }

    @Override
    public Field<Long> field2() {
        return LexemeFreeform.LEXEME_FREEFORM.LEXEME_ID;
    }

    @Override
    public Field<Long> field3() {
        return LexemeFreeform.LEXEME_FREEFORM.FREEFORM_ID;
    }

    @Override
    public Field<Long> field4() {
        return LexemeFreeform.LEXEME_FREEFORM.ORDER_BY;
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
    public Long component3() {
        return getFreeformId();
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
    public Long value3() {
        return getFreeformId();
    }

    @Override
    public Long value4() {
        return getOrderBy();
    }

    @Override
    public LexemeFreeformRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LexemeFreeformRecord value2(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public LexemeFreeformRecord value3(Long value) {
        setFreeformId(value);
        return this;
    }

    @Override
    public LexemeFreeformRecord value4(Long value) {
        setOrderBy(value);
        return this;
    }

    @Override
    public LexemeFreeformRecord values(Long value1, Long value2, Long value3, Long value4) {
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
     * Create a detached LexemeFreeformRecord
     */
    public LexemeFreeformRecord() {
        super(LexemeFreeform.LEXEME_FREEFORM);
    }

    /**
     * Create a detached, initialised LexemeFreeformRecord
     */
    public LexemeFreeformRecord(Long id, Long lexemeId, Long freeformId, Long orderBy) {
        super(LexemeFreeform.LEXEME_FREEFORM);

        set(0, id);
        set(1, lexemeId);
        set(2, freeformId);
        set(3, orderBy);
    }
}
