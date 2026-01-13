package db;

import entities.Permission;
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

public class PermissionRepositoryTest {
    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM permissions").executeUpdate();
        db.prepareStatement("DELETE FROM stages").executeUpdate();
        db.prepareStatement("DELETE FROM projects").executeUpdate();
    }

    @Test
    @DisplayName("Insert new permission")
    void insertPermission() {
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

        var permission = new Permission(
                0,
                "Site Access",
                "Permission to access construction site",
                newStage.getId()
        );
        var newPermission = PermissionRepository.addPermission(permission);

        assertNotEquals(0, newPermission.getId());
        assertEquals(permission.getIdStage(), newPermission.getIdStage());
        assertEquals(permission.getName(), newPermission.getName());
        assertEquals(permission.getDescription(), newPermission.getDescription());
    }
}


