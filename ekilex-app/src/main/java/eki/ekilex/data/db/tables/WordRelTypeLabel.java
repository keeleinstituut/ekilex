/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.WordRelTypeLabelRecord;

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
public class WordRelTypeLabel extends TableImpl<WordRelTypeLabelRecord> {

    private static final long serialVersionUID = -1603774331;

    /**
     * The reference instance of <code>public.word_rel_type_label</code>
     */
    public static final WordRelTypeLabel WORD_REL_TYPE_LABEL = new WordRelTypeLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WordRelTypeLabelRecord> getRecordType() {
        return WordRelTypeLabelRecord.class;
    }

    /**
     * The column <code>public.word_rel_type_label.code</code>.
     */
    public final TableField<WordRelTypeLabelRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.word_rel_type_label.value</code>.
     */
    public final TableField<WordRelTypeLabelRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.word_rel_type_label.lang</code>.
     */
    public final TableField<WordRelTypeLabelRecord, String> LANG = createField(DSL.name("lang"), org.jooq.impl.SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.word_rel_type_label.type</code>.
     */
    public final TableField<WordRelTypeLabelRecord, String> TYPE = createField(DSL.name("type"), org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>public.word_rel_type_label</code> table reference
     */
    public WordRelTypeLabel() {
        this(DSL.name("word_rel_type_label"), null);
    }

    /**
     * Create an aliased <code>public.word_rel_type_label</code> table reference
     */
    public WordRelTypeLabel(String alias) {
        this(DSL.name(alias), WORD_REL_TYPE_LABEL);
    }

    /**
     * Create an aliased <code>public.word_rel_type_label</code> table reference
     */
    public WordRelTypeLabel(Name alias) {
        this(alias, WORD_REL_TYPE_LABEL);
    }

    private WordRelTypeLabel(Name alias, Table<WordRelTypeLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private WordRelTypeLabel(Name alias, Table<WordRelTypeLabelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> WordRelTypeLabel(Table<O> child, ForeignKey<O, WordRelTypeLabelRecord> key) {
        super(child, key, WORD_REL_TYPE_LABEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<WordRelTypeLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<WordRelTypeLabelRecord>>asList(Keys.WORD_REL_TYPE_LABEL_CODE_LANG_TYPE_KEY);
    }

    @Override
    public List<ForeignKey<WordRelTypeLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WordRelTypeLabelRecord, ?>>asList(Keys.WORD_REL_TYPE_LABEL__WORD_REL_TYPE_LABEL_CODE_FKEY, Keys.WORD_REL_TYPE_LABEL__WORD_REL_TYPE_LABEL_LANG_FKEY, Keys.WORD_REL_TYPE_LABEL__WORD_REL_TYPE_LABEL_TYPE_FKEY);
    }

    public WordRelType wordRelType() {
        return new WordRelType(this, Keys.WORD_REL_TYPE_LABEL__WORD_REL_TYPE_LABEL_CODE_FKEY);
    }

    public Language language() {
        return new Language(this, Keys.WORD_REL_TYPE_LABEL__WORD_REL_TYPE_LABEL_LANG_FKEY);
    }

    public LabelType labelType() {
        return new LabelType(this, Keys.WORD_REL_TYPE_LABEL__WORD_REL_TYPE_LABEL_TYPE_FKEY);
    }

    @Override
    public WordRelTypeLabel as(String alias) {
        return new WordRelTypeLabel(DSL.name(alias), this);
    }

    @Override
    public WordRelTypeLabel as(Name alias) {
        return new WordRelTypeLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WordRelTypeLabel rename(String name) {
        return new WordRelTypeLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WordRelTypeLabel rename(Name name) {
        return new WordRelTypeLabel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
