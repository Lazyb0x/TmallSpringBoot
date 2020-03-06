package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public List<OrderItem> listByOrder(Order order){
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    /**
     * 获取并填充订单里面的订单项
     * @param orders 订单的列表
     */
    public void fill(List<Order> orders){
        for (Order order : orders){
            fill(order);
        }
    }

    public void fill(Order order){
        List<OrderItem> orderItems = listByOrder(order);
        float totalPrice = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems){
            totalPrice = oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setOrderItems(orderItems);
        order.setTotal(totalPrice);
        order.setTotalNumber(totalNumber);
    }

    public void add(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }

    public void delete(int id){
        orderItemDAO.delete(id);
    }

    public void update(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }

    public OrderItem get(int id){
        return orderItemDAO.findOne(id);
    }

    public int getSaleCount(Product product){
        List<OrderItem> ois = listByProduct(product);
        int result = 0;
        for (OrderItem oi : ois){
            if (null!=oi.getOrder() && null!=oi.getOrder().getPayDate());
            result += oi.getNumber();
        }
        return result;
    }

    public List<OrderItem> listByProduct(Product product){
        return orderItemDAO.findByProduct(product);
    }

    public List<OrderItem> listByUser(User user){
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }
}
