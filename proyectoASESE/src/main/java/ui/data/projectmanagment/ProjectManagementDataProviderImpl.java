package ui.data.projectmanagment;

/**
 * Project Management: data provider implementation that fetches data from database via repositories.
 */
public final class ProjectManagementDataProviderImpl implements ProjectManagementDataProvider {

    @Override
    public Object[][] getProjects() {
        return ProjectManagementData.getProjects();
    }

    @Override
    public Object[][] getStagesForProject(int projectId) {
        return ProjectManagementData.getStagesForProject(projectId);
    }

    @Override
    public Object[][] getAllStages() {
        return ProjectManagementData.getAllStages();
    }

    @Override
    public Object[][] getDeliveriesForStage(int stageId) {
        return ProjectManagementData.getDeliveriesForStage(stageId);
    }

    @Override
    public Object[][] getPermissionsForStage(int stageId) {
        return ProjectManagementData.getPermissionsForStage(stageId);
    }

    @Override
    public Object[][] getAllDeliveries() {
        return ProjectManagementData.getAllDeliveries();
    }

    @Override
    public Object[][] getAllPermissions() {
        return ProjectManagementData.getAllPermissions();
    }

    @Override
    public Object[][] getUsers() {
        return ProjectManagementData.getUsers();
    }

    @Override
    public Object[][] getWhitelist() {
        return ProjectManagementData.getWhitelist();
    }

    @Override
    public Object[][] getContractors() {
        return ProjectManagementData.getContractors();
    }

    @Override
    public Object[][] getCrew() {
        return ProjectManagementData.getCrew();
    }

    @Override
    public Object[][] getLogs() {
        return ProjectManagementData.getLogs();
    }

    @Override
    public Object[][] getProjectsForUser(int userId) {
        return ProjectManagementData.getProjectsForUser(userId);
    }

    @Override
    public Object[][] getUsersForProject(int projectId) {
        return ProjectManagementData.getUsersForProject(projectId);
    }

    @Override
    public Object[][] getCrewForProject(int projectId) {
        return ProjectManagementData.getCrewForProject(projectId);
    }

    @Override
    public Object[][] getCrewForContractor(int contractorId) {
        return ProjectManagementData.getCrewForContractor(contractorId);
    }

    @Override
    public Object[][] getLogsForUser(int userId) {
        return ProjectManagementData.getLogsForUser(userId);
    }

    @Override
    public Object[][] getStagesForUser(int userId) {
        return ProjectManagementData.getStagesForUser(userId);
    }

    @Override
    public Object[][] getDeliveriesForUser(int userId) {
        return ProjectManagementData.getDeliveriesForUser(userId);
    }

    @Override
    public Object[][] getPermissionsForUser(int userId) {
        return ProjectManagementData.getPermissionsForUser(userId);
    }

    @Override
    public Object[][] getCrewForUser(int userId) {
        return ProjectManagementData.getCrewForUser(userId);
    }

    @Override
    public Object[][] getContractorsForUser(int userId) {
        return ProjectManagementData.getContractorsForUser(userId);
    }
}

