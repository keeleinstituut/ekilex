/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Indexes;
import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.LexCollocRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
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
public class LexColloc extends TableImpl<LexCollocRecord> {

    private static final long serialVersionUID = -811368272;

    /**
     * The reference instance of <code>public.lex_colloc</code>
     */
    public static final LexColloc LEX_COLLOC = new LexColloc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexCollocRecord> getRecordType() {
        return LexCollocRecord.class;
    }

    /**
     * The column <code>public.lex_colloc.id</code>.
     */
    public final TableField<LexCollocRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('lex_colloc_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.lex_colloc.lexeme_id</code>.
     */
    public final TableField<LexCollocRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lex_colloc.rel_group_id</code>.
     */
    public final TableField<LexCollocRecord, Long> REL_GROUP_ID = createField(DSL.name("rel_group_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.lex_colloc.collocation_id</code>.
     */
    public final TableField<LexCollocRecord, Long> COLLOCATION_ID = createField(DSL.name("collocation_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.lex_colloc.member_form</code>.
     */
    public final TableField<LexCollocRecord, String> MEMBER_FORM = createField(DSL.name("member_form"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.lex_colloc.conjunct</code>.
     */
    public final TableField<LexCollocRecord, String> CONJUNCT = createField(DSL.name("conjunct"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.lex_colloc.weight</code>.
     */
    public final TableField<LexCollocRecord, BigDecimal> WEIGHT = createField(DSL.name("weight"), org.jooq.impl.SQLDataType.NUMERIC(14, 4), this, "");

    /**
     * The column <code>public.lex_colloc.member_order</code>.
     */
    public final TableField<LexCollocRecord, Integer> MEMBER_ORDER = createField(DSL.name("member_order"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.lex_colloc.group_order</code>.
     */
    public final TableField<LexCollocRecord, Integer> GROUP_ORDER = createField(DSL.name("group_order"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * Create a <code>public.lex_colloc</code> table reference
     */
    public LexColloc() {
        this(DSL.name("lex_colloc"), null);
    }

    /**
     * Create an aliased <code>public.lex_colloc</code> table reference
     */
    public LexColloc(String alias) {
        this(DSL.name(alias), LEX_COLLOC);
    }

    /**
     * Create an aliased <code>public.lex_colloc</code> table reference
     */
    public LexColloc(Name alias) {
        this(alias, LEX_COLLOC);
    }

    private LexColloc(Name alias, Table<LexCollocRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexColloc(Name alias, Table<LexCollocRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> LexColloc(Table<O> child, ForeignKey<O, LexCollocRecord> key) {
        super(child, key, LEX_COLLOC);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.LEX_COLLOC_COLLOCATION_ID_IDX, Indexes.LEX_COLLOC_LEXEME_ID_IDX, Indexes.LEX_COLLOC_REL_GROUP_ID_IDX);
    }

    @Override
    public Identity<LexCollocRecord, Long> getIdentity() {
        return Keys.IDENTITY_LEX_COLLOC;
    }

    @Override
    public UniqueKey<LexCollocRecord> getPrimaryKey() {
        return Keys.LEX_COLLOC_PKEY;
    }

    @Override
    public List<UniqueKey<LexCollocRecord>> getKeys() {
        return Arrays.<UniqueKey<LexCollocRecord>>asList(Keys.LEX_COLLOC_PKEY, Keys.LEX_COLLOC_LEXEME_ID_COLLOCATION_ID_KEY);
    }

    @Override
    public List<ForeignKey<LexCollocRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LexCollocRecord, ?>>asList(Keys.LEX_COLLOC__LEX_COLLOC_LEXEME_ID_FKEY, Keys.LEX_COLLOC__LEX_COLLOC_REL_GROUP_ID_FKEY, Keys.LEX_COLLOC__LEX_COLLOC_COLLOCATION_ID_FKEY);
    }

    public Lexeme lexeme() {
        return new Lexeme(this, Keys.LEX_COLLOC__LEX_COLLOC_LEXEME_ID_FKEY);
    }

    public LexCollocRelGroup lexCollocRelGroup() {
        return new LexCollocRelGroup(this, Keys.LEX_COLLOC__LEX_COLLOC_REL_GROUP_ID_FKEY);
    }

    public Collocation collocation() {
        return new Collocation(this, Keys.LEX_COLLOC__LEX_COLLOC_COLLOCATION_ID_FKEY);
    }

    @Override
    public LexColloc as(String alias) {
        return new LexColloc(DSL.name(alias), this);
    }

    @Override
    public LexColloc as(Name alias) {
        return new LexColloc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexColloc rename(String name) {
        return new LexColloc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexColloc rename(Name name) {
        return new LexColloc(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, Long, Long, String, String, BigDecimal, Integer, Integer> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
