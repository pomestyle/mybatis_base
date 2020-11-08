package com.udeam.xml;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.udeam.config.Configration;
import com.udeam.io.Resource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 解析xml
 * 封装属性以及Mapper属性
 */
public class xmlConfigerBuilder {

    private Configration configration;


    public xmlConfigerBuilder(Configration  configration) {
        this.configration = configration;
    }


    /**
     * 解析封装xml
     * @return
     */
    public Configration parseConfigration(InputStream inputStream) throws Exception {
        Document read = new SAXReader().read(inputStream);

        //获取跟标签
        Element rootElement = read.getRootElement();
        //1 设置datasource属性
        List<Element> elementList = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (int i = 0; i < elementList.size(); i++) {
            //设置属性值
            String name = elementList.get(i).attributeValue("name");
            String value = elementList.get(i).attributeValue("value");
            properties.setProperty(name,value);
        }
        // 设置连接池属性 , 这里使用c3p0连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        //设置datasource
        configration.setDataSource(comboPooledDataSource);

        //2 封装解析mapper属性
        // 读取mapper 设置mapper返回类型 以及sql等封装 MappedStatement 对象
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configration);
        List<Element> list = rootElement.selectNodes("//mapper");
        for (Element element : list) {
            String resource = element.attributeValue("resource");
            InputStream inputStream1 = Resource.inputStream(resource);
            //读取每一个mapper xml
            xmlMapperBuilder.parse(inputStream1);
        }

        return configration;
    }

}
