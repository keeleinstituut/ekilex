/*
 * This file is generated by jOOQ.
 */
package eki.ekilex.data.db.main.tables;


import eki.ekilex.data.db.main.Keys;
import eki.ekilex.data.db.main.Public;
import eki.ekilex.data.db.main.tables.records.FeedbackLogCommentRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
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
public class FeedbackLogComment extends TableImpl<FeedbackLogCommentRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.feedback_log_comment</code>
     */
    public static final FeedbackLogComment FEEDBACK_LOG_COMMENT = new FeedbackLogComment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FeedbackLogCommentRecord> getRecordType() {
        return FeedbackLogCommentRecord.class;
    }

    /**
     * The column <code>public.feedback_log_comment.id</code>.
     */
    public final TableField<FeedbackLogCommentRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.feedback_log_comment.feedback_log_id</code>.
     */
    public final TableField<FeedbackLogCommentRecord, Long> FEEDBACK_LOG_ID = createField(DSL.name("feedback_log_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.feedback_log_comment.comment</code>.
     */
    public final TableField<FeedbackLogCommentRecord, String> COMMENT = createField(DSL.name("comment"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.feedback_log_comment.user_name</code>.
     */
    public final TableField<FeedbackLogCommentRecord, String> USER_NAME = createField(DSL.name("user_name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.feedback_log_comment.created_on</code>.
     */
    public final TableField<FeedbackLogCommentRecord, LocalDateTime> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("statement_timestamp()", SQLDataType.LOCALDATETIME)), this, "");

    private FeedbackLogComment(Name alias, Table<FeedbackLogCommentRecord> aliased) {
        this(alias, aliased, null);
    }

    private FeedbackLogComment(Name alias, Table<FeedbackLogCommentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.feedback_log_comment</code> table reference
     */
    public FeedbackLogComment(String alias) {
        this(DSL.name(alias), FEEDBACK_LOG_COMMENT);
    }

    /**
     * Create an aliased <code>public.feedback_log_comment</code> table reference
     */
    public FeedbackLogComment(Name alias) {
        this(alias, FEEDBACK_LOG_COMMENT);
    }

    /**
     * Create a <code>public.feedback_log_comment</code> table reference
     */
    public FeedbackLogComment() {
        this(DSL.name("feedback_log_comment"), null);
    }

    public <O extends Record> FeedbackLogComment(Table<O> child, ForeignKey<O, FeedbackLogCommentRecord> key) {
        super(child, key, FEEDBACK_LOG_COMMENT);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<FeedbackLogCommentRecord, Long> getIdentity() {
        return (Identity<FeedbackLogCommentRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<FeedbackLogCommentRecord> getPrimaryKey() {
        return Keys.FEEDBACK_LOG_COMMENT_PKEY;
    }

    @Override
    public List<UniqueKey<FeedbackLogCommentRecord>> getKeys() {
        return Arrays.<UniqueKey<FeedbackLogCommentRecord>>asList(Keys.FEEDBACK_LOG_COMMENT_PKEY);
    }

    @Override
    public List<ForeignKey<FeedbackLogCommentRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<FeedbackLogCommentRecord, ?>>asList(Keys.FEEDBACK_LOG_COMMENT__FEEDBACK_LOG_COMMENT_FEEDBACK_LOG_ID_FKEY);
    }

    private transient FeedbackLog _feedbackLog;

    public FeedbackLog feedbackLog() {
        if (_feedbackLog == null)
            _feedbackLog = new FeedbackLog(this, Keys.FEEDBACK_LOG_COMMENT__FEEDBACK_LOG_COMMENT_FEEDBACK_LOG_ID_FKEY);

        return _feedbackLog;
    }

    @Override
    public FeedbackLogComment as(String alias) {
        return new FeedbackLogComment(DSL.name(alias), this);
    }

    @Override
    public FeedbackLogComment as(Name alias) {
        return new FeedbackLogComment(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FeedbackLogComment rename(String name) {
        return new FeedbackLogComment(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FeedbackLogComment rename(Name name) {
        return new FeedbackLogComment(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, Long, String, String, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
