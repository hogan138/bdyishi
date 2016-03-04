package com.fding.activity.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class UploadFile {

	private String uploadUrl;

	public UploadFile(String uploadUrl) {
		super();
		this.uploadUrl = uploadUrl;
	}

	/**
	 * 上传文件的method
	 * 
	 * @param data
	 *            二进制数组
	 * @param filename
	 *            保存的文件名
	 * @param parameters
	 *            键值对参数
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean defaultUploadMethod(byte[] data, String filename, Map parameters) throws IOException {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		StringBuffer actionUrl = new StringBuffer(uploadUrl);
		if (parameters != null) {
			for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				actionUrl.append(element.toString());
				actionUrl.append("=");
				actionUrl.append(URLEncoder.encode(parameters.get(element).toString(), "UTF-8"));
				actionUrl.append("&");
			}
			if (actionUrl.length() > 0) {
				actionUrl = actionUrl.deleteCharAt(actionUrl.length() - 1);
			}
		}
		// System.out.println(actionUrl.toString());
		HttpURLConnection con = null;
		URL url;
		try {
			url = new URL(actionUrl.toString());
			con = (HttpURLConnection) url.openConnection();

			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设定传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			con.setConnectTimeout(1200);
			con.setReadTimeout(1200);

			/* 设定DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + filename + "\";" + end);

			ds.write(data, 0, data.length);
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			ds.flush();

			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			/* 关闭DataOutputStream */
			ds.close();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			if (con != null)
				con.disconnect();
		}

	}

}