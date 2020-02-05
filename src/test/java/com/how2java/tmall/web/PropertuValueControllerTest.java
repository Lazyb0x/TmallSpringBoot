package com.how2java.tmall.web;

import com.how2java.tmall.pojo.PropertyValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertuValueControllerTest {

    @Autowired
    private PropertuValueController propertuValueController;

    @Test
    public void list() throws Exception {
        List<PropertyValue> list = propertuValueController.list(2);
        System.out.println(list);
    }

    @Test
    public void update() {
    }
}