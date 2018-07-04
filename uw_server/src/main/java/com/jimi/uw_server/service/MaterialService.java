package com.jimi.uw_server.service;

import java.util.ArrayList;
import java.util.List;

import com.jimi.uw_server.service.entity.Page;
import com.jimi.uw_server.model.Material;
import com.jimi.uw_server.model.MaterialType;
import com.jimi.uw_server.model.vo.MaterialTypeVO;
import com.jimi.uw_server.service.base.SelectService;

/**
 * 物料业务层
 * <br>
 * <b>2018年5月29日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class MaterialService extends SelectService{
	
	private	static final String getEntitiesSql = "SELECT material.id, material.type, material.row, material.col, "
			+ "material.remainder_quantity as remainderQuantity FROM material, material_type WHERE type=? "
			+ "AND material_type.id=material.type AND material_type.enabled=1";
	
	private	static final String entitySearchSql = "SELECT * FROM material WHERE type=? ";
	
	private static final String uniqueCheckSql = "SELECT * FROM material_type WHERE no = ?";
	
	private static final String countNoSortSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity"
			+ " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
			+ " group by material_type.id limit ?,?";
	
	private static final String doPaginateSql = "SELECT COUNT(*) as total FROM material_type WHERE material_type.enabled=1";

	public Object count(Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter) {
		List<MaterialType> countMaterial = null;
		List<MaterialTypeVO> materialTypeVO = new ArrayList<MaterialTypeVO>();
		
		Page page = new Page();
		page.setPageNumber(pageNo);
		page.setPageSize(pageSize);
		Integer totallyRow = Integer.parseInt(MaterialType.dao.findFirst(doPaginateSql).get("total").toString());
		page.setTotalRow(totallyRow);
		Integer firstIndex = (page.getPageNumber()-1)*page.getPageSize();
		
//		原本想用以下的几个逻辑判断增加排序，筛选功能，可是sql语句将筛选条件一起执行，但是不会报错
		// 判断是否有添加其他筛选条件，包括按某个字段排序，筛选等
//		if (ascBy == null && descBy == null && filter == null) {
//			countMaterial = MaterialType.dao.find(countNoSortSql, firstIndex, page.getPageSize());
//		} else if (!(ascBy == null) && !(filter == null)) {
//			countMaterial =  MaterialType.dao.find(countSortFilterSql, filter, ascBy, "asc", firstIndex, page.getPageSize());
//		} else if (!(descBy == null) && !(filter == null)) {
//			countMaterial =  MaterialType.dao.find(countSortFilterSql, filter, descBy.toString(), "desc", firstIndex, page.getPageSize());
//		} else if (!(ascBy == null)) {
//			countMaterial =  MaterialType.dao.find(countSortAscSql, ascBy, firstIndex, page.getPageSize());
//		} else if (!(descBy == null)) {
//			countMaterial =  MaterialType.dao.find(countSortDescSql, descBy.toString(), firstIndex, page.getPageSize());	//
//		} else if (!(filter == null)) {
//			countMaterial =  MaterialType.dao.find(countFilterSql, filter, firstIndex, page.getPageSize());
//		}
		
		countMaterial = MaterialType.dao.find(countNoSortSql, firstIndex, page.getPageSize());
		System.out.println("countMaterial: " + countMaterial);
		
		for (MaterialType item : countMaterial) {
			MaterialTypeVO m = new MaterialTypeVO(item.getId(), item.getNo(), item.getArea(), item.getRow(), item.getCol(),
					item.getHeight(), item.getEnabled(), item.getIsOnShelf(), Integer.parseInt(item.get("quantity").toString()));
			materialTypeVO.add(m);
		}
		
		page.setList(materialTypeVO);

		return page;
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
