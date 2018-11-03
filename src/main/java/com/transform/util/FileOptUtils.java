package com.transform.util;

import java.io.*;

/**
 * Created by tianhc on 2018/11/2.
 */
public class FileOptUtils {

    public static void write(String path, String content, String encoding)
            throws IOException {
        File file = new File(path);
        file.delete();
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), encoding));
        writer.write(content);
        writer.close();
    }

    public static String read(File file, String encoding) throws IOException {
        String content = "";
        //File file = new File(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), encoding));
        String line = null;
        while ((line = reader.readLine()) != null) {
            content += line + "\n";
        }
        reader.close();
        return content;
    }

    public static void main(String[] args) throws IOException {
        String content = "中文内容";
        String path = "f:/test-yelldo.txt";
        String encoding = "utf-8";
        FileOptUtils.write(path, content, encoding);
        //System.out.println(FileOptUtils.read(path, encoding));
    }
}
