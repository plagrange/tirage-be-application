(function($,undefined){var AcrobatViewer=function(element,options,stringMessages,cssStyles){var defaultSettings={documentUrl:"",minWidth:400,minHeight:200,minAcrobat:"7.0",popUpDocWidth:700,popUpDocHeight:500,disablePopUp:false,adobeReaderDownloadUrl:"http://get.adobe.com/reader/"};
var defaultKeyMessages={noAdobePlugin_warn:"",minAdobePlugin_warn:"",adobePlugin_pre_install:"",adobePlugin_install:"",adobePlugin_post_install:"",adobePlugin_cancel:"",returnNoAcrobatViewer:"",document_view:""};
var defaultStyles={noAdobePluginClass:"av_noAdobePluginClass",minAdobePluginClass:"av_minAdobePluginClass",closeButtonClass:"av_btn_close",panelPopUpButtonClass:"av_cta_btn"};
var settings=$.extend({},defaultSettings,options);
var strings=$.extend({},defaultKeyMessages,stringMessages);
var styles=$.extend({},defaultStyles,cssStyles);
var privateVars={container:undefined,};
var adobeDetection=false;
var adobeVersion="0";
var popUpID="av_popupID";
var popUpActiverID="av_popUpActiverID";
var popUpActiverClass="av_poplight";
var popupIFrameID="av_popupIFrameID";
var popUpFadeID="av_fade";
var popUpFadeNoClickID="av_fade_no_click";
function isAdobeReaderDetected(){return adobeDetection
}function isAdobeSupported(){var splitDetectedVersion=adobeVersion.toString().split(".");
var splitMinVervion=settings.minAcrobat.split(".");
var minLength=(splitDetectedVersion.length<splitMinVervion.length)?splitDetectedVersion.length:splitMinVervion.length;
for(var i=0;
i<minLength;
i++){if(parseInt(splitDetectedVersion[i],10)<parseInt(splitMinVervion[i],10)){return false
}else{if(parseInt(splitMinVervion[i],10)<parseInt(splitDetectedVersion[i],10)){return true
}}}if(splitDetectedVersion.length<splitMinVervion.length){return false
}return true
}function initAdobeInformation(){adobeDetection=false;
if(navigator.plugins&&navigator.plugins.length){if(navigator.platform.indexOf("Linux")>=0){for(var x=0,l=navigator.plugins.length;
x<l;
++x){if(navigator.plugins[x].name.indexOf("Adobe Reader ")>=0){adobeDetection=true;
adobeVersion=navigator.plugins[x].name.split("Adobe Reader ")[1];
adobeVersion=adobeVersion;
break
}}}else{for(x=0;
x<navigator.plugins.length;
x++){var description=navigator.plugins[x].description;
var name=navigator.plugins[x].name;
var version=navigator.plugins[x].version;
if(name.indexOf("Adobe Acrobat")!=-1||description.indexOf("Adobe Reader")!=-1||description.indexOf("Adobe PDF")!=-1){adobeDetection=true;
adobeVersion=parseFloat(description.split("Version ")[1]);
if(adobeVersion.toString().length==1){adobeVersion+=".0"
}if(description.indexOf("Plug-In For Firefox and Netscape")!=-1){if(!version){var parsedVersion;
try{parsedVersion=description.split("Netscape ")[1];
if(-1!=parsedVersion.indexOf('"')){parsedVersion=parsedVersion.substr(1,parsedVersion.length-2)
}}catch(e){parsedVersion=""
}y=navigator.mimeTypes["application/vnd.adobe.pdfxml"];
if(""!=parsedVersion){adobeVersion=parsedVersion
}else{if(y&&y.enabledPlugin){adobeVersion="9.0";
break
}else{y=navigator.mimeTypes["application/vnd.adobe.x-mars"];
if(y&&y.enabledPlugin){adobeVersion="8.0";
break
}}}}else{adobeVersion=version;
break
}}break
}if(name.indexOf("PDF Browser Plugin")!=-1){adobeDetection=true;
adobeVersion=name.split("PDF Browser Plugin ")[1];
break
}if(name.indexOf("PDF Plugin")!=-1){adobeDetection=true;
break
}}}}else{if(window.ActiveXObject){var control=null;
try{control=new ActiveXObject("AcroPDF.PDF")
}catch(e){}if(!control){try{control=new ActiveXObject("PDF.PdfCtrl")
}catch(e){}}if(control){version=control.GetVersions().split(",");
version=version[0].split("=");
version=parseFloat(version[1]);
if(version.toString().length==1){version+=".0"
}adobeVersion=version;
adobeDetection=true
}else{for(x=2;
x<10;
x++){try{oAcro=eval("new ActiveXObject('PDF.PdfCtrl."+x+"');");
if(oAcro){adobeVersion=x;
adobeVersion+=".0";
adobeDetection=true;
control=true;
break
}}catch(e){}}}if(!control){try{oAcro4=new ActiveXObject("PDF.PdfCtrl.1");
if(oAcro4){adobeDetection=true;
adobeVersion="4.0";
control=true
}}catch(e){}}if(!control){try{oAcro7=new ActiveXObject("AcroPDF.PDF.1");
if(oAcro7){adobeDetection=true;
adobeVersion="7.0";
control=true
}}catch(e){}}}}}function showPopUp(idToPopUp,closable,popWidth,popHeight){if(closable){$("#"+idToPopUp).fadeIn().css({width:Number(popWidth),height:Number(popHeight)}).prepend('<a href="#" class="av_close"><div class="'+styles.closeButtonClass+'"></div></a>')
}else{$("#"+idToPopUp).fadeIn().css({width:Number(popWidth),height:Number(popHeight)})
}var iframe=$("#"+popupIFrameID);
$(iframe).attr("src",settings.documentUrl);
var popMargTop=(popHeight+80)/2;
var popMargLeft=(popWidth+80)/2;
$("#"+idToPopUp).css({"margin-top":-popMargTop,"margin-left":-popMargLeft});
internal_popUpFadeID=(closable)?popUpFadeID:popUpFadeNoClickID;
$("body").append('<div id="'+internal_popUpFadeID+'"></div>');
$("#"+internal_popUpFadeID).css({filter:"alpha(opacity=80)"}).fadeIn();
hideContainer();
element.trigger(events.onAVPopUpOn);
$("a.av_close, #"+popUpFadeID).live("click",function(){$("#"+popUpFadeID+" , .av_popup_block").fadeOut(function(){$("#"+popUpFadeID+", a.av_close").remove();
element.trigger(events.onAVPopUpClosed)
});
showContainer();
return false
});
return false
}function enableDocPopUp(){$("#"+popUpActiverID).click(function(){showPopUp(popUpID,true,settings.popUpDocWidth,settings.popUpDocHeight)
})
}function returnNoActobatViewerClick(){element.trigger(events.onButtonReturnEnvNotSupportedClicked)
}var injectViewerContainer=function(){hideContainer();
var stringBuffer=[];
var errorID_to_popUP;
if(!isAdobeReaderDetected()){stringBuffer.push('<div id="docContainer">');
stringBuffer.push('    <div id="av_noAdobePluginID" class="av_popup_block '+styles.noAdobePluginClass+'">');
stringBuffer.push("        <p>"+strings.noAdobePlugin_warn);
stringBuffer.push("        <br/>");
stringBuffer.push("        <br/>");
stringBuffer.push("        "+strings.adobePlugin_pre_install+' <a href="'+settings.adobeReaderDownloadUrl+'" target="download">'+strings.adobePlugin_install+"</a>"+strings.adobePlugin_post_install);
stringBuffer.push("        <br/>");
stringBuffer.push("        <br/>");
stringBuffer.push("        "+strings.adobePlugin_cancel);
stringBuffer.push("        </p>");
stringBuffer.push("        <br/>");
stringBuffer.push('        <button id="return_no_av_btn" class="'+styles.returnNoAcrobatViewerButtonClass+'" type="button" onclick="return false;">'+strings.returnNoAcrobatViewer+"</button>");
stringBuffer.push("    </div>");
stringBuffer.push("</div>");
errorID_to_popUP="av_noAdobePluginID"
}else{if(!isAdobeSupported()){stringBuffer.push('<div id="docContainer">');
stringBuffer.push('    <div id="av_minAdobePluginID" class="av_popup_block '+styles.minAdobePluginClass+'">');
stringBuffer.push("        <p>"+strings.minAdobePlugin_warn);
stringBuffer.push("        <br/>");
stringBuffer.push("        <br/>");
stringBuffer.push("        "+strings.adobePlugin_pre_install+' <a href="'+settings.adobeReaderDownloadUrl+'" target="download">'+strings.adobePlugin_install+"</a>"+strings.adobePlugin_post_install);
stringBuffer.push("        <br/>");
stringBuffer.push("        <br/>");
stringBuffer.push("        "+strings.adobePlugin_cancel);
stringBuffer.push("        </p>");
stringBuffer.push("        <br/>");
stringBuffer.push('        <button id="return_no_av_btn" class="'+styles.returnNoAcrobatViewerButtonClass+'" type="button" onclick="return false;">'+strings.returnNoAcrobatViewer+"</button>");
stringBuffer.push("    </div>");
stringBuffer.push("</div>");
errorID_to_popUP="av_minAdobePluginID"
}else{stringBuffer.push('<div id="docContainer" class="dtp_acrobat_container">');
stringBuffer.push('    <div class="dtp_acrobat_iframe">');
stringBuffer.push('        <iframe id="docViewerFrame" src="'+settings.documentUrl+'" width="100%" height="100%">');
stringBuffer.push("            [Your browser does <em>not</em> support <code>iframe</code>]");
stringBuffer.push("        </iframe>");
stringBuffer.push("    </div>");
if(!settings.disablePopUp){stringBuffer.push('    <div class="dtp_acrobat_toolbar">');
stringBuffer.push('        <p class="'+styles.panelPopUpButtonClass+'">');
stringBuffer.push('            <a href="#" id="'+popUpActiverID+'" class="'+popUpActiverClass+'">');
stringBuffer.push("                "+strings.document_view);
stringBuffer.push("            </a>");
stringBuffer.push("        </p>");
stringBuffer.push("    </div>")
}stringBuffer.push("</div>");
if(!settings.disablePopUp){stringBuffer.push('<div id="'+popUpID+'" class="av_popup_block">');
stringBuffer.push('    <iframe id="'+popupIFrameID+'" width="100%" height="100%">');
stringBuffer.push("        [Your browser does <em>not</em> support <code>iframe</code>]");
stringBuffer.push("    </iframe>");
stringBuffer.push("</div>")
}}}element.append(stringBuffer.join(""));
$("#docContainer").css("min-width",settings.minWidth+"px");
$("#docContainer").css("min-height",settings.minHeight+"px");
if(errorID_to_popUP){errorWidth=Number($("#"+errorID_to_popUP).css("width").split("px")["0"]);
errorHeight=Number($("#"+errorID_to_popUP).css("height").split("px")["0"]);
showPopUp(errorID_to_popUP,false,errorWidth,270)
}element.find("#return_no_av_btn").click(returnNoActobatViewerClick);
showContainer()
};
function hideContainer(){$("#docContainer").css({visibility:"hidden"})
}function showContainer(){$("#docContainer").css({visibility:""})
}this.hideView=function(){hideContainer()
};
this.showView=function(){showContainer()
};
var init=function(){initAdobeInformation();
injectViewerContainer();
enableDocPopUp()
};
init()
};
var methods={hideView:function(){var element=this.first();
if(!element.data("viewer")){return
}var data=element.data("viewer");
return data.viewer.hideView()
},showView:function(){var element=this.first();
if(!element.data("viewer")){return
}var data=element.data("viewer");
return data.viewer.showView()
}};
var events={onAVPopUpOn:"onAVPopUpOn",onAVPopUpClosed:"onAVPopUpClosed",onButtonReturnEnvNotSupportedClicked:"onButtonReturnEnvNotSupportedClicked"};
$.fn.acrobatViewer=function(method,args){if(methods[method]){return methods[method].apply(this,Array.prototype.slice.call(arguments,1))
}else{if(typeof method==="object"||!method){var options=arguments[0];
var stringMessages=arguments[1];
var styles=arguments[2];
return this.each(function(){var element=$(this);
if(element.data("viewer")){return
}var viewer=new AcrobatViewer(element,options,stringMessages,styles);
element.data("viewer",{viewer:viewer})
})
}else{$.error("Method "+method+" does not exist on jQuery.docViewer")
}}}
})(jQuery);