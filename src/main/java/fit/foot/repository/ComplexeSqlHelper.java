package fit.foot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ComplexeSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nom", table, columnPrefix + "_nom"));
        columns.add(Column.aliased("longitude", table, columnPrefix + "_longitude"));
        columns.add(Column.aliased("latitude", table, columnPrefix + "_latitude"));

        columns.add(Column.aliased("quartier_id", table, columnPrefix + "_quartier_id"));
        columns.add(Column.aliased("proprietaire_id", table, columnPrefix + "_proprietaire_id"));
        return columns;
    }
}
