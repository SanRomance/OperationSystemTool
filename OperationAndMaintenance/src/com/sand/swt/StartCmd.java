package com.sand.swt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
/*
 *获取cmd执行的结果，执行输出中文会产生乱码；
 */
public class StartCmd {
	private static Logger log=Logger.getLogger("StartCmd");
	
	public List<String[]> runCmd(String cmd) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		try {
			Process resultCmd = startCmdNoResult(cmd);
			InputStream errorStream = resultCmd.getErrorStream();
			InputStream inputStream = resultCmd.getInputStream();
			//TODO对结果信息进行处理
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String s="";
			String resultStr="";
			while ((s=bufferedReader.readLine())!=null) {
				//对结果进行逐行处理
				String[] rst = s.split(" ");
				if(rst.length>1) {
					result.add(rst);
				}
				if(resultStr.equals("")) {
					resultStr+=s;
				}else {
					resultStr+="\n"+s;
				}
			}
			log.info("执行数据为：----------\n"+resultStr);
			//TODO对错误信息进行处理
			String es="";
			String errorResult="";
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
			while ((es=errorReader.readLine())!=null) {
				if(errorResult.equals("")) {
					errorResult+=es;
				}else {
					errorResult+="\n"+es;
				}
			}
			log.info("错误信息为：---------\n"+errorResult);
			bufferedReader.close();
			errorReader.close();
			inputStream.close();
			errorStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private Process startCmdNoResult(String cmd) {
		try {
			return Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String runJar(String cmd) {
		try {
		Process resultCmd = startCmdNoResult(cmd);
		InputStream errorStream = resultCmd.getErrorStream();
		InputStream inputStream = resultCmd.getInputStream();
		inputStream.close();
		String es="";
		String errorResult="";
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
		while ((es=errorReader.readLine())!=null) {
			if(errorResult.equals("")) {
				errorResult+=es;
			}else {
				errorResult+="\n"+es;
			}
		}
		log.info("错误信息为：---------\n"+errorResult);
		errorReader.close();
		errorStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return cmd;
	}
}
