/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Indexes;
import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
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
public class MeaningDomain extends TableImpl<MeaningDomainRecord> {

    private static final long serialVersionUID = 1987206102;

    /**
     * The reference instance of <code>public.meaning_domain</code>
     */
    public static final MeaningDomain MEANING_DOMAIN = new MeaningDomain();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningDomainRecord> getRecordType() {
        return MeaningDomainRecord.class;
    }

    /**
     * The column <code>public.meaning_domain.id</code>.
     */
    public final TableField<MeaningDomainRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('meaning_domain_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.meaning_domain.meaning_id</code>.
     */
    public final TableField<MeaningDomainRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.meaning_domain.domain_origin</code>.
     */
    public final TableField<MeaningDomainRecord, String> DOMAIN_ORIGIN = createField(DSL.name("domain_origin"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_domain.domain_code</code>.
     */
    public final TableField<MeaningDomainRecord, String> DOMAIN_CODE = createField(DSL.name("domain_code"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_domain.order_by</code>.
     */
    public final TableField<MeaningDomainRecord, Long> ORDER_BY = createField(DSL.name("order_by"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('meaning_domain_order_by_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>public.meaning_domain</code> table reference
     */
    public MeaningDomain() {
        this(DSL.name("meaning_domain"), null);
    }

    /**
     * Create an aliased <code>public.meaning_domain</code> table reference
     */
    public MeaningDomain(String alias) {
        this(DSL.name(alias), MEANING_DOMAIN);
    }

    /**
     * Create an aliased <code>public.meaning_domain</code> table reference
     */
    public MeaningDomain(Name alias) {
        this(alias, MEANING_DOMAIN);
    }

    private MeaningDomain(Name alias, Table<MeaningDomainRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningDomain(Name alias, Table<MeaningDomainRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> MeaningDomain(Table<O> child, ForeignKey<O, MeaningDomainRecord> key) {
        super(child, key, MEANING_DOMAIN);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MEANING_DOMAIN_CODE_ORIGIN_IDX, Indexes.MEANING_DOMAIN_LEXEME_ID_IDX);
    }

    @Override
    public Identity<MeaningDomainRecord, Long> getIdentity() {
        return Keys.IDENTITY_MEANING_DOMAIN;
    }

    @Override
    public UniqueKey<MeaningDomainRecord> getPrimaryKey() {
        return Keys.MEANING_DOMAIN_PKEY;
    }

    @Override
    public List<UniqueKey<MeaningDomainRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningDomainRecord>>asList(Keys.MEANING_DOMAIN_PKEY, Keys.MEANING_DOMAIN_MEANING_ID_DOMAIN_CODE_DOMAIN_ORIGIN_KEY);
    }

    @Override
    public List<ForeignKey<MeaningDomainRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningDomainRecord, ?>>asList(Keys.MEANING_DOMAIN__MEANING_DOMAIN_MEANING_ID_FKEY, Keys.MEANING_DOMAIN__MEANING_DOMAIN_DOMAIN_CODE_FKEY);
    }

    public Meaning meaning() {
        return new Meaning(this, Keys.MEANING_DOMAIN__MEANING_DOMAIN_MEANING_ID_FKEY);
    }

    public Domain domain() {
        return new Domain(this, Keys.MEANING_DOMAIN__MEANING_DOMAIN_DOMAIN_CODE_FKEY);
    }

    @Override
    public MeaningDomain as(String alias) {
        return new MeaningDomain(DSL.name(alias), this);
    }

    @Override
    public MeaningDomain as(Name alias) {
        return new MeaningDomain(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningDomain rename(String name) {
        return new MeaningDomain(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningDomain rename(Name name) {
        return new MeaningDomain(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, String, String, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
