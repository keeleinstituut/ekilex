/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.arch;


import eki.ekilex.data.db.arch.routines.DblinkBuildSqlDelete;
import eki.ekilex.data.db.arch.routines.DblinkBuildSqlInsert;
import eki.ekilex.data.db.arch.routines.DblinkBuildSqlUpdate;
import eki.ekilex.data.db.arch.routines.DblinkCancelQuery;
import eki.ekilex.data.db.arch.routines.DblinkClose1;
import eki.ekilex.data.db.arch.routines.DblinkClose2;
import eki.ekilex.data.db.arch.routines.DblinkClose3;
import eki.ekilex.data.db.arch.routines.DblinkClose4;
import eki.ekilex.data.db.arch.routines.DblinkConnect1;
import eki.ekilex.data.db.arch.routines.DblinkConnect2;
import eki.ekilex.data.db.arch.routines.DblinkConnectU1;
import eki.ekilex.data.db.arch.routines.DblinkConnectU2;
import eki.ekilex.data.db.arch.routines.DblinkCurrentQuery;
import eki.ekilex.data.db.arch.routines.DblinkDisconnect1;
import eki.ekilex.data.db.arch.routines.DblinkDisconnect2;
import eki.ekilex.data.db.arch.routines.DblinkErrorMessage;
import eki.ekilex.data.db.arch.routines.DblinkExec1;
import eki.ekilex.data.db.arch.routines.DblinkExec2;
import eki.ekilex.data.db.arch.routines.DblinkExec3;
import eki.ekilex.data.db.arch.routines.DblinkExec4;
import eki.ekilex.data.db.arch.routines.DblinkFdwValidator;
import eki.ekilex.data.db.arch.routines.DblinkGetConnections;
import eki.ekilex.data.db.arch.routines.DblinkIsBusy;
import eki.ekilex.data.db.arch.routines.DblinkOpen1;
import eki.ekilex.data.db.arch.routines.DblinkOpen2;
import eki.ekilex.data.db.arch.routines.DblinkOpen3;
import eki.ekilex.data.db.arch.routines.DblinkOpen4;
import eki.ekilex.data.db.arch.routines.DblinkSendQuery;
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

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;


