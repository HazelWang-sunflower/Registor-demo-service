package com.koi.api.entity;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public interface BaseData {
    default <V> V asViewObject(Class<V> vClass, Consumer<V> consumer){
        V v = this.asViewObject(vClass);
        consumer.accept(v);
        return v;
    }
    default <V> V asViewObject(Class<V> vClass){
        try {
           Field[] declaredFields = vClass.getDeclaredFields();
            Constructor<V> constructor = vClass.getConstructor();
            V v = constructor.newInstance();
            for(Field declaredField: declaredFields) {
                convert(declaredField, v);
            }
            return v;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void convert(Field field, Object vo) {
        try {
            Field source = this.getClass().getDeclaredField(field.getName());
            field.setAccessible(true);
            source.setAccessible(true);
            field.set(vo, source.get(this));
        } catch (IllegalAccessException | NoSuchFieldException ignored) {

        }
    }
}
