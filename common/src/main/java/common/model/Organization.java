package common.model;

import java.io.Serializable;

/**
 * Организация, id генерируется, name не null, annualTurnover >0.
 */
public class Organization implements Validatable, Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private long annualTurnover;
    private OrganizationType type;
    private Address postalAddress;

    public Organization(String name, long annualTurnover, OrganizationType type, Address postalAddress) {
        this.name = name;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.postalAddress = postalAddress;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public long getAnnualTurnover() {return annualTurnover;}
    public void setAnnualTurnover(long annualTurnover) {this.annualTurnover = annualTurnover;}

    public OrganizationType getType() {return type;}
    public void setType(OrganizationType type) { this.type = type;}

    public Address getPostalAddress() {return postalAddress;}
    public void setPostalAddress(Address postalAddress) {this.postalAddress = postalAddress;}


    @Override
    public void validate() throws ValidationException {
        if (id <= 0) throw new ValidationException("organization id > 0");
        if (name == null || name.trim().isEmpty()) throw new ValidationException("organization name");
        if (annualTurnover <= 0) throw new ValidationException("annualTurnover > 0");
        if (postalAddress != null) {
            postalAddress.validate();
        }
    }
}
