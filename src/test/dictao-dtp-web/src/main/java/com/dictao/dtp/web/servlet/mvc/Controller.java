package com.dictao.dtp.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dictao.dtp.core.transactions.TransactionHandler;


public interface Controller<T extends TransactionHandler> {

    ModelAndView handle(String aid, String path, HttpServletRequest req,
            HttpServletResponse resp);

    void setTransactionHandler(T txHandler);
}
