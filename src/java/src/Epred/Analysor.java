package Epred;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import com.csvreader.CsvReader;
import DataManager.dataStruct;


public class Analysor {

	/*
	 * 利用Predictor和PrecisionChecker生成的csv数据，自动生成简单的预测结果报告Word文档
	 * 函数按照生成的报表分类，每个报表都要完成两个任务：
	 * 		1.从文件读取信息
	 * 		2.加入到分析文档模板当中
	 */
	String pdataAdd;
	String modelAdd;
	String rptAdd;
	String odataAdd;
	public Analysor(String odAdd,String pdAdd,String mAdd,String rAdd) throws Exception{
		/*
		 * odataAdd：加载原始数据csv的位置
		 * pdataAdd：加载R结果csv文档的位置
		 * modelAdd：存放分析报告模板的位置
		 * rptAdd：存放分析报告结果的位置
		 */
		// TODO Auto-generated constructor stub
		odataAdd = odAdd;
		pdataAdd = pdAdd;
		modelAdd = mAdd;
		rptAdd = rAdd;
		
	} 
	
	/*
	 * 以下是生成各种分析报告的方法。需要配套Predictor与PrecisionChecker的结果使用。不建议单独使用这些方法。
	 */
	public void Y_allPred_rpt(int trainY,int predY,dataStruct datastructure) throws Exception{
		/*
		 * 7-21修改：
		 * 1、根据datastructure里面的数据,先输入已获得月份
		 */
//		System.out.println(pdataAdd+"Pred/all/allpred.csv");
		CsvReader r = new CsvReader(pdataAdd+"Pred/all/allpred.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		param.put("predRange", Integer.toString(predY));
		double year=0;
		double month;
		//检查是否java的“数据库”中已有该月的数据，若没有，通过读取输出
		int got_months = 0;
		for(int i=1;i<=12;i++){
			if(datastructure.aMap.get(Integer.toString(predY*100+i))!=null){
				month = datastructure.aMap.get(Integer.toString(predY*100+i));
				year += month;
				param.put("mon"+Integer.toString(i)+"e", String.format("%.2f", month)+"（真实值）");
				got_months=i;
			}
			else{
				r.readRecord();
				String x = r.get("x");
//				System.out.println(x);
				month = Double.parseDouble(x);
				year += month;
				param.put("mon"+Integer.toString(i)+"e", String.format("%.2f", month)+"（预测值）");
			}		
		}
		param.put("year", String.format("%.2f", year));
		if(got_months==0){
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY-1)+"年");
		}
		else{
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(got_months)+"月");
		}
		r.close();
		/*
		 * insert png
		 */
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 400);
		image.put("height", 400);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"Pred/all/allPred.png"), true));
		param.put("image", image);
		
