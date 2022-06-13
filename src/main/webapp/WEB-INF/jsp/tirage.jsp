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
		<h1>Bienvenu sur le site dedie au tirage au sort pour le 5 eme tour de la reunion des amis</h1>
		<p>Attention !!!  Vous ne pouvez voter qu'une seule fois.
		<br> Une fois que vous avez votez, votre resultat est fige et sera envoye a l'ensemble des membres de la reunion a la fin du tirage au sort

		<h2>
		<form id="tirageForm" method="post" action="tire">
			<br>Entrez votre email:				<input type="text" name="email" id="email" class="text" size="50" />
			<br>Entrez votre code confidentiel: <input type="text" name="secureCode" id="otpInput" class="secureCode" size="50" />
			<br>Entrez le nom de votre groupe:  <input type="text" name="company" id="company" class="company" size="50" />
			<div class="bottom_button">
				<button id="tirageButton" class="va_btn" type="button" onclick="submitSignForm();">
					Tirer
				</button>	
			</div>
		</form>
		</h2>
		
		<div id="footer"><a>
			Contactez le lagrangien par email a l'adresse pmekeze@yahoo.fr pour
			plus d'informations</a>
			</div>
		</div>
	</body>
</html>
