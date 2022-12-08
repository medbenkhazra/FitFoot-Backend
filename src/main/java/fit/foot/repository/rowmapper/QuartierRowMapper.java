package fit.foot.repository.rowmapper;

import fit.foot.domain.Quartier;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Quartier}, with proper type conversions.
 */
@Service
public class QuartierRowMapper implements BiFunction<Row, String, Quartier> {

    private final ColumnConverter converter;

    public QuartierRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Quartier} stored in the database.
     */
    @Override
    public Quartier apply(Row row, String prefix) {
        Quartier entity = new Quartier();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setVilleId(converter.fromRow(row, prefix + "_ville_id", Long.class));
        return entity;
    }
}
