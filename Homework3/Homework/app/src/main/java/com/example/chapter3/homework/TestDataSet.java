package com.example.chapter3.homework;

import java.util.ArrayList;
import java.util.List;

public class TestDataSet {

    public static List<TestData> getData() {
        List<TestData> result = new ArrayList();
        result.add(new TestData("明天几点走？", "刚刚", "阿红"));
        result.add(new TestData("[Hi]", "15:07", "小张"));
        result.add(new TestData("转发[视频]", "15:07","小明"));
        result.add(new TestData("冲冲冲", "14:55","爸爸"));
        result.add(new TestData("我也沉了hhh", "14:33","小王"));
        result.add(new TestData("[捂脸哭][捂脸哭]", "14:01","阮哥"));
        result.add(new TestData("五角星？", "12:09","阿万"));
        result.add(new TestData("一起干！", "10:29","颜哥"));
        result.add(new TestData("转发[6月全国菜价上涨]", "10:13","妈妈"));
        result.add(new TestData("转发[猫的第六感有多强]", "9:12","妈妈"));
        result.add(new TestData("IU真好看~", "9:10","小赵"));
        return result;
    }

}
