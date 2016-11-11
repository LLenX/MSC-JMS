package DataManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;  
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



import fix.CsvWriter;
import fix.FileTool;

import java.lang.String;


public class dataStruct {
	//7-21
	//部分数据需要在Analysor中使用，因此修改定义为public
	public dataStruct() {
		// TODO Auto-generated constructor stub
		build_DF_Struct();
		build_Spr_effection();
	}
	/*
	 * 全社会用电量存储结构
	 */
	public Map<String, Double> aMap = new HashMap<String, Double>();
	public String Asmonth,Aemonth; //数据的开始月份和结束月份
	
	/*
	 * 分镇街用电量存储
	 */
	public ArrayList<String> town_Namelist = new ArrayList<String>();
	public ArrayList<HashMap<String, Double>> tMaps = new ArrayList<HashMap<String,Double>>();//为何这里不能用hashmap,上面不能用map？
	public String Tsmonth,Temonth;
	
	/*
	 * 宏观经济指标存储
	 */
	public ArrayList<String> drive_factor_Namelist = new ArrayList<String>();
	public ArrayList<Map<String, Double>> dfMaps = new ArrayList<Map<String,Double>>();
//	private HashMap<String, Double>[] dfMaps;
	public String DFsmonth,DFemonth;
	
	//在构造实例时设定为false，在读取时检查是否已经录入数据 （最终未使用，而改成在未录入数据即读取数据时报错。）
//	public boolean agot,tgot,dfgot;
	
	//过去五年到未来十年的春节活动时间。
	public HashMap<String, Integer> SpringEffect = new HashMap<String,Integer>();
	
	
	private String find_last_month(String month){
		int present,preyear,premonth;
		present = Integer.parseInt(month);
		preyear = present/100;
		premonth = present%100;
		if(premonth>1) return(Integer.toString(present-1));
		else return(Integer.toString((preyear-1)*100+12));
	}
	
	private String find_next_month(String month){
		int present,preyear,premonth;
		present = Integer.parseInt(month);
		preyear = present/100;
		premonth = present%100;
		if(premonth<12) return(Integer.toString(present+1));
		else return(Integer.toString((preyear+1)*100+1));
	}
	
	public void A_add(String month,double elect){
		aMap.put(month, elect);
	}
	
	public void T_add(String town,String month,double elect) {
		int index = town_Namelist.indexOf(town);
		if(index==-1){
			town_Namelist.add(town);
			HashMap<String,Double> newton = new HashMap<String,Double>();
			newton.put(month, elect);
			tMaps.add(newton);
		}
		else{
			tMaps.get(index).put(month, elect);
		}
	}
	
	/*
	 * 构建驱动因素的框架
	 * 在构造函数中使用
	 * 需要对建模进行修改的时候，在这里改动
	 * 必须在已经读取用电量信息之后，再往dfMaps里扔数据
	 */
	private void build_DF_Struct(){
		drive_factor_Namelist.add("用电量");
		drive_factor_Namelist.add("温度");
		drive_factor_Namelist.add("规模以上工业销售");
		drive_factor_Namelist.add("工业综合能源消耗量");
		drive_factor_Namelist.add("进出口总额");
		dfMaps.add(new HashMap<String,Double>());
		dfMaps.add(new HashMap<String,Double>());
		dfMaps.add(new HashMap<String,Double>());
		dfMaps.add(new HashMap<String,Double>());
		dfMaps.add(new HashMap<String,Double>());
	}
	
	/*
	 * 记录过去五年和未来十年的春节活动时间
	 */
	private void build_Spr_effection(){
		//21日影响期 前7后14
		SpringEffect.put("201201", 16);
		SpringEffect.put("201202", 5);
		SpringEffect.put("201302", 21);
		SpringEffect.put("201401", 8);
		SpringEffect.put("201402", 13);
		SpringEffect.put("201502", 17);
		SpringEffect.put("201503", 4);
		SpringEffect.put("201602", 21);
		SpringEffect.put("201701", 11);
		SpringEffect.put("201702", 10);
		SpringEffect.put("201802", 20);
		SpringEffect.put("201803", 1);
		SpringEffect.put("201901", 3);
		SpringEffect.put("201902", 18);
		SpringEffect.put("202001", 14);
		SpringEffect.put("202002", 7);
		SpringEffect.put("202102", 21);
		SpringEffect.put("202201", 7);
		SpringEffect.put("202202", 14);
		SpringEffect.put("202301", 17);
		SpringEffect.put("202302", 4);
		SpringEffect.put("202402", 21);
		SpringEffect.put("202501", 10);
		SpringEffect.put("202502", 11);
		SpringEffect.put("202602", 19);
		SpringEffect.put("202603", 2);
		SpringEffect.put("202701", 2);
		SpringEffect.put("202702", 19);
		

//		//尝试调整为28日，前10天后18天 +3 +4
//		SpringEffect.put("201201", 19);
//		SpringEffect.put("201202", 9);
//		SpringEffect.put("201301", 1);
//		SpringEffect.put("201302", 27);
//		SpringEffect.put("201401", 11);
//		SpringEffect.put("201402", 17);
//		SpringEffect.put("201502", 20);
//		SpringEffect.put("201503", 8);
//		SpringEffect.put("201601", 3);
//		SpringEffect.put("201602", 25);
//		SpringEffect.put("201701", 14);
//		SpringEffect.put("201702", 14);
	}
	
	
	/*
	 * df= 0,1,2,3 分别代表四个驱动因素
	 */
	public void DF_add(int df,String month,double numeric) {
		switch (df) {
		case 0:
			dfMaps.get(df).put(month, numeric);
			break;

		case 1:
			dfMaps.get(df).put(month, numeric*10000);
			break;
			
		case 2:
			dfMaps.get(df).put(month, numeric*100);
			break;
			
		case 3:
			dfMaps.get(df).put(month, numeric*1000);
			break;
			
		case 4:
			dfMaps.get(df).put(month, numeric*0.1);
			break;
			
		default:
			break;
		}
		
	}
	
	
	/*
	 * 检查模块
	 */
	
//1.检查总用电量，分镇街用电量的有效输入范围
	
//2.对宏观经济数据缺漏进行平滑

//3.对宏观经济数据进行预处理
	
