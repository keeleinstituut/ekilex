/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt.records;


import eki.ekilex.data.db.udt.TypeWordHomNrDataTuple;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeWordHomNrDataTupleRecord extends UDTRecordImpl<TypeWordHomNrDataTupleRecord> implements Record2<Long, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.type_word_hom_nr_data_tuple.word_id</code>.
     */
    public void setWordId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.type_word_hom_nr_data_tuple.word_id</code>.
     */
    public Long getWordId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.type_word_hom_nr_data_tuple.homonym_nr</code>.
     */
    public void setHomonymNr(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.type_word_hom_nr_data_tuple.homonym_nr</code>.
     */
    public Integer getHomonymNr() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Long, Integer> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return TypeWordHomNrDataTuple.WORD_ID;
    }

    @Override
    public Field<Integer> field2() {
        return TypeWordHomNrDataTuple.HOMONYM_NR;
    }

    @Override
    public Long component1() {
        return getWordId();
    }

    @Override
    public Integer component2() {
        return getHomonymNr();
    }

    @Override
    public Long value1() {
        return getWordId();
    }

    @Override
    public Integer value2() {
        return getHomonymNr();
    }

    @Override
    public TypeWordHomNrDataTupleRecord value1(Long value) {
        setWordId(value);
        return this;
    }

    @Override
    public TypeWordHomNrDataTupleRecord value2(Integer value) {
        setHomonymNr(value);
        return this;
    }

    @Override
    public TypeWordHomNrDataTupleRecord values(Long value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TypeWordHomNrDataTupleRecord
     */
    public TypeWordHomNrDataTupleRecord() {
        super(TypeWordHomNrDataTuple.TYPE_WORD_HOM_NR_DATA_TUPLE);
    }

    /**
     * Create a detached, initialised TypeWordHomNrDataTupleRecord
     */
    public TypeWordHomNrDataTupleRecord(Long wordId, Integer homonymNr) {
        super(TypeWordHomNrDataTuple.TYPE_WORD_HOM_NR_DATA_TUPLE);

        setWordId(wordId);
        setHomonymNr(homonymNr);
    }
}
