package com.example.sparkchaindemo.ai.asr;

import com.example.sparkchaindemo.adapter.ParamInfo;

import java.util.ArrayList;
import java.util.List;

public class asrParams {
    public static List<ParamInfo> getLanguage(){
        List<ParamInfo> typeList = new ArrayList<>();
        typeList.add(new ParamInfo("zh_cn","Chinese"));
        typeList.add(new ParamInfo("en_us","English"));
        return typeList;
    }
}
