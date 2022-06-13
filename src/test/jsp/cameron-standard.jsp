<!docTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- WARNING ! No bundle basename must be define. Otherwise fmt:message does not work -->
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <title><fmt:message key="title_page"/></title>
        
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.externalRessourceCtx}/css/common.css" />
        <link rel="stylesheet" type="text/css" href="${it.externalCssUrl}scenario1.css" />

        <!-- JQuery -->
        <script src="<%=request.getContextPath()%>/../browserscript/js/jquery-1.7.1.min.js" type="text/javascript"></script>

        <!-- jQuery simple modal -->
        <script type="text/javascript" src="<%=request.getContextPath()%>/../browserscript/js/jquery.simplemodal.js"></script>
        
        
<%-- BEGIN JSP Tag, do not indent --%>
<c:choose>
    <c:when test="${it.isDocViewerEnabled}">
<%-- END JSP Tag, do indent --%>

        <!-- docViewer -->
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/../browserscript/css/jquery.dictao.docviewer-0.1.0-core.css" />
        <script src="<%=request.getContextPath()%>/../browserscript/js/jquery.dictao.docviewer-0.2.1.3.js" type="text/javascript"></script>
        
        <!-- docViewer custom theme -->
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/${it.externalRessourceCtx}/css/docviewer.css" />

         <!-- Store messages in local variables -->
        <fmt:message key="signature_field_validation_title" var="signature_field_validation_title" />
        <fmt:message key="signature_field_validation_valid_signature" var="signature_field_validation_valid_signature" />
        <fmt:message key="signature_field_validation_invalid_signature" var="signature_field_validation_invalid_signature" />
        <fmt:message key="signature_field_validation_valid_timestamp" var="signature_field_validation_valid_timestamp" />
        <fmt:message key="signature_field_validation_invalid_timestamp" var="signature_field_validation_invalid_timestamp" />
        <fmt:message key="signature_field_validation_no_timestamp" var="signature_field_validation_no_timestamp" />
        <fmt:message key="signature_field_validation_signer" var="signature_field_validation_signer" />
        <fmt:message key="signature_field_validation_signature_date" var="signature_field_validation_signature_date" />
        <fmt:message key="signature_field_validation_timestamp_date" var="signature_field_validation_timestamp_date" />

        <script type="text/javascript">
        $(document).ready(function () {
        
            $("#document").docViewer({
                documentUrl: "<%=request.getContextPath()%>/${it.urls['CONTRACT']}",
                minWidth: 500,
                minHeight: 728
            },{
                signature_field_validation_title: '${fn:replace(signature_field_validation_title, '\'', '&#39;')}',
                signature_field_validation_valid_signature: '${fn:replace(signature_field_validation_valid_signature, '\'', '&#39;')}',
                signature_field_validation_invalid_signature: '${fn:replace(signature_field_validation_invalid_signature, '\'', '&#39;')}',
                signature_field_validation_valid_timestamp: '${fn:replace(signature_field_validation_valid_timestamp, '\'', '&#39;')}',
                signature_field_validation_invalid_timestamp: '${fn:replace(signature_field_validation_invalid_timestamp, '\'', '&#39;')}',
                signature_field_validation_no_timestamp: '${fn:replace(signature_field_validation_no_timestamp, '\'', '&#39;')}',
                signature_field_validation_signer: '${fn:replace(signature_field_validation_signer, '\'', '&#39;')}',
                signature_field_validation_signature_date: '${fn:replace(signature_field_validation_signature_date, '\'', '&#39;')}',
                signature_field_validation_timestamp_date: '${fn:replace(signature_field_validation_timestamp_date, '\'', '&#39;')}'
            });

        });
    
        </script>

