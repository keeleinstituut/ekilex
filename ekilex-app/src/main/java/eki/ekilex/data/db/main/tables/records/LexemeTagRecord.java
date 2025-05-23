/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables.records;


import eki.ekilex.data.db.main.tables.LexemeTag;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexemeTagRecord extends UpdatableRecordImpl<LexemeTagRecord> implements Record4<Long, Long, String, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.lexeme_tag.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lexeme_tag.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.lexeme_tag.lexeme_id</code>.
     */
    public void setLexemeId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lexeme_tag.lexeme_id</code>.
     */
    public Long getLexemeId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.lexeme_tag.tag_name</code>.
     */
    public void setTagName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lexeme_tag.tag_name</code>.
     */
    public String getTagName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.lexeme_tag.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lexeme_tag.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(3);
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
    public Row4<Long, Long, String, LocalDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, Long, String, LocalDateTime> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return LexemeTag.LEXEME_TAG.ID;
    }

    @Override
    public Field<Long> field2() {
        return LexemeTag.LEXEME_TAG.LEXEME_ID;
    }

    @Override
    public Field<String> field3() {
        return LexemeTag.LEXEME_TAG.TAG_NAME;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return LexemeTag.LEXEME_TAG.CREATED_ON;
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
        return getTagName();
    }

    @Override
    public LocalDateTime component4() {
        return getCreatedOn();
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
        return getTagName();
    }

    @Override
    public LocalDateTime value4() {
        return getCreatedOn();
    }

    @Override
    public LexemeTagRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LexemeTagRecord value2(Long value) {
        setLexemeId(value);
        return this;
    }

    @Override
    public LexemeTagRecord value3(String value) {
        setTagName(value);
        return this;
    }

    @Override
    public LexemeTagRecord value4(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public LexemeTagRecord values(Long value1, Long value2, String value3, LocalDateTime value4) {
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
     * Create a detached LexemeTagRecord
     */
    public LexemeTagRecord() {
        super(LexemeTag.LEXEME_TAG);
    }

    /**
     * Create a detached, initialised LexemeTagRecord
     */
    public LexemeTagRecord(Long id, Long lexemeId, String tagName, LocalDateTime createdOn) {
        super(LexemeTag.LEXEME_TAG);

        setId(id);
        setLexemeId(lexemeId);
        setTagName(tagName);
        setCreatedOn(createdOn);
    }
}
