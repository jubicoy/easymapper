/*
 * This file is generated by jOOQ.
 */
package fi.jubic.easymapper.jooqtest.chatroom.db.tables.records;


import fi.jubic.easymapper.jooqtest.chatroom.db.tables.Room;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RoomRecord extends UpdatableRecordImpl<RoomRecord> implements Record4<Integer, String, Integer, Integer> {

    private static final long serialVersionUID = -687565871;

    /**
     * Setter for <code>CHAT_SAMPLE.ROOM.ID</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>CHAT_SAMPLE.ROOM.ID</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>CHAT_SAMPLE.ROOM.NAME</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>CHAT_SAMPLE.ROOM.NAME</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>CHAT_SAMPLE.ROOM.ADMINISTRATOR_ID</code>.
     */
    public void setAdministratorId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>CHAT_SAMPLE.ROOM.ADMINISTRATOR_ID</code>.
     */
    public Integer getAdministratorId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>CHAT_SAMPLE.ROOM.CREATED_BY_ID</code>.
     */
    public void setCreatedById(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>CHAT_SAMPLE.ROOM.CREATED_BY_ID</code>.
     */
    public Integer getCreatedById() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Integer, String, Integer, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Integer, String, Integer, Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Room.ROOM.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Room.ROOM.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Room.ROOM.ADMINISTRATOR_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Room.ROOM.CREATED_BY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getAdministratorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getCreatedById();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getAdministratorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getCreatedById();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomRecord value3(Integer value) {
        setAdministratorId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomRecord value4(Integer value) {
        setCreatedById(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomRecord values(Integer value1, String value2, Integer value3, Integer value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RoomRecord
     */
    public RoomRecord() {
        super(Room.ROOM);
    }

    /**
     * Create a detached, initialised RoomRecord
     */
    public RoomRecord(Integer id, String name, Integer administratorId, Integer createdById) {
        super(Room.ROOM);

        set(0, id);
        set(1, name);
        set(2, administratorId);
        set(3, createdById);
    }
}
