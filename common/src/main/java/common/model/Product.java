package common.model;

import java.io.Serializable;
import java.util.Date;
/**
 * Основной класс, хранимый в коллекции.
 * Валидируется всё, id >0, name не пуст, price >0, manufactureCost не null, manufacturer не null.
 * Сравнение по цене, потом по id.
 */
public class Product implements Validatable, Comparable<Product>, Serializable {
    private static final long serialVersionUID = 1L;
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private String partNumber; //Длина строки должна быть не меньше 10, Длина строки не должна быть больше 56, Значение этого поля должно быть уникальным, Поле может быть null
    private Long manufactureCost; //Поле не может быть null
    private UnitOfMeasure unitOfMeasure; //Поле может быть null
    private Organization manufacturer; //Поле не может быть null

    public Product(String name, Coordinates coordinates, int price, String partNumber, Long manufactureCost, UnitOfMeasure unitOfMeasure, Organization manufacturer) {
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.partNumber = partNumber;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Long getManufactureCost() {
        return manufactureCost;
    }

    public void setManufactureCost(Long manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Organization getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Organization manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public void validate() throws ValidationException {
        if (id <= 0) throw new ValidationException("id > 0");
        if (name == null || name.trim().isEmpty()) throw new ValidationException("name");
        if (coordinates == null) throw new ValidationException("coordinates");
        if (creationDate == null) throw new ValidationException("creationDate");
        if (price <= 0) throw new ValidationException("price > 0");
        if (partNumber != null && (partNumber.length() < 10 || partNumber.length() > 56))
            throw new ValidationException("partNumber length");
        if (manufactureCost == null) throw new ValidationException("manufactureCost null");
        if (manufacturer == null) throw new ValidationException("manufacturer null");

        coordinates.validate();
        manufacturer.validate();
    }

    @Override
    public int compareTo(Product product) {
        int priceComp = Integer.compare(this.price, product.price);
        if (priceComp != 0) return priceComp;
        return Integer.compare(this.id, product.id);
    }


    @Override
    public String toString() {
        Address postalAddress = manufacturer.getPostalAddress();
        String street = (postalAddress == null || postalAddress.getStreet() == null) ? "" : postalAddress.getStreet();
        String zipCode = postalAddress == null || postalAddress.getZipCode() == null ? "" : postalAddress.getZipCode();
        Location town = postalAddress != null ? postalAddress.getTown() : null;
        String townX = (town == null) ? "" : String.valueOf(town.getX());
        String townY = (town == null) ? "" : String.valueOf(town.getY());
        String townZ = (town == null) ? "" : String.valueOf(town.getZ());
        String townName = (town == null || town.getName() == null) ? "" : town.getName();
        return "product id=" + id + ", product name=" + name
                + ", coordinate x=" + coordinates.getX() + ", coordinate y=" + coordinates.getY()
                + ", creation date=" + creationDate + ", price=" + price + ", part number=" + partNumber
                + ", manufacture cost=" + manufactureCost + ", unit of measure=" + unitOfMeasure
                + ", organization id=" + manufacturer.getId() + ", organization name=" + manufacturer.getName()
                + ", annual turnover=" + manufacturer.getAnnualTurnover() + ", organization type=" + manufacturer.getType()
                + ", address street=" + street + ", zip code=" + zipCode
                + ", location x=" + townX + ", location y=" + townY
                + ", location z=" + townZ + ", location name=" + townName;
    }
}
