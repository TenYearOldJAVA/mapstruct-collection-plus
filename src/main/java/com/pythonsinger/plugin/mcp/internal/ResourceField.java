package com.pythonsinger.plugin.mcp.internal;

import org.mapstruct.ap.internal.model.Field;
import org.mapstruct.ap.internal.model.common.Type;

import java.util.Objects;

/**
 * @author Zhihe
 */
public final class ResourceField extends Field {

    public ResourceField(Type type, String variableName) {
        super(type, variableName,true);
    }

    @Override
    public boolean equals(Object o) {
        // 1. 判断对象是否为同一个
        if (this == o) {
            return true;
        }

        // 2. 判断传入对象是否为 ResourceField 类型
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        // 3. 强制转换为 ResourceField 类型
        ResourceField that = (ResourceField) o;

        // 4. 通过 variableName 字段判断相等性（忽略大小写）
        return Objects.equals(
                this.getVariableName() != null ? this.getVariableName().toLowerCase() : null,
                that.getVariableName() != null ? that.getVariableName().toLowerCase() : null
        );
    }

    @Override
    public int hashCode() {
        // 通过 variableName 的值（忽略大小写）计算哈希值
        return this.getVariableName() != null
                ? Objects.hash(this.getVariableName().toLowerCase())
                : 0;
    }


    @Override
    public String toString() {
        return "ResourceField { variableName = " + this.getVariableName() + " }";
    }

}
