package com.jimi.uw_server.service.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jimi.uw_server.exception.ParameterException;
import com.jimi.uw_server.model.MappingKit;

import cc.darhao.dautils.api.StringUtil;

/**
 * 通用查询业务层
 * <br>
 * <b>2018年5月23日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class SelectService {

	public static void main(String[] args) {
		PropKit.use("properties.ini");
		DruidPlugin dp = new DruidPlugin(PropKit.get("d_url"), PropKit.get("d_user"), PropKit.get("d_password"));
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
	    arp.setDialect(new MysqlDialect());	// 用什么数据库，就设置什么Dialect
	    arp.setShowSql(true);
	    MappingKit.mapping(arp);
		dp.start();
		arp.start();
		SelectService selectService = new SelectService();
		String result = selectService.select("material", null, null, null, null, null).getList().toString();
		System.out.println(result);
		result = selectService.select(new String[] {"material_type","material"}, new String[] {"material.type=material_type.id"}, null, null, null, null, null).getList().toString();
		System.out.println(result);
	}
	
	
	/**
	 * 分页查询，支持筛选和排序
	 * @param tables 提供可读的表名数组
	 * @param refers 外键数组，单表可为null
	 * @param pageNo 页码，从1开始
	 * @param pageSize 每页的条目数
	 * @param ascBy 按指定字段升序，不可和descBy同时使用
	 * @param descBy 按指定字段降序，不可和ascBy同时使用
	 * @param filter 按字段筛选，支持<, >, >,=, <=, !=, =，多个字段请用&隔开
	 * @return Page对象
	 */
	public Page<Record> select(String[] tables, String[] refers, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		StringBuffer sql = new StringBuffer();
		List<String> questionValues = new ArrayList<>();
		createFrom(tables, refers, sql);
		createWhere(filter, questionValues, sql);
		createOrderBy(ascBy, descBy, sql);
		return paginateAndFillWhereValues(tables, pageNo, pageSize, sql, questionValues);
	}
	
	
	/**
	 * 分页查询，支持筛选和排序
	 * @param table 提供可读的表名
	 * @param pageNo 页码，从1开始
	 * @param pageSize 每页的条目数
	 * @param ascBy 按指定字段升序，不可和descBy同时使用
	 * @param descBy 按指定字段降序，不可和ascBy同时使用
	 * @param filter 按字段筛选，支持<, >, >,=, <=, !=, =，多个字段请用&隔开
	 * @return Page对象
	 */
	public Page<Record> select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter){
		return select(new String[] {table}, null, pageNo, pageSize, ascBy, descBy, filter);
	}
	
	
	private void createFrom(String[] tables, String[] refers, StringBuffer sql) {
		//表名非空判断
		if(tables == null) {
			throw new ParameterException("table name must be provided");
		}
		//创建FROM
		sql.append(" FROM ");
		//表是否在可读范围内
		String[] readabledTables = PropKit.use("properties.ini").get("readableTables").split(",");
		int pass = 0;
		for (String table : tables) {
			for (String readabledTable : readabledTables) {
				if(readabledTable.equals(table)) {
					pass++;
					sql.append(table + " JOIN ");
					break;
				}
			}
		}
		if(pass != tables.length) {
			throw new ParameterException("some tables are not readabled");
		}
		sql.delete(sql.lastIndexOf("JOIN"), sql.length());
		//创建ON
		if(refers != null) {
			sql.append(" ON ");
			for (String refer : refers) {
				sql.append(refer);
				sql.append(" AND ");
			}
			sql.delete(sql.lastIndexOf("AND"), sql.length());
		}
	}

	
	private void createWhere(String filter, List<String> questionValues, StringBuffer sql) {
		//判断filter存在与否
		if(filter != null) {
			sql.append(" WHERE ");
			String[] whereUnits = filter.split("&");
			int index = 0;
			for (String whereUnit: whereUnits) {
				//分割键值与运算符
				int operatorStartIndex = -1;
				StringBuffer operator = new StringBuffer();
				for (int i = 0; i < whereUnit.length(); i++) {
					char c = whereUnit.charAt(i);
					if(c == '>' || c == '<' || c == '=' || c == '!') {
						operator.append(c);
						if(operatorStartIndex == -1) {
							operatorStartIndex = i;
						}
					}
				}
				String key = whereUnit.substring(0, operatorStartIndex);
				String value = whereUnit.substring(operatorStartIndex + operator.length(), whereUnit.length());
				sql.append(key + operator.toString() +"? AND ");
				questionValues.add(value);
				if(index == whereUnits.length - 1) {
					sql.delete(sql.lastIndexOf("AND"), sql.length());
				}
				index++;
			}
		}
	}


	private void createOrderBy(String ascBy, String descBy, StringBuffer sql) {
		if(ascBy != null && descBy != null) {
			throw new ParameterException("ascBy and descBy can not be provided at the same time");
		}else if(ascBy != null) {
			sql.append(" ORDER BY " + ascBy + " ASC ");
		}else if(descBy != null){
			sql.append(" ORDER BY " + descBy + " DESC ");
		}
	}


	private Page<Record> paginateAndFillWhereValues(String[]tables, Integer pageNo, Integer pageSize, StringBuffer sql, List<String> questionValues) {
		if((pageNo != null && pageSize == null) || (pageNo == null && pageSize != null)) {
			throw new ParameterException("ascBy and descBy must be provided at the same time");
		}
		String resultSet = createResultSet(tables);
		if(pageNo == null && pageSize == null) {
			return Db.paginate(1, PropKit.use("properties.ini").getInt("defaultPageSize"), resultSet, sql.toString(), questionValues.toArray());
		}else {
			return Db.paginate(pageNo, pageSize, resultSet, sql.toString(), questionValues.toArray());
		}
	}


	private String createResultSet(String[] tables) {
		if(tables.length == 1) {
			return "SELECT *";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		PropKit.use("properties.ini");
		String baseModelPackage = PropKit.get("baseModelPackage");
		for (String table : tables) {
			try {
				String beanClassName = StrKit.firstCharToUpperCase(StringUtil.trimUnderLineAndToUpCase(table));
				Class<?> beanClass = Class.forName(baseModelPackage + ".Base" + beanClassName);
				Method[] methods = beanClass.getMethods();
				for (Method method : methods) {
					String methodName = method.getName();
					if (methodName.startsWith("set") == false || methodName.length() <= 3) {
						continue;
					}
					Class<?>[] types = method.getParameterTypes();
					if (types.length != 1) {	
						continue;
					}
					String attrName = methodName.substring(3);
					String colName = StringUtil.toLowCaseAndInsertUnderLine(StrKit.firstCharToLowerCase(attrName));
					sql.append(table + "." + colName + " AS " + beanClassName + "_" + attrName);
					sql.append(",");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		sql.delete(sql.lastIndexOf(","), sql.length());
		return sql.toString();
	}
	
}
