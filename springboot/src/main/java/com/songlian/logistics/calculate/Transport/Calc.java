package com.songlian.logistics.calculate.Transport;

import com.songlian.logistics.pojo.Location;

import java.util.List;

public class Calc {
    public static Integer getDestination(Location p1, Location p2){
        int x1 = p1.getXpos();
        int y1 = p1.getYpos();
        int x2 = p2.getXpos();
        int y2 = p2.getYpos();
        return (int) Math.sqrt( (x1 - x2) * ( x1 - x2) + (y1 - y2) * (y1 - y2) );
    }

    public static int[][] getCostArray(List<Location> loc1,List<Location> loc2){
        int arr[][] = new int[loc1.size()][loc2.size()];
        for (int i = 0; i < loc1.size(); i++) {
            for (int j = 0; j < loc2.size(); j++) {
                arr[i][j] = getDestination(loc1.get(i),loc2.get(j));
            }
        }
        return arr;
    }
}
