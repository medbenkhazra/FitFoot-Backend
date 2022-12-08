package fit.foot.repository.rowmapper;

import fit.foot.domain.Reservation;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Reservation}, with proper type conversions.
 */
@Service
public class ReservationRowMapper implements BiFunction<Row, String, Reservation> {

    private final ColumnConverter converter;

    public ReservationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Reservation} stored in the database.
     */
    @Override
    public Reservation apply(Row row, String prefix) {
        Reservation entity = new Reservation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        entity.setHeureDebut(converter.fromRow(row, prefix + "_heure_debut", ZonedDateTime.class));
        entity.setHeureFin(converter.fromRow(row, prefix + "_heure_fin", ZonedDateTime.class));
        entity.setTerrainId(converter.fromRow(row, prefix + "_terrain_id", Long.class));
        return entity;
    }
}
