/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.MeaningRelTypeLabelRecord;

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
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MeaningRelTypeLabel extends TableImpl<MeaningRelTypeLabelRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.meaning_rel_type_label</code>
     */
    public static final MeaningRelTypeLabel MEANING_REL_TYPE_LABEL = new MeaningRelTypeLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningRelTypeLabelRecord> getRecordType() {
        return MeaningRelTypeLabelRecord.class;
    }

    /**
     * The column <code>public.meaning_rel_type_label.code</code>.
     */
    public final TableField<MeaningRelTypeLabelRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_rel_type_label.value</code>.
     */
    public final TableField<MeaningRelTypeLabelRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.meaning_rel_type_label.lang</code>.
     */
    public final TableField<MeaningRelTypeLabelRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.meaning_rel_type_label.type</code>.
     */
    public final TableField<MeaningRelTypeLabelRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    private MeaningRelTypeLabel(Name alias, Table<MeaningRelTypeLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningRelTypeLabel(Name alias, Table<MeaningRelTypeLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.meaning_rel_type_label</code> table reference
     */
    public MeaningRelTypeLabel(String alias) {
        this(DSL.name(alias), MEANING_REL_TYPE_LABEL);
    }

    /**
     * Create an aliased <code>public.meaning_rel_type_label</code> table reference
     */
    public MeaningRelTypeLabel(Name alias) {
        this(alias, MEANING_REL_TYPE_LABEL);
    }

    /**
     * Create a <code>public.meaning_rel_type_label</code> table reference
     */
    public MeaningRelTypeLabel() {
        this(DSL.name("meaning_rel_type_label"), null);
    }

    public <O extends Record> MeaningRelTypeLabel(Table<O> child, ForeignKey<O, MeaningRelTypeLabelRecord> key) {
        super(child, key, MEANING_REL_TYPE_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<MeaningRelTypeLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningRelTypeLabelRecord>>asList(Keys.MEANING_REL_TYPE_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<MeaningRelTypeLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningRelTypeLabelRecord, ?>>asList(Keys.MEANING_REL_TYPE_LABEL__MEANING_REL_TYPE_LABEL_CODE_FKEY, Keys.MEANING_REL_TYPE_LABEL__MEANING_REL_TYPE_LABEL_LANG_FKEY, Keys.MEANING_REL_TYPE_LABEL__MEANING_REL_TYPE_LABEL_TYPE_FKEY);
    }

    private transient MeaningRelType _meaningRelType;
    private transient Language _language;
    private transient LabelType _labelType;

    public MeaningRelType meaningRelType() {
        if (_meaningRelType == null)
            _meaningRelType = new MeaningRelType(this, Keys.MEANING_REL_TYPE_LABEL__MEANING_REL_TYPE_LABEL_CODE_FKEY);

        return _meaningRelType;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.MEANING_REL_TYPE_LABEL__MEANING_REL_TYPE_LABEL_LANG_FKEY);

        return _language;
    }

    public LabelType labelType() {
        if (_labelType == null)
            _labelType = new LabelType(this, Keys.MEANING_REL_TYPE_LABEL__MEANING_REL_TYPE_LABEL_TYPE_FKEY);

        return _labelType;
    }

    @Override
    public MeaningRelTypeLabel as(String alias) {
        return new MeaningRelTypeLabel(DSL.name(alias), this);
    }

    @Override
    public MeaningRelTypeLabel as(Name alias) {
        return new MeaningRelTypeLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningRelTypeLabel rename(String name) {
        return new MeaningRelTypeLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningRelTypeLabel rename(Name name) {
        return new MeaningRelTypeLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}