package com.pythonsinger.plugin.mcp.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author Zhihe
 */
public interface BasicConvertService<K extends Serializable,V>  {

    /**
     * 获取映射表
     * @param ids Ids
     * @return 通用映射表
     */
    Map<K, V> getNameMapByIds(Collection<K> ids);
}
