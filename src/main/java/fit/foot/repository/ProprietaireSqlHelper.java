package fit.foot.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProprietaireSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("avatar", table, columnPrefix + "_avatar"));
        columns.add(Column.aliased("avatar_content_type", table, columnPrefix + "_avatar_content_type"));
        columns.add(Column.aliased("cin", table, columnPrefix + "_cin"));
        columns.add(Column.aliased("rib", table, columnPrefix + "_rib"));
        columns.add(Column.aliased("num_tel", table, columnPrefix + "_num_tel"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
