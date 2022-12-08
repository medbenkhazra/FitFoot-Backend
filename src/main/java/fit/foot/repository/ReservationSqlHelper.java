package fit.foot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReservationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));
        columns.add(Column.aliased("heure_debut", table, columnPrefix + "_heure_debut"));
        columns.add(Column.aliased("heure_fin", table, columnPrefix + "_heure_fin"));

        columns.add(Column.aliased("terrain_id", table, columnPrefix + "_terrain_id"));
        return columns;
    }
}
