package fi.jubic.easymapper.jooqtest.chatroom;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import fi.jubic.easymapper.jooqtest.DbUtil;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.ChatUserRecord;
import fi.jubic.easymapper.jooqtest.chatroom.models.Role;
import fi.jubic.easymapper.jooqtest.chatroom.models.User;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DBUnitExtension.class)
@DBUnit(qualifiedTableNames = true)
public class BasicWriteTest {
    private Connection connection;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private ConnectionHolder connectionHolder;

    @BeforeAll
    static void createDb() throws Exception {
        Connection connection = DbUtil.getConnection();
        DbUtil.setup(connection, "/chat-sample/migrations.sql");
        connection.close();
    }

    @BeforeEach
    void setup() throws Exception {
        connection = DbUtil.getConnection();
        connectionHolder = () -> connection;
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCreateWithoutReferences() {
        DSLContext create = DSL.using(connection);

        User.mapper.write(
                create.newRecord(CHAT_USER),
                User.builder()
                        .setId(0)
                        .setRole(Role.USER)
                        .setName("new user")
                        .build()
        ).store();

        User created = create.select(DSL.asterisk())
                .from(CHAT_USER)
                .where(CHAT_USER.NAME.eq("new user"))
                .fetchOptional()
                .map(User.mapper::map)
                .orElseThrow(RuntimeException::new);

        assertEquals(4, created.getId().intValue());
        assertEquals(Role.USER, created.getRole());
        assertEquals("new user", created.getName());
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testUpdateWithoutReferences() {
        DSLContext create = DSL.using(connection);

        ChatUserRecord record = create.select(DSL.asterisk())
                .from(CHAT_USER)
                .where(CHAT_USER.ID.eq(1))
                .fetchOneInto(CHAT_USER);

        User.mapper.write(
                record,
                User.mapper.map(record)
                        .toBuilder()
                        .setName("new name")
                        .build()
        ).store();

        User updatedUser = create.select(DSL.asterisk())
                .from(CHAT_USER)
                .where(CHAT_USER.ID.eq(1))
                .fetchOptionalInto(CHAT_USER)
                .map(User.mapper::map)
                .orElseThrow(RuntimeException::new);

        assertEquals(1, updatedUser.getId().intValue());
        assertEquals(Role.SUPERADMIN, updatedUser.getRole());
        assertEquals("new name", updatedUser.getName());
    }
}
