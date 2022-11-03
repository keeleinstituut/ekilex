/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.tables;


import eki.ekilex.data.db.Public;
import eki.ekilex.data.db.tables.records.ViewWwDefinitionSourceLinkRecord;
import eki.ekilex.data.db.udt.records.TypeSourceLinkRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewWwDefinitionSourceLink extends TableImpl<ViewWwDefinitionSourceLinkRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.view_ww_definition_source_link</code>
     */
    public static final ViewWwDefinitionSourceLink VIEW_WW_DEFINITION_SOURCE_LINK = new ViewWwDefinitionSourceLink();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewWwDefinitionSourceLinkRecord> getRecordType() {
        return ViewWwDefinitionSourceLinkRecord.class;
    }

    /**
     * The column <code>public.view_ww_definition_source_link.meaning_id</code>.
     */
    public final TableField<ViewWwDefinitionSourceLinkRecord, Long> MEANING_ID = createField(DSL.name("meaning_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.view_ww_definition_source_link.source_links</code>.
     */
    public final TableField<ViewWwDefinitionSourceLinkRecord, TypeSourceLinkRecord[]> SOURCE_LINKS = createField(DSL.name("source_links"), eki.ekilex.data.db.udt.TypeSourceLink.TYPE_SOURCE_LINK.getDataType().getArrayDataType(), this, "");

    private ViewWwDefinitionSourceLink(Name alias, Table<ViewWwDefinitionSourceLinkRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewWwDefinitionSourceLink(Name alias, Table<ViewWwDefinitionSourceLinkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"view_ww_definition_source_link\" as  SELECT dsl.meaning_id,\n    array_agg(ROW('DEFINITION'::character varying(100), dsl.definition_id, dsl.source_link_id, dsl.type, dsl.name, dsl.value, dsl.order_by, dsl.source_id, dsl.source_props)::type_source_link ORDER BY dsl.definition_id, dsl.order_by) AS source_links\n   FROM ( SELECT d.meaning_id,\n            d.id AS definition_id,\n            dsl_1.id AS source_link_id,\n            dsl_1.type,\n            dsl_1.name,\n            dsl_1.value,\n            dsl_1.order_by,\n            s.source_id,\n            s.source_props\n           FROM lexeme l,\n            dataset ds,\n            definition d,\n            definition_source_link dsl_1,\n            ( SELECT s_1.id AS source_id,\n                    array_agg(encode_text(ff.value_prese) ORDER BY ff.order_by) AS source_props\n                   FROM source s_1,\n                    source_freeform sff,\n                    freeform ff\n                  WHERE ((sff.source_id = s_1.id) AND (sff.freeform_id = ff.id) AND ((ff.type)::text <> ALL ((ARRAY['SOURCE_FILE'::character varying, 'EXTERNAL_SOURCE_ID'::character varying])::text[])))\n                  GROUP BY s_1.id) s\n          WHERE ((l.is_public = true) AND (l.meaning_id = d.meaning_id) AND (d.is_public = true) AND (dsl_1.definition_id = d.id) AND (dsl_1.source_id = s.source_id) AND ((ds.code)::text = (l.dataset_code)::text) AND (ds.is_public = true))\n          GROUP BY d.meaning_id, d.id, dsl_1.id, s.source_id, s.source_props) dsl\n  GROUP BY dsl.meaning_id\n  ORDER BY dsl.meaning_id;"));
    }

    /**
     * Create an aliased <code>public.view_ww_definition_source_link</code> table reference
     */
    public ViewWwDefinitionSourceLink(String alias) {
        this(DSL.name(alias), VIEW_WW_DEFINITION_SOURCE_LINK);
    }

    /**
     * Create an aliased <code>public.view_ww_definition_source_link</code> table reference
     */
    public ViewWwDefinitionSourceLink(Name alias) {
        this(alias, VIEW_WW_DEFINITION_SOURCE_LINK);
    }

    /**
     * Create a <code>public.view_ww_definition_source_link</code> table reference
     */
    public ViewWwDefinitionSourceLink() {
        this(DSL.name("view_ww_definition_source_link"), null);
    }

    public <O extends Record> ViewWwDefinitionSourceLink(Table<O> child, ForeignKey<O, ViewWwDefinitionSourceLinkRecord> key) {
        super(child, key, VIEW_WW_DEFINITION_SOURCE_LINK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public ViewWwDefinitionSourceLink as(String alias) {
        return new ViewWwDefinitionSourceLink(DSL.name(alias), this);
    }

    @Override
    public ViewWwDefinitionSourceLink as(Name alias) {
        return new ViewWwDefinitionSourceLink(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwDefinitionSourceLink rename(String name) {
        return new ViewWwDefinitionSourceLink(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewWwDefinitionSourceLink rename(Name name) {
        return new ViewWwDefinitionSourceLink(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, TypeSourceLinkRecord[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
