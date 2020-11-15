# mybatis_base
自定义mybatis框架 实现单表增删改查





# 自定持久框架 mybatis


## 前言

JDBC操作数据库和Mybatis操作数据库,为什么使用Mybatis框架,而不去使用原生过的JDBC操作数据库呢?
带着这么几个问题,我们先来看看原生的JDBC操作数据步骤!

### JDBC操作数据库步骤

- 1 加载驱动
- 2 创建连接
- 3 编译sql语句
- 4 设置参数
- 5 执行sql语句
- 6 获取结果返回集
- 7 关闭连接

JDBC操作数据库存在的几个痛点:

- 1 首先第一步加载驱动,这个我们完全可以通过反射来解决,更换不同的数据库驱动;
- 2 创建连接,每一次操作数据库都要去现成的连接数据库,如果操作数据库很频繁,这种开销很消耗资源,我们可以采用`线程池`,`的思路去解决!
- 3 3,4步骤可以一起来看,编译sql语句,这个通常设置一些参数,如果是很多参数对象,经常改动比较大,在硬编码过程中,稍微操作不慎可能会代码出错,改动成本很高,耦合性很大!
- 4 执行sql 没什么可说的;
- 5 获取结果返回集,查询而言结果返回,每个查询接受的结果集不同,此处,对象不同,其他代码都是重复的;
- 6 关闭连接

以上几个分析过程,几乎每次操作都会面临数据库连接,关闭,获取结果集(返回类型不通过,对象类型不同)重复代码很高,参数设置严重耦合,改动频繁,出错率高;

对此重复度高的代码,可以通过封装利用去解决;
对于驱动,可以通过配置文件,更换不同的数据库驱动;
对于频繁连接,可以通过连接池去解决;
对于设置参数,获取结果集,可以通过配置文件,以及泛型,反射,去封装不同类型的返回结果集;



## 项目结构
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201108161106191.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zODM2MTM0Nw==,size_16,color_FFFFFF,t_70#pic_center)

 ###  需要用到的依赖
  
  ```xml
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.17</version>
        </dependency>
        <dependency>
            <groupId>c3p0</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.1.2</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.12</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.10</version>
        </dependency>
        <dependency>
            <groupId>dom4j</groupId>
            <artifactId>dom4j</artifactId>
            <version>1.6.1</version>
        </dependency>
        <dependency>
            <groupId>jaxen</groupId>
            <artifactId>jaxen</artifactId>
            <version>1.1.6</version>
        </dependency>

 ```

## 自定义框架

### 客户端


  ##### 步骤
  
  - 首先定义数据库配置文件,配置引入的sqlMapper文件
  
   > 使用字节输入流将其加载到内存中,使用过dom4j解析封装成Configuration对象,重复使用;

  - 定义sqlMapper文件 用来编写sql语句,入参,出参类型
  > 加载解析封装对象MappedStatement用来保存每个sqlMapper每条sql语句的入参,出参类型以及sql操作类型;
    
  
#### 定义数据库配置文件
  
首先我们定义数据库配置文件`sqlMapConfigration.xml`
  
```xml
<configuration>

    <!--数据库连接信息-->
    <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
    <property name="jdbcUrl"  value="jdbc:mysql:///stu_test"></property>
    <property name="username" value="root"></property>
    <property name="password" value="root"></property>


    <!--    配置mapper sql信息文件 会有多个-->
    <mapper resource="mapper.xml"></mapper>

</configuration>
```


定义 `Configration`类

用来保存 数据库配置信息 和 每个mapper中sql唯一类型 namespace.sql的id

```java
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

```


定义`xmlConfigerBuilder`类解析

  
#### 定义查询语句sql配置文件

mapper.xml

定义sql以及sql入参对象类型,sql查询返回类型

```xml
<mapper namespace="User"> 

<select id="selectOne" paramterType="com.udeam.com.udeam.pojo.User" resultType="com.udeam.com.udeam.pojo.User"> 
select * from user where id = #{id} and name =#{name} 
</select>

<select id="selectList" resultType="com.udeam.com.udeam.pojo.User"> 
select * from user 
</select> 

</mapper>
```


#### 定义 MappedStatement 实体类
保存每个mapper中sql的sql语句类型 , sql入参,返回类型以及sql的id

```java
public class MappedStatement {

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
```

定义`SqlSessionFactoryBuilder`类`build()`方法解析 `sqlMapConfigration.xml`
```java
public class SqlSessionFactoryBuilder {

    private Configration configration;

    public SqlSessionFactoryBuilder() {
        this.configration = new Configration();
    }

