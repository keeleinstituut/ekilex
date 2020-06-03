/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.tables;


import eki.wordweb.data.db.Indexes;
import eki.wordweb.data.db.Keys;
import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.tables.records.LexicalDecisionResultRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexicalDecisionResult extends TableImpl<LexicalDecisionResultRecord> {

    private static final long serialVersionUID = 1340532990;

    /**
     * The reference instance of <code>public.lexical_decision_result</code>
     */
    public static final LexicalDecisionResult LEXICAL_DECISION_RESULT = new LexicalDecisionResult();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexicalDecisionResultRecord> getRecordType() {
        return LexicalDecisionResultRecord.class;
    }

    /**
     * The column <code>public.lexical_decision_result.id</code>.
     */
    public final TableField<LexicalDecisionResultRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('lexical_decision_result_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.lexical_decision_result.data_id</code>.
     */
    public final TableField<LexicalDecisionResultRecord, Long> DATA_ID = createField(DSL.name("data_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_result.remote_addr</code>.
     */
    public final TableField<LexicalDecisionResultRecord, String> REMOTE_ADDR = createField(DSL.name("remote_addr"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_result.session_id</code>.
     */
    public final TableField<LexicalDecisionResultRecord, String> SESSION_ID = createField(DSL.name("session_id"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_result.answer</code>.
     */
    public final TableField<LexicalDecisionResultRecord, Boolean> ANSWER = createField(DSL.name("answer"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_result.delay</code>.
     */
    public final TableField<LexicalDecisionResultRecord, Long> DELAY = createField(DSL.name("delay"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lexical_decision_result.created</code>.
     */
    public final TableField<LexicalDecisionResultRecord, LocalDateTime> CREATED = createField(DSL.name("created"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("statement_timestamp()", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * Create a <code>public.lexical_decision_result</code> table reference
     */
    public LexicalDecisionResult() {
        this(DSL.name("lexical_decision_result"), null);
    }

    /**
     * Create an aliased <code>public.lexical_decision_result</code> table reference
     */
    public LexicalDecisionResult(String alias) {
        this(DSL.name(alias), LEXICAL_DECISION_RESULT);
    }

    /**
     * Create an aliased <code>public.lexical_decision_result</code> table reference
     */
    public LexicalDecisionResult(Name alias) {
        this(alias, LEXICAL_DECISION_RESULT);
    }

    private LexicalDecisionResult(Name alias, Table<LexicalDecisionResultRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexicalDecisionResult(Name alias, Table<LexicalDecisionResultRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> LexicalDecisionResult(Table<O> child, ForeignKey<O, LexicalDecisionResultRecord> key) {
        super(child, key, LEXICAL_DECISION_RESULT);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.LEXICAL_DECISION_RESULT_DATA_ID_IDX);
    }

    @Override
    public Identity<LexicalDecisionResultRecord, Long> getIdentity() {
        return Keys.IDENTITY_LEXICAL_DECISION_RESULT;
    }

    @Override
    public UniqueKey<LexicalDecisionResultRecord> getPrimaryKey() {
        return Keys.LEXICAL_DECISION_RESULT_PKEY;
    }

    @Override
    public List<UniqueKey<LexicalDecisionResultRecord>> getKeys() {
        return Arrays.<UniqueKey<LexicalDecisionResultRecord>>asList(Keys.LEXICAL_DECISION_RESULT_PKEY);
    }

    @Override
    public List<ForeignKey<LexicalDecisionResultRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LexicalDecisionResultRecord, ?>>asList(Keys.LEXICAL_DECISION_RESULT__LEXICAL_DECISION_RESULT_DATA_ID_FKEY);
    }

    public LexicalDecisionData lexicalDecisionData() {
        return new LexicalDecisionData(this, Keys.LEXICAL_DECISION_RESULT__LEXICAL_DECISION_RESULT_DATA_ID_FKEY);
    }

    @Override
    public LexicalDecisionResult as(String alias) {
        return new LexicalDecisionResult(DSL.name(alias), this);
    }

    @Override
    public LexicalDecisionResult as(Name alias) {
        return new LexicalDecisionResult(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexicalDecisionResult rename(String name) {
        return new LexicalDecisionResult(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexicalDecisionResult rename(Name name) {
        return new LexicalDecisionResult(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, Long, String, String, Boolean, Long, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
