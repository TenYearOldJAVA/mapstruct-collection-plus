package com.pythonsinger.plugin.mcp.util;

import com.pythonsinger.plugin.mcp.annotations.ItemFieldMapping;
import com.pythonsinger.plugin.mcp.internal.AbstractWrapperNode;
import com.pythonsinger.plugin.mcp.internal.FieldWrapperNode;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhihe
 */
public class WrapperNodeBuilderUtil {

    public static List<AbstractWrapperNode> getNodeList(List<ItemFieldMapping> itemFieldMappings) {

        List<AbstractWrapperNode> wnodeList = new ArrayList<>();
        Map<DeclaredType, List<ItemFieldMapping>> nodeList = new HashMap<>();
        for (ItemFieldMapping itemFieldMapping : itemFieldMappings) {
            DeclaredType wrapperNode = getWrapperNode(itemFieldMapping);
            nodeList.computeIfPresent(wrapperNode, (type, oldMap) -> {
                for (ItemFieldMapping oldItemFieldMapping : oldMap) {
                    if (!oldItemFieldMapping.target().equals(itemFieldMapping.target())) {
                        oldMap.add(itemFieldMapping);
                        break;
                    }
                }
                return oldMap;
            });
            nodeList.computeIfAbsent(wrapperNode, item -> {
                ArrayList<ItemFieldMapping> objects = new ArrayList<>();
                objects.add(itemFieldMapping);
                return objects;
            });
        }
        for (Map.Entry<DeclaredType, List<ItemFieldMapping>> entry : nodeList.entrySet()) {
            List<ItemFieldMapping> value = entry.getValue();
            //支持同service但不同的Id
            wnodeList.add(new FieldWrapperNode(value));
        }
        return wnodeList;

    }


    private static DeclaredType getWrapperNode(ItemFieldMapping itemFieldMapping) {
        try {
            // 直接访问 service 属性会导致 MirroredTypeException
            itemFieldMapping.service();
        } catch (MirroredTypeException e) {
            // 抓取 MirroredTypeException 并获取 TypeMirror
            return (DeclaredType) e.getTypeMirror();
        }
        return null;

    }
}
