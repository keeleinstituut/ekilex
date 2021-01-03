/*
 * This file is generated by jOOQ.
 */
package eki.stat.data.db.tables;


import eki.stat.data.db.Keys;
import eki.stat.data.db.Public;
import eki.stat.data.db.tables.records.WwSearchRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row15;
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
public class WwSearch extends TableImpl<WwSearchRecord> {

    private static final long serialVersionUID = 1028399612;

    /**
     * The reference instance of <code>public.ww_search</code>
     */
    public static final WwSearch WW_SEARCH = new WwSearch();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WwSearchRecord> getRecordType() {
        return WwSearchRecord.class;
    }

    /**
     * The column <code>public.ww_search.id</code>.
     */
    public final TableField<WwSearchRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('ww_search_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.ww_search.search_word</code>.
     */
    public final TableField<WwSearchRecord, String> SEARCH_WORD = createField(DSL.name("search_word"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.homonym_nr</code>.
     */
    public final TableField<WwSearchRecord, Integer> HOMONYM_NR = createField(DSL.name("homonym_nr"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.ww_search.search_mode</code>.
     */
    public final TableField<WwSearchRecord, String> SEARCH_MODE = createField(DSL.name("search_mode"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.destin_langs</code>.
     */
    public final TableField<WwSearchRecord, String[]> DESTIN_LANGS = createField(DSL.name("destin_langs"), org.jooq.impl.SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.ww_search.dataset_codes</code>.
     */
    public final TableField<WwSearchRecord, String[]> DATASET_CODES = createField(DSL.name("dataset_codes"), org.jooq.impl.SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.ww_search.search_uri</code>.
     */
    public final TableField<WwSearchRecord, String> SEARCH_URI = createField(DSL.name("search_uri"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.result_count</code>.
     */
    public final TableField<WwSearchRecord, Integer> RESULT_COUNT = createField(DSL.name("result_count"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.ww_search.results_exist</code>.
     */
    public final TableField<WwSearchRecord, Boolean> RESULTS_EXIST = createField(DSL.name("results_exist"), org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.ww_search.single_result</code>.
     */
    public final TableField<WwSearchRecord, Boolean> SINGLE_RESULT = createField(DSL.name("single_result"), org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.ww_search.user_agent</code>.
     */
    public final TableField<WwSearchRecord, String> USER_AGENT = createField(DSL.name("user_agent"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.referrer_domain</code>.
     */
    public final TableField<WwSearchRecord, String> REFERRER_DOMAIN = createField(DSL.name("referrer_domain"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.session_id</code>.
     */
    public final TableField<WwSearchRecord, String> SESSION_ID = createField(DSL.name("session_id"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.request_origin</code>.
     */
    public final TableField<WwSearchRecord, String> REQUEST_ORIGIN = createField(DSL.name("request_origin"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.ww_search.event_on</code>.
     */
    public final TableField<WwSearchRecord, Timestamp> EVENT_ON = createField(DSL.name("event_on"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("statement_timestamp()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>public.ww_search</code> table reference
     */
    public WwSearch() {
        this(DSL.name("ww_search"), null);
    }

    /**
     * Create an aliased <code>public.ww_search</code> table reference
     */
    public WwSearch(String alias) {
        this(DSL.name(alias), WW_SEARCH);
    }

    /**
     * Create an aliased <code>public.ww_search</code> table reference
     */
    public WwSearch(Name alias) {
        this(alias, WW_SEARCH);
    }

    private WwSearch(Name alias, Table<WwSearchRecord> aliased) {
        this(alias, aliased, null);
    }

    private WwSearch(Name alias, Table<WwSearchRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> WwSearch(Table<O> child, ForeignKey<O, WwSearchRecord> key) {
        super(child, key, WW_SEARCH);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<WwSearchRecord, Long> getIdentity() {
        return Keys.IDENTITY_WW_SEARCH;
    }

    @Override
    public UniqueKey<WwSearchRecord> getPrimaryKey() {
        return Keys.WW_SEARCH_PKEY;
    }

    @Override
    public List<UniqueKey<WwSearchRecord>> getKeys() {
        return Arrays.<UniqueKey<WwSearchRecord>>asList(Keys.WW_SEARCH_PKEY);
    }

    @Override
    public WwSearch as(String alias) {
        return new WwSearch(DSL.name(alias), this);
    }

    @Override
    public WwSearch as(Name alias) {
        return new WwSearch(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WwSearch rename(String name) {
        return new WwSearch(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WwSearch rename(Name name) {
        return new WwSearch(name, null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, String, Integer, String, String[], String[], String, Integer, Boolean, Boolean, String, String, String, String, Timestamp> fieldsRow() {
        return (Row15) super.fieldsRow();
    }
}