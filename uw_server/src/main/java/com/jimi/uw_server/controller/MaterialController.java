package com.jimi.uw_server.controller;

import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.service.MaterialService;
import com.jimi.uw_server.util.ResultUtil;

public class MaterialController extends Controller {
	
	private static MaterialService materialService = Enhancer.enhance(MaterialService.class);

	// 统计物料类型信息
	public void count(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		Object countResult = materialService.count(pageNo, pageSize, ascBy, descBy, filter);
		renderJson(ResultUtil.succeed(countResult));
	}

	// 获取物料实体
	public void getEntities(@Para("") Material material, Integer type) {
		List<Material> entities = materialService.getEntities(material, type);
		if (entities != null) {
			renderJson(ResultUtil.succeed(entities));
		} else {
			renderJson(ResultUtil.failed());
			throw new OperationException("该物料不存在，请输入正确的物料类型号！");
		}
	}
	
	// 添加物料类型#
//	@Access({"SuperAdmin"})
	public void add(@Para("") MaterialType materialType) {
		if(materialService.add(materialType)) {
			renderJson(ResultUtil.succeed());
		}else {
			renderJson(ResultUtil.failed());
			throw new OperationException("该物料已存在，请不要输入重复的物料类型号！");
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
