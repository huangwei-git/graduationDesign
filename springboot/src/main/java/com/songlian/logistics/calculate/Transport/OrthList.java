package com.songlian.logistics.calculate.Transport;

// 十字链表
public class OrthList{
    ListNode head = new ListNode();
    ListNode map[][];

    public OrthList(int arr[][]){
        int rowLen = arr.length;
        int colLen = arr[0].length;
        map = new ListNode[rowLen][colLen];
        // 创建十字链表的行头和列头
        ListNode tmp = head;
        for(int i = 0;i < rowLen;i++){
            tmp.down = new ListNode();
            tmp.down.up = tmp;
            tmp = tmp.down;
        }
        tmp = head;
        for(int i = 0;i < colLen;i++){
            tmp.right = new ListNode();
            tmp.right.left = tmp;
            tmp = tmp.right;
        }

        // 行头
        tmp = head;
        for(int i = 0;i < rowLen;i++){
            tmp = tmp.down;
            ListNode tmptmp = tmp;
            for(int j = 0;j < colLen;j++){
                if(arr[i][j] != 0){
                    tmptmp.right = new ListNode(new Point(i,j));
                    tmptmp.right.left = tmptmp;
                    tmptmp = tmptmp.right;
                    map[i][j] = tmptmp;
                }
            }
        }
        // 列头
        tmp = head;
        for(int i = 0;i < colLen;i++){
            tmp = tmp.right;
            ListNode tmptmp = tmp;
            for(int j = 0;j < rowLen;j++){
                if(arr[j][i] != 0){
                    tmptmp.down = map[j][i] ;
                    map[j][i] .up = tmptmp;
                    tmptmp = map[j][i] ;
                }
            }
        }
    }
}

// 十字链表节点
class ListNode{
    Point point;
    ListNode up,down,right,left;

    public ListNode(){
        point = null;
        up = down = right = left = null;
    }

    public ListNode(Point point){
        this.point = point;
        up = down = right = left = null;
    }
}