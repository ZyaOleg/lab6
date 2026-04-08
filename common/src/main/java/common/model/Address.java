package common.model;

import java.io.Serializable;

/**
 * Адрес, может быть null.
 * Почтовый индекс если не null то длина >=6.
 */
public class Address implements Validatable, Serializable {
    private static final long serialVersionUID = 1L;
    private String street;
    private String zipCode;
    private Location town;

    public Address(String street, String zipCode, Location town) {
        this.street = street;
        this.zipCode = zipCode;
        this.town = town;
    }

    public String getStreet() {return street;}
    public void setStreet(String street) {this.street=street;}
    public String getZipCode() {return zipCode;}
    public void setZipCode(String zipCode) {this.zipCode=zipCode;}
    public Location getTown() {return town;}
    public void setTown(Location town) {this.town=town;}

    @Override
    public void validate() throws ValidationException {
        if (zipCode != null && zipCode.length() < 6) {
            throw new ValidationException("zip code length >= 6");
        }
        if (town != null) {
            town.validate();
        }
    }
}