	/*
	 * 输入模块
	 * 根据地址打开excel（csv），读取数据，并作记录。
	 * 在没有读取记录之前，一切输出请求皆设为违法。
	 * 
	 * A、T、DF、分别对应读取全社会用电量、分镇街用电量以及宏观经济指标
	 */
	public int A_reader(String filePath) throws IOException {
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		InputStream stream = new FileInputStream(filePath);
		Workbook wb = null;
		if (fileType.equals("xls")) {
		  wb = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
		  wb = new XSSFWorkbook(stream);
		} else {
		  System.out.println("您输入的excel格式不正确");
		  return 1;
		}
		Sheet sheet1 = wb.getSheetAt(0);
		/*
		 * 读取文件头
		 * 在全社会用电量读取中不需要。
		 */

		int rowEnd = sheet1.getLastRowNum();
		String[] monlist = new String[rowEnd+1];
		for (int i=1;i<=rowEnd;i++) {
			Row row = sheet1.getRow(i);
			String month;
			double elect;
			Cell cell0 = row.getCell(0);
			Cell cell1 = row.getCell(1);
			if((cell0==null)||(cell1==null)) {
				System.out.print("null Error ");
				/*
				 * 存在数据缺失
				 * 在后续中添加报告数据缺失的动作
				 */
				return -1;
	   	  	}
			if((cell0.getCellType()!=Cell.CELL_TYPE_NUMERIC)||(cell1.getCellType()!=Cell.CELL_TYPE_NUMERIC)) {
				System.out.print("nonumeric error  ");
				/*
				 * 存在非数值数据
				 * 在后续中添加报告数据错误的动作
				 */
				return -2;
	   	  	}
			month = Integer.toString((int)cell0.getNumericCellValue());
			monlist[i]= month;
			elect = cell1.getNumericCellValue();
			A_add(month, elect);
//			System.out.println(month+' '+elect);
		}
		Asmonth = monlist[1];
//		System.out.println("Asmonth="+Asmonth);
		Aemonth = monlist[rowEnd];
//		System.out.println("Aemonth="+Aemonth);
		return 0;
		
	}
	
	public int T_reader(String filePath) throws IOException {
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		InputStream stream = new FileInputStream(filePath);
		Workbook wb = null;
		if (fileType.equals("xls")) {
		  wb = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
		  wb = new XSSFWorkbook(stream);
		} else {
		  System.out.println("您输入的excel格式不正确");//届时将有窗口弹出，提醒格式不可用
		}
		Sheet sheet1 = wb.getSheetAt(0);
		/*
		 * 读取文件头
		 * 记录镇街名称
		 */
		Row namerow = sheet1.getRow(0);
		String[] name = new String[namerow.getLastCellNum()];
/*		if(namerow.getFirstCellNum()!=1){
			System.out.println("发生错位错误");
			return -3;
		}
//		for(int i=namerow.getFirstCellNum();i<namerow.getLastCellNum();i++){*/
		for(int i=namerow.getFirstCellNum()+1;i<namerow.getLastCellNum();i++){	
			Cell cell = namerow.getCell(i);
			if(cell==null){
				System.out.println("数据缺失");
				return -1;
			}
			if(cell.getCellType()!=Cell.CELL_TYPE_STRING){
				System.out.println("镇街名必须是文字不能是数字");
				return -4;						
			}
			name[i]=cell.getStringCellValue();
//			System.out.print(name[i]+" * ");
		}
//		System.out.println("\n");
		
		/*
		 * 读取用电量和月份数据
		 * 
		 */
		int rowEnd = sheet1.getLastRowNum();
		String[] monlist = new String[rowEnd+1];

		
		for (int i=1;i<=rowEnd;i++) {
			/*
			 * 检查是否有空行
			 */
			Row row = sheet1.getRow(i);
			if(row==null){
				System.out.println("出现行缺失的错误");
				return -1;
			}
			
			/*
			 * 先读取开头的年月数据
			 * monlist到底是否还必要？
			 */
			String month;
			Cell monthcell = row.getCell(0);
			if (monthcell.getCellType()==Cell.CELL_TYPE_NUMERIC){
				month = Integer.toString((int)monthcell.getNumericCellValue());
				monlist[i]=month;
			}
			else{
				System.out.println("年月数据不是数字");
				return -5;//现在return有点乱，等写完了再统一分批
			}
			/*
			 * 读入镇街数据
			 */
			for(int j=1;j<row.getLastCellNum();j++){//为什么不是<=？
				Cell cell = row.getCell(j);
				if(cell==null){
					System.out.println("数据缺失");
					return -1;
				}
				if(cell.getCellType()!=Cell.CELL_TYPE_NUMERIC){
					System.out.println("用电量必须是数字是数字");
					return -4;						
				}
				double elect = cell.getNumericCellValue();
				T_add(name[j], month, elect);
//				System.out.print(elect+"  ");
			}
//			System.out.println("\n");
			
		}
		Tsmonth = monlist[1];
//		System.out.println("Tsmonth="+Tsmonth);
		Temonth = monlist[rowEnd];
//		System.out.println("Temonth="+Temonth);
		return 0;
		
	}
	
