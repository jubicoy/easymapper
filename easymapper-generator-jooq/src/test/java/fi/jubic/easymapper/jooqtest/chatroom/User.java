package fi.jubic.easymapper.jooqtest.chatroom;

import fi.jubic.easymapper.annotations.EasyId;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.ChatUserRecord;
import fi.jubic.easyvalue.EasyValue;

import java.util.Collections;
import java.util.List;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;

@EasyValue
public abstract class User {
    @EasyId
    public abstract Integer getId();

    public abstract Role getRole();

    public abstract String getName();

    public abstract List<Message> getMessages();

    public abstract List<Room> getRooms();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder()
                .setMessages(Collections.emptyList())
                .setRooms(Collections.emptyList());
    }

    public static class Builder extends EasyValue_User.Builder {
    }

    public static UserTableMapper<ChatUserRecord> mapper = UserTableMapper.builder(CHAT_USER)
            .setIdAccessor(CHAT_USER.ID)
            .setRoleAccessor(CHAT_USER.ROLE, Role::toString, Role::parse)
            .setNameAccessor(CHAT_USER.NAME)
            .build();
}
