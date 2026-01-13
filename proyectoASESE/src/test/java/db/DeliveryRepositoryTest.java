package db;

import entities.Delivery;
import entities.Project;
import entities.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DeliveryRepositoryTest {

    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM projects").executeUpdate();
        db.prepareStatement("DELETE FROM stages").executeUpdate();
        db.prepareStatement("DELETE FROM deliveries").executeUpdate();
    }

    @Test
    @DisplayName("Insert new delivery")
    void insertDelivery() {
        var project = new Project(
                0,
                "TestProject"
        );
        var newProject = ProjectRepository.addProject(project);

        var stage = new Stage(
                0,
                newProject.getId(),
                "Construccion",
                "Esta de construccion",
                LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-01-01"),
                0,
                0,
                0
        );
        var newStage = StageRepository.addStage(stage);

        var delivery = new Delivery(
                0,
                "Material de prueba",
                "Descripción de entrega",
                newStage.getId(),
                Delivery.DeliveryTiming.INITIAL
        );

        var newDelivery = DeliveryRepository.addDelivery(delivery);

        assertNotEquals(0, newDelivery.getId());
        assertEquals(delivery.getMaterial(), newDelivery.getMaterial());
        assertEquals(delivery.getDescription(), newDelivery.getDescription());
        assertEquals(delivery.getIdStage(), newDelivery.getIdStage());
        assertEquals(delivery.getTiming(), newDelivery.getTiming());
    }
}