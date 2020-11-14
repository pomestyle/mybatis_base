package com.udeam.eumus;

/**
 * 执行sql语句类型
 */
public enum ExcutorEnum {

    /**
     * 查询
     */
    TYPE_QUERY(1,"select"),

    /**
     * 新增
     */
    TYPE_ADD(2,"add"),

    /**
     * 修改
     */
    TYPE_UPDATE(3,"update"),

    /**
     * 删除
     */
    TYPE_DELETE(4,"delete");

    ExcutorEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    private Integer code;


    private String   type;


    /**
     * 根据code 获取 类型
     * @param code
     * @return
     */
    public static String getType(Integer code){
        for (ExcutorEnum value : ExcutorEnum.values()) {
            if (value.code == code){
                return value.type;
            }
        }
        return null;
    }

    /**
     * 根据名字获取code
     * @param type
     * @return
     */
    public static String getCode(String type){
        for (ExcutorEnum value : ExcutorEnum.values()) {
            if (value.type.equals(type)){
                return value.type;
            }
        }
        return null;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
