package com.dictao.dtp.web.rest.secure;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;

import com.dictao.dtp.web.rest.AbstractEchoCtrl;

@RequestScoped
@Path("/echo/v2012_07")
public class SecureEchoCtrl extends AbstractEchoCtrl {

}
