package com.yao.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class CorpusUtil {
    public static void parse(String text,String folder){
        JSONObject corpus = (JSONObject) JSONObject.parse(text);
        Iterator<Map.Entry<String, Object>> it = corpus.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Object> entry = it.next();
            String cat = entry.getKey();
            String folder_cat = folder + cat + "/";
            JSONArray text_list = (JSONArray) entry.getValue();
            for(int t=0;t<text_list.size();t++){
                String corps_text = text_list.getString(t);
                String file_path = folder_cat  + String.format("%04d", t) + ".txt";
                FileUtil.writeString(file_path,corps_text);
            }
        }
    }
}
