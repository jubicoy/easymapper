package fi.jubic.easymapper.jooqtest.chatroom;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import fi.jubic.easymapper.jooqtest.DbUtil;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership;
import fi.jubic.easymapper.jooqtest.chatroom.db.tables.records.RoomRecord;
import fi.jubic.easymapper.jooqtest.chatroom.models.Role;
import fi.jubic.easymapper.jooqtest.chatroom.models.Room;
import fi.jubic.easymapper.jooqtest.chatroom.models.RoomRecordMapper;
import fi.jubic.easymapper.jooqtest.chatroom.models.User;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.ChatUser.CHAT_USER;
import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room.ROOM;
import static fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership.ROOM_MEMBERSHIP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void testCollectWithJoinWithoutResults() {
        fi.jubic.easymapper.jooqtest.chatroom.db.tables.RoomMembership
                nestedMembership = ROOM_MEMBERSHIP.as("nested_membership");
        ChatUser member = CHAT_USER.as("member");
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        Optional<User> optionalUser = DSL.using(connection)
                .select()
                .from(CHAT_USER)
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.USER_ID.eq(CHAT_USER.ID))
                .leftJoin(ROOM).on(ROOM.ID.eq(ROOM_MEMBERSHIP.ROOM_ID))
                .leftJoin(nestedMembership).on(nestedMembership.ROOM_ID.eq(ROOM.ID))
                .leftJoin(member).on(member.ID.eq(nestedMembership.USER_ID))
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .where(CHAT_USER.ID.eq(100))
                .fetchStream()
                .collect(
                        User.mapper.collectingWithRooms(
                                Room.mapper.withAdmin(User.mapper.alias(admin))
                                        .withCreatedBy(User.mapper.alias(createdBy))
                                        .collectingManyWithMembers(User.mapper.alias(member))
                        )
                );

        assertFalse(optionalUser.isPresent());
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

        assertEquals(3, rooms.size());
        assertIterableEquals(
                Arrays.asList(
                        random,
                        general,
                        empty
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
                                Room.mapper
                                        .withAdmin(User.mapper.alias(admin))
                                        .withCreatedBy(User.mapper.alias(createdBy))
                                        .collectingManyWithMembers(User.mapper.alias(member))
                        )
                );

        Room randomWithMembers = random.toBuilder()
                .setMembers(Arrays.asList(
                        antti.toBuilder().setRooms(Collections.emptyList()).build(),
                        unto.toBuilder().setRooms(Collections.emptyList()).build()
                ))
                .build();
        Room generalWithMembers = general.toBuilder()
                .setMembers(Arrays.asList(
                        antti.toBuilder().setRooms(Collections.emptyList()).build(),
                        matias.toBuilder().setRooms(Collections.emptyList()).build()
                ))
                .build();

        assertIterableEquals(
                Arrays.asList(
                        antti.toBuilder()
                                .setRooms(Arrays.asList(randomWithMembers, generalWithMembers))
                                .build(),
                        unto.toBuilder()
                                .setRooms(Collections.singletonList(randomWithMembers))
                                .build(),
                        matias.toBuilder()
                                .setRooms(Collections.singletonList(generalWithMembers))
                                .build()
                ),
                users
        );
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCollectWithEmptyCollection() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        List<Room> rooms = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.ROOM_ID.eq(ROOM.ID))
                .leftJoin(CHAT_USER).on(CHAT_USER.ID.eq(ROOM_MEMBERSHIP.USER_ID))
                .fetchStream()
                .collect(
                        Room.mapper.withCreatedBy(User.mapper.alias(createdBy))
                                .withAdmin(User.mapper.alias(admin))
                                .collectingManyWithMembers(User.mapper)
                );

        assertIterableEquals(
                // region
                Arrays.asList(
                        random.toBuilder()
                                .setMembers(Arrays.asList(
                                        antti.toBuilder()
                                                .setRooms(Collections.emptyList())
                                                .build(),
                                        unto.toBuilder()
                                                .setRooms(Collections.emptyList())
                                                .build()
                                ))
                                .build(),
                        general.toBuilder()
                                .setMembers(Arrays.asList(
                                        antti.toBuilder()
                                                .setRooms(Collections.emptyList())
                                                .build(),
                                        matias.toBuilder()
                                                .setRooms(Collections.emptyList())
                                                .build()
                                ))
                                .build(),
                        empty.toBuilder()
                                .setMembers(Collections.emptyList())
                                .build()
                ),
                // endregion
                rooms
        );
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCollectWithReference() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room nestedRoom = ROOM.as("nested_room");
        ChatUser nestedAdmin = CHAT_USER.as("nested_admin");
        ChatUser nestedCreatedBy = CHAT_USER.as("nested_created_by");

        RoomRecordMapper<RoomRecord> nestedRoomMapper = Room.mapper.alias(nestedRoom)
                .withAdmin(User.mapper.alias(nestedAdmin))
                .withCreatedBy(User.mapper.alias(nestedCreatedBy));

        Room room = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.USER_ID.eq(admin.ID))
                .leftJoin(nestedRoom).on(nestedRoom.ID.eq(ROOM_MEMBERSHIP.ROOM_ID))
                .leftJoin(nestedAdmin).on(nestedAdmin.ID.eq(nestedRoom.ADMINISTRATOR_ID))
                .leftJoin(nestedCreatedBy).on(nestedCreatedBy.ID.eq(nestedRoom.CREATED_BY_ID))
                .where(ROOM.ID.eq(1))
                .fetchStream()
                .collect(
                        Room.mapper.withCreatedBy(User.mapper.alias(createdBy))
                                .collectingWithAdmin(
                                        User.mapper.alias(admin)
                                                .collectingWithRooms(nestedRoomMapper)
                                )
                )
                .orElseThrow(IllegalStateException::new);

        assertEquals(
                random.toBuilder()
                        .setAdmin(
                                antti.toBuilder()
                                        .setRooms(
                                                Arrays.asList(
                                                        random,
                                                        general
                                                )
                                        )
                                        .build()
                        )
                        .setMembers(Collections.emptyList())
                        .build(),
                room
        );
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCollectManyWithReference() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room nestedRoom = ROOM.as("nested_room");
        ChatUser nestedAdmin = CHAT_USER.as("nested_admin");
        ChatUser nestedCreatedBy = CHAT_USER.as("nested_created_by");

        RoomRecordMapper<RoomRecord> nestedRoomMapper = Room.mapper.alias(nestedRoom)
                .withAdmin(User.mapper.alias(nestedAdmin))
                .withCreatedBy(User.mapper.alias(nestedCreatedBy));

        List<Room> rooms = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.USER_ID.eq(admin.ID))
                .leftJoin(nestedRoom).on(nestedRoom.ID.eq(ROOM_MEMBERSHIP.ROOM_ID))
                .leftJoin(nestedAdmin).on(nestedAdmin.ID.eq(nestedRoom.ADMINISTRATOR_ID))
                .leftJoin(nestedCreatedBy).on(nestedCreatedBy.ID.eq(nestedRoom.CREATED_BY_ID))
                .fetchStream()
                .collect(
                        Room.mapper.withCreatedBy(User.mapper.alias(createdBy))
                                .collectingManyWithAdmin(
                                        User.mapper.alias(admin)
                                                .collectingWithRooms(nestedRoomMapper)
                                )
                );

        assertEquals(
                Arrays.asList(
                        random.toBuilder()
                                .setAdmin(
                                        antti.toBuilder()
                                                .setRooms(
                                                        Arrays.asList(
                                                                random,
                                                                general
                                                        )
                                                )
                                                .build()
                                )
                                .build(),
                        general,
                        empty.toBuilder()
                                .setAdmin(
                                        unto.toBuilder()
                                                .setRooms(
                                                        Collections.singletonList(random)
                                                )
                                                .build()
                                )
                                .build()
                ),
                rooms
        );
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCollectWithReferenceAndEmptyRelation() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        RoomMembership nestedMembership = ROOM_MEMBERSHIP.as("nested_room_membership");
        ChatUser member = CHAT_USER.as("member");
        fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room nestedRoom = ROOM.as("nested_room");
        ChatUser nestedAdmin = CHAT_USER.as("nested_admin");
        ChatUser nestedCreatedBy = CHAT_USER.as("nested_created_by");

        RoomRecordMapper<RoomRecord> nestedRoomMapper = Room.mapper
                .alias(nestedRoom)
                .withAdmin(User.mapper.alias(nestedAdmin))
                .withCreatedBy(User.mapper.alias(nestedCreatedBy));

        Room room = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.ROOM_ID.eq(ROOM.ID))
                .leftJoin(member).on(member.ID.eq(ROOM_MEMBERSHIP.USER_ID))
                .leftJoin(nestedMembership).on(nestedMembership.USER_ID.eq(member.ID))
                .leftJoin(nestedRoom).on(nestedRoom.ID.eq(nestedMembership.ROOM_ID))
                .leftJoin(nestedAdmin).on(nestedAdmin.ID.eq(nestedRoom.ADMINISTRATOR_ID))
                .leftJoin(nestedCreatedBy).on(nestedCreatedBy.ID.eq(nestedRoom.CREATED_BY_ID))
                .where(ROOM.ID.eq(empty.getId()))
                .fetch()
                .stream()
                .collect(
                        Room.mapper
                                .withAdmin(User.mapper.alias(admin))
                                .withCreatedBy(User.mapper.alias(createdBy))
                                .collectingWithMembers(
                                        User.mapper.alias(member)
                                                .collectingManyWithRooms(nestedRoomMapper)
                                )
                )
                .orElseThrow(IllegalStateException::new);

        assertEquals(
                empty.toBuilder()
                    .setAdmin(unto)
                    .setMembers(Collections.emptyList())
                    .build(),
                room
        );
    }

    @Test
    @DataSet("chat-sample/datasets/multiple-rooms.xml")
    void testCollectWithReferenceAndSubCollection() {
        ChatUser admin = CHAT_USER.as("admin");
        ChatUser createdBy = CHAT_USER.as("created_by");

        fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room nestedRoom = ROOM.as("nested_room");
        ChatUser nestedAdmin = CHAT_USER.as("nested_admin");
        ChatUser nestedCreatedBy = CHAT_USER.as("nested_created_by");

        RoomRecordMapper<RoomRecord> nestedRoomMapper = Room.mapper
                .alias(nestedRoom)
                .withAdmin(User.mapper.alias(nestedAdmin))
                .withCreatedBy(User.mapper.alias(nestedCreatedBy));

        Room room = DSL.using(connection)
                .select()
                .from(ROOM)
                .leftJoin(admin).on(admin.ID.eq(ROOM.ADMINISTRATOR_ID))
                .leftJoin(createdBy).on(createdBy.ID.eq(ROOM.CREATED_BY_ID))
                .leftJoin(ROOM_MEMBERSHIP).on(ROOM_MEMBERSHIP.USER_ID.eq(ROOM.CREATED_BY_ID))
                .leftJoin(nestedRoom).on(nestedRoom.ID.eq(ROOM_MEMBERSHIP.ROOM_ID))
                .leftJoin(nestedAdmin).on(nestedAdmin.ID.eq(nestedRoom.ADMINISTRATOR_ID))
                .leftJoin(nestedCreatedBy).on(nestedCreatedBy.ID.eq(nestedRoom.CREATED_BY_ID))
                .where(ROOM.ID.eq(empty.getId()))
                .fetch()
                .stream()
                .collect(
                        Room.mapper
                                .withAdmin(User.mapper.alias(admin))
                                .collectingWithCreatedBy(
                                        User.mapper.alias(createdBy)
                                                .collectingWithRooms(nestedRoomMapper)
                                )
                )
                .orElseThrow(IllegalStateException::new);

        assertEquals(
                empty.toBuilder()
                        .setCreatedBy(
                                matias.toBuilder()
                                        .setRooms(Collections.singletonList(general))
                                        .build()
                        )
                        .build(),
                room
        );
    }

    private static final User antti = User.builder()
            .setId(1)
            .setRole(Role.SUPERADMIN)
            .setName("Antti Admin")
            .build();
    private static final User unto = User.builder()
            .setId(2)
            .setRole(Role.USER)
            .setName("Unto User")
            .build();
    private static final User matias = User.builder()
            .setId(3)
            .setRole(Role.USER)
            .setName("Matias Messager")
            .build();

    private static final Room random = Room.builder()
            .setId(1)
            .setName("#random")
            .setAdmin(antti)
            .setCreatedBy(unto)
            .build();
    private static final Room general = Room.builder()
            .setId(2)
            .setName("#general")
            .setAdmin(null)
            .setCreatedBy(matias)
            .build();
    private static final Room empty = Room.builder()
            .setId(3)
            .setName("#empty")
            .setAdmin(unto)
            .setCreatedBy(matias)
            .build();
}
