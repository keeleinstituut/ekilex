/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.udt.records.TypeFreeformRecord;

import java.sql.Timestamp;

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
public class TypeFreeform extends UDTImpl<TypeFreeformRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_freeform</code>
     */
    public static final TypeFreeform TYPE_FREEFORM = new TypeFreeform();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeFreeformRecord> getRecordType() {
        return TypeFreeformRecord.class;
    }

    /**
     * The attribute <code>public.type_freeform.freeform_id</code>.
     */
    public static final UDTField<TypeFreeformRecord, Long> FREEFORM_ID = createField(DSL.name("freeform_id"), SQLDataType.BIGINT, TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.freeform_type_code</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> FREEFORM_TYPE_CODE = createField(DSL.name("freeform_type_code"), SQLDataType.VARCHAR(100), TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.value</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.lang</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.complexity</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> COMPLEXITY = createField(DSL.name("complexity"), SQLDataType.VARCHAR(100), TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.created_by</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CLOB, TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.created_on</code>.
     */
    public static final UDTField<TypeFreeformRecord, Timestamp> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.TIMESTAMP(0), TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.modified_by</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> MODIFIED_BY = createField(DSL.name("modified_by"), SQLDataType.CLOB, TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.modified_on</code>.
     */
    public static final UDTField<TypeFreeformRecord, Timestamp> MODIFIED_ON = createField(DSL.name("modified_on"), SQLDataType.TIMESTAMP(0), TYPE_FREEFORM, "");

    /**
     * No further instances allowed
     */
    private TypeFreeform() {
        super("type_freeform", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
