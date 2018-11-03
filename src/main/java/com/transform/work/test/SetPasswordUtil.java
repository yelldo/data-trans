package com.transform.work.test;

import com.transform.config.TsMysqlTemplate;
import com.transform.jdbc.SQL;
import com.transform.util.CalculateUtils;
import com.transform.util.ServiceCodeGenerator;
import com.transform.util.StrUtils;
import com.transform.util.ValChangeUtils;
import com.transform.work.AbstractWorker;
import com.transform.work.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业信息表迁移合并
 * kt/MCS_COMPANY_INFO + kt/MCS_HOSPITAL_INFO + kt/MCS_REGULATOR_INFO -> hx/uas_org_info
 * <p>
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class SetPasswordUtil implements Converter {

    @Resource
    protected TsMysqlTemplate tt;

    @Override
    public boolean convert() {
        //设置管理单位的密码
        List<Map<String, Object>> regulators = tt.query("select * from sys_n_users where OPTYPE = 6 and ENABLED = 1");
        for (Map<String, Object> map : regulators) {
            String opno = map.get("OPNO") + "";
            String pwd = DigestUtils.md5Hex(DigestUtils.md5Hex(opno) + DigestUtils.md5Hex("hx123456"));
            tt.update("update sys_n_users set PWD = ? where OPNO = ?", pwd, opno);
        }
        return true;
    }

}
