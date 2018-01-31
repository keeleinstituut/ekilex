/*
 * This file is generated by jOOQ.
*/
package eki.eve.data.db.tables;


import eki.eve.data.db.Indexes;
import eki.eve.data.db.Keys;
import eki.eve.data.db.Public;
import eki.eve.data.db.tables.records.GovernmentTypeRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GovernmentType extends TableImpl<GovernmentTypeRecord> {

    private static final long serialVersionUID = 1437453794;

    /**
     * The reference instance of <code>public.government_type</code>
     */
    public static final GovernmentType GOVERNMENT_TYPE = new GovernmentType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GovernmentTypeRecord> getRecordType() {
        return GovernmentTypeRecord.class;
    }

    /**
     * The column <code>public.government_type.code</code>.
     */
    public final TableField<GovernmentTypeRecord, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>public.government_type.datasets</code>.
     */
    public final TableField<GovernmentTypeRecord, String[]> DATASETS = createField("datasets", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * Create a <code>public.government_type</code> table reference
     */
    public GovernmentType() {
        this(DSL.name("government_type"), null);
    }

    /**
     * Create an aliased <code>public.government_type</code> table reference
     */
    public GovernmentType(String alias) {
        this(DSL.name(alias), GOVERNMENT_TYPE);
    }

    /**
     * Create an aliased <code>public.government_type</code> table reference
     */
    public GovernmentType(Name alias) {
        this(alias, GOVERNMENT_TYPE);
    }

    private GovernmentType(Name alias, Table<GovernmentTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private GovernmentType(Name alias, Table<GovernmentTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.GOVERNMENT_TYPE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GovernmentTypeRecord> getPrimaryKey() {
        return Keys.GOVERNMENT_TYPE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GovernmentTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<GovernmentTypeRecord>>asList(Keys.GOVERNMENT_TYPE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GovernmentType as(String alias) {
        return new GovernmentType(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GovernmentType as(Name alias) {
        return new GovernmentType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentType rename(String name) {
        return new GovernmentType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentType rename(Name name) {
        return new GovernmentType(name, null);
    }
}
