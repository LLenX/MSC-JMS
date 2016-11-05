package fix;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

public class CsvWriter extends com.csvreader.CsvWriter{

	//参数意义参见com.csvreader.CsvWriter
	public CsvWriter(Writer arg0, char arg1) {
		super(arg0, arg1);
	}

	public CsvWriter(String arg0, char arg1, Charset arg2) {
		super(arg0, arg1, arg2);
		// XXX: 增加文件夹判断...
		FileTool.ensurePathFolder(arg0);
	}

	public CsvWriter(String arg0) {
		super(arg0);
		FileTool.ensurePathFolder(arg0);
	}

	public CsvWriter(OutputStream arg0, char arg1, Charset arg2) {
		super(arg0, arg1, arg2);
		
	}

}
