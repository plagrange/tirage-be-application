<!docTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
	 <head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <title>Tirage au sort</title>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.ressources}/common.css" />
	</head>
	<body>
		<div id="mainContainer">
			<div id="header">
			   <div class="titre">Creation d'un tirage au sort pour une groupe</div>
			</div>
			<h1>Votre tirage au sort a été créé avec succès.</h1>
			
			<p>Vous pouvez dès maintenant informer les participants</p>
			
			<h2>Merci et a bientot </h2>
			
			<div id="footer">
				Contactez le lagrangien par email a l'adresse <a>pmekeze@yahoo.fr</a> pour
				plus d'informations
			</div>
		</div>
	</body>
</html>