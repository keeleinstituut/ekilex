/*
 * This file is generated by jOOQ.
*/
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Indexes;
import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.GovernmentTypeLabelRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class GovernmentTypeLabel extends TableImpl<GovernmentTypeLabelRecord> {

    private static final long serialVersionUID = -1085038483;

    /**
     * The reference instance of <code>public.government_type_label</code>
     */
    public static final GovernmentTypeLabel GOVERNMENT_TYPE_LABEL = new GovernmentTypeLabel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GovernmentTypeLabelRecord> getRecordType() {
        return GovernmentTypeLabelRecord.class;
    }

    /**
     * The column <code>public.government_type_label.code</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.government_type_label.value</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.government_type_label.lang</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> LANG = createField("lang", org.jooq.impl.SQLDataType.CHAR(3).nullable(false), this, "");

    /**
     * The column <code>public.government_type_label.type</code>.
     */
    public final TableField<GovernmentTypeLabelRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>public.government_type_label</code> table reference
     */
    public GovernmentTypeLabel() {
        this(DSL.name("government_type_label"), null);
    }

    /**
     * Create an aliased <code>public.government_type_label</code> table reference
     */
    public GovernmentTypeLabel(String alias) {
        this(DSL.name(alias), GOVERNMENT_TYPE_LABEL);
    }

    /**
     * Create an aliased <code>public.government_type_label</code> table reference
     */
    public GovernmentTypeLabel(Name alias) {
        this(alias, GOVERNMENT_TYPE_LABEL);
    }

    private GovernmentTypeLabel(Name alias, Table<GovernmentTypeLabelRecord> aliased) {
        this(alias, aliased, null);
    }

    private GovernmentTypeLabel(Name alias, Table<GovernmentTypeLabelRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GOVERNMENT_TYPE_LABEL_CODE_LANG_TYPE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GovernmentTypeLabelRecord>> getKeys() {
        return Arrays.<UniqueKey<GovernmentTypeLabelRecord>>asList(Keys.GOVERNMENT_TYPE_LABEL_CODE_LANG_TYPE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GovernmentTypeLabelRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GovernmentTypeLabelRecord, ?>>asList(Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_CODE_FKEY, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_LANG_FKEY, Keys.GOVERNMENT_TYPE_LABEL__GOVERNMENT_TYPE_LABEL_TYPE_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GovernmentTypeLabel as(String alias) {
        return new GovernmentTypeLabel(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GovernmentTypeLabel as(Name alias) {
        return new GovernmentTypeLabel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentTypeLabel rename(String name) {
        return new GovernmentTypeLabel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GovernmentTypeLabel rename(Name name) {
        return new GovernmentTypeLabel(name, null);
    }
}
