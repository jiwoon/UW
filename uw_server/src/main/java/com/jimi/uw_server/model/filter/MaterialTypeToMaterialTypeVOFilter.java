//package com.jimi.uw_server.model.filter;
//
//import com.jimi.uw_server.model.MaterialType;
//import com.jimi.uw_server.model.vo.MaterialTypeVO;
//import com.jimi.uw_server.util.EntityFieldFiller;
//
//public class MaterialTypeToMaterialTypeVOFilter extends EntityFieldFiller<MaterialType, MaterialTypeVO> {
//	
//	private static final String countSql = "SELECT material_type.*, SUM(material.remainder_quantity) AS quantity"
//			+ " FROM material_type,material WHERE material_type.id=material.type AND material_type.enabled=1"
//			+ " group by material_type.id";
//	
//	public MaterialTypeVO fill(MaterialType materialType) {
////		
//	}
//	
//}
