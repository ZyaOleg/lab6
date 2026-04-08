package common.model;

import java.io.Serializable;

/**
 * Координаты, y не больше 557.
 */
public class Coordinates implements Validatable, Serializable {
    private static final long serialVersionUID = 1L;
    private float x;
    private float y;

    public Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {return x;}
    public void setX(float x) {this.x=x;}
    public float getY() {return y;}
    public void setY(float y) {this.y=y;}

    @Override
    public void validate() throws ValidationException{
        if (y > 557) {
            throw new ValidationException("Значение y не должно превышать 557");
        }
    }
}
