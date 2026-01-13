package db;

import entities.Crew;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class CrewRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Crew> getAllCrew() {
        var results = new ArrayList<Crew>();

        try (var st = db.prepareStatement(
                "SELECT id, full_name, job_type, id_contractor, id_project FROM crew");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                results.add(new Crew(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("job_type"),
                        rs.getInt("id_contractor"),
                        rs.getInt("id_project")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Crew addCrew(Crew crew) {
        try (var st = db.prepareStatement(
                "INSERT INTO crew VALUES (DEFAULT, ?, ?, ?, ?)")
        ) {
            st.setString(1, crew.getName());
            st.setString(2, crew.getCrewType());
            st.setInt(3, crew.getIdContractor());
            st.setInt(4, crew.getIdProject());
            st.executeUpdate();

            try (var queryCrew = db.prepareStatement(
                    "SELECT * FROM crew WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryCrew.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                return new Crew(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("job_type"),
                        rs.getInt("id_contractor"),
                        rs.getInt("id_project")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Crew> getCrewById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM crew WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Crew(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("job_type"),
                        rs.getInt("id_contractor"),
                        rs.getInt("id_project")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Crew updateCrew(Crew crew) {
        try (var st = db.prepareStatement(
                "UPDATE crew SET full_name = ?, job_type = ?, id_contractor = ?, id_project = ? WHERE id = ?")
        ) {
            st.setString(1, crew.getName());
            st.setString(2, crew.getCrewType());
            st.setInt(3, crew.getIdContractor());
            st.setInt(4, crew.getIdProject());
            st.setInt(5, crew.getId());
            st.executeUpdate();

            try (var queryCrew = db.prepareStatement(
                    "SELECT * FROM crew WHERE id = ?")
            ) {
                queryCrew.setInt(1, crew.getId());
                try (var rs = queryCrew.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    return new Crew(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("job_type"),
                            rs.getInt("id_contractor"),
                            rs.getInt("id_project")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteCrew(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM crew WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<Crew> getCrewForProject(int projectId) {
        var results = new ArrayList<Crew>();

        try (var st = db.prepareStatement("SELECT * FROM crew WHERE id_project = ?")) {
            st.setInt(1, projectId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(new Crew(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("job_type"),
                            rs.getInt("id_contractor"),
                            rs.getInt("id_project")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static ArrayList<Crew> getCrewForContractor(int contractorId) {
        var results = new ArrayList<Crew>();

        try (var st = db.prepareStatement("SELECT * FROM crew WHERE id_contractor = ?")) {
            st.setInt(1, contractorId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(new Crew(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("job_type"),
                            rs.getInt("id_contractor"),
                            rs.getInt("id_project")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }
}