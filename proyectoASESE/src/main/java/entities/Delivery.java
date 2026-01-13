package entities;

public class Delivery {
    private int id;
    private String material;
    private String description;
    private int idStage;
    private DeliveryTiming timing;

    public enum DeliveryTiming {
        INITIAL,
        FINAL,
    }

    public Delivery(int id, String material, String description, int idStage, DeliveryTiming timing) {
        this.id = id;
        this.material = material;
        this.description = description;
        this.idStage = idStage;
        this.timing = timing;
    }

    public int getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }

    public int getIdStage() {
        return idStage;
    }

    public DeliveryTiming getTiming() {
        return timing;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdStage(int idStage) {
        this.idStage = idStage;
    }

    public void setTiming(DeliveryTiming timing) {
        this.timing = timing;
    }
}