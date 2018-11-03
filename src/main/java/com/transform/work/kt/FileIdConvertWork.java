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
    //@Value("${kthc.file.convert.targetUrl:http://172.18.30.33:9645/dws/pub/upload}")
    @Value("${kthc.file.convert.targetUrl:http://172.18.30.33:9645/dws/pub/uploadForKt}")
    private String targetUrl;
    @Value("${kthc.file.convert.tmpFilePath:F:\\yelldo\\tmp\\temp\\}")
    private String tmpFilePathPre;
    // kt_file_id : hx_file_id 文件id是唯一的
    private static Map<String, String> ktHxFileIdMap = new HashMap<>();

    @Override
    public boolean convert() {
        // 加载已经处理过的文件
        List<Map<String, Object>> files = tt.queryForMapList("select kt_file_id,hx_file_id from ts_fileid_convert where success = true");
        for (Map<String, Object> map : files) {
            ktHxFileIdMap.put(map.get("kt_file_id") + "", map.get("hx_file_id") + "");
        }
        convertOrgInfo();
        //convertOrgApply();
        //convertOrgApplyHis();
        return true;
    }

    private void convertOrgInfo() {
        //Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO).where("isFileConvert = 0 and ts_deal_flag = 1 and kt_org_id = '5F7B1B351E7CE1ABE05346681BAC7889'").build()).get("count(1)");
        Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO).where("isFileConvert = 0 and ts_deal_flag = 1").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, 0);
        log.info("FileIdConvertWork-convertOrgInfo 任务开始 ======= total: {}", total);
        int limit = 1;
        //int offset = 0;
        long dealTotal = 0;
        while (true) {
            List<Map<String, Object>> lm = tt.query(SQL.select(//
                    "id",//
                    "kt_org_id",//
                    "buz_licence_file",//
                    "organization_file",//
                    "tax_file",//
                    "auth_file",//
                    "license_file",//
                    "product_cert_file",//
                    "bus_cert_file",//
                    "other_ref_cert_file",//
                    "auth_person_idcard_file",//
                    "application_file",//
                    "kt_product_cert_file",//
                    "legal_person_idcard_file",//
                    "social_insurance_file",//
                    "authorization_file",//
                    "authorization_cert_file",//
                    "kt_commitment_file"//
            ).from(UAS_ORG_INFO).where("isFileConvert = 0 and ts_deal_flag = 1").limit(limit)/*.offset(offset)*/.build());
            for (Map<String, Object> map : lm) {
                changeFileId(map, UAS_ORG_INFO);
            }
            int jobNum = lm.size();
            dealTotal += limit;
            log.info("FileIdConvertWork-convertOrgInfo 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            if (dealTotal >= total) {
                break;
            }
        }
        log.info("FileIdConvertWork-convertOrgInfo 任务结束 =======");
    }

    private int changeFileId(Map<String, Object> fileIdMap, String table) {
        StringBuilder sbuild = new StringBuilder();
        sbuild.append("update ").append(table).append(" set ");
        // 处理的记录主键
        Long recordId = null;
        String ktOrgId = null;
        for (Map.Entry<String, Object> entry : fileIdMap.entrySet()) {
            String key = entry.getKey(); // 文件字段
            Object obj = entry.getValue();  // 值
            if (obj != null && "id".equals(key)) {
                recordId = Long.valueOf(obj + "");
                continue;
            }
            if (obj != null && "kt_org_id".equals(key)) {
                ktOrgId = obj+"";
                continue;
            }
            if (!(obj instanceof String)) {
                continue;
            }
            String[] fileIdStrs = ((String) obj).split(",");
            StringBuilder sb = new StringBuilder();
            for (String fileId : fileIdStrs) {
                // 获取kt的文件id
                Map<String, Object> ktFile = tt.queryFirst(SQL.select("ATTACH_ID", "FILENAME", "REALNAME", "FILEPATH")//
                        .from(MCS_ATTACH_FILE).where("ATTACH_ID = ?").build(), fileId);
                if (ktFile != null) {
                    String ktFileId = ktFile.get("ATTACH_ID") + "";
                    // 已经处理过的fileId,直接用处理好的值
                    if (ktHxFileIdMap.containsKey(ktFileId)) {
                        // 转正确的fileId
                        if (ktHxFileIdMap.get(ktFileId) != null) {
                            sb.append(ktHxFileIdMap.get(ktFileId)).append(",");
                            continue;
                        }
                    }
                    String fpath = ktFile.get("FILEPATH") + "";
                    //String fname = ktFile.get("FILENAME") + "";
                    String fname = ktFile.get("REALNAME") + "";

                    JSONObject json = null;
                    try {
                        json = JSONObject.parseObject(HttpClientUtils.uploadFile(sourceUrlPre + fpath, targetUrl, tmpFilePathPre + fname));
                    } catch (IOException e) {
                        log.error("从kt文件上传到阿里云失败, ktFileId:{}, recordId:{}, field:{}, -----------{}", ktFileId, recordId, key, e);
                        //throw new TsException(String.format("从kt文件上传到阿里云失败, ktFileId:%s, recordId:%s, field:%s",ktFileId, recordId,key),e);
                    }
                    if (json != null && json.getBoolean("success") != null && json.getBoolean("success")) {
                        JSONObject content = json.getJSONObject("content");
                        String hxFileId = content.getString("id");
                        // 刚处理的fileId存放到map中复用，并持久化
                        Map<String, Object> ps = new HashMap<>();
                        ps.put("kt_file_id", ktFileId);
                        ps.put("hx_file_id", hxFileId);
                        ps.put("kt_org_id", ktOrgId);
                        ps.put("field_name", key);
                        ps.put("success", true);
                        tt.insert("ts_fileid_convert", ps);
                        ktHxFileIdMap.put(ktFileId, hxFileId);
                        sb.append(hxFileId).append(",");
                    } else {
                        // 上传失败
                        log.error("从kt文件上传到阿里云失败, ktFileId:{}, recordId:{}, field:{}", ktFileId, recordId, key);
                        Map<String, Object> ps = new HashMap<>();
                        ps.put("kt_file_id", ktFileId);
                        ps.put("kt_org_id", ktOrgId);
                        ps.put("field_name", key);
                        ps.put("success", false);
                        tt.insert("ts_fileid_convert", ps);
                        ktHxFileIdMap.put(ktFileId, null);
                        continue;
                    }
                }
            }
            // 去掉最后一个逗号","
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            // file_field = multi_file_id    xxx,xxx,xxx
            sbuild.append(entry.getKey()).append("='").append(sb).append("',");
        }
        sbuild.append("isFileConvert=1").append(" where id = ").append(recordId);
        return tt.update(sbuild.toString());
    }

    private void convertOrgApply() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO_MODIFY_APPLY).where("isFileConvert = 0 and ts_deal_flag = 1").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, 0);
        log.info("FileIdConvertWork-convertOrgApply 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        //int offset = 0;
        int limit = 1;
        while (true) {
            List<Map<String, Object>> lm = tt.query(SQL.select(//
                    "id",//
                    "kt_org_id",//
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
                    "license_file",//
                    "kt_product_cert_file",//
                    "legal_person_idcard_file",//
                    "social_insurance_file",//
                    "authorization_file",//
                    "authorization_cert_file",//
                    "kt_commitment_file"//
            ).from(UAS_ORG_INFO_MODIFY_APPLY).where("isFileConvert = 0 and ts_deal_flag = 1").limit(limit).build());
            for (Map<String, Object> map : lm) {
                changeFileId(map, UAS_ORG_INFO_MODIFY_APPLY);
            }
            int jobNum = lm.size();
            dealTotal += limit;
            log.info("FileIdConvertWork-convertOrgApply 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            if (dealTotal >= total) {
                break;
            }
        }
        log.info("FileIdConvertWork-convertOrgApply 任务结束 =======");
    }

    private void convertOrgApplyHis() {
        Object obj = tt.queryFirst(SQL.select("count(1)").from(UAS_ORG_INFO_MODIFY_APPLY_HIS).where("isFileConvert = 0 and ts_deal_flag = 1").build()).get("count(1)");
        int total = ValChangeUtils.toIntegerIfNull(obj, 0);
        log.info("FileIdConvertWork-convertOrgApplyHis 任务开始 ======= total: {}", total);
        long dealTotal = 0;
        int limit = 1;
        while (true) {
            List<Map<String, Object>> lm = tt.query(SQL.select(//
                    "id",//
                    "kt_org_id",//
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
                    "kt_product_cert_file",//
                    "legal_person_idcard_file",//
                    "authorization_file",//
                    "authorization_cert_file",//
                    "kt_commitment_file"//
            ).from(UAS_ORG_INFO_MODIFY_APPLY_HIS).where("isFileConvert = 0 and ts_deal_flag = 1").limit(limit).build());
            for (Map<String, Object> map : lm) {
                changeFileId(map, UAS_ORG_INFO_MODIFY_APPLY_HIS);
            }
            int jobNum = lm.size();
            dealTotal += limit;
            log.info("FileIdConvertWork-convertOrgApplyHis 处理中 ======= 处理记录：{},已处理记录：{},完成度：{}", jobNum, dealTotal, CalculateUtils.percentage(dealTotal, total));
            if (dealTotal >= total) {
                break;
            }
        }
        log.info("FileIdConvertWork-convertOrgApplyHis 任务结束 =======");
    }

}
