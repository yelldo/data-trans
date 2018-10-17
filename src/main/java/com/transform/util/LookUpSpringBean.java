package com.transform.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tianhc on 2018/10/16.
 */
@RestController
@RequestMapping("utils/springBean")
public class LookUpSpringBean implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    @RequestMapping("list")
    public List springBeanList() {
        String[] beans = applicationContext.getBeanDefinitionNames();
        return Arrays.asList(beans);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
