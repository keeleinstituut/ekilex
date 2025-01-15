/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.udt.records.TypeSourceLinkRecord;

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
public class TypeSourceLink extends UDTImpl<TypeSourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_source_link</code>
     */
    public static final TypeSourceLink TYPE_SOURCE_LINK = new TypeSourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeSourceLinkRecord> getRecordType() {
        return TypeSourceLinkRecord.class;
    }

    /**
     * The attribute <code>public.type_source_link.source_link_id</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, Long> SOURCE_LINK_ID = createField(DSL.name("source_link_id"), SQLDataType.BIGINT, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.source_link_name</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, String> SOURCE_LINK_NAME = createField(DSL.name("source_link_name"), SQLDataType.CLOB, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.order_by</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.source_id</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, Long> SOURCE_ID = createField(DSL.name("source_id"), SQLDataType.BIGINT, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.source_name</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, String> SOURCE_NAME = createField(DSL.name("source_name"), SQLDataType.CLOB, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.source_value</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, String> SOURCE_VALUE = createField(DSL.name("source_value"), SQLDataType.CLOB, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.source_value_prese</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, String> SOURCE_VALUE_PRESE = createField(DSL.name("source_value_prese"), SQLDataType.CLOB, TYPE_SOURCE_LINK, "");

    /**
     * The attribute <code>public.type_source_link.is_source_public</code>.
     */
    public static final UDTField<TypeSourceLinkRecord, Boolean> IS_SOURCE_PUBLIC = createField(DSL.name("is_source_public"), SQLDataType.BOOLEAN, TYPE_SOURCE_LINK, "");

    /**
     * No further instances allowed
     */
    private TypeSourceLink() {
        super("type_source_link", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
