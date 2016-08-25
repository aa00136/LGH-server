package com.lgh.spiter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 一个excel对象
 * @param T 是excel的行数据所对应的对象的类型
 * */
public class Excel<T> {

	
	/**
	 * 将一个List里面的T对象写入一个excel里面
	 * @param t  要把数据储存到excel的对象的集合
	 * @param storePath 模版文件的位置,支持xls和xlsx
	 * @param beginRow 从excel的下标为beginRow的行开始写数据（下标是从0开始的
	 * @param beginCol 从excel的x下标为beginCol的列开始写数据（下标是从0开始的）
	 * @param lists 需要或不需要写入excel的数据属性
	 * @param like 0表示像excel中写入除headers以外的属性值，要求实体类的属性的排列属性要和excel的一致.
	 * 			   1表示写入headers对应的属性值，要求headers与excel的解释一致
	 * 			   如需全部写入调用重载的CreateExcel(ArrayList<T> t,String storePath)方法
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
    public	Workbook CreateExcel(ArrayList<T> t,String storePath,int beginRow,int beginCol,String[] headers,int like) throws Exception {
		
		
		//获得模版文件
		FileInputStream fis = null;
		Workbook workBook = null;
		
		File file = new File(storePath);
		int type = 0;
		try {
			 	fis = new FileInputStream(file);
			 	workBook = new XSSFWorkbook(fis);
			 	type = 1;
	    } catch (Exception ex) {
	        	fis = new FileInputStream(file);
	        	workBook = new HSSFWorkbook(fis);
	    }
		//获得模版文件里面的第一个工作表
		Sheet sheet = workBook.getSheetAt(0);
		sheet.setVerticallyCenter(true);
		int end = beginRow + t.size();
		int j = 0;//记录要获得的order的下标
		
		//创建行
		if(like==1){
			for(int i = beginRow;i < end;i++) {
				
				Row row1 = createRowWithHeaders(sheet,t.get(j),i,beginCol,headers);
				j++;
			}
		}else if(like==0 || like==2){
			
			for(int i = beginRow;i < end;i++) {
				
				Row row1 = createRowWithoutHeaders(sheet,t.get(j),i,beginCol,headers);
				j++;
			}
		}
		
		return workBook;
	}

    /**
	 * 默认从第0行，第0列开始写数据，同时所有的属性都要写
	 * */
	public	Workbook CreateExcel(ArrayList<T> t,String storePath) throws Exception {
		return CreateExcel(t,storePath,0,0,new String[]{},2);
	}
	
	/**
	 * 创建一个excel表格的行，将extras数组中model属性以外的一行数据写入excel中
	 * @param sheet 行所在的工作表
	 * @param p 是要存储到excel里面的一个对象，对应一行的数据
	 * @param rowIndex 要创建的行的位置
	 * @param beginRow 从excel的beginCol的下一列开始写数据
	 **/
	private Row createRowWithoutHeaders(Sheet sheet,T p,int rowIndex,int beginCol,String[] headers) throws Exception{	
		Row row =  sheet.createRow(rowIndex);//创建一个row
		Class<? extends Object> t = p.getClass();//获得运行时的具体类
		Field[] fields = t.getDeclaredFields();//获得所有的域
		int i = beginCol;
		createCell(row,String.valueOf(rowIndex-2),0);
		for(Field f : fields) {
			int flag = 1;
			String fieldName = f.getName();
			//去除不用导出的属性
			if(headers!=null){
    			for(String sample : headers) {
    				if(sample.equals(fieldName)) {
    					flag = 0;//标识这个域不用写入excel
    				}
    			}
			}
			if(flag == 1) {
				String firstLater = fieldName.substring(0,1);
				String getMethod = "get"+firstLater.toUpperCase()+fieldName.substring(1);//获得域的名字
				Method m = t.getMethod(getMethod,new Class[]{});
				Object result = (Object)m.invoke(p,new Object[]{});
				String r = "";
				if(result instanceof String) {
	                r = (String)result;
	            } 
	            else if(result instanceof Integer) {
	                r = String.valueOf(result);
	            }else if(result instanceof java.util.Date){     //判断是否为Date型
	                r = String.valueOf(result);
	            }else if(result instanceof java.lang.Double){     //判断是否为Date型
	                r = String.valueOf(result);
	            }else if(result!=null||!"".equals(result)){
	                System.out.println("Error of createRow2,Unknow fieldName:"+fieldName);  //方面后面检错
	            }
		
				//System.out.println(r);
				
				createCell(row,r,i++);
			}
		}
		return row;
	}
	
	
	
