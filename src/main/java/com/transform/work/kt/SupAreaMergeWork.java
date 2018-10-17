package com.transform.work.kt;

import com.transform.jdbc.Ops;
import com.transform.jdbc.SQL;
import com.transform.util.StrUtils;
import com.transform.util.ValChangeUtils;
import com.transform.work.AbstractWorker;
import com.transform.work.MergeWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监管片区表迁移 kt/D_CODE -> hx/uas_supervise_area_tmp
 * Created by tianhc on 2018/10/16.
 */
@Slf4j
@Service
public class SupAreaMergeWork extends AbstractWorker implements MergeWork {

    @Override
    public boolean merge() {

        String countSql = SQL.select("count(1)").from(D_CODE).where("categoryno = '0008'").build();
        int total = Integer.valueOf(tt.queryFirst(countSql).get("count(1)") + "");
        log.info("SupAreaMergeWork 任务开始 ======= total: {}", total);
        int offset = 0;
        int limit = 100;
        while (true) {
            oneBatchProcessing(offset, limit);
            offset += limit;
            if (offset > total) {
                break;
            }
        }
        log.info("SupAreaMergeWork 任务结束 =======");
        return true;
    }

    private void oneBatchProcessing(int offset, int limit) {

        String sql = SQL.select("cno", "parentno", "cvalue", "extitem1")//
                .from(D_CODE).where("categoryno = '0008'").limit(limit).offset(offset).build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql, null);
        List<Map<String, Object>> datas = new ArrayList<>();
        for (Map<String, Object> map : ret) {
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("id", Long.valueOf(map.get("cno") + ""));
            volVal.put("parentId", ValChangeUtils.toLong(map.get("parentno"), null));
            volVal.put("name", map.get("cvalue"));
            volVal.put("pinyin", map.get("extitem1"));
            volVal.put("areaType", 1);
            if (StrUtils.isBlankOrNullVal(map.get("parentno"))) {
                volVal.put("level", 1);
            } else {
                Ops ops = Ops.get().eq("cno", map.get("parentno"));
                String sql2 = SQL.select("parentno").from(D_CODE).where(ops.str()).build();
                Map<String, Object> pno = tt.queryFirst(sql2, ops.vals());
                if (StrUtils.isBlankOrNullVal(pno.get("parentno"))) {
                    volVal.put("level", 2);
                } else {
                    volVal.put("level", 3);
                }
            }
            datas.add(volVal);
            // 单条出入效率低
            //tt.insert(SUPERVISE_AREA_TMP, volVal);
        }
        tt.batchInsert(SUPERVISE_AREA_TMP, datas);
        log.info("SupAreaMergeWork 处理中 ======= 处理记录：{}", ret.size());
    }

}
