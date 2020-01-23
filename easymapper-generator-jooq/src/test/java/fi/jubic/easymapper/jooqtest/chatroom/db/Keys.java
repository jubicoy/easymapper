/*
 * This file is generated by jOOQ.
 */
package fi.jubic.easymapper.jooqtest.chatroom.db;


import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Message;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.ChatUserRecord;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.MessageRecord;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.RoomMembershipRecord;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.RoomRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>CHAT_SAMPLE</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<ChatUserRecord, Integer> IDENTITY_CHAT_USER = Identities0.IDENTITY_CHAT_USER;
    public static final Identity<MessageRecord, Integer> IDENTITY_MESSAGE = Identities0.IDENTITY_MESSAGE;
    public static final Identity<RoomRecord, Integer> IDENTITY_ROOM = Identities0.IDENTITY_ROOM;
    public static final Identity<RoomMembershipRecord, Integer> IDENTITY_ROOM_MEMBERSHIP = Identities0.IDENTITY_ROOM_MEMBERSHIP;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<ChatUserRecord> CONSTRAINT_8 = UniqueKeys0.CONSTRAINT_8;
    public static final UniqueKey<MessageRecord> CONSTRAINT_6 = UniqueKeys0.CONSTRAINT_6;
    public static final UniqueKey<RoomRecord> CONSTRAINT_2 = UniqueKeys0.CONSTRAINT_2;
    public static final UniqueKey<RoomMembershipRecord> CONSTRAINT_7 = UniqueKeys0.CONSTRAINT_7;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<MessageRecord, ChatUserRecord> CONSTRAINT_63 = ForeignKeys0.CONSTRAINT_63;
    public static final ForeignKey<MessageRecord, RoomRecord> CONSTRAINT_63B = ForeignKeys0.CONSTRAINT_63B;
    public static final ForeignKey<RoomRecord, ChatUserRecord> CONSTRAINT_26 = ForeignKeys0.CONSTRAINT_26;
    public static final ForeignKey<RoomRecord, ChatUserRecord> CONSTRAINT_267 = ForeignKeys0.CONSTRAINT_267;
    public static final ForeignKey<RoomMembershipRecord, RoomRecord> CONSTRAINT_74 = ForeignKeys0.CONSTRAINT_74;
    public static final ForeignKey<RoomMembershipRecord, ChatUserRecord> CONSTRAINT_747 = ForeignKeys0.CONSTRAINT_747;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<ChatUserRecord, Integer> IDENTITY_CHAT_USER = Internal.createIdentity(ChatUser.CHAT_USER, ChatUser.CHAT_USER.ID);
        public static Identity<MessageRecord, Integer> IDENTITY_MESSAGE = Internal.createIdentity(Message.MESSAGE, Message.MESSAGE.ID);
        public static Identity<RoomRecord, Integer> IDENTITY_ROOM = Internal.createIdentity(Room.ROOM, Room.ROOM.ID);
        public static Identity<RoomMembershipRecord, Integer> IDENTITY_ROOM_MEMBERSHIP = Internal.createIdentity(RoomMembership.ROOM_MEMBERSHIP, RoomMembership.ROOM_MEMBERSHIP.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<ChatUserRecord> CONSTRAINT_8 = Internal.createUniqueKey(ChatUser.CHAT_USER, "CONSTRAINT_8", ChatUser.CHAT_USER.ID);
        public static final UniqueKey<MessageRecord> CONSTRAINT_6 = Internal.createUniqueKey(Message.MESSAGE, "CONSTRAINT_6", Message.MESSAGE.ID);
        public static final UniqueKey<RoomRecord> CONSTRAINT_2 = Internal.createUniqueKey(Room.ROOM, "CONSTRAINT_2", Room.ROOM.ID);
        public static final UniqueKey<RoomMembershipRecord> CONSTRAINT_7 = Internal.createUniqueKey(RoomMembership.ROOM_MEMBERSHIP, "CONSTRAINT_7", RoomMembership.ROOM_MEMBERSHIP.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<MessageRecord, ChatUserRecord> CONSTRAINT_63 = Internal.createForeignKey(fi.jubic.easymapper.jooqtest.chatroom.db.Keys.CONSTRAINT_8, Message.MESSAGE, "CONSTRAINT_63", Message.MESSAGE.AUTHOR_ID);
        public static final ForeignKey<MessageRecord, RoomRecord> CONSTRAINT_63B = Internal.createForeignKey(fi.jubic.easymapper.jooqtest.chatroom.db.Keys.CONSTRAINT_2, Message.MESSAGE, "CONSTRAINT_63B", Message.MESSAGE.ROOM_ID);
        public static final ForeignKey<RoomRecord, ChatUserRecord> CONSTRAINT_26 = Internal.createForeignKey(fi.jubic.easymapper.jooqtest.chatroom.db.Keys.CONSTRAINT_8, Room.ROOM, "CONSTRAINT_26", Room.ROOM.ADMINISTRATOR_ID);
        public static final ForeignKey<RoomRecord, ChatUserRecord> CONSTRAINT_267 = Internal.createForeignKey(fi.jubic.easymapper.jooqtest.chatroom.db.Keys.CONSTRAINT_8, Room.ROOM, "CONSTRAINT_267", Room.ROOM.CREATED_BY_ID);
        public static final ForeignKey<RoomMembershipRecord, RoomRecord> CONSTRAINT_74 = Internal.createForeignKey(fi.jubic.easymapper.jooqtest.chatroom.db.Keys.CONSTRAINT_2, RoomMembership.ROOM_MEMBERSHIP, "CONSTRAINT_74", RoomMembership.ROOM_MEMBERSHIP.ROOM_ID);
        public static final ForeignKey<RoomMembershipRecord, ChatUserRecord> CONSTRAINT_747 = Internal.createForeignKey(fi.jubic.easymapper.jooqtest.chatroom.db.Keys.CONSTRAINT_8, RoomMembership.ROOM_MEMBERSHIP, "CONSTRAINT_747", RoomMembership.ROOM_MEMBERSHIP.USER_ID);
    }
}
