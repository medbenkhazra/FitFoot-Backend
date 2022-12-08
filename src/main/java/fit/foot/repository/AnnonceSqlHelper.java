package fit.foot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AnnonceSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("heure_debut", table, columnPrefix + "_heure_debut"));
        columns.add(Column.aliased("heure_fin", table, columnPrefix + "_heure_fin"));
        columns.add(Column.aliased("duree", table, columnPrefix + "_duree"));
        columns.add(Column.aliased("validation", table, columnPrefix + "_validation"));
        columns.add(Column.aliased("nombre_par_equipe", table, columnPrefix + "_nombre_par_equipe"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("terrain_id", table, columnPrefix + "_terrain_id"));
        columns.add(Column.aliased("responsable_id", table, columnPrefix + "_responsable_id"));
        return columns;
    }
}
