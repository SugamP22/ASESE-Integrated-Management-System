package db;

import entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserRepositoryTest {
    static Connection db;

    @BeforeAll
    static void setup() throws SQLException {
        db = Db.getConnection();
        db.prepareStatement("DELETE FROM users").executeUpdate();
    }

    @Test
    @DisplayName("Insert new user and check password")
    void insertUserAndCheckPassword() throws SQLException {
        String nonHashedPassword = "1234";
        String hashedPassword;

        var st = db.prepareStatement("SELECT PASSWORD(?) AS hashed_password");
        st.setString(1, nonHashedPassword);
        var rs = st.executeQuery();
        rs.next();
        hashedPassword = rs.getString("hashed_password");

        var user = new User(
                0,
                "Pepe",
                "Garcia",
                "pepe@pepe.com",
                "1234",
                "1234",
                User.UserRol.ADMIN
        );
        var newUser = UserRepository.addUser(user);

        assertNotEquals(0, newUser.getId());
        assertEquals(user.getName(), newUser.getName());
        assertEquals(user.getSurname(), newUser.getSurname());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getEmailToken(), newUser.getEmailToken());
        assertEquals(hashedPassword, newUser.getPassword());

        newUser = UserRepository.getUserByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertNotEquals(0, newUser.getId());
        assertEquals(user.getName(), newUser.getName());
        assertEquals(user.getSurname(), newUser.getSurname());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getEmailToken(), newUser.getEmailToken());
        assertEquals(hashedPassword, newUser.getPassword());
    }
}
