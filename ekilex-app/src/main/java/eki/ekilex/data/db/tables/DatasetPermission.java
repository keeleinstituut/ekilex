/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.DatasetPermissionRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class DatasetPermission extends TableImpl<DatasetPermissionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.dataset_permission</code>
     */
    public static final DatasetPermission DATASET_PERMISSION = new DatasetPermission();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DatasetPermissionRecord> getRecordType() {
        return DatasetPermissionRecord.class;
    }

    /**
     * The column <code>public.dataset_permission.id</code>.
     */
    public final TableField<DatasetPermissionRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.dataset_permission.dataset_code</code>.
     */
    public final TableField<DatasetPermissionRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>public.dataset_permission.user_id</code>.
     */
    public final TableField<DatasetPermissionRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.dataset_permission.auth_operation</code>.
     */
    public final TableField<DatasetPermissionRecord, String> AUTH_OPERATION = createField(DSL.name("auth_operation"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.dataset_permission.auth_item</code>.
     */
    public final TableField<DatasetPermissionRecord, String> AUTH_ITEM = createField(DSL.name("auth_item"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.dataset_permission.auth_lang</code>.
     */
    public final TableField<DatasetPermissionRecord, String> AUTH_LANG = createField(DSL.name("auth_lang"), SQLDataType.CHAR(3), this, "");

    private DatasetPermission(Name alias, Table<DatasetPermissionRecord> aliased) {
        this(alias, aliased, null);
    }

    private DatasetPermission(Name alias, Table<DatasetPermissionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.dataset_permission</code> table reference
     */
    public DatasetPermission(String alias) {
        this(DSL.name(alias), DATASET_PERMISSION);
    }

    /**
     * Create an aliased <code>public.dataset_permission</code> table reference
     */
    public DatasetPermission(Name alias) {
        this(alias, DATASET_PERMISSION);
    }

    /**
     * Create a <code>public.dataset_permission</code> table reference
     */
    public DatasetPermission() {
        this(DSL.name("dataset_permission"), null);
    }

    public <O extends Record> DatasetPermission(Table<O> child, ForeignKey<O, DatasetPermissionRecord> key) {
        super(child, key, DATASET_PERMISSION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<DatasetPermissionRecord, Long> getIdentity() {
        return (Identity<DatasetPermissionRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<DatasetPermissionRecord> getPrimaryKey() {
        return Keys.DATASET_PERMISSION_PKEY;
    }

    @Override
    public List<UniqueKey<DatasetPermissionRecord>> getKeys() {
        return Arrays.<UniqueKey<DatasetPermissionRecord>>asList(Keys.DATASET_PERMISSION_PKEY, Keys.DATASET_PERMISSION_DATASET_CODE_USER_ID_AUTH_OPERATION_AUTH_KEY);
    }

    @Override
    public List<ForeignKey<DatasetPermissionRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DatasetPermissionRecord, ?>>asList(Keys.DATASET_PERMISSION__DATASET_PERMISSION_DATASET_CODE_FKEY, Keys.DATASET_PERMISSION__DATASET_PERMISSION_USER_ID_FKEY, Keys.DATASET_PERMISSION__DATASET_PERMISSION_AUTH_LANG_FKEY);
    }

    private transient Dataset _dataset;
    private transient EkiUser _ekiUser;
    private transient Language _language;

    public Dataset dataset() {
        if (_dataset == null)
            _dataset = new Dataset(this, Keys.DATASET_PERMISSION__DATASET_PERMISSION_DATASET_CODE_FKEY);

        return _dataset;
    }

    public EkiUser ekiUser() {
        if (_ekiUser == null)
            _ekiUser = new EkiUser(this, Keys.DATASET_PERMISSION__DATASET_PERMISSION_USER_ID_FKEY);

        return _ekiUser;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.DATASET_PERMISSION__DATASET_PERMISSION_AUTH_LANG_FKEY);

        return _language;
    }

    @Override
    public DatasetPermission as(String alias) {
        return new DatasetPermission(DSL.name(alias), this);
    }

    @Override
    public DatasetPermission as(Name alias) {
        return new DatasetPermission(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DatasetPermission rename(String name) {
        return new DatasetPermission(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DatasetPermission rename(Name name) {
        return new DatasetPermission(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, Long, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
