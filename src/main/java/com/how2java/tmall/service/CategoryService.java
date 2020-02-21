package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    //start: 从0开始的页面索引（？），就是第几页
    //size： 每页含有几个项目
    //navivatePages 导航栏里面一行显示几个页码

    /**
     * 分类的分页
     *
     * @param start 第几页（从0开始数）
     * @param size 每页有几个项目
     * @param navigatePages 导航栏里面一行显示几个页码
     * @return 包含了 Page 的主要信息以及一个用来表示页码的数组
     */
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public void add(Category bean){
        categoryDAO.save(bean);
    }

    public void delete(int id){
        categoryDAO.delete(id);
    }

    public void update(Category bean){
        categoryDAO.save(bean);
    }

    public Category get(int id){
        Category bean = categoryDAO.findOne(id);
        return bean;
    }

    /**
     * 从分类里面的产品列表里面删除分类，防止转换成 json 对象的时候无穷遍历
     */
    public void removeCategoryFromProduct(List<Category> categories){
        for (Category category : categories){
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category){
        List<Product> products = category.getProducts();
        if (null!=products){
            for (Product product : products){
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null!=productsByRow){
            for (List<Product> ps : productsByRow){
                for (Product product : ps){
                    product.setCategory(null);
                }
            }
        }
    }
}