	public int DF_reader(String filePath) throws IOException {
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		InputStream stream = new FileInputStream(filePath);
		Workbook wb = null;
		if (fileType.equals("xls")) {
		  wb = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
		  wb = new XSSFWorkbook(stream);
		} else {
		  System.out.println("您输入的excel格式不正确");//届时将有窗口弹出，提醒格式不可用
		}
		Sheet sheet1 = wb.getSheetAt(0);
		/*
		 * 读取文件头
		 * 判断数据是否齐备。顺序是否正确
		 * 判断数据正确较麻烦。先留空，直接读取数据内容
		 */
//		Row namerow = sheet1.getRow(0);
//		String[] name = new String[namerow.getLastCellNum()];
//		if(namerow.getFirstCellNum()!=1){
//			System.out.println("发生错位错误");
//			return -3;
//		}
//		for(int i=namerow.getFirstCellNum();i<namerow.getLastCellNum();i++){
//			
//			Cell cell = namerow.getCell(i);
//			if(cell==null){
//				System.out.println("数据缺失");
//				return -1;
//			}
//			if(cell.getCellType()!=Cell.CELL_TYPE_STRING){
//				System.out.println("镇街名必须是文字不能是数字");
//				return -4;						
//			}
//			name[i]=cell.getStringCellValue();
//			System.out.print(name[i]+" * ");
//		}
//		System.out.println("\n");
//		
		/*
		 * 读取用电量和月份数据
		 * 
		 */
		int rowEnd = sheet1.getLastRowNum();
		String[] monlist = new String[rowEnd+1];

		
		for (int i=1;i<=rowEnd;i++) {
			/*
			 * 检查是否有空行
			 */
			Row row = sheet1.getRow(i);
			if(row==null){
				System.out.println("出现行缺失的错误");
				return -1;
			}
			
			/*
			 * 先读取开头的年月数据
			 * monlist到底是否还必要？
			 */
			String month;
			Cell monthcell = row.getCell(0);
			if (monthcell.getCellType()==Cell.CELL_TYPE_NUMERIC){
				month = Integer.toString((int)monthcell.getNumericCellValue());
//				System.out.println("month="+month);
				monlist[i]=month;
			}
			else{
				System.out.println("年月数据不是数字");
				return -5;//现在return有点乱，等写完了再统一分批
			}
			/*
			 * 读入宏观经济数据
			 */
			DF_add(0, month, aMap.get(month));
			for(int j=1;j<row.getLastCellNum();j++){//为什么不是<=？
				Cell cell = row.getCell(j);
				if(cell==null){
					System.out.println("宏观经济数据缺失");
					return -1;
				}
				if(cell.getCellType()!=Cell.CELL_TYPE_NUMERIC){
					System.out.println("宏观经济数据必须是数字");
					return -4;						
				}
				double numer= cell.getNumericCellValue();
				DF_add(j, month, numer);
//				System.out.print(numer+"  ");
			}
//			System.out.println("\n");
			
		}
		DFsmonth = monlist[0];
		System.out.println("dfsmonth="+DFsmonth);
		DFemonth = monlist[rowEnd];
		System.out.println("Aemonth="+DFemonth);
		return 0;
		
	}
	
	
	/*
	 * 输出模块，根据不同的问题需求提取相应的数据到csv中，并保存到指定位置供R脚本使用。
	 * 多个方法，针对全社会，分镇街，副模型等不同的问题。
	 * return： 
	 * info 大于等于0， 需要跳多少个月预测。
	 * 小于0，表示发生不同的错误问题。
	 */
	
	/*
	 * 1.提供全社会用电量年度预测的数据
	 * 根据Asmonth和Aemonth判断能够预测的年度。
	 * 生成all.csv
	 * 根据需要预测的区域和训练集区域，生成spring,resmo,starttime
	 * 根据预测的季度、半年度，确定是否需要逆平滑。
	 * 返回结构：
	 * jump：需要跳过的月份数
	 * 若是1-3，需跳过1-3个月
	 * 若是4，说明缺失月份数>3，相当于数据缺失错误。
	 * 若返回-1~-11，说明本年度多少个月的数据已获得。
	 * 若返回-12，说明本年度所有数据已获得，无需再预测。
	 */
	public int a_Y_dataProvide(int year,String path) throws IOException{
		//0.先检查有无足够数据,只能提前3个月
		String month = Integer.toString((year-3)*100+1);
		while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString((year-1)*100+9)))){
			month = find_next_month(month);
		}
		if(aMap.get(month)==null){
			return 4;
		}
		//1.生成all.csv
		CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
//		writer.write("all");//不换行的
		String[] header = {"all"};
		writer.writeRecord(header);
		month = Integer.toString((year-3)*100+1);
		//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
		//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
		while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//			System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
			String[] content = {Double.toString(aMap.get(month))};
			writer.writeRecord(content);
			month = find_next_month(month);
		}
		//如果该时间段中的所有数据都已经获得，显示错误，不再提供数据
		if(month.equals(Integer.toString(year*100+101))){
			System.out.println("该年所有月份数据都已获得，无需预测");
			writer.close();
			return -12;
		}
		else{
			//检查最后一个月是什么时候，返回需要跳多少月。
			int jump;
			if((aMap.get(month)==null)&&(Integer.parseInt(month)<(year*100+1))){//如果数据库中有的数据不到year当年1月
				jump = (year-1)*100+13-Integer.parseInt(month);
			}
			else jump = -Integer.parseInt(month)+(year*100+1);
			writer.close();
			//最终得到的month，就是需要预测的第一个月。
			//现用Aemonth检验，得到month是否正确。
			if(month.equals(find_next_month(Aemonth))) System.out.println("month correct! from dataStruct, a_Y_allPred");
			else System.out.println("month wrong! from dataStruct, a_Y_allPred");
			/*
			 * 7-21
			 * 因为现在需要根据已获得数据动态的预测，因此spring、resmo、starttime的生成取决于现有数据到哪一个月。
			 * 
			 */
			
			//2.生成spring.csv
			//need_resmo 检验是否需要提供resmo
			boolean need_resmo = false;
			writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
			for(int y = year-3;y<year;y++){
				int pre = 0;
				for(int m = 1;m<=3;m++){
					if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//						System.out.println("year="+y);
						String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(y*100+m));
					}
				}
			}
				//检验本年的数据是否已获得，将已获得的部分加入spring	
			int	pre=0;
			for(int m = 1;m<=3;m++){
				if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)<Integer.parseInt(month))){
					String[] content = {Integer.toString(36+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
					writer.writeRecord(content);
					pre = pre+SpringEffect.get(Integer.toString(year*100+m));
				}
				if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)>=Integer.parseInt(month))){
					need_resmo = true;
					//如果存在本年度需平滑月份在需预测范围内，则需要提供resmo
					//上级程序同样通过检查本年度需平滑月份是否在需预测范围内（根据本方法返回的jump），再次判断是否需要平滑。
				}
			}
			writer.close();
			//3.生成resmo.csv
			if(need_resmo){
				writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
				pre = 0;
				for(int m = 1;m<=3;m++){
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)>=Integer.parseInt(month))){
						String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(year*100+m));
					}
				}
				writer.close();
			}
			//4.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
