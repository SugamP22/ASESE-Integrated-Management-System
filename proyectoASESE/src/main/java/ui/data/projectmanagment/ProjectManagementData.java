package ui.data.projectmanagment;

import db.*;
import entities.*;

import java.util.*;

/**
 * Project Management: converts entities from repositories to Object[][] for UI.
 */
public final class ProjectManagementData {

    private ProjectManagementData() {
    }

    // Helper methods to convert entities to Object[]
    
    private static Object[] toObjectArray(Project p) {
        return new Object[]{p.getId(), p.getName()};
    }

    private static Object[] toObjectArray(Stage s) {
        return new Object[]{
            s.getId(),
            s.getIdProject(),
            s.getName(),
            s.getDescription(),
            s.getInitialDate() != null ? s.getInitialDate().toString() : null,
            s.getFinalDate() != null ? s.getFinalDate().toString() : null,
            s.getPermissionCount(),
            s.getInitialDeliveryCount(),
            s.getFinalDeliveryCount()
        };
    }

    private static Object[] toObjectArray(Delivery d) {
        return new Object[]{
            d.getId(),
            d.getIdStage(),
            d.getMaterial(),
            d.getDescription(),
            d.getTiming().name()
        };
    }

    private static Object[] toObjectArray(Permission p) {
        return new Object[]{
            p.getId(),
            p.getIdStage(),
            p.getName(),
            p.getDescription()
        };
    }

    private static Object[] toObjectArray(User u) {
        return new Object[]{
            u.getId(),
            u.getName(),
            u.getSurname(),
            u.getEmail(),
            u.getRol().name()
        };
    }

    private static Object[] toObjectArray(Whitelist w) {
        return new Object[]{w.getGmail()};
    }

    private static Object[] toObjectArray(Contractor c) {
        return new Object[]{c.getId(), c.getName(), c.getAddress()};
    }

    private static Object[] toObjectArray(Crew c) {
        return new Object[]{
            c.getId(),
            c.getName(),
            c.getCrewType(),
            c.getIdContractor(),
            c.getIdProject()
        };
    }

    private static Object[][] logsToObjectArrayWithUserEmail(List<Log> logs) {
        // Build a lookup map once to avoid N+1 queries.
        Map<Integer, String> userEmailById = new HashMap<>();
        for (User u : UserRepository.getAllUsers()) {
            userEmailById.put(u.getId(), u.getEmail());
        }

        Object[][] result = new Object[logs.size()][];
        for (int i = 0; i < logs.size(); i++) {
            Log l = logs.get(i);
            String email = userEmailById.getOrDefault(l.getIdUser(), "");
            result[i] = new Object[]{
                    l.getId(),
                    l.getDate() != null ? l.getDate().toString() : null,
                    l.getDescription(),
                    l.getIdUser(),
                    email
            };
        }
        return result;
    }

    // Generic helper to convert List<Entity> to Object[][]
    private static <T> Object[][] toObjectArray(List<T> entities, java.util.function.Function<T, Object[]> converter) {
        Object[][] result = new Object[entities.size()][];
        for (int i = 0; i < entities.size(); i++) {
            result[i] = converter.apply(entities.get(i));
        }
        return result;
    }

    // Public methods

