package com.udeam.config;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用来保存 数据库配置信息 和 每个mapper中sql唯一类型 namespace.sql的id
 *
 */
public class Configration {

    /**
     * 数据源对象
     */
    private DataSource dataSource;

    /**
     * key 规则是namesapc + . + id(每个sql语句的id)  设置参数以及返回类型时候使用
     */
    private Map<String,MappedStatement> mapperStamentMap = new HashMap<>();


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMapperStamentMap() {
        return mapperStamentMap;
    }

    public void setMapperStamentMap(Map<String, MappedStatement> mapperStamentMap) {
        this.mapperStamentMap = mapperStamentMap;
    }
}