//			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month);
			String[] content2;
			if(jump>=0){
				String[] content21 = {Integer.toString(year),Integer.toString(1)};
				content2=content21;
			}
			else{
				String[] content22 = {month.substring(0, 4),month.substring(4,6)};
				content2 = content22;
			}
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return jump;
		}
		
	}

	/*
	 * 2.提供全社会用电量半年度预测的数据
	 * 根据Asmonth和Aemonth判断能够预测的年度。
	 * 生成all.csv
	 * 根据需要预测的区域和训练集区域，生成spring,resmo,starttime
	 * 根据预测的季度、半年度，确定是否需要逆平滑。
	 * 返回结构：
	 * jump：需要跳过的月份数
	 * 若是1-3，需跳过1-3个月
	 * 若是4，说明缺失月份数>3，相当于数据缺失错误。
	 * 若返回-1~-5，说明本年度多少个月的数据已获得。
	 * 若返回-6，说明本半年度所有数据已获得，无需再预测。
	 */
	public int a_HY_dataProvide(int year,String path,int uod)throws IOException{
		/*
		 * uod==0上半年，需要resmo 和年度数据供给没有太大区别。
		 * uod==1下半年，无需resmo。但data要加上本年上半年
		 */
		if(uod==0){
			//0.先检查有无足够数据,只能提前3个月
			String month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString((year-1)*100+9)))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return 4;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
//			writer.write("all");//不换行的
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+7)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			if(month.equals(Integer.toString(year*100+7))){
				System.out.println("该年所有月份数据都已获得，无需预测");
				return -6;
			}
			else{
				//检查最后一个月是什么时候，返回需要跳多少月。
				int jump;
				if((aMap.get(month)==null)&&(Integer.parseInt(month)<(year*100+1))){//如果数据库中有的数据不到year当年1月
					jump = (year-1)*100+13-Integer.parseInt(month);
				}
				else jump = -Integer.parseInt(month)+(year*100+1);
				writer.close();
				//最终得到的month，就是需要预测的第一个月。
				//现用Aemonth检验，得到month是否正确。
				if(month.equals(find_next_month(Aemonth))) {
					System.out.println("month correct! from dataStruct, a_Y_allPred");
				}
				else {
					System.out.println("month wrong! from dataStruct, a_Y_allPred");
				}
				/*
				 * 7-21
				 * 因为现在需要根据已获得数据动态的预测，因此spring、resmo、starttime的生成取决于现有数据到哪一个月。
				 * 
				 */
				
				//2.生成spring.csv
				//need_resmo 检验是否需要提供resmo
				boolean need_resmo = false;
				writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
				for(int y = year-3;y<year;y++){
					int pre = 0;
					for(int m = 1;m<=3;m++){
						if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//							System.out.println("year="+y);
							String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
							writer.writeRecord(content);
							pre = pre+SpringEffect.get(Integer.toString(y*100+m));
						}
					}
				}
					//检验本年的数据是否已获得，将已获得的部分加入spring	
				int	pre=0;
				for(int m = 1;m<=3;m++){
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)<Integer.parseInt(month))){
						String[] content = {Integer.toString(36+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(year*100+m));
					}
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)>=Integer.parseInt(month))){
						need_resmo = true;
						//如果存在本年度需平滑月份在需预测范围内，则需要提供resmo
						//上级程序同样通过检查本年度需平滑月份是否在需预测范围内（根据本方法返回的jump），再次判断是否需要平滑。
					}
				}
				writer.close();
				//3.生成resmo.csv
				if(need_resmo)
				writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
				pre = 0;
				for(int m = 1;m<=3;m++){
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)>=Integer.parseInt(month))){
						String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(year*100+m));
					}
				}
				writer.close();
				//4.生成starttime.csv
				writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
				String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
				writer.writeRecord(content1);
//				String[] content2 = {Integer.toString(year),Integer.toString(1)};
//				System.out.println(month);
				String[] content2 = {month.substring(0, 4),month.substring(4,6)};
//				System.out.println(month.substring(0,4));
//				System.out.println(month.substring(4,6));
				writer.writeRecord(content2);
				writer.close();
				return jump;
			}
			
		}
		else{
			/*
			 * 预测下半年度，还需要上半年度的数据。
			 */
			//0.先检查有无足够数据,只能提前3个月
			String month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+3)))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return 4;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
//			writer.write("all");//不换行的
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			
			
			if(month.equals(Integer.toString(year*100+101))){
				System.out.println("该年所有月份数据都已获得，无需预测");
				return -6;
			}
			else{
				//检查最后一个月是什么时候，返回需要跳多少月。
				int jump;
				if((aMap.get(month)==null)&&(Integer.parseInt(month)<(year*100+7))){//如果数据库中有的数据不到year当年7月
					jump = year*100+7-Integer.parseInt(month);
				}
				else jump = -Integer.parseInt(month)+(year*100+7);
				writer.close();
				//最终得到的month，就是需要预测的第一个月。
				//现用Aemonth检验，得到month是否正确。
				if(month.equals(find_next_month(Aemonth))) System.out.println("month correct! from dataStruct, a_HY_allPred");
				else System.out.println("month wrong! from dataStruct, a_HY_allPred");
				/*
				 * 7-21
				 * 因为现在需要根据已获得数据动态的预测，因此spring、resmo、starttime的生成取决于现有数据到哪一个月。
				 * 
				 */
				
				//2.生成spring.csv
				//need_resmo 检验是否需要提供resmo
				boolean need_resmo = false;
				writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
				for(int y = year-3;y<=year;y++){
					int pre = 0;
					for(int m = 1;m<=3;m++){
						if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//							System.out.println("year="+y);
							String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
							writer.writeRecord(content);
							pre = pre+SpringEffect.get(Integer.toString(y*100+m));
						}
					}
				}
				writer.close();
				
				//4.生成starttime.csv
				writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
				String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
				writer.writeRecord(content1);
				String[] content2;
				if(jump>=0){
					String[] content21 = {Integer.toString(year),Integer.toString(7)};
					content2=content21;
				}
				else{
					String[] content22 = {month.substring(0, 4),month.substring(4,6)};
					content2 = content22;
				}
//				String[] content2 = {Integer.toString(year),Integer.toString(1)};
//				System.out.println(month);
				
