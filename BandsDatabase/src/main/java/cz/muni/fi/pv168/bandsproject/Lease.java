package cz.muni.fi.pv168.bandsproject;

import java.util.Date;

/**
 * Created by Lenka on 9.3.2016.
 */
public class Lease {
    private Long id = null;
    private Customer customer;
    private Band band;
    private Date date;
    private Region place;
    private int duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Region getPlace() {
        return place;
    }

    public void setPlace(Region place) {
        this.place = place;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    @Override
    public String toString() {
        return "ID: "+id+", customer_id: "+customer.getId()+", band_id: "+band.getId()+", date: "+date.toString()+", region: "+place+", duration: "+duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lease lease = (Lease) o;

        if (duration != lease.duration) return false;
        if (id != null ? !id.equals(lease.id) : lease.id != null) return false;
        if (customer != null ? !customer.equals(lease.customer) : lease.customer != null) return false;
        if (band != null ? !band.equals(lease.band) : lease.band != null) return false;
        return place == lease.place;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (band != null ? band.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + duration;
        return result;
    }
}
