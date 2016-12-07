package org.leo.apppay.apppay_sdk;

import java.util.HashMap;
import java.util.Map;

import org.leo.apppay.apppay_sdk.utils.HttpClient;
import org.leo.apppay.apppay_sdk.utils.MD5;
import org.leo.apppay.apppay_sdk.utils.SignUtils;

/**
 * 
 * 退款查询
 *
 */
public class RefundQueryTest {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		String merchantNo = "898875475663019";
		String orderNo = "123456789012327";
		map.put("merchantNo", merchantNo);
		map.put("orderNo", orderNo);
		
		String a = SignUtils.payParamsToString(map);
		a = a+"93117a5dfe3046f787f0dd7fbe0f6822";
		System.out.println("sign加密前的字符串="+a);
		System.out.println("密钥=93117a5dfe3046f787f0dd7fbe0f6822");
		String sign = MD5.md5Str(a).toUpperCase();
		System.out.println("MD5加密后的结果="+sign);
		map.put("sign", sign);
		
		String url = "http://apppay.vitongpay.com/apppay/refundQuery";
		String response = HttpClient.post(url, map);
		System.out.println("response="+response);

	}

}
