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
	
	private static final String GET_SPECIFIED_POSITION_MATERIAL_TYEP_SQL = "SELECT * FROM material_type WHERE row = ? AND col = ? AND height = ?";

	private	static final String GET_ENTITIES_SQL = "SELECT material.id, material.type, material.row, material.col, "
			+ "material.remainder_quantity as remainderQuantity FROM material, material_type WHERE type = ? "
			+ "AND material_type.id = material.type AND material_type.enabled = 1";

	private	static final String GET_SPECIFIC_ENTITY_SQL = "SELECT * FROM material WHERE type = ?";

	private static final String UNIQUE_MATERIAL_TYPE_CHECK_SQL = "SELECT * FROM material_type WHERE no = ? AND enabled = 1";

	public Object count(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		Page<Record> result = selectService.select(new String[] {"material_type"}, null,
				pageNo, pageSize, ascBy, descBy, filter);
		List<MaterialTypeVO> materialTypeVOs = new ArrayList<MaterialTypeVO>();
		int totallyRow =  0;
		for (Record res : result.getList()) {
			MaterialTypeVO m = new MaterialTypeVO(res.get("id"), res.get("no"), res.get("area"),
					res.get("row"), res.get("col"), res.get("height"), res.get("enabled"));
			if (res.get("enabled").equals(true)) {
				materialTypeVOs.add(m);
				totallyRow += 1;
			}
		}

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(totallyRow);
		pagePaginate.setList(materialTypeVOs);

		return pagePaginate;
	}

	public Object getEntities(Integer type, Integer pageNo, Integer pageSize) {
		// 判断该物料是否有库存
		if(Material.dao.find(GET_SPECIFIC_ENTITY_SQL, type).size() == 0) {
			return null;
		}
		List<Material> materialEntities = Material.dao.find(GET_ENTITIES_SQL, type);

		PagePaginate pagePaginate = new PagePaginate();
		pagePaginate.setPageSize(pageSize);
		pagePaginate.setPageNumber(pageNo);
		pagePaginate.setTotalRow(materialEntities.size());
		pagePaginate.setList(materialEntities);

		return pagePaginate;
	}

	public boolean add(String no, Integer area, Integer row, Integer col, Integer height) {
		if(MaterialType.dao.find(UNIQUE_MATERIAL_TYPE_CHECK_SQL, no).size() != 0) {
			return false;
		}
		MaterialType materialType = new MaterialType();
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
	
	/**
	 * 列出同一个坐标盒子的所有物料类型
	 */
	public List<MaterialType> listByXYZ(int x, int y, int z) {
		return MaterialType.dao.find(GET_SPECIFIED_POSITION_MATERIAL_TYEP_SQL, x, y, z);
	}

}