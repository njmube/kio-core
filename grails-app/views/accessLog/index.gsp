<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Control de acceso</title>
	<asset:javascript src="datatables/datatables.js"/>
</head>
<body>
	
	<div class="container">
		<div class="row ">
			<div class="col-md-12">
				<div class="well">
					<h3>
						Control de accesso 
					</h3>
				</div>
			</div>
		</div><!-- end row-->

		<div class="row">
			<div class="col-md-12">
				<div class="button-panel">
					<div class="btn-group">
						<input type='text' id="socioField" placeholder="Socio" class="form-control" autocomplete="off" autofocus="autofocus">
					</div>
					<div class="btn-group">
						<input type='text' id="tarjetaField" placeholder="Tarjeta" class="form-control" autocomplete="off" >
					</div>
					<div class="btn-group ">
						<g:link action="index" class="btn btn-default" > Refrescar</g:link>
					</div> <%-- end .btn-group acciones --%>
				</div>
			</div>
		</div><!-- end .row button panel -->
		
		<div class="row">
			<div class="col-md-12">
				<g:render template="grid"/>
			</div>
		</div>


	</div>
	
	
	
</body>
</html>