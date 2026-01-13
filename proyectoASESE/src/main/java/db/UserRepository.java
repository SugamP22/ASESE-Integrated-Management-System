package db;

import entities.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class UserRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<User> getAllUsers() {
        var results = new ArrayList<User>();

        try (
                var st = db.prepareStatement("SELECT * FROM users");
                var rs = st.executeQuery();
        ) {
            while (rs.next()) {
                results.add(new User(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("email_token"),
                        rs.getString("passw"),
                        User.UserRol.valueOf(rs.getString("rol"))
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static User addUser(User user) {
        try (var st = db.prepareStatement("INSERT INTO users VALUES (DEFAULT, ?, ?, ?, ?, ?, PASSWORD(?))")) {
            st.setString(1, user.getName());
            st.setString(2, user.getSurname());
            st.setString(3, user.getRol().toString());
            st.setString(4, user.getEmail());
            st.setString(5, user.getEmailToken());
            st.setString(6, user.getPassword());
            st.executeUpdate();

            try (
                    var queryUser = db.prepareStatement("SELECT * FROM users WHERE id = LAST_INSERT_ID() LIMIT 1");
                    var rs = queryUser.executeQuery();
            ) {
                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                return new User(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("email_token"),
                        rs.getString("passw"),
                        User.UserRol.valueOf(rs.getString("rol"))
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<User> getUserById(int id) {
        try (var st = db.prepareStatement("SELECT * FROM users WHERE id = ? LIMIT 1")) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("email_token"),
                        rs.getString("passw"),
                        User.UserRol.valueOf(rs.getString("rol"))
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<User> getUserByEmailAndPassword(String email, String password) {
        try (var st = db.prepareStatement("SELECT * FROM users WHERE users.email LIKE BINARY ? AND passw = PASSWORD(?) LIMIT 1")) {
            st.setString(1, email);
            st.setString(2, password);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("email_token"),
                        rs.getString("passw"),
                        User.UserRol.valueOf(rs.getString("rol"))
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<User> getUserByEmail(String email) {
        try (var st = db.prepareStatement("SELECT * FROM users WHERE TRIM(LOWER(users.email)) = TRIM(LOWER(?)) LIMIT 1;")) {
            st.setString(1, email);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("email_token"),
                        rs.getString("passw"),
                        User.UserRol.valueOf(rs.getString("rol"))
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void updateUserPassword(int userId, String passwordValue) {
        try (var st = db.prepareStatement("UPDATE users SET passw = PASSWORD(?) WHERE id = ? LIMIT 1;")) {
            st.setString(1, passwordValue);
            st.setInt(2, userId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static User updateUser(User user) {
        try (var st = db.prepareStatement(
                "UPDATE users SET firstname = ?, surname = ?, rol = ?, email = ?, email_token = ? WHERE id = ?")
        ) {
            st.setString(1, user.getName());
            st.setString(2, user.getSurname());
            st.setString(3, user.getRol().toString());
            st.setString(4, user.getEmail());
            st.setString(5, user.getEmailToken());
            st.setInt(6, user.getId());
            st.executeUpdate();

            try (var queryUser = db.prepareStatement(
                    "SELECT * FROM users WHERE id = ?")
            ) {
                queryUser.setInt(1, user.getId());
                try (var rs = queryUser.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    return new User(
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("email_token"),
                            rs.getString("passw"),
                            User.UserRol.valueOf(rs.getString("rol"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteUser(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM users WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<User> getUsersForProject(int projectId) {
        var results = new ArrayList<User>();

        try (var st = db.prepareStatement("SELECT users.* FROM project_access JOIN users ON project_access.id_user = users.id WHERE project_access.id_project = ?")) {
            st.setInt(1, projectId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(new User(
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("email_token"),
                            rs.getString("passw"),
                            User.UserRol.valueOf(rs.getString("rol"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }
}