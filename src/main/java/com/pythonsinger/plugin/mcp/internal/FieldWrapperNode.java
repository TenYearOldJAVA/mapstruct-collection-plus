package com.pythonsinger.plugin.mcp.internal;

import com.pythonsinger.plugin.mcp.annotations.ItemFieldMapping;

import java.util.*;

/**
 * @author Zhihe
 */
public final class FieldWrapperNode extends com.pythonsinger.plugin.mcp.internal.AbstractWrapperNode {

    private final Map<String,FieldNode> fieldNodeMap = new HashMap<>();

    public FieldWrapperNode(List<ItemFieldMapping> itemFieldMapping) {
        super(itemFieldMapping.get(0));
        itemFieldMapping.forEach(
                item -> {
                    String keyField = item.keyField();
                    String idFieldName = "get" + keyField.substring(0, 1).toUpperCase() + keyField.substring(1);

                    CellNode cellNode = new CellNode();
                    String target = item.target();
                    cellNode.setTargetName("set" + target.substring(0, 1).toUpperCase() + target.substring(1));
                    String source = item.source();
                    boolean needVar = false;
                    if("".equals(source)) {
                        cellNode.setSourceMethod("");
                    }else{
                        cellNode.setSourceMethod("get" + source.substring(0, 1).toUpperCase() + source.substring(1));
                        needVar = true;
                    }
                    FieldNode fieldNode = fieldNodeMap.get(idFieldName);
                    if (fieldNode == null) {
                        fieldNode = new FieldNode();
                        fieldNode.idFieldName = idFieldName;
                        if(needVar){
                            fieldNode.needVar = true;
                        }
                        fieldNode.wrapCells = new ArrayList<CellNode>();
                        fieldNode.wrapCells.add(cellNode);
                        fieldNodeMap.put(idFieldName, fieldNode);
                    }else{
                        if(needVar){
                            fieldNode.needVar = true;
                        }
                        fieldNode.wrapCells.add(cellNode);
                    }
                }
        );
    }

    public List<FieldNode> getFieldNodes() {
        return fieldNodeMap.values().stream().toList();
    }

    public static class CellNode {

        private String targetName;

        private String sourceMethod;


        public String getSourceMethod() {
            return sourceMethod;
        }

        public void setSourceMethod(String sourceMethod) {
            this.sourceMethod = sourceMethod;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }
    }

    public static class FieldNode {

        private String idFieldName;

        private Boolean needVar =false;

        private List<CellNode> wrapCells;

        public String getIdFieldName() {
            return idFieldName;
        }

        public Boolean getNeedVar() {
            return needVar;
        }

        public List<CellNode> getWrapCells() {
            return wrapCells;
        }
    }

}
