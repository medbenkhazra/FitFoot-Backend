package fit.foot.repository.rowmapper;

import fit.foot.domain.Complexe;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Complexe}, with proper type conversions.
 */
@Service
public class ComplexeRowMapper implements BiFunction<Row, String, Complexe> {

    private final ColumnConverter converter;

    public ComplexeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Complexe} stored in the database.
     */
    @Override
    public Complexe apply(Row row, String prefix) {
        Complexe entity = new Complexe();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setLongitude(converter.fromRow(row, prefix + "_longitude", Double.class));
        entity.setLatitude(converter.fromRow(row, prefix + "_latitude", Double.class));
        entity.setQuartierId(converter.fromRow(row, prefix + "_quartier_id", Long.class));
        entity.setProprietaireId(converter.fromRow(row, prefix + "_proprietaire_id", Long.class));
        return entity;
    }
}