    public SqlSessionFactory build(InputStream inputStream) throws Exception {
        //1 解析配置文件,封装Configuration
        xmlConfigerBuilder xmlConfigerBuilder = new xmlConfigerBuilder(configration);
        //2 解析
        configration = xmlConfigerBuilder.parseConfigration(inputStream);

        // 3 创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configration);
        return sqlSessionFactory;
    }

}
```


定义`xmlConfigerBuilder`类用于

```java
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

```


定义`XMLMapperBuilder`类解析Mapper类中信息,解析Mapper封装配置类中每个sql的sql语句以及返回类型,入参类型

解析mapper 封装sql语句属性到MappedStatement

```java
public class XMLMapperBuilder {

    private Configration configration;

    public XMLMapperBuilder(Configration configration) {
        this.configration = configration;
    }

    public void parse(InputStream inputStream1) throws DocumentException, ClassNotFoundException {

        Document read = new SAXReader().read(inputStream1);
        Element rootElement = read.getRootElement();

        //获取namespace
        String namespace = rootElement.attributeValue("namespace");
        //读取每一个查询标签
        List<Element> list = rootElement.selectNodes("//select");
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
```

定义`Resource`类读取xml
```java
public class Resource {

    /**
     * 加载配置文件工具类
     * @param name
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream inputStream(String name) throws Exception {
        //使用类加载器加载配置文件
        InputStream inputStream = Resource.class.getClassLoader().getResourceAsStream(name);
        return inputStream;
    }
}

```







### 查询

#### 定义查询接口SqlSession

```java
public interface SqlSession {

    //查询多个
    public <E>List<E> selectList(String statementId,Object...params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;

    //查询单个
    public <T> T  selectOne(String statementId,Object...params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;


    public void close() throws Exception;

}

```


sql语句执行器`Excutor`接口

```java
public interface Excutor {

    /**
     * 查询接口
     * @param configration 数据库配置类
     * @param mappedStatement mapper 信息对象
     * @param params 参数
     * @param <E>
     * @return
     */
    <E> List<E> query(Configration configration, MappedStatement mappedStatement,Object[] params) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException, IntrospectionException, InvocationTargetException;

    void close() throws Exception;
}

```

sql语句执行器实现类 
具体的sql执行器 (mybatis 中有三个),默认的是SimpleExcutor


这里面对通过传入的配置文件以及具体的 key(namespace.sql id)从MappedStatement获取sql以及sql入参返回类型
然后通过反射区设置参数,获取结果返回集;

其中 `BoundSql`类是对xml中sql进行处理,将其转换为`?`占位符,解析出#{}里面的值进行存储,然后再去执行后续的赋值操作!
```java
public class SimpleExcutor implements Excutor {

    private Connection connection;

    @Override
    public <E> List<E> query(Configration configration, MappedStatement mappedStatement, Object[] params) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException, IntrospectionException, InvocationTargetException {

        //1 获取连接
        connection = configration.getDataSource().getConnection();

        //2 获取sql select * from user where id = #{id} and name = #{name}
        String sql = mappedStatement.getSql();
        //对sql进行处理    //转换sql语句： select * from user where id = ? and name = ? ，转换的过程中，还需要对#{}里面的值进行解析存储
        BoundSql boundSql = getBoundSql(sql);

        //最终的sql
        String finalSql = boundSql.getSqlText();

        // 3 预编译对象
        PreparedStatement preparedStatement = connection.prepareStatement(finalSql);


        //获取传入的参数类型
        Class<?> paramType = mappedStatement.getParamType();


        // 4 获取传入参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        //设置参数
        for (int i = 0; i < parameterMappingList.size(); i++) {
            String content = parameterMappingList.get(i).getContent();
            //反射设置值
            Field declaredField = paramType.getDeclaredField(content);
            //强制访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            //占位符设置值  列是从1开始的
            preparedStatement.setObject(i + 1, o);
            System.out.println(" 当前属性是 " + content + " 值是 : " + o);
        }

        // 5. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        //返回的参数类型
        Class<?> resultType = mappedStatement.getResultType();

        ArrayList<E> objects = new ArrayList<>();
        while (resultSet.next()) {
            //创建对象
            Object o = (E) resultType.newInstance();

            //获取数据库返回的列值 元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //返回列总数
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                //获取列值
                String columnName = metaData.getColumnName(i);
                //获取值
                Object value = resultSet.getObject(columnName);
                //使用内省技术 也可以使用反射技术
                //创建属性描述器，为属性生成读写方法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultType);
                //获取写方法
                Method writeMethod = propertyDescriptor.getWriteMethod();
                // 向类中写入值
                writeMethod.invoke(o, value);
            }

            objects.add((E) o);
        }


        return objects;
    }


    @Override
    public void close() throws Exception {
        connection.close();
    }



    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
        return boundSql;

    }
}

```

具体的实现查询接口如`DefaultSqlSession`
默认的sqlsession实现类(mybatis中默认的DefaultSqlSession)

```java
public class DefaultSqlSession implements SqlSession {


