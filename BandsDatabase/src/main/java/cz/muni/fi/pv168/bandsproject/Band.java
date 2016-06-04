package cz.muni.fi.pv168.bandsproject;

import java.util.List;

/**
 * Created by Lenka on 9.3.2016.
 */
public class Band {
    private Long id = null;
    private String bandName;
    private List<Style> styles;
    private Region region;
    private Double pricePerHour;
    private Double rate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }
    
    public void addStyle(Style style) {
        styles.add(style);
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
    
    public String toString() {
        String ret = "id: " + id + ", band name: " + bandName + ", styles: ";
        ret += styles.toString();
        return ret + "region: " + region.toString() + ", price: " + pricePerHour + ", rate: " + rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Band band = (Band) o;

        if (id != null ? !id.equals(band.id) : band.id != null) return false;
        if (bandName != null ? !bandName.equals(band.bandName) : band.bandName != null) return false;
        if (styles != null ? !styles.equals(band.styles) : band.styles != null) return false;
        return region == band.region;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (bandName != null ? bandName.hashCode() : 0);
        result = 31 * result + (styles != null ? styles.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }
}
