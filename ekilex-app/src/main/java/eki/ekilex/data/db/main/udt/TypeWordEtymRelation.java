/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.udt.records.TypeWordEtymRelationRecord;

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
public class TypeWordEtymRelation extends UDTImpl<TypeWordEtymRelationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_word_etym_relation</code>
     */
    public static final TypeWordEtymRelation TYPE_WORD_ETYM_RELATION = new TypeWordEtymRelation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeWordEtymRelationRecord> getRecordType() {
        return TypeWordEtymRelationRecord.class;
    }

    /**
     * The attribute <code>public.type_word_etym_relation.word_etym_rel_id</code>.
     */
    public static final UDTField<TypeWordEtymRelationRecord, Long> WORD_ETYM_REL_ID = createField(DSL.name("word_etym_rel_id"), SQLDataType.BIGINT, TYPE_WORD_ETYM_RELATION, "");

    /**
     * The attribute <code>public.type_word_etym_relation.comment</code>.
     */
    public static final UDTField<TypeWordEtymRelationRecord, String> COMMENT = createField(DSL.name("comment"), SQLDataType.CLOB, TYPE_WORD_ETYM_RELATION, "");

    /**
     * The attribute <code>public.type_word_etym_relation.is_questionable</code>.
     */
    public static final UDTField<TypeWordEtymRelationRecord, Boolean> IS_QUESTIONABLE = createField(DSL.name("is_questionable"), SQLDataType.BOOLEAN, TYPE_WORD_ETYM_RELATION, "");

    /**
     * The attribute <code>public.type_word_etym_relation.is_compound</code>.
     */
    public static final UDTField<TypeWordEtymRelationRecord, Boolean> IS_COMPOUND = createField(DSL.name("is_compound"), SQLDataType.BOOLEAN, TYPE_WORD_ETYM_RELATION, "");

    /**
     * The attribute <code>public.type_word_etym_relation.related_word_id</code>.
     */
    public static final UDTField<TypeWordEtymRelationRecord, Long> RELATED_WORD_ID = createField(DSL.name("related_word_id"), SQLDataType.BIGINT, TYPE_WORD_ETYM_RELATION, "");

    /**
     * No further instances allowed
     */
    private TypeWordEtymRelation() {
        super("type_word_etym_relation", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}