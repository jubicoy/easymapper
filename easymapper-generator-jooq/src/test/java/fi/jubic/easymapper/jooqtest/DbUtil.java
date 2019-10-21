package fi.jubic.easymapper.jooqtest;

import org.h2.tools.RunScript;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil {
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                "jdbc:h2:file:./target/tmp/test-db",
                "SA",
                ""
        );
    }

    public static void setup(Connection connection, String migrations) throws Exception {
        RunScript.execute(
                connection,
                new InputStreamReader(DbUtil.class.getResourceAsStream(migrations))
        );
    }
}
