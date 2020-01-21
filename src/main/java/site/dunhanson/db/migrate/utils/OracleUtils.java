package site.dunhanson.db.migrate.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import site.dunhanson.db.migrate.baisc.Basic;
import site.dunhanson.db.migrate.baisc.Page;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class OracleUtils {
    public static DataSource dataSource = new ComboPooledDataSource("migrate_oracle");
    public static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * 从oracle读取数据
     * @param info 迁移信息对象
     */
    public static void migrateToMySQL(Basic info) {
        //runner
        QueryRunner runner = DbUtils.getQueryRunner(dataSource);
        //迁移变量
        String tableName = info.getTableName();
        //oracle表名
        String oracleTableName = DbUtils.getTableName(tableName, true, true);
        //自定义字段
        List<String> fieldName = info.getFieldName();
        //where条件
        List<String> condition = info.getCondition();
        //分页大小
        Integer pageSize = info.getPageSize();
        //总记录数
        long totalCount = countOracle(oracleTableName, condition);
        //分页大小
        pageSize = pageSize == null || pageSize == 0 ? DEFAULT_PAGE_SIZE : pageSize;
        //分页对象
        Page page = new Page(pageSize, totalCount);
        //分页获取数据
        for(long pageNum = 1; pageNum <= page.getPages(); pageNum++) {
            //当前页的索引下标
            long pageIndex = page.getPageIndex(pageNum);
            long endIndex = pageIndex + page.getPageSize();
            try {
                //字段名集
                List<String> oracleFieldName = getFieldName(fieldName, oracleTableName);
                //结果集
                List<Map<String, Object>> list = runner.query(
                        getQuerySql(oracleTableName, condition, pageIndex, endIndex),
                        new MapListHandler()
                );
                if(list != null && list.size() > 0) {
                    int oneSize = list.size();
                    int twoSize = oracleFieldName.size();
                    Object[][] params = new Object[oneSize][twoSize];
                    for(int i = 0; i < oneSize; i++) {
                        Map<String, Object> map = list.get(i);
                        for(int j = 0; j < twoSize; j++) {
                            params[i][j] = map.get(oracleFieldName.get(j));
                        }
                    }
                    String sql = MysqlUtils.getInsertSql(tableName, oracleFieldName);
                    DbUtils.getQueryRunner(MysqlUtils.dataSource).insertBatch(sql, new ArrayListHandler(), params);
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 获取oracle字段数组
     * @param customFieldName
     * @param tableName
     * @return
     */
    private static List<String> getFieldName(List<String> customFieldName, String tableName) {
        List<String> fieldName = new ArrayList<>();
        if(customFieldName == null || customFieldName.size() == 0) {
            QueryRunner runner = DbUtils.getQueryRunner(dataSource);
            String sql = "select column_name as name from all_tab_columns where TABLE_NAME = ?";
            try {
                //查询获取oracle字段
                String[] arr = tableName.split("\\.");
                tableName = arr.length == 2 ? arr[1] : arr[0];
                List<Map<String,Object>> list = runner.query(sql, new MapListHandler(), tableName);
                list.forEach(row->{
                    row.keySet().forEach(field->{
                        fieldName.add((String)row.get(field));
                    });
                });
                if(fieldName.size() == 0) {
                    throw new RuntimeException("oracle field can't get");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for(String getFieldName : customFieldName) {
                fieldName.add(getFieldName.split("\\.")[0]);
            }
        }
        return fieldName;
    }

    /**
     * 获取oracle查询sql
     * @param tableName 表名
     * @param startIndex 开始记录数
     * @param startIndex 开始记录数
     * @param endIndex 结束记录数
     * @return
     */
    public static String getQuerySql(String tableName, List<String> condition, long startIndex, long endIndex) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("select t2.* from ( ");
        stringBuffer.append("select ROWNUM as rowno, t.* from %s t where 1=1 ");
        stringBuffer.append("and ");;
        if(condition != null && condition.size() > 0) {
            condition.forEach(obj->{
                stringBuffer.append(obj);
                stringBuffer.append("and ");
            });
        }
        stringBuffer.append("ROWNUM < %s ");
        stringBuffer.append(") t2 where t2.rowno >= %s");
        String sql = stringBuffer.toString();
        sql = String.format(sql, tableName, endIndex, startIndex);
        return sql;
    }

    /**
     * 获取count SQL
     * @param tableName 表名
     * @param condition 查询条件
     * @return
     */
    public static long countOracle(String tableName, List<String> condition) {
        StringBuffer countSQL = new StringBuffer();
        countSQL.append("select count(1) from ");
        countSQL.append(tableName + " ");
        if(condition != null && condition.size() > 0) {
            countSQL.append("where ");
            countSQL.append("1=1 ");
            countSQL.append("and ");
            condition.forEach(obj->{
                countSQL.append(obj);
                countSQL.append("and ");
            });
        }
        String sql = countSQL.toString();
        if(sql.endsWith("and ")) {
            int index = sql.lastIndexOf("and ");
            sql = sql.substring(0, index);
        }
        QueryRunner runner = DbUtils.getQueryRunner(dataSource);
        try {
            return runner.query(sql, new ScalarHandler<BigDecimal>()).longValue();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return 0;
    }
}