/**
 * Convenience access to all stored procedures and functions in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Routines {

    /**
     * Call <code>public.dblink_build_sql_delete</code>
     */
    public static String dblinkBuildSqlDelete(
          Configuration configuration
        , String __1
        , Object[] __2
        , Integer __3
        , String[] __4
    ) {
        DblinkBuildSqlDelete f = new DblinkBuildSqlDelete();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_build_sql_delete</code> as a field.
     */
    public static Field<String> dblinkBuildSqlDelete(
          String __1
        , Object[] __2
        , Integer __3
        , String[] __4
    ) {
        DblinkBuildSqlDelete f = new DblinkBuildSqlDelete();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_build_sql_delete</code> as a field.
     */
    public static Field<String> dblinkBuildSqlDelete(
          Field<String> __1
        , Field<Object[]> __2
        , Field<Integer> __3
        , Field<String[]> __4
    ) {
        DblinkBuildSqlDelete f = new DblinkBuildSqlDelete();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_build_sql_insert</code>
     */
    public static String dblinkBuildSqlInsert(
          Configuration configuration
        , String __1
        , Object[] __2
        , Integer __3
        , String[] __4
        , String[] __5
    ) {
        DblinkBuildSqlInsert f = new DblinkBuildSqlInsert();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);
        f.set__5(__5);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_build_sql_insert</code> as a field.
     */
    public static Field<String> dblinkBuildSqlInsert(
          String __1
        , Object[] __2
        , Integer __3
        , String[] __4
        , String[] __5
    ) {
        DblinkBuildSqlInsert f = new DblinkBuildSqlInsert();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);
        f.set__5(__5);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_build_sql_insert</code> as a field.
     */
    public static Field<String> dblinkBuildSqlInsert(
          Field<String> __1
        , Field<Object[]> __2
        , Field<Integer> __3
        , Field<String[]> __4
        , Field<String[]> __5
    ) {
        DblinkBuildSqlInsert f = new DblinkBuildSqlInsert();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);
        f.set__5(__5);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_build_sql_update</code>
     */
    public static String dblinkBuildSqlUpdate(
          Configuration configuration
        , String __1
        , Object[] __2
        , Integer __3
        , String[] __4
        , String[] __5
    ) {
        DblinkBuildSqlUpdate f = new DblinkBuildSqlUpdate();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);
        f.set__5(__5);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_build_sql_update</code> as a field.
     */
    public static Field<String> dblinkBuildSqlUpdate(
          String __1
        , Object[] __2
        , Integer __3
        , String[] __4
        , String[] __5
    ) {
        DblinkBuildSqlUpdate f = new DblinkBuildSqlUpdate();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);
        f.set__5(__5);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_build_sql_update</code> as a field.
     */
    public static Field<String> dblinkBuildSqlUpdate(
          Field<String> __1
        , Field<Object[]> __2
        , Field<Integer> __3
        , Field<String[]> __4
        , Field<String[]> __5
    ) {
        DblinkBuildSqlUpdate f = new DblinkBuildSqlUpdate();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);
        f.set__5(__5);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_cancel_query</code>
     */
    public static String dblinkCancelQuery(
          Configuration configuration
        , String __1
    ) {
        DblinkCancelQuery f = new DblinkCancelQuery();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_cancel_query</code> as a field.
     */
    public static Field<String> dblinkCancelQuery(
          String __1
    ) {
        DblinkCancelQuery f = new DblinkCancelQuery();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_cancel_query</code> as a field.
     */
    public static Field<String> dblinkCancelQuery(
          Field<String> __1
    ) {
        DblinkCancelQuery f = new DblinkCancelQuery();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_close</code>
     */
    public static String dblinkClose1(
          Configuration configuration
        , String __1
    ) {
        DblinkClose1 f = new DblinkClose1();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose1(
          String __1
    ) {
        DblinkClose1 f = new DblinkClose1();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose1(
          Field<String> __1
    ) {
        DblinkClose1 f = new DblinkClose1();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_close</code>
     */
    public static String dblinkClose2(
          Configuration configuration
        , String __1
        , Boolean __2
    ) {
        DblinkClose2 f = new DblinkClose2();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose2(
          String __1
        , Boolean __2
    ) {
        DblinkClose2 f = new DblinkClose2();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose2(
          Field<String> __1
        , Field<Boolean> __2
    ) {
        DblinkClose2 f = new DblinkClose2();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_close</code>
     */
    public static String dblinkClose3(
          Configuration configuration
        , String __1
        , String __2
    ) {
        DblinkClose3 f = new DblinkClose3();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose3(
          String __1
        , String __2
    ) {
        DblinkClose3 f = new DblinkClose3();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose3(
          Field<String> __1
        , Field<String> __2
    ) {
        DblinkClose3 f = new DblinkClose3();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_close</code>
     */
    public static String dblinkClose4(
          Configuration configuration
        , String __1
        , String __2
        , Boolean __3
    ) {
        DblinkClose4 f = new DblinkClose4();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose4(
          String __1
        , String __2
        , Boolean __3
    ) {
        DblinkClose4 f = new DblinkClose4();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_close</code> as a field.
     */
    public static Field<String> dblinkClose4(
          Field<String> __1
        , Field<String> __2
        , Field<Boolean> __3
    ) {
        DblinkClose4 f = new DblinkClose4();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_connect</code>
     */
    public static String dblinkConnect1(
          Configuration configuration
        , String __1
    ) {
        DblinkConnect1 f = new DblinkConnect1();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_connect</code> as a field.
     */
    public static Field<String> dblinkConnect1(
          String __1
    ) {
        DblinkConnect1 f = new DblinkConnect1();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_connect</code> as a field.
     */
    public static Field<String> dblinkConnect1(
          Field<String> __1
    ) {
        DblinkConnect1 f = new DblinkConnect1();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_connect</code>
     */
    public static String dblinkConnect2(
          Configuration configuration
        , String __1
        , String __2
    ) {
        DblinkConnect2 f = new DblinkConnect2();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_connect</code> as a field.
     */
    public static Field<String> dblinkConnect2(
          String __1
        , String __2
    ) {
        DblinkConnect2 f = new DblinkConnect2();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_connect</code> as a field.
     */
    public static Field<String> dblinkConnect2(
          Field<String> __1
        , Field<String> __2
    ) {
        DblinkConnect2 f = new DblinkConnect2();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_connect_u</code>
     */
    public static String dblinkConnectU1(
          Configuration configuration
        , String __1
    ) {
        DblinkConnectU1 f = new DblinkConnectU1();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_connect_u</code> as a field.
     */
    public static Field<String> dblinkConnectU1(
          String __1
    ) {
        DblinkConnectU1 f = new DblinkConnectU1();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_connect_u</code> as a field.
     */
    public static Field<String> dblinkConnectU1(
          Field<String> __1
    ) {
        DblinkConnectU1 f = new DblinkConnectU1();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_connect_u</code>
     */
    public static String dblinkConnectU2(
          Configuration configuration
        , String __1
        , String __2
    ) {
        DblinkConnectU2 f = new DblinkConnectU2();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_connect_u</code> as a field.
     */
    public static Field<String> dblinkConnectU2(
          String __1
        , String __2
    ) {
        DblinkConnectU2 f = new DblinkConnectU2();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_connect_u</code> as a field.
     */
    public static Field<String> dblinkConnectU2(
          Field<String> __1
        , Field<String> __2
    ) {
        DblinkConnectU2 f = new DblinkConnectU2();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_current_query</code>
     */
    public static String dblinkCurrentQuery(
          Configuration configuration
    ) {
        DblinkCurrentQuery f = new DblinkCurrentQuery();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_current_query</code> as a field.
     */
    public static Field<String> dblinkCurrentQuery() {
        DblinkCurrentQuery f = new DblinkCurrentQuery();

        return f.asField();
    }

    /**
     * Call <code>public.dblink_disconnect</code>
     */
    public static String dblinkDisconnect1(
          Configuration configuration
    ) {
        DblinkDisconnect1 f = new DblinkDisconnect1();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_disconnect</code> as a field.
     */
    public static Field<String> dblinkDisconnect1() {
        DblinkDisconnect1 f = new DblinkDisconnect1();

        return f.asField();
    }

    /**
     * Call <code>public.dblink_disconnect</code>
     */
    public static String dblinkDisconnect2(
          Configuration configuration
        , String __1
    ) {
        DblinkDisconnect2 f = new DblinkDisconnect2();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_disconnect</code> as a field.
     */
    public static Field<String> dblinkDisconnect2(
          String __1
    ) {
        DblinkDisconnect2 f = new DblinkDisconnect2();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_disconnect</code> as a field.
     */
    public static Field<String> dblinkDisconnect2(
          Field<String> __1
    ) {
        DblinkDisconnect2 f = new DblinkDisconnect2();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_error_message</code>
     */
    public static String dblinkErrorMessage(
          Configuration configuration
        , String __1
    ) {
        DblinkErrorMessage f = new DblinkErrorMessage();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_error_message</code> as a field.
     */
    public static Field<String> dblinkErrorMessage(
          String __1
    ) {
        DblinkErrorMessage f = new DblinkErrorMessage();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_error_message</code> as a field.
     */
    public static Field<String> dblinkErrorMessage(
          Field<String> __1
    ) {
        DblinkErrorMessage f = new DblinkErrorMessage();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_exec</code>
     */
    public static String dblinkExec1(
          Configuration configuration
        , String __1
        , String __2
    ) {
        DblinkExec1 f = new DblinkExec1();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec1(
          String __1
        , String __2
    ) {
        DblinkExec1 f = new DblinkExec1();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec1(
          Field<String> __1
        , Field<String> __2
    ) {
        DblinkExec1 f = new DblinkExec1();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_exec</code>
     */
    public static String dblinkExec2(
          Configuration configuration
        , String __1
        , String __2
        , Boolean __3
    ) {
        DblinkExec2 f = new DblinkExec2();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec2(
          String __1
        , String __2
        , Boolean __3
    ) {
        DblinkExec2 f = new DblinkExec2();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec2(
          Field<String> __1
        , Field<String> __2
        , Field<Boolean> __3
    ) {
        DblinkExec2 f = new DblinkExec2();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_exec</code>
     */
    public static String dblinkExec3(
          Configuration configuration
        , String __1
    ) {
        DblinkExec3 f = new DblinkExec3();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec3(
          String __1
    ) {
        DblinkExec3 f = new DblinkExec3();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec3(
          Field<String> __1
    ) {
        DblinkExec3 f = new DblinkExec3();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_exec</code>
     */
    public static String dblinkExec4(
          Configuration configuration
        , String __1
        , Boolean __2
    ) {
        DblinkExec4 f = new DblinkExec4();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec4(
          String __1
        , Boolean __2
    ) {
        DblinkExec4 f = new DblinkExec4();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_exec</code> as a field.
     */
    public static Field<String> dblinkExec4(
          Field<String> __1
        , Field<Boolean> __2
    ) {
        DblinkExec4 f = new DblinkExec4();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_fdw_validator</code>
     */
    public static void dblinkFdwValidator(
          Configuration configuration
        , String[] options
        , Long catalog
    ) {
        DblinkFdwValidator p = new DblinkFdwValidator();
        p.setOptions(options);
        p.setCatalog_(catalog);

        p.execute(configuration);
    }

    /**
     * Call <code>public.dblink_get_connections</code>
     */
    public static String[] dblinkGetConnections(
          Configuration configuration
    ) {
        DblinkGetConnections f = new DblinkGetConnections();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_get_connections</code> as a field.
     */
    public static Field<String[]> dblinkGetConnections() {
        DblinkGetConnections f = new DblinkGetConnections();

        return f.asField();
    }

    /**
     * Call <code>public.dblink_is_busy</code>
     */
    public static Integer dblinkIsBusy(
          Configuration configuration
        , String __1
    ) {
        DblinkIsBusy f = new DblinkIsBusy();
        f.set__1(__1);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_is_busy</code> as a field.
     */
    public static Field<Integer> dblinkIsBusy(
          String __1
    ) {
        DblinkIsBusy f = new DblinkIsBusy();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_is_busy</code> as a field.
     */
    public static Field<Integer> dblinkIsBusy(
          Field<String> __1
    ) {
        DblinkIsBusy f = new DblinkIsBusy();
        f.set__1(__1);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_open</code>
     */
    public static String dblinkOpen1(
          Configuration configuration
        , String __1
        , String __2
    ) {
        DblinkOpen1 f = new DblinkOpen1();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen1(
          String __1
        , String __2
    ) {
        DblinkOpen1 f = new DblinkOpen1();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen1(
          Field<String> __1
        , Field<String> __2
    ) {
        DblinkOpen1 f = new DblinkOpen1();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_open</code>
     */
    public static String dblinkOpen2(
          Configuration configuration
        , String __1
        , String __2
        , Boolean __3
    ) {
        DblinkOpen2 f = new DblinkOpen2();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen2(
          String __1
        , String __2
        , Boolean __3
    ) {
        DblinkOpen2 f = new DblinkOpen2();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen2(
          Field<String> __1
        , Field<String> __2
        , Field<Boolean> __3
    ) {
        DblinkOpen2 f = new DblinkOpen2();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_open</code>
     */
    public static String dblinkOpen3(
          Configuration configuration
        , String __1
        , String __2
        , String __3
    ) {
        DblinkOpen3 f = new DblinkOpen3();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen3(
          String __1
        , String __2
        , String __3
    ) {
        DblinkOpen3 f = new DblinkOpen3();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen3(
          Field<String> __1
        , Field<String> __2
        , Field<String> __3
    ) {
        DblinkOpen3 f = new DblinkOpen3();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_open</code>
     */
    public static String dblinkOpen4(
          Configuration configuration
        , String __1
        , String __2
        , String __3
        , Boolean __4
    ) {
        DblinkOpen4 f = new DblinkOpen4();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen4(
          String __1
        , String __2
        , String __3
        , Boolean __4
    ) {
        DblinkOpen4 f = new DblinkOpen4();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_open</code> as a field.
     */
    public static Field<String> dblinkOpen4(
          Field<String> __1
        , Field<String> __2
        , Field<String> __3
        , Field<Boolean> __4
    ) {
        DblinkOpen4 f = new DblinkOpen4();
        f.set__1(__1);
        f.set__2(__2);
        f.set__3(__3);
        f.set__4(__4);

        return f.asField();
    }

    /**
     * Call <code>public.dblink_send_query</code>
     */
    public static Integer dblinkSendQuery(
          Configuration configuration
        , String __1
        , String __2
    ) {
        DblinkSendQuery f = new DblinkSendQuery();
        f.set__1(__1);
        f.set__2(__2);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.dblink_send_query</code> as a field.
     */
    public static Field<Integer> dblinkSendQuery(
          String __1
        , String __2
    ) {
        DblinkSendQuery f = new DblinkSendQuery();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Get <code>public.dblink_send_query</code> as a field.
     */
    public static Field<Integer> dblinkSendQuery(
          Field<String> __1
        , Field<String> __2
    ) {
        DblinkSendQuery f = new DblinkSendQuery();
        f.set__1(__1);
        f.set__2(__2);

        return f.asField();
    }

    /**
     * Call <code>public.dblink</code>.
     */
    public static Result<DblinkRecord> dblink(
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
    public static Dblink dblink(
          String __1
    ) {
        return eki.ekilex.data.db.arch.tables.Dblink.DBLINK.call(
              __1
        );
    }

    /**
     * Get <code>public.dblink</code> as a table.
     */
    public static Dblink dblink(
          Field<String> __1
    ) {
        return eki.ekilex.data.db.arch.tables.Dblink.DBLINK.call(
              __1
        );
    }

    /**
     * Call <code>public.dblink_fetch</code>.
     */
    public static Result<DblinkFetchRecord> dblinkFetch(
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
    public static DblinkFetch dblinkFetch(
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
    public static DblinkFetch dblinkFetch(
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
     * Call <code>public.dblink_get_notify</code>.
     */
    public static Result<DblinkGetNotifyRecord> dblinkGetNotify(
          Configuration configuration
    ) {
        return configuration.dsl().selectFrom(eki.ekilex.data.db.arch.tables.DblinkGetNotify.DBLINK_GET_NOTIFY.call(
        )).fetch();
    }

    /**
     * Get <code>public.dblink_get_notify</code> as a table.
     */
    public static DblinkGetNotify dblinkGetNotify() {
        return eki.ekilex.data.db.arch.tables.DblinkGetNotify.DBLINK_GET_NOTIFY.call(
        );
    }

    /**
     * Call <code>public.dblink_get_pkey</code>.
     */
    public static Result<DblinkGetPkeyRecord> dblinkGetPkey(
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
    public static DblinkGetPkey dblinkGetPkey(
          String __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetPkey.DBLINK_GET_PKEY.call(
              __1
        );
    }

    /**
     * Get <code>public.dblink_get_pkey</code> as a table.
     */
    public static DblinkGetPkey dblinkGetPkey(
          Field<String> __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetPkey.DBLINK_GET_PKEY.call(
              __1
        );
    }

    /**
     * Call <code>public.dblink_get_result</code>.
     */
    public static Result<DblinkGetResultRecord> dblinkGetResult(
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
    public static DblinkGetResult dblinkGetResult(
          String __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetResult.DBLINK_GET_RESULT.call(
              __1
        );
    }

    /**
     * Get <code>public.dblink_get_result</code> as a table.
     */
    public static DblinkGetResult dblinkGetResult(
          Field<String> __1
    ) {
        return eki.ekilex.data.db.arch.tables.DblinkGetResult.DBLINK_GET_RESULT.call(
              __1
        );
    }
}
