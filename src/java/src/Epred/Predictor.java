package Epred;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;

public class Predictor {

	String dataadd,rfileadd,outputadd;
//构造函数的细节在EpredMain类的66-69行
	public Predictor(String dadd,String rfadd,String opadd) {
		dataadd = dadd;
		rfileadd=rfadd;
		outputadd = opadd;
	}
	
/*
 * 以下为Predictor类的主要方法。方法代码均依照以下顺序：
 * 1.新建RConnection的实例
 * 2.通过RConnection实例，调用相应的R函数
 * 3.关闭RConnection
 * 切记，在建立了Rconnection之后，必须确保在使用对最后调用close()方法。否则将会造成下次申请Rconnection空等，程序无法运行。
 * 
 * 各方法调用rfileadd中的R脚本文件，在R中读取dataadd中的数据，在R中完成计算，将结果保存到outputadd当中。
 * 可以脱离EpredMain中的方法直接调用，但不推荐。
 * 各方法简介如下：
 * 
 * 1.apred(int range,int needsmooth,int numjump)
 * 全社会用电量预测：
 * range是需要预测的月份数，取值1-12
 * needsmooth表示是否需要对数据进行逆平滑（0：不需要，1：需要）
 * numjump为需要跳过几个月不预测（取值0-3，最多只能跳过3个月）
 * 
 * 2.tpred(int range,int numjump)
 * 分镇街用电量预测：
 * range是需要预测的月份数，取值1-12
 * numjump为需要跳过几个月不预测（取值0-3，最多只能跳过3个月）
 * 
 * 3.varpred()
 * 利用副模型预测未来一年的用电量。（在csv文档中有，但在分析报告中不展示）
 * 生成副模型的分析结果。
 */
	public void apred(int range,int needsmooth,int numjump)throws REXPMismatchException,REngineException{
		RConnection rc = new RConnection();
		try {
			rc.setStringEncoding("utf8");
//			System.out.println("try(rfileadd=\""+rfileadd+"\",silent=TRUE)");
//			rc.eval("try(setwd('"+rfileadd+"'),silent=TRUE)");
			rc.eval("setwd('"+rfileadd+"')");
//			System.out.println(xRexp.asString());
			rc.eval("source('allPred.r')");
//			System.out.println("allPred('"+dataadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+")");
//			String cmd = "allPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+")";
			rc.eval("allPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+")");
//			REXP xRexp = rc.parseAndEval("try(allPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+"),silent=TRUE)");
//			System.out.println("cmd="+"try(allPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+"),silent=TRUE)");
			
//			
//			System.out.println("allPred(\""+dataadd+"\",\""+outputadd+"\","+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+")");
//			rc.eval("allPred(\""+dataadd+"\",\""+outputadd+"\","+Integer.toString(range)+","+Integer.toString(needsmooth)+","+Integer.toString(numjump)+")");
			System.out.println("apred load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
		
	}
	
	public void tpred(int range,int numjump)throws REXPMismatchException,REngineException{
		RConnection rc = new RConnection();
		try {
			rc.setStringEncoding("utf8");
			rc.eval("setwd('"+rfileadd+"')");
			rc.eval("source('townPred.r')");
			System.out.println("townPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(numjump)+")");
			rc.eval("townPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(range)+","+Integer.toString(numjump)+")");
			System.out.println("tpred load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
	}
	
	public void varpred(int needsmooth)throws REXPMismatchException,REngineException{
		/*
		 * VAR 记得要先对数据进行预处理！都提高到十万级别
		 */
		RConnection rc = new RConnection();
		try {
			rc.setStringEncoding("utf8");
			rc.eval("setwd('"+rfileadd+"')");
			rc.eval("source('varPred.r')");
//		System.out.println("varPred('"+dataadd+"','"+rfileadd+"',"+Integer.toString(needsmooth)+")");
			rc.eval("varPred('"+dataadd+"','"+outputadd+"',"+Integer.toString(needsmooth)+")");
			System.out.println("vpred load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
	}
	
}



