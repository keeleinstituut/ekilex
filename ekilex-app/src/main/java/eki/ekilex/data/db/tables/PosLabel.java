/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.PosLabelRecord;

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
public class PosLabel extends TableImpl<PosLabelRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.pos_label</code>
     */
    public static final PosLabel POS_LABEL = new PosLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PosLabelRecord> getRecordType() {
        return PosLabelRecord.class;
    }

    /**
     * The column <code>public.pos_label.code</code>.
     */
    public final TableField<PosLabelRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.pos_label.value</code>.
     */
    public final TableField<PosLabelRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.pos_label.lang</code>.
     */
    public final TableField<PosLabelRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.pos_label.type</code>.
     */
    public final TableField<PosLabelRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    private PosLabel(Name alias, Table<PosLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private PosLabel(Name alias, Table<PosLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.pos_label</code> table reference
     */
    public PosLabel(String alias) {
        this(DSL.name(alias), POS_LABEL);
    }

    /**
     * Create an aliased <code>public.pos_label</code> table reference
     */
    public PosLabel(Name alias) {
        this(alias, POS_LABEL);
    }

    /**
     * Create a <code>public.pos_label</code> table reference
     */
    public PosLabel() {
        this(DSL.name("pos_label"), null);
    }

    public <O extends Record> PosLabel(Table<O> child, ForeignKey<O, PosLabelRecord> key) {
        super(child, key, POS_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<PosLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<PosLabelRecord>>asList(Keys.POS_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<PosLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PosLabelRecord, ?>>asList(Keys.POS_LABEL__POS_LABEL_CODE_FKEY, Keys.POS_LABEL__POS_LABEL_LANG_FKEY, Keys.POS_LABEL__POS_LABEL_TYPE_FKEY);
    }

    private transient Pos _pos;
    private transient Language _language;
    private transient LabelType _labelType;

    public Pos pos() {
        if (_pos == null)
            _pos = new Pos(this, Keys.POS_LABEL__POS_LABEL_CODE_FKEY);

        return _pos;
    }

    public Language language() {
        if (_language == null)
            _language = new Language(this, Keys.POS_LABEL__POS_LABEL_LANG_FKEY);

        return _language;
    }

    public LabelType labelType() {
        if (_labelType == null)
            _labelType = new LabelType(this, Keys.POS_LABEL__POS_LABEL_TYPE_FKEY);

        return _labelType;
    }

    @Override
    public PosLabel as(String alias) {
        return new PosLabel(DSL.name(alias), this);
    }

    @Override
    public PosLabel as(Name alias) {
        return new PosLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PosLabel rename(String name) {
        return new PosLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PosLabel rename(Name name) {
        return new PosLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
