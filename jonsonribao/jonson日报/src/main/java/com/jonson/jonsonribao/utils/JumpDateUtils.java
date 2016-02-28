package com.jonson.jonsonribao.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jonson on 2016/2/22.
 */
public class JumpDateUtils {

    private static int currentYear;
    private static int currentMonth;
    private static int currentDay;
    private static int monthDays;
    private static int jumpDays;
    private int targetDays;

    public String getResult(Date currentDate , int jumpDays){

        this.jumpDays = jumpDays;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String cur = format.format(currentDate);
        String[] split = cur.split("-");
        currentYear = Integer.parseInt(split[0]);
        currentMonth = Integer.parseInt(split[1]);
        currentDay = Integer.parseInt(split[2]);

        monthDays = getMonthDays(currentMonth);

        targetDays = currentDay + jumpDays;

        return caculate(targetDays);

    }

    private String caculate(int targetDays) {

        if(targetDays <= 0){
            currentMonth = currentMonth - 1;

            //如果当前月份小于0 , 把年份-1 , 月份设置为12;
            if (currentMonth < 1) {
                currentYear = currentYear - 1;
                currentMonth = 12;
            }
            //获取月份天数,因为月份-1了,所以currentDay设置为月份天数,然后把currentDay加上负数的目标天数,在
            monthDays = getMonthDays(currentMonth);
            currentDay = monthDays;
            targetDays = currentDay + targetDays;
            caculate(targetDays);
        }
        //如果当前月份足够减,那么直接返回数据
        return currentYear + "" + (currentMonth < 10 ? "0" + currentMonth : currentMonth) + "" + (targetDays<10 ? "0" + targetDays : targetDays);
    }


    //获取月份的天数
    private int getMonthDays(int month){
        if(month <1 || month >12){
            return -1;
        }
        else if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
            return 31;
        }
        else if(month == 4 || month == 6 || month == 9 || month == 11){
            return 30;
        }
        else{
            if(isLeapYear(currentYear)){
                return 29;
            }else{
                return 28;
            }
        }
    }

    //是否是闰年
    private static boolean isLeapYear(int year){
        return (year % 4 == 0 && (year % 100 != 0) || (year % 400 == 0));
    }

}
