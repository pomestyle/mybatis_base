package com.udeam.io;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 加载配置文件
 */
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
