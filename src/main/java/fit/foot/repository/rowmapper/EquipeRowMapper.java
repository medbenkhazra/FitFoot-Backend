package fit.foot.repository.rowmapper;

import fit.foot.domain.Equipe;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Equipe}, with proper type conversions.
 */
@Service
public class EquipeRowMapper implements BiFunction<Row, String, Equipe> {

    private final ColumnConverter converter;

    public EquipeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Equipe} stored in the database.
     */
    @Override
    public Equipe apply(Row row, String prefix) {
        Equipe entity = new Equipe();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAnnonceId(converter.fromRow(row, prefix + "_annonce_id", Long.class));
        return entity;
    }
}
