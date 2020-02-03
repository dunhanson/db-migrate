package site.dunhanson.db.migrate.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.StringUtils;
import site.dunhanson.db.migrate.baisc.DbType;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class DbUtils {

    /**
     * 获取mysql表名
     * @param basicTableName
     * @param first
     * @param point
     * @return
     */
    public static String getTableName(String basicTableName, boolean first, boolean point) {
        String[] tableNameArr = basicTableName.split(",");
        if(tableNameArr == null || tableNameArr.length == 1) {
            tableNameArr = new String[]{tableNameArr[0], tableNameArr[0]};
        }
        String tableName = tableNameArr[0];
        //第一个
        if(first) {
            tableName = tableNameArr[1];
        }
        //无点
        if(!point) {
            String[] arr = tableName.split("\\.");
            tableName = arr[1];
        }
        return tableName;
    }

    /**
     * 获取QueryRunner
     * @param dataSource
     * @return
     */
    public static QueryRunner getQueryRunner(DataSource dataSource) {
        return new QueryRunner(dataSource);
    }

    /**
     * 获取最后一条记录
     * @param runner
     * @param type
     * @param tableName
     * @param primary
     * @return
     */
    public static Map<String, Object> getLastRecord(QueryRunner runner, DbType type, String tableName, String primary) {
        String sql = "";
        switch (type) {
            case MYSQL:
                sql = "select * from %s order by %s desc limit 0,1";
                break;
        }
        if(StringUtils.isBlank(sql)) {
            throw new RuntimeException("database not support");
        }
        sql = String.format(sql, tableName, primary);
        try {
            List<Map<String, Object>> list = runner.query(sql, new MapListHandler());
            if(list == null) {
                return null;
            }
            return list.get(0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
