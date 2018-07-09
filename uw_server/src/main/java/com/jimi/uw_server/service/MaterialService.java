package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.vo.MaterialTypeVO;
import com.jimi.uw_server.service.base.SelectService;
import com.jimi.uw_server.service.entity.PagePaginate;

/**
 * 物料业务层
 * @author HardyYao
 * @createTime 2018年6月8日
 */
public class MaterialService extends SelectService{
	
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	
	private	static final String getEntitiesSql = "SELECT material.id, material.type, material.row, material.col, "
			+ "material.remainder_quantity as remainderQuantity FROM material, material_type WHERE type=? "
			+ "AND material_type.id=material.type AND material_type.enabled=1";
	
	private	static final String entitySearchSql = "SELECT * FROM material WHERE type=? ";
	
	private static final String uniqueCheckSql = "SELECT * FROM material_type WHERE no = ?";

	public Object count(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<MaterialTypeVO> materialTypeVO = new ArrayList<MaterialTypeVO>();
		
		if (filter != null) {
			filter = filter + "&material_type.enabled=1";
			if (filter.contains("col")) {
				filter = filter.replace("col", "material_type.col");
			}
			if (filter.contains("row")) {
				filter = filter.replace("row", "material_type.row");
			}
		} else {
			filter = "material_type.enabled=1";
		}

		Page<Record> result = selectService.select(new String[] {"material_type", "material"}, new String[] {"material.type=material_type.id"},
				pageNo, pageSize, ascBy, descBy, filter);
		
		int totallyRow =  result.getTotalRow();
		for (Record res : result.getList()) {
			MaterialTypeVO m = new MaterialTypeVO(res.get("MaterialType_Id"), res.get("MaterialType_No"), res.get("MaterialType_Area"),
					res.get("MaterialType_Row"), res.get("MaterialType_Col"), res.get("MaterialType_Height"), res.get("MaterialType_Enabled"),
					res.get("Material_RemainderQuantity"));
			materialTypeVO.add(m);
		}
		
		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);
		
		pagePaginate.setList(materialTypeVO);

		return pagePaginate;
	}
	
	public Object getEntities(Material material, Integer type) {
		List<Material> materialEntities;
		if(Material.dao.find(entitySearchSql, material.getType()).size() == 0) {
			return null;
		}
		materialEntities = Material.dao.find(getEntitiesSql, type);
		
		int pageSize = 20;
		int pageNo = 1;
		int totallyRow =  materialEntities.size();
		
		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);
		
		pagePaginate.setList(materialEntities);
		
		return pagePaginate;
	}

	public boolean add(MaterialType materialType) {
		materialType.setEnabled(true);
		materialType.setIsOnShelf(true);
		if(MaterialType.dao.find(uniqueCheckSql, materialType.getNo()).size() != 0) {
			return false;
		}
		return materialType.save();
	}
	
	public boolean update(MaterialType materialType) {
		materialType.keep("id","no","area","row","col","height","enabled", "is_on_shelf");
		return materialType.update();
	}
	
}