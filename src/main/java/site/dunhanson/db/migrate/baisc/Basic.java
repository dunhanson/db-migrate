package site.dunhanson.db.migrate.baisc;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author dunhanson
 * @date 2020-01-21
 * @description 迁移信息
 */
@Data
@Builder
public class Basic {
    /**表名**/
    private String tableName;
    /**字段**/
    private List<String> fieldName;
    /**where条件**/
    private List<String> condition;
    /**分页大小**/
    private Integer pageSize;
}
