package DataManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import com.csvreader.CsvReader;
import com.sun.jndi.url.corbaname.corbanameURLContextFactory;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class resultStruct {

	public allPred allpred = new allPred();
	public townPred townpred = new townPred();
	public varPred varpred = new varPred();
	public allCheck allcheck = new allCheck();
	public townCheck towncheck = new townCheck();
	public varCheck varcheck = new varCheck();
	public creditAssess creditassess = new creditAssess();

	public class allPred {
		public Map<String, Double> rlt = new HashMap<String, Double>();
		public String Asmonth,Aemonth;
		public boolean read; 
		
		public allPred(){
			read = false;
		}
		//file是输出所在的文件夹地址（以/结束）
		//Smonth为输出的起始月份
		//Pmonth为预测的月份数
		//若读取成功，返回true
		public boolean get_rlt(String file,String Smonth,Integer Pmonth) throws IOException {
			try {
				CsvReader r = new CsvReader(file+"allpred.csv", ',', Charset.forName("GB18030"));
				r.readHeaders();
				Asmonth = Smonth;
				String month=Smonth;
				for(int i=1;i<=Pmonth;i++){
					r.readRecord();
					String x = r.get("x");
					rlt.put(month, Double.parseDouble(x));
					month = Integer.toString(Integer.parseInt(month)+1);

				}
				Aemonth = Integer.toString(Integer.parseInt(Asmonth)+Pmonth-1);
				r.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
		}
	}
	
	public class townPred {
		//rlt 镇街名称，用电量总和
		public Map<String, Double> rlt = new HashMap<String, Double>();
		public String Tsmonth,Temonth;
		public boolean read;
		
		public townPred(){
			read = false;
		}
		
		public boolean get_rlt(String file,String Smonth,Integer Pmonth) throws IOException {
			try {
				CsvReader r = new CsvReader(file+"分镇街预测结果.csv", ',', Charset.forName("GB18030"));
				r.readHeaders();
				Tsmonth = Smonth;
				Temonth = Integer.toString(Integer.parseInt(Smonth)+Pmonth-1);
				while(r.readRecord()){
					rlt.put(r.get(0), Double.parseDouble(r.get(1)));
				}	
				r.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
		}
	}

	public class varPred {
		public Map<String, Double> rlt = new HashMap<String, Double>();
		public Integer year;
		public double[][] coef = new double[3][5];
		public double[] influ = new double[4];
		public boolean read;
		
		public varPred(){
			read = false;
		}
		
		public boolean get_rlt(String file,Integer y) throws IOException{
			try {
				year = y;
				CsvReader rcoef = new CsvReader(file+"varcoef.csv", ',', Charset.forName("GB18030"));
				CsvReader rinf = new CsvReader(file+"ftinflu.csv", ',', Charset.forName("GB18030"));
				CsvReader rpred = new CsvReader(file+"varpred.csv",',',Charset.forName("GB18030"));
				rcoef.readHeaders();
				rinf.readHeaders();
				rpred.readHeaders();
				//读取方程系数
				rcoef.readRecord();
				for(int i=0;i<3;i++){
					rcoef.readRecord();
					for(int j=1;j<=5;j++){
						coef[i][j-1]= Double.parseDouble(rcoef.get(j));
					}
				}
				rcoef.close();
				//读取平均影响力
				for(int i=0;i<4;i++){
					rinf.readRecord();
					influ[i]=Double.parseDouble(rinf.get(1));
				}
				rinf.close();
				//读取每个月预测结果
				for(int i=1;i<=12;i++){
					rpred.readRecord();
					String month = Integer.toString(year*100+i);
					rlt.put(month, Double.parseDouble(rpred.get(1)));
				}
				rpred.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
		}
	}

	public class allCheck{
		public double mon_bias;
		public double bias;
		
		public class rltList{
			public double pred;
			public double real;
			public double bias;
			
			public rltList(double p,double r,double b){
				pred = p;
				real = r;
				bias = b;
			}
		}
		
		public Map<String, rltList> rlt = new HashMap<String, rltList>();
		public String Asmonth,Aemonth;
		public boolean read;
		
		public allCheck(){
			read = false;
		}
		
		public boolean get_rlt(String file,String Smonth,Integer Pmonth) throws IOException {
			try {
				Asmonth = Smonth;
				Aemonth = Integer.toString(Integer.parseInt(Smonth)+Pmonth-1);
				
				CsvReader r = new CsvReader(file+"allcheck.csv", ',', Charset.forName("GB18030"));
				CsvReader rmon = new CsvReader(file+"monthcheck.csv", ',', Charset.forName("GB18030"));
				r.readHeaders();
				rmon.readHeaders();
				//读取月平均误差，总误差
				r.readRecord();
				mon_bias = Double.parseDouble(r.get(0));
				bias = Double.parseDouble(r.get(1));
				r.close();
				//读取每个月的数据
				double prede,reale,erre;
				String month = Asmonth;
				for(int i=1;i<=Pmonth;i++){
					rmon.readRecord();
					prede = Double.parseDouble(rmon.get(1));
					reale = Double.parseDouble(rmon.get(2));
					if(prede>=reale){
						erre = Double.parseDouble(rmon.get(3));
					}
					else{
						erre = -Double.parseDouble(rmon.get(3));
					}
					rlt.put(month, new rltList(prede, reale,erre));
					month = Integer.toString(Integer.parseInt(month)+1);
				}
				rmon.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
		}
	}

	public class townCheck{
		public class rltList{
			public double bias;
			public double ratio;
			
			public rltList(double b,double r){
				bias = b;
				ratio = r;
			}
		}
		public Map<String, rltList> rlt = new HashMap<String, rltList>();
		public double[][] group= new double[3][3];
		public String Tsmonth,Temonth;
		public boolean read;
		
		public townCheck(){
			read = false;
		}

		public boolean get_rlt(String file,String Smonth,Integer Pmonth) throws IOException{
			try {
				Tsmonth = Smonth;
				Temonth = Integer.toString(Integer.parseInt(Smonth)+Pmonth-1);
				
				CsvReader r = new CsvReader(file+"towncheck.csv", ',', Charset.forName("GB18030"));
				CsvReader ratio = new CsvReader(file+"towncheckratio.csv", ',', Charset.forName("GB18030"));
				r.readHeaders();
				ratio.readHeaders();
				while(r.readRecord()){
					rlt.put(r.get(0), new rltList(Double.parseDouble(r.get(1)), Double.parseDouble(r.get(2))));
				}
				r.close();
				for(int i=0;i<3;i++){
					ratio.readRecord();
					for(int j=0;j<3;j++){
						group[i][j]=Double.parseDouble(ratio.get(j+1));
					}
				}
				ratio.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
		}
	}

	public class varCheck{
		public double year_bias;
		public double mean_bias;
		public double[] mon_bias = new double[12];
		public boolean read;
		
		public varCheck() {
			// TODO Auto-generated constructor stub
			read = false;
		}
		
		public boolean get_rlt(String file) throws IOException{
			try {
				CsvReader r = new CsvReader(file+"var模型与Holt-Winter模型预测结果比较.csv", ',', Charset.forName("GB18030"));
				r.readHeaders();
				r.readRecord();
				year_bias = Double.parseDouble(r.get(1));
				mean_bias = Double.parseDouble(r.get(2));
				for(int i=0;i<12;i++){
					mon_bias[i]=Double.parseDouble(r.get(i+3));
				}
				r.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
			
		}
	}

	public class creditAssess{
		public Map<String, Double> rlt = new HashMap<String, Double>();
		public boolean read;
		
		public creditAssess(){
			read = false;
		}
		
		public boolean get_rlt(String file){
			try {
				CsvReader r = new CsvReader(file+"userassessment.csv", ',', Charset.forName("GB18030"));
				r.readHeaders();
				while(r.readRecord()){
					rlt.put(r.get(1), Double.parseDouble(r.get(2)));
				}
				r.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				read = false;
				return false;
			}
			read = true;
			return true;
		}
	}
}

