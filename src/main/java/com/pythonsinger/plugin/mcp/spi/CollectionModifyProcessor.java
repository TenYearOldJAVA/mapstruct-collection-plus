package com.pythonsinger.plugin.mcp.spi;

import com.pythonsinger.plugin.mcp.util.ConvertMethod;
import com.pythonsinger.plugin.mcp.annotations.ItemFieldMapping;
import com.pythonsinger.plugin.mcp.internal.AbstractWrapperNode;
import com.pythonsinger.plugin.mcp.internal.ResourceField;
import com.pythonsinger.plugin.mcp.internal.WrapperMappingMethod;
import jakarta.annotation.Resource;
import org.mapstruct.ap.internal.gem.MappingConstantsGem;
import org.mapstruct.ap.internal.model.*;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.common.TypeFactory;
import org.mapstruct.ap.internal.model.source.MapperOptions;
import org.mapstruct.ap.internal.processor.ModelElementProcessor;
import org.mapstruct.ap.internal.util.ElementUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.*;

/**
 * 集合修饰过程
 *
 * @author Zhihe
 */
public class CollectionModifyProcessor implements ModelElementProcessor<Mapper, Mapper> {
    @Override
    public Mapper process(ProcessorContext context, TypeElement mapperTypeElement, Mapper sourceModel) {
        //校验是不是spring的
        MapperOptions mapperAnnotation = MapperOptions.getInstanceOn(mapperTypeElement, context.getOptions());
        String componentModel = mapperAnnotation.componentModel();
        if (!MappingConstantsGem.ComponentModelGem.SPRING.equalsIgnoreCase(componentModel)) {
            return sourceModel;
        }

        TypeFactory typeFactory = context.getTypeFactory();
        List<MappingMethod> methods = sourceModel.getMethods();
        Set<Field> fieldSet = new HashSet<>();
        int size = methods.size();
        ElementUtils elementUtils = context.getElementUtils();
        List<ExecutableElement> allEnclosedExecutableElements = elementUtils.getAllEnclosedExecutableElements(mapperTypeElement);
        Map<String,List<ItemFieldMapping>> map = new HashMap<>();

        allEnclosedExecutableElements.forEach(element -> {
            List<ItemFieldMapping> itemFieldMappings = getItemNameMappings(element);
            if(!itemFieldMappings.isEmpty()) {
                map.put(element.getSimpleName().toString(), itemFieldMappings);
            }
        });
        if(map.isEmpty()){
            return sourceModel;
        }

        for (int i = 0; i < size; i++) {
            MappingMethod mappingMethod = methods.get(i);
            if (mappingMethod instanceof IterableMappingMethod iterableMappingMethod) {
                String name = iterableMappingMethod.getName();
                List<ItemFieldMapping> annotation = map.get(name);
                if(annotation == null){
                    continue;
                }
                ConvertMethod convertMethod = new ConvertMethod(iterableMappingMethod);
                List<LifecycleCallbackMethodReference> beforeMappingReferences = new ArrayList<>();
                beforeMappingReferences.addAll(iterableMappingMethod.getBeforeMappingReferencesWithoutMappingTarget());
                beforeMappingReferences.addAll(iterableMappingMethod.getBeforeMappingReferencesWithMappingTarget());
                WrapperMappingMethod wrapperMappingMethod = new WrapperMappingMethod(convertMethod, new ArrayList<>(convertMethod.getParameterNames()),
                        beforeMappingReferences, iterableMappingMethod, annotation);
                wrapperMappingMethod.addImport(typeFactory.getType(Set.class));
                wrapperMappingMethod.addImport(typeFactory.getType(HashSet.class));
                wrapperMappingMethod.addImport(typeFactory.getType(Map.class));
                wrapperMappingMethod.addImport(typeFactory.getType(Resource.class));
                List<AbstractWrapperNode> nodeList = wrapperMappingMethod.getNodeList();
                for (AbstractWrapperNode node : nodeList) {
                    Type type = typeFactory.getType(node.getServiceMirror());
                    Type service = typeFactory.getType(node.getTypeParameterMirror());
                    wrapperMappingMethod.addImport(type);
                    wrapperMappingMethod.addImport(service);
                    ResourceField field = new ResourceField(type, node.getServiceName());
                    fieldSet.add(field);
                }
                methods.set(i, wrapperMappingMethod);
            }
        }
        List<Field> fields = sourceModel.getFields();
        fields.addAll(fieldSet);
        return sourceModel;
    }

    @Override
    public int getPriority() {
        return 1200;
    }

    private List<ItemFieldMapping> getItemNameMappings(Element typeElement) {
        return getItemNameMappings(typeElement, new HashSet<>());
    }

    private List<ItemFieldMapping> getItemNameMappings(Element typeElement, Set<Element> visited) {
        if (typeElement == null || visited.contains(typeElement)) {
            return List.of();
        }
        visited.add(typeElement);

        List<ItemFieldMapping> itemFieldMappings = new ArrayList<>(List.of(typeElement.getAnnotationsByType(ItemFieldMapping.class)));

        List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            DeclaredType annotationType = annotationMirror.getAnnotationType();
            Element element = annotationType.asElement();
            itemFieldMappings.addAll(getItemNameMappings(element, visited));
        }
        return itemFieldMappings;
    }

}
