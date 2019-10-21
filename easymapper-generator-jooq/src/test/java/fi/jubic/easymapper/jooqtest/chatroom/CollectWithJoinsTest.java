package fi.jubic.easymapper.jooqtest.chatroom;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import fi.jubic.easymapper.jooqtest.DbUtil;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;
import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room.ROOM;
import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership.ROOM_MEMBERSHIP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith({DBUnitExtension.class})
@DBUnit(qualifiedTableNames = true)
public class CollectWithJoinsTest {
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
    void testCollectionWithJoin() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        List<Room> rooms = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .fetchStream()
                .collect(
                        Room.mapper.withAdmin(User.mapper.alias(admin))
                                .withCreatedBy(User.mapper.alias(createdBy))
                );

        assertEquals(2, rooms.size());
        assertIterableEquals(
                Arrays.asList(
                        // region
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
                        Room.builder()
                                .setId(2)
                                .setName("#general")
                                .setAdmin(
                                        User.builder()
                                                .setId(1)
                                                .setRole(Role.SUPERADMIN)
                                                .setName("Antti Admin")
                                                .build()
                                )
                                .setCreatedBy(
                                        User.builder()
                                                .setId(3)
                                                .setRole(Role.USER)
                                                .setName("Matias Messager")
                                                .build()
                                )
                                .build()
                        // endregion
                ),
                rooms
        );
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCollectWithMultipleLevelJoins() {
        fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership
                nestedMembership = ROOM_MEMBERSHIP.as("nested_membership");
        ChatUser member = CHAT_USER.as("member");
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        List<User> users = DSL.using(connection)
                .select()
                .from(CHAT_USER)
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.USER_ID.eq(CHAT_USER.ID))
                .leftJoin(ROOM).on(ROOM.ID.eq(ROOM_MEMBERSHIP.ROOM_ID))
                .leftJoin(nestedMembership).on(nestedMembership.ROOM_ID.eq(ROOM.ID))
                .leftJoin(member).on(member.ID.eq(nestedMembership.USER_ID))
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .fetchStream()
                .collect(
                        User.mapper.collectingManyWithRooms(
                                Room.mapper.withAdmin(User.mapper.alias(admin))
                                        .withCreatedBy(User.mapper.alias(createdBy))
                                        .collectingManyWithMembers(User.mapper.alias(member))
                        )
                );

        User antti = User.builder()
                .setId(1)
                .setRole(Role.SUPERADMIN)
                .setName("Antti Admin")
                .build();
        User unto = User.builder()
                .setId(2)
                .setRole(Role.USER)
                .setName("Unto User")
                .build();
        User matias = User.builder()
                .setId(3)
                .setRole(Role.USER)
                .setName("Matias Messager")
                .build();

        Room random = Room.builder()
                .setId(1)
                .setName("#random")
                .setAdmin(antti)
                .setCreatedBy(unto)
                .setMembers(Arrays.asList(
                        antti.toBuilder().setRooms(Collections.emptyList()).build(),
                        unto.toBuilder().setRooms(Collections.emptyList()).build()
                ))
                .build();
        Room general = Room.builder()
                .setId(2)
                .setName("#general")
                .setAdmin(antti)
                .setCreatedBy(matias)
                .setMembers(Arrays.asList(
                        antti.toBuilder().setRooms(Collections.emptyList()).build(),
                        matias.toBuilder().setRooms(Collections.emptyList()).build()
                ))
                .build();

        assertIterableEquals(
                // region
                Arrays.asList(
                        antti.toBuilder()
                                .setRooms(Arrays.asList(random, general))
                                .build(),
                        unto.toBuilder()
                                .setRooms(Collections.singletonList(random))
                                .build(),
                        matias.toBuilder()
                                .setRooms(Collections.singletonList(general))
                                .build()
                ),
                // endregion
                users
        );
    }
}
