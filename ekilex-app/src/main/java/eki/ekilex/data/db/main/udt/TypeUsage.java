/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.udt.records.TypeUsageRecord;

import org.jooq.JSON;
import org.jooq.Schema;
import org.jooq.UDTField;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.UDTImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeUsage extends UDTImpl<TypeUsageRecord> {

    private static final long serialVersionUID = 1L;

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
    public static final UDTField<TypeUsageRecord, Long> USAGE_ID = createField(DSL.name("usage_id"), SQLDataType.BIGINT, TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.value</code>.
     */
    public static final UDTField<TypeUsageRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.value_prese</code>.
     */
    public static final UDTField<TypeUsageRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB, TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.lang</code>.
     */
    public static final UDTField<TypeUsageRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.complexity</code>.
     */
    public static final UDTField<TypeUsageRecord, String> COMPLEXITY = createField(DSL.name("complexity"), SQLDataType.VARCHAR(100), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_translations</code>.
     */
    public static final UDTField<TypeUsageRecord, String[]> USAGE_TRANSLATIONS = createField(DSL.name("usage_translations"), SQLDataType.CLOB.getArrayDataType(), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.usage_definitions</code>.
     */
    public static final UDTField<TypeUsageRecord, String[]> USAGE_DEFINITIONS = createField(DSL.name("usage_definitions"), SQLDataType.CLOB.getArrayDataType(), TYPE_USAGE, "");

    /**
     * The attribute <code>public.type_usage.source_links</code>.
     */
    public static final UDTField<TypeUsageRecord, JSON> SOURCE_LINKS = createField(DSL.name("source_links"), SQLDataType.JSON, TYPE_USAGE, "");

    /**
     * No further instances allowed
     */
    private TypeUsage() {
        super("type_usage", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}