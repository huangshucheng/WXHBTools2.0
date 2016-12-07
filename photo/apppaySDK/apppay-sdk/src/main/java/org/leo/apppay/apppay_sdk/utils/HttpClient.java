package org.leo.apppay.apppay_sdk.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpClient {

	public static String get(String url, String urlWithParams) {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		// HttpGet httpget = new HttpGet("http://www.baidu.com/");
		HttpGet httpget = new HttpGet(url + "?" + urlWithParams);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(50).setConnectTimeout(50)
				.setSocketTimeout(50).build();
		httpget.setConfig(requestConfig);

		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("StatusCode -> "
				+ response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		String jsonStr = null;
		try {
			jsonStr = EntityUtils.toString(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}// , "utf-8");
		System.out.println(jsonStr);

		httpget.releaseConnection();
		return jsonStr;
		// CloseableHttpResponse response = null;
		// CloseableHttpClient client = null;
		// String res = null;
		// try {
		// HttpPost httpPost = new HttpPost(url);
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// for (Map.Entry entry : map.entrySet()) {
		// params.add(new BasicNameValuePair((String) entry.getKey(),
		// (String) entry.getValue()));
		// }
		// httpPost.setEntity(new UrlEncodedFormEntity(params));
		// client = HttpClients.createDefault();
		// response = client.execute(httpPost);
		// int statusCode = response.getStatusLine().getStatusCode();
		// if (statusCode == HttpStatus.SC_OK) {
		// if (response != null && response.getEntity() != null) {
		// //读取字符串
		// res=EntityUtils.toString(response.getEntity());
		// return res;
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return null;
	}

	public static String post(String url, Map<String, String> map) {
		CloseableHttpResponse response = null;
		CloseableHttpClient client = null;
		String res = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Map.Entry entry : map.entrySet()) {
				params.add(new BasicNameValuePair((String) entry.getKey(),
						(String) entry.getValue()));
			}
			 UrlEncodedFormEntity param = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			 httpPost.setEntity(param);
			client = HttpClients.createDefault();
			response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				if (response != null && response.getEntity() != null) {
					// 读取字符串
					res = EntityUtils.toString(response.getEntity());
					return res;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String post(String url, String xml) {
		CloseableHttpResponse response = null;
		CloseableHttpClient client = null;
		String res = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			StringEntity entityParams = new StringEntity(xml, "utf-8");
			httpPost.setEntity(entityParams);
			client = HttpClients.createDefault();
			response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				if (response != null && response.getEntity() != null) {
					// 读取字符串
					res = EntityUtils.toString(response.getEntity());
					return res;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
