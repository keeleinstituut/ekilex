/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.FormRecord;

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
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Form extends TableImpl<FormRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.form</code>
     */
    public static final Form FORM = new Form();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FormRecord> getRecordType() {
        return FormRecord.class;
    }

    /**
     * The column <code>public.form.id</code>.
     */
    public final TableField<FormRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.form.paradigm_id</code>.
     */
    public final TableField<FormRecord, Long> PARADIGM_ID = createField(DSL.name("paradigm_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.form.morph_group1</code>.
     */
    public final TableField<FormRecord, String> MORPH_GROUP1 = createField(DSL.name("morph_group1"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.form.morph_group2</code>.
     */
    public final TableField<FormRecord, String> MORPH_GROUP2 = createField(DSL.name("morph_group2"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.form.morph_group3</code>.
     */
    public final TableField<FormRecord, String> MORPH_GROUP3 = createField(DSL.name("morph_group3"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.form.display_level</code>.
     */
    public final TableField<FormRecord, Integer> DISPLAY_LEVEL = createField(DSL.name("display_level"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.field("1", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.form.morph_code</code>.
     */
    public final TableField<FormRecord, String> MORPH_CODE = createField(DSL.name("morph_code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.form.morph_exists</code>.
     */
    public final TableField<FormRecord, Boolean> MORPH_EXISTS = createField(DSL.name("morph_exists"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.form.value</code>.
     */
    public final TableField<FormRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.form.value_prese</code>.
     */
    public final TableField<FormRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.form.components</code>.
     */
    public final TableField<FormRecord, String[]> COMPONENTS = createField(DSL.name("components"), SQLDataType.VARCHAR(100).getArrayDataType(), this, "");

    /**
     * The column <code>public.form.display_form</code>.
     */
    public final TableField<FormRecord, String> DISPLAY_FORM = createField(DSL.name("display_form"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.form.audio_file</code>.
     */
    public final TableField<FormRecord, String> AUDIO_FILE = createField(DSL.name("audio_file"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.form.order_by</code>.
     */
    public final TableField<FormRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.form.is_questionable</code>.
     */
    public final TableField<FormRecord, Boolean> IS_QUESTIONABLE = createField(DSL.name("is_questionable"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");

    private Form(Name alias, Table<FormRecord> aliased) {
        this(alias, aliased, null);
    }

    private Form(Name alias, Table<FormRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.form</code> table reference
     */
    public Form(String alias) {
        this(DSL.name(alias), FORM);
    }

    /**
     * Create an aliased <code>public.form</code> table reference
     */
    public Form(Name alias) {
        this(alias, FORM);
    }

    /**
     * Create a <code>public.form</code> table reference
     */
    public Form() {
        this(DSL.name("form"), null);
    }

    public <O extends Record> Form(Table<O> child, ForeignKey<O, FormRecord> key) {
        super(child, key, FORM);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<FormRecord, Long> getIdentity() {
        return (Identity<FormRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<FormRecord> getPrimaryKey() {
        return Keys.FORM_PKEY;
    }

    @Override
    public List<UniqueKey<FormRecord>> getKeys() {
        return Arrays.<UniqueKey<FormRecord>>asList(Keys.FORM_PKEY);
    }

    @Override
    public List<ForeignKey<FormRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<FormRecord, ?>>asList(Keys.FORM__FORM_PARADIGM_ID_FKEY, Keys.FORM__FORM_MORPH_CODE_FKEY);
    }

    private transient Paradigm _paradigm;
    private transient Morph _morph;

    public Paradigm paradigm() {
        if (_paradigm == null)
            _paradigm = new Paradigm(this, Keys.FORM__FORM_PARADIGM_ID_FKEY);

        return _paradigm;
    }

    public Morph morph() {
        if (_morph == null)
            _morph = new Morph(this, Keys.FORM__FORM_MORPH_CODE_FKEY);

        return _morph;
    }

    @Override
    public Form as(String alias) {
        return new Form(DSL.name(alias), this);
    }

    @Override
    public Form as(Name alias) {
        return new Form(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Form rename(String name) {
        return new Form(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Form rename(Name name) {
        return new Form(name, null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, Long, String, String, String, Integer, String, Boolean, String, String, String[], String, String, Long, Boolean> fieldsRow() {
        return (Row15) super.fieldsRow();
    }
}
