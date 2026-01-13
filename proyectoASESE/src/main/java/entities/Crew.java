package entities;

import java.util.Objects;

public class Crew {
    private int id;
    private String name;
    private String crewType;

    private int idContractor;
    private int idProject;

    public Crew(int id, String name, String crewType, int idContractor, int idProject) {
        this.id = id;
        this.name = name;
        this.crewType = crewType;
        this.idContractor = idContractor;
        this.idProject = idProject;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public int getIdContractor() {
        return idContractor;
    }

    public String getCrewType() {
        return crewType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdContractor(int idContractor) {
        this.idContractor = idContractor;
    }

    public void setCrewType(String crewType) {
        this.crewType = crewType;
    }

    public int getIdProject() {
        return idProject;
    }

    @Override
    public String toString() {
        return "Crew{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", crewType='" + crewType + '\'' +
                ", idContractor=" + idContractor +
                ", idProject=" + idProject +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Crew crew = (Crew) o;
        return id == crew.id && idContractor == crew.idContractor && idProject == crew.idProject && Objects.equals(name, crew.name) && Objects.equals(crewType, crew.crewType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, crewType, idContractor, idProject);
    }
}