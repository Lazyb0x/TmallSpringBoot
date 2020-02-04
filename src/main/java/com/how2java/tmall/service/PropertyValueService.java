package com.how2java.tmall.service;

import com.how2java.tmall.dao.PropertyValueDAO;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueService {
    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    public void update(PropertyValue bean){
        propertyValueDAO.save(bean);
    }

    /**
     * 通过产品和属性获得属性值
     */
    public PropertyValue getByPropertyAndProduct(Product product, Property property){
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    /**
     * 通过产品获得属性值列表
     */
    public List<PropertyValue> list(Product product){
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    /**
     * 初始化产品的所有属性值
     */
    public void init(Product product) {
        //列出属于商品所属分类的所有属性
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        for (Property property : properties){
            PropertyValue propertyValue = this.getByPropertyAndProduct(product, property);
            if (propertyValue==null){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);

            }
        }
    }


}
