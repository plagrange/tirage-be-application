<!docTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!-- WARNING ! No bundle basename must be define. Otherwise fmt:message does not work -->
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <title><fmt:message key="title_page"/></title>
        
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.externalRessourceCtx}/css/common.css" />
        <link rel="stylesheet" type="text/css" href="${it.externalCssUrl}scenario2.css" />

        <!-- jQuery -->
        <script src="<%=request.getContextPath()%>/../browserscript/js/jquery-1.7.1.min.js" type="text/javascript"></script>

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
                documentUrl: "<%=request.getContextPath()%>/${it.current_document.url}",
                minWidth: 500,
                minHeight: 300
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
<%-- END JSP Tag, do indent --%>

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
        var contractURL = "<%=request.getContextPath()%>/${it.current_document.url}#toolbar=0&statusbar=0&navpanes=0&messages=0&<%=System.currentTimeMillis()%>";
        
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
                    panelPopUpButtonClass: 'cta_btn',
                    returnNoAcrobatViewerButtonClass: 'av_return_no_av_btn'
                });
            });
        });
        </script>
<%-- BEGIN JSP Tag, do not indent --%>
    </c:otherwise>
</c:choose>
<%-- END JSP Tag, do indent --%>

    </head>
        
<%-- BEGIN JSP Tag, do not indent --%>
<%-- ----------- SESSION -------- --%>
<c:set var="count" value="${fn:length(it.urls)}" scope="session" />

<c:choose>
    <c:when test="${!empty param.current}">
        <c:set var="current" value="${param.current}" scope="session" />
    </c:when>
    <c:otherwise>
        <c:set var="current" value="1" scope="session" />
    </c:otherwise>
</c:choose>
<%-- -------- END SESSION -------- --%>
<%-- END JSP Tag, do indent --%>

    <!--[if IE 6 ]><body class="ie6 ie"><![endif]-->
    <!--[if IE 7 ]><body class="ie7 ie"><![endif]-->
    <!--[if IE 8 ]><body class="ie8 ie"><![endif]-->
    <!--[if IE 9 ]><body class="ie9 ie"><![endif]-->

    <!--[if !IE]>--><body class="notie"><!--<![endif]-->

        <div class="container">
            <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
            <noscript>
                <p class="warning" align="center">
                    <fmt:message key="warning_message" />
                </p>
            </noscript>
            
            <div id="header"></div>

            <div class="legal">
                <!----------- IDENTITY ----------->
                <h2>
                    <fmt:message key="user_identity">
                        <fmt:param value="${it.ua.personalInfo.title}"/>
                        <fmt:param value="${it.ua.personalInfo.firstName}"/>
                        <fmt:param value="${it.ua.personalInfo.lastName}"/>
                    </fmt:message>
                </h2>
                <!----------- BUSINESS ----------->
                <div class="textBlock1">
                    <div class="reference">
                        <fmt:message key="reference">
                            <fmt:param value="${it.tx.businessType}"/>
                            <fmt:param value="${it.tx.businessID}"/>
                        </fmt:message>
                    </div>
                    <fmt:message key="legal_presentation_step" />
                </div>
                <!----------- FIRST STEP ----------->
                <h4><fmt:message key="legal_first_step_title" /></h4>
                <div class="textBlock">
                    <fmt:message key="legal_first_step" /><br>
                    <fmt:message key="legal_first_step_next"/><br>
                    <fmt:message key="legal_first_step_view_all_documents"/>
                </div>

                <!----------- NUMBER DOCUMENTS ----------->
                <h3>
                    <fmt:message key="document"/> ${current}/${count} : ${it.current_document.label}
                </h3>
        
                <!----------- CONTRACTS ----------->
                <div id="document" class="document_container"></div>
        
                <!----------- popup ----------->
                <div id="popup" style="display:none;"></div>
                
                <!----------- LINK ----------->
                <p class="bottombutton_view">
                    <!-- Cancel -->
                    <input type="button" value='<fmt:message key="button_cancel" />' onclick="location.href='cancel'" class="an_btn"/>
                    <!-- Next Page -->
                    <form id="" method="post" action="consult-and-agreement?current=${current + 1}">
                    <c:choose>
                        <c:when test="${current == count}">
                        <input type="submit" value='<fmt:message key="button_continue" />' class="va_btn" />
                        </c:when>
                        <c:otherwise>
                        <input type="submit" value='<fmt:message key="button_next" />' class="va_btn" />
                        </c:otherwise>
                    </c:choose>
                    </form>
                    <!-- Previous Page -->
                    <c:choose>
                    <c:when test="${current <= 1}">
                        <button type="button" class="va_btn_disabled"><fmt:message key="button_previous" /></button>
                    </c:when>
                    <c:otherwise>
                        <form id="" method="post" action="consult-and-agreement?current=${current - 1}">
                        <input type="submit" value='<fmt:message key="button_previous" />' class="va_btn"/>
                        </form>
                    </c:otherwise>
                    </c:choose>
                </p>
                
            </div>
        
            <div id="footer"></div>
        </div>
    </body>
</html>