package com.forsuntech.prisonmdm.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;


public class LogWriterUtils {

	// 默认的日志最大行数
	private int MAX_LOG_LINE_COUNT = 10000;
	// 文件输出流
	private PrintWriter writer;
	// 日志文件名
	private String logFileName;
	// 日志行数
	private int nLine;
	String appHome ;
	String DebugPath;
	public LogWriterUtils(String filename) {

		{
			appHome = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MDM-NFC";

			File homeDir = new File(appHome);
			if (!homeDir.exists()) {
				homeDir.mkdirs();
			}
			DebugPath=appHome+"/deDug";
			File DebugDir = new File(DebugPath);
			if (!DebugDir.exists()) {
				DebugDir.mkdirs();
			}
		}

		logFileName =DebugPath + "/" + filename;
		nLine = getFileLineCount();

		Boolean bDel = false;
		if (nLine >= MAX_LOG_LINE_COUNT)
			bDel = true;
		
		
		File logFile = new File(DebugPath, filename);
		if(!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		
		if (bDel)
			logFile.delete();

		try {
			writer = new PrintWriter(new FileWriter(logFile, true), true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public int getFileLineCount() {
		int cnt = 0;
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(logFileName));
			while ((reader.readLine()) != null) {
			}
			cnt = reader.getLineNumber();
		} catch (Exception ex) {
			cnt = 0;
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
			}
		}
		return cnt;
	}

	public synchronized void log(String logMsg) {
		if (nLine >= MAX_LOG_LINE_COUNT) {
			close();
			File logFile = new File(this.logFileName);
			logFile.delete();
			try {
				writer = new PrintWriter(new FileWriter(logFile, true), true);
			} catch (IOException ex) {
			}
			nLine = 0;
		}
		if (writer != null) {
			writer.println(new java.util.Date() + ": " + logMsg);
		}

		nLine++;
	}

	// 关闭LogWriter
	public void close() {
		if (writer != null) {
			writer.close();
		}
	}

	public static void main(String[] args) {
		LogWriterUtils logger = new LogWriterUtils("Forsun.log");
		logger.log("First log!");
		logger.log("第二个日志信息");
		logger.log("Third log");
		logger.log("第四个日志信息");
		logger.close();
	}
}
