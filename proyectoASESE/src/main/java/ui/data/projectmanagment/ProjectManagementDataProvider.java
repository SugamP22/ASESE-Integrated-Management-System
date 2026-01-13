package ui.data.projectmanagment;

/**
 * Project Management: data source contract used by the UI (demo now, DB later).
 */
public interface ProjectManagementDataProvider {

    Object[][] getProjects();

    Object[][] getStagesForProject(int projectId);

    Object[][] getAllStages();

    Object[][] getDeliveriesForStage(int stageId);

    Object[][] getPermissionsForStage(int stageId);

    Object[][] getAllDeliveries();

    Object[][] getAllPermissions();

    Object[][] getUsers();

    Object[][] getWhitelist();

    Object[][] getContractors();

    Object[][] getCrew();

    Object[][] getLogs();

    Object[][] getProjectsForUser(int userId);

    Object[][] getUsersForProject(int projectId);

    Object[][] getCrewForProject(int projectId);

    Object[][] getCrewForContractor(int contractorId);

    Object[][] getLogsForUser(int userId);

    Object[][] getStagesForUser(int userId);

    Object[][] getDeliveriesForUser(int userId);

    Object[][] getPermissionsForUser(int userId);

    Object[][] getCrewForUser(int userId);

    Object[][] getContractorsForUser(int userId);

    default Integer findProjectIdByStageId(int stageId) {
        Object[][] stages = getAllStages();
        for (Object[] row : stages) {
            if (row.length > 1 && row[0] instanceof Number idNumber) {
                int id = idNumber.intValue();
                if (id == stageId) {
                    Object projectIdValue = row[1];
                    if (projectIdValue instanceof Number projectIdNumber) {
                        return projectIdNumber.intValue();
                    }
                    return null;
                }
            }
        }
        return null;
    }

    default String findProjectNameById(int projectId) {
        Object[][] projects = getProjects();
        for (Object[] row : projects) {
            if (row.length > 1 && row[0] instanceof Number idNumber) {
                int id = idNumber.intValue();
                if (id == projectId) {
                    return row[1] != null ? row[1].toString() : "";
                }
            }
        }
        return "";
    }

    default String findStageNameById(int stageId) {
        Object[][] stages = getAllStages();
        for (Object[] row : stages) {
            if (row.length > 2 && row[0] instanceof Number idNumber) {
                int id = idNumber.intValue();
                if (id == stageId) {
                    return row[2] != null ? row[2].toString() : "";
                }
            }
        }
        return "";
    }

    default String findContractorNameById(int contractorId) {
        Object[][] contractors = getContractors();
        for (Object[] row : contractors) {
            if (row.length > 1 && row[0] instanceof Number idNumber) {
                int id = idNumber.intValue();
                if (id == contractorId) {
                    return row[1] != null ? row[1].toString() : "";
                }
            }
        }
        return "";
    }

    default String[] findUserInfoById(int userId) {
        Object[][] users = getUsers();
        for (Object[] row : users) {
            if (row.length > 2 && row[0] instanceof Number idNumber) {
                int id = idNumber.intValue();
                if (id == userId) {
                    String name = row[1] != null ? row[1].toString() : "";
                    String surname = row[2] != null ? row[2].toString() : "";
                    String fullName = name + (surname.isEmpty() ? "" : " " + surname);
                    return new String[]{fullName};
                }
            }
        }
        return null;
    }
}

