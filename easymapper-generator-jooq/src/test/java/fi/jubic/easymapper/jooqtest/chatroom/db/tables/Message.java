/*
 * This file is generated by jOOQ.
 */
package fi.jubic.easymapper.jooqtest.chatroom.db.tables;


import fi.jubic.easymapper.jooqtest.chatroom.db.ChatSample;
import fi.jubic.easymapper.jooqtest.chatroom.db.Keys;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.MessageRecord;

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
public class Message extends TableImpl<MessageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>CHAT_SAMPLE.MESSAGE</code>
     */
    public static final Message MESSAGE = new Message();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MessageRecord> getRecordType() {
        return MessageRecord.class;
    }

    /**
     * The column <code>CHAT_SAMPLE.MESSAGE.ID</code>.
     */
    public final TableField<MessageRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>CHAT_SAMPLE.MESSAGE.TEXT</code>.
     */
    public final TableField<MessageRecord, String> TEXT = createField(DSL.name("TEXT"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>CHAT_SAMPLE.MESSAGE.AUTHOR_ID</code>.
     */
    public final TableField<MessageRecord, Integer> AUTHOR_ID = createField(DSL.name("AUTHOR_ID"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>CHAT_SAMPLE.MESSAGE.ROOM_ID</code>.
     */
    public final TableField<MessageRecord, Integer> ROOM_ID = createField(DSL.name("ROOM_ID"), SQLDataType.INTEGER.nullable(false), this, "");

    private Message(Name alias, Table<MessageRecord> aliased) {
        this(alias, aliased, null);
    }

    private Message(Name alias, Table<MessageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>CHAT_SAMPLE.MESSAGE</code> table reference
     */
    public Message(String alias) {
        this(DSL.name(alias), MESSAGE);
    }

    /**
     * Create an aliased <code>CHAT_SAMPLE.MESSAGE</code> table reference
     */
    public Message(Name alias) {
        this(alias, MESSAGE);
    }

    /**
     * Create a <code>CHAT_SAMPLE.MESSAGE</code> table reference
     */
    public Message() {
        this(DSL.name("MESSAGE"), null);
    }

    public <O extends Record> Message(Table<O> child, ForeignKey<O, MessageRecord> key) {
        super(child, key, MESSAGE);
    }

    @Override
    public Schema getSchema() {
        return ChatSample.CHAT_SAMPLE;
    }

    @Override
    public Identity<MessageRecord, Integer> getIdentity() {
        return (Identity<MessageRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<MessageRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_6;
    }

    @Override
    public List<UniqueKey<MessageRecord>> getKeys() {
        return Arrays.<UniqueKey<MessageRecord>>asList(Keys.CONSTRAINT_6);
    }

    @Override
    public List<ForeignKey<MessageRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MessageRecord, ?>>asList(Keys.CONSTRAINT_63, Keys.CONSTRAINT_63B);
    }

    public ChatUser chatUser() {
        return new ChatUser(this, Keys.CONSTRAINT_63);
    }

    public Room room() {
        return new Room(this, Keys.CONSTRAINT_63B);
    }

    @Override
    public Message as(String alias) {
        return new Message(DSL.name(alias), this);
    }

    @Override
    public Message as(Name alias) {
        return new Message(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Message rename(String name) {
        return new Message(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Message rename(Name name) {
        return new Message(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, Integer, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
