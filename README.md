# mapstruct-collection-plus

用于mps增强集合类转换

## TODOLIST
1. ConvertMethod优化成Builder
2. fieldWrapper支持source和target类型不匹配时转换
3. 支持部分原生功能，如condition和expression
4. 拆包将运行级和编译级的分开

## 快速开始

### 1.引入依赖包

```xml

<dependency>
    <groupId>com.pythonsinger.plugin</groupId>
    <artifactId>mapstruct-collection-plus</artifactId>
</dependency>
```

### 2.定义名称转换器
名称转换器必须被Spring管理，且必须实现NameConvertService<T>的转换接口
```java
public interface MemberService extends BasicConvertService<Long,String> {
}

@Service
public class MemberServiceImpl implements MemberService {


    @DubboReference
    private MemberDubboService memberDubboService;

    @Override
    public Map<Long, String> getNameMapByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<MemberListDTO> memberListByIds = memberDubboService.getMemberListByIds(ids.stream().map(Long::intValue).distinct().collect(Collectors.toList()));
        return memberListByIds.stream().collect(
                Collectors.toMap(
                        item -> item.getMemberId().longValue(),
                        MemberListDTO::getRealName));
    }

}
```

### 2.设置转换策略
将Mapper的componentModel改为spring，并用ItemNameMapping修饰List转换的Method

```java
@Mapper(componentModel="spring")
public interface SupplierConfigConvert {

    @ItemNameMapping(keyField ="updateBy",target = "updateByName",service = MemberService.class)
    List<SupplierConfigBO> convertToBoList(List<SupplierConfigEntity> list);

}
```

### 3.放入对应的需要名称映射的方法中

```java
@Override
public List<SupplierConfigBO> list(SupplierConfigQueryParams reqVO) {
    QueryWrapper queryWrapper = QueryConditionBuilder.toWrapper(reqVO);
    List<SupplierConfigEntity> list = iSupplierConfigSerive.list(queryWrapper);
    return supplierConfigConvert.convertToBoList(list);
}
```

### 4.查看convert的编译结果

```java
@Component
public class SupplierConfigConvertImpl implements SupplierConfigConvert {

    @Resource
    private MemberService memberService;

    @Override
    public SupplierConfigBO convertToBo(SupplierConfigEntity supplierConfigEntity) {
        if ( supplierConfigEntity == null ) {
            return null;
        }

        SupplierConfigBO supplierConfigBO = new SupplierConfigBO();

        supplierConfigBO.setId( supplierConfigEntity.getId() );
        supplierConfigBO.setStatus( supplierConfigEntity.getStatus() );
        supplierConfigBO.setCreatedBy( supplierConfigEntity.getCreatedBy() );
        supplierConfigBO.setUpdatedBy( supplierConfigEntity.getUpdatedBy() );
        supplierConfigBO.setCreatedAt( supplierConfigEntity.getCreatedAt() );
        supplierConfigBO.setUpdatedAt( supplierConfigEntity.getUpdatedAt() );

        return supplierConfigBO;
    }

    @Override
    public List<SupplierConfigBO> convertToBoList(List<SupplierConfigEntity> list) {
        if ( list == null || list.isEmpty()) {
            return null;
        }

        List<SupplierConfigBO> list1 = new ArrayList<SupplierConfigBO>( list.size() );
        Set<Long> arr0 = new HashSet<>();
        for ( SupplierConfigEntity supplierConfigEntity : list ) {
            if (supplierConfigEntity.getUpdatedBy()!=null){
                arr0.add(supplierConfigEntity.getUpdatedBy());
            }
        }
        Map<Long, String> memberMap = memberService.getNameMapByIds(arr0);

        for ( SupplierConfigEntity supplierConfigEntity : list ) {
            SupplierConfigBO tempVar = convertToBo( supplierConfigEntity );
            if (supplierConfigEntity.getUpdatedBy()!=null){
                tempVar.setUpdatedByName(memberMap.get(supplierConfigEntity.getUpdatedBy()));
            }
            list1.add(tempVar);
        }

        return list1;
    }
}

```

## field支持
如果NameConvertService的泛型非基本类，而是一个业务Class，可通过ItemNameMapping中的source字段指定目标field需要映射表中的哪一个field。
