package Epred;

import java.awt.HeadlessException;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

//测试构建EPredMain实例，并测试其中的函数。
public class example {
	static String Origin_data_Add;
	static String Data4R_Add;
	static String Rfile_Add;
	static String Result_Add;
	static String Report_Add;
	static String ipadd,opadd;
	
	private static String get_Dictionary() {
		JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
		String path=null;
		File f=null;
		int flag = 0;
		try{     
			flag=fc.showOpenDialog(null);     
			}    
		    catch(HeadlessException head){     
		         System.out.println("Open File Dialog ERROR!");    
		    }        
		if(flag==JFileChooser.APPROVE_OPTION){
		//获得该文件
			System.out.println(fc.getSelectedFile().toString());
		return(fc.getSelectedFile().toString());
		}
		else return "error";
	
	}
	
	public static EPredMain Epred_sample;
	public static void main(String[] args) {
//		ipadd = get_Dictionary().replace("\\", "/");
//		opadd = get_Dictionary().replace("\\", "/");
		ipadd = "G:/Test2/数据";
		opadd = "G:/Test2/结果";
		Epred_sample = new EPredMain(opadd+"/Result/", opadd+"/Report/", 
				ipadd+'/',opadd+"/Data4r/",ipadd+"/Rfile/",ipadd+"/Model/");
		
//		Epred_sample.credit();
		
//		Epred_sample.a_Y_pred(2016);  //全年度预测
//		Epred_sample.a_HY_pred(2016, 1);  //半年度预测，上下半年均已尝试
//		Epred_sample.a_S_pred(2016, 4); //季度预测，都没有出问题
//		
//		Epred_sample.t_Y_pred(2016);
//		Epred_sample.t_HY_pred(2016,1);
//
//		Epred_sample.a_Y_Check(2015);
//		Epred_sample.a_HY_Check(2016,0);
//		Epred_sample.a_S_Check(2016,1);
//		
//		Epred_sample.t_Y_Check(2015);		
//		Epred_sample.t_HY_Check(2015, 1);
	
//		Epred_sample.var_pred(2016);
//		Epred_sample.var_Check(2016);
	

	}		
}