# db-migrate

database data migrate to other database 

*now just support Oracle to MySQL*



## Detail

site.dunhanson.db.migrate.baisc.Basic

> basic info

* tableName

  > table name

* fieldName

  > field name

* condition

  > where condition

* pageSize

  > page size



site.dunhanson.db.migrate.baisc.DbType

* MYSQL

  > MySQL



site.dunhanson.db.migrate.baisc.Page

* pageSize

  > page size

* totalCount

  > total count

* pages

  > pages

* pageIndex

  > page index

  

## Test Code

```java
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
```





## Reference

[Commons DbUtils: JDBC Utility Component](https://commons.apache.org/proper/commons-dbutils/)

[c3p0 - JDBC3 Connection and Statement Pooling](https://www.mchange.com/projects/c3p0/)



