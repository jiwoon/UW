package com.jimi.uw_server.util;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 处理Excel表的常用工具类
 * <br>
 * 所需第三方依赖：
 * <br>
 * log4j
 * <br>
 * poi
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ExcelHelper{
	
	/**
	 * 标记了该注解的属性（Fields）所属的类的实例，在调用ExcelHelper类实例的fill方法时，可以被作为参数传入，完成excel表格填写
	 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Excel{
		String head();
		int col();
	}
	
	protected static Logger logger = LogManager.getLogger();
	
	protected enum RequireType{
		/**
		 * 优先尝试转换成双精度浮点型
		 */
		DOUBLE,
		
		/**
		 * 优先尝试转换成字符串型
		 */
		STRING,
		
		/**
		 * 优先尝试转换成日期型
		 */
		DATE,
		
		/**
		 * 优先尝试转换成整型
		 */
		INT,
		
		/**
		 * 优先尝试转换成布尔型
		 */
		BOOLEAN,
	}
	
	
	protected Workbook workbook;
	
	protected CellStyle headStyle;
	
	protected CellStyle bodyStyle;
	
	protected int currentSheetNum;
	
	
	/**
	 * 传入一个excel表格，构造Helper
	 */
	public static ExcelHelper from(File file) throws IOException {
		return new ExcelHelper(file);
	}
	
	
	/**
	 * 规定excel表格属于2007版之前还是之后，构造Helper
	 */
	public static ExcelHelper create(boolean isNewVersion) {
		return new ExcelHelper(isNewVersion);
	}
	
	
	/**
	 * 构造Helper，为旧版excel
	 */
	public static ExcelHelper create() {
		return new ExcelHelper(false);
	}
	
	/**
	 * 获取一个值
	 */
	public String getString(int rowNum, int colNum) {
		return (String) get(rowNum, colNum, RequireType.STRING);
	}
	
	
	/**
	 * 获取一个值
	 */
	public Integer getInt(int rowNum, int colNum) {
		return (Integer) get(rowNum, colNum, RequireType.INT);
	}
	
	
	/**
	 * 获取一个值
	 */
	public Date getDate(int rowNum, int colNum) {
		return (Date) get(rowNum, colNum, RequireType.DATE);
	}
	
	
	/**
	 * 获取一个值
	 */
	public Double getDouble(int rowNum, int colNum) {
		return (Double) get(rowNum, colNum, RequireType.DOUBLE);
	}
	
	
	/**
	 * 获取一个值
	 */
	public Boolean getBoolean(int rowNum, int colNum) {
		return (Boolean) get(rowNum, colNum, RequireType.BOOLEAN);
	}

	/**
	 * 根据提供的Class类，从第一行开始解析出报表实例列表
	 * @throws 表头解析错误时抛出
	 */
	public <T> List<T> unfill(Class<T> clazz) throws Exception{
		return unfill(clazz, 0);
	}
	
	
	/**
	 * 根据提供的Class类，从某一行开始解析出报表实例列表
	 * @throws 表头解析错误时抛出
	 */
	public <T> List<T> unfill(Class<T> clazz, int startRowNum) throws Exception{
		List<T> entities = new ArrayList<T>();
		for (int i = startRowNum; i < workbook.getSheetAt(currentSheetNum).getLastRowNum(); i++) {
			T entity = null;
			try {
				entity = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e2) {
				e2.printStackTrace();
			}
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				Excel e = field.getAnnotation(Excel.class);
				if(e == null) {
					continue;
				}
				//如果是第一行则校验表头
				if(i == startRowNum) {
					if(!e.head().equals(getString(i, e.col()))){
						throw new Exception("表头校验失败");
					}
				}
				//填充list
				field.setAccessible(true);
				try {
					//判断Field类型
					String type = field.getType().getName();
					Object value = null;
					switch (type) {
						case "java.util.Date":
							value = get(i+1, e.col(), RequireType.DATE);
							break;
						case "double":
						case "java.lang.Double":
							value = get(i+1, e.col(), RequireType.DOUBLE);
							break;
						case "int":
						case "java.lang.Integer":
							value = get(i+1, e.col(), RequireType.INT);
							break;
						case "boolean":
						case "java.lang.Boolean":
							value = get(i+1, e.col(), RequireType.BOOLEAN);
							break;
						case "java.lang.String":
							value = get(i+1, e.col(), RequireType.STRING);
							break;
						default:
							break;
					}
					field.set(entity, value);
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					logger.error("调用ExcelHelper.unfill()中field.set()方法时出错");
					e1.printStackTrace();
				}
			}
			entities.add(entity);
		}
		return entities;
	}
	

	/**
	 * 获取workbook
	 */
	public Workbook getBook() {
		return workbook;
	}

	/**
	 * 切换至指定sheet，失败返回false
	 */
	public boolean switchSheet(int sheetNum) {
		if(workbook.getSheetAt(sheetNum) != null) {
			this.currentSheetNum = sheetNum;
			return true;
		}else {
			return false;
		}
	}

	
	
	/**
	 * 切换至指定sheet，失败返回false
	 */
	public boolean switchSheet(String sheetName) {
		if(workbook.getSheet(sheetName) != null) {
			this.currentSheetNum = workbook.getSheetIndex(sheetName);
			return true;
		}else {
			return false;
		}
	}

	
	protected Object get(int rowNum, int colNum, RequireType requireType) {
		try {
			Cell cell = workbook.getSheetAt(currentSheetNum).getRow(rowNum).getCell(colNum);
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				switch (requireType) {
				case BOOLEAN:
					return cell.getBooleanCellValue();
				case STRING:
					return cell.getBooleanCellValue() ? "true" : "false";
				case INT:
					return cell.getBooleanCellValue() ? 1 : 0;
				case DATE:
					logger.error("无法把坐标为("+currentSheetNum+","+rowNum+","+colNum+")的布尔值转成日期");
					return null;
				case DOUBLE:
					return cell.getBooleanCellValue() ? 1.0d : 0.0d;
				}
			case Cell.CELL_TYPE_NUMERIC:
				switch (requireType) {
				case BOOLEAN:
					return cell.getNumericCellValue() != 0 ? true : false;
				case STRING:
					return String.valueOf((int)cell.getNumericCellValue());
				case INT:
					return (int)cell.getNumericCellValue();
				case DATE:
					return cell.getDateCellValue();
				case DOUBLE:
					return cell.getNumericCellValue();
				}
			case Cell.CELL_TYPE_FORMULA:
			case Cell.CELL_TYPE_STRING:
				switch (requireType) {
				case BOOLEAN:
					return cell.getStringCellValue().equals("") ? false : true;
				case STRING:
					return cell.getStringCellValue();
				case INT:
					return Integer.parseInt(cell.getStringCellValue());
				case DATE:
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cell.getStringCellValue());
				case DOUBLE:
					return Double.parseDouble(cell.getStringCellValue());
				}
			default:
				switch (requireType) {
				case BOOLEAN:
					return false;
				case STRING:
					return "";
				case INT:
					return 0;
				case DATE:
					logger.error("无法把坐标为("+currentSheetNum+","+rowNum+","+colNum+")的布尔值转成日期");
					return null;
				case DOUBLE:
					return 0.0d;
				default:
					return null;
				}
			}
		}catch (NullPointerException e) {
			logger.error("无法获取坐标为("+currentSheetNum+","+rowNum+","+colNum+")的值，该单元格可能为空");
			return null;
		}catch (NumberFormatException e) {
			logger.error("无法把坐标为("+currentSheetNum+","+rowNum+","+colNum+")的数值转成字符串");
			return null;
		}catch (ParseException e) {
			logger.error("无法把坐标为("+currentSheetNum+","+rowNum+","+colNum+")的字符串转成日期");
			return null;
		}
	}


	protected ExcelHelper(boolean isNewVersion) {
		//判断格式
		if(isNewVersion){
			workbook = new XSSFWorkbook();
		}else {
			workbook = new HSSFWorkbook();
		}
		workbook.createSheet();
		init();
	}


	protected ExcelHelper(File file) throws IOException {
		//判断格式
		if(file.getName().endsWith(".xlsx")){
			workbook = new XSSFWorkbook(new FileInputStream(file));
		}else {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		}
		init();
	}
	

	protected ExcelHelper() {
	}


	protected void init() {
		//默认表
		currentSheetNum = 0;
		//默认样式
		//创建表头样式
		headStyle = workbook.createCellStyle();
		headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		headStyle.setFillForegroundColor(new HSSFColor.GREY_25_PERCENT().getIndex());
		headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		Font headFont = workbook.createFont();
		headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headFont.setFontName("Arial");
		headFont.setFontHeightInPoints((short) 12);
		headStyle.setFont(headFont);
		//创建数据样式
		bodyStyle = workbook.createCellStyle();
		bodyStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		Font bodyFont = workbook.createFont();
		bodyFont.setFontName("Arial");
		bodyFont.setFontHeightInPoints((short) 10);
		bodyStyle.setFont(bodyFont);
	}
	
	
	protected void autoColumnWidth() {
		try {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (int j = 0; j < sheet.getRow(sheet.getLastRowNum()).getLastCellNum(); j++) {
					sheet.autoSizeColumn(j);
					sheet.setColumnWidth(j, sheet.getColumnWidth(j) + 4 *256);
					//设置上限
					if(sheet.getColumnWidth(j) >= 80 * 256) {
						sheet.setColumnWidth(j, 80 * 256);
					}
				}
			}
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
}
