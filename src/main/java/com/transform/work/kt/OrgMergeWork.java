package com.transform.work.kt;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.alexfu.sqlitequerybuilder.builder.SegmentBuilder;
import com.sun.scenario.effect.Merge;
import com.transform.jdbc.Ops;
import com.transform.work.AbstractWorker;
import com.transform.work.MergeWork;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 机构表迁移合并
 * kt/MCS_COMPANY_INFO + kt/MCS_HOSPITAL_INFO + kt/MCS_REGULATOR_INFO -> hx/uas_org_info
 *
 * Created by tianhc on 2018/10/16.
 */
@Service
public class OrgMergeWork extends AbstractWorker implements MergeWork{

    private static final String MCS_COMPANY_INFO = "mcs_company_info";
    private static final String MCS_HOSPITAL_INFO = "mcs_hospital_info";
    private static final String MCS_REGULATOR_INFO = "mcs_regulator_info";
    private static final String UAS_ORG_INFO = "uas_org_info_tmp";

    @Override
    public boolean merge() {
        //Map<String,Object> count = tt.queryFirst("select count(1) from " + MCS_COMPANY_INFO);
        //
        //int offset = 0;
        //int limit = 100;
        //SegmentBuilder sb = SQLiteQueryBuilder.select("*").from(MCS_COMPANY_INFO).limit(limit);
        //List<Map<String, Object>> ret = tt.queryForMapList(sql, null);


        return true;
    }

    private static final String[] MCS_COMPANY_INFO_COLUMNS = {
            "ENT_ID",
            "COMPID",
            "COMPNAME",
            "COMPNAME2",
            "ORGCODE",
            "COMPPY",
            "COMPWB",
            "REGCODE",
            "ISPROVINCE",
            "ADDRS",
            "COMPTYPE",
            "LICENCE",
            "REGCAP",
            "ESTDATE",
            "ENDDATE",
            "LEREP",
            "LINKMAN",
            "TEL",
            "FOX",
            "EMAIL",
            "POSTCODE",
            "REMARK",
            "USERNAME",
            "USERID",
            "CREATETIME",
            "LASTUPDATE",
            "ISDEL",
            "ISHOME",
            "DATA_NETWORK",
            "REGNOENDDATE",
            "DISRANGE",
            "LINKMAN2",
            "TEL2",
            "FILE_ORGCODE",
            "FILE_BUSLISCENSE",
            "FILE_TAXREG",
            "FILE_PERMIT",
            "SOCIALCODE",
            "COMBINED",
            "COMBINEDNAME",
            "LASTUPDATE_CGZX",
            "COMBINEDID",
            "UPDATE_COUNTER",
            "PRODUCT_CLASS",
            "FILE_OWNER",
            "FILE_AUTHORIZED",
            "FILE_INSURANCE",
            "FILE_OTHER",
            "FILE_INSTRUMENT",
            "FILE_INSTRUMENTCERT",
            "FILE_COMMITMENT",
            "DATA_PASS",
            "AUTHORIZED_ID",
            "IFTHREEINONE",
            "ENTTYPE",
            "AUTHORIZED_NAME",
            "AUTHORIZED_TEL",
            "CGZX_REMARK"

    };
}
