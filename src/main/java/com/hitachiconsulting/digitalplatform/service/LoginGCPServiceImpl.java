package com.hitachiconsulting.digitalplatform.service;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginGCPServiceImpl implements LoginGCPService {
	
	public static final String REQAUTHHEADER = "Authorization";
	
	@Value("${employeeBaseURL}")
    private String empBaseUrl;
	
	@Value("${employeeListURL}")
    private String employeeListURL;

	@CrossOrigin
	@Override
	public void googlAuthService() {
		// TODO Auto-generated method stub
		String googleAuthURL = "https://accounts.google.com/o/oauth2/v2/auth";
		String modifiedAuthURL = googleAuthURL+"?"+"scope=";
		String completeAuthURL = "https://accounts.google.com/o/oauth2/v2/auth"
				+ "?scope=https://www.googleapis.com/auth/drive.metadata.readonly"
				+ "&access_type=offline"
				+ "&include_granted_scopes=true"
				+ "&state=state_parameter_passthrough_value"
				+ "&redirect_uri=http://localhost:9022/"
				+ "&response_type=code"
				+ "&client_id=155249140930-743tookoo2legm3goqtkggfdl0cl5ptq.apps.googleusercontent.com";
		RestTemplate restTemplate = null;
		try {
			restTemplate = restTemplate();
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//new RestTemplate(); 
//		https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/drive.metadata.readonly&access_type=offline&include_granted_scopes=true&state=state_parameter_passthrough_value&redirect_uri=http://localhost:9022/&response_type=code&client_id=155249140930-qdpfma1aktt27rp6jctr1pfjnqubrmm4.apps.googleusercontent.com
    	HttpHeaders httpReqHeaders = new HttpHeaders();
//		httpReqHeaders.add(REQAUTHHEADER, authorizationHeader);
		/*httpReqHeaders.add("Content-Type", "application/json");
		httpReqHeaders.add("Accept", "application/json");*/
		HttpEntity<Long> httpEntity = new HttpEntity<Long>(null, httpReqHeaders);

    	ResponseEntity<String> responseEntity =  
				restTemplate.exchange(completeAuthURL,
				HttpMethod.GET, httpEntity, String.class);
    	
//    	GoogleClientSecrets.load(jsonFactory, reader);
    		   	
    	String employeeName = null;
    	try {
			JSONObject json = new JSONObject(responseEntity.getBody());
			JSONObject empVOjson = json.get("employeeVO") instanceof JSONObject ?  (JSONObject) json.get("employeeVO") :  null ;
//			employeeName = empVOjson != null ? (String) empVOjson.get("empName") : empId;
		} catch (JSONException e) {
			e.printStackTrace();
//			return empId;
		}
		
	}

	@Override
	public String getGoogleAuthToken(String authCode) {

		RestTemplate restTemplate = null;
		try {
			restTemplate = restTemplate();
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//new RestTemplate();    	
    	
    	HttpHeaders httpReqHeaders = new HttpHeaders();    	 
		httpReqHeaders.add(REQAUTHHEADER, "");
		httpReqHeaders.add("Content-Type", "application/x-www-form-urlencoded");
		httpReqHeaders.add("Accept", "application/json");
    	String googleAuthTokenURL = "https://www.googleapis.com/oauth2/v4/token?code="+authCode
    			+ "&client_id=155249140930-j7sfop9pk4aq1fu5abft2q87nesgj902.apps.googleusercontent.com"
    			+ "&client_secret=DrNtAkds2-4JpB88TOhnLgn6"
    			+ "&redirect_uri=http://localhost:9022/"
    			+ "&grant_type=authorization_code";
    	Map<String, String> employeeNames = new HashMap<String, String>();
    	try {
    		
    		/*JSONObject httpReqJSON = new JSONObject();
    		httpReqJSON.put("empIDs", empIDs);
    		httpReqJSON.getString("empIDs");*/
    		
    		Map<String, String[]> map = new HashMap<String, String[]>();
//    		map.put("empIDs", empIDs);
    		HttpEntity <Map> httpEntity = new HttpEntity <Map> (null, httpReqHeaders);
    		
    		List<LinkedHashMap> jsonArray = restTemplate.postForObject(googleAuthTokenURL, httpEntity, List.class);
    		
    		for(Object obj : jsonArray){
    			LinkedHashMap<String, Object> json = (LinkedHashMap) obj;
    			LinkedHashMap empVOjson = json.get("employeeVO") instanceof LinkedHashMap ?  (LinkedHashMap) json.get("employeeVO") :  null ;
    			if(empVOjson != null){
    				 String empName = (String) empVOjson.get("empName");
    				 String empId = (String) empVOjson.get("empId");
    				 employeeNames.put(empId, empName);
    			}
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
			
			/*for(int i = 0; i < empIDs.length; i++){
				if(empIDs[i] != null){
					employeeNames.put(empIDs[i], empIDs[i]);
				}
			}
			return employeeNames;*/
		}
    	return "";
	}
	
	public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	                    .loadTrustMaterial(null, acceptingTrustStrategy)
	                    .build();

	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

	    CloseableHttpClient httpClient = HttpClients.custom()
	                    .setSSLSocketFactory(csf)
	                    .build();

	    HttpComponentsClientHttpRequestFactory requestFactory =
	                    new HttpComponentsClientHttpRequestFactory();

	    requestFactory.setHttpClient(httpClient);
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    return restTemplate;
	 }
}