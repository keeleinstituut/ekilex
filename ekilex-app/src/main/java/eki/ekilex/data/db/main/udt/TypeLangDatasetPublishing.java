/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.udt;


import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.udt.records.TypeLangDatasetPublishingRecord;

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
public class TypeLangDatasetPublishing extends UDTImpl<TypeLangDatasetPublishingRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.type_lang_dataset_publishing</code>
     */
    public static final TypeLangDatasetPublishing TYPE_LANG_DATASET_PUBLISHING = new TypeLangDatasetPublishing();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TypeLangDatasetPublishingRecord> getRecordType() {
        return TypeLangDatasetPublishingRecord.class;
    }

    /**
     * The attribute <code>public.type_lang_dataset_publishing.lang</code>.
     */
    public static final UDTField<TypeLangDatasetPublishingRecord, String> LANG = createField(DSL.name("lang"), SQLDataType.VARCHAR(10), TYPE_LANG_DATASET_PUBLISHING, "");

    /**
     * The attribute <code>public.type_lang_dataset_publishing.dataset_code</code>.
     */
    public static final UDTField<TypeLangDatasetPublishingRecord, String> DATASET_CODE = createField(DSL.name("dataset_code"), SQLDataType.VARCHAR(10), TYPE_LANG_DATASET_PUBLISHING, "");

    /**
     * The attribute <code>public.type_lang_dataset_publishing.is_ww_unif</code>.
     */
    public static final UDTField<TypeLangDatasetPublishingRecord, Boolean> IS_WW_UNIF = createField(DSL.name("is_ww_unif"), SQLDataType.BOOLEAN, TYPE_LANG_DATASET_PUBLISHING, "");

    /**
     * The attribute <code>public.type_lang_dataset_publishing.is_ww_lite</code>.
     */
    public static final UDTField<TypeLangDatasetPublishingRecord, Boolean> IS_WW_LITE = createField(DSL.name("is_ww_lite"), SQLDataType.BOOLEAN, TYPE_LANG_DATASET_PUBLISHING, "");

    /**
     * The attribute <code>public.type_lang_dataset_publishing.is_ww_od</code>.
     */
    public static final UDTField<TypeLangDatasetPublishingRecord, Boolean> IS_WW_OD = createField(DSL.name("is_ww_od"), SQLDataType.BOOLEAN, TYPE_LANG_DATASET_PUBLISHING, "");

    /**
     * No further instances allowed
     */
    private TypeLangDatasetPublishing() {
        super("type_lang_dataset_publishing", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC != null ? Public.PUBLIC : new SchemaImpl(DSL.name("public"));
    }
}
