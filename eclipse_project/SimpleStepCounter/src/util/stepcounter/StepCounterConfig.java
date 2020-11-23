package util.stepcounter;

import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class StepCounterConfig {

	String blockCommentStart = null;
	String blockCommentEnd = null;
	String lineCommentStart = null;
	String lineCommentEnd = null;
	Character charLiteralDelim = null;
	Character escapeChar_charLiteral = null;
	Character[] stringLiteralDelim = null;
	Character escapeChar_stringLiteral = null;

	private static HashMap<String,StepCounterConfig> configMap = null;
	private static Object lock = new Object();

	public static StepCounterConfig getConfig(String ext) throws Exception{

		if( configMap == null ){
			synchronized(lock){
				load();
			}
		}

		return configMap.get(ext);

	}

	private static void load() throws Exception{

		configMap = new HashMap<String,StepCounterConfig>();

		ResourceBundle bundle = PropertyResourceBundle.getBundle("StepCounter");

		String typesStr = bundle.getString("supported_filetypes");
		String[] filetypes = typesStr.split(",");
		for(int i=0;i<filetypes.length;i++){

			String filetype = filetypes[i];
			String extentionsStr = bundle.getString(filetype + ".extentions");

			StepCounterConfig config = new StepCounterConfig();
			String tmp = null;

			tmp = bundle.getString(filetype + ".blockCommentStart");
			if( tmp.length() > 0 ){
				config.blockCommentStart = tmp;
			}
			tmp = bundle.getString(filetype + ".blockCommentEnd");
			if( tmp.length() > 0 ){
				config.blockCommentEnd = tmp;
			}
			tmp = bundle.getString(filetype + ".lineCommentStart");
			if( tmp.length() > 0 ){
				config.lineCommentStart = tmp;
			}
			tmp = bundle.getString(filetype + ".lineCommentEnd");
			if( tmp.length() > 0 ){
				config.lineCommentEnd = tmp;
			}
			tmp = bundle.getString(filetype + ".charLiteralDelim");
			if( tmp.length() > 0 ){
				if( tmp.length() >= 2 ){
					throw new Exception("charLiteralDelim is not character:" + tmp);
				}
				config.charLiteralDelim = new Character(tmp.charAt(0));
			}

			tmp = bundle.getString(filetype + ".escapeChar_charLiteral");
			if( tmp.length() > 0 ){
				if( tmp.length() >= 2 ){
					throw new Exception("escapeChar_charLiteral is not character:" + tmp);
				}
				config.escapeChar_charLiteral = new Character(tmp.charAt(0));
			}

			if( (config.charLiteralDelim != null && config.escapeChar_charLiteral == null)
				 || (config.charLiteralDelim == null && config.escapeChar_charLiteral != null) ){
				throw new Exception("charLiteralDelim/escapeChar_charLiteral cannot set one side only(both set or both not set).");
			}

			tmp = bundle.getString(filetype + ".stringLiteralDelim");
			if( tmp.length() > 0 ){
				String[] dat = tmp.split("\\|");
				config.stringLiteralDelim = new Character[dat.length];
				for(int j=0;j<dat.length;j++){
					String s = dat[j];
					if( s.length() >= 2 ){
						throw new Exception("stringLiteralDelim is not character:" + tmp);
					}
					config.stringLiteralDelim[j] = new Character(s.charAt(0));
				}
			}

			tmp = bundle.getString(filetype + ".escapeChar_stringLiteral");
			if( tmp.length() > 0 ){
				if( tmp.length() >= 2 ){
					throw new Exception("escapeChar_stringLiteral is not character:" + tmp);
				}
				config.escapeChar_stringLiteral = new Character(tmp.charAt(0));
			}

			if( (config.stringLiteralDelim != null && config.escapeChar_stringLiteral == null)
					 || (config.stringLiteralDelim == null && config.escapeChar_stringLiteral != null) ){
					throw new Exception("stringLiteralDelim/escapeChar_stringLiteral cannot set one side only(both set or both not set).");
			}

			String[] extentions = extentionsStr.split(",");
			for(int j=0;j<extentions.length;j++){
				String ext = extentions[j];
				if( configMap.containsKey(ext) ){
					throw new Exception("extention duplicate:" + ext);
				}
				configMap.put(ext,config);
			}

		}

	}

}
