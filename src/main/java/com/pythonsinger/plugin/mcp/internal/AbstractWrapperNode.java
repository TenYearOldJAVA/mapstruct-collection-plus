package com.pythonsinger.plugin.mcp.internal;

import com.pythonsinger.plugin.mcp.annotations.ItemFieldMapping;
import com.pythonsinger.plugin.mcp.service.BasicConvertService;
import org.mapstruct.ap.internal.model.common.ModelElement;
import org.mapstruct.ap.internal.model.common.Type;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

/**
 * @author Zhihe
 */
public abstract class AbstractWrapperNode extends ModelElement {



    /**
     * 服务类名
     */
    private final String serviceName;

    /**
     * 主键类名
     */
    private String keyClassName;

    /**
     * 泛型类名
     */
    private String typeParameterClassName;

    /**
     * 泛型类
     */
    private TypeMirror typeParameterMirror;

    /**
     * 映射表名称
     */
    private final String mapName;

    /**
     * 服务类
     */
    private TypeMirror serviceMirror;

    public AbstractWrapperNode(ItemFieldMapping itemFieldMapping) {

        String name;
        try {
            // 直接访问 service 属性会导致 MirroredTypeException
            Class<? extends BasicConvertService<?,?>> service = itemFieldMapping.service();
            name = service.getName();
            this.serviceMirror = null;
        } catch (MirroredTypeException e) {
            // 抓取 MirroredTypeException 并获取 TypeMirror
            DeclaredType type = (DeclaredType) e.getTypeMirror();
            this.serviceMirror = type;
            // 获取直接父类
            TypeElement typeElement = (TypeElement) type.asElement();
            wrapTypeParameter(typeElement);

            String[] split = type.toString().split("\\.");
            name = split[split.length - 1];
        }
        serviceName = name.substring(0, 1).toLowerCase() + name.substring(1);
        mapName = serviceName.replace("Service", "Map");
    }

    private void wrapTypeParameter(TypeElement typeElement){
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for(TypeMirror typeMirror : interfaces) {
            if (typeMirror.getKind() == TypeKind.DECLARED) {
                DeclaredType type = (DeclaredType) typeMirror;
                TypeElement typeElementInterface = (TypeElement) type.asElement();
                // 检查接口是否是 BasicConvertService
                String convertClassName = typeElementInterface.getQualifiedName().toString();
                if (convertClassName.contains("com.pythonsinger.plugin.mcp.service")) {
                    List<? extends TypeMirror> typeArguments = type.getTypeArguments();
                    TypeMirror keyType = typeArguments.get(0);
                    String[] keyClass = keyType.toString().split("\\.");
                    this.keyClassName = keyClass[keyClass.length - 1];
                    TypeMirror valueType = typeArguments.get(1);
                    this.typeParameterMirror = valueType;
                    String[] typeClass = valueType.toString().split("\\.");
                    this.typeParameterClassName = typeClass[typeClass.length - 1];

                }
            }
        }
    }

//    private TypeMirror getTypeParameter(TypeElement typeElement){
//        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
//        for(TypeMirror typeMirror : interfaces) {
//            if (typeMirror.getKind() == TypeKind.DECLARED) {
//                DeclaredType type = (DeclaredType) typeMirror;
//                List<? extends TypeMirror> typeArguments = type.getTypeArguments();
//                return typeArguments.get(0);
//            }
//        }
//        return null;
//    }


    public String getKeyClassName() {
        return keyClassName;
    }

    public String getTypeParameterClassName() {
        return typeParameterClassName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMapName() {
        return mapName;
    }

    public TypeMirror getServiceMirror() {
        return serviceMirror;
    }

    public TypeMirror getTypeParameterMirror() {
        return typeParameterMirror;
    }

    @Override
    public Set<Type> getImportTypes() {
        return Set.of();
    }

}
