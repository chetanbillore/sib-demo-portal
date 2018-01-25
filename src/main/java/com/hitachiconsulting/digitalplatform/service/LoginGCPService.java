package com.hitachiconsulting.digitalplatform.service;

public interface LoginGCPService {
	
	public void googlAuthService();
	
	public String getGoogleAuthToken(String authCode);
}
