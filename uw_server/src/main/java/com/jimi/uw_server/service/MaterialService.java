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

		Page<Record> result = selectService.select(new String[] {"material_type"}, null,
				pageNo, pageSize, ascBy, descBy, filter);

		int totallyRow =  result.getTotalRow();
		for (Record res : result.getList()) {
			// 在VO中，为获取quantity的值，进行了sql查询，相当于在for循环中执行了sql查询，会影响执行效率，暂时还没想到两全其美的解决方案，争取这周(7.23-7.28)想出解决方案
			MaterialTypeVO m = new MaterialTypeVO(res.get("id"), res.get("no"), res.get("area"),
					res.get("row"), res.get("col"), res.get("height"), res.get("enabled"));
			materialTypeVO.add(m);
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);

		pagePaginate.setList(materialTypeVO);

		return pagePaginate;
	}

	public Object getEntities(Integer type) {
		List<Material> materialEntities;
		// 判断该物料是否有库存
		if(Material.dao.find(entitySearchSql, type).size() == 0) {
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

	public boolean add(String no, Integer area, Integer row, Integer col, Integer height) {
		MaterialType materialType = new MaterialType();
		if(MaterialType.dao.find(uniqueCheckSql, no).size() != 0) {
			return false;
		}
		materialType.setNo(no);
		materialType.setArea(area);
		materialType.setRow(row);
		materialType.setCol(col);
		materialType.setHeight(height);
		materialType.setEnabled(true);
		materialType.setIsOnShelf(true);
		return materialType.save();
	}

	public boolean update(MaterialType materialType) {
		return materialType.update();
	}

}