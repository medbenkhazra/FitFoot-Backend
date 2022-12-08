package fit.foot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class JoueurSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("birth_day", table, columnPrefix + "_birth_day"));
        columns.add(Column.aliased("gender", table, columnPrefix + "_gender"));
        columns.add(Column.aliased("avatar", table, columnPrefix + "_avatar"));
        columns.add(Column.aliased("avatar_content_type", table, columnPrefix + "_avatar_content_type"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("quartier_id", table, columnPrefix + "_quartier_id"));
        return columns;
    }
}
