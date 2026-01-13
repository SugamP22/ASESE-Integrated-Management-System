package db;

import entities.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ProjectRepositoryTest {
    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM projects").executeUpdate();
    }

    @Test
    @DisplayName("Insert new project")
    void insertProject() {
        var project = new Project(
                0,
                "Proyecto de prueba"
        );
        var newProject = ProjectRepository.addProject(project);

        assertNotEquals(0, newProject.getId());
        assertEquals(project.getName(), newProject.getName());
    }
}
