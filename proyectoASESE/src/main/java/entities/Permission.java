package entities;

import java.util.Objects;

public class Permission {
    private int id;
    private String name;
    private String description;

    private int idStage;

    public Permission(int id, String name, String description, int idStage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.idStage = idStage;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getIdStage() {
        return idStage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdStage(int idStage) {
        this.idStage = idStage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", idStage=" + idStage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id == that.id && idStage == that.idStage && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, idStage);
    }
}