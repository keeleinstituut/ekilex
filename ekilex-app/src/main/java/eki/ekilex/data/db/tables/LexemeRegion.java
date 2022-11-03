/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.LexemeRegionRecord;

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
public class LexemeRegion extends TableImpl<LexemeRegionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.lexeme_region</code>
     */
    public static final LexemeRegion LEXEME_REGION = new LexemeRegion();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexemeRegionRecord> getRecordType() {
        return LexemeRegionRecord.class;
    }

    /**
     * The column <code>public.lexeme_region.id</code>.
     */
    public final TableField<LexemeRegionRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.lexeme_region.lexeme_id</code>.
     */
    public final TableField<LexemeRegionRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lexeme_region.region_code</code>.
     */
    public final TableField<LexemeRegionRecord, String> REGION_CODE = createField(DSL.name("region_code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.lexeme_region.order_by</code>.
     */
    public final TableField<LexemeRegionRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private LexemeRegion(Name alias, Table<LexemeRegionRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexemeRegion(Name alias, Table<LexemeRegionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.lexeme_region</code> table reference
     */
    public LexemeRegion(String alias) {
        this(DSL.name(alias), LEXEME_REGION);
    }

    /**
     * Create an aliased <code>public.lexeme_region</code> table reference
     */
    public LexemeRegion(Name alias) {
        this(alias, LEXEME_REGION);
    }

    /**
     * Create a <code>public.lexeme_region</code> table reference
     */
    public LexemeRegion() {
        this(DSL.name("lexeme_region"), null);
    }

    public <O extends Record> LexemeRegion(Table<O> child, ForeignKey<O, LexemeRegionRecord> key) {
        super(child, key, LEXEME_REGION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<LexemeRegionRecord, Long> getIdentity() {
        return (Identity<LexemeRegionRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<LexemeRegionRecord> getPrimaryKey() {
        return Keys.LEXEME_REGION_PKEY;
    }

    @Override
    public List<UniqueKey<LexemeRegionRecord>> getKeys() {
        return Arrays.<UniqueKey<LexemeRegionRecord>>asList(Keys.LEXEME_REGION_PKEY, Keys.LEXEME_REGION_LEXEME_ID_REGION_CODE_KEY);
    }

    @Override
    public List<ForeignKey<LexemeRegionRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LexemeRegionRecord, ?>>asList(Keys.LEXEME_REGION__LEXEME_REGION_LEXEME_ID_FKEY, Keys.LEXEME_REGION__LEXEME_REGION_REGION_CODE_FKEY);
    }

    private transient Lexeme _lexeme;
    private transient Region _region;

    public Lexeme lexeme() {
        if (_lexeme == null)
            _lexeme = new Lexeme(this, Keys.LEXEME_REGION__LEXEME_REGION_LEXEME_ID_FKEY);

        return _lexeme;
    }

    public Region region() {
        if (_region == null)
            _region = new Region(this, Keys.LEXEME_REGION__LEXEME_REGION_REGION_CODE_FKEY);

        return _region;
    }

    @Override
    public LexemeRegion as(String alias) {
        return new LexemeRegion(DSL.name(alias), this);
    }

    @Override
    public LexemeRegion as(Name alias) {
        return new LexemeRegion(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeRegion rename(String name) {
        return new LexemeRegion(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexemeRegion rename(Name name) {
        return new LexemeRegion(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, Long> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
