package db;

import entities.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;

public class StageRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Stage> getAllStages() {
        var results = new ArrayList<Stage>();

        try (var st = db.prepareStatement(
                "SELECT id, id_project, full_name, descripcion, initial_date, final_date, permissionCount, initial_delivery_count, final_delivery_count FROM stages");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                Date initialSqlDate = rs.getDate("initial_date");
                Date finalSqlDate = rs.getDate("final_date");
                results.add(new Stage(
                        rs.getInt("id"),
                        rs.getInt("id_project"),
                        rs.getString("full_name"),
                        rs.getString("descripcion"),
                        initialSqlDate != null ? initialSqlDate.toLocalDate() : null,
                        finalSqlDate != null ? finalSqlDate.toLocalDate() : null,
                        rs.getInt("permissionCount"),
                        rs.getInt("initial_delivery_count"),
                        rs.getInt("final_delivery_count")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Stage addStage(Stage stage) {
        try (var st = db.prepareStatement(
                "INSERT INTO stages VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)")
        ) {
            st.setInt(1, stage.getIdProject());
            st.setString(2, stage.getName());
            st.setString(3, stage.getDescription());
            if (stage.getInitialDate() != null) {
                st.setDate(4, Date.valueOf(stage.getInitialDate()));
            } else {
                st.setNull(4, Types.DATE);
            }
            if (stage.getFinalDate() != null) {
                st.setDate(5, Date.valueOf(stage.getFinalDate()));
            } else {
                st.setNull(5, Types.DATE);
            }
            st.setInt(6, stage.getPermissionCount());
            st.setInt(7, stage.getInitialDeliveryCount());
            st.setInt(8, stage.getFinalDeliveryCount());
            st.executeUpdate();

            try (var queryStage = db.prepareStatement(
                    "SELECT * FROM stages WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryStage.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                Date initialSqlDate = rs.getDate("initial_date");
                Date finalSqlDate = rs.getDate("final_date");
                return new Stage(
                        rs.getInt("id"),
                        rs.getInt("id_project"),
                        rs.getString("full_name"),
                        rs.getString("descripcion"),
                        initialSqlDate != null ? initialSqlDate.toLocalDate() : null,
                        finalSqlDate != null ? finalSqlDate.toLocalDate() : null,
                        rs.getInt("permissionCount"),
                        rs.getInt("initial_delivery_count"),
                        rs.getInt("final_delivery_count")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Stage> getStageById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM stages WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Date initialSqlDate = rs.getDate("initial_date");
                Date finalSqlDate = rs.getDate("final_date");
                return Optional.of(new Stage(
                        rs.getInt("id"),
                        rs.getInt("id_project"),
                        rs.getString("full_name"),
                        rs.getString("descripcion"),
                        initialSqlDate != null ? initialSqlDate.toLocalDate() : null,
                        finalSqlDate != null ? finalSqlDate.toLocalDate() : null,
                        rs.getInt("permissionCount"),
                        rs.getInt("initial_delivery_count"),
                        rs.getInt("final_delivery_count")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Stage updateStage(Stage stage) {
        try (var st = db.prepareStatement(
                "UPDATE stages SET id_project = ?, full_name = ?, descripcion = ?, " +
                "initial_date = ?, final_date = ?, permissionCount = ?, " +
                "initial_delivery_count = ?, final_delivery_count = ? WHERE id = ?")
        ) {
            st.setInt(1, stage.getIdProject());
            st.setString(2, stage.getName());
            st.setString(3, stage.getDescription());
            if (stage.getInitialDate() != null) {
                st.setDate(4, Date.valueOf(stage.getInitialDate()));
            } else {
                st.setNull(4, Types.DATE);
            }
            if (stage.getFinalDate() != null) {
                st.setDate(5, Date.valueOf(stage.getFinalDate()));
            } else {
                st.setNull(5, Types.DATE);
            }
            st.setInt(6, stage.getPermissionCount());
            st.setInt(7, stage.getInitialDeliveryCount());
            st.setInt(8, stage.getFinalDeliveryCount());
            st.setInt(9, stage.getId());
            st.executeUpdate();

            try (var queryStage = db.prepareStatement(
                    "SELECT * FROM stages WHERE id = ?")
            ) {
                queryStage.setInt(1, stage.getId());
                try (var rs = queryStage.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    Date initialSqlDate = rs.getDate("initial_date");
                    Date finalSqlDate = rs.getDate("final_date");
                    return new Stage(
                            rs.getInt("id"),
                            rs.getInt("id_project"),
                            rs.getString("full_name"),
                            rs.getString("descripcion"),
                            initialSqlDate != null ? initialSqlDate.toLocalDate() : null,
                            finalSqlDate != null ? finalSqlDate.toLocalDate() : null,
                            rs.getInt("permissionCount"),
                            rs.getInt("initial_delivery_count"),
                            rs.getInt("final_delivery_count")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteStage(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM stages WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<Stage> getStagesForProject(int projectId) {
        var results = new ArrayList<Stage>();

        try (var st = db.prepareStatement("SELECT * FROM stages WHERE id_project = ?")) {
            st.setInt(1, projectId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    Date initialSqlDate = rs.getDate("initial_date");
                    Date finalSqlDate = rs.getDate("final_date");
                    results.add(new Stage(
                            rs.getInt("id"),
                            rs.getInt("id_project"),
                            rs.getString("full_name"),
                            rs.getString("descripcion"),
                            initialSqlDate != null ? initialSqlDate.toLocalDate() : null,
                            finalSqlDate != null ? finalSqlDate.toLocalDate() : null,
                            rs.getInt("permissionCount"),
                            rs.getInt("initial_delivery_count"),
                            rs.getInt("final_delivery_count")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }
}