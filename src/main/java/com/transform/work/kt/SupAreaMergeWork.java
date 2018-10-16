package com.transform.work.kt;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.transform.utils.ValChangeUtil;
import com.transform.work.AbstractWorker;
import com.transform.work.MergeWork;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static final String SUPERVISE_AREA = "uas_supervise_area_tmp";
    private static final String D_CODE = "d_code";

    @Override
    public boolean merge() {
        log.info("SupAreaMergeWork 任务开始 ======= ");
        String sql = SQLiteQueryBuilder.select("cno", "parentno", "cvalue", "extitem1").from(D_CODE).where("categoryno = '0008'").build();
        List<Map<String, Object>> ret = tt.queryForMapList(sql, null);
        List<Map<String, Object>> datas = new ArrayList<>();
        for (Map<String, Object> map : ret) {
            Map<String, Object> volVal = new HashMap<>();
            volVal.put("id", Long.valueOf(map.get("cno") + ""));
            volVal.put("parentId", ValChangeUtil.toLong(map.get("parentno"), null));
            volVal.put("name", map.get("cvalue"));
            volVal.put("pinyin", map.get("extitem1"));
            volVal.put("areaType", 1);
            if (StringUtils.isBlank(map.get("parentno") + "")) {
                volVal.put("level", 1);
            } else {
                Map<String,Object> pno = tt.queryFirst("select parentno from " + D_CODE + " where parentno = '" + Long.valueOf(map.get("parentno") + "") + "'");
                if (StringUtils.isBlank(pno.get("parentno") + "")) {
                    volVal.put("level", 2);
                } else {
                    volVal.put("level", 3);
                }
            }
            datas.add(volVal);
            // 单条出入效率低
            //tt.insert(SUPERVISE_AREA, volVal);
        }
        tt.batchInsert(SUPERVISE_AREA, datas);
        log.info("SupAreaMergeWork 任务完成 ======= 处理记录：%s", ret.size());
        return true;
    }

}
