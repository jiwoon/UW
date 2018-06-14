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
	
	private static final String uniqueCheckSql = "SELECT * FROM material_type WHERE no = ?";
	
	private static final String countSelectSql = "select material_type.*, sum(material.remainderQuantity) as quantity";
	
	private	static final String countNonSelectSql = " from material_type,material where material_type.id=material.type and material_type.enabled=1"
			+ " group by material_type.id";
	
	private	static final String getEntitiesSql = "SELECT material.* FROM material, material_type where type=? "
			+ "and material_type.id=material.type and material_type.enabled=1";

//	public List<MaterialType> count(MaterialType materialType) {
//		List<MaterialType> materialCount;
//		materialCount = materialType.dao.find(countSql);
//		return materialCount;
//	}

	public Object count(Integer pageNo, Integer pageSize) {
		return Db.paginate(pageNo, pageSize, countSelectSql, countNonSelectSql);
	}
	
	public List<Material> getEntities(Integer type) {
		List<Material> materialEntities;
		materialEntities = Material.dao.find(getEntitiesSql, type);
		return materialEntities;
	}
	
	// 需要同步到 material表
	public boolean add(MaterialType materialType) {
		materialType.setEnabled(true);
		if(MaterialType.dao.find(uniqueCheckSql, materialType.getNo()).size() != 0) {
			throw new OperationException("material is already exist");
		}
		return materialType.save();
	}
	
	// 需要同步到 material表
	public boolean update(MaterialType materialType) {
		materialType.keep("id","no","area","row","col","height","enabled");
		return materialType.update();
	}
	
}
