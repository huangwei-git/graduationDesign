package com.songlian.logistics.calculate.EchartsDataTransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphDataTransfer {

    public static List<Map> graphLinkFormat(String path) {
        String locs[] = path.substring(1,path.length() - 1).split(",");
        List<Map> links = new ArrayList<>();
        for (int i = 0; i < locs.length - 1; i++) {
            HashMap<String, Integer> map = new HashMap();
            map.put("source", Integer.valueOf(locs[i].trim()));
            map.put("target", Integer.valueOf(locs[i + 1].trim()));
            links.add(map);
        }
        //System.out.println("links = " + links);
        return links;
    }

    public static List<Map> graphDataFormat(List<double[]> locationList, List<String> locNameList){
        List<Map> locsData = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            HashMap map = new HashMap();
            if(i == 0){
                map.put("name", "运");
                map.put("symbolSize", 50);
            }
            else map.put("name", locNameList.get(i).charAt(locNameList.get(i).length() - 1));
            map.put("x", (int) locationList.get(i)[0]);
            map.put("y", (int) locationList.get(i)[1]);
            locsData.add(map);
        }
        //System.out.println("locsData = " + locsData);
        return locsData;
    }
}

/*
{source:12, target:9},
{source:9, target:11}, 
{source:11, target:7}, 
{source:7, target:2}, 
{source:2, target:6}, 
{source:6, target:0}, 
{source:0, target:5}, 
{source:5, target:4},
{source:4, target:3}, 
{source:3, target:1}, 
{source:1, target:8}, 
{source:8, target:10}, 
{source:10, target:12}



{y:16, x:32},
{y:18, x:2}, 
{y:35, x:25}, 
{y:10, x:18}, 
{y:5, x:35}, 
{y:10, x:45},
{y:25, x:26}, 
{y:32, x:38}, 
{y:27, x:8}, 
{y:50, x:50},
{y:40, x:10}, 
{y:40, x:40},
{y:56, x:30}

* */
