package wrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.stardust.function.function;

import fix.FileTool;
import Epred.EPredMain;

/**
 * 
 * @author Stardust
 *
 *
 *          参数说明: 
 *          ***注意：所有文件夹路径必须以/结束
 *          		输出文件夹的文件夹结构将会被自动创建
 *          		参数之间不分顺序
 *          		重复为相同选项指定参数时，后面的参数会覆盖前面的
 *          		多余参数会被忽略
 *          
 *         	-i 输入文件夹总路径(也即'数据'文件夹路径)
 *          -o 输出文件夹总路径(也即'结果'文件夹路径) 
 *          -l 日志文件保存路径（默认为输出文件夹下log.txt)
 *          ========== 以下没有卵用=========
 *             以下参数若不指定，将为上述总路径的某个子文件夹；若指定，将会覆盖总路径的设定。
 *             具体参数意义参见原程序相关材料。
 * 			   -result 默认输出路径 下 Result/
 * 			   -report 默认输出路径 下 Report/
 * 			   -origin-data 默认输入路径 
 * 			   -data4r 默认输出路径 下 Data4r/
 * 			   -rfile 默认输入路径 下 Rfile/
 *             -model 默认输入路径 下 Model/
 *          ============== 以上 ============
 *          
 *          预测：
 *          -p-area
 *          	all 全社会
 *          	town 分城镇
 *          -p-year 年份
 *          -p-duration 时间跨度 
 *          	annual 年度
 *          	semi-annual 半年度
 *          	quarter 季度
 *          -p-which 年度时该选项被忽略
 *          		 半年度时0为上半年，1为下半年
 *          		 季度时1~4为1~4季度
 *         
 *          精度检验：
 *          -c-area
 *          	all 全社会
 *          	town 分城镇
 *          	vice-model 副模型
 *          -c-duration 同上
 *          -c-which 同上
 *          
 *          关联性分析：
 *          -a
 *          
 *          其他选项：
 *          -rm 自动清空结果文件夹下所有文件（不包括文件夹）
 *          
 *          示例
 *             java -jar xxx.jar -i e:/data/ -o e:/output/ -p-area all -p-year 2016 -p-duration quarter -p-which 2
 *          
 * 
 * 
 * 
 *         -
 */


public class Wrapper {

	public static void main(String[] args) {
		LogPrintStream logPrintStream = new LogPrintStream();
		System.setErr(logPrintStream);
		System.setOut(logPrintStream);
		try {
			ArgumentReader reader = new ArgumentReader(args);
			reader.setParameterNumber("a", 0);
			reader.setParameterNumber("rm", 0);
			Context context = new Context();
			boolean removeAllFiles = false;
			while (reader.hasMoreArgument()) {
				Argument arg = reader.read();
				if(arg.option.equals("rm"))
					removeAllFiles = true;
				else if (arg.parameters == null || arg.parameters.length == 0 || !context.setFieldIfExists(arg.option, arg.parameters[0])) {
					TaskFactory.addArugment(arg);
				} 
			}
			context.fillDefaultValue();
			logPrintStream.setLogPath(context.l);
			ensureOutputFolder(context);
			if(removeAllFiles)
				FileTool.clearFolders(context.result, context.report, context.data4r, context.o);
			EPredMain ePredMain = new EPredMain(context.result, context.report, context.origin_data, context.data4r, context.rfile, context.model);
			int failCount = TaskFactory.executeAll(ePredMain);
			System.out.println("failCount=" + failCount);
			logPrintStream.flush();
			System.exit(failCount);
		} catch (Exception e) {
			e.printStackTrace();
			logPrintStream.flush();
			System.exit(-1);
		}

		
	}


	private static void ensureOutputFolder(Context context) {
		FileTool.ensureSubFolders(context.result, "Credit", "VARAna");
		FileTool.ensureSubFolders(context.result + "PCheck/", "all", "town", "var");
		FileTool.ensureSubFolders(context.result + "Pred/", "all", "town");
		FileTool.ensureSubFolders(context.report, "Analysis/", "Check", "Pred");
		FileTool.ensureSubFolders(context.data4r + "Check/", "all", "town", "var");
		FileTool.ensureSubFolders(context.data4r + "Predict/", "all", "town", "var", "indu");
		FileTool.ensurePathFolder(context.data4r + "credit_assessment/");
	}


	@SuppressWarnings("unused")
	public static class Context {
		private String i, o, l;
		String result, report, origin_data, data4r, rfile, model;

		private FieldSetter fieldSetter;

