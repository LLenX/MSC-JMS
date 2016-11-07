package Epred;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

public class Creditor {
	String dataadd,outputadd,rfileadd;
	public Creditor(String dadd,String opadd,String rfadd) {
		// TODO Auto-generated constructor stub
		dataadd = dadd;
		rfileadd=rfadd;
		outputadd = opadd;
	}
	public void credit_assessment()throws REXPMismatchException,REngineException{
		/*
		 * 全社会用电量预测
		 */
		RConnection rc = new RConnection();
		
		try {
			rc.setStringEncoding("utf8");
			rc.eval("setwd('"+rfileadd+"RCredit/')");
			rc.eval("source('credit_ass.r')");
//		System.out.println("credit_ass('"+rfileadd+"RCredit/','"+dataadd+"','"+rfileadd+"')");
//		rc.eval("credit_ass('"+rfileadd+"RCredit/','"+dataadd+"','"+rfileadd+"')");
//			System.out.println("credit_ass('"+rfileadd+"RCredit/','"+dataadd+"','"+outputadd+"Credit/')");
			rc.eval("credit_ass('"+rfileadd+"RCredit/','"+dataadd+"','"+outputadd+"Credit/')");
			System.out.println("credit assessment load finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.close();
		
	}

}
