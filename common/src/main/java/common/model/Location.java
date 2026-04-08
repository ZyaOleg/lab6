package common.model;

import java.io.Serializable;

/**
 * name может быть null.
 */
public class Location implements  Validatable, Serializable {
    private static final long serialVersionUID = 1L;
    private double x;
    private float y;
    private float z;
    private String name;
    public Location(double x, float y, float z, String name){
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void validate() throws ValidationException {
    }
}
