/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.MeaningImageRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row12;
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
public class MeaningImage extends TableImpl<MeaningImageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.meaning_image</code>
     */
    public static final MeaningImage MEANING_IMAGE = new MeaningImage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningImageRecord> getRecordType() {
        return MeaningImageRecord.class;
    }

    /**
     * The column <code>public.meaning_image.id</code>.
     */
    public final TableField<MeaningImageRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.meaning_image.original_freeform_id</code>.
     */
    public final TableField<MeaningImageRecord, Long> ORIGINAL_FREEFORM_ID = createField(DSL.name("original_freeform_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.meaning_image.meaning_id</code>.
     */
    public final TableField<MeaningImageRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_image.title</code>.
     */
    public final TableField<MeaningImageRecord, String> TITLE = createField(DSL.name("title"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_image.url</code>.
     */
    public final TableField<MeaningImageRecord, String> URL = createField(DSL.name("url"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.meaning_image.complexity</code>.
     */
    public final TableField<MeaningImageRecord, String> COMPLEXITY = createField(DSL.name("complexity"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_image.is_public</code>.
     */
    public final TableField<MeaningImageRecord, Boolean> IS_PUBLIC = createField(DSL.name("is_public"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("true", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.meaning_image.created_by</code>.
     */
    public final TableField<MeaningImageRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_image.created_on</code>.
     */
    public final TableField<MeaningImageRecord, LocalDateTime> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.meaning_image.modified_by</code>.
     */
    public final TableField<MeaningImageRecord, String> MODIFIED_BY = createField(DSL.name("modified_by"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_image.modified_on</code>.
     */
    public final TableField<MeaningImageRecord, LocalDateTime> MODIFIED_ON = createField(DSL.name("modified_on"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.meaning_image.order_by</code>.
     */
    public final TableField<MeaningImageRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private MeaningImage(Name alias, Table<MeaningImageRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningImage(Name alias, Table<MeaningImageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.meaning_image</code> table reference
     */
    public MeaningImage(String alias) {
        this(DSL.name(alias), MEANING_IMAGE);
    }

    /**
     * Create an aliased <code>public.meaning_image</code> table reference
     */
    public MeaningImage(Name alias) {
        this(alias, MEANING_IMAGE);
    }

    /**
     * Create a <code>public.meaning_image</code> table reference
     */
    public MeaningImage() {
        this(DSL.name("meaning_image"), null);
    }

    public <O extends Record> MeaningImage(Table<O> child, ForeignKey<O, MeaningImageRecord> key) {
        super(child, key, MEANING_IMAGE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MeaningImageRecord, Long> getIdentity() {
        return (Identity<MeaningImageRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<MeaningImageRecord> getPrimaryKey() {
        return Keys.MEANING_IMAGE_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningImageRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningImageRecord>>asList(Keys.MEANING_IMAGE_PKEY);
    }

    @Override
    public List<ForeignKey<MeaningImageRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningImageRecord, ?>>asList(Keys.MEANING_IMAGE__MEANING_IMAGE_MEANING_ID_FKEY);
    }

    private transient Meaning _meaning;

    public Meaning meaning() {
        if (_meaning == null)
            _meaning = new Meaning(this, Keys.MEANING_IMAGE__MEANING_IMAGE_MEANING_ID_FKEY);

        return _meaning;
    }

    @Override
    public MeaningImage as(String alias) {
        return new MeaningImage(DSL.name(alias), this);
    }

    @Override
    public MeaningImage as(Name alias) {
        return new MeaningImage(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningImage rename(String name) {
        return new MeaningImage(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningImage rename(Name name) {
        return new MeaningImage(name, null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, Long, String, String, String, Boolean, String, LocalDateTime, String, LocalDateTime, Long> fieldsRow() {
        return (Row12) super.fieldsRow();
    }
}
