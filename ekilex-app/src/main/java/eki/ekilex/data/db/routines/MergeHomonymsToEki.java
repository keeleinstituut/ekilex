/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.routines;


import eki.ekilex.data.db.Public;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MergeHomonymsToEki extends AbstractRoutine<java.lang.Void> {

    private static final long serialVersionUID = -1428701147;

    /**
     * The parameter <code>public.merge_homonyms_to_eki.included_langs</code>.
     */
    public static final Parameter<String[]> INCLUDED_LANGS = Internal.createParameter("included_langs", org.jooq.impl.SQLDataType.CHAR.getArrayDataType(), false, false);

    /**
     * Create a new routine call instance
     */
    public MergeHomonymsToEki() {
        super("merge_homonyms_to_eki", Public.PUBLIC);

        addInParameter(INCLUDED_LANGS);
    }

    /**
     * Set the <code>included_langs</code> parameter IN value to the routine
     */
    public void setIncludedLangs(String[] value) {
        setValue(INCLUDED_LANGS, value);
    }
}