<%-- BEGIN JSP Tag, do not indent --%>
   </c:when>
   <c:otherwise>
        <!-- Plugin Dictao AcrobatViewer -->
        <script src="<%= request.getContextPath() %>/../browserscript/js/jquery.dictao.acrobatviewer-0.1.0.js" type="text/javascript"></script>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/../browserscript/css/jquery.dictao.acrobatviewer-0.1.0-core.css" />
        
        <!-- acrobatViewer custom theme -->
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/${it.externalRessourceCtx}/css/acrobatviewer.css" />
        
        <!-- Store messages in local variables -->
        <c:set var="minAdobePlugin_version_number" value="7.0" scope="request"/>
       
        <fmt:message key="noAdobePlugin_warn" var ="noAdobePlugin_warn" />
        <fmt:message key="minAdobePlugin_warn" var ="minAdobePlugin_warn">
            <fmt:param value="${minAdobePlugin_version_number}"/>
        </fmt:message>
        <fmt:message key="adobePlugin_pre_install" var ="adobePlugin_pre_install" />
	    <fmt:message key="adobePlugin_install" var ="adobePlugin_install" />
        <fmt:message key="adobePlugin_post_install" var ="adobePlugin_post_install" />
        <fmt:message key="adobePlugin_cancel" var ="adobePlugin_cancel" />
        <fmt:message key="returnNoAcrobatViewer" var ="returnNoAcrobatViewer" />
        <fmt:message key="document_view" var ="document_view" />

        <script type="text/javascript">
        var contractURL = "<%=request.getContextPath()%>/${it.urls['CONTRACT']}#toolbar=0&statusbar=0&navpanes=0&messages=0&<%=System.currentTimeMillis()%>";
        
        $(document).ready(function(){
        	$(document).ready(function () {
                
                $("#document").on("onButtonReturnEnvNotSupportedClicked", function (event, data) {
                    // Cancel and return to portal
                    location.href='cancel_no_av';
                });
                
                var popupWidth = $("#popup").width();
                var popupHeight = $("#popup").height();
                
                $("#document").acrobatViewer({
                    documentUrl: contractURL,
                    minAcrobat: '${minAdobePlugin_version_number}',
                    popUpDocWidth: popupWidth,
                    popUpDocHeight: popupHeight,
                    disablePopUp: false
                },{
                    noAdobePlugin_warn: '${fn:replace(noAdobePlugin_warn, '\'', '&#39;')}',
                    minAdobePlugin_warn: '${fn:replace(minAdobePlugin_warn, '\'', '&#39;')}',
                    adobePlugin_pre_install: '${fn:replace(adobePlugin_pre_install, '\'', '&#39;')}',
                    adobePlugin_install: '${fn:replace(adobePlugin_install, '\'', '&#39;')}',
                    adobePlugin_post_install: '${fn:replace(adobePlugin_post_install, '\'', '&#39;')}',
                    adobePlugin_cancel: '${fn:replace(adobePlugin_cancel, '\'', '&#39;')}',
                    returnNoAcrobatViewer: '${fn:replace(returnNoAcrobatViewer, '\'', '&#39;')}',
                    document_view: '${fn:replace(document_view, '\'', '&#39;')}'
                },{
                    noAdobePluginClass: 'av_noAdobePluginClass',
                    minAdobePluginClass: 'av_minAdobePluginClass',
                    closeButtonClass: 'av_btn_close',
                    panelPopUpButtonClass: 'av_cta_btn',
                    returnNoAcrobatViewerButtonClass: 'av_return_no_av_btn'
                });
            });
        });
        </script>
<%-- BEGIN JSP Tag, do not indent --%>
    </c:otherwise>
