package com.udeam.xml;

import com.udeam.config.Configration;
import com.udeam.config.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * 解析mapper 封装sql语句属性
 */
public class XMLMapperBuilder {

    private Configration configration;

    public XMLMapperBuilder(Configration configration) {
        this.configration = configration;
    }

    public void parse(InputStream inputStream1) throws DocumentException, ClassNotFoundException {

        Document read = new SAXReader().read(inputStream1);
        Element rootElement = read.getRootElement();
        //读取每一个查询标签
        rootElementVoid(rootElement);
    }


    private void rootElementVoid(Element rootElement) throws ClassNotFoundException {
        //获取namespace
        String namespace = rootElement.attributeValue("namespace");

        //读取每一个查询标签
        List<Element> list = rootElement.selectNodes("//select");
        List<Element> update = rootElement.selectNodes("//update");
        List<Element> delete = rootElement.selectNodes("//delete");
        mappedStatementVoid(list, namespace);
        mappedStatementVoid(update, namespace);
        mappedStatementVoid(delete, namespace);
    }

    private void mappedStatementVoid(List<Element> list,String namespace) throws ClassNotFoundException {
        if (list==null || list.size() == 0 ){
            return;
        }
        for (Element element : list) {
            MappedStatement mappedStatement = new MappedStatement();
            //获取sql
            String sql = element.getTextTrim();
            mappedStatement.setSql(sql);
            //设置id
            String id = element.attributeValue("id");
            mappedStatement.setId(id);

            //设置入参类型
            String paramterType = element.attributeValue("paramterType");
            mappedStatement.setParamType(getClassType(paramterType));

            //设置返回类型
            String resultType = element.attributeValue("resultType");
            mappedStatement.setResultType(getClassType(resultType));

            //设置mapperStamentMap
            configration.getMapperStamentMap().put(namespace + "." + id, mappedStatement);
        }
    }


    public Class<?> getClassType(String type) throws ClassNotFoundException {
        if(type==null){
            return null;
        }

        Class<?> clasz = Class.forName(type);
        return clasz;

    }
}
