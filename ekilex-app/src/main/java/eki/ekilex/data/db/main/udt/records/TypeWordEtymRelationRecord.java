/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt.records;


import eki.ekilex.data.db.main.udt.TypeWordEtymRelation;

import org.jooq.Field;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeWordEtymRelationRecord extends UDTRecordImpl<TypeWordEtymRelationRecord> implements Record5<Long, String, Boolean, Boolean, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_word_etym_relation.word_etym_rel_id</code>.
     */
    public void setWordEtymRelId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_word_etym_relation.word_etym_rel_id</code>.
     */
    public Long getWordEtymRelId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.type_word_etym_relation.comment</code>.
     */
    public void setComment(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_word_etym_relation.comment</code>.
     */
    public String getComment() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.type_word_etym_relation.is_questionable</code>.
     */
    public void setIsQuestionable(Boolean value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.type_word_etym_relation.is_questionable</code>.
     */
    public Boolean getIsQuestionable() {
        return (Boolean) get(2);
    }

    /**
     * Setter for <code>public.type_word_etym_relation.is_compound</code>.
     */
    public void setIsCompound(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.type_word_etym_relation.is_compound</code>.
     */
    public Boolean getIsCompound() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>public.type_word_etym_relation.related_word_id</code>.
     */
    public void setRelatedWordId(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.type_word_etym_relation.related_word_id</code>.
     */
    public Long getRelatedWordId() {
        return (Long) get(4);
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, Boolean, Boolean, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, String, Boolean, Boolean, Long> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TypeWordEtymRelation.WORD_ETYM_REL_ID;
    }

    @Override
    public Field<String> field2() {
        return TypeWordEtymRelation.COMMENT;
    }

    @Override
    public Field<Boolean> field3() {
        return TypeWordEtymRelation.IS_QUESTIONABLE;
    }

    @Override
    public Field<Boolean> field4() {
        return TypeWordEtymRelation.IS_COMPOUND;
    }

    @Override
    public Field<Long> field5() {
        return TypeWordEtymRelation.RELATED_WORD_ID;
    }

    @Override
    public Long component1() {
        return getWordEtymRelId();
    }

    @Override
    public String component2() {
        return getComment();
    }

    @Override
    public Boolean component3() {
        return getIsQuestionable();
    }

    @Override
    public Boolean component4() {
        return getIsCompound();
    }

    @Override
    public Long component5() {
        return getRelatedWordId();
    }

    @Override
    public Long value1() {
        return getWordEtymRelId();
    }

    @Override
    public String value2() {
        return getComment();
    }

    @Override
    public Boolean value3() {
        return getIsQuestionable();
    }

    @Override
    public Boolean value4() {
        return getIsCompound();
    }

    @Override
    public Long value5() {
        return getRelatedWordId();
    }

    @Override
    public TypeWordEtymRelationRecord value1(Long value) {
        setWordEtymRelId(value);
        return this;
    }

    @Override
    public TypeWordEtymRelationRecord value2(String value) {
        setComment(value);
        return this;
    }

    @Override
    public TypeWordEtymRelationRecord value3(Boolean value) {
        setIsQuestionable(value);
        return this;
    }

    @Override
    public TypeWordEtymRelationRecord value4(Boolean value) {
        setIsCompound(value);
        return this;
    }

    @Override
    public TypeWordEtymRelationRecord value5(Long value) {
        setRelatedWordId(value);
        return this;
    }

    @Override
    public TypeWordEtymRelationRecord values(Long value1, String value2, Boolean value3, Boolean value4, Long value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeWordEtymRelationRecord
     */
    public TypeWordEtymRelationRecord() {
        super(TypeWordEtymRelation.TYPE_WORD_ETYM_RELATION);
    }

    /**
     * Create a detached, initialised TypeWordEtymRelationRecord
     */
    public TypeWordEtymRelationRecord(Long wordEtymRelId, String comment, Boolean isQuestionable, Boolean isCompound, Long relatedWordId) {
        super(TypeWordEtymRelation.TYPE_WORD_ETYM_RELATION);

        setWordEtymRelId(wordEtymRelId);
        setComment(comment);
        setIsQuestionable(isQuestionable);
        setIsCompound(isCompound);
        setRelatedWordId(relatedWordId);
    }
}
