package com.pythonsinger.plugin.mcp.internal;

import com.pythonsinger.plugin.mcp.util.ConvertMethod;
import com.pythonsinger.plugin.mcp.annotations.ItemFieldMapping;
import com.pythonsinger.plugin.mcp.util.WrapperNodeBuilderUtil;
import org.mapstruct.ap.internal.model.*;
import org.mapstruct.ap.internal.model.common.Assignment;
import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.Strings;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mapstruct.ap.internal.util.Collections.first;

/**
 * @author Zhihe
 */
public final class WrapperMappingMethod extends MappingMethod {

    //追加参数
    private final Integer fieldSize;
    private final List<AbstractWrapperNode> nodeList;

    //MPS自带参数
    private final MethodReference factoryMethod;
    private final boolean overridden;
    private final boolean mapNullToDefault;
    private final String loopVariableName;
    private final String index1Name;
    private final String index2Name;
    private final IterableCreation iterableCreation;
    private final Assignment elementAssignment;

    private final Set<Type> selfTypes = new HashSet<>();

    public void addImport(Type e){
        selfTypes.add(e);
    }

    public WrapperMappingMethod(ConvertMethod convertMethod, Collection<String> existingVariables , List<LifecycleCallbackMethodReference> beforeMappingReferences, IterableMappingMethod iterableMappingMethod, List<ItemFieldMapping> itemFieldMappings) {
        super(convertMethod,existingVariables,beforeMappingReferences ,iterableMappingMethod.getAfterMappingReferences());
        this.overridden = iterableMappingMethod.isOverridden();
        this.mapNullToDefault = iterableMappingMethod.isMapNullToDefault();
        this.loopVariableName = iterableMappingMethod.getLoopVariableName();
        this.index1Name = Strings.getSafeVariableName( "i", existingVariables );
        this.index2Name = Strings.getSafeVariableName( "j", existingVariables );
        this.iterableCreation = IterableCreation.create( iterableMappingMethod, getSourceParameter() );
        this.elementAssignment = iterableMappingMethod.getElementAssignment();
        this.factoryMethod = iterableMappingMethod.getFactoryMethod();
        //拿到循环里面的字段
        this.nodeList = WrapperNodeBuilderUtil.getNodeList(itemFieldMappings);
        this.fieldSize = this.nodeList.size();
//        this.nodeList = new ArrayList<WrapperNode>(fieldSize);
    }

    public IterableCreation getIterableCreation() {
        return iterableCreation;
    }

    public boolean isOverridden() {
        return overridden;
    }

    public boolean isMapNullToDefault() {
        return mapNullToDefault;
    }

    public Parameter getSourceParameter() {
        for ( Parameter parameter : getParameters() ) {
            if ( !parameter.isMappingTarget() && !parameter.isMappingContext() ) {
                return parameter;
            }
        }

        throw new IllegalStateException( "Method " + this + " has no source parameter." );
    }

    public String getLoopVariableName() {
        return loopVariableName;
    }

    public String getIndex1Name() {
        return index1Name;
    }

    public String getIndex2Name() {
        return index2Name;
    }

    @Override
    public Set<Type> getImportTypes() {
        Set<Type> types = super.getImportTypes();
        if ( ( factoryMethod == null ) && ( !isExistingInstanceMapping() ) ) {
            if ( getReturnType().getImplementationType() != null ) {
                types.addAll( getReturnType().getImplementationType().getImportTypes() );
            }
        }
        else if ( factoryMethod != null ) {
            types.addAll( factoryMethod.getImportTypes() );
        }
        if ( elementAssignment != null ) {
            types.addAll( elementAssignment.getImportTypes() );
        }

        if ( iterableCreation != null ) {
            types.addAll( iterableCreation.getImportTypes() );
        }

        types.addAll( selfTypes );


        types.add( getSourceElementType() );
        return types;
    }

    public Type getSourceElementType() {
        Type sourceParameterType = getSourceParameter().getType();

        if ( sourceParameterType.isArrayType() ) {
            return sourceParameterType.getComponentType();
        }
        else {
            return first( sourceParameterType.determineTypeArguments( Iterable.class ) ).getTypeBound();
        }
    }

    public Type getResultElementType() {
        if ( getResultType().isArrayType() ) {
            return getResultType().getComponentType();
        }
        else {
            return first( getResultType().determineTypeArguments( Iterable.class ) );
        }
    }

    public Assignment getElementAssignment() {
        return elementAssignment;
    }

    public MethodReference getFactoryMethod() {
        return this.factoryMethod;
    }

    public Integer getFieldSize() {
        return fieldSize;
    }

    public List<AbstractWrapperNode> getNodeList() {
        return nodeList;
    }
}
