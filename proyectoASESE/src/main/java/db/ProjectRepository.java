package db;

import entities.Project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class ProjectRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Project> getAllProjects() {
        var results = new ArrayList<Project>();

        try (var st = db.prepareStatement("SELECT id, full_name FROM projects");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                results.add(new Project(
                        rs.getInt("id"),
                        rs.getString("full_name")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Project addProject(Project project) {
        try (var st = db.prepareStatement(
                "INSERT INTO projects VALUES (DEFAULT, ?)")
        ) {
            st.setString(1, project.getName());
            st.executeUpdate();

            try (var queryProject = db.prepareStatement(
                    "SELECT * FROM projects WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryProject.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                return new Project(
                        rs.getInt("id"),
                        rs.getString("full_name")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Project> getProjectById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM projects WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Project(
                        rs.getInt("id"),
                        rs.getString("full_name")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Project updateProject(Project project) {
        try (var st = db.prepareStatement(
                "UPDATE projects SET full_name = ? WHERE id = ?")
        ) {
            st.setString(1, project.getName());
            st.setInt(2, project.getId());
            st.executeUpdate();

            try (var queryProject = db.prepareStatement(
                    "SELECT * FROM projects WHERE id = ?")
            ) {
                queryProject.setInt(1, project.getId());
                try (var rs = queryProject.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    return new Project(
                            rs.getInt("id"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteProject(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM projects WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<Project> getProjectsForUser(int userId) {
        var results = new ArrayList<Project>();
        try (var st = db.prepareStatement(
                "SELECT p.id, p.full_name FROM projects p " +
                "INNER JOIN project_access pa ON p.id = pa.id_project " +
                "WHERE pa.id_user = ?")
        ) {
            st.setInt(1, userId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(new Project(
                            rs.getInt("id"),
                            rs.getString("full_name")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return results;
    }
}
