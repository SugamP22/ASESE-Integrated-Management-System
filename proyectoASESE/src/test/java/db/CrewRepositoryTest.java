package db;

import entities.Contractor;
import entities.Crew;
import entities.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CrewRepositoryTest {
    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM crew").executeUpdate();
    }

    @Test
    @DisplayName("Insert new crew")
    void insertCrew() {
        var contractor = new Contractor(
                0,
                "TestProject",
                "calle de los lirios"
        );
        var newContractor = ContractorRepository.addContractor(contractor);

        var project = new Project(
                0,
                "TestProject"
        );
        var newProject = ProjectRepository.addProject(project);

        var crew = new Crew(
                123,
                "La banda del peugeot",
                "CONSTRUCCION",
                newContractor.getId(),
                newProject.getId()
        );
        var newCrew = CrewRepository.addCrew(crew);

        assertNotEquals(0, newCrew.getId());
        assertEquals(crew.getName(), newCrew.getName());
        assertEquals(crew.getCrewType(), newCrew.getCrewType());
        assertEquals(crew.getIdContractor(), newCrew.getIdContractor());
        assertEquals(crew.getIdProject(), newCrew.getIdProject());
    }
}
