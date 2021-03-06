/*
 * This file is generated by jOOQ.
 */
package fi.jubic.easymapper.jooqtest.chatroom.db.tables;


import fi.jubic.easymapper.jooqtest.chatroom.db.ChatSample;
import fi.jubic.easymapper.jooqtest.chatroom.db.Keys;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.ChatUserRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class ChatUser extends TableImpl<ChatUserRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>CHAT_SAMPLE.CHAT_USER</code>
     */
    public static final ChatUser CHAT_USER = new ChatUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ChatUserRecord> getRecordType() {
        return ChatUserRecord.class;
    }

    /**
     * The column <code>CHAT_SAMPLE.CHAT_USER.ID</code>.
     */
    public final TableField<ChatUserRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>CHAT_SAMPLE.CHAT_USER.ROLE</code>.
     */
    public final TableField<ChatUserRecord, String> ROLE = createField(DSL.name("ROLE"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>CHAT_SAMPLE.CHAT_USER.NAME</code>.
     */
    public final TableField<ChatUserRecord, String> NAME = createField(DSL.name("NAME"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>CHAT_SAMPLE.CHAT_USER.DELETED</code>.
     */
    public final TableField<ChatUserRecord, Boolean> DELETED = createField(DSL.name("DELETED"), SQLDataType.BOOLEAN.nullable(false), this, "");

    private ChatUser(Name alias, Table<ChatUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private ChatUser(Name alias, Table<ChatUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>CHAT_SAMPLE.CHAT_USER</code> table reference
     */
    public ChatUser(String alias) {
        this(DSL.name(alias), CHAT_USER);
    }

    /**
     * Create an aliased <code>CHAT_SAMPLE.CHAT_USER</code> table reference
     */
    public ChatUser(Name alias) {
        this(alias, CHAT_USER);
    }

    /**
     * Create a <code>CHAT_SAMPLE.CHAT_USER</code> table reference
     */
    public ChatUser() {
        this(DSL.name("CHAT_USER"), null);
    }

    public <O extends Record> ChatUser(Table<O> child, ForeignKey<O, ChatUserRecord> key) {
        super(child, key, CHAT_USER);
    }

    @Override
    public Schema getSchema() {
        return ChatSample.CHAT_SAMPLE;
    }

    @Override
    public Identity<ChatUserRecord, Integer> getIdentity() {
        return (Identity<ChatUserRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<ChatUserRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_8;
    }

    @Override
    public List<UniqueKey<ChatUserRecord>> getKeys() {
        return Arrays.<UniqueKey<ChatUserRecord>>asList(Keys.CONSTRAINT_8);
    }

    @Override
    public ChatUser as(String alias) {
        return new ChatUser(DSL.name(alias), this);
    }

    @Override
    public ChatUser as(Name alias) {
        return new ChatUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ChatUser rename(String name) {
        return new ChatUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ChatUser rename(Name name) {
        return new ChatUser(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, String, Boolean> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
