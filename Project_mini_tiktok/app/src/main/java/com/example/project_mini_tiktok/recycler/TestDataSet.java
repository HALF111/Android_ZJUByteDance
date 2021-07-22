package com.example.project_mini_tiktok.recycler;

import java.util.ArrayList;
import java.util.List;
import com.example.project_mini_tiktok.R;

public class TestDataSet {

    public static List<TestData> getData() {
        List<TestData> result = new ArrayList();
        result.add(new TestData("figure1", R.mipmap.figure1));
        result.add(new TestData("figure2", R.mipmap.figure2));
        result.add(new TestData("laba", R.mipmap.laba));
        result.add(new TestData("xuexi", R.mipmap.xuexi));
//        result.add(new TestData("暑期嘉年华", "285.6w"));
//        result.add(new TestData("2020年三伏天有40天", "183.2w"));
//        result.add(new TestData("会跟游客合照的老虎", "139.4w"));
//        result.add(new TestData("苏州暴雨", "75.6w"));
//        result.add(new TestData("6月全国菜价上涨", "55w"));
//        result.add(new TestData("猫的第六感有多强", "43w"));
//        result.add(new TestData("IU真好看", "22.2w"));
        return result;
    }

}
