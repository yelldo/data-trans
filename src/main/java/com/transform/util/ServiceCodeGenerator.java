package com.transform.util;

import com.transform.config.TsMysqlTemplate;
import com.transform.exception.TsException;
import com.transform.work.AbstractWorker;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by tianhc on 2018/10/22.
 */
@Component
public class ServiceCodeGenerator{

    /**
     * 生成企业编码
     * @param orgType
     * @param orgId
     * @return
     */
    public static String generateOrgCode(Integer orgType, Long orgId) {
        if (orgType == null || orgId == null) {
            throw new TsException("参数不能为空");
        }
        // 生成机构编码（1000001，总共8位，机构类型+补零+机构id）
        StringBuffer orgCode = new StringBuffer(orgType + "");
        int size = (orgId + "").length();
        for(int i = 0; i<7-size; i++) {
            orgCode.append("0");
        }
        return orgCode.append(orgId).toString();
    }

}
