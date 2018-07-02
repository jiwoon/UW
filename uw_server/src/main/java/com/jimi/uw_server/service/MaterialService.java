package com.jimi.uw_server.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.vo.MaterialTypeVO;
import com.jimi.uw_server.service.base.SelectService;

/**
 * 订单业务层
 * <br>
 * <b>2018年5月29日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class MaterialService extends SelectService{
	
	private static final String countSelectSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity";
	
	private	static final String countNonSelectSql = " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
			+ " group by material_type.id";
	
	private static final String countSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity"
			+ " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
			+ " group by material_type.id";
	
	private	static final String getEntitiesSql = "SELECT material.id, material.type, material.row, material.col, "
			+ "material.remainder_quantity as remainderQuantity FROM material, material_type WHERE type=? "
			+ "AND material_type.id=material.type AND material_type.enabled=1";
	
	private	static final String entitySearchSql = "SELECT * FROM material WHERE type=? ";
	
	private static final String uniqueCheckSql = "SELECT * FROM material_type WHERE no = ?";

	public Object count(Integer pageNo, Integer pageSize) {
		Object countMaterial = Db.paginate(pageNo, pageSize, countSelectSql, countNonSelectSql);
		MaterialTypeVO.dao.find(countSql);
		System.out.println("countSql: " + MaterialTypeVO.dao.find(countSql));
		return countMaterial;
	}
	
	public List<Material> getEntities(Material material, Integer type) {
		List<Material> materialEntities;
		if(Material.dao.find(entitySearchSql, material.getType()).size() == 0) {
			return null;
		}
		materialEntities = Material.dao.find(getEntitiesSql, type);
		return materialEntities;
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
