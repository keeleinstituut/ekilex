/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Indexes;
import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.MeaningRelMappingRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MeaningRelMapping extends TableImpl<MeaningRelMappingRecord> {

    private static final long serialVersionUID = -2017550504;

    /**
     * The reference instance of <code>public.meaning_rel_mapping</code>
     */
    public static final MeaningRelMapping MEANING_REL_MAPPING = new MeaningRelMapping();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MeaningRelMappingRecord> getRecordType() {
        return MeaningRelMappingRecord.class;
    }

    /**
     * The column <code>public.meaning_rel_mapping.code1</code>.
     */
    public final TableField<MeaningRelMappingRecord, String> CODE1 = createField(DSL.name("code1"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.meaning_rel_mapping.code2</code>.
     */
    public final TableField<MeaningRelMappingRecord, String> CODE2 = createField(DSL.name("code2"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * Create a <code>public.meaning_rel_mapping</code> table reference
     */
    public MeaningRelMapping() {
        this(DSL.name("meaning_rel_mapping"), null);
    }

    /**
     * Create an aliased <code>public.meaning_rel_mapping</code> table reference
     */
    public MeaningRelMapping(String alias) {
        this(DSL.name(alias), MEANING_REL_MAPPING);
    }

    /**
     * Create an aliased <code>public.meaning_rel_mapping</code> table reference
     */
    public MeaningRelMapping(Name alias) {
        this(alias, MEANING_REL_MAPPING);
    }

    private MeaningRelMapping(Name alias, Table<MeaningRelMappingRecord> aliased) {
        this(alias, aliased, null);
    }

    private MeaningRelMapping(Name alias, Table<MeaningRelMappingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> MeaningRelMapping(Table<O> child, ForeignKey<O, MeaningRelMappingRecord> key) {
        super(child, key, MEANING_REL_MAPPING);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MEANING_REL_MAPPING_CODE1_IDX, Indexes.MEANING_REL_MAPPING_CODE2_IDX);
    }

    @Override
    public List<UniqueKey<MeaningRelMappingRecord>> getKeys() {
        return Arrays.<UniqueKey<MeaningRelMappingRecord>>asList(Keys.MEANING_REL_MAPPING_CODE1_CODE2_KEY);
    }

    @Override
    public List<ForeignKey<MeaningRelMappingRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MeaningRelMappingRecord, ?>>asList(Keys.MEANING_REL_MAPPING__MEANING_REL_MAPPING_CODE1_FKEY, Keys.MEANING_REL_MAPPING__MEANING_REL_MAPPING_CODE2_FKEY);
    }

    public MeaningRelType meaningRelMappingCode1Fkey() {
        return new MeaningRelType(this, Keys.MEANING_REL_MAPPING__MEANING_REL_MAPPING_CODE1_FKEY);
    }

    public MeaningRelType meaningRelMappingCode2Fkey() {
        return new MeaningRelType(this, Keys.MEANING_REL_MAPPING__MEANING_REL_MAPPING_CODE2_FKEY);
    }

    @Override
    public MeaningRelMapping as(String alias) {
        return new MeaningRelMapping(DSL.name(alias), this);
    }

    @Override
    public MeaningRelMapping as(Name alias) {
        return new MeaningRelMapping(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningRelMapping rename(String name) {
        return new MeaningRelMapping(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MeaningRelMapping rename(Name name) {
        return new MeaningRelMapping(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
