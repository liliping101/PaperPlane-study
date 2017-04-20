package com.bh.paperplane_study.entity;

import com.bh.paperplane_study.bean.BeanType;

import org.greenrobot.greendao.converter.PropertyConverter;

public class BeanTypeConverter implements PropertyConverter<BeanType, String> {
    @Override
    public BeanType convertToEntityProperty(String databaseValue) {
        return BeanType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(BeanType entityProperty) {
        return entityProperty.name();
    }
}
