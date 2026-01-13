package db;

import entities.Log;
import entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LogRepositoryTest {
    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM logs").executeUpdate();
        db.prepareStatement("DELETE FROM users").executeUpdate();
    }

    @Test
    @DisplayName("Insert new log")
    void insertLog() {
        var user = new User(
                0,
                "Pepe",
                "Garcia",
                "pepe.log@pepe.com",
                "1234",
                "1234",
                User.UserRol.ADMIN
        );
        var newUser = UserRepository.addUser(user);

        var log = new Log(
                0,
                newUser.getId(),
                LocalDate.parse("2025-12-01"),
                "User logged in"
        );
        var newLog = LogRepository.addLog(log);

        assertNotEquals(0, newLog.getId());
        assertEquals(log.getIdUser(), newLog.getIdUser());
        assertEquals(log.getDate(), newLog.getDate());
        assertEquals(log.getDescription(), newLog.getDescription());
    }
}


