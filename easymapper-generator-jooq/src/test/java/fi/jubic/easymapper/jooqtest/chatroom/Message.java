package fi.jubic.easymapper.jooqtest.chatroom;

import fi.jubic.easymapper.annotations.EasyId;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.MessageRecord;
import fi.jubic.easyvalue.EasyValue;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.Message.MESSAGE;

@EasyValue
public abstract class Message {
    @EasyId
    public abstract Integer getId();

    public abstract String getText();

    public abstract User getAuthor();

    public abstract Room getRoom();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends EasyValue_Message.Builder {

    }

    public static final MessageTableMapper<MessageRecord> mapper = MessageTableMapper
            .builder(MESSAGE)
            .setIdAccessor(MESSAGE.ID)
            .setTextAccessor(MESSAGE.TEXT)
            .setAuthorAccessor(MESSAGE.AUTHOR_ID, User::getId)
            .setRoomAccessor(MESSAGE.ROOM_ID, Room::getId)
            .build();
}
