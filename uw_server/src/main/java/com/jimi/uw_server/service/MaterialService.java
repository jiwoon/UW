package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.record.Record;

import com.jimi.uw_server.service.entity.Page;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.vo.MaterialTypeVO;
//import com.jimi.uw_server.model.filter.MaterialTypeToMaterialTypeVOFilter;
import com.jimi.uw_server.service.base.SelectService;

/**
 * 物料业务层
 * <br>
 * <b>2018年5月29日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class MaterialService extends SelectService{
	
//	private static final String countSelectSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity";
//	
//	private	static final String countNonSelectSql = " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
//			+ " group by material_type.id";
	
	private	static final String getEntitiesSql = "SELECT material.id, material.type, material.row, material.col, "
			+ "material.remainder_quantity as remainderQuantity FROM material, material_type WHERE type=? "
			+ "AND material_type.id=material.type AND material_type.enabled=1";
	
	private	static final String entitySearchSql = "SELECT * FROM material WHERE type=? ";
	
	private static final String uniqueCheckSql = "SELECT * FROM material_type WHERE no = ?";
	
	private static final String countSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity"
			+ " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
			+ " group by material_type.id";		//  order by id desc limit ?,?
	
	private static final String doPaginateSql = "SELECT COUNT(*) as total FROM material_type WHERE material_type.enabled=1";

	public List<MaterialTypeVO> count(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<MaterialType> countMaterial;
		List<MaterialTypeVO> materialTypeVO = new ArrayList<MaterialTypeVO>();
		countMaterial = MaterialType.dao.find(countSql);
//		System.out.println("count: " + countMaterial);
		
//		List logList = list.getList();
//		System.out.println("logList: " + logList);
		
		for (MaterialType item : countMaterial) {
			MaterialTypeVO m = new MaterialTypeVO();
			m.setId(item.getId());
			m.setNo(item.getNo());
			m.setArea(item.getArea());
			m.setRow(item.getRow());
			m.setCol(item.getCol());
			m.setHeight(item.getHeight());
			m.setEnabled(item.getEnabled());
			m.setEnabledString(item.getEnabled() ? "是" : "否");
			m.setIsOnShelf(item.getIsOnShelf());
			m.setQuantity(Integer.parseInt(item.get("quantity").toString()));
			materialTypeVO.add(m);
			System.out.println("materialTypeVO: " + materialTypeVO);
		}
		
//		Long totalRecord = MaterialType.dao.findFirst(doPaginateSql).get("total");
//		System.out.println("totalRecord: " + totalRecord);
		
		MaterialTypeVO m = new MaterialTypeVO();
		
		m.setPageNumber(1);
		
		materialTypeVO.add(m);
		
		Page page = new Page();
		page.setCurrentPage(pageNo);
		page.setPageSize(pageSize);
//		page.getFirstIndex();
		Integer totallyRow = Integer.parseInt(MaterialType.dao.findFirst(doPaginateSql).get("total").toString());
		page.setTotallyData(totallyRow);
//		Integer totallyPage = Integer.parseInt(totalRecord.toString()) / pageSize;
		Integer totallyPage = page.getTotallyPage();
		System.out.println("totallyPage: " + totallyPage);
		
		return materialTypeVO;
	}
	
	public List<Material> getEntities(Material material, Integer type) {
		List<Material> materialEntities;
		if(Material.dao.find(entitySearchSql, material.getType()).size() == 0) {
			return null;
		}
		materialEntities = Material.dao.find(getEntitiesSql, type);
//		System.out.println("materialEntities: " + materialEntities);
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
