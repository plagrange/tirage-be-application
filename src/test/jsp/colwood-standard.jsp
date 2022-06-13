﻿﻿<!docTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!-- WARNING ! No bundle basename must be define. Otherwise fmt:message does not work -->
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <title><fmt:message key="title_page"/></title>

        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.externalRessourceCtx}/css/common.css" />
        <link rel="stylesheet" type="text/css" href="${it.externalCssUrl}scenario4.css" />

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
                <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.externalRessourceCtx}/css/docviewer.css" />

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

                    function injectViewer(begin_url, url, end_url) {
                        $('#popup').empty();
                        var stringBuffer = [];
                        stringBuffer.push('            <div id="document" class="document"></div>');
                        $('#popup').append(stringBuffer.join(''));
                        $("#document").docViewer({
                            documentUrl: begin_url + url,
                            enableFullScreen: false,
                            minWidth: 500,
                            minHeight: 400
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
                    }
                </script>

                <%-- BEGIN JSP Tag, do not indent --%>
            </c:when>
            <c:otherwise>
                <%-- END JSP Tag, do indent --%>
                <script type="text/javascript">
                    $(document).ready(function(){
                        var stringBuffer = [];
                        stringBuffer.push('            <iframe id="iframe-popup" width="100%" height="100%">');
                        stringBuffer.push('                [Your browser does <em>not</em> support <code>iframe</code>]');
                        stringBuffer.push('            </iframe>');
                        $('#popup').append(stringBuffer.join(''));
                    });

                    function injectViewer(begin_url, url, end_url) {
                        var iframe = $('#iframe-popup');
                        $(iframe).attr('src', begin_url + url + end_url);
                    }
                </script>
                <%-- BEGIN JSP Tag, do not indent --%>
            </c:otherwise>
        </c:choose>
        <%-- END JSP Tag, do indent --%>

        <!--  Images & Consent management -->
        <script type="text/javascript">
            var tabViewedDocuments=new Array;
            <c:forEach var="doc" items="${it.urls}">
                <c:if test="${doc.mandatory}">
                    tabViewedDocuments['${doc.fileName}']=${doc.viewed};
                </c:if>
            </c:forEach>

            function documentViewed(id) {
                document.getElementById(id).className = 'documentViewed';
                tabViewedDocuments[id]=true;
                areAllDocumentsViewed();
            }

            function areAllDocumentsViewed() {                
             <c:choose>
                <c:when test="${!empty it.term_and_conditions}">
                    for (var valeur in tabViewedDocuments) {
                        if(!tabViewedDocuments[valeur]) {
                            if(!document.getElementById("consentCheckBox").disabled){
                                document.getElementById("consentCheckBox").disabled=true;
                            }
                            return;
                        }
                    }
                    if(document.getElementById("consentCheckBox").disabled){
                        document.getElementById("consentCheckBox").disabled=false;
                    }
                </c:when>
                <c:otherwise>
                     for (var valeur in tabViewedDocuments) {
                        if(!tabViewedDocuments[valeur]) {
                            if(!document.getElementById("signButton").disabled){
                                document.getElementById("signButton").disabled=true;
                            }
                            return;
                        }
                    }
                    if(document.getElementById("signButton").disabled){
                        document.getElementById("signButton").disabled=false;
                    }
                 </c:otherwise>
             </c:choose>
            }
        </script>
        <!--  end Images & Consent management -->

        <!--  GWT: import JS-->
        <script type="text/javascript" src="<%= request.getContextPath()%>/ui/dtp/dtp.nocache.js"></script>

        <!--  GWT dico -->
        <script type="text/javascript">
            var dtpGwtDico = {
                widget: "SignPanel",
                forceCheckBoxEnable : "false",
                authentication: "${it.authentication}"
            };

            // callBack on popup post hide
            function popupPostHide() {
                $('#mainContainer').removeClass("opacity");
                $('#legal').removeClass("opacity");
            };

            // callBack on popup pre show
            function popupPreShow() {
                $('#mainContainer').addClass("opacity");
                $('#legal').addClass("opacity");
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
			
            $(document).ready(function(){
                // Pop Up
                $('a.poplight[href^=#]').click(function() {
                    var popID = $(this).attr('rel');
                    var popURL = $(this).attr('href');
                    var id = $(this).attr('id');

                    var popupWidth = $('#popup').width();
                    var popupHeight = $('#popup').height();

                    var query= popURL.split('?');
                    var dim= query[1].split('&');
                    var popWidth = dim[0].split('=')[1];

                    var url = dim[1].split('=')[1];
                    var end_url = "#toolbar=0&statusbar=0&navpanes=0&messages=0&<%= System.currentTimeMillis()%>";
                    var begin_url = "<%= request.getContextPath()%>/";
                    //test
                    var id = dim[2].split('=')[1];
                    documentViewed(id);

                    // inject Acrobat Reader or Dictao DocViewer.
                    // Be aware that Dictao DocViewer remove all child of the popup div,
                    // so we have to inject it before prepending the close button
                    injectViewer(begin_url, url, end_url);

                    $('#' + popID).fadeIn().css({ 'width': popupWidth }).css({ 'height': popupHeight }).prepend('<a href="#" class="close"><div class="btn_close"></div></a>');

                    var popMargTop = ($('#' + popID).height() + 80) / 2;
                    var popMargLeft = ($('#' + popID).width() + 80) / 2;

                    //Apply Margin to Popup
                    $('#' + popID).css({
                        'margin-top' : -popMargTop,
                        'margin-left' : -popMargLeft
                    });

                    //Fade in Background
                    $('body').append('<div id="fade"></div>');
                    $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn();
                    return false;
                });

                //Close Popups and Fade Layer
                $('a.close, #fade').live('click', function() {
                    $('#fade , .popup_block').fadeOut(function() {
                        $('#fade, a.close').remove();
                    });
                    return false;
                });

            });
        </script>

    </head>
    <!--[if IE 6 ]><body class="ie6 ie" onLoad="javascript:areAllDocumentsViewed()"><![endif]-->
    <!--[if IE 7 ]><body class="ie7 ie" onLoad="javascript:areAllDocumentsViewed()"><![endif]-->
    <!--[if IE 8 ]><body class="ie8 ie" onLoad="javascript:areAllDocumentsViewed()"><![endif]-->
    <!--[if IE 9 ]><body class="ie9 ie" onLoad="javascript:areAllDocumentsViewed()"><![endif]-->

    <!--[if !IE]>--><body class="notie" onLoad="javascript:areAllDocumentsViewed()"><!--<![endif]-->

        <div id="mainContainer">
            <div class="container">
                <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
                <noscript>
                    <p class="warning" align="center">
                        <fmt:message key="warning_message" />
                    </p>
                </noscript>

                <div id="header">
                    <div class="logo"></div>
                    <div class="titre"><fmt:message key="title_header" /></div>
                </div>

                <c:if test="${!empty it.otpTryRemains}">
                    <div class="optMessage">
                        <fmt:message key="otp_message">
                            <fmt:param value="${it.otpTryRemains}"/>
                        </fmt:message>
                    </div>
                </c:if>

                <div id="legal" class="legal">
                    <!----------- BUSINESS ----------->
                    <div class="textBlock identityBlock">
                        <!----------- IDENTITY ----------->
                        <h2>
                            <fmt:message key="user_identity">
                                <fmt:param value="${it.ua.personalInfo.title}"/>
                                <fmt:param value="${it.ua.personalInfo.firstName}"/>
                                <fmt:param value="${it.ua.personalInfo.lastName}"/>
                            </fmt:message>
                        </h2>
                        <!----------- BUSINESS ----------->
                        <div class="reference">
                            <fmt:message key="reference">
                                <fmt:param value="${it.tx.businessType}"/>
                                <fmt:param value="${it.tx.businessID}"/>
                            </fmt:message>
                        </div>
                        <br>
                        <fmt:message key="legal_presentation_step" />
                    </div>
                    <!----------- SECOND STEP ----------->
                    <div class="textBlock">
                        <h4><fmt:message key="legal_second_step_title" /></h4>
                        <fmt:message key="legal_second_step_information" />
                        <ul>
                            <c:forEach var="document" items="${it.urls}" begin="0" varStatus="rowCounter">
                                <c:if test="${rowCounter.count % 3 == 1}">
                                   <c:choose>
                                      <c:when test="${rowCounter.index != 0}">
                                      </ul><ul>
                                      </c:when>
                                  </c:choose>
                                </c:if>
                                <li>
                                        <c:choose>
                                            <c:when test="${document.viewed}">
                                            <a href="#?w=700&url=${document.url}&id=${document.fileName}" rel="popup" class="poplight">
                                                <div class="documentInfo"><div id="${document.fileName}" class="documentViewed" ></div>
                                                ${document.label}</div>
                                            </a>
                                            </c:when>
                                            <c:when test="${document.mandatory}">
                                            <a href="#?w=700&url=${document.url}&id=${document.fileName}" rel="popup" class="poplight">
                                                <div class="documentInfo"><div id="${document.fileName}" class="documentMandatory" ></div>
                                                ${document.label}</div>
                                            </a>
                                            </c:when>
                                            <c:otherwise>
                                            <a href="#?w=700&url=${document.url}&id=${document.fileName}" rel="popup" class="poplight">
                                                <div class="documentInfo"><div id="${document.fileName}" class="documentByDefault" ></div>
                                                ${document.label}</div>
                                            </a>
                                            </c:otherwise>
                                        </c:choose>

                                </li>

                            </c:forEach>
                        </ul>
                    </div>
                    <!----------- THIRD STEP ----------->
                    <form id="signForm" method="post" action="signature">
                        <input type="hidden" name="otpInput" id="otpInput" class="text" size="6" maxlength="6" />
                        <input type="hidden" name="currentPage" id="currentPage" class="text" value="signature" />
                        <div class="textBlock">
                            
                            <c:if test="${!empty it.term_and_conditions}">
                                <h4><fmt:message key="legal_third_step_title" /></h4>
                                <fmt:message key="legal_third_description" /><br><br>
                                <div class="reference">
                                    <input id="consentCheckBox" type="checkbox" name="consentCheckBox" value="true" disabled/>
                                    <fmt:message key="legal_third_step" />
                                </div>
                                <ul>${it.term_and_conditions}</ul>
                            </c:if>
                            
                            <div class="bottom_button">
                                <button class="an_btn" type="button" onclick="location.href='cancel'">
                                    <fmt:message key="button_cancel" />
                                </button>
                                    <c:choose>
                                        <c:when test="${!empty it.term_and_conditions}">
                                            <button id="signButton" class="va_btn" type="button" onclick="onSignButtonClicked();">
                                                <fmt:message key="button_sign" />
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button id="signButton" class="va_btn" type="button" onclick="onSignButtonClicked();" disabled>
                                                <fmt:message key="button_sign" />
                                            </button>
                                        </c:otherwise>
                                    </c:choose>		
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- OTP Popup Panel -->
        <div id="otpPopupPanel" class="legal textBlock popup_otp" style="display:none;">
            <h2><fmt:message key="authen_pin_title" /></h2>
            <br>
            <div class="textInner"><fmt:message key="authen_pin_text" /></div>
            <label for="otpInputPopup" class="strong gray"><fmt:message key="authen_pin" /></label>
            <input type="password" name="otpInputPopup" id="otpInputPopup" class="text" size="6" maxlength="6" />
            <div class="bottombutton_sign_pop_up">
                <button id="validateOtpButton" type="button" disabled><fmt:message key="button_valid" /></button>
                <button id="cancelOtpButton" class="an_btn" type="button"><fmt:message key="button_cancel" /></button>
            </div>
        </div>

        <!-- LOADER -->
        <div id="loading" style="height: 75px; width: 200px; display:none">
            <fmt:message key="load_page" /><br/>
            <div class="loader"></div>
        </div>

        <!-- DocViewer/AcrobatReader POPUP VIEW-->
        <div id="popup" class="popup_block">
        </div>

    </body>
</html>