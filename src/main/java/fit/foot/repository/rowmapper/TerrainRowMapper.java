package fit.foot.repository.rowmapper;

import fit.foot.domain.Terrain;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Terrain}, with proper type conversions.
 */
@Service
public class TerrainRowMapper implements BiFunction<Row, String, Terrain> {

    private final ColumnConverter converter;

    public TerrainRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Terrain} stored in the database.
     */
    @Override
    public Terrain apply(Row row, String prefix) {
        Terrain entity = new Terrain();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setCapaciteParEquipe(converter.fromRow(row, prefix + "_capacite_par_equipe", Integer.class));
        entity.setComplexeId(converter.fromRow(row, prefix + "_complexe_id", Long.class));
        return entity;
    }
}