		public Context() {
			fieldSetter = new FieldSetter(this);
			fieldSetter.setDefaultValue("origin-data", new function<String>() {
				String getDefaltValue() {
					return i;
				}
			});
			fieldSetter.setDefaultValue("model", new function<String>() {
				String getDefaltValue() {
					return i + "Model/";
				}
			});
			fieldSetter.setDefaultValue("rfile", new function<String>() {
				String getDefaltValue() {
					return i + "Rfile/";
				}
			});
			fieldSetter.setDefaultValue("data4r", new function<String>() {
				String getDefaltValue() {
					return o + "Data4r/";
				}
			});
			fieldSetter.setDefaultValue("report", new function<String>() {
				String getDefaltValue() {
					return o + "Report/";
				}
			});
			fieldSetter.setDefaultValue("result", new function<String>() {
				String getDefaltValue() {
					return o + "Result/";
				}
			});
			fieldSetter.setDefaultValue("l", new function<String>() {
				String getDefaltValue() {
					return (o != null ? o : "" ) + "log.txt";
				}
			});
		}

	
		public boolean setFieldIfExists(String fieldName, String value) {
			return fieldSetter.setFieldIfExists(fieldName, value);
		}

		public void fillDefaultValue() {
			fieldSetter.fillDefaultValue();
			result = result.replace('\\', '/');
			report = report.replace('\\', '/');
			origin_data = origin_data.replace('\\', '/');
			data4r = data4r.replace('\\', '/');
			model = model.replace('\\', '/');
			rfile = rfile.replace('\\', '/');
		}

	}

	public static abstract class Task {
		protected Map<String, String> paramters = new TreeMap<String, String>();

		public abstract boolean execute(EPredMain ePredMain) throws IOException;

		public void setParameter(String paramName, String value) {
			paramters.put(paramName, value);
		}

		public String getParamter(String optionName) {
			String param = paramters.get(optionName);
			if(param == null)
				throw new IllegalArgumentException("缺少选项:" + optionName);
			return param;
		}

		public int getParamterInt(String optionName) {
			try {
				return Integer.parseInt(getParamter(optionName));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("选项" + optionName + "的参数应该是整数");
			}
		}
	}

	public static class TaskFactory {
		private static Task predict = new Task() {

			@Override
			public boolean execute(EPredMain ePredMain) throws IOException {
				boolean isAreaAll = getParamter("p-area").equals("all");
				switch (getParamter("p-duration")) {
				case "annual":
					if (!(isAreaAll ? ePredMain.a_Y_pred(getParamterInt("p-year")) :  ePredMain.t_Y_pred(getParamterInt("p-year"))))
						return false;
					break;
				case "semi-annual":
					if (!(isAreaAll ? ePredMain.a_HY_pred(getParamterInt("p-year"), getParamterInt("p-which")) : ePredMain.t_HY_pred(getParamterInt("p-year"), getParamterInt("p-which"))))
						return false;
					break;
				case "quarter":
					if (!ePredMain.a_S_pred(getParamterInt("p-year"), getParamterInt("p-which")))
						return false;
					break;
				default:
					throw new IllegalArgumentException("选项p-duration的参数 " + getParamter("p-duration") + " 不合法");
				}
				return true;
			}
		};
		private static boolean executePredict = false;

		private static Task precisionCheck = new Task() {

			@Override
			public boolean execute(EPredMain ePredMain) throws IOException {
				if(getParamter("c-area").equals("vice-model")){
					return ePredMain.var_Check(getParamterInt("c-year"));
				}
				boolean isAreaAll = getParamter("c-area").equals("all");
				switch (getParamter("c-duration")) {
				case "annual":
					if (!(isAreaAll ? ePredMain.a_Y_Check(getParamterInt("c-year")) :  ePredMain.t_Y_Check(getParamterInt("c-year"))))
						return false;
					break;
				case "semi-annual":
					if (!(isAreaAll ? ePredMain.a_HY_Check(getParamterInt("c-year"), getParamterInt("c-which")) : ePredMain.t_HY_Check(getParamterInt("c-year"), getParamterInt("c-which"))))
						return false;
					break;
				case "quarter":
					if (!ePredMain.a_S_Check(getParamterInt("c-year"), getParamterInt("c-which")))
						return false;
					break;
				default:
					throw new IllegalArgumentException("选项c-duration的参数 " + getParamter("c-duration") + " 不合法");
				}
				return true;
			}
		};
		private static boolean executeCheck = false;

		private static Task analyze = new Task() {

			@Override
			public boolean execute(EPredMain ePredMain) throws IOException {
				return ePredMain.var_pred(Calendar.getInstance().get(Calendar.YEAR));
			}
		};
		private static boolean executeAnaylze = false;

		public static void addArugment(Argument argument) {
			if (argument.option.equals("a")) {
				executeAnaylze = true;
				return;
			}
			if (argument.option.startsWith("p")) {
				executePredict = true;
				predict.setParameter(argument.option, argument.parameters[0]);
				return;
			}
			if (argument.option.startsWith("c")) {
				executeCheck = true;
				precisionCheck.setParameter(argument.option, argument.parameters[0]);
				return;
			}
			throw new IllegalArgumentException("参数 " + argument + " 不合法");
		}

