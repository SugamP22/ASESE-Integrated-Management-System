package db;

import entities.Contractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class ContractorRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Contractor> getAllContractors() {
        var results = new ArrayList<Contractor>();

        try (var st = db.prepareStatement("SELECT id, full_name, addres FROM contractors");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                results.add(new Contractor(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("addres")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Contractor addContractor(Contractor contractor) {
        try (var st = db.prepareStatement(
                "INSERT INTO contractors VALUES (DEFAULT, ?, ?)")
        ) {
            st.setString(1, contractor.getName());
            st.setString(2, contractor.getAddress());
            st.executeUpdate();

            try (var queryContractor = db.prepareStatement(
                    "SELECT * FROM contractors WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryContractor.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                return new Contractor(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("addres")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Contractor> getContractorById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM contractors WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Contractor(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("addres")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Contractor updateContractor(Contractor contractor) {
        try (var st = db.prepareStatement(
                "UPDATE contractors SET full_name = ?, addres = ? WHERE id = ?")
        ) {
            st.setString(1, contractor.getName());
            st.setString(2, contractor.getAddress());
            st.setInt(3, contractor.getId());
            st.executeUpdate();

            try (var queryContractor = db.prepareStatement(
                    "SELECT * FROM contractors WHERE id = ?")
            ) {
                queryContractor.setInt(1, contractor.getId());
                try (var rs = queryContractor.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    return new Contractor(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("addres")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteContractor(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM contractors WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
