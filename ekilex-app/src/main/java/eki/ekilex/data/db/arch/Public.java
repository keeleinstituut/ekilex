/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.arch;


import eki.ekilex.data.db.arch.tables.ActivityLogBulk;
import eki.ekilex.data.db.arch.tables.Dblink;
import eki.ekilex.data.db.arch.tables.DblinkFetch;
import eki.ekilex.data.db.arch.tables.DblinkGetNotify;
import eki.ekilex.data.db.arch.tables.DblinkGetPkey;
import eki.ekilex.data.db.arch.tables.DblinkGetResult;
import eki.ekilex.data.db.arch.tables.records.DblinkFetchRecord;
import eki.ekilex.data.db.arch.tables.records.DblinkGetNotifyRecord;
import eki.ekilex.data.db.arch.tables.records.DblinkGetPkeyRecord;
import eki.ekilex.data.db.arch.tables.records.DblinkGetResultRecord;
import eki.ekilex.data.db.arch.tables.records.DblinkRecord;
import eki.ekilex.data.db.arch.udt.DblinkPkeyResults;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.UDT;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.activity_log_bulk</code>.
     */
    public final ActivityLogBulk ACTIVITY_LOG_BULK = ActivityLogBulk.ACTIVITY_LOG_BULK;

    /**
     * The table <code>public.dblink</code>.
     */
    public final Dblink DBLINK = Dblink.DBLINK;

    /**
     * Call <code>public.dblink</code>.
     */
    public static Result<DblinkRecord> DBLINK(
          Configuration configuration
        , String __1
    ) {
        return configuration.dsl().selectFrom(eki.ekilex.data.db.arch.tables.Dblink.DBLINK.call(
              __1
        )).fetch();
    }

    /**
     * Get <code>public.dblink</code> as a table.
     */
    public static Dblink DBLINK(
          String __1
    ) {
        return eki.ekilex.data.db.arch.tables.Dblink.DBLINK.call(
              __1
        );
    }

    /**
     * Get <code>public.dblink</code> as a table.
     */
    public static Dblink DBLINK(
          Field<String> __1
    ) {
        return eki.ekilex.data.db.arch.tables.Dblink.DBLINK.call(
              __1
        );
    }

    /**
     * The table <code>public.dblink_fetch</code>.
     */
    public final DblinkFetch DBLINK_FETCH = DblinkFetch.DBLINK_FETCH;

    /**
     * Call <code>public.dblink_fetch</code>.
     */
    public static Result<DblinkFetchRecord> DBLINK_FETCH(
          Configuration configuration
        , String __1
        , Integer __2
        , Boolean __3
    ) {
        return configuration.dsl().selectFrom(eki.ekilex.data.db.arch.tables.DblinkFetch.DBLINK_FETCH.call(
              __1
            , __2
            , __3
        )).fetch();
    }

    /**
     * Get <code>public.dblink_fetch</code> as a table.
     */
    public static DblinkFetch DBLINK_FETCH(
          String __1
        , Integer __2
        , Boolean __3
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkFetch.DBLINK_FETCH.call(
              __1
            , __2
            , __3
        );
    }

    /**
     * Get <code>public.dblink_fetch</code> as a table.
     */
    public static DblinkFetch DBLINK_FETCH(
          Field<String> __1
        , Field<Integer> __2
        , Field<Boolean> __3
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkFetch.DBLINK_FETCH.call(
              __1
            , __2
            , __3
        );
    }

    /**
     * The table <code>public.dblink_get_notify</code>.
     */
    public final DblinkGetNotify DBLINK_GET_NOTIFY = DblinkGetNotify.DBLINK_GET_NOTIFY;

    /**
     * Call <code>public.dblink_get_notify</code>.
     */
    public static Result<DblinkGetNotifyRecord> DBLINK_GET_NOTIFY(
          Configuration configuration
    ) {
        return configuration.dsl().selectFrom(eki.ekilex.data.db.arch.tables.DblinkGetNotify.DBLINK_GET_NOTIFY.call(
        )).fetch();
    }

    /**
     * Get <code>public.dblink_get_notify</code> as a table.
     */
    public static DblinkGetNotify DBLINK_GET_NOTIFY() {
        return eki.ekilex.data.db.arch.tables.DblinkGetNotify.DBLINK_GET_NOTIFY.call(
        );
    }

    /**
     * The table <code>public.dblink_get_pkey</code>.
     */
    public final DblinkGetPkey DBLINK_GET_PKEY = DblinkGetPkey.DBLINK_GET_PKEY;

    /**
     * Call <code>public.dblink_get_pkey</code>.
     */
    public static Result<DblinkGetPkeyRecord> DBLINK_GET_PKEY(
          Configuration configuration
        , String __1
    ) {
        return configuration.dsl().selectFrom(eki.ekilex.data.db.arch.tables.DblinkGetPkey.DBLINK_GET_PKEY.call(
              __1
        )).fetch();
    }

    /**
     * Get <code>public.dblink_get_pkey</code> as a table.
     */
    public static DblinkGetPkey DBLINK_GET_PKEY(
          String __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetPkey.DBLINK_GET_PKEY.call(
              __1
        );
    }

    /**
     * Get <code>public.dblink_get_pkey</code> as a table.
     */
    public static DblinkGetPkey DBLINK_GET_PKEY(
          Field<String> __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetPkey.DBLINK_GET_PKEY.call(
              __1
        );
    }

    /**
     * The table <code>public.dblink_get_result</code>.
     */
    public final DblinkGetResult DBLINK_GET_RESULT = DblinkGetResult.DBLINK_GET_RESULT;

    /**
     * Call <code>public.dblink_get_result</code>.
     */
    public static Result<DblinkGetResultRecord> DBLINK_GET_RESULT(
          Configuration configuration
        , String __1
    ) {
        return configuration.dsl().selectFrom(eki.ekilex.data.db.arch.tables.DblinkGetResult.DBLINK_GET_RESULT.call(
              __1
        )).fetch();
    }

    /**
     * Get <code>public.dblink_get_result</code> as a table.
     */
    public static DblinkGetResult DBLINK_GET_RESULT(
          String __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetResult.DBLINK_GET_RESULT.call(
              __1
        );
    }

    /**
     * Get <code>public.dblink_get_result</code> as a table.
     */
    public static DblinkGetResult DBLINK_GET_RESULT(
          Field<String> __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetResult.DBLINK_GET_RESULT.call(
              __1
        );
    }

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.<Sequence<?>>asList(
            Sequences.ACTIVITY_LOG_BULK_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            ActivityLogBulk.ACTIVITY_LOG_BULK,
            Dblink.DBLINK,
            DblinkFetch.DBLINK_FETCH,
            DblinkGetNotify.DBLINK_GET_NOTIFY,
            DblinkGetPkey.DBLINK_GET_PKEY,
            DblinkGetResult.DBLINK_GET_RESULT);
    }

    @Override
    public final List<UDT<?>> getUDTs() {
        return Arrays.<UDT<?>>asList(
            DblinkPkeyResults.DBLINK_PKEY_RESULTS);
    }
}