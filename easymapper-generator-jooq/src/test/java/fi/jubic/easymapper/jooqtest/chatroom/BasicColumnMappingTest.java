package fi.jubic.easymapper.jooqtest.chatroom;

import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.ChatUserRecord;
import fi.jubic.easymapper.jooqtest.chatroom.models.Role;
import fi.jubic.easymapper.jooqtest.chatroom.models.User;
import fi.jubic.easymapper.jooqtest.chatroom.models.UserRecordMapper;
import org.junit.jupiter.api.Test;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;
import static org.junit.Assert.assertEquals;

class BasicColumnMappingTest {
    @Test
    void testColumnMapping() {
        ChatUserRecord record = new ChatUserRecord();
        record.set(CHAT_USER.ID, 1);
        record.set(CHAT_USER.ROLE, "USER");
        record.set(CHAT_USER.NAME, "Antti Admin");
        record.set(CHAT_USER.DELETED, false);

        UserRecordMapper<ChatUserRecord> mapper = UserRecordMapper.builder(CHAT_USER)
                .setIdAccessor(CHAT_USER.ID)
                .setRoleAccessor(CHAT_USER.ROLE, Role::toString, Role::parse)
                .setNameAccessor(CHAT_USER.NAME)
                .setDeletedAccessor(CHAT_USER.DELETED)
                .build();

        User user = mapper.map(record);

        assertEquals(
                User.builder()
                        .setId(1)
                        .setRole(Role.USER)
                        .setName("Antti Admin")
                        .setDeleted(false)
                        .build(),
                user
        );
    }

    @Test
    void testAliasedColumnMapping() {
        ChatUser alias = CHAT_USER.as("aliased_user");

        ChatUserRecord record = new ChatUserRecord();
        record.set(alias.ID, 1);
        record.set(alias.ROLE, "USER");
        record.set(alias.NAME, "Antti Admin");
        record.set(alias.DELETED, true);

        UserRecordMapper<ChatUserRecord> mapper = UserRecordMapper.builder(CHAT_USER)
                .setIdAccessor(CHAT_USER.ID)
                .setRoleAccessor(CHAT_USER.ROLE, Role::toString, Role::parse)
                .setNameAccessor(CHAT_USER.NAME)
                .setDeletedAccessor(CHAT_USER.DELETED)
                .build();

        User user = mapper.alias(alias)
                .map(record);

        assertEquals(
                User.builder()
                        .setId(1)
                        .setRole(Role.USER)
                        .setName("Antti Admin")
                        .setDeleted(true)
                        .build(),
                user
        );
    }
}
