package com.janson.netty.demo.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: Janson
 * @Date: 2022/3/12 17:37
 **/
public class UserFilterDemo {

    public static void main(String[] args) {

        List<String> total = new ArrayList<>();
        List<String> exits = new ArrayList<>();

        Set<String> totalSet = new HashSet<>();
        Set<String> exitsSet = new HashSet<>();

        String s = "1686864262,\n" +
                "9029351,\n" +
                "527456040,\n" +
                "9680616,\n" +
                "1471635879,\n" +
                "6270618,\n" +
                "17874082,\n" +
                "15197706,\n" +
                "15753691,\n" +
                "10872228,\n" +
                "18925402,\n" +
                "8822245,\n" +
                "4396800,\n" +
                "10829139,\n" +
                "5268110,\n" +
                "10797064,\n" +
                "10841917,\n" +
                "6120395,\n" +
                "10119094,\n" +
                "10871434,\n" +
                "10788681,\n" +
                "10893093,\n" +
                "4570302,\n" +
                "10873096,\n" +
                "6217382,\n" +
                "15074626,\n" +
                "12828847,\n" +
                "9615545,\n" +
                "1564300967,\n" +
                "1281249369,\n" +
                "207265053,\n" +
                "447910099,\n" +
                "973003259,\n" +
                "809352048,\n" +
                "1972339667,\n" +
                "1597205593,\n" +
                "1775016700,\n" +
                "4657239,\n" +
                "10522246,\n" +
                "6380480,\n" +
                "6380480,\n" +
                "6380480,\n" +
                "10114116,\n" +
                "10055235,\n" +
                "6319146,\n" +
                "6058911,\n" +
                "9586351,\n" +
                "1500353319,\n" +
                "1553338071,\n" +
                "1553338071,\n" +
                "10279056,\n" +
                "10279056,\n" +
                "10056682,\n" +
                "9500231,\n" +
                "6617136,\n" +
                "9748380,\n" +
                "10056682,\n" +
                "13715829,\n" +
                "1553338071,\n" +
                "14622766,\n" +
                "1541218741,\n" +
                "820821209,\n" +
                "13638446,\n" +
                "8397231,\n" +
                "1690934228,\n" +
                "18894264,\n" +
                "18783626,\n" +
                "1175661398,\n" +
                "1435743277,\n" +
                "6238870,\n" +
                "867238919,\n" +
                "489353649,\n" +
                "1234813987,\n" +
                "124580903,\n" +
                "10124793,\n" +
                "3608150,\n" +
                "9135497,\n" +
                "11241789,\n" +
                "15020840,\n" +
                "16318553,\n" +
                "379433060,\n" +
                "5189229,\n" +
                "11555603,\n" +
                "1269095,\n" +
                "5086695,\n" +
                "4855468,\n" +
                "5397660,\n" +
                "11878395,\n" +
                "11449539,\n" +
                "10146365,\n" +
                "11418700,\n" +
                "11393687,\n" +
                "6784302,\n" +
                "4647139,\n" +
                "11206706,\n" +
                "11066442,\n" +
                "1913397668,\n" +
                "1224159873,\n" +
                "4528570,\n" +
                "11339953,\n" +
                "11209844,\n" +
                "11360458,\n" +
                "10832898,\n" +
                "7748165,\n" +
                "1301986957,\n" +
                "826317368,\n" +
                "1477635327,\n" +
                "14366319,\n" +
                "11291596,\n" +
                "1669810535,\n" +
                "2517042,\n" +
                "1695956355,\n" +
                "8731266,\n" +
                "6661806,\n" +
                "14668792,\n" +
                "9687668,\n" +
                "9657556,\n" +
                "9615096,\n" +
                "9608460,\n" +
                "11111047,\n" +
                "10942221,\n" +
                "3962948,\n" +
                "6436051,\n" +
                "7105648,\n" +
                "8856582,\n" +
                "8786839,\n" +
                "8864384,\n" +
                "7952703,\n" +
                "8922420,\n" +
                "9017723,\n" +
                "7276347,\n" +
                "1392430999,\n" +
                "704202168,\n" +
                "11241789,\n" +
                "8575084,\n" +
                "8677711,\n" +
                "8522062,\n" +
                "15296640,\n" +
                "15020090,\n" +
                "18771511,\n" +
                "15483263,\n" +
                "6661806,\n" +
                "9028398,\n" +
                "15392769,\n" +
                "6355587,\n" +
                "4082003,\n" +
                "4647139,\n" +
                "1891114350,\n" +
                "8036219,\n" +
                "9810339,\n" +
                "9468857,\n" +
                "9317526,\n" +
                "15285765,\n" +
                "6249112";


        String[] split = s.split(",\n");
        for (String s1 : split) {
            total.add(s1);
            totalSet.add(s1);
        }


        String s1 = "1269095,\n" +
                "2517042,\n" +
                "3608150,\n" +
                "3962948,\n" +
                "4082003,\n" +
                "4396800,\n" +
                "4570302,\n" +
                "4647139,\n" +
                "4855468,\n" +
                "5086695,\n" +
                "5189229,\n" +
                "5397660,\n" +
                "6058911,\n" +
                "6120395,\n" +
                "6217382,\n" +
                "6249112,\n" +
                "6270618,\n" +
                "6319146,\n" +
                "6355587,\n" +
                "6436051,\n" +
                "6661806,\n" +
                "6784302,\n" +
                "7748165,\n" +
                "8036219,\n" +
                "8731266,\n" +
                "8822245,\n" +
                "8856582,\n" +
                "9028398,\n" +
                "9029351,\n" +
                "9135497,\n" +
                "9468857,\n" +
                "9586351,\n" +
                "9608460,\n" +
                "9615096,\n" +
                "9615545,\n" +
                "9657556,\n" +
                "9680616,\n" +
                "9687668,\n" +
                "9810339,\n" +
                "10055235,\n" +
                "10119094,\n" +
                "10124793,\n" +
                "10279056,\n" +
                "10522246,\n" +
                "10788681,\n" +
                "10797064,\n" +
                "10829139,\n" +
                "10832898,\n" +
                "10841917,\n" +
                "10871434,\n" +
                "10872228,\n" +
                "10873096,\n" +
                "10893093,\n" +
                "10942221,\n" +
                "11066442,\n" +
                "11111047,\n" +
                "11206706,\n" +
                "11209844,\n" +
                "11241789,\n" +
                "11291596,\n" +
                "11339953,\n" +
                "11360458,\n" +
                "11393687,\n" +
                "11418700,\n" +
                "11449539,\n" +
                "11878395,\n" +
                "12828847,\n" +
                "13638446,\n" +
                "13715829,\n" +
                "14366319,\n" +
                "14622766,\n" +
                "14668792,\n" +
                "15020090,\n" +
                "15074626,\n" +
                "15197706,\n" +
                "15296640,\n" +
                "15483263,\n" +
                "16318553,\n" +
                "17874082,\n" +
                "18894264,\n" +
                "124580903,\n" +
                "207265053,\n" +
                "379433060,\n" +
                "489353649,\n" +
                "527456040,\n" +
                "704202168,\n" +
                "809352048,\n" +
                "820821209,\n" +
                "826317368,\n" +
                "867238919,\n" +
                "973003259,\n" +
                "1175661398,\n" +
                "1224159873,\n" +
                "1281249369,\n" +
                "1301986957,\n" +
                "1392430999,\n" +
                "1471635879,\n" +
                "1500353319,\n" +
                "1541218741,\n" +
                "1564300967,\n" +
                "1669810535,\n" +
                "1695956355,\n" +
                "1775016700,\n" +
                "1891114350,\n" +
                "1913397668,\n" +
                "1972339667";


        String[] split1 = s1.split(",\n");
        for (String s2 : split1) {
            exits.add(s2);
            exitsSet.add(s2);
        }

        System.out.println("total size:" + total.size());
        System.out.println("total  set size:" + totalSet.size());
        System.out.println("exits size:" + exits.size());
        System.out.println("exits set size:" + exitsSet.size());
        List<String> nonExits = new ArrayList<>();
        total.forEach(item -> {
            if (!exitsSet.contains(item)) {
                nonExits.add(item);
            }
        });

        System.out.println("nonExits size:" + nonExits.size());
        Iterator<String> iterator = nonExits.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next() + ",");
        }
    }

}
