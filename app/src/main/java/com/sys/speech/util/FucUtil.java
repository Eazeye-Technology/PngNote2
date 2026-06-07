package com.sys.speech.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class FucUtil {
	public static String readFile(Context mContext,String file,String code)
	{
		int len = 0;
		byte []buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);			
			len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			result = new String(buf,code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<byte[]> splitBuffer(byte[] buffer,int length,int spsize)
	{
		ArrayList<byte[]> array = new ArrayList<byte[]>();
		if(spsize <= 0 || length <= 0 || buffer == null || buffer.length < length)
			return array;
		int size = 0;
		while(size < length)
		{
			int left = length - size;
			if(spsize < left)
			{
				byte[] sdata = new byte[spsize];
				System.arraycopy(buffer,size,sdata,0,spsize);
				array.add(sdata);
				size += spsize;
			}else
			{
				byte[] sdata = new byte[left];
				System.arraycopy(buffer,size,sdata,0,left);
				array.add(sdata);
				size += left;
			}
		}
		return array;
	}

	public static String checkLocalResource(){
		String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ASR);
		try {
			JSONObject result = new JSONObject(resource);
			int ret = result.getInt(SpeechUtility.TAG_RESOURCE_RET);
			switch (ret) {
			case ErrorCode.SUCCESS:
				JSONArray asrArray = result.getJSONObject("result").optJSONArray("asr");
				if (asrArray != null) {
					int i = 0;
					for (; i < asrArray.length(); i++) {
						if("iat".equals(asrArray.getJSONObject(i).get(SpeechConstant.DOMAIN))){
							break;
						}
					}
					if (i >= asrArray.length()) {
						SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);	
						return "no listen resource";
					}
				}else {
					SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
					return "no listen resource";
				}
				break;
			case ErrorCode.ERROR_VERSION_LOWER:
				return "version too low";
			case ErrorCode.ERROR_INVALID_RESULT:
				SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
				return "get result error";
			case ErrorCode.ERROR_SYSTEM_PREINSTALL:
			default:
				break;
			}
		} catch (Exception e) {
			SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
			return "get result error";
		}
		return "";
	}

	public static byte[] readAudioFile(Context context, String filename, boolean isFromAssets) {
		try {
			InputStream ins;
			if (isFromAssets) {
				ins = context.getAssets().open(filename);
			} else {
				ins = new FileInputStream(filename);
			}
			byte[] data = new byte[ins.available()];
			
			ins.read(data);
			ins.close();
			
			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}	
	
}
