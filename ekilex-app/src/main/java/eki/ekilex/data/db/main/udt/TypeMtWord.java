/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.udt.records.TypeMtWordRecord;

import java.time.LocalDateTime;

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
public class TypeMtWord extends UDTImpl<TypeMtWordRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_mt_word</code>
     */
    public static final TypeMtWord TYPE_MT_WORD = new TypeMtWord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeMtWordRecord> getRecordType() {
        return TypeMtWordRecord.class;
    }

    /**
     * The attribute <code>public.type_mt_word.lexeme_id</code>.
     */
    public static final UDTField<TypeMtWordRecord, Long> LEXEME_ID = createField(DSL.name("lexeme_id"), SQLDataType.BIGINT, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.word_id</code>.
     */
    public static final UDTField<TypeMtWordRecord, Long> WORD_ID = createField(DSL.name("word_id"), SQLDataType.BIGINT, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.value</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.value_prese</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> VALUE_PRESE = createField(DSL.name("value_prese"), SQLDataType.CLOB, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.lang</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.CHAR(3), TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.homonym_nr</code>.
     */
    public static final UDTField<TypeMtWordRecord, Integer> HOMONYM_NR = createField(DSL.name("homonym_nr"), SQLDataType.INTEGER, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.display_morph_code</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> DISPLAY_MORPH_CODE = createField(DSL.name("display_morph_code"), SQLDataType.VARCHAR(100), TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.gender_code</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> GENDER_CODE = createField(DSL.name("gender_code"), SQLDataType.VARCHAR(100), TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.aspect_code</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> ASPECT_CODE = createField(DSL.name("aspect_code"), SQLDataType.VARCHAR(100), TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.vocal_form</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> VOCAL_FORM = createField(DSL.name("vocal_form"), SQLDataType.CLOB, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.morphophono_form</code>.
     */
    public static final UDTField<TypeMtWordRecord, String> MORPHOPHONO_FORM = createField(DSL.name("morphophono_form"), SQLDataType.CLOB, TYPE_MT_WORD, "");

    /**
     * The attribute <code>public.type_mt_word.manual_event_on</code>.
     */
    public static final UDTField<TypeMtWordRecord, LocalDateTime> MANUAL_EVENT_ON = createField(DSL.name("manual_event_on"), SQLDataType.LOCALDATETIME(0), TYPE_MT_WORD, "");

    /**
     * No further instances allowed
     */
    private TypeMtWord() {
        super("type_mt_word", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
