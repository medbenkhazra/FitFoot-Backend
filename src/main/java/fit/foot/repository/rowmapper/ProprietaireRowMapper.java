package fit.foot.repository.rowmapper;

import fit.foot.domain.Proprietaire;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Proprietaire}, with proper type conversions.
 */
@Service
public class ProprietaireRowMapper implements BiFunction<Row, String, Proprietaire> {

    private final ColumnConverter converter;

    public ProprietaireRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Proprietaire} stored in the database.
     */
    @Override
    public Proprietaire apply(Row row, String prefix) {
        Proprietaire entity = new Proprietaire();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAvatarContentType(converter.fromRow(row, prefix + "_avatar_content_type", String.class));
        entity.setAvatar(converter.fromRow(row, prefix + "_avatar", byte[].class));
        entity.setCin(converter.fromRow(row, prefix + "_cin", String.class));
        entity.setRib(converter.fromRow(row, prefix + "_rib", String.class));
        entity.setNumTel(converter.fromRow(row, prefix + "_num_tel", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