//				System.out.println(month.substring(0,4));
//				System.out.println(month.substring(4,6));
				writer.writeRecord(content2);
				writer.close();
				return jump;
			}
		}
	}

	public int a_S_dataProvide(int year,String path,int season)throws IOException {
		/*
		 * season==1第一季度，需要resmo 和年度数据供给没有太大区别。
		 * season!=1非第一季度，无需resmo。但data要加上本年前几个月
		 */
		if(season==1){
			//0.先检查有无足够数据,只能提前3个月
			String month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString((year-1)*100+9)))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return 4;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
//			writer.write("all");//不换行的
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+7)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			if(month.equals(Integer.toString(year*100+4))){
				System.out.println("该年所有月份数据都已获得，无需预测");
				return -3;
			}
			else{
				//检查最后一个月是什么时候，返回需要跳多少月。
				int jump;
				if((aMap.get(month)==null)&&(Integer.parseInt(month)<(year*100+1))){//如果数据库中有的数据不到year当年1月
					jump = (year-1)*100+13-Integer.parseInt(month);
				}
				else jump = -Integer.parseInt(month)+(year*100+1);
				writer.close();
				//最终得到的month，就是需要预测的第一个月。
				//现用Aemonth检验，得到month是否正确。
				if(month.equals(find_next_month(Aemonth))) {
					System.out.println("month correct! from dataStruct, a_S_allPred");
				}
				else {
					System.out.println("month wrong! from dataStruct, a_S_allPred");
				}
				/*
				 * 7-21
				 * 因为现在需要根据已获得数据动态的预测，因此spring、resmo、starttime的生成取决于现有数据到哪一个月。
				 * 
				 */
				
				//2.生成spring.csv
				//need_resmo 检验是否需要提供resmo
				boolean need_resmo = false;
				writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
				for(int y = year-3;y<year;y++){
					int pre = 0;
					for(int m = 1;m<=3;m++){
						if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//							System.out.println("year="+y);
							String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
							writer.writeRecord(content);
							pre = pre+SpringEffect.get(Integer.toString(y*100+m));
						}
					}
				}
					//检验本年的数据是否已获得，将已获得的部分加入spring	
				int	pre=0;
				for(int m = 1;m<=3;m++){
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)<Integer.parseInt(month))){
						String[] content = {Integer.toString(36+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(year*100+m));
					}
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)>=Integer.parseInt(month))){
						need_resmo = true;
						//如果存在本年度需平滑月份在需预测范围内，则需要提供resmo
						//上级程序同样通过检查本年度需平滑月份是否在需预测范围内（根据本方法返回的jump），再次判断是否需要平滑。
					}
				}
				writer.close();
				//3.生成resmo.csv
				if(need_resmo)
				writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
				pre = 0;
				for(int m = 1;m<=3;m++){
					if((SpringEffect.get(Integer.toString(year*100+m))!=null)&&((year*100+m)>=Integer.parseInt(month))){
						String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(year*100+m));
					}
				}
				writer.close();
				//4.生成starttime.csv
				writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
				String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
				writer.writeRecord(content1);
//				String[] content2 = {Integer.toString(year),Integer.toString(1)};
//				System.out.println(month);
				String[] content2;
				if(jump>=0){
					String[] content21 = {Integer.toString(year),Integer.toString(1)};
					content2=content21;
				}
				else{
					String[] content22 = {month.substring(0, 4),month.substring(4,6)};
					content2 = content22;
				}
				
