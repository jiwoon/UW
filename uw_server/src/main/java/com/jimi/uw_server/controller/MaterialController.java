package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.service.MaterialService;
import com.jimi.uw_server.util.ResultUtil;

/**
 * 物料控制层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class MaterialController extends Controller {

	private static MaterialService materialService = Enhancer.enhance(MaterialService.class);

	// 统计物料类型信息
	public void count(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		Object countResult = materialService.count(pageNo, pageSize, ascBy, descBy, filter);
		if (countResult == null) {
			renderJson(ResultUtil.failed("不存在该物料类型！"));
		} else {
			renderJson(ResultUtil.succeed(countResult));
		}
	}

	// 获取物料实体
	public void getEntities(Integer type) {
		Object entities = materialService.getEntities(type);
		if (entities != null) {
			renderJson(ResultUtil.succeed(entities));
		} else {
			renderJson(ResultUtil.failed("该物料类型暂无库存！"));
		}
	}

	// 添加物料类型#
	public void add(String no, Integer area, Integer row, Integer col, Integer height) {
		if(materialService.add(no, area, row, col, height)) {
			renderJson(ResultUtil.succeed());
		}else {
			renderJson(ResultUtil.failed(412));
			throw new OperationException("该物料已存在，请不要添加重复的物料类型号！");
		}
	}

    // 更新物料类型#
	public void update(@Para("") MaterialType materialType) {
		if(materialService.update(materialType)) {
			renderJson(ResultUtil.succeed());
		}else {
			renderJson(ResultUtil.failed());
		}
	}
	
}
