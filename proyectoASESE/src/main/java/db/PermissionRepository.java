package db;

import entities.Permission;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class PermissionRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Permission> getAllPermissions() {
        var results = new ArrayList<Permission>();

        try (var st = db.prepareStatement(
                "SELECT id, id_stage, full_name, descripcion FROM permissions");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                results.add(new Permission(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("descripcion"),
                        rs.getInt("id_stage")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Permission addPermission(Permission permission) {
        try (var st = db.prepareStatement(
                "INSERT INTO permissions VALUES (DEFAULT, ?, ?, ?)")
        ) {
            st.setInt(1, permission.getIdStage());
            st.setString(2, permission.getName());
            st.setString(3, permission.getDescription());
            st.executeUpdate();

            try (var queryPermission = db.prepareStatement(
                    "SELECT * FROM permissions WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryPermission.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                return new Permission(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("descripcion"),
                        rs.getInt("id_stage")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Permission> getPermissionById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM permissions WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Permission(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("descripcion"),
                        rs.getInt("id_stage")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Permission updatePermission(Permission permission) {
        try (var st = db.prepareStatement(
                "UPDATE permissions SET id_stage = ?, full_name = ?, descripcion = ? WHERE id = ?")
        ) {
            st.setInt(1, permission.getIdStage());
            st.setString(2, permission.getName());
            st.setString(3, permission.getDescription());
            st.setInt(4, permission.getId());
            st.executeUpdate();

            try (var queryPermission = db.prepareStatement(
                    "SELECT * FROM permissions WHERE id = ?")
            ) {
                queryPermission.setInt(1, permission.getId());
                try (var rs = queryPermission.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    return new Permission(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("descripcion"),
                            rs.getInt("id_stage")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deletePermission(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM permissions WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<Permission> getPermissionsForStage(int stageId) {
        var results = new ArrayList<Permission>();

        try (var st = db.prepareStatement("SELECT * FROM permissions WHERE id_stage = ?")) {
            st.setInt(1, stageId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(new Permission(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("descripcion"),
                            rs.getInt("id_stage")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }
}


