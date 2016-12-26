package com.junyou.hbks.apppayutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.junyou.hbks.config.Constants;

import org.json.JSONObject;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.conn.util.InetAddressUtils;

public class ComFunction {

	 public static String genNonceStr(){
		 try {
	            Random random = new Random();
	            String rStr = MD5.getMessageDigest(String.valueOf(
	                    random.nextInt(10000)).getBytes("utf-8"));
	            return rStr;
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	            return null;
	        }
	 }
	 
	 public static String genPackageSign(List<NameValuePair> params){
		 try {
	            StringBuilder sb = new StringBuilder();
	            for (int i = 0; i < params.size(); i++) {
	                sb.append(params.get(i).getName());
	                sb.append('=');
	                sb.append( params.get(i).getValue());
	                sb.append('&');
	            }
	            sb.append("key=");
	            sb.append(Constants.PARTNER_KEY);
//	            Log.i("TBU_DEBUG", "生成字符串: "+sb.toString());
	            String packageSign = MD5.getMessageDigest(
	                    sb.toString().getBytes("utf-8")).toUpperCase();
	            return packageSign;
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	            return null;
	        }
	 }
	 
	 public static long genTimeStamp() {
			return System.currentTimeMillis() / 1000;
		}
	 
		public static String getIPAddress() {
			try {
				Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
				while (en.hasMoreElements()) {
					NetworkInterface nif = en.nextElement();
					Enumeration<InetAddress> inet = nif.getInetAddresses();
					while (inet.hasMoreElements()) {
						InetAddress ip = inet.nextElement();
						if (!ip.isLoopbackAddress()
								&& InetAddressUtils.isIPv4Address(ip
										.getHostAddress())) {
							return ip.getHostAddress();
						}
					}
				}
			} catch (SocketException e) {
				Log.e("TBU_DEGUB", "获取ip地址失败", e);
			}
			return null;
		}
		
		   //�?测网络状�?
	    public static boolean networkInfo(Context contxt){
	        ConnectivityManager connMgr = (ConnectivityManager)contxt.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	    
	    public static String genOutTradNo()
	    {
	        Random random = new Random();
	        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	    } 
	    
	    public static boolean isWechatAvilible(Context context)
	    {
	        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
	        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取�?有已安装程序的包信息
	        if (pinfo != null) {
	            for (int i = 0; i < pinfo.size(); i++)
	            {
	                String pn = pinfo.get(i).packageName;
	                if (pn.equals("com.tencent.mm"))
	                {
	                    return true;
	                }
	            }
	        }
	        return false;
	    }

	public static Map<String, String> jsonToObject(String jsonStr) throws Exception {
		JSONObject jsonObj = new JSONObject(jsonStr);
		Iterator<String> nameItr = jsonObj.keys();
		String name;
		Map<String, String> outMap = new HashMap<String, String>();
		while (nameItr.hasNext()) {
			name = nameItr.next();
			outMap.put(name, jsonObj.getString(name));
		}
		return outMap;
	}

//识别设备信息
public static boolean checkPermission(Context context, String permission) {
    boolean result = false;
    if (Build.VERSION.SDK_INT >= 23) {
        try {
            Class<?> clazz = Class.forName("android.content.Context");
            Method method = clazz.getMethod("checkSelfPermission", String.class);
            int rest = (Integer) method.invoke(context, permission);
            if (rest == PackageManager.PERMISSION_GRANTED) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
    } else {
        PackageManager pm = context.getPackageManager();
        if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            result = true;
        }
    }
    return result;
}

public static String getDeviceInfo(Context context) {
    try {
        org.json.JSONObject json = new org.json.JSONObject();
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = null;
        if (checkPermission(context, permission.READ_PHONE_STATE)) {
            device_id = tm.getDeviceId();
        }
        String mac = null;
        FileReader fstream = null;
        try {
            fstream = new FileReader("/sys/class/net/wlan0/address");
        } catch (FileNotFoundException e) {
            fstream = new FileReader("/sys/class/net/eth0/address");
        }
        BufferedReader in = null;
        if (fstream != null) {
            try {
                in = new BufferedReader(fstream, 1024);
                mac = in.readLine();
            } catch (IOException e) {
            } finally {
                if (fstream != null) {
                    try {
                        fstream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        json.put("mac", mac);
        if (TextUtils.isEmpty(device_id)) {
            device_id = mac;
        }
        if (TextUtils.isEmpty(device_id)) {
            device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        }
        json.put("device_id", device_id);
        return json.toString();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

	//判断文件是否存在
	public static boolean fileIsExists(Context context)
	{
		String databasename = "hbks.db";
		boolean isSdcardEnable = false;
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			//SDCard是否插入
			isSdcardEnable = true;
		}
		String dbPath = null;
		if(isSdcardEnable){
			dbPath =Environment.getExternalStorageDirectory().getPath() +"/database/";
		}else{
			dbPath =context.getFilesDir().getPath() + "/database/";
		}
		databasename = dbPath + databasename;
		try {
			File f=new File(databasename);
			if(f.exists()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
