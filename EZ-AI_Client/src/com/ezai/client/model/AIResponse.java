package com.ezai.client.model;

import java.util.Map;

public class AIResponse {
	
	//	This is the status of the AI response, error, ok or other
	private String status;
	
	//	This is the returned speech reply of the AI server
	private String response;
	
	//	This is true if you are in a staged command
	private boolean isStaged;
	
	//	This is the query which was resolved by the AI server
	private String resolvedQuery;
	
	//	This is the doAction of the response.  If there is further action on the client, this will be populated.
	//	This is mostly used for robotics
	private String doAction;
	
	//	These are the parameters of the doAction (if there is any further action to be made)
	private Map<String, String> doParams;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getResolvedQuery() {
		return resolvedQuery;
	}
	public void setResolvedQuery(String resolvedQuery) {
		this.resolvedQuery = resolvedQuery;
	}
	public String getDoAction() {
		return doAction;
	}
	public void setDoAction(String doAction) {
		this.doAction = doAction;
	}
	public Map<String, String> getDoParams() {
		return doParams;
	}
	public void setDoParams(Map<String, String> doParams) {
		this.doParams = doParams;
	}
	public boolean getIsStaged() {
		return isStaged;
	}
	public void setIsStaged(boolean isStaged) {
		this.isStaged = isStaged;
	}
	
	public String toString() {
		String ret = "";
		ret += "status: " + status + "\n"; 
		ret += "response: " + response + "\n"; 
		ret += "isStaged: " + isStaged + "\n"; 
		ret += "resolvedQuery: " + resolvedQuery + "\n"; 
		ret += "doAction: " + doAction; 
		return ret;
	}
}
