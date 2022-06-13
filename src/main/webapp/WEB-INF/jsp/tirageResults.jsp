<!docTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
	
	 <head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
        <title>Tirage au sort reunion</title>

        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/${it.ressources}/common.css" />
		 <script type="text/javascript">
			function submitSignForm() {
                document.forms["tirageForm"].submit();
            }
		</script>
	</head>
	<body>
		<div id="mainContainer">
		<div id="header">
           <div class="titre">Tirage au sort de la Reunion</div>
         </div>
		<h1>Merci d'avoir participe au tirage au sort</h1>
		<h2> Voici le resultat de tout ceux qui ont participe au tirage : </h2>
		<br/>
		<table id="tableResultId" align="center" style="width:50%">
			<tr><th class="tableHead">Adresse email</th> <th class="tableHead" >Numéro de passage</th></tr>
		  <c:forEach items="${it.results}" var="result" varStatus="count">
			<tr> <td>${result.email}</td> <td>${result.numero}</td> </tr>
		</c:forEach>
		</table>
		<br>
		<h2>Merci et a bientot </h2>
		
		
		<div id="footer"><a>
			Contactez le lagrangien par email a l'adresse pmekeze@yahoo.fr pour
			plus d'informations</a>
			</div>
		</div>
	</body>
</html>