package fit.foot.repository.rowmapper;

import fit.foot.domain.Joueur;
import fit.foot.domain.enumeration.GENDER;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Joueur}, with proper type conversions.
 */
@Service
public class JoueurRowMapper implements BiFunction<Row, String, Joueur> {

    private final ColumnConverter converter;

    public JoueurRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Joueur} stored in the database.
     */
    @Override
    public Joueur apply(Row row, String prefix) {
        Joueur entity = new Joueur();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBirthDay(converter.fromRow(row, prefix + "_birth_day", LocalDate.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", GENDER.class));
        entity.setAvatarContentType(converter.fromRow(row, prefix + "_avatar_content_type", String.class));
        entity.setAvatar(converter.fromRow(row, prefix + "_avatar", byte[].class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setQuartierId(converter.fromRow(row, prefix + "_quartier_id", Long.class));
        return entity;
    }
}
