/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Keys;
import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.LexicalDecisionDataRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class LexicalDecisionData extends TableImpl<LexicalDecisionDataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.lexical_decision_data</code>
     */
    public static final LexicalDecisionData LEXICAL_DECISION_DATA = new LexicalDecisionData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexicalDecisionDataRecord> getRecordType() {
        return LexicalDecisionDataRecord.class;
    }

    /**
     * The column <code>public.lexical_decision_data.id</code>.
     */
    public final TableField<LexicalDecisionDataRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.lexical_decision_data.word</code>.
     */
    public final TableField<LexicalDecisionDataRecord, String> WORD = createField(DSL.name("word"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_data.lang</code>.
     */
    public final TableField<LexicalDecisionDataRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_data.is_word</code>.
     */
    public final TableField<LexicalDecisionDataRecord, Boolean> IS_WORD = createField(DSL.name("is_word"), SQLDataType.BOOLEAN.nullable(false), this, "");

    private LexicalDecisionData(Name alias, Table<LexicalDecisionDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexicalDecisionData(Name alias, Table<LexicalDecisionDataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.lexical_decision_data</code> table reference
     */
    public LexicalDecisionData(String alias) {
        this(DSL.name(alias), LEXICAL_DECISION_DATA);
    }

    /**
     * Create an aliased <code>public.lexical_decision_data</code> table reference
     */
    public LexicalDecisionData(Name alias) {
        this(alias, LEXICAL_DECISION_DATA);
    }

    /**
     * Create a <code>public.lexical_decision_data</code> table reference
     */
    public LexicalDecisionData() {
        this(DSL.name("lexical_decision_data"), null);
    }

    public <O extends Record> LexicalDecisionData(Table<O> child, ForeignKey<O, LexicalDecisionDataRecord> key) {
        super(child, key, LEXICAL_DECISION_DATA);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<LexicalDecisionDataRecord, Long> getIdentity() {
        return (Identity<LexicalDecisionDataRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<LexicalDecisionDataRecord> getPrimaryKey() {
        return Keys.LEXICAL_DECISION_DATA_PKEY;
    }

    @Override
    public List<UniqueKey<LexicalDecisionDataRecord>> getKeys() {
        return Arrays.<UniqueKey<LexicalDecisionDataRecord>>asList(Keys.LEXICAL_DECISION_DATA_PKEY);
    }

    @Override
    public LexicalDecisionData as(String alias) {
        return new LexicalDecisionData(DSL.name(alias), this);
    }

    @Override
    public LexicalDecisionData as(Name alias) {
        return new LexicalDecisionData(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexicalDecisionData rename(String name) {
        return new LexicalDecisionData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexicalDecisionData rename(Name name) {
        return new LexicalDecisionData(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, String, Boolean> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
