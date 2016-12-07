package com.junyou.hbks.apppayutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cz.msebera.android.httpclient.NameValuePair;

public class XmlUtil {

	/**
	 * 生成 XML
	 */
	public static String toXml(List<NameValuePair> packageParams) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < packageParams.size(); i++) {
			sb.append("<" + packageParams.get(i).getName() + ">");
			sb.append(packageParams.get(i).getValue());
			sb.append("</" + packageParams.get(i).getName() + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}
	
//	public static String toXml(List<org.apache.http.NameValuePair> packageParams) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<xml>");
//		for (int i = 0; i < packageParams.size(); i++) {
//			sb.append("<" + packageParams.get(i).getName() + ">");
//			sb.append("<![CDATA[" + packageParams.get(i).getValue() + "]]>");
//			sb.append("</" + packageParams.get(i).getName() + ">");
//		}
//		sb.append("</xml>");
//		return sb.toString();
//	}

	/**
	 * 解析xml,返回第一级元素键值对。如果第�?级元素有子节点，则此节点的�?�是子节点的xml数据�?
	 */
	public static Map doXMLParse(String strxml) throws Exception {
		if (null == strxml || "".equals(strxml)) {
			return null;
		}
		Map m = new HashMap();
		InputStream in = new ByteArrayInputStream(strxml.getBytes());
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}
			m.put(k, v);
		}
		// 关闭�?
		in.close();
		return m;
	}

	/**
	 * 获取子结点的xml
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		return sb.toString();
	}
	
}
