package DataManager;

import java.io.File;

public class fileStruct {

	//清空文件夹中的所有文件
	public void deleteFile(File file) {  
		if (file.exists()) {//判断文件是否存在  
			if (file.isDirectory()) {//否则如果它是一个目录  
				File[] files = file.listFiles();//声明目录下所有的文件 files[];  
				for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件  
					this.deleteFile(files[i]);//把每个文件用这个方法进行迭代  
				}  
			}
			if (file.isFile()){
				file.delete();
			}
	    } else {  
	    	System.out.println("所删除的文件不存在");  
	    }  
	} 

}
