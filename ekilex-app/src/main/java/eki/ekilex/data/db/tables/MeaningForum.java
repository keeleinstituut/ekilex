/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.MeaningForumRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row10;
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
public class MeaningForum extends TableImpl<MeaningForumRecord> {

    private static final long serialVersionUID = -1130974494;

    /**
     * The reference instance of <code>public.meaning_forum</code>
     */
    public static final MeaningForum MEANING_FORUM = new MeaningForum();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningForumRecord> getRecordType() {
        return MeaningForumRecord.class;
    }

    /**
     * The column <code>public.meaning_forum.id</code>.
     */
    public final TableField<MeaningForumRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('meaning_forum_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.meaning_forum.meaning_id</code>.
     */
    public final TableField<MeaningForumRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_forum.value</code>.
     */
    public final TableField<MeaningForumRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.meaning_forum.value_prese</code>.
     */
    public final TableField<MeaningForumRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.meaning_forum.creator_id</code>.
     */
    public final TableField<MeaningForumRecord, Long> CREATOR_ID = createField(DSL.name("creator_id"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.meaning_forum.created_by</code>.
     */
    public final TableField<MeaningForumRecord, String> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_forum.created_on</code>.
     */
    public final TableField<MeaningForumRecord, Timestamp> CREATED_ON = createField(DSL.name("created_on"), org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>public.meaning_forum.modified_by</code>.
     */
    public final TableField<MeaningForumRecord, String> MODIFIED_BY = createField(DSL.name("modified_by"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.meaning_forum.modified_on</code>.
     */
    public final TableField<MeaningForumRecord, Timestamp> MODIFIED_ON = createField(DSL.name("modified_on"), org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>public.meaning_forum.order_by</code>.
     */
    public final TableField<MeaningForumRecord, Long> ORDER_BY = createField(DSL.name("order_by"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('meaning_forum_order_by_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>public.meaning_forum</code> table reference
     */
    public MeaningForum() {
        this(DSL.name("meaning_forum"), null);
    }

    /**
     * Create an aliased <code>public.meaning_forum</code> table reference
     */
    public MeaningForum(String alias) {
        this(DSL.name(alias), MEANING_FORUM);
    }

    /**
     * Create an aliased <code>public.meaning_forum</code> table reference
     */
    public MeaningForum(Name alias) {
        this(alias, MEANING_FORUM);
    }

    private MeaningForum(Name alias, Table<MeaningForumRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningForum(Name alias, Table<MeaningForumRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> MeaningForum(Table<O> child, ForeignKey<O, MeaningForumRecord> key) {
        super(child, key, MEANING_FORUM);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<MeaningForumRecord, Long> getIdentity() {
        return Keys.IDENTITY_MEANING_FORUM;
    }

    @Override
    public UniqueKey<MeaningForumRecord> getPrimaryKey() {
        return Keys.MEANING_FORUM_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningForumRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningForumRecord>>asList(Keys.MEANING_FORUM_PKEY);
    }

    @Override
    public List<ForeignKey<MeaningForumRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningForumRecord, ?>>asList(Keys.MEANING_FORUM__MEANING_FORUM_MEANING_ID_FKEY, Keys.MEANING_FORUM__MEANING_FORUM_CREATOR_ID_FKEY);
    }

    public Meaning meaning() {
        return new Meaning(this, Keys.MEANING_FORUM__MEANING_FORUM_MEANING_ID_FKEY);
    }

    public EkiUser ekiUser() {
        return new EkiUser(this, Keys.MEANING_FORUM__MEANING_FORUM_CREATOR_ID_FKEY);
    }

    @Override
    public MeaningForum as(String alias) {
        return new MeaningForum(DSL.name(alias), this);
    }

    @Override
    public MeaningForum as(Name alias) {
        return new MeaningForum(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningForum rename(String name) {
        return new MeaningForum(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningForum rename(Name name) {
        return new MeaningForum(name, null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Long, Long, String, String, Long, String, Timestamp, String, Timestamp, Long> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}