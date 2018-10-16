package com.transform.work;

import com.transform.config.TsMysqlTemplate;

import javax.annotation.Resource;

/**
 * Created by tianhc on 2018/10/16.
 */
public abstract class AbstractWorker {

    @Resource
    protected TsMysqlTemplate tt;
}
