package cz.muni.fi.pv168.bandsproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Created by Lenka on 9.3.2016.
 */
public class BandManagerImpl implements BandManager{
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    
    public BandManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public void createBand(Band band) throws ServiceFailureException { 
        validate(band);
        if (band.getId() != null) {
            throw new IllegalArgumentException("band id is already set");
        }
        SimpleJdbcInsert insertBand = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("band").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("name", band.getName());
        parameters.put("region", band.getRegion().ordinal());
        parameters.put("pricePerHour", band.getPricePerHour());
        parameters.put("rate", band.getRate());
        Number id = insertBand.executeAndReturnKey(parameters);
        band.setId(id.longValue());
        
        createStylesBand(band.getId(), band.getStyles());
    }
    
    @Override
    public void updateBand(Band band) throws ServiceFailureException {
        validate(band);
        if(band.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        String SQL = "UPDATE band SET name = ?,region = ?,pricePerHour = ?,rate = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL,band.getName(),band.getRegion().ordinal(),band.getPricePerHour(),band.getRate(),band.getId());
        updateStylesBand(band.getId(), band.getStyles());
    }

    @Override
    public void deleteBand(Band band) throws ServiceFailureException {
        if (band == null) {
            throw new IllegalArgumentException("band is null");
        }
        if (band.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        deleteStylesBand(band.getId());
        jdbcTemplateObject.update("DELETE FROM band WHERE id = ?", band.getId());
    }
    
    @Override
    public void createStylesBand(Long id, List<Style> styles) throws ServiceFailureException {
        for(Style style : styles){
            SimpleJdbcInsert insertStyles = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("band_styles").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>(2);
            parameters.put("idBand", id);
            parameters.put("style", style.ordinal());
            insertStyles.executeAndReturnKey(parameters);
        }
    }
    
    @Override
    public void updateStylesBand(Long id, List<Style> styles) throws ServiceFailureException {
        deleteStylesBand(id);
        for(Style style : styles){
            SimpleJdbcInsert insertStyles = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("band_styles").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>(2);
            parameters.put("idBand", id);
            parameters.put("style", style.ordinal());
            insertStyles.executeAndReturnKey(parameters);
        }
    }
    
    @Override
    public void deleteStylesBand(Long id) throws ServiceFailureException {
        if (id == null) {
            throw new IllegalArgumentException("band is null");
        }
        
        jdbcTemplateObject.update("DELETE FROM band_styles WHERE idBand = ?", id);
    }
    
    @Override
    public List<Style> getStylesBand(Long id) throws ServiceFailureException {
        List<Style> styles = jdbcTemplateObject.query("SELECT style FROM band_styles WHERE idBand = ?", (ResultSet rs, int rowNum) -> Style.values()[rs.getInt("style")], id);

        return styles;
    }

    @Override
    public List<Band> getAllBands() {
        try {
            List<Band> bands = jdbcTemplateObject.query("SELECT * FROM band", bandMapper);
            for(Band b: bands) {
                b.setStyles(getStylesBand(b.getId()));
            }
            return bands;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Band findBandById(Long id) throws ServiceFailureException {
        try {
            Band band = jdbcTemplateObject.queryForObject("SELECT * FROM band WHERE id = ?", bandMapper, id);
            band.setStyles(getStylesBand(id));
            return band;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Band> findBandByName(String name) throws ServiceFailureException {
        try {
            List<Band> bands = jdbcTemplateObject.query("SELECT * FROM band WHERE name= ?", bandMapper, name);
            for(Band b: bands) {
                b.setStyles(getStylesBand(b.getId()));
            }
            return bands;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Band> findBandByStyles(List<Style> styles) {
        List<Band> bands = new ArrayList<>();
        try {
            for(Style s : styles) {
                bands = jdbcTemplateObject.query("SELECT * FROM band LEFT JOIN band_styles " +
                        "ON band.id = band_styles.idBand and band_styles.style = ?", bandMapper, s.ordinal());
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return bands;
    }

    @Override
    public List<Band> findBandByRegion(List<Region> regions) {
        List<Band> bands = new ArrayList<>();
        try {
            String regionsString = "";
            for(Region r: regions) {
                regionsString += r.ordinal()+",";
            }
            bands = jdbcTemplateObject.query("SELECT * FROM BAND WHERE region IN("+regionsString.substring(0, regionsString.length()-1)+")", bandMapper);
            for(Band b: bands) {
                b.setStyles(getStylesBand(b.getId()));
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return bands;
    }

    @Override
    public List<Band> findBandByPriceRange(Double from, Double to) throws ServiceFailureException {
        List<Band> bands = new ArrayList<>();
        try {
            bands = jdbcTemplateObject.query("SELECT * FROM band "
                    + "WHERE pricePerHour >= ? AND pricePerHour <= ?", bandMapper, from, to);
            for(Band b: bands) {
                b.setStyles(getStylesBand(b.getId()));
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return bands;
    }

    @Override
    public List<Band> findBandByRate(Double from) throws ServiceFailureException {
        List<Band> bands = new ArrayList<>();
        try {
            bands = jdbcTemplateObject.query("SELECT * FROM band " +
                    "WHERE rate >= ?", bandMapper, from);
            for(Band b: bands) {
                b.setStyles(getStylesBand(b.getId()));
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return bands;
    }

    /**
     *
     * @param band
     * @throws IllegalArgumentException
     */
    private void validate(Band band) throws IllegalArgumentException {
        if (band == null) {
            throw new IllegalArgumentException("band is null");
        }
        if (band.getName() == null) {
            throw new IllegalArgumentException("band name is null");
        }
        if (band.getStyles() == null) {
            throw new IllegalArgumentException("band styles is null");
        }
        if (band.getRegion() == null) {
            throw new IllegalArgumentException("band region is null");
        }
        if (band.getPricePerHour() < 0) {
            throw new IllegalArgumentException("band price per hour is negative");
        }
        if (band.getRate() < 0) {
            throw new IllegalArgumentException("band rate is negative");
        }
    }

    private RowMapper<Band> bandMapper = new RowMapper<Band>() {
        @Override
        public Band mapRow(ResultSet rs, int rowNum) throws SQLException {
            Band band = new Band();
            band.setId(rs.getLong("id"));
            band.setBandName(rs.getString("name"));
            band.setRegion(Region.values()[rs.getInt("region")]);
            band.setPricePerHour(rs.getDouble("pricePerHour"));
            band.setRate(rs.getDouble("rate"));
            return band;
        }
    };
}

