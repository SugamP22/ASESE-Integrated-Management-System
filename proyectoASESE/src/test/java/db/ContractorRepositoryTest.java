package db;

import entities.Contractor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContractorRepositoryTest {

    @BeforeAll
    static void setup() throws SQLException {
        Connection db = Db.getConnection();
        db.prepareStatement("DELETE FROM contractors").executeUpdate();
    }

    @Test
    @DisplayName("Insert new contractor")
    void insertContractor() {
        var contractor = new Contractor(
                0,
                "Contratista de prueba",
                "Calle Falsa 123"
        );

        var newContractor = ContractorRepository.addContractor(contractor);

        assertNotEquals(0, newContractor.getId());
        assertEquals(contractor.getName(), newContractor.getName());
        assertEquals(contractor.getAddress(), newContractor.getAddress());
    }
}
