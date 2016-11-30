package Epred;
/*
 * EpredMain类是整个预测系统的中心。
 * 类实例的构造完成以下工作：
 * 1.确定输入输出文件的目录
 * 2.构造系统各功能模块的实例。
 * 
 * 各方法的用途和用法如下。每个功能的具体介绍请参考相关的设计文档。
 * 作为参考，在a_Y_pred方法中会有足够的注释，表明该方法实现的几个功能的部分。
 * public void a_Y_pred(int year) 
 * 全社会用电量的年度预测。
 * 参数year为需要预测的年度
 * 
 * public void a_HY_pred(int year,int uod)
 * 全社会用电量的半年度预测。
 * 参数year为需要预测的年度
 * 参数uod为预测的是上半年还是下半年（up or down），若要预测上半年，输入0。若要预测下半年，输入1。
 * 
 * public void a_S_pred(int year,int season)
 * 全社会用电量的季度预测。
 * 参数year为需要预测的年度
 * 参数season为需要预测的季度，输入1、2、3或4
 * 
 * public void t_Y_pred(int year)
 * 分镇街用电量的年度预测。参数设置与上述方法类似
 * 
 * public void t_HY_pred(int year,int uod)
 * 分镇街用电量的半年度预测。参数设置与上述方法类似
 * 
 * public void a_Y_Check(int year)
 * 全社会用电量的年度预测结果检测。
 * 参数year为需要检测预测结果好坏的年份。
 * 
 * public void a_HY_Check(int year,int uod)
 * 全社会用电量的半年度预测结果检测。参数设置和上述方法类似
 * 
 * public void a_S_Check(int year,int season)
 * 全社会用电量的季度预测结果检测。参数设置和上述方法类似
 * 
 * public void t_Y_Check(int year)
 * 分镇街用电量的年度预测结果检测
 * 
 * public void t_HY_Check(int year,int uod)
 * 分镇街用电量的半年度预测结果检测
 * 
 * public void var_pred(int year)
 * 副模型分析报告生成。
 * 
 * public void var_Check(int year)
 * 副模型有效性检验
 * 
 * public static boolean Ins_Rpackage(String rfile)
 * 安装所需的R包，返回是否成功。
 */

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import com.sun.java_cup.internal.runtime.virtual_parse_stack;
import com.sun.org.apache.bcel.internal.generic.NEW;

import DataManager.*;
import DataManager.resultStruct.townPred;

import java.io.File;
import java.io.IOException;

public class EPredMain {
/*
 * 以下为各功能模块的实例，每个类原则上只需要一个实例：
 * 
 * 1.dataStruct类
 * 		数据的读入与管理。
 * 		无需任何构造参数
 * 
 * 2.Predictor类
 * 		调用相应R代码。生成预测的数据结果。
 * 		构造：predictor = new Predictor(Data4R_Add, Rfile_Add,Result_Add);
 * 		参数分别为R代码所需数据的地址，R代码的存放地址，以及csv结果的存放地址
 * 
 * 3.PrecisionChecker类
 * 		调用相应R代码。检测预测的准确性，生成csv文件类型的数据
 * 		构造：checker = new PrecisionChecker(Data4R_Add,Rfile_Add,Result_Add);
 * 		参数同上
 * 
 * 4.Analysor类
 * 		根据Predictor与PrecisionChecker类生成的结果，生成分析报告文档。
 * 		构造：analysor = new Analysor(Data4R_Add,Result_Add, Model_Add, Report_Add);
 * 		参数分别为R代码所需数据的地址，csv结果的存放地址，分析报告模板存放地址以及分析报告存放地址
 * 
 * 5.Creditor类（好像英文拼错了）
 * 		用电信用评估。
 * 		构造：creditor = new Creditor(Origin_data_Add, Result_Add,Rfile_Add);
 * 		参数分别为，数据输入地址，结果存放地址，R代码存放地址
 * 
 * 6.Debugger类
 * 		异常处理。
 * 		无需构造，修改或重载Debugger类中的方法，即可实现异常处理。
 * 
 * 7.fileStruct类
 * 		文件结构管理
 * 		暂时只有清空某文件夹下所有文件的功能。
 */
	
