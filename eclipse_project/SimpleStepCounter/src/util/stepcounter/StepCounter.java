package util.stepcounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import util.io.FileUtil;

public class StepCounter {

	public static void main(String[] args){

		PrintStream ps = System.out;

		try{

			if( args.length < 1 ){
				usage();
				return;
			}

			String rootdir = args[0];

			if( args.length >= 2 ){
				String outfilepath = args[1];
				ps = new PrintStream(outfilepath);
			}

			execute(new File(rootdir),ps);

			ps.flush();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( ps != null && ps.equals(System.out) == false ){
					ps.close();
				}
			}catch(Exception e){}
		}

	}

	private static void usage(){
		System.out.println("usage:StepCounter [path_rootdir_of_getting_step] ([output_file_path])");
	}

	private static void execute(File f,PrintStream ps){

		if( f.isDirectory() ){
			File[] children = f.listFiles();
			for(File child:children){
				execute(child,ps);
			}
		}else{
			try{
				int linecnt = getLineCount(f);
				int execStep = getExecStep(f);
				ps.println(f.getAbsolutePath() + "\t" + FileUtil.getExtention(f) + "\t" + linecnt + "\t" + execStep);
			}catch(Exception e){
				e.printStackTrace();
				ps.println(f.getAbsolutePath() + "\t" + FileUtil.getExtention(f) + "\t" + "ERROR");
			}
		}

	}

	public static int getLineCount(File f) throws Exception{

		FileReader fr = null;
		BufferedReader br = null;

		try{

			fr = new FileReader(f);
			br = new BufferedReader(fr);

			int linecnt = 0;

			String buf = null;
			while( (buf=br.readLine()) != null ){
				linecnt++;
			}

			return linecnt;

		}catch(Exception e){
			throw e;
		}finally{
			try{
				br.close();
			}catch(Exception e){}
			try{
				fr.close();
			}catch(Exception e){}
		}

	}

	public static int getExecStep(File f) throws Exception{

		FileReader fr = null;
		BufferedReader br = null;

		try{

			String fname = f.getName();
			int idx_ext = fname.lastIndexOf(".");
			String ext = fname.substring(idx_ext+1);

			StepCounterConfig config = StepCounterConfig.getConfig(ext);

			//
			// if config for file extention not found,return -1(treat as cannot get execStep)
			//
			if( config == null ){
				return -1;
			}

			fr = new FileReader(f);
			br = new BufferedReader(fr);

			//
			// put source file countent into StringBuilder
			//
			StringBuilder sb_src = new StringBuilder();
			String buf = null;
			while( (buf=br.readLine()) != null ){
				sb_src.append(buf).append("\n");
			}
			String srcStr = sb_src.toString();

			//
			// get parse info from config
			//
			boolean insideBlickComment = false;
			String blockCommentStartStr = config.blockCommentStart;
			String blockCommentEndStr = config.blockCommentEnd;

			boolean insideLineComment = false;
			String lineCommentStartStr = config.lineCommentStart;
			String lineCommentEndStr = config.lineCommentEnd;

			boolean insideCharLiteral = false;
			Character charLiteralDelim = config.charLiteralDelim;
			Character escapeChar_charLiteral = config.escapeChar_charLiteral;

			boolean insideStringLiteral = false;
			Character[] stringLiteralDelim = config.stringLiteralDelim;
			Character escapeChar_stringLiteral = config.escapeChar_stringLiteral;

			//
			// remove block comments & line comments
			//
			StringBuilder commentDelStr = new StringBuilder(1024*1024);
			Character current_stringLiteralDelim = null;

			for(int i=0;i<srcStr.length();i++){

				char ch = srcStr.charAt(i);

				//
				// if inside any comment/literal:
				//  check comment/literal are finished
				//
				if( insideBlickComment ){

					if( checkMatches(srcStr,i,blockCommentEndStr) ){
						insideBlickComment = false;
						i += blockCommentEndStr.length() - 1;
						continue;
					}

				}else if( insideLineComment ){

					if( checkMatches(srcStr,i,lineCommentEndStr) ){
						insideLineComment = false;
						i += lineCommentEndStr.length() - 1;
						commentDelStr.append(lineCommentEndStr);
						continue;
					}

				//
				// if inside string/character literal,append into buffer
				//
				}else if( insideCharLiteral ){

					if( checkMatchesForLiteralEnd(srcStr,i,charLiteralDelim,escapeChar_charLiteral) ){
						insideCharLiteral = false;
					}
					commentDelStr.append(ch);

				}else if( insideStringLiteral ){

					if( checkMatchesForLiteralEnd(srcStr,i,current_stringLiteralDelim,escapeChar_stringLiteral) ){
						current_stringLiteralDelim = null;
						insideStringLiteral = false;
					}
					commentDelStr.append(ch);

				//
				// if not inside any comment/literal:
				//
				}else{

					//
					// check any comment/literal are started
					//
					if( checkMatches(srcStr,i,blockCommentStartStr) ){
						insideBlickComment = true;
						i += blockCommentStartStr.length() - 1;
						continue;
					}

					if( checkMatches(srcStr,i,lineCommentStartStr) ){
						insideLineComment = true;
						i += lineCommentStartStr.length() - 1;
						continue;
					}

					if( checkMatches(srcStr,i,charLiteralDelim) ){
						insideCharLiteral = true;
						commentDelStr.append(ch);
						continue;
					}

					for(Character _delim:stringLiteralDelim){
						if( checkMatches(srcStr,i,_delim) ){
							insideStringLiteral = true;
							current_stringLiteralDelim = _delim;
							commentDelStr.append(ch);
							continue;
						}
					}

					//
					// if any comment/literal are not started:
					//  append character into buffer
					//
					commentDelStr.append(ch);

				}

			}

			//
			// remove empty row & get exec step
			//  * No problem simply spliting by newline('\n'),
			//    because newline inside string/character literal is escaped ,in almost languages.
			//
			int rowcnt = 0;
			String[] rows = commentDelStr.toString().split("\n");
			for(int i=0;i<rows.length;i++){

				String tmp = rows[i];

				if( tmp.replaceAll("\t"," ").trim().length() > 0 ){
					rowcnt++;
					//System.out.println(rows[i]);
				}

			}

			return rowcnt;

		}catch(Exception e){
			throw e;
		}finally{
			try{
				br.close();
			}catch(Exception e){}
			try{
				fr.close();
			}catch(Exception e){}
		}

	}

	private static boolean checkMatches(String srcStr,int currentPos,String needle){

		boolean ret = true;

		if( needle == null ){
			return false;
		}

		if( (currentPos + needle.length()) >= srcStr.length() ){
			ret = false;
		}else{

			for(int i=0;i<needle.length();i++){
				if( srcStr.charAt(currentPos+i) != needle.charAt(i) ){
					ret = false;
					break;
				}
			}

		}

		return ret;

	}

	private static boolean checkMatches(String srcStr,int currentPos,Character needle){

		boolean ret = true;

		if( needle == null ){
			return false;
		}

		if( (currentPos + 1) >= srcStr.length() ){
			ret = false;
		}else{
			if( srcStr.charAt(currentPos) != needle.charValue() ){
				ret = false;
			}
		}

		return ret;

	}

	private static boolean checkMatchesForLiteralEnd(String srcStr,int currentPos,Character enclosure,Character escapeChar){

		boolean ret = true;

		if( enclosure == null || escapeChar == null ){
			return false;
		}

		//rest char count < enclosure length:not literal end
		if( srcStr.length()-1  - currentPos < 1  ){
			ret = false;

		//rest char count >= enclosure length
		}else{

			//char not same as enclosure:not literal end
			if( srcStr.charAt(currentPos) != enclosure.charValue() ){
				ret = false;

			//char is same as enclosure:
			}else{

				//current position >= 2
				if( currentPos >= 2 ){

					//character before current is escape char:
					if( srcStr.charAt(currentPos - 1) == escapeChar.charValue() ){

						//character before escape char is also escape char:literal end
						if( srcStr.charAt(currentPos - 2) == escapeChar.charValue() ){
							ret = true;

						//character before escape char is not escape char:not literal end
						//(only enclosure char is escaped)
						}else{
							ret = false;
						}

					//character before current is not escape char:literal end
					}else{
						ret = true;
					}

				//current position == 1:literal end
				//(because this method is called after literal start check,so position 1 is literal end)
				}else if( currentPos == 1 ){
					ret = true;

				//current position == 0:not literal end
				//(because this method is called after literal start check,so position 0 is not literal end)
				}else{
					ret = false;
				}
				
			}

		}

		return ret;

	}

}
