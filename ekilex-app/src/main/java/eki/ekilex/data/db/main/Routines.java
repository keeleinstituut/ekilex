/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main;


import eki.ekilex.data.db.main.routines.AdjustHomonymNrs;
import eki.ekilex.data.db.main.routines.EncodeText;

import org.jooq.Configuration;
import org.jooq.Field;


/**
 * Convenience access to all stored procedures and functions in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Routines {

    /**
     * Call <code>public.adjust_homonym_nrs</code>
     */
    public static void adjustHomonymNrs(
          Configuration configuration
    ) {
        AdjustHomonymNrs p = new AdjustHomonymNrs();

        p.execute(configuration);
    }

    /**
     * Call <code>public.encode_text</code>
     */
    public static String encodeText(
          Configuration configuration
        , String initialText
    ) {
        EncodeText f = new EncodeText();
        f.setInitialText(initialText);

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.encode_text</code> as a field.
     */
    public static Field<String> encodeText(
          String initialText
    ) {
        EncodeText f = new EncodeText();
        f.setInitialText(initialText);

        return f.asField();
    }

    /**
     * Get <code>public.encode_text</code> as a field.
     */
    public static Field<String> encodeText(
          Field<String> initialText
    ) {
        EncodeText f = new EncodeText();
        f.setInitialText(initialText);

        return f.asField();
    }
}
