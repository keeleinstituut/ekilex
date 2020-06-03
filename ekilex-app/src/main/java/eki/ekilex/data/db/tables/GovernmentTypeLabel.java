/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.GovernmentTypeLabelRecord;

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
public class GovernmentTypeLabel extends TableImpl<GovernmentTypeLabelRecord> {

    private static final long serialVersionUID = 1678963509;

    /**
     * The reference instance of <code>public.government_type_label</code>
     */
    public static final GovernmentTypeLabel GOVERNMENT_TYPE_LABEL = new GovernmentTypeLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GovernmentTypeLabelRecord> getRecordType() {
        return GovernmentTypeLabelRecord.class;
    }

    /**
     * The column <code>public.government_type_label.code</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.government_type_label.value</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.government_type_label.lang</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> LANG = createField(DSL.name("lang"), org.jooq.impl.SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.government_type_label.type</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> TYPE = createField(DSL.name("type"), org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>public.government_type_label</code> table reference
     */
    public GovernmentTypeLabel() {
        this(DSL.name("government_type_label"), null);
    }

    /**
     * Create an aliased <code>public.government_type_label</code> table reference
     */
    public GovernmentTypeLabel(String alias) {
        this(DSL.name(alias), GOVERNMENT_TYPE_LABEL);
    }

    /**
     * Create an aliased <code>public.government_type_label</code> table reference
     */
    public GovernmentTypeLabel(Name alias) {
        this(alias, GOVERNMENT_TYPE_LABEL);
    }

    private GovernmentTypeLabel(Name alias, Table<GovernmentTypeLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private GovernmentTypeLabel(Name alias, Table<GovernmentTypeLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> GovernmentTypeLabel(Table<O> child, ForeignKey<O, GovernmentTypeLabelRecord> key) {
        super(child, key, GOVERNMENT_TYPE_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<GovernmentTypeLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<GovernmentTypeLabelRecord>>asList(Keys.GOVERNMENT_TYPE_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<GovernmentTypeLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GovernmentTypeLabelRecord, ?>>asList(Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_CODE_FKEY, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_LANG_FKEY, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_TYPE_FKEY);
    }

    public GovernmentType governmentType() {
        return new GovernmentType(this, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_CODE_FKEY);
    }

    public Language language() {
        return new Language(this, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_LANG_FKEY);
    }

    public LabelType labelType() {
        return new LabelType(this, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_TYPE_FKEY);
    }

    @Override
    public GovernmentTypeLabel as(String alias) {
        return new GovernmentTypeLabel(DSL.name(alias), this);
    }

    @Override
    public GovernmentTypeLabel as(Name alias) {
        return new GovernmentTypeLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentTypeLabel rename(String name) {
        return new GovernmentTypeLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentTypeLabel rename(Name name) {
        return new GovernmentTypeLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
