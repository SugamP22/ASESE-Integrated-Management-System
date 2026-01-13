package db;

import entities.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;

public class LogRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Log> getAllLogs() {
        var results = new ArrayList<Log>();

        try (var st = db.prepareStatement(
                "SELECT id, date_log, description, id_user FROM logs");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("date_log");
                results.add(new Log(
                        rs.getInt("id"),
                        rs.getInt("id_user"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Log addLog(Log log) {
        try (var st = db.prepareStatement(
                "INSERT INTO logs VALUES (DEFAULT, ?, ?, ?)")
        ) {
            if (log.getDate() != null) {
                st.setDate(1, Date.valueOf(log.getDate()));
            } else {
                st.setNull(1, Types.DATE);
            }
            st.setString(2, log.getDescription());
            st.setInt(3, log.getIdUser());
            st.executeUpdate();

            try (var queryLog = db.prepareStatement(
                    "SELECT * FROM logs WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryLog.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                Date sqlDate = rs.getDate("date_log");
                return new Log(
                        rs.getInt("id"),
                        rs.getInt("id_user"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Log> getLogById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM logs WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Date sqlDate = rs.getDate("date_log");
                return Optional.of(new Log(
                        rs.getInt("id"),
                        rs.getInt("id_user"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Log updateLog(Log log) {
        try (var st = db.prepareStatement(
                "UPDATE logs SET date_log = ?, description = ?, id_user = ? WHERE id = ?")
        ) {
            if (log.getDate() != null) {
                st.setDate(1, Date.valueOf(log.getDate()));
            } else {
                st.setNull(1, Types.DATE);
            }
            st.setString(2, log.getDescription());
            st.setInt(3, log.getIdUser());
            st.setInt(4, log.getId());
            st.executeUpdate();

            try (var queryLog = db.prepareStatement(
                    "SELECT * FROM logs WHERE id = ?")
            ) {
                queryLog.setInt(1, log.getId());
                try (var rs = queryLog.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    Date sqlDate = rs.getDate("date_log");
                    return new Log(
                            rs.getInt("id"),
                            rs.getInt("id_user"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteLog(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM logs WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<Log> getLogsForUser(int userId) {
        var results = new ArrayList<Log>();

        try (var st = db.prepareStatement("SELECT id, date_log, description, id_user FROM logs WHERE id_user = ?")) {
            st.setInt(1, userId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    var sqlDate = rs.getDate("date_log");
                    results.add(new Log(
                            rs.getInt("id"),
                            rs.getInt("id_user"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }
}