//		System.out.println(modelAdd+"Pred/全社会用电量年度预测结果报告模板.docx");
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Pred/全社会用电量年度预测结果报告模板.docx");
//		System.out.println(rptAdd+"Pred/全社会用电量年度预测结果报告.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Pred/"+Integer.toString(predY)+"年全社会用电量年度预测结果报告.docx");
		doc.write(fopts);
		fopts.close();
	}

	public void HY_allPred_rpt(int trainY,int predY,dataStruct datastructure,int uod) throws Exception{
		/*
		 * uod 上半年还是下半年 0上1下
		 */
//		System.out.println(pdataAdd+"Pred/all/allpred.csv");
		CsvReader r = new CsvReader(pdataAdd+"Pred/all/allpred.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
//		if(uod==0){
//			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
//		}
//		else {
//			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY)+"上半年");
//		}
		param.put("predRange", Integer.toString(predY));
		int uodmove = 0;
		if(uod==0){
			param.put("UOD", "上");
			uodmove=0;
		}
		else {
			param.put("UOD", "下");
			uodmove=6;
		}
		
		double hyear=0;
		double month;
		
		int got_months = 0;
		for(int i=1;i<=6;i++){
			if(datastructure.aMap.get(Integer.toString(predY*100+i+uodmove))!=null){
				month = datastructure.aMap.get(Integer.toString(predY*100+i+uodmove));
				hyear += month;
				param.put("mon"+Integer.toString(i), String.format("%.2f", month)+"（真实值）");
				param.put("mname"+Integer.toString(i), Integer.toString(i+uodmove));
				got_months=i;
			}
			else{
				r.readRecord();
				String x = r.get("x");
//				System.out.println(x);
				month = Double.parseDouble(x);
				hyear += month;
				param.put("mon"+Integer.toString(i), String.format("%.2f", month)+"（预测值）");
				param.put("mname"+Integer.toString(i), Integer.toString(i+uodmove));
			}		
		}
		
//		for(int i=1;i<=6;i++){
//			r.readRecord();
//			String x = r.get("x");
////			System.out.println(x);
//			month = Double.parseDouble(x);
//			hyear += month;
//			param.put("mon"+Integer.toString(i), String.format("%.2f", month));
//			param.put("mname"+Integer.toString(i), Integer.toString(i+uodmove));
//			
//		}
		param.put("Hyear", String.format("%.2f", hyear));
		r.close();
		/*
		 * insert png
		 */
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 400);
		image.put("height", 400);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"Pred/all/allPred.png"), true));
		param.put("image", image);
		
		if(got_months==0){
			if(uod==0){
				param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY-1)+"年");
			}
			else{
				param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(6)+"月");
			}
			
		}
		else{
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(got_months+uod*6)+"月");
		}
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Pred/全社会用电量半年度预测结果报告模板.docx");
		
		String filename;
		if(uod==0){
			filename = rptAdd+"Pred/"+Integer.toString(predY)+"上半年全社会用电量半年度预测结果报告.docx";
		}
		else{
			filename = rptAdd+"Pred/"+Integer.toString(predY)+"下半年全社会用电量半年度预测结果报告.docx";
		}
		FileOutputStream fopts = new FileOutputStream(filename);

		doc.write(fopts);
		fopts.close();
	}

	public void S_allPred_rpt(int trainY,int predY,dataStruct datastructure,int season) throws Exception{
		/*
		 * season 1~4季度
		 */
//		System.out.println(pdataAdd+"Pred/all/allpred.csv");
		CsvReader r = new CsvReader(pdataAdd+"Pred/all/allpred.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
//		if(season==1){
//			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
//		}
//		else{
//			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY)+"第"+Integer.toString(season-1)+"季度");
//		}
		param.put("season", Integer.toString(season));
		param.put("predRange", Integer.toString(predY));
		int seamove = 3*(season-1);
		
		int got_months = 0;
		double seause=0;
		double month;
		for(int i=1;i<=3;i++){
			if(datastructure.aMap.get(Integer.toString(predY*100+i+seamove))!=null){
				month = datastructure.aMap.get(Integer.toString(predY*100+i+seamove));
				seause += month;
				param.put("mon"+Integer.toString(i), String.format("%.2f", month)+"（真实值）");
				param.put("mname"+Integer.toString(i), Integer.toString(i+seamove));
				got_months=i;
			}
			else{
				r.readRecord();
				String x = r.get("x");
//				System.out.println(x);
				month = Double.parseDouble(x);
				seause += month;
				param.put("mon"+Integer.toString(i), String.format("%.2f", month)+"（预测值）");
				param.put("mname"+Integer.toString(i), Integer.toString(i+seamove));
			}	
		}
		param.put("Spred", String.format("%.2f", seause));

		if(got_months==0){
			if(season==1){
				param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY-1)+"年");
			}
			else{
				param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(3*(season-1))+"月");
			}
			
		}
		else{
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(got_months+(season-1)*3)+"月");
		}
		
		r.close();
		/*
		 * insert png
		 */
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 400);
		image.put("height", 400);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"Pred/all/allPred.png"), true));
		param.put("image", image);
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Pred/全社会用电量季度预测结果报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Pred/"+Integer.toString(predY)+
				"年第"+Integer.toString(season)+"季度全社会用电量季度预测结果报告.docx");
		doc.write(fopts);
		fopts.close();
	}

	public void Y_townPred_rpt(int trainY,int predY,dataStruct datastructure)throws Exception{
		CsvReader r = new CsvReader(pdataAdd+"Pred/town/分镇街预测结果.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		param.put("predRange", Integer.toString(predY));
		int i = 0;
		int m=0;
		String townname;
		double yearuse;
		
		while(r.readRecord()){
			i++;
			townname = r.get(0);
			yearuse = Double.parseDouble(r.get(1));
			m = 1;
			while(datastructure.tMaps.get(i-1).get(Integer.toString(predY*100+m))!=null){
				yearuse += datastructure.tMaps.get(i-1).get(Integer.toString(predY*100+m));
				m++;
			}
			param.put("t"+Integer.toString(i)+"e", String.format("%.2f", yearuse));
			param.put("town"+Integer.toString(i)+"e", townname);
		}
		if(i<36){//预留36个空位未填满
			for(int j=i+1;j<=36;j++){
				param.put("t"+Integer.toString(j)+"e","");
				param.put("town"+Integer.toString(j)+"e", "");
			}
		}
		int got_months = m-1;
		if(got_months==0){
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY-1)+"年");
		}
		else{
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(got_months)+"月");
		}
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Pred/分镇街年度预测结果报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Pred/"+Integer.toString(predY)+"年分镇街年度预测结果报告.docx");
		doc.write(fopts);
		fopts.close();
	}

	public void HY_townPred_rpt(int trainY,int predY,dataStruct datastructure,int uod)throws Exception{
		CsvReader r = new CsvReader(pdataAdd+"Pred/town/分镇街预测结果.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		param.put("predRange", Integer.toString(predY));
		int i = 0;
		int m=0;
		String townname;
		double hyearuse;
		
		while(r.readRecord()){
			i++;
			townname = r.get(0);
			hyearuse = Double.parseDouble(r.get(1));
			m = 1+uod*6;
			while(datastructure.tMaps.get(i-1).get(Integer.toString(predY*100+m))!=null){
				hyearuse += datastructure.tMaps.get(i-1).get(Integer.toString(predY*100+m));
				m++;
			}
			param.put("t"+Integer.toString(i)+"e", String.format("%.2f", hyearuse));
			param.put("town"+Integer.toString(i)+"e", townname);
		}
		if(i<36){//预留36个空位未填满
			for(int j=i+1;j<=36;j++){
				param.put("t"+Integer.toString(j)+"e","");
				param.put("town"+Integer.toString(j)+"e", "");
			}
		}
		int got_months = m-1;
		if(got_months==0){
			if(uod==0){
				param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY-1)+"年");
			}
			else{
				param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(6)+"月");
			}
			
		}
		else{
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(got_months)+"月");
		}
		if(uod==0){
			param.put("UOD", "上");
		}
		else {
			param.put("UOD", "下");
		}
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Pred/分镇街半年度预测结果报告模板.docx");
		
		String filename;
		if(uod==0){
			filename = rptAdd+"Pred/"+Integer.toString(predY)+"上半年分镇街半年度预测结果报告.docx";
		}
		else {
			filename = rptAdd+"Pred/"+Integer.toString(predY)+"下半年分镇街半年度预测结果报告.docx";

		}
		FileOutputStream fopts = new FileOutputStream(filename);
		doc.write(fopts);
		fopts.close();
		
		
		
		
		
