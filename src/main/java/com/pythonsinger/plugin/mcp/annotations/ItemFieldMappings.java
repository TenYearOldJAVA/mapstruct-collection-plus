package com.pythonsinger.plugin.mcp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhihe
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
public @interface ItemFieldMappings {


    ItemFieldMapping[] value();
}
