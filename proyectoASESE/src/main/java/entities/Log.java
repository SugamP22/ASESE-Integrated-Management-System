package entities;

import java.time.LocalDate;
import java.util.Objects;

public class Log {
    private int id;
    private int idUser;
    private LocalDate date;
    private String description;

    public Log(int idUser, String description) {
        this(0, idUser, LocalDate.now(), description);
    }

    public Log(int id, int idUser, LocalDate date, String description) {
        this.id = id;
        this.idUser = idUser;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return idUser;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return id == log.id && idUser == log.idUser && Objects.equals(date, log.date) && Objects.equals(description, log.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, date, description);
    }
}