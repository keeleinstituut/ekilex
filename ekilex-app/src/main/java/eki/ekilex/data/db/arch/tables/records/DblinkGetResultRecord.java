/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.arch.tables.records;


import eki.ekilex.data.db.arch.tables.DblinkGetResult;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Row1;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DblinkGetResultRecord extends TableRecordImpl<DblinkGetResultRecord> implements Record1<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.dblink_get_result.dblink_get_result</code>.
     */
    public void setDblinkGetResult(Record value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.dblink_get_result.dblink_get_result</code>.
     */
    public Record getDblinkGetResult() {
        return (Record) get(0);
    }

    // -------------------------------------------------------------------------
    // Record1 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row1<Record> fieldsRow() {
        return (Row1) super.fieldsRow();
    }

    @Override
    public Row1<Record> valuesRow() {
        return (Row1) super.valuesRow();
    }

    @Override
    public Field<Record> field1() {
        return DblinkGetResult.DBLINK_GET_RESULT.DBLINK_GET_RESULT_;
    }

    @Override
    public Record component1() {
        return getDblinkGetResult();
    }

    @Override
    public Record value1() {
        return getDblinkGetResult();
    }

    @Override
    public DblinkGetResultRecord value1(Record value) {
        setDblinkGetResult(value);
        return this;
    }

    @Override
    public DblinkGetResultRecord values(Record value1) {
        value1(value1);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DblinkGetResultRecord
     */
    public DblinkGetResultRecord() {
        super(DblinkGetResult.DBLINK_GET_RESULT);
    }

    /**
     * Create a detached, initialised DblinkGetResultRecord
     */
    public DblinkGetResultRecord(Record dblinkGetResult) {
        super(DblinkGetResult.DBLINK_GET_RESULT);

        setDblinkGetResult(dblinkGetResult);
    }
}