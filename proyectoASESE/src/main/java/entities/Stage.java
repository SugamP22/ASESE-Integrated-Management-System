package entities;

import java.time.LocalDate;

public class Stage {
    private int id;
    private int idProject;
    private String name;
    private String description;
    private LocalDate initialDate;
    private LocalDate finalDate;
    private int permissionCount;
    private int initialDeliveryCount;
    private int finalDeliveryCount;

    public Stage(int id, int idProject, String name, String description, LocalDate initialDate, LocalDate finalDate, int permissionCount, int initialDeliveryCount, int finalDeliveryCount) {
        this.id = id;
        this.idProject = idProject;
        this.name = name;
        this.description = description;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.permissionCount = permissionCount;
        this.initialDeliveryCount = initialDeliveryCount;
        this.finalDeliveryCount = finalDeliveryCount;
    }

    public int getId() {
        return id;
    }

    public int getIdProject() {
        return idProject;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public int getPermissionCount() {
        return permissionCount;
    }

    public int getInitialDeliveryCount() {
        return initialDeliveryCount;
    }

    public int getFinalDeliveryCount() {
        return finalDeliveryCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }

    public void setPermissionCount(int permissionCount) {
        this.permissionCount = permissionCount;
    }

    public void setInitialDeliveryCount(int initialDeliveryCount) {
        this.initialDeliveryCount = initialDeliveryCount;
    }

    public void setFinalDeliveryCount(int finalDeliveryCount) {
        this.finalDeliveryCount = finalDeliveryCount;
    }
}
