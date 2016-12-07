package org.leo.apppay.apppay_sdk.utils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsClient {
	  protected static final String CONTENT_TYPE = "application/vnd.ehking-v1.0+json";
	  protected static final String CHARSET = "UTF-8";
	  static HostnameVerifier sslHostnameVerifier = createHostnameVerifier();
	  static SSLSocketFactory sslSocketFactory = createSSLSocketFactory();
	  
	  public static String post(String urlStr, String data) {
		    HttpURLConnection con = null;
		    StringBuilder sb = new StringBuilder();
		    try {
		      URL url = new URL(urlStr);
		      con = (HttpURLConnection)url.openConnection();
		      con.setDoOutput(true);
		      con.setDoInput(true);
		      con.setInstanceFollowRedirects(false);
		      con.setRequestMethod("POST");
		      con.setRequestProperty("Content-Type", "application/vnd.ehking-v1.0+json");
		      con.setRequestProperty("charset", "UTF-8");
		      con.setUseCaches(false);
		      con.setConnectTimeout(60000);
		      con.setReadTimeout(59000);

		      if ((con instanceof HttpsURLConnection)) {
		        HttpsURLConnection httpsCon = (HttpsURLConnection)con;
		        httpsCon.setHostnameVerifier(sslHostnameVerifier);
		        httpsCon.setSSLSocketFactory(sslSocketFactory);
		      }

		      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		      wr.write(data.getBytes("UTF-8"));
		      wr.flush();
		      wr.close();
		      int HttpResult = con.getResponseCode();
		      if (HttpResult == 200) {
		        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

		        String line = null;

		        while ((line = br.readLine()) != null) {
		          sb.append(line + "\n");
		        }
		        br.close();
		      } else {
		        System.out.println(con.getResponseMessage());
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    } finally {
		      if (con != null)
		        con.disconnect();
		    }
		    return sb.toString();
		  }

		  public static String post(String urlStr, String data, String language) {
		    System.out.println("data:[" + data + "]");
		    HttpURLConnection con = null;
		    StringBuilder sb = new StringBuilder();
		    try {
		      URL url = new URL(urlStr);
		      con = (HttpURLConnection)url.openConnection();
		      con.setDoOutput(true);
		      con.setDoInput(true);
		      con.setInstanceFollowRedirects(false);
		      con.setRequestMethod("POST");
		      con.setRequestProperty("Content-Type", "application/vnd.ehking-v1.0+json");
		      con.setRequestProperty("charset", "UTF-8");
		      System.out.println("language:[" + language + "]");
		      String strLanguage = language.replace("_", "-");
		      con.setRequestProperty("Accept-Language", strLanguage);
		      con.setUseCaches(false);
		      con.setConnectTimeout(60000);
		      con.setReadTimeout(59000);

		      if ((con instanceof HttpsURLConnection)) {
		        HttpsURLConnection httpsCon = (HttpsURLConnection)con;
		        httpsCon.setHostnameVerifier(sslHostnameVerifier);
		        httpsCon.setSSLSocketFactory(sslSocketFactory);
		      }

		      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		      wr.write(data.getBytes("UTF-8"));
		      wr.flush();
		      wr.close();
		      int HttpResult = con.getResponseCode();
		      if (HttpResult == 200) {
		        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

		        String line = null;

		        while ((line = br.readLine()) != null) {
		          sb.append(line + "\n");
		        }
		        br.close();
		      } else {
		        System.out.println(con.getResponseMessage());
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    } finally {
		      if (con != null)
		        con.disconnect();
		    }
		    return sb.toString();
		  }
	  
	  
	  private static HostnameVerifier createHostnameVerifier()
	  {
	    return new HostnameVerifier() {
	      public boolean verify(String urlHostName, SSLSession session) {
	        return (urlHostName != null) && (urlHostName.equals(session.getPeerHost()));
	      }
	    };
	  }

	  private static SSLSocketFactory createSSLSocketFactory()
	  {
	    SSLSocketFactory sslSocketFactory = null;
	    try {
	      SSLContext context = SSLContext.getInstance("TLS");
	      X509TrustManager trustManager = new X509TrustManager() {
	        public X509Certificate[] getAcceptedIssuers() {
	          return null;
	        }

	        public void checkClientTrusted(X509Certificate[] chain, String authType)
	          throws CertificateException
	        {
	        }

	        public void checkServerTrusted(X509Certificate[] chain, String authType)
	          throws CertificateException
	        {
	        }
	      };
	      context.init(null, new TrustManager[] { trustManager }, null);
	      sslSocketFactory = context.getSocketFactory();
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return sslSocketFactory;
	  }
}
