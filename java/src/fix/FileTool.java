package fix;

import java.io.File;

public class FileTool {

	public static void ensurePathFolder(String path) {
		int i = path.lastIndexOf('/');
		if (i < 0) {
			i = path.lastIndexOf('\\');
		}
		if (i >= 0) {
			File folder = new File(path.substring(0, i + 1));
			folder.mkdirs();
		}
	}

	private FileTool() {

	}

	public static void clearFolder(String folder) {
		clearFolder(new File(folder));
	}

	public static void clearFolders(String... folderPath) {
		for (String folder : folderPath) {
			FileTool.clearFolder(folder);
		}
	}

	public static void ensureFolders(String... folderPath) {
		for (String folder : folderPath) {
			FileTool.ensurePathFolder(folder);
		}
	}

	public static void clearFolder(File folder) {
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					clearFolder(file);
				} else {
					file.delete();
				}
			}
		}

	}

	public static void ensureSubFolders(String folder, String... subFolders) {
		folder = ensureFolderName(folder);
		for(String subFolder : subFolders){
			ensurePathFolder(folder + ensureFolderName(subFolder));
		}
	}

	private static String ensureFolderName(String folderName) {
		if (!(folderName.endsWith("\\") || folderName.endsWith("/"))) {
			folderName += "/";
		}
		return folderName;
	}
}
