package com.udeam.config;

import com.udeam.eumus.ExcutorEnum;

/**
 * 保存每个mapper中sql的sql语句类型 , sql入参,返回类型以及sql的id
 */
public class MappedStatement {

    /**
     * sql类型
     */
    private Integer codeType;

    /**
     * sql xml语句id 表示每条sql的唯一性
     */
    private String id;

    /**
     * sql入参类型
     */
    private Class<?> paramType;

    /**
     * sql返回类型
     */
    private Class<?> resultType;

    /**
     * sql语句
     */
    private String sql;


    public Integer getCodeType() {
        return codeType;
    }

    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public void setParamType(Class<?> paramType) {
        this.paramType = paramType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