//				System.out.println(month.substring(0,4));
//				System.out.println(month.substring(4,6));
				writer.writeRecord(content2);
				writer.close();
				return jump;
			}
			
		}
		else{
			/*
			 * 预测下半年度，还需要上半年度的数据。
			 */
			//0.先检查有无足够数据,只能提前3个月
			String month = Integer.toString((year-3)*100+1);
			String lastmonth = find_last_month(Integer.toString(year*100+(season-2)*3+1));
			while ((aMap.get(month)!=null)&&(!month.equals(lastmonth))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return 4;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
//			writer.write("all");//不换行的
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			String nextmonth = find_next_month(Integer.toString(year*100+season*3));
			while ((aMap.get(month)!=null)&&(!month.equals(nextmonth))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			
			
			if(month.equals(nextmonth)){
				System.out.println("该季度所有月份数据都已获得，无需预测");
				return -3;
			}
			else{
				//检查最后一个月是什么时候，返回需要跳多少月。
				int jump;
				if((aMap.get(month)==null)&&(Integer.parseInt(month)<(year*100+1+(season-1)*3))){//如果数据库中有的数据不到year当年前季
					jump = year*100+1+(season-1)*3-Integer.parseInt(month);
				}
				else jump = -Integer.parseInt(month)+(year*100+1+(season-1)*3);
				writer.close();
				//最终得到的month，就是需要预测的第一个月。
				//现用Aemonth检验，得到month是否正确。
				if(month.equals(find_next_month(Aemonth))) System.out.println("month correct! from dataStruct, a_S_allPred");
				else System.out.println("month wrong! from dataStruct, a_S_allPred");
				/*
				 * 7-21
				 * 因为现在需要根据已获得数据动态的预测，因此spring、resmo、starttime的生成取决于现有数据到哪一个月。
				 * 
				 */
				
				//2.生成spring.csv
				//need_resmo 检验是否需要提供resmo
				boolean need_resmo = false;
				writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
				for(int y = year-3;y<=year;y++){
					int pre = 0;
					for(int m = 1;m<=3;m++){
						if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//							System.out.println("year="+y);
							String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
							writer.writeRecord(content);
							pre = pre+SpringEffect.get(Integer.toString(y*100+m));
						}
					}
				}
				writer.close();
				
				//4.生成starttime.csv
				writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
				String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
				writer.writeRecord(content1);
//				String[] content2 = {Integer.toString(year),Integer.toString(1)};
//				System.out.println(month);
				String[] content2;
				if(jump>=0){
					String[] content21 = {Integer.toString(year),Integer.toString(season*3-2)};
					content2=content21;
				}
				else{
					String[] content22 = {month.substring(0, 4),month.substring(4,6)};
					content2 = content22;
				}
//				System.out.println(month.substring(0,4));
//				System.out.println(month.substring(4,6));
				writer.writeRecord(content2);
				writer.close();
				return jump;
			}
		}
	}
	
	/*
	 * 数据的完整性由公司保证，这里假定只要有一个镇街该月有值，则其他镇街也有值。
	 */
	public int t_Y_dataProvide(int year,String path) throws IOException{
		//0.先检查有无足够数据,只能提前3个月
		String month = Integer.toString((year-3)*100+1);
		while ((tMaps.get(1).get(month)!=null)&&(!month.equals(Integer.toString((year-1)*100+9)))){
			month = find_next_month(month);
		}
		if(tMaps.get(1).get(month)==null){
			return 4;
		}
		//1.生成all.csv
		CsvWriter writer = new CsvWriter(path+"town.csv",',',Charset.forName("GB18030"));
//		writer.write("all");//不换行的
		String[] header = town_Namelist.toArray(new String[town_Namelist.size()]);
		writer.writeRecord(header);
		month = Integer.toString((year-3)*100+1);
		//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
		//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
		while ((tMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//			System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
			String[] content = new String[town_Namelist.size()];
			for(int i=0;i<town_Namelist.size();i++){
				content[i] = Double.toString(tMaps.get(i).get(month));
			}
			writer.writeRecord(content);
			month = find_next_month(month);
		}
		if(month.equals(Integer.toString(year*100+101))){
			System.out.println("该年所有月份数据都已获得，无需预测");
			return -12;
		}
		else{
			//检查最后一个月是什么时候，返回需要跳多少月。
			int jump;
			if((tMaps.get(0).get(month)==null)&&(Integer.parseInt(month)<(year*100+1))){//如果数据库中有的数据不到year当年1月
				jump = (year-1)*100+13-Integer.parseInt(month);
			}
			else jump = -Integer.parseInt(month)+(year*100+1);
			writer.close();
			//最终得到的month，就是需要预测的第一个月。
			//现用Aemonth检验，得到month是否正确。
			if(month.equals(find_next_month(Temonth))) System.out.println("month correct! from dataStruct, t_Y_allPred");
			else System.out.println("month wrong! from dataStruct, t_Y_allPred");
			
			
			//4.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
//			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month);
			String[] content2;
			if(jump>=0){
				String[] content21 = {Integer.toString(year),Integer.toString(1)};
				content2=content21;
			}
			else{
				String[] content22 = {month.substring(0, 4),month.substring(4,6)};
				content2 = content22;
			}
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return jump;
		}
	}	

	public int t_HY_dataProvide(int year,String path,int uod) throws IOException{
		if(uod==0){
			//0.先检查有无足够数据,只能提前3个月
			String month = Integer.toString((year-3)*100+1);
			while ((tMaps.get(1).get(month)!=null)&&(!month.equals(Integer.toString((year-1)*100+9)))){
				month = find_next_month(month);
			}
			if(tMaps.get(1).get(month)==null){
				return 4;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"town.csv",',',Charset.forName("GB18030"));
//			writer.write("all");//不换行的
			String[] header = town_Namelist.toArray(new String[town_Namelist.size()]);
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
			//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
			while ((tMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+7)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = new String[town_Namelist.size()];
				for(int i=0;i<town_Namelist.size();i++){
					content[i] = Double.toString(tMaps.get(i).get(month));
				}
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			if(month.equals(Integer.toString(year*100+7))){
				System.out.println("该半年所有月份数据都已获得，无需预测");
				return -6;
			}
			else{
				//检查最后一个月是什么时候，返回需要跳多少月。
				int jump;
				if((tMaps.get(0).get(month)==null)&&(Integer.parseInt(month)<(year*100+1))){//如果数据库中有的数据不到year当年1月
					jump = (year-1)*100+13-Integer.parseInt(month);
				}
				else jump = -Integer.parseInt(month)+(year*100+1);
				writer.close();
				//最终得到的month，就是需要预测的第一个月。
				//现用Aemonth检验，得到month是否正确。
				if(month.equals(find_next_month(Temonth))) System.out.println("month correct! from dataStruct, t_HY_allPred");
				else System.out.println("month wrong! from dataStruct, t_HY_allPred");
				
				
				//4.生成starttime.csv
				writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
				String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
				writer.writeRecord(content1);
//				String[] content2 = {Integer.toString(year),Integer.toString(1)};
//				System.out.println(month);
				String[] content2;
				if(jump>=0){
					String[] content21 = {Integer.toString(year),Integer.toString(1)};
					content2=content21;
				}
				else{
					String[] content22 = {month.substring(0, 4),month.substring(4,6)};
					content2 = content22;
				}
//				System.out.println(month.substring(0,4));
//				System.out.println(month.substring(4,6));
				writer.writeRecord(content2);
				writer.close();
				return jump;
			}
		}
		else{
			//0.先检查有无足够数据,只能提前3个月
			String month = Integer.toString((year-3)*100+1);
			while ((tMaps.get(1).get(month)!=null)&&(!month.equals(Integer.toString((year)*100+3)))){
				month = find_next_month(month);
			}
			if(tMaps.get(1).get(month)==null){
				return 4;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"town.csv",',',Charset.forName("GB18030"));
//			writer.write("all");//不换行的
			String[] header = town_Namelist.toArray(new String[town_Namelist.size()]);
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
			//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
			while ((tMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = new String[town_Namelist.size()];
				for(int i=0;i<town_Namelist.size();i++){
					content[i] = Double.toString(tMaps.get(i).get(month));
				}
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			if(month.equals(Integer.toString(year*100+101))){
				System.out.println("该年所有月份数据都已获得，无需预测");
				return -6;
			}
			else{
				//检查最后一个月是什么时候，返回需要跳多少月。
				int jump=year*100+7-Integer.parseInt(month);
				
				writer.close();
				//最终得到的month，就是需要预测的第一个月。
				//现用Aemonth检验，得到month是否正确。
				if(month.equals(find_next_month(Temonth))) System.out.println("month correct! from dataStruct, t_HY_allPred");
				else System.out.println("month wrong! from dataStruct, t_HY_allPred");
				
				
				//4.生成starttime.csv
				writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
				String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
				writer.writeRecord(content1);
//				String[] content2 = {Integer.toString(year),Integer.toString(1)};
//				System.out.println(month);
				String[] content2;
				if(jump>=0){
					String[] content21 = {Integer.toString(year),Integer.toString(7)};
					content2=content21;
				}
				else{
					String[] content22 = {month.substring(0, 4),month.substring(4,6)};
					content2 = content22;
				}
//				System.out.println(month.substring(0,4));
//				System.out.println(month.substring(4,6));
				writer.writeRecord(content2);
				writer.close();
				return jump;
			}
		}
		
	}

	/*
	 * 7-27修改 添加半年度、季度检验的全社会用电量数据分配功能。
	 * 以及半年度的分镇街用电量检验功能
	 * 返回1/0,分别表示能够做该年的检验/数据不足不能做该年的检验
	 */
	public boolean a_Y_Check_dataProvide(int year,String path)throws IOException {
		String month = Integer.toString((year-3)*100+1);
		while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+12)))){
			month = find_next_month(month);
		}
		if(aMap.get(month)==null){
			return false;
		}
		//1.生成all.csv
		CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
		String[] header = {"all"};
		writer.writeRecord(header);
		month = Integer.toString((year-3)*100+1);
		//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
		//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
		while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//			System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
			String[] content = {Double.toString(aMap.get(month))};
			writer.writeRecord(content);
			month = find_next_month(month);
		}		
		writer.close();
		//2.生成spring.csv
		//need_resmo 检验是否需要提供resmo
		writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
		for(int y = year-3;y<year;y++){
			int pre = 0;
			for(int m = 1;m<=3;m++){
				if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//					System.out.println("year="+y);
					String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
					writer.writeRecord(content);
					pre = pre+SpringEffect.get(Integer.toString(y*100+m));
				}
			}
		}
		writer.close();
		
		//3.生成resmo.csv
		writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
		int pre = 0;
		for(int m = 1;m<=3;m++){
			if(SpringEffect.get(Integer.toString(year*100+m))!=null){
				String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
				writer.writeRecord(content);
				pre = pre+SpringEffect.get(Integer.toString(year*100+m));
			}
		}
		writer.close();
		
		//4.生成starttime.csv
		writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
		String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
		writer.writeRecord(content1);
		String[] content2 = {Integer.toString(year),Integer.toString(1)};
