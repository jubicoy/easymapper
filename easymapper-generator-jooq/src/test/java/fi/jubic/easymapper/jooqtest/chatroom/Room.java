package fi.jubic.easymapper.jooqtest.chatroom;

import fi.jubic.easymapper.annotations.EasyId;
import fi.jubic.easymapper.jooq.Jooq;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.RoomRecord;
import fi.jubic.easyvalue.EasyValue;

import java.util.Collections;
import java.util.List;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room.ROOM;

@EasyValue
public abstract class Room {
    @EasyId
    public abstract Integer getId();

    public abstract String getName();

    public abstract User getAdmin();

    public abstract User getCreatedBy();

    public abstract List<User> getMembers();

    public abstract List<Message> getMessages();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder()
                .setMembers(Collections.emptyList())
                .setMessages(Collections.emptyList());
    }

    public static class Builder extends EasyValue_Room.Builder {

    }

    public static final RoomTableMapper<RoomRecord> mapper = RoomTableMapper.builder(ROOM)
            .setIdAccessor(ROOM.ID)
            .setNameAccessor(ROOM.NAME)
            .setAdminAccessor(ROOM.ADMINISTRATOR_ID, User::getId)
            .setCreatedByAccessor(ROOM.CREATED_BY_ID, User::getId)
            .build();
}
