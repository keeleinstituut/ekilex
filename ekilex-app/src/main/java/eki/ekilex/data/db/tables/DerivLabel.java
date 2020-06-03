/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.DerivLabelRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class DerivLabel extends TableImpl<DerivLabelRecord> {

    private static final long serialVersionUID = -1769585462;

    /**
     * The reference instance of <code>public.deriv_label</code>
     */
    public static final DerivLabel DERIV_LABEL = new DerivLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DerivLabelRecord> getRecordType() {
        return DerivLabelRecord.class;
    }

    /**
     * The column <code>public.deriv_label.code</code>.
     */
    public final TableField<DerivLabelRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.deriv_label.value</code>.
     */
    public final TableField<DerivLabelRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.deriv_label.lang</code>.
     */
    public final TableField<DerivLabelRecord, String> LANG = createField(DSL.name("lang"), org.jooq.impl.SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.deriv_label.type</code>.
     */
    public final TableField<DerivLabelRecord, String> TYPE = createField(DSL.name("type"), org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>public.deriv_label</code> table reference
     */
    public DerivLabel() {
        this(DSL.name("deriv_label"), null);
    }

    /**
     * Create an aliased <code>public.deriv_label</code> table reference
     */
    public DerivLabel(String alias) {
        this(DSL.name(alias), DERIV_LABEL);
    }

    /**
     * Create an aliased <code>public.deriv_label</code> table reference
     */
    public DerivLabel(Name alias) {
        this(alias, DERIV_LABEL);
    }

    private DerivLabel(Name alias, Table<DerivLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private DerivLabel(Name alias, Table<DerivLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> DerivLabel(Table<O> child, ForeignKey<O, DerivLabelRecord> key) {
        super(child, key, DERIV_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<DerivLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<DerivLabelRecord>>asList(Keys.DERIV_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<DerivLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DerivLabelRecord, ?>>asList(Keys.DERIV_LABEL__DERIV_LABEL_CODE_FKEY, Keys.DERIV_LABEL__DERIV_LABEL_LANG_FKEY, Keys.DERIV_LABEL__DERIV_LABEL_TYPE_FKEY);
    }

    public Deriv deriv() {
        return new Deriv(this, Keys.DERIV_LABEL__DERIV_LABEL_CODE_FKEY);
    }

    public Language language() {
        return new Language(this, Keys.DERIV_LABEL__DERIV_LABEL_LANG_FKEY);
    }

    public LabelType labelType() {
        return new LabelType(this, Keys.DERIV_LABEL__DERIV_LABEL_TYPE_FKEY);
    }

    @Override
    public DerivLabel as(String alias) {
        return new DerivLabel(DSL.name(alias), this);
    }

    @Override
    public DerivLabel as(Name alias) {
        return new DerivLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DerivLabel rename(String name) {
        return new DerivLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DerivLabel rename(Name name) {
        return new DerivLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