	Predictor predictor;
	PrecisionChecker checker;
	Analysor analysor;
	Creditor creditor;
	dataStruct datastructure;
	Debugger debugger = new Debugger();
	fileStruct filestructure = new fileStruct();
	resultStruct resultstructure;
	
/*
 * 以下为与程序完成交互的地址：
 * Data4R_Add R程序用到的经过处理的数据的存储目录，为项目的data4r文件夹
 * Rfile_Add R程序的存储目录，为项目的Rprogram文件夹
 * Model_Add 分析报告模板的文件夹目录，为项目的Model文件夹
 * Result_Add R程序生成结果的存储目录。
 * Report_Add 生成分析报告的存储目录。
 */
	String Origin_data_Add;
//	String Data4R_Add = System.getProperty("user.dir").replace("\\", "/")+"/data4r/";
//	String Rfile_Add= System.getProperty("user.dir").replace("\\", "/")+"/Rprogram/";
//	String Model_Add = System.getProperty("user.dir").replace("\\", "/")+"/Model/";
	String Data4R_Add;
	String Rfile_Add;
	String Model_Add;
	String Result_Add;
	String Report_Add;
	
	public EPredMain(String result,String report,String origindata,String data4r,String rfile,String model) {
		Origin_data_Add = origindata;
		Data4R_Add = data4r;//修改将R数据的读取外放到程序之外。
		Rfile_Add = rfile;
		Model_Add = model;
//		System.out.println(Data4R_Add);
//		System.out.println(Rfile_Add);
		Result_Add = result;
		Report_Add = report;
		
		// TODO Auto-generated method stub
		//启动提示
		System.out.println("Electric Prediction System is now working.");
		String rserveadd = Rfile_Add+"lib.r";
		//启动Rserve后台
		/*
		Runtime run = Runtime.getRuntime();
		// XXX delete restart rserver
		try{
//			String cmds ="Rscript \""+rserveadd+"\"";
			String cmds ="Rscript -e \" library(Rserve);Rserve()\"";
//			System.out.println("cmds="+cmds);
			run.exec("taskkill /f /t /im Rserve.exe");
			Process process = run.exec(cmds);
//			run.exec(cmds);
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			debugger.Rserve_err();
		}*/
		
		//各模块实例构造
		
		try {
			datastructure = new dataStruct();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			predictor = new Predictor(Data4R_Add, Rfile_Add,Result_Add);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("predictor builded");
		
		try {
			checker = new PrecisionChecker(Data4R_Add,Rfile_Add, Result_Add);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("checker builded");
		
		try {
			creditor = new Creditor(Origin_data_Add, Result_Add,Rfile_Add);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			analysor = new Analysor(Data4R_Add,Result_Add, Model_Add, Report_Add);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resultstructure = new resultStruct();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public int Allread() throws IOException{//若没有产生异常，返回0;
		return datastructure.A_reader(Origin_data_Add+"总用电量.xlsx");//最后需要统一，只读一次？
	}
	
	public int Townread() throws IOException{
		return datastructure.T_reader(Origin_data_Add+"分镇街用电量.xlsx");
	}
	
	public int DFread() throws IOException{
		return datastructure.DF_reader(Origin_data_Add+"宏观数据.xlsx");
	}
	//全社会全年用电量预测
	public boolean a_Y_pred(int year){//年度全社会用电量
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Predict/all"));
		filestructure.deleteFile(new File(Result_Add+"Pred/all"));
		//1.读取原始数据，生成csv文档放到data4r文件夹相应位置
		int readInfo=10;//检验读取是否出错，与预测是否需要跳月无关
		int jump=5;
		try {
			readInfo = Allread();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				jump = datastructure.a_Y_dataProvide(year, Data4R_Add+"Predict/all/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("总用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		//2.R运算得到csv文件结果
		System.out.println("jump="+Integer.toString(jump));
		if((readInfo!=10)&&(jump<4)&&(jump>-12)){
			try {
				if(jump>=0){
					predictor.apred(12, 1, jump);
				}
				else{
					//检查是否需要逆平滑
					boolean need_resmo=false;
					for(int m=1;m<=3;m++){
						if((datastructure.SpringEffect.get(Integer.toString(year*100+m))!=null)&&((m+jump)>0)){
							need_resmo = true;
							//如果存在本年度需平滑月份在需预测范围内，则需要提供resmo
						}
					}
					if(need_resmo){
						predictor.apred(12+jump, 1, 0);
					}
					else{
						predictor.apred(12+jump, 0, 0);
					}
//					System.out.println("jump="+Integer.toString(12+jump));
				}
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
		}
		else {
			debugger.Provide_err();//数据不足，或需要预测的时间段内均已有真实值。
			return false;
		}
		System.out.println("allPred passed");
		//3.生成分析报告
		try {
			analysor.Y_allPred_rpt(year-3, year,datastructure);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			debugger.Report_err();
			e.printStackTrace();
			return false;
		}
		System.out.println("All Ana created succeed");
		//4.将数据读入类中
		try {
			if(jump>=0){
				resultstructure.allpred.get_rlt(Result_Add+"Pred/all/", Integer.toString(year*100+1), 12);
			}
			else {
				resultstructure.allpred.get_rlt(Result_Add+"Pred/all/", Integer.toString(year*100+1-jump), 12+jump);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("AllPred result have been set into result Structure");
		return true;
	}
	
	//全社会半年用电量预测
	public boolean a_HY_pred(int year,int uod){//年度全社会用电量
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Predict/all"));
		filestructure.deleteFile(new File(Result_Add+"Pred/all"));
		//1.生成csv文档放到需要的空间位置
		int readInfo=10;//检验读取是否出错，与预测是否需要跳月无关
		int jump=5;
		try {
			readInfo = Allread();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0){
			try {
				jump = datastructure.a_HY_dataProvide(year, Data4R_Add+"Predict/all/",uod);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
			}
		}
		else{
			System.out.println("总用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		//2.R运算得到csv文件结果
		System.out.println("jump="+Integer.toString(jump));
		if((readInfo!=10)&&(jump<4)&&(jump>-6)){
			try {
				if(uod==0){
					if(jump>=0){
						predictor.apred(6, 1, jump);
					}
					else{
						//检查是否需要逆平滑
						boolean need_resmo=false;
						for(int m=1;m<=3;m++){
							if((datastructure.SpringEffect.get(Integer.toString(year*100+m))!=null)&&((m+jump)>0)){
								need_resmo = true;
								//如果存在本年度需平滑月份在需预测范围内，则需要提供resmo
							}
						}
						if(need_resmo){
							predictor.apred(6+jump, 1, 0);
						}
						else{
							predictor.apred(6+jump, 0, 0);
						}
						System.out.println("jump="+Integer.toString(12+jump));
					}
				}
				else{
					if(jump>=0){
						predictor.apred(6, 0, jump);
					}
					else{
						predictor.apred(6+jump, 0, 0);
					}
				}
				
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			System.out.println("allPred passed");
			//3.生成分析报告
			try {
				analysor.HY_allPred_rpt(year-3, year,datastructure,uod);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Report_err();
				return false;
			}
			System.out.println("All Ana created succeed");
			//4.将数据读入类中
			try {
				if(jump>=0){
					resultstructure.allpred.get_rlt(Result_Add+"Pred/all/", Integer.toString(year*100+1+uod*6), 6);
				}
				else {
					resultstructure.allpred.get_rlt(Result_Add+"Pred/all/", Integer.toString(year*100+1-jump+uod*6), 6+jump);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Output_err();
				return false;
			}
			System.out.println("AllPred result have been set into result Structure");
			return true;
		}
		else{
			debugger.Provide_err();
			return false;
		}
	}

	public boolean a_S_pred(int year,int season) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Predict/all"));
		filestructure.deleteFile(new File(Result_Add+"Pred/all"));
		//1.生成csv文档放到需要的空间位置
		int readInfo=10;//检验读取是否出错，与预测是否需要跳月无关
		int jump=5;
		try {
			readInfo = Allread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				jump = datastructure.a_S_dataProvide(year, Data4R_Add+"Predict/all/",season);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
			}
		}
		else{
				System.out.println("总用电量数据输入出错！！");
				debugger.Reader_err();
				return false;
		}
		//2.R运算得到csv文件结果
		System.out.println("jump="+Integer.toString(jump));
		if((readInfo!=10)&&(jump<4)&&(jump>-3)){
			try {
				if(season==1){
					if(jump>=0){
						predictor.apred(3, 1, jump);
					}
					else{
						//检查是否需要逆平滑
						boolean need_resmo=false;
						for(int m=1;m<=3;m++){
							if((datastructure.SpringEffect.get(Integer.toString(year*100+m))!=null)&&((m+jump)>0)){
								need_resmo = true;
								//如果存在本年度需平滑月份在需预测范围内，则需要提供resmo
							}
						}
						if(need_resmo){
							predictor.apred(3+jump, 1, 0);
						}
						else{
							predictor.apred(3+jump, 0, 0);
						}
						System.out.println("jump="+Integer.toString(12+jump));
					}
				}
				else{
					if(jump>=0){
						predictor.apred(3, 0, jump);
					}
					else{
						predictor.apred(3+jump, 0, 0);
					}
				}					
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			System.out.println("allPred passed");
			//3.生成分析报告
			try {
				analysor.S_allPred_rpt(year-3, year,datastructure,season);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Report_err();
				return false;
			}
			System.out.println("All Ana created succeed");
			//4.将数据读入类中
			try {
				if(jump>=0){
					resultstructure.allpred.get_rlt(Result_Add+"Pred/all/", Integer.toString(year*100+1+(season-1)*3), 3);
				}
				else {
					resultstructure.allpred.get_rlt(Result_Add+"Pred/all/", Integer.toString(year*100+1-jump+(season-1)*3), 3+jump);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Output_err();
				return false;
			}
			System.out.println("AllPred result have been set into result Structure");
			return true;
		}
		else{
			debugger.Provide_err();
			return false;
		}
	}		
		
	public boolean t_Y_pred(int year) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Predict/town"));
		filestructure.deleteFile(new File(Result_Add+"Pred/town"));
		//1.生成csv文档放到需要的空间位置
		int readInfo=10;//检验读取是否出错，与预测是否需要跳月无关
		int jump=5;
		try {
			readInfo = Townread();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				jump = datastructure.t_Y_dataProvide(year, Data4R_Add+"Predict/town/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("分镇街用电量用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		//2.R运算得到csv文件结果
		System.out.println("jump="+Integer.toString(jump));
		if((readInfo!=10)&&(jump<4)&&(jump>-12)){
			try {
				if(jump>=0){
					predictor.tpred(12,jump);
				}
				else{
					predictor.tpred(12+jump, 0);
					System.out.println("jump="+Integer.toString(12+jump));
				}
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			System.out.println("townPred passed");
			//3.生成分析报告
			try {
				analysor.Y_townPred_rpt(year-3, year,datastructure);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Report_err();
				return false;
			}
			System.out.println("Town Ana created succeed");
			//4.将数据读入类中
			try {
				if(jump>=0){
					resultstructure.townpred.get_rlt(Result_Add+"Pred/town/", Integer.toString(year*100+1), 12);
				}
				else {
					resultstructure.townpred.get_rlt(Result_Add+"Pred/town/", Integer.toString(year*100+1-jump), 12+jump);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Output_err();
				return false;
			}
			System.out.println("TownPred result have been set into result Structure");
			return true;
		}
		else {
			debugger.Provide_err();
			return false;
		}
	}

	public boolean t_HY_pred(int year,int uod){
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Predict/town"));
		filestructure.deleteFile(new File(Result_Add+"Pred/town"));
		//1.生成csv文档放到需要的空间位置
		int readInfo=10;//检验读取是否出错，与预测是否需要跳月无关
		int jump=5;
		try {
			readInfo = Townread();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				jump = datastructure.t_HY_dataProvide(year, Data4R_Add+"Predict/town/",uod);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("分镇街用电量用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		
		//2.R运算得到csv文件结果
		System.out.println("jump="+Integer.toString(jump));
		if((readInfo!=10)&&(jump<4)&&(jump>-6)){
			try {
				if(jump>=0){
					predictor.tpred(6,jump);
				}
				else{
					predictor.tpred(6+jump, 0);
//					System.out.println("jump="+Integer.toString(6+jump));
				}
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			System.out.println("townPred passed");
			//3.生成分析报告
			try {
				analysor.HY_townPred_rpt(year-3, year,datastructure,uod);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Report_err();
				return false;
			}
			System.out.println("Town Ana created succeed");
			//4.将数据读入类中
			try {
				if(jump>=0){
					resultstructure.townpred.get_rlt(Result_Add+"Pred/town/", Integer.toString(year*100+1+uod*6), 6);
				}
				else {
					resultstructure.townpred.get_rlt(Result_Add+"Pred/town/", Integer.toString(year*100+1-jump+uod*6), 6+jump);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Output_err();
				return false;
			}
			System.out.println("TownPred result have been set into result Structure");
			return true;
		}
		else {
			debugger.Provide_err();
			return false;
		}
	}

	public boolean a_Y_Check(int year) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Check/all"));
		filestructure.deleteFile(new File(Result_Add+"Pcheck/all"));
		//1.读入数据
		int readInfo;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo = Allread();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				cancheck = datastructure.a_Y_Check_dataProvide(year, Data4R_Add+"Check/all/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("总用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		
		//2.进行预测检验
		if(cancheck==true){
			try {
				checker.acheck(12,1);
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
//			try {
//				checker.acheck(12,1);
//			} catch (REXPMismatchException | REngineException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		else{
			System.out.println("数据输入不足，不能做年度预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.Y_allCheck_rpt(year-3, year);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.allcheck.get_rlt(Result_Add+"PCheck/all/", Integer.toString(year*100+1), 12);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("AllCheck result have been set into result Structure");
		return true;
	}

	public boolean a_HY_Check(int year,int uod) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Check/all"));
		filestructure.deleteFile(new File(Result_Add+"Pcheck/all"));
		//1.读入数据
		int readInfo;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo = Allread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				cancheck = datastructure.a_HY_Check_dataProvide(year, Data4R_Add+"Check/all/",uod);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("总用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		
		//2.进行预测检验
		if(cancheck==true){
			try {
				if(uod==0){
					checker.acheck(6,1);
				}
				else {
					checker.acheck(6,0);
				}
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
//			try {
//				if(uod==0){
//					checker.acheck(6,1);
//				}
//				else {
//					checker.acheck(6,0);
//				}
//			} catch (REXPMismatchException | REngineException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		else{
			System.out.println("数据输入不足，不能做半年度预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.HY_allCheck_rpt(year-3, year,uod);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.allcheck.get_rlt(Result_Add+"PCheck/all/", Integer.toString(year*100+1+uod*6), 6);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("AllCheck result have been set into result Structure");
		return true;
	}

	public boolean a_S_Check(int year,int season) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Check/all"));
		filestructure.deleteFile(new File(Result_Add+"Pcheck/all"));
		//1.读入数据
		int readInfo;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo = Allread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				cancheck = datastructure.a_S_Check_dataProvide(year, Data4R_Add+"Check/all/",season);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("总用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		//2.进行预测检验
		if(cancheck==true){
			try {
				if(season==1){
					checker.acheck(3,1);
				}
				else {
					checker.acheck(3,0);
				}
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
		}
		else{
			System.out.println("数据输入不足，不能做季度预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.S_allCheck_rpt(year-3, year,season);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.allcheck.get_rlt(Result_Add+"PCheck/all/", Integer.toString(year*100+1+(season-1)*3), 3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("AllCheck result have been set into result Structure");
		return true;
	}
	
	public boolean t_Y_Check(int year) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Check/town"));
		filestructure.deleteFile(new File(Result_Add+"Pcheck/town"));
		//1.读入数据
		int readInfo;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo = Townread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				cancheck = datastructure.t_Y_Check_dataProvide(year, Data4R_Add+"Check/town/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("用电量数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		
		//2.进行预测检验
		if(cancheck==true){
			try {
				checker.tcheck(12);
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			
		}
		else{
			System.out.println("数据输入不足，不能做年度预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.Y_townCheck_rpt(year-3, year);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.towncheck.get_rlt(Result_Add+"PCheck/town/", Integer.toString(year*100+1), 12);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("TownCheck result have been set into result Structure");
		return true;
	}

	public boolean t_HY_Check(int year,int uod){
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Check/town"));
		filestructure.deleteFile(new File(Result_Add+"Pcheck/town"));
		//1.读入数据
		int readInfo=1;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo = Townread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if(readInfo==0)	{
			try {
				cancheck = datastructure.t_HY_Check_dataProvide(year, Data4R_Add+"Check/town/",uod);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("用电量数据输入出错！！");
			debugger.Provide_err();
			return false;
		}
		//2.进行预测检验
		if(cancheck==true){
			try {
				checker.tcheck(6);
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			
		}
		else{
			System.out.println("数据输入不足，不能做半年度预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.HY_townCheck_rpt(year-3, year,uod);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.towncheck.get_rlt(Result_Add+"PCheck/town/", Integer.toString(year*100+1+uod*6), 6);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("TownCheck result have been set into result Structure");
		return true;
	}
	
	public boolean var_pred(int year) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Predict/var"));
		filestructure.deleteFile(new File(Result_Add+"VarAna"));
		//1.读入数据
		int readInfo1,readInfo2;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo1 = Allread();
			readInfo2 = DFread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if((readInfo2==0)&&(readInfo1==0))	{
			try {
				cancheck = datastructure.varPred_dataProvide(year, Data4R_Add+"Predict/var/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("宏观经济数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		
		//2.进行预测检验
		if(cancheck==true){
			try {
				predictor.varpred(1);
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			
		}
		else{
			System.out.println("数据输入不足，不能做年度预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.varAna();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.varpred.get_rlt(Result_Add+"VarAna/", year);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("VarAna result have been set into result Structure");
		return true;
		
	}

	public boolean var_Check(int year) {
		//0.清空文件夹结构
		filestructure.deleteFile(new File(Data4R_Add+"Check/var"));
		filestructure.deleteFile(new File(Result_Add+"Pcheck/var"));
		//1.读入数据
		int readInfo1,readInfo2;//检验读取是否出错
		boolean cancheck = false;
		try {
			readInfo1 = Allread();
			readInfo2 = DFread();//最后需要统一，只读一次？
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			debugger.Reader_err();
			return false;
		}
		if((readInfo2==0)&&(readInfo1==0))	{
			try {
				cancheck = datastructure.varPred_dataProvide(year, Data4R_Add+"Check/var/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Provide_err();
				return false;
			}
		}
		else{
			System.out.println("宏观经济数据输入出错！！");
			debugger.Reader_err();
			return false;
		}
		
		//2.进行预测检验
		if(cancheck==true){
			try {
				checker.var_hw_comp();
			} catch (REXPMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			} catch (REngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				debugger.Calculate_err();
				return false;
			}
			
		}
		else{
			System.out.println("数据输入不足，不能做预测检验");
			debugger.Provide_err();
			return false;
		}
		
		//3.输出分析报告
		try {
			analysor.varCheck_rpt(year-3, year);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Report_err();
			return false;
		}
		
		//4.将数据读入类中
		try {
			resultstructure.varcheck.get_rlt(Result_Add+"PCheck/var/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			debugger.Output_err();
			return false;
		}
		System.out.println("VarCheck result have been set into result Structure");
		return true;
	}
	
	public boolean credit(){
		try {
			creditor.credit_assessment();
			resultstructure.creditassess.get_rlt(Result_Add+"Credit/");
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (REngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void reset_data() {
		datastructure = new dataStruct();
	}

//	public static boolean Ins_Rpackage(String rfile) {
//		Runtime run = Runtime.getRuntime();
//		try {
//			System.out.println("Rscript "+rfile+"installPackage.r");
//			String cmd = "Rscript "+rfile+"installPackage.r";
//			run.exec(cmd);
//			System.out.println("RPackage安装成功");
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			System.out.println("RPackage安装失败");
//			return false;
//		}
//		return true;
//	}
}



