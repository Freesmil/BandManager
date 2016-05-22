package cz.muni.fi.pv168.bandsproject;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Lenka
 */
public interface BandManager {
   
    public void createBand(Band band);
    
    public void updateBand(Band band);
    
    public void deleteBand(Band band);
    
    public Collection<Band> getAllBands();
    
    public Band findBandById(Long id);
    
    public Collection<Band> findBandByName(String name);
    
    public Collection<Band> findBandByStyles(List<Style> styles);
    
    public Collection<Band> findBandByRegion(List<Region> regions);
    
    public Collection<Band> findBandByPriceRange(Double from, Double to);
    
    public Collection<Band> findBandByRate(Double from);
}
