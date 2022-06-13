<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.dictao.dtp.web.data.RedirectStatus"%>
<html>
    <head>
        <title>Erreur</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style type="text/css">
            <!--
            .Style1 {
                color: #003399;
                font-size: 24px;
                font-family: Arial, Helvetica, sans-serif;
                padding-left:10px;
                border-bottom:1px #abb5e3 solid;
            }
            .Style2 {
                color: #003399;
                font-size: 18px;
                font-family: Arial, Helvetica, sans-serif;
                padding-left:25px;
            }
            -->
        </style>

        <%

                    String status = request.getParameter("status");
                    String errorTitle;
                    String errorMessage;
                    RedirectStatus statusCode = RedirectStatus.INTERNAL_ERROR;

                    try {
                        statusCode = RedirectStatus.fromString(status);
                    } catch (Throwable ex) {
                         // error, statusCode already init with INTERNAL_ERROR
                    }

                    switch (statusCode) {

                        case SESSION_EXPIRED: // SESSION_EXPIRED
                            errorTitle = "Erreur de traitement";
                            errorMessage = "Votre session a expir&#233; en raison d'inactivit&#233;.";
                            break;

                        case USER_ERROR: // USER_ERROR
                            errorTitle = "Erreur de traitement";
                            errorMessage = "Les param&#232;tres utilisateur sont invalides.";
                            break;

                        case ENVIRONMENT_ERROR: // ENVIRONMENT_ERROR
                            errorTitle = "Erreur d'environnement";
                            errorMessage = "Le serveur a rencontr&#233; une condition inattendue. Veuillez contacter votre administrateur.";
                            break;

                        case INTERNAL_ERROR: // INTERNAL_ERROR
                        default:
                            errorTitle = "Erreur interne";
                            errorMessage = "Le serveur a rencontr&#233; une condition inattendue. Veuillez contacter votre administrateur.";
                            break;
                    }
        %>

    </head>

    <body>
        <p class="Style1"><% out.print(errorTitle);%></p>
        <p>&nbsp;</p>
        <p class="Style2"><% out.print(errorMessage);%></p>
    </body>

</html>
