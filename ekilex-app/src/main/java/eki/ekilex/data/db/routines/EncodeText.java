/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.routines;


import eki.ekilex.data.db.Public;

import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EncodeText extends AbstractRoutine<String> {

    private static final long serialVersionUID = 1L;

    /**
     * The parameter <code>public.encode_text.RETURN_VALUE</code>.
     */
    public static final Parameter<String> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", SQLDataType.CLOB, false, false);

    /**
     * The parameter <code>public.encode_text.initial_text</code>.
     */
    public static final Parameter<String> INITIAL_TEXT = Internal.createParameter("initial_text", SQLDataType.CLOB, false, false);

    /**
     * Create a new routine call instance
     */
    public EncodeText() {
        super("encode_text", Public.PUBLIC, SQLDataType.CLOB);

        setReturnParameter(RETURN_VALUE);
        addInParameter(INITIAL_TEXT);
    }

    /**
     * Set the <code>initial_text</code> parameter IN value to the routine
     */
    public void setInitialText(String value) {
        setValue(INITIAL_TEXT, value);
    }

    /**
     * Set the <code>initial_text</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setInitialText(Field<String> field) {
        setField(INITIAL_TEXT, field);
    }
}
