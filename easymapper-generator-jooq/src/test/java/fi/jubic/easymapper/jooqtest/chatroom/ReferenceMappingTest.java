package fi.jubic.easymapper.jooqtest.chatroom;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import fi.jubic.easymapper.jooqtest.DbUtil;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;
import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room.ROOM;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DBUnitExtension.class)
@DBUnit(qualifiedTableNames = true)
class ReferenceMappingTest {
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
    @DataSet("chat-sample/datasets/single-room.xml")
    void testJoinedAliasedMapping() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        Record record = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .where(ROOM.ID.eq(1))
                .fetchOne();

        Room room = Room.mapper.withAdmin(User.mapper.alias(admin))
                .withCreatedBy(User.mapper.alias(createdBy))
                .map(record);

        assertEquals(
                Room.builder()
                        .setId(1)
                        .setName("#random")
                        .setAdmin(
                                User.builder()
                                        .setId(1)
                                        .setRole(Role.SUPERADMIN)
                                        .setName("Antti Admin")
                                        .build()
                        )
                        .setCreatedBy(
                                User.builder()
                                        .setId(2)
                                        .setRole(Role.USER)
                                        .setName("Unto User")
                                        .build()
                        )
                        .build(),
                room
        );
    }
}
