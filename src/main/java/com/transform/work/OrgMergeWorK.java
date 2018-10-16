package com.transform.work;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
 * Created by tianhc on 2018/10/16.
 */
public class OrgMergeWorK {

    public boolean merge() {
        //select * from d_code where categoryno = '0010';

        String query = SQLiteQueryBuilder
                .select(MCS_COMPANY_INFO_COLUMNS)
                .from(MCS_COMPANY_INFO)
                .where("id = 1")
                .orderBy("rank")
                .desc()
                .limit(10)
                .offset(5)
                .build();


        return false;
    }

    private static final String MCS_COMPANY_INFO = "mcs_company_info";
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
