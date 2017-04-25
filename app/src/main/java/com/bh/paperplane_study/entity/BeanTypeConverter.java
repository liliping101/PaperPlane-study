package com.bh.paperplane_study.entity;

import com.bh.paperplane_study.bean.BeanType;

import org.greenrobot.greendao.converter.PropertyConverter;

public class BeanTypeConverter implements PropertyConverter<BeanType, Integer> {
    @Override
    public BeanType convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        for (BeanType type : BeanType.values()) {
            if (type.id == databaseValue) {
                return type;
            }
        }
        return BeanType.DEFAULT;
    }

    @Override
    public Integer convertToDatabaseValue(BeanType entityProperty) {
        return entityProperty == null ? null : entityProperty.id;
    }
}
