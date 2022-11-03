/*
 * This file is generated by jOOQ.
 */
package eki.wordweb.data.db.udt;


import eki.wordweb.data.db.Public;
import eki.wordweb.data.db.udt.records.TypeDefinitionRecord;

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
public class TypeDefinition extends UDTImpl<TypeDefinitionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_definition</code>
     */
    public static final TypeDefinition TYPE_DEFINITION = new TypeDefinition();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeDefinitionRecord> getRecordType() {
        return TypeDefinitionRecord.class;
    }

    /**
     * The attribute <code>public.type_definition.lexeme_id</code>.
     */
    public static final UDTField<TypeDefinitionRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT, TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.meaning_id</code>.
     */
    public static final UDTField<TypeDefinitionRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT, TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.definition_id</code>.
     */
    public static final UDTField<TypeDefinitionRecord, Long> DEFINITION_ID = createField(DSL.name("definition_id"), SQLDataType.BIGINT, TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.value</code>.
     */
    public static final UDTField<TypeDefinitionRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.value_prese</code>.
     */
    public static final UDTField<TypeDefinitionRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB, TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.lang</code>.
     */
    public static final UDTField<TypeDefinitionRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.complexity</code>.
     */
    public static final UDTField<TypeDefinitionRecord, String> COMPLEXITY = createField(DSL.name("complexity"), SQLDataType.VARCHAR(100), TYPE_DEFINITION, "");

    /**
     * The attribute <code>public.type_definition.notes</code>.
     */
    public static final UDTField<TypeDefinitionRecord, String[]> NOTES = createField(DSL.name("notes"), SQLDataType.CLOB.getArrayDataType(), TYPE_DEFINITION, "");

    /**
     * No further instances allowed
     */
    private TypeDefinition() {
        super("type_definition", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
