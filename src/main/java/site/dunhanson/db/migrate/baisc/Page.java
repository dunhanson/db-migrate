package site.dunhanson.db.migrate.baisc;

import lombok.Builder;
import lombok.Data;

@Data
public class Page {
    //分页大小
    private Integer pageSize;
    //总记录数
    private Long totalCount;
    //总页数
    private Long pages;
    //当前下标
    private Long pageIndex;

    public Page(int pageSize, long totalCount) {
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        if(pageSize >= totalCount) {
            this.pages = new Long(1);
        } else {
            this.pages = totalCount % pageSize == 0 ? totalCount / pageSize : (totalCount / pageSize + 1);
        }
    }

    public Long getPageIndex(long pageNum) {
        return new Long((pageNum - 1) * pageSize);
    }
}
