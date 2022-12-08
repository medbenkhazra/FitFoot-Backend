package fit.foot.repository.rowmapper;

import fit.foot.domain.Ville;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ville}, with proper type conversions.
 */
@Service
public class VilleRowMapper implements BiFunction<Row, String, Ville> {

    private final ColumnConverter converter;

    public VilleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ville} stored in the database.
     */
    @Override
    public Ville apply(Row row, String prefix) {
        Ville entity = new Ville();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        return entity;
    }
}
