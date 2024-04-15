/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.routines;


import eki.ekilex.data.db.Public;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActivityLogIdFunc extends AbstractRoutine<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * The parameter <code>public.activity_log_id_func.RETURN_VALUE</code>.
     */
    public static final Parameter<Long> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", SQLDataType.BIGINT, false, false);

    /**
     * Create a new routine call instance
     */
    public ActivityLogIdFunc() {
        super("activity_log_id_func", Public.PUBLIC, SQLDataType.BIGINT);

        setReturnParameter(RETURN_VALUE);
    }
}
