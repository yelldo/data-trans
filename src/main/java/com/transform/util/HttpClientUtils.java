package com.transform.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * 文件下载和上传工具类
 * Created by tianhc on 2018/10/24.
 */

@Slf4j
public class HttpClientUtils {

    public static void main(String[] args) throws IOException {
        //String sourceUrl = "http://120.35.29.87:8082/fjmbid_upload_file/UploadFiles/201806/p1cge40iigqs21kdf12ap1n2h7729.edc";
        String sourceUrl = "http://120.35.29.87:8082/fjmbid_upload_file/UploadFiles/201807/p1cja56tae1ucg1fc01a6v17o1q0do.edc";
        String targetUrl = "http://172.18.30.33:9645/dws/pub/upload";
        //String tmpFilePath = "F:\\yelldo\\tmp\\p1cge40iigqs21kdf12ap1n2h7729.edc";
        String tmpFilePath = "F:\\yelldo\\tmp\\p1cja56tae1ucg1fc01a6v17o1q0do.edc";
        String content = uploadFile(sourceUrl, targetUrl, tmpFilePath);
        System.out.println(content);
        JSONObject json = JSONObject.parseObject(content);
        JSONObject json2 = json.getJSONObject("content");
        if (json.getBoolean("success")) {
            System.out.println("成功：" + json2.getString("id"));
        } else {
            System.out.println("失败...");
        }
    }

    public static String uploadFile(String sourceUrl, String targetUrl, String tmpFilePath) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(sourceUrl);
        CloseableHttpResponse resp = httpclient.execute(httpGet);
        try {
            //HttpEntity entity = resp.getEntity();
            byte[] bytes = EntityUtils.toByteArray(resp.getEntity());
            HttpPost httpPost = new HttpPost(targetUrl);
            File file = new File(tmpFilePath);
            //FileUtils.copyInputStreamToFile(entity.getContent(), file);
            FileUtils.writeByteArrayToFile(file,bytes);
            FileBody bin = new FileBody(file);
            StringBody project = new StringBody("uas", ContentType.create("text/plain", Consts.UTF_8));
            StringBody filestore = new StringBody("fs", ContentType.create("text/plain", Consts.UTF_8));
            HttpEntity reqEntity = MultipartEntityBuilder.create()//
                    .addPart("file", bin)//
                    .addPart("project", project)//
                    .addPart("filestore", filestore)//
                    .build();
            httpPost.setEntity(reqEntity);
            resp = httpclient.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            String content = EntityUtils.toString(entity, Charset.forName("UTF-8"));
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            log.warn("文件转换失败，ktFileId:{},errorStack:{}", sourceUrl, e);
        } finally {
            resp.close();
            // 删除临时文件
            deleteFile(tmpFilePath);
        }
        return null;
    }

    /**
     * 删除文件，提供绝对路径
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除文件夹，提供绝对路径
     *
     * @param sPath
     * @return
     */
    public static boolean deleteDir(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件夹且不为空则进行删除
        if (file.isDirectory() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

}