    private Configration configration;

    //sql执行器
    private Excutor simpleExcutor = new SimpleExcutor();

    public DefaultSqlSession(Configration configration) {

        this.configration = configration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {

        //根据 statementId 获取 MappedStatement 对象
        MappedStatement mappedStatement = configration.getMapperStamentMap().get(statementId);

        //sql 执行器
        List<Object> query = simpleExcutor.query(configration, mappedStatement, params);
        return (List<E>) query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementId, params);
        if (objects==null || objects.size() == 0){
            return null;
        }
        if (objects.size()>1){
            throw new RuntimeException("存在多个值!");
        }

        return (T) objects.get(0);
    }

    @Override
    public void close() throws Exception {
        simpleExcutor.close();
    }

}
```


定义`SqlSessionFactory`工厂用来生产不同的sqlSession去执行sql
获取SqlSession 示例 以及对象接口
```java
public interface SqlSessionFactory {

    public SqlSession openSqlSession();
}

```
具体工厂实现类,生产sqlsession对象执行增删改查操作
```java
public class DefaultSqlSessionFactory  implements SqlSessionFactory {


    private Configration configration;

    public DefaultSqlSessionFactory(Configration configration) {
        this.configration = configration;
    }

    @Override
    public SqlSession openSqlSession() {
        return new DefaultSqlSession(configration);
    }

```


解析的#{id}成站位符`?`工具类,以及内省创建对象工具类
GenericTokenParser
```java
public class GenericTokenParser {

  private final String openToken; //开始标记
  private final String closeToken; //结束标记
  private final TokenHandler handler; //标记处理器

  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  /**
   * 解析${}和#{}
   * @param text
   * @return
   * 该方法主要实现了配置文件、脚本等片段中占位符的解析、处理工作，并返回最终需要的数据。
   * 其中，解析工作由该方法完成，处理工作是由处理器handler的handleToken()方法来实现
   */
  public String parse(String text) {
    // 验证参数问题，如果是null，就返回空字符串。
    if (text == null || text.isEmpty()) {
      return "";
    }

    // 下面继续验证是否包含开始标签，如果不包含，默认不是占位符，直接原样返回即可，否则继续执行。
    int start = text.indexOf(openToken, 0);
    if (start == -1) {
      return text;
    }

   // 把text转成字符数组src，并且定义默认偏移量offset=0、存储最终需要返回字符串的变量builder，
    // text变量中占位符对应的变量名expression。判断start是否大于-1(即text中是否存在openToken)，如果存在就执行下面代码
    char[] src = text.toCharArray();
    int offset = 0;
    final StringBuilder builder = new StringBuilder();
    StringBuilder expression = null;
    while (start > -1) {
     // 判断如果开始标记前如果有转义字符，就不作为openToken进行处理，否则继续处理
      if (start > 0 && src[start - 1] == '\\') {
        builder.append(src, offset, start - offset - 1).append(openToken);
        offset = start + openToken.length();
      } else {
        //重置expression变量，避免空指针或者老数据干扰。
        if (expression == null) {
          expression = new StringBuilder();
        } else {
          expression.setLength(0);
        }
        builder.append(src, offset, start - offset);
        offset = start + openToken.length();
        int end = text.indexOf(closeToken, offset);
        while (end > -1) {////存在结束标记时
          if (end > offset && src[end - 1] == '\\') {//如果结束标记前面有转义字符时
            // this close token is escaped. remove the backslash and continue.
            expression.append(src, offset, end - offset - 1).append(closeToken);
            offset = end + closeToken.length();
            end = text.indexOf(closeToken, offset);
          } else {//不存在转义字符，即需要作为参数进行处理
            expression.append(src, offset, end - offset);
            offset = end + closeToken.length();
            break;
          }
        }
        if (end == -1) {
          // close token was not found.
          builder.append(src, start, src.length - start);
          offset = src.length;
        } else {
          //首先根据参数的key（即expression）进行参数处理，返回?作为占位符
          builder.append(handler.handleToken(expression.toString()));
          offset = end + closeToken.length();
        }
      }
      start = text.indexOf(openToken, offset);
    }
    if (offset < src.length) {
      builder.append(src, offset, src.length - offset);
    }
    return builder.toString();
  }
}

```

ParameterMapping 
```java

public class ParameterMapping {

    private String content;

