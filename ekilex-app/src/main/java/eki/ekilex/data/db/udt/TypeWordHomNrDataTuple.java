/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.udt;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.udt.records.TypeWordHomNrDataTupleRecord;

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
public class TypeWordHomNrDataTuple extends UDTImpl<TypeWordHomNrDataTupleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_word_hom_nr_data_tuple</code>
     */
    public static final TypeWordHomNrDataTuple TYPE_WORD_HOM_NR_DATA_TUPLE = new TypeWordHomNrDataTuple();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeWordHomNrDataTupleRecord> getRecordType() {
        return TypeWordHomNrDataTupleRecord.class;
    }

    /**
     * The attribute <code>public.type_word_hom_nr_data_tuple.word_id</code>.
     */
    public static final UDTField<TypeWordHomNrDataTupleRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, TYPE_WORD_HOM_NR_DATA_TUPLE, "");

    /**
     * The attribute <code>public.type_word_hom_nr_data_tuple.homonym_nr</code>.
     */
    public static final UDTField<TypeWordHomNrDataTupleRecord, Integer> HOMONYM_NR = createField(DSL.name("homonym_nr"), SQLDataType.INTEGER, TYPE_WORD_HOM_NR_DATA_TUPLE, "");

    /**
     * No further instances allowed
     */
    private TypeWordHomNrDataTuple() {
        super("type_word_hom_nr_data_tuple", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
