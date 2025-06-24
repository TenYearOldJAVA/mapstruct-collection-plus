<#-- @ftlvariable name="" type="com.pythonsinger.plugin.mcp.internal.FieldWrapperNode" -->
<#list fieldNodes as fieldNode>
    if (${ext.loopVariableName}.${fieldNode.idFieldName}()!=null){
    <#if fieldNode.needVar>
        ${typeParameterClassName} var${ext.targetIndex}  = ${mapName}.get(${ext.loopVariableName}.${fieldNode.idFieldName}());
        if(var${ext.targetIndex} != null){
        <#list fieldNode.wrapCells as cell>
            <#if cell.sourceMethod == "">
                tempVar.${cell.targetName}(var${ext.targetIndex});
            <#else >
                tempVar.${cell.targetName}(var${ext.targetIndex}.${cell.sourceMethod}());
            </#if>
        </#list>
        }
    <#else >
        <#list fieldNode.wrapCells as cell>
            tempVar.${cell.targetName}(${mapName}.get(${ext.loopVariableName}.${fieldNode.idFieldName}()));
        </#list>
    </#if>
    }
</#list>