//		//########################
//		CsvReader r = new CsvReader(pdataAdd+"Pred/town/分镇街预测结果.csv", ',', Charset.forName("GB18030"));
//		r.readHeaders();
//		Map<String, Object> param = new HashMap<String, Object>();
//		if(uod==0){
//			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
//		}
//		else {
//			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY)+"上半年");
//		}
//		param.put("predRange", Integer.toString(predY));
//		if(uod==0){
//			param.put("UOD", "上");
//		}
//		else {
//			param.put("UOD", "下");
//		}
//		int i = 0;
//		String townname;
//		double yearuse;
//		while(r.readRecord()){
//			i++;
//			townname = r.get(0);
//			yearuse = Double.parseDouble(r.get(1));
//			param.put("t"+Integer.toString(i)+"e", String.format("%.2f", yearuse));
//			param.put("town"+Integer.toString(i)+"e", townname);
//		}
//		if(i<36){
//			for(int j=i+1;j<=36;j++){
//				param.put("t"+Integer.toString(j)+"e","");
//				param.put("town"+Integer.toString(j)+"e", "");
//			}
//		}
//		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Pred/分镇街半年度预测结果报告模板.docx");
//		FileOutputStream fopts = new FileOutputStream(rptAdd+"Pred/分镇街半年度预测结果报告.docx");
//		doc.write(fopts);
//		fopts.close();
	}

	public void Y_allCheck_rpt(int trainY,int predY) throws Exception {
		CsvReader r = new CsvReader(pdataAdd+"PCheck/all/allcheck.csv", ',', Charset.forName("GB18030"));
		CsvReader rmon = new CsvReader(pdataAdd+"PCheck/all/monthcheck.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		rmon.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		param.put("predRange", Integer.toString(predY));
		r.readRecord();
		double mbias = Double.parseDouble(r.get(0))*100;
		double sumerr = Double.parseDouble(r.get(1))*100;
		param.put("yearerr", String.format("%.2f", sumerr)+"%");
		param.put("yearcor", String.format("%.2f", 100-sumerr)+"%");
		r.close();
		double sumreal=0;
		double sumpred=0;
		double prede,reale,erre;
		int i=1;
		while(rmon.readRecord()){
			prede=Double.parseDouble(rmon.get(1));
			reale=Double.parseDouble(rmon.get(2));
			sumreal+=reale;
			sumpred+=prede;
			erre =Double.parseDouble(rmon.get(3));
			String symbol;
			if(prede>=reale){
				symbol = "+";
			}
			else {
				symbol = "-";
			}
			param.put("pred"+Integer.toString(i)+"e", String.format("%.2f", prede));
			param.put("real"+Integer.toString(i)+"e", String.format("%.2f", reale));
			param.put("err"+Integer.toString(i)+"e", symbol+String.format("%.2f", erre*100)+"%");		
			i++;
		}
		param.put("realE", String.format("%.2f", sumreal));
		param.put("predE", String.format("%.2f", sumpred));
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 300);
		image.put("height", 300);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"PCheck/all/monthbias.png"), true));
		param.put("image", image);
		rmon.close();
		
		if(sumerr<=2){
			param.put("Comment", "年预测误差在2%以内，预测结果理想，模型十分准确");
		}
		else if (sumerr<=3) {
			param.put("Comment", "结果较为理想，模型仍然有效");
		}
		else {
			param.put("Comment", "模型结果较不理想，可能相比建模时，东莞当地的经济形势已经发生较大变化。建议重新调整参数");
		}
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Check/全社会预测模型往年预测效果检验报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Check/"+Integer.toString(predY)+"全社会预测模型往年预测效果检验报告.docx");
		doc.write(fopts);
		fopts.close();
	}

	public void HY_allCheck_rpt(int trainY,int predY,int uod)throws Exception{
		CsvReader r = new CsvReader(pdataAdd+"PCheck/all/allcheck.csv", ',', Charset.forName("GB18030"));
		CsvReader rmon = new CsvReader(pdataAdd+"PCheck/all/monthcheck.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		rmon.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
		if(uod==0){
			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1)+"年");
			param.put("uod", "上");
		}
		else{
			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY)+"上半年");
			param.put("uod", "下");
		}
		param.put("predRange", Integer.toString(predY));
		
		r.readRecord();
		double mbias = Double.parseDouble(r.get(0))*100;
		double sumerr = Double.parseDouble(r.get(1))*100;
		param.put("yearerr", String.format("%.2f", sumerr)+"%");
		param.put("yearcor", String.format("%.2f", 100-sumerr)+"%");
		r.close();
		double sumreal=0;
		double sumpred=0;
		double prede,reale,erre;
		int i=1;
		while(rmon.readRecord()){
			prede=Double.parseDouble(rmon.get(1));
			reale=Double.parseDouble(rmon.get(2));
			sumreal+=reale;
			sumpred+=prede;
			erre =Double.parseDouble(rmon.get(3));
			String symbol;
			if(prede>reale){
				symbol = "+";
			}
			else {
				symbol = "-";
			}
			param.put("pred"+Integer.toString(i)+"e", String.format("%.2f", prede));
			param.put("real"+Integer.toString(i)+"e", String.format("%.2f", reale));
			param.put("err"+Integer.toString(i)+"e", symbol+String.format("%.2f", erre*100)+"%");
			param.put("mon"+Integer.toString(i)+"e", Integer.toString(i+uod*6));
			i++;
		}
		param.put("realE", String.format("%.2f", sumreal));
		param.put("predE", String.format("%.2f", sumpred));
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 300);
		image.put("height", 300);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"PCheck/all/monthbias.png"), true));
		param.put("image", image);
		rmon.close();
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Check/全社会预测模型半年预测效果检验报告模板.docx");
		
		String filename;
		if(uod==0){
			filename = rptAdd+"Check/"+Integer.toString(predY)+"上半年全社会预测模型半年预测效果检验报告.docx";
		}
		else{
			filename = rptAdd+"Check/"+Integer.toString(predY)+"下半年全社会预测模型半年预测效果检验报告.docx";
		}
		FileOutputStream fopts = new FileOutputStream(filename);
		doc.write(fopts);
		fopts.close();
	}

	public void S_allCheck_rpt(int trainY,int predY,int season)throws Exception {
		CsvReader r = new CsvReader(pdataAdd+"PCheck/all/allcheck.csv", ',', Charset.forName("GB18030"));
		CsvReader rmon = new CsvReader(pdataAdd+"PCheck/all/monthcheck.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		rmon.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("season", Integer.toString(season));
		param.put("predRange", Integer.toString(predY));
		
		r.readRecord();
		double mbias = Double.parseDouble(r.get(0))*100;
		double sumerr = Double.parseDouble(r.get(1))*100;
		param.put("yearerr", String.format("%.2f", sumerr)+"%");
		param.put("yearcor", String.format("%.2f", 100-sumerr)+"%");
		r.close();
		double sumreal=0;
		double sumpred=0;
		double prede,reale,erre;
		int i=1;
		while(rmon.readRecord()){
			prede=Double.parseDouble(rmon.get(1));
			reale=Double.parseDouble(rmon.get(2));
			sumreal+=reale;
			sumpred+=prede;
			erre =Double.parseDouble(rmon.get(3));
			String symbol;
			if(prede>reale){
				symbol = "+";
			}
			else {
				symbol = "-";
			}
			param.put("pred"+Integer.toString(i)+"e", String.format("%.2f", prede));
			param.put("real"+Integer.toString(i)+"e", String.format("%.2f", reale));
			param.put("err"+Integer.toString(i)+"e", symbol+String.format("%.2f", erre*100)+"%");
			param.put("mon"+Integer.toString(i)+"e", Integer.toString(i+(season-1)*3));
			i++;
		}
		if(season==1){
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY-1)+"年");
		}
		else{
			param.put("histRange", Integer.toString(trainY)+"年-"+Integer.toString(predY)+"年"+Integer.toString(3*(season-1))+"月");
		}
			
		param.put("realE", String.format("%.2f", sumreal));
		param.put("predE", String.format("%.2f", sumpred));
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 300);
		image.put("height", 300);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"PCheck/all/monthbias.png"), true));
		param.put("image", image);
		rmon.close();
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Check/全社会预测模型季度预测效果检验报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Check/"+Integer.toString(predY)+
				"年第"+Integer.toString(season)+"季度全社会预测模型季度预测效果检验报告.docx");
		doc.write(fopts);
		fopts.close();

	}
	
	public void Y_townCheck_rpt(int trainY,int predY) throws Exception {
		CsvReader r = new CsvReader(pdataAdd+"PCheck/town/towncheck.csv", ',', Charset.forName("GB18030"));
		CsvReader ratio = new CsvReader(pdataAdd+"PCheck/town/towncheckratio.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		ratio.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		param.put("predRange", Integer.toString(predY));
		
		int i=1;
		while(r.readRecord()){
			param.put("town"+Integer.toString(i)+"e", r.get(0));
			param.put("rat"+Integer.toString(i)+"e", String.format("%.2f", Double.parseDouble(r.get(2))*100)+"%");
			param.put("err"+Integer.toString(i)+"e", String.format("%.2f", Double.parseDouble(r.get(1))*100)+"%");
			i++;
		}
		r.close();
		if(i<=36){
			for(int j=i;j<=36;j++){
				param.put("town"+Integer.toString(j)+"e", "");
				param.put("rat"+Integer.toString(j)+"e", "");
				param.put("err"+Integer.toString(j)+"e", "");
			}
		}
		
		ratio.readRecord();
		param.put("under2", ratio.get(1));
		param.put("between25", ratio.get(2));
		param.put("over5", ratio.get(3));
		int townnum = (int)(Double.parseDouble(ratio.get(1))+Double.parseDouble(ratio.get(2))+Double.parseDouble(ratio.get(3)));
		param.put("townnum", Integer.toString(townnum));
		
		ratio.readRecord();
		param.put("ru2", String.format("%.2f", Double.parseDouble(ratio.get(1))*100));
		param.put("rb25", String.format("%.2f", Double.parseDouble(ratio.get(2))*100));
		param.put("ro5", String.format("%.2f", Double.parseDouble(ratio.get(3))*100));
		
		ratio.readRecord();
		param.put("pu2", String.format("%.2f", Double.parseDouble(ratio.get(1))*100));
		param.put("pb25", String.format("%.2f", Double.parseDouble(ratio.get(2))*100));
		param.put("po5", String.format("%.2f", Double.parseDouble(ratio.get(3))*100));
		
		ratio.close();
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Check/分镇街预测模型往年预测效果检验报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Check/"+Integer.toString(predY)+"年分镇街预测模型往年预测效果检验报告.docx");
		doc.write(fopts);
		fopts.close();
	}

	public void HY_townCheck_rpt(int trainY,int predY,int uod)throws Exception {
		CsvReader r = new CsvReader(pdataAdd+"PCheck/town/towncheck.csv", ',', Charset.forName("GB18030"));
		CsvReader ratio = new CsvReader(pdataAdd+"PCheck/town/towncheckratio.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		ratio.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		if(uod==0){
			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1)+"年");
			param.put("uod", "上");
		}
		else {
			param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY)+"上半年");
			param.put("uod", "下");
		}
		param.put("predRange", Integer.toString(predY));
		
		int i=1;
		while(r.readRecord()){
			param.put("town"+Integer.toString(i)+"e", r.get(0));
			param.put("rat"+Integer.toString(i)+"e", String.format("%.2f", Double.parseDouble(r.get(2))*100)+"%");
			param.put("err"+Integer.toString(i)+"e", String.format("%.2f", Double.parseDouble(r.get(1))*100)+"%");
			i++;
		}
		r.close();
		if(i>36){
			System.out.println("系统设定最多可预测36个镇街，现已超出该范围");
		}
		if(i<=36){
			for(int j=i;j<=36;j++){
				param.put("town"+Integer.toString(j)+"e", "");
				param.put("rat"+Integer.toString(j)+"e", "");
				param.put("err"+Integer.toString(j)+"e", "");
			}
		}
		
		ratio.readRecord();
		param.put("under2", ratio.get(1));
		param.put("between25", ratio.get(2));
		param.put("over5", ratio.get(3));
		int townnum = (int)(Double.parseDouble(ratio.get(1))+Double.parseDouble(ratio.get(2))+Double.parseDouble(ratio.get(3)));
		param.put("townnum", Integer.toString(townnum));
		
		ratio.readRecord();
		param.put("ru2", String.format("%.2f", Double.parseDouble(ratio.get(1))*100));
		param.put("rb25", String.format("%.2f", Double.parseDouble(ratio.get(2))*100));
		param.put("ro5", String.format("%.2f", Double.parseDouble(ratio.get(3))*100));
		
		ratio.readRecord();
		param.put("pu2", String.format("%.2f", Double.parseDouble(ratio.get(1))*100));
		param.put("pb25", String.format("%.2f", Double.parseDouble(ratio.get(2))*100));
		param.put("po5", String.format("%.2f", Double.parseDouble(ratio.get(3))*100));
		
		ratio.close();
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Check/分镇街预测模型半年度预测效果检验报告模板.docx");
		String filename;
		if(uod==0){
			filename = rptAdd+"Check/"+Integer.toString(predY)+"上半年分镇街预测模型半年度预测效果检验报告.docx";
		}
		else{
			filename = rptAdd+"Check/"+Integer.toString(predY)+"下半年分镇街预测模型半年度预测效果检验报告.docx";

		}
		FileOutputStream fopts = new FileOutputStream(filename);
		doc.write(fopts);
		fopts.close();
	}
	
	public void varCheck_rpt(int trainY,int predY)throws Exception {
		CsvReader r = new CsvReader(pdataAdd+"PCheck/var/var模型与Holt-Winter模型预测结果比较.csv", ',', Charset.forName("GB18030"));
		r.readHeaders();
		Map<String, Object> param = new HashMap<String, Object>();
		r.readRecord();
		
		param.put("histRange", Integer.toString(trainY)+'-'+Integer.toString(predY-1));
		param.put("predRange", Integer.toString(predY));
		double bias = Double.parseDouble(r.get(1))*100;
		param.put("Bias", String.format("%.2f", bias));
		
		Map<String, Object> image = new HashMap<String,Object>();
		image.put("width", 400);
		image.put("height", 400);
		image.put("type", "png");
		image.put("content", WordUtil.inputStream2ByteArray(new FileInputStream(pdataAdd+"PCheck/var/var模型与Holt-Winter模型预测结果比较.png"), true));
		param.put("image", image);
		
		if(bias<=4){
			param.put("Comment", "两种预测方法结果相差在4%以内，认为主模型的预测结果准确，副模型的分析结果正确。");
		}
		else{
			param.put("Comment", "两种预测方法结果相差超过4%，可能原因在于副模型需要先对宏观经济数据做出预测，导致误差叠加。预测结果以主模型为准。");
		}
		r.close();
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Check/副模型预测效果检验报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Check/"+Integer.toString(predY)+"年副模型预测效果检验报告报告.docx");
		doc.write(fopts);
		fopts.close();
		
	}

	public void varAna()throws Exception {
		CsvReader rcoef = new CsvReader(pdataAdd+"VarAna/varcoef.csv", ',', Charset.forName("GB18030"));
		CsvReader rinf = new CsvReader(pdataAdd+"VarAna/ftinflu.csv", ',', Charset.forName("GB18030"));
		rcoef.readHeaders();
		rinf.readHeaders();
		
		Map<String, Object> param = new HashMap<String, Object>();
		/*
		 * 读取方程系数
		 */
		rcoef.readRecord();
		//1
		rcoef.readRecord();
		param.put("Cy1", String.format("%.2f", Double.parseDouble(rcoef.get(1))));
		param.put("Cx11", String.format("%.2f", Double.parseDouble(rcoef.get(2))));
		param.put("Cx21", String.format("%.2f", Double.parseDouble(rcoef.get(3))));
		param.put("Cx31", String.format("%.2f", Double.parseDouble(rcoef.get(4))));
		param.put("Cx41", String.format("%.2f", Double.parseDouble(rcoef.get(5))));
		
		//2
		rcoef.readRecord();
		param.put("Cy2", String.format("%.2f", Double.parseDouble(rcoef.get(1))));
		param.put("Cx12", String.format("%.2f", Double.parseDouble(rcoef.get(2))));
		param.put("Cx22", String.format("%.2f", Double.parseDouble(rcoef.get(3))));
		param.put("Cx32", String.format("%.2f", Double.parseDouble(rcoef.get(4))));
		param.put("Cx42", String.format("%.2f", Double.parseDouble(rcoef.get(5))));
		
		//3
		rcoef.readRecord();
		param.put("Cy3", String.format("%.2f", Double.parseDouble(rcoef.get(1))));
		param.put("Cx13", String.format("%.2f", Double.parseDouble(rcoef.get(2))));
		param.put("Cx23", String.format("%.2f", Double.parseDouble(rcoef.get(3))));
		param.put("Cx33", String.format("%.2f", Double.parseDouble(rcoef.get(4))));
		param.put("Cx43", String.format("%.2f", Double.parseDouble(rcoef.get(5))));

		rcoef.close();
		
		for(int i=1;i<=4;i++){
			rinf.readRecord();
			param.put("influ"+Integer.toString(i), String.format("%.2f",Double.parseDouble(rinf.get(1))*100));
		}
		rinf.close();
		
		CustomXWPFDocument doc = WordUtil.generateWord(param, modelAdd+"Analysis/宏观驱动因素分析报告模板.docx");
		FileOutputStream fopts = new FileOutputStream(rptAdd+"Analysis/宏观驱动因素分析报告.docx");
		doc.write(fopts);
		fopts.close();
	}
}