//		System.out.println(month);
		
//		System.out.println(month.substring(0,4));
//		System.out.println(month.substring(4,6));
		writer.writeRecord(content2);
		writer.close();
		return true;
	}

	public boolean a_HY_Check_dataProvide(int year,String path,int uod)throws IOException{
		if(uod==0){//上半年
			String month = Integer.toString((year-3)*100+1);
			
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+6)))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return false;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
			//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+7)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}		
			writer.close();
			//2.生成spring.csv
			writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
			for(int y = year-3;y<year;y++){
				int pre = 0;
				for(int m = 1;m<=3;m++){
					if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//						System.out.println("year="+y);
						String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(y*100+m));
					}
				}
			}
			writer.close();
			
			//3.生成resmo.csv
			writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
			int pre = 0;
			for(int m = 1;m<=3;m++){
				if(SpringEffect.get(Integer.toString(year*100+m))!=null){
					String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
					writer.writeRecord(content);
					pre = pre+SpringEffect.get(Integer.toString(year*100+m));
				}
			}
			writer.close();
			
			//4.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month);
			
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return true;
		}
		else {//uod==1 下半年 无需resmo
			String month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+12)))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return false;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
			//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}		
			writer.close();
			//2.生成spring.csv
			//need_resmo 检验是否需要提供resmo
			writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
			for(int y = year-3;y<=year;y++){
				int pre = 0;
				for(int m = 1;m<=3;m++){
					if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//						System.out.println("year="+y);
						String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(y*100+m));
					}
				}
			}
			writer.close();
			//4.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
			String[] content2 = {Integer.toString(year),Integer.toString(7)};
//			System.out.println(month);
			
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return true;
		}
	}
	
	public boolean a_S_Check_dataProvide(int year,String path,int season)throws IOException {
		if(season==1){//第一季度
			String month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+3)))){
//				System.out.println(month);
				month=find_next_month(month);
			}
			if(aMap.get(month)==null){
				return false;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
			//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+4)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}		
			writer.close();
			//2.生成spring.csv
			writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
			for(int y = year-3;y<year;y++){
				int pre = 0;
				for(int m = 1;m<=3;m++){
					if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//						System.out.println("year="+y);
						String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(y*100+m));
					}
				}
			}
			writer.close();
			
			//3.生成resmo.csv
			writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
			int pre = 0;
			for(int m = 1;m<=3;m++){
				if(SpringEffect.get(Integer.toString(year*100+m))!=null){
					String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
					writer.writeRecord(content);
					pre = pre+SpringEffect.get(Integer.toString(year*100+m));
				}
			}
			writer.close();
			
			//4.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month);
			
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return true;
		}
		else {//season!=1 非第一季度 无需resmo
			String month = Integer.toString((year-3)*100+1);
			while ((aMap.get(month)!=null)&&(!month.equals(Integer.toString(year*100+season*3)))){
				month = find_next_month(month);
			}
			if(aMap.get(month)==null){
				return false;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"all.csv",',',Charset.forName("GB18030"));
			String[] header = {"all"};
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			//7-21修改 实现动态预测，考虑到需预测时间区域中已获得的真实值数据
			//只有当需预测的时间区域仍有需预测月份时，才会进行预测。否则返回错误表示，该时间段的数据已经全部获得。
			while ((aMap.get(month)!=null)&&(!month.equals(find_next_month(Integer.toString(year*100+season*3))))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = {Double.toString(aMap.get(month))};
				writer.writeRecord(content);
				month = find_next_month(month);
			}		
			writer.close();
			//2.生成spring.csv
			//need_resmo 检验是否需要提供resmo
			writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
			for(int y = year-3;y<=year;y++){
				int pre = 0;
				for(int m = 1;m<=3;m++){
					if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//						System.out.println("year="+y);
						String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
						writer.writeRecord(content);
						pre = pre+SpringEffect.get(Integer.toString(y*100+m));
					}
				}
			}
			writer.close();
			//4.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
			String[] content2 = {Integer.toString(year),Integer.toString(season*3-2)};
