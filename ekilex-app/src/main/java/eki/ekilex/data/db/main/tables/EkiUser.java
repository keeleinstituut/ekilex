/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.EkiUserRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row14;
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
public class EkiUser extends TableImpl<EkiUserRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.eki_user</code>
     */
    public static final EkiUser EKI_USER = new EkiUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EkiUserRecord> getRecordType() {
        return EkiUserRecord.class;
    }

    /**
     * The column <code>public.eki_user.id</code>.
     */
    public final TableField<EkiUserRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.eki_user.name</code>.
     */
    public final TableField<EkiUserRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.eki_user.email</code>.
     */
    public final TableField<EkiUserRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.eki_user.password</code>.
     */
    public final TableField<EkiUserRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.eki_user.activation_key</code>.
     */
    public final TableField<EkiUserRecord, String> ACTIVATION_KEY = createField(DSL.name("activation_key"), SQLDataType.VARCHAR(60), this, "");

    /**
     * The column <code>public.eki_user.recovery_key</code>.
     */
    public final TableField<EkiUserRecord, String> RECOVERY_KEY = createField(DSL.name("recovery_key"), SQLDataType.VARCHAR(60), this, "");

    /**
     * The column <code>public.eki_user.is_admin</code>.
     */
    public final TableField<EkiUserRecord, Boolean> IS_ADMIN = createField(DSL.name("is_admin"), SQLDataType.BOOLEAN.defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.eki_user.is_enabled</code>.
     */
    public final TableField<EkiUserRecord, Boolean> IS_ENABLED = createField(DSL.name("is_enabled"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.eki_user.review_comment</code>.
     */
    public final TableField<EkiUserRecord, String> REVIEW_COMMENT = createField(DSL.name("review_comment"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.eki_user.created</code>.
     */
    public final TableField<EkiUserRecord, Timestamp> CREATED = createField(DSL.name("created"), SQLDataType.TIMESTAMP(6).nullable(false).defaultValue(DSL.field("statement_timestamp()", SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>public.eki_user.is_master</code>.
     */
    public final TableField<EkiUserRecord, Boolean> IS_MASTER = createField(DSL.name("is_master"), SQLDataType.BOOLEAN.defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.eki_user.terms_ver</code>.
     */
    public final TableField<EkiUserRecord, String> TERMS_VER = createField(DSL.name("terms_ver"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.eki_user.api_key</code>.
     */
    public final TableField<EkiUserRecord, String> API_KEY = createField(DSL.name("api_key"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.eki_user.is_api_crud</code>.
     */
    public final TableField<EkiUserRecord, Boolean> IS_API_CRUD = createField(DSL.name("is_api_crud"), SQLDataType.BOOLEAN.defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    private EkiUser(Name alias, Table<EkiUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private EkiUser(Name alias, Table<EkiUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.eki_user</code> table reference
     */
    public EkiUser(String alias) {
        this(DSL.name(alias), EKI_USER);
    }

    /**
     * Create an aliased <code>public.eki_user</code> table reference
     */
    public EkiUser(Name alias) {
        this(alias, EKI_USER);
    }

    /**
     * Create a <code>public.eki_user</code> table reference
     */
    public EkiUser() {
        this(DSL.name("eki_user"), null);
    }

    public <O extends Record> EkiUser(Table<O> child, ForeignKey<O, EkiUserRecord> key) {
        super(child, key, EKI_USER);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<EkiUserRecord, Long> getIdentity() {
        return (Identity<EkiUserRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<EkiUserRecord> getPrimaryKey() {
        return Keys.EKI_USER_PKEY;
    }

    @Override
    public List<UniqueKey<EkiUserRecord>> getKeys() {
        return Arrays.<UniqueKey<EkiUserRecord>>asList(Keys.EKI_USER_PKEY, Keys.EKI_USER_EMAIL_KEY);
    }

    @Override
    public List<ForeignKey<EkiUserRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<EkiUserRecord, ?>>asList(Keys.EKI_USER__EKI_USER_TERMS_VER_FKEY);
    }

    private transient TermsOfUse _termsOfUse;

    public TermsOfUse termsOfUse() {
        if (_termsOfUse == null)
            _termsOfUse = new TermsOfUse(this, Keys.EKI_USER__EKI_USER_TERMS_VER_FKEY);

        return _termsOfUse;
    }

    @Override
    public EkiUser as(String alias) {
        return new EkiUser(DSL.name(alias), this);
    }

    @Override
    public EkiUser as(Name alias) {
        return new EkiUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EkiUser rename(String name) {
        return new EkiUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EkiUser rename(Name name) {
        return new EkiUser(name, null);
    }

    // -------------------------------------------------------------------------
    // Row14 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row14<Long, String, String, String, String, String, Boolean, Boolean, String, Timestamp, Boolean, String, String, Boolean> fieldsRow() {
        return (Row14) super.fieldsRow();
    }
}