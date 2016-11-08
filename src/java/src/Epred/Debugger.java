package Epred;

public class Debugger {

	/*
	 * 为方便调试与检测，Debugger类给出异常处理的接口
	 * 对程序中最有可能出错的地方，已尽可能的使用try-catch结构包裹，当出现异常时，在程序中会调用Debugger类的相应方法。
	 * 因此，若需要程序在发生异常的时候执行固定的操作，只需修改Debugger类的方法即可。
	 */
	
/*
 * 按照功能实现的流程，程序主要有以下环节：
 * 1.构造实例时，启动Rserve监听接口———— 异常主要来源于EPredMain的构造函数
 * 以下是实现某具体功能的流程：
 * 
 * 2.读取数据
 * 		———— 异常主要来源于dataStruct类的A_reader T_reader DF_reader 
 * 			   通常为需要读取的文件不存在或数据格式、编码格式错误。
 * 
 * 3.数据分配
 * 		———— 异常主要来源于dataStruct类中名字含'Provide'的方法。
 * 			   通常为写入csv文件的目录不存在，是EPredMain类中字符串变量Data4R_Add对应的文件目录中的子目录结构出错的问题。
 * 			   或现有数据不符合该预测、检验问题的数据需求
 * 
 * 4.运算并生成计算结果
 * 		———— 异常主要来源于Predictor类与PrecisionChecker类中的各方法。
 * 			   最可能的错误为 
 * 			 a) 文档编码错误（当文档绝对路径含有中文时，有可能出现该错误）
 * 			 b)输出csv等结果的目录不存在，是EPredMain类中字符串变量Result_Add对应的文件目录中的子目录结构出错的问题。
 * 
 * 5.利用结果生成分析报告
 * 		———— 异常主要来源于Analysor类中的方法。
 * 			   通常为在指定文件夹中找不到相应的模板。
 * 
 * 给出以上5种主要错误的异常处理接口。
 * 
 */
	
	
	public void Rserve_err() {
		System.err.println("RServe 监听程序启动失败");
	}
	
	public void Reader_err() {
		System.err.println("数据读取时发生错误");
	}
	
	public void Provide_err() {
		System.err.println("数据分配时发生错误");
	}
	
	public void Calculate_err() {
		System.err.println("运算过程中发生错误");
	}

	public void Report_err() {
		System.err.println("生成分析报告的过程中出错");
	}
	
	public void Output_err() {
		System.err.println("数据导出过程发生错误");
	}
}
