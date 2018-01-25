package com.hitachiconsulting.digitalplatform.resourceserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@PropertySource({ "classpath:oauth2_server.properties" })							 
@EnableResourceServer
public class SIBDemoResourceServer  extends ResourceServerConfigurerAdapter {
    
    @Value("${oauth2.server.checkpoint.url}")
	private String authServerCheckTokenUrl;
    
    
    @Value("${oauth2.server.client_id}") 
    private String clientId;
    
    @Value("${oauth2.server.client_secret}") 
    private String clientSecret;


    @Override
	public void configure(HttpSecurity http) throws Exception {
		
		/*http
			.authorizeRequests()			 
				.anyRequest().authenticated() ;*/
    		http
    					.authorizeRequests()
    					.antMatchers("/v2/api-docs", "/swagger-ui.html" ).permitAll()			
    					//.antMatchers("/api/activitypoints/**").authenticated();
    					.anyRequest().permitAll(); 

	}
    
    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
    	config
    	.tokenServices(tokenServices())
    	 .accessDeniedHandler(accessDeniedHandler())
         .authenticationEntryPoint(authenticationEntryPoint());
    	
    }

    @Bean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
         return new DefaultWebResponseExceptionTranslator() {

		@Override
         public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
             ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
             OAuth2Exception body = responseEntity.getBody();
             
             HttpHeaders headers = new HttpHeaders();
             headers.setAll(responseEntity.getHeaders().toSingleValueMap());
             
             // do something with header or response
             return new ResponseEntity<>(body, headers, responseEntity.getStatusCode());
         }
     };
     }

    /**
     * Inject your custom exception translator into the OAuth2 {@link AuthenticationEntryPoint}.
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        final OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
        entryPoint.setExceptionTranslator(webResponseExceptionTranslator());
        return entryPoint;
    }

    /**
     * Classic Spring Security stuff, defining how to handle {@link AccessDeniedException}s.
     * Inject your custom exception translator into the OAuth2AccessDeniedHandler.
     * (if you don't add this access denied exceptions may use a different format)
     * 
     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        final OAuth2AccessDeniedHandler handler = new OAuth2AccessDeniedHandler();
        handler.setExceptionTranslator(webResponseExceptionTranslator());
        return handler;
    }
    
    @Primary
    @Bean
    public RemoteTokenServices tokenServices() {
    	RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(authServerCheckTokenUrl );
        tokenService.setClientId(clientId);
        tokenService.setClientSecret(clientSecret);
        return tokenService;
    }

}
