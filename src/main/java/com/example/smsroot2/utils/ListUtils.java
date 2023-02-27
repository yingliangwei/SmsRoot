package com.example.smsroot2.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    /**
     * 将一个list均分成n个list
     * @param source
     * @return
     */
    public static <T> List <List<T>> averageAssign(List<T>source,int n){
        List <List<T>> result= new ArrayList<>();
        int remainder=source.size()%n;  //先计算出余数
        int number=source.size()/n;  //然后是商
        int offset=0;//偏移量（用以标识加的余数）
        for(int i=0;i<n;i++){
            List<T>value;
            if(remainder>0){
                value=source.subList(i*number+offset, (i+1)*number+offset+1);
                remainder--;
                offset++;
            }else{
                value=source.subList(i*number+offset, (i+1)*number+offset);
            }
            result.add(value);
        }
        return result;
    }

}