    public static Object[][] getProjects() {
        return toObjectArray(ProjectRepository.getAllProjects(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getAllStages() {
        return toObjectArray(StageRepository.getAllStages(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getStagesForProject(int projectId) {
        return toObjectArray(StageRepository.getStagesForProject(projectId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getAllDeliveries() {
        return toObjectArray(DeliveryRepository.getAllDeliveries(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getDeliveriesForStage(int stageId) {
        return toObjectArray(DeliveryRepository.getDeliveriesForStage(stageId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getAllPermissions() {
        return toObjectArray(PermissionRepository.getAllPermissions(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getPermissionsForStage(int stageId) {
        return toObjectArray(PermissionRepository.getPermissionsForStage(stageId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getUsers() {
        return toObjectArray(UserRepository.getAllUsers(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getWhitelist() {
        return toObjectArray(WhitelistRepository.getWhitelist(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getContractors() {
        return toObjectArray(ContractorRepository.getAllContractors(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getCrew() {
        return toObjectArray(CrewRepository.getAllCrew(), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getLogs() {
        return logsToObjectArrayWithUserEmail(LogRepository.getAllLogs());
    }

    public static Object[][] getProjectsForUser(int userId) {
        return toObjectArray(ProjectRepository.getProjectsForUser(userId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getUsersForProject(int projectId) {
        return toObjectArray(UserRepository.getUsersForProject(projectId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getCrewForProject(int projectId) {
        return toObjectArray(CrewRepository.getCrewForProject(projectId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getCrewForContractor(int contractorId) {
        return toObjectArray(CrewRepository.getCrewForContractor(contractorId), ProjectManagementData::toObjectArray);
    }

    public static Object[][] getLogsForUser(int userId) {
        return logsToObjectArrayWithUserEmail(LogRepository.getLogsForUser(userId));
    }

    /**
     * Get all stages for projects that a user has access to.
     */
    public static Object[][] getStagesForUser(int userId) {
        List<Project> userProjects = ProjectRepository.getProjectsForUser(userId);
        if (userProjects.isEmpty()) {
            return new Object[0][];
        }
        
        List<Stage> allStages = new ArrayList<>();
        for (Project project : userProjects) {
            allStages.addAll(StageRepository.getStagesForProject(project.getId()));
        }
        return toObjectArray(allStages, ProjectManagementData::toObjectArray);
    }

    /**
     * Get all deliveries for stages in projects that a user has access to.
     */
    public static Object[][] getDeliveriesForUser(int userId) {
        List<Project> userProjects = ProjectRepository.getProjectsForUser(userId);
        if (userProjects.isEmpty()) {
            return new Object[0][];
        }
        
        List<Delivery> allDeliveries = new ArrayList<>();
        for (Project project : userProjects) {
            List<Stage> stages = StageRepository.getStagesForProject(project.getId());
            for (Stage stage : stages) {
                allDeliveries.addAll(DeliveryRepository.getDeliveriesForStage(stage.getId()));
            }
        }
        return toObjectArray(allDeliveries, ProjectManagementData::toObjectArray);
    }

    /**
     * Get all permissions for stages in projects that a user has access to.
     */
    public static Object[][] getPermissionsForUser(int userId) {
        List<Project> userProjects = ProjectRepository.getProjectsForUser(userId);
        if (userProjects.isEmpty()) {
            return new Object[0][];
        }
        
        List<Permission> allPermissions = new ArrayList<>();
        for (Project project : userProjects) {
            List<Stage> stages = StageRepository.getStagesForProject(project.getId());
            for (Stage stage : stages) {
                allPermissions.addAll(PermissionRepository.getPermissionsForStage(stage.getId()));
            }
        }
        return toObjectArray(allPermissions, ProjectManagementData::toObjectArray);
    }

    /**
     * Get all crew for projects that a user has access to.
     */
    public static Object[][] getCrewForUser(int userId) {
        List<Project> userProjects = ProjectRepository.getProjectsForUser(userId);
        if (userProjects.isEmpty()) {
            return new Object[0][];
        }
        
        List<Crew> allCrew = new ArrayList<>();
        for (Project project : userProjects) {
            allCrew.addAll(CrewRepository.getCrewForProject(project.getId()));
        }
        return toObjectArray(allCrew, ProjectManagementData::toObjectArray);
    }

    /**
     * Get all contractors that are associated with projects a user has access to (via crew).
     */
    public static Object[][] getContractorsForUser(int userId) {
        List<Project> userProjects = ProjectRepository.getProjectsForUser(userId);
        if (userProjects.isEmpty()) {
            return new Object[0][];
        }
        
        // Collect unique contractor IDs from crews in user's projects
        Set<Integer> contractorIds = new HashSet<>();
        for (Project project : userProjects) {
            List<Crew> crews = CrewRepository.getCrewForProject(project.getId());
            for (Crew crew : crews) {
                contractorIds.add(crew.getIdContractor());
            }
        }
        
        // Get contractors and convert to Object[][]
        List<Contractor> contractors = new ArrayList<>();
        for (Integer contractorId : contractorIds) {
            var contractorOpt = ContractorRepository.getContractorById(contractorId);
            contractorOpt.ifPresent(contractors::add);
        }
        return toObjectArray(contractors, ProjectManagementData::toObjectArray);
    }
}

