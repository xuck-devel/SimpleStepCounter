package util.io;

import java.io.File;

public class FileUtil {

	//
	public static String getExtention(File f){

		String fname = f.getName();

		int idx = fname.lastIndexOf(".");
		if( idx != -1 ){
			return fname.substring(idx+1);
		}else{
			return "";
		}

	}

}
