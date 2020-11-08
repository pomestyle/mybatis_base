package com.udeam.config;


import com.udeam.utils.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 对mapper 里sql 进行处理
 */
public class BoundSql {


    //解析过后的sql语句
    private String sqlText;
    // 解析出来的参数
    private List<ParameterMapping> parameterMappingList = new ArrayList<ParameterMapping>();

    public BoundSql(String sqlText, List<ParameterMapping> parameterMappingList) {
        this.sqlText = sqlText;
        this.parameterMappingList = parameterMappingList;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }

}
