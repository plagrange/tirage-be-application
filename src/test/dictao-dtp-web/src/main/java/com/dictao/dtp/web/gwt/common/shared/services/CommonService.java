package com.dictao.dtp.web.gwt.common.shared.services;

import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface CommonService extends RemoteService {

    void back(String accessID) throws RedirectException;
    
    String getMessage(String message, String accessID) throws RedirectException;

    void seal(String accessID) throws RedirectException;

    void finish(String accessID) throws RedirectException;
}