package fi.jubic.easymapper.jooqtest.chatroom;

public enum Role {
    USER,
    SUPERADMIN;

    public static Role parse(String role) {
        switch (role.toUpperCase()) {
            case "USER": return USER;
            case "SUPERADMIN": return SUPERADMIN;
            default: return null;
        }
    }
}
