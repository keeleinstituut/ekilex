/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.DefinitionSourceLinkRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
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
public class DefinitionSourceLink extends TableImpl<DefinitionSourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.definition_source_link</code>
     */
    public static final DefinitionSourceLink DEFINITION_SOURCE_LINK = new DefinitionSourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefinitionSourceLinkRecord> getRecordType() {
        return DefinitionSourceLinkRecord.class;
    }

    /**
     * The column <code>public.definition_source_link.id</code>.
     */
    public final TableField<DefinitionSourceLinkRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.definition_source_link.definition_id</code>.
     */
    public final TableField<DefinitionSourceLinkRecord, Long> DEFINITION_ID = createField(DSL.name("definition_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.definition_source_link.source_id</code>.
     */
    public final TableField<DefinitionSourceLinkRecord, Long> SOURCE_ID = createField(DSL.name("source_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.definition_source_link.name</code>.
     */
    public final TableField<DefinitionSourceLinkRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.definition_source_link.order_by</code>.
     */
    public final TableField<DefinitionSourceLinkRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private DefinitionSourceLink(Name alias, Table<DefinitionSourceLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private DefinitionSourceLink(Name alias, Table<DefinitionSourceLinkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.definition_source_link</code> table reference
     */
    public DefinitionSourceLink(String alias) {
        this(DSL.name(alias), DEFINITION_SOURCE_LINK);
    }

    /**
     * Create an aliased <code>public.definition_source_link</code> table reference
     */
    public DefinitionSourceLink(Name alias) {
        this(alias, DEFINITION_SOURCE_LINK);
    }

    /**
     * Create a <code>public.definition_source_link</code> table reference
     */
    public DefinitionSourceLink() {
        this(DSL.name("definition_source_link"), null);
    }

    public <O extends Record> DefinitionSourceLink(Table<O> child, ForeignKey<O, DefinitionSourceLinkRecord> key) {
        super(child, key, DEFINITION_SOURCE_LINK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<DefinitionSourceLinkRecord, Long> getIdentity() {
        return (Identity<DefinitionSourceLinkRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<DefinitionSourceLinkRecord> getPrimaryKey() {
        return Keys.DEFINITION_SOURCE_LINK_PKEY;
    }

    @Override
    public List<UniqueKey<DefinitionSourceLinkRecord>> getKeys() {
        return Arrays.<UniqueKey<DefinitionSourceLinkRecord>>asList(Keys.DEFINITION_SOURCE_LINK_PKEY);
    }

    @Override
    public List<ForeignKey<DefinitionSourceLinkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DefinitionSourceLinkRecord, ?>>asList(Keys.DEFINITION_SOURCE_LINK__DEFINITION_SOURCE_LINK_DEFINITION_ID_FKEY, Keys.DEFINITION_SOURCE_LINK__DEFINITION_SOURCE_LINK_SOURCE_ID_FKEY);
    }

    private transient Definition _definition;
    private transient Source _source;

    public Definition definition() {
        if (_definition == null)
            _definition = new Definition(this, Keys.DEFINITION_SOURCE_LINK__DEFINITION_SOURCE_LINK_DEFINITION_ID_FKEY);

        return _definition;
    }

    public Source source() {
        if (_source == null)
            _source = new Source(this, Keys.DEFINITION_SOURCE_LINK__DEFINITION_SOURCE_LINK_SOURCE_ID_FKEY);

        return _source;
    }

    @Override
    public DefinitionSourceLink as(String alias) {
        return new DefinitionSourceLink(DSL.name(alias), this);
    }

    @Override
    public DefinitionSourceLink as(Name alias) {
        return new DefinitionSourceLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DefinitionSourceLink rename(String name) {
        return new DefinitionSourceLink(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefinitionSourceLink rename(Name name) {
        return new DefinitionSourceLink(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, Long, String, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
