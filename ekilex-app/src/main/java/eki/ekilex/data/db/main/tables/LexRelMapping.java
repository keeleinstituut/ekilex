/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.LexRelMappingRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LexRelMapping extends TableImpl<LexRelMappingRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.lex_rel_mapping</code>
     */
    public static final LexRelMapping LEX_REL_MAPPING = new LexRelMapping();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LexRelMappingRecord> getRecordType() {
        return LexRelMappingRecord.class;
    }

    /**
     * The column <code>public.lex_rel_mapping.code1</code>.
     */
    public final TableField<LexRelMappingRecord, String> CODE1 = createField(DSL.name("code1"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.lex_rel_mapping.code2</code>.
     */
    public final TableField<LexRelMappingRecord, String> CODE2 = createField(DSL.name("code2"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    private LexRelMapping(Name alias, Table<LexRelMappingRecord> aliased) {
        this(alias, aliased, null);
    }

    private LexRelMapping(Name alias, Table<LexRelMappingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.lex_rel_mapping</code> table reference
     */
    public LexRelMapping(String alias) {
        this(DSL.name(alias), LEX_REL_MAPPING);
    }

    /**
     * Create an aliased <code>public.lex_rel_mapping</code> table reference
     */
    public LexRelMapping(Name alias) {
        this(alias, LEX_REL_MAPPING);
    }

    /**
     * Create a <code>public.lex_rel_mapping</code> table reference
     */
    public LexRelMapping() {
        this(DSL.name("lex_rel_mapping"), null);
    }

    public <O extends Record> LexRelMapping(Table<O> child, ForeignKey<O, LexRelMappingRecord> key) {
        super(child, key, LEX_REL_MAPPING);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<LexRelMappingRecord>> getKeys() {
        return Arrays.<UniqueKey<LexRelMappingRecord>>asList(Keys.LEX_REL_MAPPING_CODE1_CODE2_KEY);
    }

    @Override
    public List<ForeignKey<LexRelMappingRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LexRelMappingRecord, ?>>asList(Keys.LEX_REL_MAPPING__LEX_REL_MAPPING_CODE1_FKEY, Keys.LEX_REL_MAPPING__LEX_REL_MAPPING_CODE2_FKEY);
    }

    private transient LexRelType _lexRelMappingCode1Fkey;
    private transient LexRelType _lexRelMappingCode2Fkey;

    public LexRelType lexRelMappingCode1Fkey() {
        if (_lexRelMappingCode1Fkey == null)
            _lexRelMappingCode1Fkey = new LexRelType(this, Keys.LEX_REL_MAPPING__LEX_REL_MAPPING_CODE1_FKEY);

        return _lexRelMappingCode1Fkey;
    }

    public LexRelType lexRelMappingCode2Fkey() {
        if (_lexRelMappingCode2Fkey == null)
            _lexRelMappingCode2Fkey = new LexRelType(this, Keys.LEX_REL_MAPPING__LEX_REL_MAPPING_CODE2_FKEY);

        return _lexRelMappingCode2Fkey;
    }

    @Override
    public LexRelMapping as(String alias) {
        return new LexRelMapping(DSL.name(alias), this);
    }

    @Override
    public LexRelMapping as(Name alias) {
        return new LexRelMapping(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LexRelMapping rename(String name) {
        return new LexRelMapping(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LexRelMapping rename(Name name) {
        return new LexRelMapping(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}