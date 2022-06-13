package com.dictao.dtp.web.gwt.common.client;

import com.dictao.dtp.web.gwt.common.shared.RedirectStatus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Principal view.
 * 
 * @author gla
 */
public abstract class View {

    /* **************************** CONSTANTS *************************** */

    /* **************************** ATTRIBUTES ************************** */

    protected PopupPanel popupWait;

    /* ************************** PUBLIC METHODS ************************ */

    public View() {
	super();
	// Init wait popup
	popupWait = new PopupPanel(true);
	popupWait.addStyleName("dtp-popupwait-loader");
	popupWait.setGlassEnabled(true);
	popupWait.setAutoHideEnabled(false);
	popupWait.setSize("50px", "50px");
    }

    public View(String label) {
	super();

	// Init du wait
	popupWait = new PopupPanel(true);
	VerticalPanel contents = new VerticalPanel();

	SimplePanel panel = new SimplePanel();

	HTML mp = new HTML(label);
	panel.add(mp);
	popupWait.addStyleName("dtp-popupwait-text");
	contents.add(panel);

	SimplePanel panel2 = new SimplePanel();
	popupWait.addStyleName("dtp-popupwait-img");
	contents.add(panel2);

	popupWait.add(contents);

	popupWait.setGlassEnabled(true);
	popupWait.setAutoHideEnabled(false);
    }

    /**
     * Show a waiting gif and change the cursor image.
     */
    public void startWaiting() {
	// On désactive les elements graphiques
	DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
	popupWait.center();
    }

    /**
     * Show a waiting gif and change the cursor image.
     */
    public void startWaiting(int left, int top) {
	// On désactive les elements graphiques
	DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
	popupWait.setPopupPosition(left, top);
	popupWait.show();
    }

    /**
     * Hide the waiting gif and put the default cursor.
     */
    public void stopWaiting() {
	DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
	popupWait.hide();
    }

    /* ********************* PROTECTED/PRIVATE METHODS ****************** */

    /**
     * redirect user to set URL
     * 
     * @param url
     */
    protected void redirect(String url) {
	Window.Location.replace(url);
    }

    protected void redirect(String url, String tid, RedirectStatus status) {
	int qpos = url.indexOf('?');
	String params = qpos == -1 ? "?" : "&";
	params += "status=" + status.toString() + "&tid=" + tid;
	this.redirect(url + params);
    }
}
