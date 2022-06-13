<!docTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- WARNING ! No bundle basename must be define. Otherwise fmt:message does not work -->
<html>
    <head>
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <meta name="viewport" content="initial-scale=1,user-scalable=no,maximum-scale=1,width=device-width">
        
        <title><fmt:message key="title_page" /></title>

        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.externalRessourceCtx}/css/common-tablet.css" />
        <link rel="stylesheet" type="text/css" href="${it.externalCssUrl}scenario1-tablet.css" />
        
        <!-- JQuery -->
        <script src="<%=request.getContextPath()%>/../browserscript/js/jquery-1.7.1.min.js" type="text/javascript"></script>

        <!-- jQuery simple modal -->
        <script type="text/javascript" src="<%=request.getContextPath()%>/../browserscript/js/jquery.simplemodal.js"></script>

    <c:if test="${!empty it.authentication && it.authentication}">
        <!-- pinPad -->		
        <script type="text/javascript" src="<%=request.getContextPath()%>/../browserscript/js/dictao.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/../browserscript/js/dictao.kit.pinpad.js"></script>
        <script type="text/javascript">	
            // pinpad init configuration
            var config = {
                valueLength: 6,
                withConfirmation: false,
                labels: ['<fmt:message key="authen_pin_reset"/>','','<fmt:message key="authen_pin"/>'],
                parentId: 'PinpadPlaceHolder',
                customValidateButtonId: 'signButton',
                withStatusDiv: false
            };

            // pinpad input otp callback	
            function inputOtp(otp) {
                document.getElementById('otpInput').value=otp.toString();
            };
                        
            function showPinpad() {	
                var pinpad = dictao.kit.Pinpad.getInstance(config, inputOtp);
                pinpad.show();		
                pinpad.reset();
            };

            // pinpad complete otp callback	
            function onPinpadCollectionComplete(){
                $('#consentCheckBox').removeAttr('disabled');
                updateGui();
            }
            // pinpad reset callback	
            function onPinpadReset() {
                $('#consentCheckBox').attr('disabled','disabled');
                document.getElementById('otpInput').value=null ;
            }
        </script>
        <!-- /pinPad -->
    </c:if>	

    <!-- docViewer with hammer touch library -->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/../browserscript/css/jquery.dictao.docviewer-0.1.0-core.css" />
    <script src="<%=request.getContextPath()%>/../browserscript/js/jquery.hammer-1.0.5.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/../browserscript/js/jquery.dictao.docviewer-0.2.1.3.js" type="text/javascript"></script>

    <!-- docViewer custom theme -->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/${it.externalRessourceCtx}/css/docviewer.css" />

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
            $("#document").on("onSignatureAreaClick", function (event, data) {

                if(!${it.clickToSign})
                    return;

                onSignButtonClicked();
            });
            
            $("#document").docViewer({documentUrl: "<%=request.getContextPath()%>/${it.urls['CONTRACT']}",
                ZoomLevel: 0.52,
                enableFullScreen : false
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
            
            // To avoid unloaded spinner images, the container is visible in CSS and we hide it in code
            $(".modal_loading_content").hide();
        });

        function updateGui(){
            var  btnSubmit = $('#signButton');
            var checkButton = $('#consentCheckBox');
            if( checkButton.is(':checked') ){
                btnSubmit.removeAttr('disabled');
            }else{
                btnSubmit.attr('disabled','disabled');
            }
        }
            
        $(document).ready(function () {
            resize();
        });
             
        $(window).resize(function(e){
            resize();
        });
             
        function resize(){
            $("._content").height($("#view-cast").height() - $("._header").height() -  $("._footer").height());
        }
        
        
        function onSignButtonClicked() {
            openModal(${it.authentication});
        }
        
        function submitSignForm() {
            showLoadingPanel();
            document.forms["signature"].submit();
        }
        
        //popin
        function openModal(authenticationRequired) {
            $("#modal-popup").modal({
                opacity: 70
            });
            if (authenticationRequired){
                showPinpad();
            }else{
                $('#consentCheckBox').removeAttr('disabled');
            }
        }
        
        function openLaoder() {
            $("#modal-loading").modal({
                opacity: 70
            });
        }
        
        function onSignature() {
            document.forms['signature'].submit();
            $.modal.close();
            openLaoder();
        }
            
    </script>
</head>
<body>

    <div id="view-cast"
        style="height: 100%; width: 100%; visibility: hidden; position: fixed;">
    </div>

    <div id="main-container">
        <div class="_container">
            <div class="_header">
                <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
                <noscript>
                <p class="_no_script_warning" align="center">
                <fmt:message key="warning_message" />
                </p>
                </noscript>

                <div id="header-background">
                    <div id="header"></div>
                </div>
                <c:if test="${!empty it.otpTryRemains}">
                    <div class="_otp_trials_number">
                        <fmt:message key="otp_message">
                            <fmt:param value="${it.otpTryRemains}"/>
                        </fmt:message>
                    </div>
                </c:if>
            </div>
            <div class="_content">
                <div id="main">
                    <div class="_documents">
                        <div id="document" class="_document"></div>
                    </div>
                </div>
            </div>
            <div class="_footer">
                <div id="footer" align="center">
                    <div id="main-buttons">
                        <div class="_align_left">
                            <button class="_an_btn" type="button" onclick="location.href='cancel'; openLaoder();">
                                <fmt:message key="button_cancel" />
                            </button>
                        </div>
                        <div class="_align_right">
                            <button id="validateButton" class="_va_btn"  onclick="onSignButtonClicked();">
                                <fmt:message key="button_valid" />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Signature Popup -->
    <div id="modal-popup" class="modal">
        <div class="_signature_popup_container">
            <c:choose>
                <c:when test="${!empty it.authentication && it.authentication}">
                    <div id="legal-popup" class="_signature_popup_legal">	
                        <h1>
                            <fmt:message key="authen_pin_title" />
                        </h1>
                        <h2>
                            <fmt:message key="authen_pin_text" />
                        </h2>
                        <div id="PinpadPlaceHolder" class="_pinpad_container"></div>
                </c:when>
                <c:otherwise>
                    <div id="legal-popup" class="_signature_popup_legal_nootp">	
                        <h1>
                            <fmt:message key="legal_popup_consent_title" />
                        </h1>
                </c:otherwise>
            </c:choose>
            <form method="post" id="signature" action="signature">
                
                <input type="hidden" name="currentPage" id="currentPage" class="text" value="signature" />
                
                <c:if test="${!empty it.term_and_conditions}">
                    <div class="_signature_popup_term_and_conditions">
                        <input id="consentCheckBox" type="checkbox" class="_signature_popup_checkbox" name="consentCheckBox"
                               onclick="updateGui()" value="true" disabled />
                        <fmt:message key="legal_second_step" />
                        <ul>${it.term_and_conditions}
                        </ul>
                    </div>
                </c:if>
                    
                <c:if test="${!empty it.authentication && it.authentication}">
                    <input type="hidden" name="otpInput" id="otpInput" size="6" maxlength="6" />
                </c:if>              
                <div id="legal-popup-buttons">
                    <div class="_align_left">
                        <button class="_an_btn" type="button" onclick="$.modal.close();">
                            <fmt:message key="button_back" />
                        </button>
                    </div>
                    <div class="_align_right">
                        <button id="signButton" type="button" onclick="onSignature();" disabled>
                            <fmt:message key="button_sign_popup" />
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- LOADING -->
    <div id="modal-loading" class="_modal_loading_content">
        <p><fmt:message key="waiting_popup_text" /></p>
        <p class="_modal_loading_image"></p>
    </div>
</body>
</html>