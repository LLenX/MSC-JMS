package Epred;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

public class PrecisionChecker {

	String dataadd,rfileadd,outputadd;
	public PrecisionChecker(String dadd,String rfadd,String opadd) {
		dataadd=dadd;
		rfileadd = rfadd;
		outputadd = opadd;
	}
	/*
	 * 以下为PrecisionChecker类的主要方法。方法代码均依照以下顺序：
	 * 1.新建RConnection的实例
	 * 2.通过RConnection实例，调用相应的R函数
	 * 3.关闭RConnection
	 * 切记，在建立了Rconnection之后，必须确保在使用对最后调用close()方法。否则将会造成下次申请Rconnection空等，程序无法运行。
	 * 
	 * 各方法调用rfileadd中的R脚本文件，在R中读取dataadd中的数据，在R中完成计算，将结果保存到outputadd当中。
	 * 可以脱离EpredMain中的方法直接调用，但不推荐。
	 * 各方法简介如下：
	 * 
	 * 1.acheck(int range,int needsmooth)
	 * 全社会用电量预测：
	 * range是需要预测的月份数，取值1-12
	 * needsmooth表示是否需要对数据进行逆平滑（0：不需要，1：需要）
	 * 
	 * 2.tcheck(int range)
	 * 分镇街用电量预测：
	 * range是需要预测的月份数，取值1-12
	 * 
	 * 3.var_hw_comp()
	 * 比较副模型与主模型的预测结果的差异，证明副模型的有效性。
	 */	
	public void acheck(int range,int needsmooth)throws REXPMismatchException,REngineException{
		RConnection rc = new RConnection();
		try {
			rc.setStringEncoding("utf8");
			rc.eval("setwd('"+rfileadd+"')");
			rc.eval("source('allCheck.r')");
//		System.out.println("allCheck('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+")");
			rc.eval("allCheck('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+")");
			System.out.println("acheck load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
	}
		
	public void tcheck(int range)throws REXPMismatchException,REngineException{
		RConnection rc = new RConnection();
		try {
			rc.setStringEncoding("utf8");
			rc.eval("setwd('"+rfileadd+"')");
			rc.eval("source('townCheck.r')");
			rc.eval("townCheck('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+")");
			System.out.println("tcheck load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
	}
	
	public void var_hw_comp()throws REXPMismatchException,REngineException{
		/*
		 * 通过将var模型预测结果与Holt-Winters模型结果比较，推断var模型是否正确
		 */
		RConnection rc = new RConnection();
		try {
			rc.setStringEncoding("utf8");
			rc.eval("setwd('"+rfileadd+"')");
			rc.eval("source('var_hw_Check.r')");
//		System.out.println("var_hw_Check('"+dataadd+"','"+outputadd+"')");
			rc.eval("var_hw_Check('"+dataadd+"','"+outputadd+"')");
			System.out.println("var_hw_Check load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
	}
	
}
