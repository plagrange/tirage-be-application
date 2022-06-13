package com.dictao.dtp.web.gwt.adsigner.client.util;

public class Base64 {

	public static native String encode(final String data) /*-{
															
		var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var out = "", c1, c2, c3, e1, e2, e3, e4;
		
		for (var i = 0; i < data.length; ) {
		c1 = data.charCodeAt(i++);
		c2 = data.charCodeAt(i++);
		c3 = data.charCodeAt(i++);
		e1 = c1 >> 2;
		e2 = ((c1 & 3) << 4) + (c2 >> 4);
		e3 = ((c2 & 15) << 2) + (c3 >> 6);
		e4 = c3 & 63;
		if (isNaN(c2))
		e3 = e4 = 64;
		else if (isNaN(c3))
		e4 = 64;
		out += tab.charAt(e1) + tab.charAt(e2) + tab.charAt(e3) + tab.charAt(e4);
		}
		return out;
	}-*/;

	public static native String decode(final String data) /*-{

		var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var out = "", c1, c2, c3, e1, e2, e3, e4;
		for (var i = 0; i < data.length; ) {
		e1 = tab.indexOf(data.charAt(i++));
		e2 = tab.indexOf(data.charAt(i++));
		e3 = tab.indexOf(data.charAt(i++));
		e4 = tab.indexOf(data.charAt(i++));
		c1 = (e1 << 2) + (e2 >> 4);
		c2 = ((e2 & 15) << 4) + (e3 >> 2);
		c3 = ((e3 & 3) << 6) + e4;
		out += String.fromCharCode(c1);
		if (e3 != 64)
		out += String.fromCharCode(c2);
		if (e4 != 64)
		out += String.fromCharCode(c3);
		}

		return out;
	}-*/;

	public static native String decodeComplaiment(final String data) /*-{

		var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var out = "", c11, c22, c33, e1, e2, e3, e4;
		for (var i = 0; i < data.length; ) {
		e1 = tab.indexOf(data.charAt(i++));
		e2 = tab.indexOf(data.charAt(i++));
		e3 = tab.indexOf(data.charAt(i++));
		e4 = tab.indexOf(data.charAt(i++));
		c11 = (e1 << 2) + (e2 >> 4);
		c22 = ((e2 & 15) << 4) + (e3 >> 2);
		c33 = ((e3 & 3) << 6) + e4;
		out += String.fromCharCode(c11);
		if (e3 != 64)
		out += String.fromCharCode(c22);
		if (e4 != 64)
		out += String.fromCharCode(c33);
		}

		//UTF-8 decoding part

		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while ( i < out.length ) {

		c = out.charCodeAt(i);

		if (c < 128) {
		string += String.fromCharCode(c);
		i++;
		}
		else if((c > 191) && (c < 224)) {
		c2 = out.charCodeAt(i+1);
		string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
		i += 2;
		}
		else {
		c2 = out.charCodeAt(i+1);
		c3 = out.charCodeAt(i+2);
		string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
		i += 3;
		}

		}

		return string;

	}-*/;

}
