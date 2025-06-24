package com.pythonsinger.plugin.mcp.annotations;

import com.pythonsinger.plugin.mcp.service.BasicConvertService;

import java.lang.annotation.*;

/**
 * 迭代转换器
 * @author Zhihe
 */
@Repeatable(ItemFieldMappings.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
public @interface ItemFieldMapping {

    /**
     * 目标字段
     */
    String target();

    /**
     * 来源字段（如果为空就是自己，如果是字段就是对象中的下一层）
     */
    String source() default "";

    /**
     * 主键字段名
     */
    String keyField();

    /**
     *转义服务
     */
    Class<? extends BasicConvertService<?,?>> service();

}
