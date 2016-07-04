package com.ezai.client.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;

import com.ezai.client.gui.TestGui;
import com.ezai.client.model.AIResponse;
import com.ezai.client.service.IWebService;
import com.ezai.client.util.IPAddressValidator;
import com.ezai.client.util.MultipartUtility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebService implements IWebService {
	private String serverIp = null;
	private String deviceId = null;
	
	// Singleton instance and methods
	private static IWebService instance;

	private WebService() {
	};

	public static IWebService getInstance() {
		if (instance == null) {
			instance = new WebService();
		}
		return instance;
	}

	@Override
	public String getServerIP(String ezaiHealthCheckUrl) throws Exception {
		if (serverIp != null && !serverIp.equals("")) {
			return serverIp;
		}
		try {
			String ip = "";

			IPAddressValidator ipValidator = new IPAddressValidator();
			// Iterate over network devices to find ip address connected to lan
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					String checkIp = addr.getHostAddress();
					// We found an IPv4!
					if (ipValidator.validate(checkIp)) {
						ip = checkIp;
					}
				}
			}

			if (ip == null || ip.equals("")) {
				throw new Exception("Could not get ip address of client device.");
			}

			String ipBase = "";
			String[] ipSplit = ip.split("\\.");
			for (int i = 0; i < 3; i++) {
				ipBase += ipSplit[i] + ".";
			}

			for (int i = 0; i < 256; i++) {
				String tryUrl = ezaiHealthCheckUrl.replace("$IP_ADDRESS", ipBase + i);

				URL obj = null;
				obj = new URL(tryUrl);
				HttpURLConnection con = null;
				con = (HttpURLConnection) obj.openConnection();
				con.setConnectTimeout(500);
				con.setRequestMethod("GET");
				System.out.println("TRYING " + tryUrl);
				TestGui.getInstance().addText("TRYING " + tryUrl);

				try {
					int responseCode = con.getResponseCode();
					// Check response code
					BufferedReader in = null;
					in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					// print result
					System.out.println(response.toString());
					TestGui.getInstance().addText(response.toString());

					serverIp = ipBase + i;
					return serverIp;
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	@Override
	public String getDeviceID(String ezaiGetDeviceIdUrl) throws Exception {
		if (deviceId != null && !deviceId.equals("")) {
			return deviceId;
		}
		URL obj = new URL(ezaiGetDeviceIdUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		con.setConnectTimeout(500);
		
		con.connect();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		JsonElement jelement = new JsonParser().parse(response.toString());
		JsonObject jobject = jelement.getAsJsonObject();
		return jobject.get("deviceId").toString().replaceAll("\"", "");
	}

	@SuppressWarnings("deprecation")
	@Override
	public AIResponse sendText(String ezaiSendTextUrl, String text, String deviceId, boolean isStaged)
			throws Exception {
		ezaiSendTextUrl = ezaiSendTextUrl.replace("$QUERY", URLEncoder.encode(text)).replace("$DEVICE_ID", deviceId)
				.replace("$IS_STAGED", isStaged + "");
		URL obj = new URL(ezaiSendTextUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		JsonElement jelement = new JsonParser().parse(response.toString());
		JsonObject jobject = jelement.getAsJsonObject();
		JsonObject reply = jobject.get("reply").getAsJsonObject();
		return parseAiReply(reply);
	}

	@Override
	public AIResponse sendAudio(String ezaiSendAudioUrl, String filename, String deviceId, boolean isStaged)
			throws Exception {
		String charset = "UTF-8";
		MultipartUtility multipart = new MultipartUtility(
				ezaiSendAudioUrl.replace("$DEVICE_ID", deviceId).replace("$IS_STAGED", isStaged + ""), charset);
		multipart.addFilePart("file", new File(filename));

		HttpURLConnection response = multipart.sendRequest();

		String rply = IOUtils.toString(response.getInputStream(), charset);
		response.disconnect();
		System.out.println(rply);
		TestGui.getInstance().addText(rply);

		// Process reply
		JsonElement jelement = new JsonParser().parse(rply);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonObject reply = jobject.getAsJsonObject("reply");
		return parseAiReply(reply);
	}

	@Override
	public AIResponse sendImage(String ezaiSendImageUrl, String filename, String deviceId) throws Exception {
		MultipartUtility multipart = new MultipartUtility(ezaiSendImageUrl.replace("$DEVICE_ID", deviceId), "UTF-8");
		multipart.addFilePart("file", new File(filename));
		HttpURLConnection httpResponse = multipart.sendRequest();
		String rply = IOUtils.toString(httpResponse.getInputStream(), "UTF-8");
		httpResponse.disconnect();
		System.out.println(rply);
		TestGui.getInstance().addText(rply);

		// Check for error (empty reply)
		JsonElement jelement = new JsonParser().parse(rply);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonObject reply = jobject.getAsJsonObject("reply");
		return parseAiReply(reply);
	}

	@Override
	public void getAudio(String ezaiGetAudioUrl, String text, String filename) throws Exception {
		// Get the audio from the server
		@SuppressWarnings("deprecation")
		URL obj = new URL(ezaiGetAudioUrl.replace("$TEXT", URLEncoder.encode(text)));
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		InputStream in = con.getInputStream();
		// Write the file sent by the server
		OutputStream outputStream = new FileOutputStream(new File(filename));
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = in.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		outputStream.flush();
		outputStream.close();
	}

	private AIResponse parseAiReply(JsonObject reply) {
		String resp = reply.get("response") != null ? reply.get("response").getAsString() : "";
		boolean isStgd = reply.get("isStaged") != null ? reply.get("isStaged").getAsBoolean() : false;
		String stus = reply.get("status") != null ? reply.get("status").getAsString() : "";
		String resolvedQuery = reply.get("resolvedQuery") != null ? reply.get("resolvedQuery").getAsString() : "";
		String doAction = reply.get("doAction") != null ? reply.get("doAction").getAsString() : "";
		String doParamsStr = reply.get("doParams") != null ? reply.get("doParams").toString() : "";
		Map<String, String> doParams = new HashMap<String, String>();
		if (!doParamsStr.equals("")) {
			// Here, we want to parse the JSON object to make a doParams
			// hashmap.
		}

		AIResponse aiResponse = new AIResponse();
		aiResponse.setResponse(resp);
		aiResponse.setStatus(stus);
		aiResponse.setIsStaged(isStgd);
		aiResponse.setResolvedQuery(resolvedQuery);
		aiResponse.setDoAction(doAction);
		aiResponse.setDoParams(doParams);
		return aiResponse;
	}
}
