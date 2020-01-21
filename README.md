# db-migrate

database data migrate to other database 

*now just support Oracle to MySQL*



## Quick Start

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



## Config

c3p0-config.xml

```xml
<c3p0-config>
    <named-config name="migrate_mysql">
        <property name="jdbcUrl">jdbc:mysql://XXX.XX.X.XXX:3306/bxkc</property>
        <property name="driverClass">com.mysql.jdbc.Driver</property>
        <property name="user">mysql</property>
        <property name="password">******</property>
        <property name="acquireIncrement">5</property>
        <property name="initialPoolSize">5</property>
        <property name="minPoolSize">15</property>
        <property name="maxPoolSize">30</property>
    </named-config>
    <named-config name="migrate_oracle">
        <property name="jdbcUrl">jdbc:oracle:thin:@XXX.XX.X.XXX:1521:orcl</property>
        <property name="driverClass">oracle.jdbc.OracleDriver</property>
        <property name="user">oracle</property>
        <property name="password">******</property>
        <property name="acquireIncrement">5</property>
        <property name="initialPoolSize">5</property>
        <property name="minPoolSize">15</property>
        <property name="maxPoolSize">30</property>
    </named-config>
</c3p0-config>
```



log4j.properties

```properties
log4j.rootLogger=DEBUG, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m%n
```



## Class

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

  

## Reference

[Commons DbUtils: JDBC Utility Component](https://commons.apache.org/proper/commons-dbutils/)

[c3p0 - JDBC3 Connection and Statement Pooling](https://www.mchange.com/projects/c3p0/)



