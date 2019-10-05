package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

//    @GetMapping("/categories")
//    public List<Category> list() throws Exception {
//        return categoryService.list();
//    }

    @GetMapping("/categories")
    public Page4Navigator<Category> list(
            @RequestParam(value = "start", defaultValue = "0")
            int start,
            @RequestParam(value = "size", defaultValue = "5")
            int size) throws Exception {
        start = start<0 ? 0 : start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request)
        throws  Exception{
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
        // 返回的bean的id在Hibernate插入数据库后便装配为数据库中的id,及自增长后的编号。
    }
    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
        throws IOException{
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, bean.getId()+".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)
        throws Exception{
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id+".jpg");
        file.delete();
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id, HttpServletRequest request)
        throws Exception{
        Category bean = categoryService.get(id);
        return bean;
    }

    @PutMapping("/categories/{id}")
    public Object update(MultipartFile image,Category bean,  HttpServletRequest request)
        throws Exception{
//        String name = request.getParameter("name");
//        int id = Integer.parseInt(request.getParameter("id"));

//        System.out.println(id + name);
//        bean.setName(name);
//        bean.setId(id);
//        System.out.println(bean.getId() + bean.getName());
        categoryService.update(bean);
        if (image!=null){
            saveOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }
}
