/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.Paradigm;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ParadigmRecord extends UpdatableRecordImpl<ParadigmRecord> implements Record7<Long, Long, String, String, String, Boolean, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.paradigm.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.paradigm.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.paradigm.word_id</code>.
     */
    public void setWordId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.paradigm.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.paradigm.comment</code>.
     */
    public void setComment(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.paradigm.comment</code>.
     */
    public String getComment() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.paradigm.inflection_type_nr</code>.
     */
    public void setInflectionTypeNr(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.paradigm.inflection_type_nr</code>.
     */
    public String getInflectionTypeNr() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.paradigm.inflection_type</code>.
     */
    public void setInflectionType(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.paradigm.inflection_type</code>.
     */
    public String getInflectionType() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.paradigm.is_secondary</code>.
     */
    public void setIsSecondary(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.paradigm.is_secondary</code>.
     */
    public Boolean getIsSecondary() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>public.paradigm.word_class</code>.
     */
    public void setWordClass(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.paradigm.word_class</code>.
     */
    public String getWordClass() {
        return (String) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, Long, String, String, String, Boolean, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, Long, String, String, String, Boolean, String> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Paradigm.PARADIGM.ID;
    }

    @Override
    public Field<Long> field2() {
        return Paradigm.PARADIGM.WORD_ID;
    }

    @Override
    public Field<String> field3() {
        return Paradigm.PARADIGM.COMMENT;
    }

    @Override
    public Field<String> field4() {
        return Paradigm.PARADIGM.INFLECTION_TYPE_NR;
    }

    @Override
    public Field<String> field5() {
        return Paradigm.PARADIGM.INFLECTION_TYPE;
    }

    @Override
    public Field<Boolean> field6() {
        return Paradigm.PARADIGM.IS_SECONDARY;
    }

    @Override
    public Field<String> field7() {
        return Paradigm.PARADIGM.WORD_CLASS;
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
        return getComment();
    }

    @Override
    public String component4() {
        return getInflectionTypeNr();
    }

    @Override
    public String component5() {
        return getInflectionType();
    }

    @Override
    public Boolean component6() {
        return getIsSecondary();
    }

    @Override
    public String component7() {
        return getWordClass();
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
        return getComment();
    }

    @Override
    public String value4() {
        return getInflectionTypeNr();
    }

    @Override
    public String value5() {
        return getInflectionType();
    }

    @Override
    public Boolean value6() {
        return getIsSecondary();
    }

    @Override
    public String value7() {
        return getWordClass();
    }

    @Override
    public ParadigmRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public ParadigmRecord value2(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public ParadigmRecord value3(String value) {
        setComment(value);
        return this;
    }

    @Override
    public ParadigmRecord value4(String value) {
        setInflectionTypeNr(value);
        return this;
    }

    @Override
    public ParadigmRecord value5(String value) {
        setInflectionType(value);
        return this;
    }

    @Override
    public ParadigmRecord value6(Boolean value) {
        setIsSecondary(value);
        return this;
    }

    @Override
    public ParadigmRecord value7(String value) {
        setWordClass(value);
        return this;
    }

    @Override
    public ParadigmRecord values(Long value1, Long value2, String value3, String value4, String value5, Boolean value6, String value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ParadigmRecord
     */
    public ParadigmRecord() {
        super(Paradigm.PARADIGM);
    }

    /**
     * Create a detached, initialised ParadigmRecord
     */
    public ParadigmRecord(Long id, Long wordId, String comment, String inflectionTypeNr, String inflectionType, Boolean isSecondary, String wordClass) {
        super(Paradigm.PARADIGM);

        setId(id);
        setWordId(wordId);
        setComment(comment);
        setInflectionTypeNr(inflectionTypeNr);
        setInflectionType(inflectionType);
        setIsSecondary(isSecondary);
        setWordClass(wordClass);
    }
}