//			System.out.println(month);
			
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return true;
		}
	}
	
	public boolean t_Y_Check_dataProvide(int year,String path)throws IOException {
		//0.先检查有无足够数据
		String month = Integer.toString((year-3)*100+1);
		while ((tMaps.get(1).get(month)!=null)&&(!month.equals(Integer.toString(year*100+12)))){
			month = find_next_month(month);
		}
		if(tMaps.get(1).get(month)==null){
			return false;
		}
		//1.生成all.csv
		CsvWriter writer = new CsvWriter(path+"town.csv",',',Charset.forName("GB18030"));
		String[] header = town_Namelist.toArray(new String[town_Namelist.size()]);
		writer.writeRecord(header);
		month = Integer.toString((year-3)*100+1);
		while ((tMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//			System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
			String[] content = new String[town_Namelist.size()];
			for(int i=0;i<town_Namelist.size();i++){
				content[i] = Double.toString(tMaps.get(i).get(month));
			}
			writer.writeRecord(content);
			month = find_next_month(month);
		}
		writer.close();
		
		//2.生成starttime.csv
		writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
		String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
		writer.writeRecord(content1);
//		String[] content2 = {Integer.toString(year),Integer.toString(1)};
//		System.out.println(month);
		String[] content2 = {Integer.toString(year),Integer.toString(1)};
//		System.out.println(month.substring(0,4));
//		System.out.println(month.substring(4,6));
		writer.writeRecord(content2);
		writer.close();
		return true;		
	}

	public boolean t_HY_Check_dataProvide(int year,String path,int uod)throws IOException{
		if(uod==0){
			//0.先检查有无足够数据
			String month = Integer.toString((year-3)*100+1);
			while ((tMaps.get(1).get(month)!=null)&&(!month.equals(Integer.toString(year*100+6)))){
				month = find_next_month(month);
			}
			if(tMaps.get(1).get(month)==null){
				return false;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"town.csv",',',Charset.forName("GB18030"));
			String[] header = town_Namelist.toArray(new String[town_Namelist.size()]);
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			while ((tMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+7)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = new String[town_Namelist.size()];
				for(int i=0;i<town_Namelist.size();i++){
					content[i] = Double.toString(tMaps.get(i).get(month));
				}
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			writer.close();
			
			//2.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
//			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month);
			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return true;
		}
		else {
			//0.先检查有无足够数据
			String month = Integer.toString((year-3)*100+1);
			while ((tMaps.get(1).get(month)!=null)&&(!month.equals(Integer.toString(year*100+12)))){
				month = find_next_month(month);
			}
			if(tMaps.get(1).get(month)==null){
				return false;
			}
			//1.生成all.csv
			CsvWriter writer = new CsvWriter(path+"town.csv",',',Charset.forName("GB18030"));
			String[] header = town_Namelist.toArray(new String[town_Namelist.size()]);
			writer.writeRecord(header);
			month = Integer.toString((year-3)*100+1);
			while ((tMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+101)))){
//				System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
				String[] content = new String[town_Namelist.size()];
				for(int i=0;i<town_Namelist.size();i++){
					content[i] = Double.toString(tMaps.get(i).get(month));
				}
				writer.writeRecord(content);
				month = find_next_month(month);
			}
			writer.close();
			
			//2.生成starttime.csv
			writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
			String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
			writer.writeRecord(content1);
//			String[] content2 = {Integer.toString(year),Integer.toString(1)};
//			System.out.println(month);
			String[] content2 = {Integer.toString(year),Integer.toString(7)};
//			System.out.println(month.substring(0,4));
//			System.out.println(month.substring(4,6));
			writer.writeRecord(content2);
			writer.close();
			return true;
		}
	}

	public boolean varPred_dataProvide(int year,String path)throws IOException {
		String month = Integer.toString((year-3)*100+1);
		while ((dfMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString((year-1)*100+12)))){
			month = find_next_month(month);
		}
		if(dfMaps.get(0).get(month)==null){
			System.out.println(month);
			return false;
		}
		//1.生成var.csv
		CsvWriter writer = new CsvWriter(path+"var.csv",',',Charset.forName("GB18030"));
		String[] header = drive_factor_Namelist.toArray(new String[drive_factor_Namelist.size()]);
		writer.writeRecord(header);
		month = Integer.toString((year-3)*100+1);
		while ((dfMaps.get(0).get(month)!=null)&&(!month.equals(Integer.toString(year*100+1)))){
//			System.out.println("month="+month+"   final="+Integer.toString(year*100+1));
			String[] content = new String[drive_factor_Namelist.size()];
			for(int i=0;i<drive_factor_Namelist.size();i++){
				content[i] = Double.toString(dfMaps.get(i).get(month));
			}
			writer.writeRecord(content);
			month = find_next_month(month);
		}
		writer.close();
		//2.生成spring.csv
		//need_resmo 检验是否需要提供resmo
		writer = new CsvWriter(path+"spring.csv",',',Charset.forName("GB18030"));
		for(int y = year-3;y<year;y++){
			int pre = 0;
			for(int m = 1;m<=3;m++){
				if(SpringEffect.get(Integer.toString(y*100+m))!=null){
//					System.out.println("year="+y);
					String[] content = {Integer.toString(12*(y-year+3)+m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(y*100+m))))};
					writer.writeRecord(content);
					pre = pre+SpringEffect.get(Integer.toString(y*100+m));
				}
			}
		}
		writer.close();
		
		//3.生成resmo.csv
		writer = new CsvWriter(path+"resmo.csv",',',Charset.forName("GB18030"));
		int pre = 0;
		for(int m = 1;m<=3;m++){
			if(SpringEffect.get(Integer.toString(year*100+m))!=null){
				String[] content = {Integer.toString(m),Integer.toString(pre+1),Integer.toString((int) (pre+SpringEffect.get(Integer.toString(year*100+m))))};
				writer.writeRecord(content);
				pre = pre+SpringEffect.get(Integer.toString(year*100+m));
			}
		}
		writer.close();
		
		//4.生成starttime.csv
		writer = new CsvWriter(path+"starttime.csv",',',Charset.forName("GB18030"));
		String[] content1 = {Integer.toString(year-3),Integer.toString(1)};
		writer.writeRecord(content1);
		String[] content2 = {Integer.toString(year),Integer.toString(1)};
//		System.out.println(month);
		
//		System.out.println(month.substring(0,4));
//		System.out.println(month.substring(4,6));
		writer.writeRecord(content2);
		writer.close();
		return true;
	}

}
