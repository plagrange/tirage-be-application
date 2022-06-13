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
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.1/jquery.min.js"></script> 
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.7/angular.min.js"></script>
		<script type="text/javascript">
			var nombre=2;
			var tableUsers = new Array();
			function submitSignForm() {
				document.forms["tirageForm"].submit();
			}
			$(function () {
				$("#add_line").click(function (e) {
					
					var last_row = $('#matable tr:last');
					var new_id = parseInt($(last_row).attr('id').split("_")[1]) + 1;
					$(last_row).clone().insertAfter('#matable tr:last').attr('id', 'row_' + new_id);
					$('#matable tr:last td:first input').attr('id', 'name_' + new_id);
					$('#matable tr:last td:last input').attr('id', 'criteria_' + new_id);
					nombre = nombre + 1;
				});
			});
			$(function () {
				$("#remove_line").click(function (e) {
					if(nombre>1){
						var last_row = $('#matable tr:last');
						var new_id = parseInt($(last_row).attr('id').split("_")[1]) + 1;
						$(last_row).remove();
						nombre = nombre - 1;
					}
				});
			});
				
			function buildLoginTab(n){
				for(i=1;i<=n;i++){
					var name = document.getElementById("name_"+i).value;
					var secureCode = document.getElementById("criteria_"+i).value;
					var user = "{email:"+name+", secureCode:"+secureCode+"}";
					var numero = i-1;
					tableUsers[numero]=user;
				}
			}
			
			function submitSignForm() {
				buildLoginTab(nombre);
				var userList = tableUsers.toString();
				userList = "{initUserList : [" + userList + "]}";
				document.getElementById("usersId").value=userList;
				document.forms["listForm"].submit();
			}
		</script>
	</head>
	<body>
		<div id="mainContainer">
		<div id="header">
           <div class="titre">Creation d'un tirage au sort pour une groupe</div>
        </div>
		<h1>Bienvenue sur cette page qui vous permet de créer un tirage au sort.</h1>
		<h2> Le mode opérationnel est simple. Remplir les champs cidessous et cliquer sur le bouton envoyer</h2>
		<p>Nous vous conseillons d'utiliser les adresses emails pour identifier les participants, <br>ainsi, les mails automatiques seront envoyer au participants pour les inviter à participer au tirage au sort.</p>
		<p>Les boutons "ajouter une ligne" et "supprimer une ligne" vous permet d'ajouter ou de supprimer des lignes</p>
		<form id="listForm" method="POST" action="createtirage" >
			Nom du groupe <input type="text" name="company"/>
			<input id="usersId" type="hidden" name="users"/>
			<table id="matable" align="center" style="width:50%" border="1">
				<tr id="tabUserHeader">
					<td class="tableHead">Adresse email</td>
					<td class="tableHead">Code de vérification</td>
				</tr>
				<tr id="row_1">
					<td><input id="name_1" type="text" name="login"></td>
					<td><input id="criteria_1" type="text" name="secureCode"></td>
				</tr>
				<tr id="row_2">
					<td><input id="name_2" type="text" name="login"></td>
					<td><input id="criteria_2" type="text" name="secureCode"></td>
				</tr>
			</table>
		
		</form>
		
		<div id="tabControl" align="center" >
			<button id="add_line">Ajouter une ligne</button>
			<button id="remove_line">Supprimer une ligne</button>
		</div>
		
		<div class="bottom_button">
			<button id="sendButton" class="va_btn" type="button" onclick="submitSignForm();">
				Envoyer
			</button>	
		</div>
			
		<h2>Merci et a bientot </h2>
		
		<div id="footer">
			Contactez le lagrangien par email a l'adresse <a>pmekeze@yahoo.fr</a> pour
			plus d'informations
			</div>
		</div>
	</body>
</html>