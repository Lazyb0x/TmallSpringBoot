package com.how2java.tmall.service;

import com.how2java.tmall.dao.UserDAO;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    /**
     * 用户列表的分页
     * @param start 第几页
     * @param size 每页几个用户
     * @param navigatepages 导航栏有几项
     * @return com.how2java.tmall.util.Page4Navigator
     */
    public Page4Navigator<User> list(int start, int size, int navigatepages){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatepages);
    }
}
