package model.models;

public abstract class ModelInstanceWithCost extends ModelInstance {

    private final Double cost;

    public ModelInstanceWithCost(String date, String id, String cost) {
        super(date, id);
        this.cost = Double.parseDouble(cost);
    }

    public Double getCost() {
        return cost;
    }
}
