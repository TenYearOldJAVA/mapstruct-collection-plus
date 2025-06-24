package com.pythonsinger.plugin.mcp.util;

import org.mapstruct.ap.internal.model.IterableMappingMethod;
import org.mapstruct.ap.internal.model.common.Accessibility;
import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.source.MappingMethodOptions;
import org.mapstruct.ap.internal.model.source.Method;
import org.mapstruct.ap.internal.model.source.ParameterProvidedMethods;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

/**
 * @author Zhihe
 */
public class ConvertMethod implements Method {

    private final IterableMappingMethod iterableMappingMethod;

    public ConvertMethod(IterableMappingMethod iterableMappingMethod) {
        this.iterableMappingMethod = iterableMappingMethod;
    }



    @Override
    public boolean matches(List<Type> sourceTypes, Type targetType) {
        return false;
    }

    @Override
    public Type getDeclaringMapper() {
        return null;
    }

    @Override
    public String getName() {
        return iterableMappingMethod.getName();
    }

    @Override
    public List<Parameter> getParameters() {
        return iterableMappingMethod.getParameters();
    }

    @Override
    public List<Parameter> getSourceParameters() {
        return iterableMappingMethod.getSourceParameters();
    }

    @Override
    public List<Parameter> getContextParameters() {
        //TODO
        return List.of();
    }

    @Override
    public ParameterProvidedMethods getContextProvidedMethods() {
        return null;
    }

    @Override
    public Parameter getMappingTargetParameter() {
        //TODO
        return null;
    }

    @Override
    public boolean isObjectFactory() {
        return false;
    }

    @Override
    public Parameter getTargetTypeParameter() {
        return null;
    }

    @Override
    public Accessibility getAccessibility() {
        return iterableMappingMethod.getAccessibility();
    }

    @Override
    public Type getReturnType() {
        return iterableMappingMethod.getReturnType();
    }

    @Override
    public List<Type> getThrownTypes() {
        return iterableMappingMethod.getThrownTypes();
    }

    @Override
    public Type getResultType() {
        return null;
    }

    @Override
    public List<String> getParameterNames() {
        return iterableMappingMethod.getParameters().stream().map(Parameter::getName).toList();
    }

    @Override
    public boolean overridesMethod() {
        return false;
    }

    @Override
    public ExecutableElement getExecutable() {
        return null;
    }

    @Override
    public boolean isStatic() {
        return iterableMappingMethod.isStatic();
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public Type getDefiningType() {
        return null;
    }

    @Override
    public boolean isLifecycleCallbackMethod() {
        return false;
    }

    @Override
    public boolean isUpdateMethod() {
        return false;
    }

    @Override
    public MappingMethodOptions getOptions() {
        return null;
    }

    @Override
    public String describe() {
        return "";
    }

    @Override
    public List<Type> getTypeParameters() {
        return List.of();
    }
}
