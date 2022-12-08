package fit.foot.repository.rowmapper;

import fit.foot.domain.Annonce;
import fit.foot.domain.enumeration.STATUS;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Annonce}, with proper type conversions.
 */
@Service
public class AnnonceRowMapper implements BiFunction<Row, String, Annonce> {

    private final ColumnConverter converter;

    public AnnonceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Annonce} stored in the database.
     */
    @Override
    public Annonce apply(Row row, String prefix) {
        Annonce entity = new Annonce();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setHeureDebut(converter.fromRow(row, prefix + "_heure_debut", ZonedDateTime.class));
        entity.setHeureFin(converter.fromRow(row, prefix + "_heure_fin", ZonedDateTime.class));
        entity.setDuree(converter.fromRow(row, prefix + "_duree", Integer.class));
        entity.setValidation(converter.fromRow(row, prefix + "_validation", Boolean.class));
        entity.setNombreParEquipe(converter.fromRow(row, prefix + "_nombre_par_equipe", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", STATUS.class));
        entity.setTerrainId(converter.fromRow(row, prefix + "_terrain_id", Long.class));
        entity.setResponsableId(converter.fromRow(row, prefix + "_responsable_id", Long.class));
        return entity;
    }
}
