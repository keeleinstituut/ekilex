/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.udt;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.udt.records.TypeLangComplexityRecord;

import org.jooq.Schema;
import org.jooq.UDTField;
import org.jooq.impl.DSL;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.UDTImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeLangComplexity extends UDTImpl<TypeLangComplexityRecord> {

    private static final long serialVersionUID = -250821026;

    /**
     * The reference instance of <code>public.type_lang_complexity</code>
     */
    public static final TypeLangComplexity TYPE_LANG_COMPLEXITY = new TypeLangComplexity();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeLangComplexityRecord> getRecordType() {
        return TypeLangComplexityRecord.class;
    }

    /**
     * The attribute <code>public.type_lang_complexity.lang</code>.
     */
    public static final UDTField<TypeLangComplexityRecord, String> LANG = createField(DSL.name("lang"), org.jooq.impl.SQLDataType.VARCHAR(10), TYPE_LANG_COMPLEXITY, "");

    /**
     * The attribute <code>public.type_lang_complexity.dataset_code</code>.
     */
    public static final UDTField<TypeLangComplexityRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), org.jooq.impl.SQLDataType.VARCHAR(10), TYPE_LANG_COMPLEXITY, "");

    /**
     * The attribute <code>public.type_lang_complexity.lex_complexity</code>.
     */
    public static final UDTField<TypeLangComplexityRecord, String> LEX_COMPLEXITY = createField(DSL.name("lex_complexity"), org.jooq.impl.SQLDataType.VARCHAR(100), TYPE_LANG_COMPLEXITY, "");

    /**
     * The attribute <code>public.type_lang_complexity.data_complexity</code>.
     */
    public static final UDTField<TypeLangComplexityRecord, String> DATA_COMPLEXITY = createField(DSL.name("data_complexity"), org.jooq.impl.SQLDataType.VARCHAR(100), TYPE_LANG_COMPLEXITY, "");

    /**
     * No further instances allowed
     */
    private TypeLangComplexity() {
        super("type_lang_complexity", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
