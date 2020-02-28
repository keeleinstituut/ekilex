/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.udt.records.TypeUsageRecord;

import javax.annotation.Generated;

import org.jooq.Schema;
import org.jooq.UDTField;
import org.jooq.impl.DSL;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.UDTImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeUsage extends UDTImpl<TypeUsageRecord> {

    private static final long serialVersionUID = 1024604666;

    /**
     * The reference instance of <code>public.type_usage</code>
     */
    public static final TypeUsage TYPE_USAGE = new TypeUsage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeUsageRecord> getRecordType() {
        return TypeUsageRecord.class;
    }

    /**
     * The attribute <code>public.type_usage.usage_id</code>.
     */
    public static final UDTField<TypeUsageRecord, Long> USAGE_ID = createField("usage_id", org.jooq.impl.SQLDataType.BIGINT, TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage</code>.
     */
    public static final UDTField<TypeUsageRecord, String> USAGE = createField("usage", org.jooq.impl.SQLDataType.CLOB, TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_prese</code>.
     */
    public static final UDTField<TypeUsageRecord, String> USAGE_PRESE = createField("usage_prese", org.jooq.impl.SQLDataType.CLOB, TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_lang</code>.
     */
    public static final UDTField<TypeUsageRecord, String> USAGE_LANG = createField("usage_lang", org.jooq.impl.SQLDataType.CHAR(3), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.complexity</code>.
     */
    public static final UDTField<TypeUsageRecord, String> COMPLEXITY = createField("complexity", org.jooq.impl.SQLDataType.VARCHAR(100), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_type_code</code>.
     */
    public static final UDTField<TypeUsageRecord, String> USAGE_TYPE_CODE = createField("usage_type_code", org.jooq.impl.SQLDataType.VARCHAR(100), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_translations</code>.
     */
    public static final UDTField<TypeUsageRecord, String[]> USAGE_TRANSLATIONS = createField("usage_translations", org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_definitions</code>.
     */
    public static final UDTField<TypeUsageRecord, String[]> USAGE_DEFINITIONS = createField("usage_definitions", org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.od_usage_definitions</code>.
     */
    public static final UDTField<TypeUsageRecord, String[]> OD_USAGE_DEFINITIONS = createField("od_usage_definitions", org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.od_usage_alternatives</code>.
     */
    public static final UDTField<TypeUsageRecord, String[]> OD_USAGE_ALTERNATIVES = createField("od_usage_alternatives", org.jooq.impl.SQLDataType.CLOB.getArrayDataType(), TYPE_USAGE, "");

    /**
     * No further instances allowed
     */
    private TypeUsage() {
        super("type_usage", null, null, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