</c:choose>
<%-- END JSP Tag, do indent --%>

        <!--  GWT: import JS-->
        <script type="text/javascript" src="<%=request.getContextPath()%>/ui/dtp/dtp.nocache.js"></script>

        <!--  GWT  -->
        <script type="text/javascript">
        // Dico
        var dtpGwtDico = {
            widget: "SignPanel",
            forceCheckBoxEnable : "true",
            authentication: "${it.authentication}"
        };

        // callBack on popup post hide
        function popupPostHide() {
            $('#mainContainer').removeClass("opacity");
	    	if(!${it.isDocViewerEnabled}) {
                // show acrobat
                $("#document").acrobatViewer('showView');
            }
        };

        // callBack on popup pre show
        function popupPreShow() {
	    	$('#mainContainer').addClass("opacity");
            if(!${it.isDocViewerEnabled}) {
                // hide acrobat
                $("#document").acrobatViewer('hideView');
            }
        };
        
        function onSignButtonClicked() {
            if(${it.authentication}) {
                sendOtpToUser();
                showOtpPanel();
            } else {
                submitSignForm();
            }
        }
        
        function submitSignForm() {
            showLoadingPanel();
            document.forms["signForm"].submit();
        }
        
        function sendOtpToUser() {
             xmlhttp=new XMLHttpRequest();
             xmlhttp.open("POST","sendOtp",true);
             xmlhttp.send();
        }
        // Alert handling
        function showAlert() {
            $("#alert_popup").modal({
                opacity: 38
            });
        }
        
        function onAlertOkButtonClick(){
            $.modal.close();
        }
        
        </script>

    </head>
    <!--[if IE 6 ]><body class="ie6 ie"><![endif]-->
    <!--[if IE 7 ]><body class="ie7 ie"><![endif]-->
    <!--[if IE 8 ]><body class="ie8 ie"><![endif]-->

    <!--[if !IE]>--><body class="notie"><!--<![endif]-->

    <div id="mainContainer">
        <div class="container">
            <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
            <noscript>
                <p class="warning" align="center">
                    <fmt:message key="warning_message" />
                </p>
            </noscript>

            <div id="header"></div>
    
            <c:if test="${!empty it.otpTryRemains}">
            <div class="optMessage">
                        <fmt:message key="otp_message">
                <fmt:param value="${it.otpTryRemains}"/>
                        </fmt:message>
            </div>
            </c:if>
    
            <div id="main">
                <div class="content">
                <div class="top"></div>
                <div class="documents">
                    <div id="document" class="document"></div>
                </div>
                <div class="legal">
                    <form id="signForm" method="post" action="signature">
                        <h2><!-- USER -->
                            <fmt:message key="user_identity">
                                <fmt:param value="${it.ua.personalInfo.title}"/>
                                <fmt:param value="${it.ua.personalInfo.firstName}"/>
                                <fmt:param value="${it.ua.personalInfo.lastName}"/>
                            </fmt:message>
                        </h2>
                        <div class="textBlock1"><!-- BUSINESS -->
                            <div class="reference">
                            <fmt:message key="reference">
                                <fmt:param value="${it.tx.businessType}"/>
                                <fmt:param value="${it.tx.businessID}"/>
                            </fmt:message>
                            </div>
                            <fmt:message key="legal_presentation_step" />
                        </div> 
                        <h2><fmt:message key="legal_first_step_title" /></h2>
                        <div class="textBlock2">
                            <fmt:message key="legal_first_step" />
                        </div>
                        <input type="hidden" name="otpInput" id="otpInput" size="6" maxlength="6" />
                        <input type="hidden" name="currentPage" id="currentPage" class="text" value="signature" />
                        
                        <c:if test="${!empty it.term_and_conditions}">
                            <h2><fmt:message key="legal_second_step_title" /></h2>
                            <div class="textBlock3">
                                <input id="consentCheckBox" type="checkbox" name="consentCheckBox" value="true" disabled/>
                                <fmt:message key="legal_second_step" />
                                <ul>${it.term_and_conditions}</ul>
                            </div>
                        </c:if>
                        
                        <p class="center link">
                            <button class="an_btn" type="button" onclick="location.href='cancel'"><fmt:message key="button_cancel" /></button>                    
                            <button id="signButton" type="button" onclick="onSignButtonClicked();" disabled><fmt:message key="button_valid" /></button>
                        </p>
                    </form>
                </div>
                <div class="bottom"></div>
            </div>
            </div>
            <div id="footer"></div>
        </div>
    </div>

    <!-- OTP Popup Panel -->
    <div id="otpPopupPanel" class="legal textBlock popup_otp" style="display:none;">
        <h2><fmt:message key="authen_pin_title" /></h2>
        <br/>
        <div class="textInner"><fmt:message key="authen_pin_text" /></div>
        <label for="otpInputPopup" class="strong gray"><fmt:message key="authen_pin" /></label>
        <input type="password" name="otpInputPopup" id="otpInputPopup" class="text" size="6" maxlength="6" />
        <p class="center link">
            <button id="validateOtpButton" type="button" disabled><fmt:message key="button_valid" /></button>
            <button id="cancelOtpButton" class="an_btn" type="button"><fmt:message key="button_cancel" /></button>
        </p>
    </div>

     <!----------- popup ----------->
     <div id="popup" style="display:none;"></div>
     
<c:if test="${it.clickToSign}">
    <!-- Alert popup -->
    <div id="alert_popup" class="alert_popup">
        <div id="alert_content" class="alert_content">
            <fmt:message key="accept_agreement_before_click_to_sign" />
        </div>
        <div id="alert_footer" class="alert_footer">
            <button id="alert_ok_button" class="alert_ok_button" type="button" onclick="onAlertOkButtonClick();"><fmt:message key="button_ok" /></button>
        </div>
    </div>
</c:if>
    <!-- LOADER -->
    <div id="loading" style="height: 75px; width: 200px; display:none">
        <fmt:message key="load_page" />
        <br/>
        <div class="loader"></div>
    </div>

    </body>
</html>