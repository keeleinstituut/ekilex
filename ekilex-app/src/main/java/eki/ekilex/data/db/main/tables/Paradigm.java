/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.ParadigmRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Paradigm extends TableImpl<ParadigmRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.paradigm</code>
     */
    public static final Paradigm PARADIGM = new Paradigm();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ParadigmRecord> getRecordType() {
        return ParadigmRecord.class;
    }

    /**
     * The column <code>public.paradigm.id</code>.
     */
    public final TableField<ParadigmRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.paradigm.word_id</code>.
     */
    public final TableField<ParadigmRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.paradigm.comment</code>.
     */
    public final TableField<ParadigmRecord, String> COMMENT = createField(DSL.name("comment"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.paradigm.inflection_type_nr</code>.
     */
    public final TableField<ParadigmRecord, String> INFLECTION_TYPE_NR = createField(DSL.name("inflection_type_nr"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.paradigm.inflection_type</code>.
     */
    public final TableField<ParadigmRecord, String> INFLECTION_TYPE = createField(DSL.name("inflection_type"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.paradigm.is_secondary</code>.
     */
    public final TableField<ParadigmRecord, Boolean> IS_SECONDARY = createField(DSL.name("is_secondary"), SQLDataType.BOOLEAN.defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.paradigm.word_class</code>.
     */
    public final TableField<ParadigmRecord, String> WORD_CLASS = createField(DSL.name("word_class"), SQLDataType.VARCHAR(100), this, "");

    private Paradigm(Name alias, Table<ParadigmRecord> aliased) {
        this(alias, aliased, null);
    }

    private Paradigm(Name alias, Table<ParadigmRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.paradigm</code> table reference
     */
    public Paradigm(String alias) {
        this(DSL.name(alias), PARADIGM);
    }

    /**
     * Create an aliased <code>public.paradigm</code> table reference
     */
    public Paradigm(Name alias) {
        this(alias, PARADIGM);
    }

    /**
     * Create a <code>public.paradigm</code> table reference
     */
    public Paradigm() {
        this(DSL.name("paradigm"), null);
    }

    public <O extends Record> Paradigm(Table<O> child, ForeignKey<O, ParadigmRecord> key) {
        super(child, key, PARADIGM);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<ParadigmRecord, Long> getIdentity() {
        return (Identity<ParadigmRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ParadigmRecord> getPrimaryKey() {
        return Keys.PARADIGM_PKEY;
    }

    @Override
    public List<UniqueKey<ParadigmRecord>> getKeys() {
        return Arrays.<UniqueKey<ParadigmRecord>>asList(Keys.PARADIGM_PKEY);
    }

    @Override
    public List<ForeignKey<ParadigmRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ParadigmRecord, ?>>asList(Keys.PARADIGM__PARADIGM_WORD_ID_FKEY);
    }

    private transient Word _word;

    public Word word() {
        if (_word == null)
            _word = new Word(this, Keys.PARADIGM__PARADIGM_WORD_ID_FKEY);

        return _word;
    }

    @Override
    public Paradigm as(String alias) {
        return new Paradigm(DSL.name(alias), this);
    }

    @Override
    public Paradigm as(Name alias) {
        return new Paradigm(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Paradigm rename(String name) {
        return new Paradigm(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Paradigm rename(Name name) {
        return new Paradigm(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, Long, String, String, String, Boolean, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}