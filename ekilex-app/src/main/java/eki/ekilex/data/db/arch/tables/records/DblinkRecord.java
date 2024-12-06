/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.arch.tables.records;


import eki.ekilex.data.db.arch.tables.Dblink;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Row1;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DblinkRecord extends TableRecordImpl<DblinkRecord> implements Record1<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.dblink.dblink</code>.
     */
    public void setDblink(Record value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.dblink.dblink</code>.
     */
    public Record getDblink() {
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
        return Dblink.DBLINK.DBLINK_;
    }

    @Override
    public Record component1() {
        return getDblink();
    }

    @Override
    public Record value1() {
        return getDblink();
    }

    @Override
    public DblinkRecord value1(Record value) {
        setDblink(value);
        return this;
    }

    @Override
    public DblinkRecord values(Record value1) {
        value1(value1);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DblinkRecord
     */
    public DblinkRecord() {
        super(Dblink.DBLINK);
    }

    /**
     * Create a detached, initialised DblinkRecord
     */
    public DblinkRecord(Record dblink) {
        super(Dblink.DBLINK);

        setDblink(dblink);
    }
}