		public static int executeAll(EPredMain ePredMain) {
			int failCount = 0;
			try {
				if (executePredict)
					failCount += predict.execute(ePredMain) ? 0 : 1;
				if (executeCheck)
					failCount += precisionCheck.execute(ePredMain) ? 0 : 1;
				if (executeAnaylze)
					failCount += analyze.execute(ePredMain) ? 0 : 1;
			} catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
			return failCount;
		}
	}

	public static class ArgumentReader {

		private String[] args;
		private int readPos = -1;
		private Map<String, Integer> parameterNumberMap = new HashMap<String, Integer>();

		public ArgumentReader(String[] args) {
			if (args == null || args.length == 0)
				throw new IllegalArgumentException("Wrapper: 参数错误，参数不能为空");
			this.args = args;
		}

		public void setParameterNumber(String option, int number) {
			parameterNumberMap.put(option, number);
		}

		protected int getParameterNumber(String option) {
			Integer number = parameterNumberMap.get(option);
			return number == null ? 1 : number.intValue();
		}

		public Argument read() {
			String option = readString();
			if (option.startsWith("-") && option.length() > 1) {
				option = option.substring(1);
				String[] param = new String[getParameterNumber(option)];
				for (int i = 0; i < param.length; i++) {
					param[i] = readString();
				}
				return new Argument(option, param);
			}
			throw new IllegalArgumentException("选项应该是以-开头的长度大于1的字符串: option=" + option);
		}

		private String readString() {
			readPos++;
			checkReadPos();
			return args[readPos];
		}

		public boolean hasMoreArgument() {
			return readPos < args.length - 1;
		}

		private void checkReadPos() {
			if (readPos >= args.length) {
				throw new IllegalArgumentException("参数错误。参数数量不足。");
			}
		}

	}

	public static class Argument {
		public String option;
		public String[] parameters;

		public Argument(String option, String... parameters) {
			this.option = option;
			this.parameters = parameters;
		}

		public String toString() {
			return "Argument[option=" + option + ", parameters=" + Arrays.toString(parameters) + "]";
		}
	}

	public static class FieldSetter {
		private Object target;
		private Map<String, function<String>> defaultValueMap = new TreeMap<String, function<String>>();

		public FieldSetter(Object target) {
			this.target = target;
		}

		public void setDefaultValue(String fieldName, function<String> defaultValueFunction) {
			defaultValueMap.put(fieldName.replace('-', '_'), defaultValueFunction);
		}

		public void fillDefaultValue() {
			Field[] fields = target.getClass().getDeclaredFields();
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.get(target) == null && defaultValueMap.containsKey(field.getName())) {
						set(field, defaultValueMap.get(field.getName()).call());
					}
				} catch (Exception e) {
					throw new RuntimeException("Wrapper开发者的锅", e);
				}
			}
		}

		public boolean setFieldIfExists(String fieldName, String value) {
			try {
				Field field = target.getClass().getDeclaredField(fieldName);
				set(field, value);
				return true;
			} catch(NoSuchFieldException e){
				return false;
			} catch (Exception e) {
				throw new RuntimeException("Wrapper开发者的锅", e);
			}
		}

		private void set(Field field, String value) {
			try {
				field.setAccessible(true);
				if (field.getClass().equals(Integer.TYPE)) {
					field.set(target, Integer.valueOf(value));
				} else {
					field.set(target, value);
				}
			} catch (Exception e) {
				throw new RuntimeException("Wrapper开发者的锅", e);
			}

		}

	}

	public static class LogPrintStream extends PrintStream {

		StringBuffer logBuffer = new StringBuffer();
		String logPath;

		public LogPrintStream() {
			super(System.out);
		}

		public void setLogPath(String path) {
			logPath = path;
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			String message = new String(buf, off, len);
			message = message.replaceAll("\n", "\n[" + new GregorianCalendar().toZonedDateTime() + "]");
			logBuffer.append(message);
			super.write(buf, off, len);
		}

		public void flush() {
			if (logPath == null || logPath.isEmpty())
				logPath = "log.txt";
			try {
				FileTool.ensurePathFolder(logPath);
				File file = new File(logPath);
				if(!file.exists())
					file.createNewFile();
				FileOutputStream fos = new FileOutputStream(logPath, true);
				fos.write(logBuffer.toString().getBytes());
				fos.flush();
				fos.close();
				logBuffer.delete(0, logBuffer.length());
			} catch (FileNotFoundException whoCares) {
				whoCares.printStackTrace(System.out);
			} catch (IOException whoCares) {
				whoCares.printStackTrace(System.out);
			}
		}
		
	}
}
