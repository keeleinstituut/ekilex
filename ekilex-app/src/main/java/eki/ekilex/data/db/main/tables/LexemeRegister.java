/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.LexemeRegisterRecord;

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
public class LexemeRegister extends TableImpl<LexemeRegisterRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.lexeme_register</code>
     */
    public static final LexemeRegister LEXEME_REGISTER = new LexemeRegister();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexemeRegisterRecord> getRecordType() {
        return LexemeRegisterRecord.class;
    }

    /**
     * The column <code>public.lexeme_register.id</code>.
     */
    public final TableField<LexemeRegisterRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.lexeme_register.lexeme_id</code>.
     */
    public final TableField<LexemeRegisterRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lexeme_register.register_code</code>.
     */
    public final TableField<LexemeRegisterRecord, String> REGISTER_CODE = createField(DSL.name("register_code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.lexeme_register.order_by</code>.
     */
    public final TableField<LexemeRegisterRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private LexemeRegister(Name alias, Table<LexemeRegisterRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexemeRegister(Name alias, Table<LexemeRegisterRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.lexeme_register</code> table reference
     */
    public LexemeRegister(String alias) {
        this(DSL.name(alias), LEXEME_REGISTER);
    }

    /**
     * Create an aliased <code>public.lexeme_register</code> table reference
     */
    public LexemeRegister(Name alias) {
        this(alias, LEXEME_REGISTER);
    }

    /**
     * Create a <code>public.lexeme_register</code> table reference
     */
    public LexemeRegister() {
        this(DSL.name("lexeme_register"), null);
    }

    public <O extends Record> LexemeRegister(Table<O> child, ForeignKey<O, LexemeRegisterRecord> key) {
        super(child, key, LEXEME_REGISTER);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<LexemeRegisterRecord, Long> getIdentity() {
        return (Identity<LexemeRegisterRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<LexemeRegisterRecord> getPrimaryKey() {
        return Keys.LEXEME_REGISTER_PKEY;
    }

    @Override
    public List<UniqueKey<LexemeRegisterRecord>> getKeys() {
        return Arrays.<UniqueKey<LexemeRegisterRecord>>asList(Keys.LEXEME_REGISTER_PKEY, Keys.LEXEME_REGISTER_LEXEME_ID_REGISTER_CODE_KEY);
    }

    @Override
    public List<ForeignKey<LexemeRegisterRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LexemeRegisterRecord, ?>>asList(Keys.LEXEME_REGISTER__LEXEME_REGISTER_LEXEME_ID_FKEY, Keys.LEXEME_REGISTER__LEXEME_REGISTER_REGISTER_CODE_FKEY);
    }

    private transient Lexeme _lexeme;
    private transient Register _register;

    public Lexeme lexeme() {
        if (_lexeme == null)
            _lexeme = new Lexeme(this, Keys.LEXEME_REGISTER__LEXEME_REGISTER_LEXEME_ID_FKEY);

        return _lexeme;
    }

    public Register register() {
        if (_register == null)
            _register = new Register(this, Keys.LEXEME_REGISTER__LEXEME_REGISTER_REGISTER_CODE_FKEY);

        return _register;
    }

    @Override
    public LexemeRegister as(String alias) {
        return new LexemeRegister(DSL.name(alias), this);
    }

    @Override
    public LexemeRegister as(Name alias) {
        return new LexemeRegister(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeRegister rename(String name) {
        return new LexemeRegister(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeRegister rename(Name name) {
        return new LexemeRegister(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}