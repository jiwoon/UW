package com.jimi.uw_server.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jimi.uw_server.exception.OperationException;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
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
	
	private	static final String getEntitiesSql = "SELECT material.* FROM material, material_type WHERE type=? "
			+ "AND material_type.id=material.type AND material_type.enabled=1";
	
	private	static final String entitySearchSql = "SELECT * FROM material WHERE type=? ";
	
	private static final String uniqueCheckSql = "SELECT * FROM material_type WHERE no = ?";

	public Object count(Integer pageNo, Integer pageSize) {
		return Db.paginate(pageNo, pageSize, true, countSelectSql, countNonSelectSql);
	}
	
	public List<Material> getEntities(Material material, Integer type) {
		List<Material> materialEntities;
		if(Material.dao.find(entitySearchSql, material.getType()).size() == 0) {
			throw new OperationException("material doesn't exist");
		}
		materialEntities = Material.dao.find(getEntitiesSql, type);
		return materialEntities;
	}

	public boolean add(MaterialType materialType) {
		materialType.setEnabled(true);
		if(MaterialType.dao.find(uniqueCheckSql, materialType.getNo()).size() != 0) {
			throw new OperationException("material is already exist");
		}
		return materialType.save();
	}
	
	public boolean update(MaterialType materialType) {
		materialType.keep("id","no","area","row","col","height","enabled");
		return materialType.update();
	}
	
}
