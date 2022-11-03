/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Keys;
import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ProficiencyLevelRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class ProficiencyLevel extends TableImpl<ProficiencyLevelRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.proficiency_level</code>
     */
    public static final ProficiencyLevel PROFICIENCY_LEVEL = new ProficiencyLevel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ProficiencyLevelRecord> getRecordType() {
        return ProficiencyLevelRecord.class;
    }

    /**
     * The column <code>public.proficiency_level.code</code>.
     */
    public final TableField<ProficiencyLevelRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.proficiency_level.datasets</code>.
     */
    public final TableField<ProficiencyLevelRecord, String[]> DATASETS = createField(DSL.name("datasets"), SQLDataType.VARCHAR(10).getArrayDataType(), this, "");

    /**
     * The column <code>public.proficiency_level.order_by</code>.
     */
    public final TableField<ProficiencyLevelRecord, Long> ORDER_BY = createField(DSL.name("order_by"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private ProficiencyLevel(Name alias, Table<ProficiencyLevelRecord> aliased) {
        this(alias, aliased, null);
    }

    private ProficiencyLevel(Name alias, Table<ProficiencyLevelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.proficiency_level</code> table reference
     */
    public ProficiencyLevel(String alias) {
        this(DSL.name(alias), PROFICIENCY_LEVEL);
    }

    /**
     * Create an aliased <code>public.proficiency_level</code> table reference
     */
    public ProficiencyLevel(Name alias) {
        this(alias, PROFICIENCY_LEVEL);
    }

    /**
     * Create a <code>public.proficiency_level</code> table reference
     */
    public ProficiencyLevel() {
        this(DSL.name("proficiency_level"), null);
    }

    public <O extends Record> ProficiencyLevel(Table<O> child, ForeignKey<O, ProficiencyLevelRecord> key) {
        super(child, key, PROFICIENCY_LEVEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<ProficiencyLevelRecord, Long> getIdentity() {
        return (Identity<ProficiencyLevelRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ProficiencyLevelRecord> getPrimaryKey() {
        return Keys.PROFICIENCY_LEVEL_PKEY;
    }

    @Override
    public List<UniqueKey<ProficiencyLevelRecord>> getKeys() {
        return Arrays.<UniqueKey<ProficiencyLevelRecord>>asList(Keys.PROFICIENCY_LEVEL_PKEY);
    }

    @Override
    public ProficiencyLevel as(String alias) {
        return new ProficiencyLevel(DSL.name(alias), this);
    }

    @Override
    public ProficiencyLevel as(Name alias) {
        return new ProficiencyLevel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ProficiencyLevel rename(String name) {
        return new ProficiencyLevel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ProficiencyLevel rename(Name name) {
        return new ProficiencyLevel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String[], Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
