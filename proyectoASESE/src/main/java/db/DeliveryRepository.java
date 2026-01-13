package db;

import entities.Delivery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class DeliveryRepository {
    private static final Connection db = Db.getConnection();

    public static ArrayList<Delivery> getAllDeliveries() {
        var results = new ArrayList<Delivery>();

        try (var st = db.prepareStatement(
                "SELECT id, material, descripcion, id_stage, tipo_entrega FROM deliveries");
             var rs = st.executeQuery()) {

            while (rs.next()) {
                results.add(new Delivery(
                        rs.getInt("id"),
                        rs.getString("material"),
                        rs.getString("descripcion"),
                        rs.getInt("id_stage"),
                        Delivery.DeliveryTiming.valueOf(rs.getString("tipo_entrega"))
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }

    public static Delivery addDelivery(Delivery delivery) {
        try (var st = db.prepareStatement(
                "INSERT INTO deliveries VALUES (DEFAULT, ?, ?, ?, ?)")
        ) {
            st.setString(1, delivery.getMaterial());
            st.setString(2, delivery.getDescription());
            st.setInt(3, delivery.getIdStage());
            st.setString(4, delivery.getTiming().toString());
            st.executeUpdate();

            try (var queryDelivery = db.prepareStatement(
                    "SELECT * FROM deliveries WHERE id = LAST_INSERT_ID() LIMIT 1");
                 var rs = queryDelivery.executeQuery()) {

                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }

                return new Delivery(
                        rs.getInt("id"),
                        rs.getString("material"),
                        rs.getString("descripcion"),
                        rs.getInt("id_stage"),
                        Delivery.DeliveryTiming.valueOf(rs.getString("tipo_entrega"))
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Optional<Delivery> getDeliveryById(int id) {
        try (var st = db.prepareStatement(
                "SELECT * FROM deliveries WHERE id = ?")
        ) {
            st.setInt(1, id);

            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Delivery(
                        rs.getInt("id"),
                        rs.getString("material"),
                        rs.getString("descripcion"),
                        rs.getInt("id_stage"),
                        Delivery.DeliveryTiming.valueOf(rs.getString("tipo_entrega"))
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Delivery updateDelivery(Delivery delivery) {
        try (var st = db.prepareStatement(
                "UPDATE deliveries SET material = ?, descripcion = ?, id_stage = ?, tipo_entrega = ? WHERE id = ?")
        ) {
            st.setString(1, delivery.getMaterial());
            st.setString(2, delivery.getDescription());
            st.setInt(3, delivery.getIdStage());
            st.setString(4, delivery.getTiming().toString());
            st.setInt(5, delivery.getId());
            st.executeUpdate();

            try (var queryDelivery = db.prepareStatement(
                    "SELECT * FROM deliveries WHERE id = ?")
            ) {
                queryDelivery.setInt(1, delivery.getId());
                try (var rs = queryDelivery.executeQuery()) {
                    if (!rs.next()) {
                        throw new DatabaseException("No row was returned");
                    }

                    return new Delivery(
                            rs.getInt("id"),
                            rs.getString("material"),
                            rs.getString("descripcion"),
                            rs.getInt("id_stage"),
                            Delivery.DeliveryTiming.valueOf(rs.getString("tipo_entrega"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteDelivery(int id) {
        try (var st = db.prepareStatement(
                "DELETE FROM deliveries WHERE id = ?")
        ) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static ArrayList<Delivery> getDeliveriesForStage(int stageId) {
        var results = new ArrayList<Delivery>();

        try (var st = db.prepareStatement("SELECT * FROM deliveries WHERE id_stage = ?")) {
            st.setInt(1, stageId);
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    results.add(new Delivery(
                            rs.getInt("id"),
                            rs.getString("material"),
                            rs.getString("descripcion"),
                            rs.getInt("id_stage"),
                            Delivery.DeliveryTiming.valueOf(rs.getString("tipo_entrega"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return results;
    }
}
