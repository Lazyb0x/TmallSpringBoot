package com.how2java.tmall.web;

import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    PropertyValueService propertyValueService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    OrderItemService orderItemService;

    @GetMapping("/forehome")
    public Object home(){
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist){
            String message = "用户名已经被使用，不能使用！";
            return Result.fail(message);
        }

        user.setPassword(password);
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session){
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user = userService.get(name, userParam.getPassword());
        if (null==user){
            String message = "账号或密码错误！";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            return Result.success();
        }
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid){
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages =
                productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages =
                productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String,Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @GetMapping("forecheckLogin")
    public Object checkLogin(HttpSession session){
        User user = (User)session.getAttribute("user");
        if (null!=user){
            return Result.success();
        }
        return Result.fail("未登录");
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid, String sort){
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);
        Comparator<Product> productComparator = null;

        if (null != sort && !sort.equals("null")) {
            switch (sort) {
                case "review":
                    productComparator = Comparator.comparing(Product::getReviewCount);
                    break;
                case "date":
                    productComparator = Comparator.comparing(Product::getCreateDate);
                    break;
                case "saleCount":
                    productComparator = Comparator.comparing(Product::getSaleCount);
                    break;
                case "price":
                    productComparator = Comparator.comparing(Product::getPromotePrice);
                    break;
                case "all":
                    productComparator = Comparator.comparing(
                            p -> p.getReviewCount() * p.getSaleCount()
                    );
                    break;
            }
            Collections.sort(c.getProducts(), productComparator);
        }

        return c;
    }

    @PostMapping("foresearch")
    public Object search(String keyword){
        if(null==keyword){
            keyword = "";
        }
        List<Product> ps = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session){
        return buyOneAndAddCart(pid, num, session);
    }

    private int buyOneAndAddCart(int pid, int num, HttpSession session){
        Product product = productService.get(pid);
        int oiid = 0;

        User user = (User)session.getAttribute("user");
        boolean isFound = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois){
            if (oi.getProduct().getId()==pid){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                isFound = true;
                oiid = oi.getId();
                break;
            }
        }

        if(!isFound){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(product);
            orderItemService.add(oi);
            oiid = oi.getId();
        }

        return oiid;
    }

    @GetMapping("forebuy")
    public Object buy(String[] oiid, HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid){
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session){
        buyOneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("forecart")
    public Object cart(HttpSession session){
        User user = (User)session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }
}
