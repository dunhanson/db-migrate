package site.dunhanson.db.migrate.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MysqlUtils {
    public static DataSource dataSource = new ComboPooledDataSource("migrate_mysql");

    /**
     * 获取mysql插入sql
     * @param tableName
     * @param fieldName
     * @return
     */
    public static String getInsertSql(String tableName, List<String> fieldName) {
        //参数检查
        if(StringUtils.isBlank(tableName)) {
            throw new RuntimeException("tableName is empty");
        }
        if(fieldName == null || fieldName.isEmpty()) {
            throw new RuntimeException("fieldName is empty");
        }
        //字段值
        List<String> fieldValue = new ArrayList<>();
        fieldName.forEach(obj->{
            fieldValue.add("?");
        });
        //sql字符串
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("insert into %s (");
        stringBuffer.append(StringUtils.join(fieldName, ","));
        stringBuffer.append(") values(");
        stringBuffer.append(StringUtils.join(fieldValue, ","));
        stringBuffer.append(")");
        String sql = stringBuffer.toString();
        sql = String.format(sql, DbUtils.getTableName(tableName, false, false)).toLowerCase();
        return sql;
    }
}
