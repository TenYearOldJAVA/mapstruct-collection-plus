package com.pythonsinger.plugin.mcp.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Zhihe
 */
public interface ListNameConvertService<K extends Serializable,V>  extends BasicConvertService<K,V> {

    /**
     * 获取映射表
     * @param ids Ids
     * @return 通用映射表
     */
    @Override
    default Map<K, V> getNameMapByIds(Collection<K> ids){
        return getNameMapByListIds(new ArrayList<>(ids));
    }

    Map<K, V> getNameMapByListIds(List<K> idList);
}