	/**
	 * 功能：创建一个excel表格的行，按照likes数组中model属性值写入excel到一行中
	 * @param sheet
	 * @param p
	 * @param rowIndex  
	 * @param beginRow 
	 * @param likes  excel表头一一对应的model的字段集合
	 * @return
	 * @throws Exception
	 */
	private Row createRowWithHeaders(Sheet sheet,T p,int rowIndex,int beginRow,String[] headers) throws Exception{	
		Row row = (Row) sheet.createRow(rowIndex);//创建一个row
		Class<? extends Object> t = p.getClass();//获得运行时的具体类
		Field[] fields = t.getDeclaredFields();//获得所有的域
		int i = beginRow;
		
		for(String single : headers){
			String fieldName = null;
			for(Field f : fields) {
				
				fieldName = f.getName();
				if(fieldName.equals(single)){
					break;
				}
			}
			String firstLater = fieldName.substring(0,1);
			String getMethod = "get"+firstLater.toUpperCase()+fieldName.substring(1);//获得域的名字
			Method m = t.getMethod(getMethod,new Class[]{});
			Object result = (Object)m.invoke(p,new Object[]{});
			String r = "";
			if(result instanceof String) {
				r = (String)result;
			} 
			else if(result instanceof Integer) {
				r = String.valueOf(result);
			}else if(result instanceof java.util.Date){		//判断是否为Date型
				r = String.valueOf(result);
			}else if(result instanceof java.lang.Double){     //判断是否为Date型
                r = String.valueOf(result);
            }else if(result!=null||!"".equals(result)){
				System.out.println("Error of createRow2,Unknow fieldName");  //方面后面检错
			}
				System.out.println(r);
			createCell(row,r,i++);
		}
		
		return row;
	}
	
	/**
	 * 创建单元格
	 * @param row 要创建单元格的行数
 	 * @param string 
	 * @param index 要创建单元格的列数
	 */
	private void createCell(Row row,String string,int index) {
		Cell cell = row.createCell(index);
		cell.setCellValue(string);
		cell.setCellType(Cell.CELL_TYPE_STRING);	
	}
	
	/**
	 * 当找不到文件的时候，将会抛出异常
	 * @throws Exception 
	 * */
	public ArrayList<ArrayList<String>> readExcel(String fileName,String path) throws Exception {  
    	ArrayList<ArrayList<String>> Row =new ArrayList<ArrayList<String>>();  
        
    	Workbook workBook = null;  
		String filePath = path+File.separator+fileName;
        //System.out.println(filePath);
        try{
        	workBook=new XSSFWorkbook(filePath);
        }catch(OfficeXmlFileException ex){
        	workBook = new HSSFWorkbook(new FileInputStream(filePath)); 
        }
		  
		for (int numSheet = 0; numSheet < workBook.getNumberOfSheets(); numSheet++) {  
		    Sheet sheet = workBook.getSheetAt(numSheet);  //获取第一个工作表
		    if (sheet == null) {  
		        continue;  
		    }  
		    // 循环行Row,每一行对应一个
		    for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {  
		        Row row = sheet.getRow(rowNum);  
		        if (row == null) {  
		            continue;  
		        }  
		          
		        // 循环列Cell  
		        ArrayList<String> arrCell =new ArrayList<String>();   

		        for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {  
		        	Cell cell = row.getCell(cellNum);
		        	if (null == cell) {  
		               arrCell.add("");
		               continue;
		            }
		            arrCell.add(getValue(cell));  
		        }  
		        Row.add(arrCell);  
		    }  
		}
		workBook.close();
        return Row;  
    }  
    private String getValue(Cell cell) {  
    
    	 if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) { 
             return String.valueOf(cell.getBooleanCellValue());  
   
         } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
         	   //System.out.println(cell.getStringCellValue());
         	 return String.valueOf(cell.getStringCellValue()); 
         
         } else {  
         	if(HSSFDateUtil.isCellDateFormatted(cell)) {
         		String dateS = null;
         		if(cell.getDateCellValue()!=null){
         			Date date = cell.getDateCellValue();
         			SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");  
         			dateS = sdf.format(date);
         		}
         		return dateS;
         	}
         	else {
         	
         	BigDecimal db = new BigDecimal(cell.getNumericCellValue());
  		    String ii = db.toPlainString();
             return String.valueOf(ii);
         	}
         }  
        

    }  
  
    
    /**
     * 检查文件是否为excel文件（通过文件名检查）
     * @param fileName 文件的名字
     * */
    public static boolean checkFormat(String fileName) {
    	//获得文件的后缀
    	int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index+1);
        if(suffix.equals("xls") || suffix.equals("xlsx")) {
        	return true;
        }
        else 
        {
        	return false;
        }
    }
	
}
