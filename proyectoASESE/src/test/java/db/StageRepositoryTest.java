package db;

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

public class StageRepositoryTest {
    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM projects").executeUpdate();
        db.prepareStatement("DELETE FROM stages").executeUpdate();
    }

    @Test
    @DisplayName("Insert new stage")
    void insertStage() {
        var project = new Project(
                0,
                "TestProject"
        );
        var newProject = ProjectRepository.addProject(project);
        var stage = new Stage(
                0,
                newProject.getId(),
                "Etapa de prueba",
                "Descripción de prueba",
                LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-01-01"),
                0,
                0,
                0
        );

        var newStage = StageRepository.addStage(stage);

        assertNotEquals(0, newStage.getId());
        assertEquals(stage.getIdProject(), newStage.getIdProject());
        assertEquals(stage.getName(), newStage.getName());
        assertEquals(stage.getDescription(), newStage.getDescription());
        assertEquals(stage.getInitialDate(), newStage.getInitialDate());
        assertEquals(stage.getFinalDate(), newStage.getFinalDate());
        assertEquals(stage.getPermissionCount(), newStage.getPermissionCount());
        assertEquals(stage.getInitialDeliveryCount(), newStage.getInitialDeliveryCount());
        assertEquals(stage.getFinalDeliveryCount(), newStage.getFinalDeliveryCount());
    }
}