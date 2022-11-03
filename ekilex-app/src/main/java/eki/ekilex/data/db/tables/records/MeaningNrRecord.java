/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables.records;


import eki.ekilex.data.db.tables.MeaningNr;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MeaningNrRecord extends UpdatableRecordImpl<MeaningNrRecord> implements Record4<Long, Long, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.meaning_nr.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.meaning_nr.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.meaning_nr.meaning_id</code>.
     */
    public void setMeaningId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.meaning_nr.meaning_id</code>.
     */
    public Long getMeaningId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.meaning_nr.mnr</code>.
     */
    public void setMnr(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.meaning_nr.mnr</code>.
     */
    public String getMnr() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.meaning_nr.dataset_code</code>.
     */
    public void setDatasetCode(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.meaning_nr.dataset_code</code>.
     */
    public String getDatasetCode() {
        return (String) get(3);
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
    public Row4<Long, Long, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, Long, String, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MeaningNr.MEANING_NR.ID;
    }

    @Override
    public Field<Long> field2() {
        return MeaningNr.MEANING_NR.MEANING_ID;
    }

    @Override
    public Field<String> field3() {
        return MeaningNr.MEANING_NR.MNR;
    }

    @Override
    public Field<String> field4() {
        return MeaningNr.MEANING_NR.DATASET_CODE;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getMeaningId();
    }

    @Override
    public String component3() {
        return getMnr();
    }

    @Override
    public String component4() {
        return getDatasetCode();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getMeaningId();
    }

    @Override
    public String value3() {
        return getMnr();
    }

    @Override
    public String value4() {
        return getDatasetCode();
    }

    @Override
    public MeaningNrRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public MeaningNrRecord value2(Long value) {
        setMeaningId(value);
        return this;
    }

    @Override
    public MeaningNrRecord value3(String value) {
        setMnr(value);
        return this;
    }

    @Override
    public MeaningNrRecord value4(String value) {
        setDatasetCode(value);
        return this;
    }

    @Override
    public MeaningNrRecord values(Long value1, Long value2, String value3, String value4) {
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
     * Create a detached MeaningNrRecord
     */
    public MeaningNrRecord() {
        super(MeaningNr.MEANING_NR);
    }

    /**
     * Create a detached, initialised MeaningNrRecord
     */
    public MeaningNrRecord(Long id, Long meaningId, String mnr, String datasetCode) {
        super(MeaningNr.MEANING_NR);

        setId(id);
        setMeaningId(meaningId);
        setMnr(mnr);
        setDatasetCode(datasetCode);
    }
}
