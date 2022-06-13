package com.dictao.dtp.web.webagent;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dictao.dacs.websso.common.AuthenticationRequestMessage;
import com.dictao.dacs.websso.common.AuthnRequestContext;
import com.dictao.dacs.websso.filter.DACSFilter;
import com.dictao.dacs.websso.filter.DACSFilterServletRequestWrapper;
import com.dictao.dacs.websso.filter.WebSSOHelper;
import com.dictao.dtp.core.data.IAgent;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.web.HttpContext;

/**
 * 
 * @author kchakali
 */
public class WebAgentSSO implements IAgent {

	private static String AGENT_URL = "DACSAgentWebSSO/";

	private WebSSOHelper agentHelper;
	
	private Locale locale;
	
	private Map<String, String> metadata;
	
	private String webAppContextPath = null;
	
	private String contextId;
	
	/**
	 * @param request
	 *            the current HttpServletRequest <i><b>(required)</i></b>
	 */
	public WebAgentSSO(HttpServletRequest request, Locale locale) {

		if (!(request instanceof DACSFilterServletRequestWrapper))
			throw new EnvironmentException(
					EnvironmentException.Code.DTP_ENV_CONFIGURATION,
					"WebAgentSSO must be instantiated from DACSAgentServletRequestWrapperImpl. "
							+ "Please check filters order in WEB-INF/web.xml and set DACSAgentFilter in first position.");

		String webappBaseUrl = request.getContextPath();
		if (!webappBaseUrl.endsWith("/")) {
			webappBaseUrl += "/";
		}
		this.webAppContextPath = webappBaseUrl.substring(0, webappBaseUrl.length() - 1);
		this.locale = locale;
		this.agentHelper = new WebSSOHelper(request, webappBaseUrl + AGENT_URL);

	}
	
	public WebAgentSSO(HttpServletRequest request) {
		this(request, null);
	}

	@Override
	public int getUserSecurityLevel() {
		if (null == agentHelper)
			throw new UserException(
					UserException.Code.DTP_USER_INVALID_PARAMETER,
					"Unexpected condition occurred : no instance 'DacsWebAgent' set,  internal agent is nil.");
		return agentHelper.getSessionSecurityLevel();
	}

	@Override
	public String getProofId() {
		if (null != agentHelper)
			return agentHelper.getCurrentAuthenticationResponseId();
		throw new UserException(
				UserException.Code.DTP_USER_INVALID_PARAMETER,
				"Unexpected condition occurred : no instance 'DacsWebAgent' set,  internal agent is nil.");
	}

	@Override
	public String getAuthenticationUrl(int requestedLevel,
			String applicationId, String transactionId, String userIdentifier, String contextId, Map<String, String> metadata,
			String urlOK, String urlKO) throws IOException {
		this.metadata = metadata;
		this.contextId = contextId;
		return getAuthenticationUrl(requestedLevel, applicationId, transactionId, userIdentifier, urlOK, urlKO);
	}
	
	@Override
	public String getAuthenticationUrl(int requestedLevel, String applicationId, String transactionId, String userIdentifier, String urlOK, String urlKO) throws IOException{
		
		
		// Hack Lors de la prochaine release verifier absolument https://jira.dictao.com/browse/DTPJAVA-2184 et MAJ la ligne ci-dessous
		HttpContext.getRequest().getSession().setAttribute(DACSFilter.DACS_SSO_IDENTITY_SESSION_ATTRIBUTE_NAME, null);
		
		
		AuthnRequestContext context = null;
		if (contextId != null)
			context = new AuthnRequestContext(contextId, locale, metadata);
		AuthenticationRequestMessage authenticationRequestMessage = new AuthenticationRequestMessage(
				applicationId, transactionId, new Date());
		authenticationRequestMessage.setSecurityLevel(requestedLevel);
		authenticationRequestMessage.setSuccessfulUrl(webAppContextPath + urlOK);
		authenticationRequestMessage.setErrorUrl(webAppContextPath + urlKO);
		authenticationRequestMessage.setPrincipal(userIdentifier);
		authenticationRequestMessage.setForceAuthentication(true);
		authenticationRequestMessage.setContext(context);
		return agentHelper
				.getUrlToAuthenticationPage(authenticationRequestMessage);
	}

	@Override
	public String getUserIdentifier() {
		if (null != agentHelper)
			return agentHelper.getUserIdentifier();
		throw new UserException(
				UserException.Code.DTP_USER_INVALID_PARAMETER,
				"Unexpected condition occurred : no instance 'DacsWebAgent' set,  internal agent is nil.");
	}

	@Override
	public Map<String, String> getMetadata() {
		return metadata;
	}

	@Override
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}
	
}
