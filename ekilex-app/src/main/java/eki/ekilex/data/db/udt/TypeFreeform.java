/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.udt.records.TypeFreeformRecord;

import org.jooq.Schema;
import org.jooq.UDTField;
import org.jooq.impl.DSL;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.UDTImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeFreeform extends UDTImpl<TypeFreeformRecord> {

    private static final long serialVersionUID = -57361741;

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
    public static final UDTField<TypeFreeformRecord, Long> FREEFORM_ID = createField(DSL.name("freeform_id"), org.jooq.impl.SQLDataType.BIGINT, TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.type</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> TYPE = createField(DSL.name("type"), org.jooq.impl.SQLDataType.VARCHAR(100), TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.value</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB, TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.lang</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> LANG = createField(DSL.name("lang"), org.jooq.impl.SQLDataType.CHAR(3), TYPE_FREEFORM, "");

    /**
     * The attribute <code>public.type_freeform.complexity</code>.
     */
    public static final UDTField<TypeFreeformRecord, String> COMPLEXITY = createField(DSL.name("complexity"), org.jooq.impl.SQLDataType.VARCHAR(100), TYPE_FREEFORM, "");

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
