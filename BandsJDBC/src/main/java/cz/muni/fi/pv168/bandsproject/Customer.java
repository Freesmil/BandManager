package cz.muni.fi.pv168.bandsproject;

/**
 * Created by Lenka on 9.3.2016.
 */
public class Customer {
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString(){
        return "Customer: ID="+this.getId()+" name="+this.getName()+" phoneNumber="+this.getPhoneNumber()+" address="+this.getAddress();
    }
}