    public ParameterMapping(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

```

ParameterMappingTokenHandler
```java
public class ParameterMappingTokenHandler implements TokenHandler {
	private List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();

	// context是参数名称 #{id} #{username}

	public String handleToken(String content) {
		parameterMappings.add(buildParameterMapping(content));
		return "?";
	}

	private ParameterMapping buildParameterMapping(String content) {
		ParameterMapping parameterMapping = new ParameterMapping(content);
		return parameterMapping;
	}

	public List<ParameterMapping> getParameterMappings() {
		return parameterMappings;
	}

	public void setParameterMappings(List<ParameterMapping> parameterMappings) {
		this.parameterMappings = parameterMappings;
	}

}

```
TokenHandler
```java
public interface TokenHandler {
  String handleToken(String content);
}

```



#### 普通测试

```java
    String name = "sqlMapConfigration.xml";
    // 1 加载xml配置文件
    InputStream inputStream = Resource.inputStream(name);
    // 2 解析配置文件
       //初始化Configration 初始化容器 mapperStamentMap容器保存mapper中sql 信息
    SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    // 3 创建会话
    SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
    SqlSession sqlSession = sqlSessionFactory.openSqlSession();
    //4 查询
    User user = new User();
    user.setId(2);
    user.setName("小王");
    
    //非代理模式
    User user1 = sqlSession.selectOne("User.selectOne", user);
    System.out.println(user1);
    List<User> usersList = sqlSession.selectList("User.selectList");
    System.out.println(usersList); 
```



这是使用namespace.id方式硬编码去查询,但实际过程中我们直接通过service层调用dao层mapper去查询执行的;

故此,我们需要想mybatis那样定义一个mapper接口类,然后使用动态代理调用执行;

在SqlSession接口中定义一个mapper代理接口
```java
    /**
     * Mapper代理接口
     * @param mapperClass
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<?> mapperClass);
```

#### UserMapper创建
```java

public interface UserMapper {

    /**
     * 查询所有
     * @return
     */
    List<User> selectList2();

    /**
     * 查询单个 根据条件
     * @param user
     * @return
     */
    User selectOne2(User user);
}

```

新创建mapper2.xml

````xml

<!--mapper代理模式
 语句id 必须和mapper 中查询语句方法名保持一致
 namespace 必须是类的权限定命名

 原因是JDK动态代理中 无法提供对应的namespace和查询语句配置id
 故此用方法名和mapper类的全限定命名进行使用 key从获取mapper配置文件sql语句的入参,返回类型;
-->

<mapper namespace="com.udeam.test.mapper.UserMapper">

    <!--    表示查询单个-->
    <select id="selectOne2" paramterType="com.udeam.pojo.User" resultType="com.udeam.pojo.User">
        select * from user where id = #{id} and name = #{name}
    </select>


    <!--    表示查询多个-->
    <select id="selectList2" resultType="com.udeam.pojo.User">
        select * from user
    </select>

</mapper>

````
然后在SqlMapXml里添加进去mapper

```java
 <mapper resource="mapper2.xml"></mapper>
```

在子类中去实现这个代理方法

这里需要注意的是

- 在下面jdk代理里面,我们无法拿到xml文件里`namespace和sql id`值
- 实际上使用mybatis时候,`mapper代理的方法名`,和`mapper,xml`里的会`保持一致`,namspacs会使用该mapper的权限定名;

在JDK动态代理中使用方法名和全路径去从Configration封装的map对象去获取xml里的sql的入参类型和返回类型;
```java
    /**
     * 使用JDK动态代理来执行mapper
     */
    @Override
    public <T> T getMapper(Class<?> mapperClass) {

        Object o = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //获取class的权限定命名
                String className = method.getDeclaringClass().getName();
                //获取方法名
                String name = method.getName();
                //拼接statementid 从map中获取sql 入参类型,返回类型
                String statementid = className + "." + name;
                //判断是否实现泛型类型参数化
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    //还是去执行查询方法
                    return selectList(statementid, args);
                }
                return selectOne(statementid, args);


            }
        });

       return (T) o;
    }
```


####  mapper代理测试

```java
  // 代理测试
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        System.out.println(userMapper.selectList2());
```


以上仅仅是实现了单表的查询操作,和传入的固定参数,对动态sql和删除在`底部`源代码中实现了,可以下载下来康康,删除和新增基本实现方式一样;


### 用到的设计模式
- 工厂模式
  在创建不同的sqlSession时进行使用,可以选择new 不同的子类;
- 代理模式
  使用JDK动态代理对Mapper进行代理
- 构建者模式
  在SqlSessionFactoryBuilder类中build()方法中通过对Configration 对象的属性构建;



### 代码地址
[点击下载](https://github.com/pomestyle/mybatis_base.git)
