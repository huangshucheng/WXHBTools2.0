package org.leo.apppay.apppay_sdk;

import java.util.HashMap;
import java.util.Map;

import org.leo.apppay.apppay_sdk.utils.HttpClient;
import org.leo.apppay.apppay_sdk.utils.MD5;
import org.leo.apppay.apppay_sdk.utils.SignUtils;

/**
 * 
 * 下单接口
 * 请求参数url：http://apppay.vitongpay.com/apppay/order
 *
 */
public class OrderTest {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		String merchantNo = "898875475663024";
		String orderAmount = "1";
		String orderNo = "123456789012374";
		String notifyUrl = "http://wappay.vitongpay.com/apppay/bjbocNotify";
		String callbackUrl = "http://wappay.vitongpay.com/apppay/callback";
		String productName = "productName";
		String productDesc = "productDesc";
		String remark = "remark";
		map.put("merchantNo", merchantNo);
		map.put("orderAmount", orderAmount);
		map.put("orderNo", orderNo);
		map.put("notifyUrl", notifyUrl);
		map.put("callbackUrl", callbackUrl);
		map.put("productName", productName);
		map.put("productDesc", productDesc);
		map.put("remark", remark);
		map.put("payType", "3");
		
		String a = SignUtils.payParamsToString(map);
		a = a+"9d101c97133837e13dde2d32a5054abb";
		System.out.println("sign加密前的字符串="+a);
		System.out.println("密钥=9d101c97133837e13dde2d32a5054abb");
		String sign = MD5.md5Str(a).toUpperCase();
		System.out.println("MD5加密后的结果="+sign);
		map.put("sign", sign);
		
		String url = "http://wappay.vitongpay.com/apppay/order";
		String response = HttpClient.post(url, map);			//响应参数，格式为json
		System.out.println("response="+response);
	}

}
