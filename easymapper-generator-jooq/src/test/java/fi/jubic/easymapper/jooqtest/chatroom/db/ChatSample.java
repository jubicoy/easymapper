/*
 * This file is generated by jOOQ.
 */
package fi.jubic.easymapper.jooqtest.chatroom.db;


import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Message;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ChatSample extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>CHAT_SAMPLE</code>
     */
    public static final ChatSample CHAT_SAMPLE = new ChatSample();

    /**
     * The table <code>CHAT_SAMPLE.CHAT_USER</code>.
     */
    public final ChatUser CHAT_USER = ChatUser.CHAT_USER;

    /**
     * The table <code>CHAT_SAMPLE.MESSAGE</code>.
     */
    public final Message MESSAGE = Message.MESSAGE;

    /**
     * The table <code>CHAT_SAMPLE.ROOM</code>.
     */
    public final Room ROOM = Room.ROOM;

    /**
     * The table <code>CHAT_SAMPLE.ROOM_MEMBERSHIP</code>.
     */
    public final RoomMembership ROOM_MEMBERSHIP = RoomMembership.ROOM_MEMBERSHIP;

    /**
     * No further instances allowed
     */
    private ChatSample() {
        super("CHAT_SAMPLE", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            ChatUser.CHAT_USER,
            Message.MESSAGE,
            Room.ROOM,
            RoomMembership.ROOM_MEMBERSHIP);
    }
}
