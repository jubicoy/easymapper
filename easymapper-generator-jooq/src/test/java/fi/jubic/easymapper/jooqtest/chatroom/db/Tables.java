/*
 * This file is generated by jOOQ.
 */
package fi.jubic.easymapper.jooqtest.chatroom.db;


import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Message;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in CHAT_SAMPLE
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>CHAT_SAMPLE.CHAT_USER</code>.
     */
    public static final ChatUser CHAT_USER = fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;

    /**
     * The table <code>CHAT_SAMPLE.MESSAGE</code>.
     */
    public static final Message MESSAGE = fi.jubic.easymapper.jooqtest.chatroom.db.tables.Message.MESSAGE;

    /**
     * The table <code>CHAT_SAMPLE.ROOM</code>.
     */
    public static final Room ROOM = fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room.ROOM;

    /**
     * The table <code>CHAT_SAMPLE.ROOM_MEMBERSHIP</code>.
     */
    public static final RoomMembership ROOM_MEMBERSHIP = fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership.ROOM_MEMBERSHIP;
}
