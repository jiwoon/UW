package com.example.jimi.in_outstock.common;

public class UrlData {
    // ip地址
    private static final  String IP_ADRESS ="http://192.168.2.110:8080";

    // 程序路径
    private static final String URL = IP_ADRESS+"/uw_server";

    // 用户登录接口
    private static final String URL_LOGIN= URL+"/manage/user/login";

    // 获取仓口任务条目接口
    private static final String URL_GETWINDOWTASKITEMS = URL+"/task/getWindowTaskItems";

    //获取仓口停泊条目
    private static final String UPL_GETWINDOWPARKINGITEM = URL+"/task/getWindowParkingItem";

    // 写入出入库任务日志接口
    private  static final String URL_WRITEIO = URL +"/task/io";

    // 完成出入库任务条目接口
    private static final String URL_FINISHITEM = URL+"/task/finishItem";

    // 叉车回库接口
    private static final String URL_ROBOTBACK = URL+"/manage/robot/back";

    public static String getUrlFinishitem() {
        return URL_FINISHITEM;
    }

    public static String getUrlGetwindowtaskitems() {
        return URL_GETWINDOWTASKITEMS;
    }

    public static String getUrlLogin() {
        return URL_LOGIN;
    }

    public static String getUrlRobotback() {
        return URL_ROBOTBACK;
    }

    public static String getUrlWriteio() {
        return URL_WRITEIO;
    }

    public static String getUrlGetwindowparkingitem(){ return UPL_GETWINDOWPARKINGITEM; }
}
