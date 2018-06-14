package com.jimi.uw_server.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.service.MaterialService;
import com.jimi.uw_server.util.ResultUtil;

public class MaterialController extends Controller {
	
	private static MaterialService materialService = Enhancer.enhance(MaterialService.class);
	
//	public void select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
//		renderJson(ResultUtil.succeed(materialService.select(table, pageNo, pageSize, ascBy, descBy, filter)));
//	}

	// 统计物料类型信息
	public void count(Integer pageNo, Integer pageSize) {
		renderJson(ResultUtil.succeed(materialService.count(pageNo, pageSize)));
	}

	// 获取物料实体
	public void getEntities(Integer type) {
		renderJson(ResultUtil.succeed(materialService.getEntities(type)));
	}
	
	// 添加物料类型#
//	@Access({"SuperAdmin"})
	public void add(@Para("") MaterialType materialType) {
		if(materialService.add(materialType)) {
			renderJson(ResultUtil.succeed());
		}else {
			renderJson(ResultUtil.failed());
		}
	}

    // 更新物料类型#
//	@Access({"SuperAdmin"})
	public void update(@Para("") MaterialType materialType) {
		if(materialService.update(materialType)) {
			renderJson(ResultUtil.succeed());
		}else {
			renderJson(ResultUtil.failed());
		}
	}
	
}
