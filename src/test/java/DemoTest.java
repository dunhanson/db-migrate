import com.google.gson.Gson;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;
import site.dunhanson.db.migrate.baisc.Basic;
import site.dunhanson.db.migrate.baisc.DbType;
import site.dunhanson.db.migrate.utils.DbUtils;
import site.dunhanson.db.migrate.utils.MysqlUtils;
import site.dunhanson.db.migrate.utils.OracleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoTest {

    @Test
    public void test() {
        String tableName = "BXKC.T_REGISTRANT";
        List<String> condition = new ArrayList<>();
        condition.add("CREATE_TIME>'2020-01-21 10:00:00'");
        //condition.add("CREATE_TIME<'2020-01-21 10:00:00'");
        Basic basic = Basic.builder()
                .tableName(tableName)
                .condition(condition)
                .build();
        OracleUtils.migrateToMySQL(basic);
    }

    @Test
    public void test2() {
        QueryRunner runner = DbUtils.getQueryRunner(MysqlUtils.dataSource);
        DbType type = DbType.MYSQL;
        Map<String, Object> map = DbUtils.getLastRecord(runner, type, "t_registrant", "id");
        System.out.println(new Gson().toJson(map));
    }

    @Test
    public void fullImport() {
        String tableName = "BXKC.T_REGISTRANT";
        Basic basic = Basic.builder()
                .tableName(tableName)
                .pageSize(10000)
                .build();
        OracleUtils.migrateToMySQL(basic);
    }

    @Test
    public void deltaImport() {
        String tableName = "BXKC.T_REGISTRANT";
        String mysqlTableName = DbUtils.getTableName(tableName, false, false);
        String mysqlPrimaryKey = "id";
        QueryRunner runner = DbUtils.getQueryRunner(MysqlUtils.dataSource);
        DbType type = DbType.MYSQL;
        Map<String, Object> map = DbUtils.getLastRecord(runner, type, mysqlTableName, mysqlPrimaryKey);
        Object lastId = map.get(mysqlPrimaryKey);
        //migrate
        List<String> condition = new ArrayList<>();
        condition.add(String.format("%s>%s", mysqlPrimaryKey, lastId));
        Basic basic = Basic.builder()
                .tableName(tableName)
                .condition(condition)
                .build();
        OracleUtils.migrateToMySQL(basic);
    }

}
