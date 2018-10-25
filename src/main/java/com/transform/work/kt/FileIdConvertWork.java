package com.transform.work.kt;

import com.alibaba.fastjson.JSONObject;
import com.transform.jdbc.SQL;
import com.transform.util.*;
import com.transform.work.AbstractWorker;
import com.transform.work.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件迁移
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class FileIdConvertWork extends AbstractWorker implements Converter {

    @Value("${kthc.file.convert.sourceUrlPre:http://120.35.29.87:8082/fjmbid_upload_file/}")
    private String sourceUrlPre;
    @Value("${kthc.file.convert.targetUrl:http://172.18.30.33:9645/dws/pub/upload}")
    private String targetUrl;
    @Value("${kthc.file.convert.tmpFilePath:F:\\yelldo\\tmp\\temp\\}")
    private String tmpFilePathPre;
    // kt_file_id : hx_file_id 文件id是唯一的
    private static Map<String, String> ktHxFileIdMap = new HashMap<>();

    @Override
    public boolean convert() {
        convertOrgInfo();
        //convertOrgApply();
        //convertOrgApplyHis();
        return true;
    }

    private void convertOrgInfo() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO).where("isFileConvert = 0").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, 0);
        log.info("FileIdConvertWork-convertOrgInfo 任务开始 ======= total: {}", total);
        int limit = 100;
        int offset = 0;
        long dealTotal = 0;
        while (true) {
            List<Map<String, Object>> lm = tt.query(SQL.select(//
                    "id",//
                    "buz_licence_file",//
                    "organization_file",//
                    "organization_file",//
                    "tax_file",//
                    "auth_file",//
                    "license_file",//
                    "product_cert_file",//
                    "bus_cert_file",//
                    "other_ref_cert_file",//
                    "auth_person_idcard_file",//
                    "auth_person_idcard2_file",//
                    "application_file",//
                    "kt_product_cert_file",//
                    "legal_person_idcard_file",//
                    "kt_auth_person_idcard_file",//
                    "social_insurance_file",//
                    "authorization_file",//
                    "authorization_cert_file",//
                    "kt_commitment_file"//
            ).from(UAS_ORG_INFO).where("isFileConvert = 0").limit(limit).offset(offset).build());
            for (Map<String, Object> map : lm) {
                changeFileId(map);
            }
            int jobNum = lm.size();
            dealTotal += jobNum;
            log.info("FileIdConvertWork-convertOrgInfo 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("FileIdConvertWork-convertOrgInfo 任务结束 =======");
    }

    private int changeFileId(Map<String, Object> fileIdMap) {
        StringBuilder sbuild = new StringBuilder();
        sbuild.append("update ").append(UAS_ORG_INFO).append(" set ");
        // 处理的记录主键
        Long recordId = null;
        for (Map.Entry<String, Object> entry : fileIdMap.entrySet()) {
            Object obj = entry.getValue();
            String key = entry.getKey();
            if (obj != null && "id".equals(key)) {
                recordId = Long.valueOf(obj + "");
                continue;
            }
            if (!(obj instanceof String)) {
                continue;
            }
            String[] fileIdStrs = ((String) obj).split(",");
            StringBuilder sb = new StringBuilder();
            for (String fileId : fileIdStrs) {
                // 获取kt的文件id
                Map<String, Object> ktFile = tt.queryFirst(SQL.select("ATTACH_ID", "FILENAME", "FILEPATH")//
                        .from(MCS_ATTACH_FILE).where("ATTACH_ID = ?").build(), fileId);
                if (ktFile != null) {
                    String ktFileId = ktFile.get("ATTACH_ID") + "";
                    if (ktHxFileIdMap.containsKey(ktFileId)) {
                        // 已经处理过的fileId,直接用处理好的值
                        sb.append(ktHxFileIdMap.get(ktFileId)).append(",");
                        continue;
                    }
                    String fpath = ktFile.get("FILEPATH") + "";
                    String fname = ktFile.get("FILENAME") + "";
                    JSONObject json = null;
                    try {
                        json = JSONObject.parseObject(HttpClientUtils.uploadFile(sourceUrlPre + fpath, targetUrl, tmpFilePathPre + fname));
                        System.out.println(json);
                    } catch (IOException e) {
                        log.error("从kt文件上传到阿里云失败, ktFileId:{}, recordId:{}, field:{}, -----------{}", ktFileId, recordId, key, e);
                    }
                    if (json.getBoolean("success")) {
                        JSONObject content = json.getJSONObject("content");
                        String hxFileId = content.getString("id");
                        ktHxFileIdMap.put(ktFileId, hxFileId);
                        sb.append(hxFileId).append(",");
                    }
                }
            }
            sbuild.append(entry.getKey()).append("='").append(sb.substring(0, sb.length() - 1)).append("',");
        }
        sbuild.append("isFileConvert=1").append(" where id = ").append(recordId);
        return tt.update(sbuild.toString());
    }

    private void convertOrgApply() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO_MODIFY_APPLY).where("isFileConvert = 0").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, 0);
        log.info("FileIdConvertWork-convertOrgApply 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        int offset = 0;
        int limit = 100;
        while (true) {
            List<Map<String, Object>> lm = tt.query(SQL.select(//
                    "id",//
                    "buz_licence_file",//
                    "audit_desc_file",//
                    "buz_licence_file",//
                    "organization_file",//
                    "tax_file",//
                    "auth_file",//
                    "product_cert_file",//
                    "bus_cert_file",//
                    "application_file",//
                    "other_ref_cert_file",//
                    "auth_person_idcard_file",//
                    "auth_person_idcard2_file",//
                    "license_file",//
                    "kt_product_cert_file",//
                    "legal_person_idcard_file",//
                    "kt_auth_person_idcard_file",//
                    "social_insurance_file",//
                    "authorization_file",//
                    "authorization_cert_file",//
                    "kt_commitment_file"//
            ).from(UAS_ORG_INFO).where("isFileConvert = 0").limit(limit).offset(offset).build());
            for (Map<String, Object> map : lm) {
                changeFileId(map);
            }
            int jobNum = lm.size();
            dealTotal += jobNum;
            log.info("FileIdConvertWork-convertOrgApply 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("FileIdConvertWork-convertOrgApply 任务结束 =======");
    }

    private void convertOrgApplyHis() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO_MODIFY_APPLY_HIS).where("isFileConvert = 0").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, 0);
        log.info("FileIdConvertWork-convertOrgApplyHis 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        int offset = 0;
        int limit = 300;
        while (true) {
            List<Map<String, Object>> lm = tt.query(SQL.select(//
                    "id",//
                    "buz_licence_file",//
                    "audit_desc_file",//
                    "buz_licence_file",//
                    "organization_file",//
                    "tax_file",//
                    "auth_file",//
                    "product_cert_file",//
                    "bus_cert_file",//
                    "application_file",//
                    "social_insurance_file",//
                    "other_ref_cert_file",//
                    "auth_person_idcard_file",//
                    "auth_person_idcard2_file",//
                    "kt_product_cert_file",//
                    "legal_person_idcard_file",//
                    "kt_auth_person_idcard_file",//
                    "authorization_file",//
                    "authorization_cert_file",//
                    "kt_commitment_file"//
            ).from(UAS_ORG_INFO).where("isFileConvert = 0").limit(limit).offset(offset).build());
            for (Map<String, Object> map : lm) {
                changeFileId(map);
            }
            int jobNum = lm.size();
            dealTotal += jobNum;
            log.info("FileIdConvertWork-convertOrgApplyHis 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            offset += limit;
            if (offset >= total) {
                break;
            }
        }
        log.info("FileIdConvertWork-convertOrgApplyHis 任务结束 =======");
    }

}