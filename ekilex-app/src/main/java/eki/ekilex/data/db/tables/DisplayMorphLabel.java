/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.DisplayMorphLabelRecord;

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
public class DisplayMorphLabel extends TableImpl<DisplayMorphLabelRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.display_morph_label</code>
     */
    public static final DisplayMorphLabel DISPLAY_MORPH_LABEL = new DisplayMorphLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DisplayMorphLabelRecord> getRecordType() {
        return DisplayMorphLabelRecord.class;
    }

    /**
     * The column <code>public.display_morph_label.code</code>.
     */
    public final TableField<DisplayMorphLabelRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.display_morph_label.value</code>.
     */
    public final TableField<DisplayMorphLabelRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.display_morph_label.lang</code>.
     */
    public final TableField<DisplayMorphLabelRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.display_morph_label.type</code>.
     */
    public final TableField<DisplayMorphLabelRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    private DisplayMorphLabel(Name alias, Table<DisplayMorphLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private DisplayMorphLabel(Name alias, Table<DisplayMorphLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.display_morph_label</code> table reference
     */
    public DisplayMorphLabel(String alias) {
        this(DSL.name(alias), DISPLAY_MORPH_LABEL);
    }

    /**
     * Create an aliased <code>public.display_morph_label</code> table reference
     */
    public DisplayMorphLabel(Name alias) {
        this(alias, DISPLAY_MORPH_LABEL);
    }

    /**
     * Create a <code>public.display_morph_label</code> table reference
     */
    public DisplayMorphLabel() {
        this(DSL.name("display_morph_label"), null);
    }

    public <O extends Record> DisplayMorphLabel(Table<O> child, ForeignKey<O, DisplayMorphLabelRecord> key) {
        super(child, key, DISPLAY_MORPH_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<DisplayMorphLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<DisplayMorphLabelRecord>>asList(Keys.DISPLAY_MORPH_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<DisplayMorphLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DisplayMorphLabelRecord, ?>>asList(Keys.DISPLAY_MORPH_LABEL__DISPLAY_MORPH_LABEL_CODE_FKEY, Keys.DISPLAY_MORPH_LABEL__DISPLAY_MORPH_LABEL_LANG_FKEY, Keys.DISPLAY_MORPH_LABEL__DISPLAY_MORPH_LABEL_TYPE_FKEY);
    }

    private transient DisplayMorph _displayMorph;
    private transient Language _language;
    private transient LabelType _labelType;

    public DisplayMorph displayMorph() {
        if (_displayMorph == null)
            _displayMorph = new DisplayMorph(this, Keys.DISPLAY_MORPH_LABEL__DISPLAY_MORPH_LABEL_CODE_FKEY);

        return _displayMorph;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.DISPLAY_MORPH_LABEL__DISPLAY_MORPH_LABEL_LANG_FKEY);

        return _language;
    }

    public LabelType labelType() {
        if (_labelType == null)
            _labelType = new LabelType(this, Keys.DISPLAY_MORPH_LABEL__DISPLAY_MORPH_LABEL_TYPE_FKEY);

        return _labelType;
    }

    @Override
    public DisplayMorphLabel as(String alias) {
        return new DisplayMorphLabel(DSL.name(alias), this);
    }

    @Override
    public DisplayMorphLabel as(Name alias) {
        return new DisplayMorphLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DisplayMorphLabel rename(String name) {
        return new DisplayMorphLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DisplayMorphLabel rename(Name name) {
        return new DisplayMorphLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
