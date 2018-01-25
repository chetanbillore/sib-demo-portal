package com.hitachiconsulting.digitalplatform.controller;

import static com.hitachiconsulting.digitalplatform.utils.RequestMappingConstants.LOGIN_TO_GCP;

import static com.hitachiconsulting.digitalplatform.utils.RequestMappingConstants.OAUTH_TO_CALLBACK;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hitachiconsulting.digitalplatform.exception.LeaderBoardSourceNotFoundException;
import com.hitachiconsulting.digitalplatform.service.LoginGCPService;
import com.wordnik.swagger.annotations.Api;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Api(basePath = "api/", value = "leaderBoard", description = "Operations with Leader Board Service", produces = "application/json")
@RestController
public class LoginController {
	
	public static final String REQAUTHHEADER = "Authorization";

	private static final String TRIGGERTYPE = "First time login";
	
	private static final String TRIGGERCODE = "First_time_login";
	
	private static final String TRIGGER_EVENT = "trigger_event";
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final Collection<String> SCOPES = Arrays.asList("email", "profile");
	private static final String USERINFO_ENDPOINT
    = "https://www.googleapis.com/plus/v1/people/me/openIdConnect";
	
	private static final Logger logger = Logger.getLogger(LoginController.class.getName());
	
	private GoogleAuthorizationCodeFlow flow;
	
	@Value("${rabbitMQHost}")
    private String rabbitMQHost;
	
	@Value("${rabbitMQPort}")
    private int rabbitMQPort;
	
	@Value("${rabbitMQUserName}")
    private String rabbitMQUserName;
	
	@Value("${rabbitMQPassword}")
    private String rabbitMQPassword;
	
	@Value("${rabbitMQVirtualHost}")
    private String rabbitMQVirtualHost;
	
	@Autowired
	private LoginGCPService loginGCPService;
	
	@RequestMapping("/")
	  public String home(HttpServletRequest request, HttpServletResponse response) {
//	    return "Hello World!";
	    String authCode = request.getParameter("code");
//	    String authToken = loginGCPService.getGoogleAuthToken(authCode);
	    return "";
		
	  }
	
	@CrossOrigin
    @RequestMapping(value = LOGIN_TO_GCP, method = RequestMethod.GET)
    public String getTopTenForAllLeagues(@RequestHeader (REQAUTHHEADER) String authorizationHeader, HttpServletRequest request, HttpServletResponse response) throws LeaderBoardSourceNotFoundException{
    	/*GoogleCredential googleCred = new GoogleCredential();*/
    	
//    	googleCred.auth
//    	loginGCPService.googlAuthService();
		String state = new BigInteger(130, new SecureRandom()).toString(32);  // prevent request forgery
		request.getSession().setAttribute("state", state);
		flow = new GoogleAuthorizationCodeFlow.Builder(
		        HTTP_TRANSPORT,
		        JSON_FACTORY
		        /*request.getServletContext().getInitParameter("bookshelf.clientID"),
		        request.getServletContext().getInitParameter("bookshelf.clientSecret"),*/
		        ,"423472271381-27ilik9qpvhicf8nghvnk487g5o8koji.apps.googleusercontent.com","iK3EjOIxNucQCrycC2BuuyQg",
		        SCOPES)
		        .build();

		    // Callback url should be the one registered in Google Developers Console
		    String url =
		        flow.newAuthorizationUrl()
		            .setRedirectUri("http://localhost:9022/api/sibDemoPortal/oauth2callback")
		            .setState(state)            // Prevent request forgery
		            .build();
		    TokenResponse tokenResponse = null;
		    Credential credential = null;
			try {
				tokenResponse = flow.newTokenRequest(request.getParameter("code"))
		            .setRedirectUri(request.getServletContext().getInitParameter("bookshelf.callback"))
		            .execute();
				request.getSession().setAttribute("token", tokenResponse.toString()); // Keep track of the token.
				
			
				credential = flow.createAndStoreCredential(tokenResponse, null);
				final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);

			    final GenericUrl url1 = new GenericUrl(USERINFO_ENDPOINT);      // Make an authenticated request.
			    final HttpRequest req = requestFactory.buildGetRequest(url1);
			    req.getHeaders().setContentType("application/json");

			    final String jsonIdentity = req.execute().parseAsString();
			    @SuppressWarnings("unchecked")
			    HashMap<String, String> userIdResult =
			        new ObjectMapper().readValue(jsonIdentity, HashMap.class);
			    // From this map, extract the relevant profile info and store it in the session.
			    request.getSession().setAttribute("userEmail", userIdResult.get("email"));
			    request.getSession().setAttribute("userId", userIdResult.get("sub"));
			    request.getSession().setAttribute("userImageUrl", userIdResult.get("picture"));
			    logger.log(Level.INFO, "Login successful, redirecting to "
			        + (String) request.getSession().getAttribute("loginDestination"));
//			    resp.sendRedirect((String) req.getSession().getAttribute("loginDestination"));
			
				response.sendRedirect(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		authenticate(request, response);
    	return "";
    }
	
	@RequestMapping(value = OAUTH_TO_CALLBACK, method = RequestMethod.GET)
	public void authenticate(HttpServletRequest req, HttpServletResponse resp){


	    // Ensure that this is no request forgery going on, and that the user
	    // sending us this connect request is the user that was supposed to.
		/*if (req.getSession().getAttribute("state") == null
	        || !req.getParameter("state").equals((String) req.getSession().getAttribute("state"))) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			logger.log(
	          Level.WARNING,
	          "Invalid state parameter, expected " + (String) req.getSession().getAttribute("state")
	              + " got " + req.getParameter("state"));
			try {
				resp.sendRedirect("/books");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}*/

	    req.getSession().removeAttribute("state");     // Remove one-time use state.

	    flow = new GoogleAuthorizationCodeFlow.Builder(
	        HTTP_TRANSPORT,
	        JSON_FACTORY,
	        /*req.getServletContext().getInitParameter("bookshelf.clientID"),
	        req.getServletContext().getInitParameter("bookshelf.clientSecret"),*/
	        "423472271381-27ilik9qpvhicf8nghvnk487g5o8koji.apps.googleusercontent.com","iK3EjOIxNucQCrycC2BuuyQg",
	        SCOPES).build();

	    TokenResponse tokenResponse = null;
	    Credential credential = null;
		try {
			tokenResponse = flow.newTokenRequest(req.getParameter("code"))
	            /*.setRedirectUri("http://localhost:9022/")*/
	            .execute();
			req.getSession().setAttribute("token", tokenResponse.toString()); // Keep track of the token.
			
		
			credential = flow.createAndStoreCredential(tokenResponse, null);
			final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);

		    final GenericUrl url = new GenericUrl(USERINFO_ENDPOINT);      // Make an authenticated request.
		    final HttpRequest request = requestFactory.buildGetRequest(url);
		    request.getHeaders().setContentType("application/json");

		    final String jsonIdentity = request.execute().parseAsString();
		    @SuppressWarnings("unchecked")
		    HashMap<String, String> userIdResult =
		        new ObjectMapper().readValue(jsonIdentity, HashMap.class);
		    // From this map, extract the relevant profile info and store it in the session.
		    req.getSession().setAttribute("userEmail", userIdResult.get("email"));
		    req.getSession().setAttribute("userId", userIdResult.get("sub"));
		    req.getSession().setAttribute("userImageUrl", userIdResult.get("picture"));
		    logger.log(Level.INFO, "Login successful, redirecting to "
		        + (String) req.getSession().getAttribute("loginDestination"));
//		    resp.sendRedirect((String) req.getSession().getAttribute("loginDestination"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	  
	}
}
