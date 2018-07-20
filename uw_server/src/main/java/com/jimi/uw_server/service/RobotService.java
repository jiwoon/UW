package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.agv.entity.bo.AGVRobot;
import com.jimi.uw_server.agv.handle.SwitchHandler;
import com.jimi.uw_server.model.Robot;
import com.jimi.uw_server.model.vo.RobotVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

/**
 * 叉车业务层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class RobotService extends SelectService {

    private static SelectService selectService = Enhancer.enhance(SelectService.class);

    public static final String getRobotAllId = "SELECT id FROM robot";

    public Object select(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
        List<RobotVO> robotVO = new ArrayList<RobotVO>();

        Page<Record> result = selectService.select("robot", pageNo, pageSize, ascBy, descBy, filter);

        int totallyRow =  result.getTotalRow();
        for (Record res : result.getList()) {
            RobotVO r = new RobotVO(res.get("id"), res.get("status"), res.get("battery"),
                    res.get("x"), res.get("y"), res.get("enabled"), res.get("error"), res.get("warn"), res.get("pause"));
            robotVO.add(r);
        }

        PagePaginate pagePaginate = new PagePaginate();
        pagePaginate.setPageNumber(pageNo);
        pagePaginate.setPageSize(pageSize);
        pagePaginate.setTotalRow(totallyRow);

        pagePaginate.setList(robotVO);

        return pagePaginate;
    }



    public void robotSwitch(String id, Integer enabled) {
        List<Integer> idList = java.util.Arrays.asList(id.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        if (enabled == 2) {
            SwitchHandler.sendEnable(idList);
        } else if (enabled == 1) {
            SwitchHandler.sendDisable(idList);
        }
    }



    public void pause(boolean pause) {
        if (pause) {
            SwitchHandler.sendAllStart();
        } else {
            SwitchHandler.sendAllPause();

        }
    public void updateRobotInfo(Map<Integer, AGVRobot> newRobots, Map<Integer, AGVRobot> robots) {
        //获取新增项
        Set<Integer> addRobotsIds = new HashSet<>(newRobots.keySet());
        addRobotsIds.removeAll(robots.keySet());
        //新增机器记录
        for (Integer id : addRobotsIds) {
            Robot robot = AGVRobot.toModel(newRobots.get(id));
            robot.save();
        }

        //获取减少项
        Set<Integer> removeRobotsIds = new HashSet<>(robots.keySet());
        removeRobotsIds.removeAll(newRobots.keySet());
        //删除机器记录
        for (Integer id : removeRobotsIds) {
            Robot.dao.deleteById(id);
        }

        //获取修改项
        Set<Integer> modifyRobotsIds = new HashSet<>(newRobots.keySet());
        modifyRobotsIds.retainAll(robots.keySet());
        //修改机器记录
        for (Integer id : modifyRobotsIds) {
            Robot robot = AGVRobot.toModel(newRobots.get(id));
            robot.update();
        }
    }


    public void setloadException()

}
