<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Cfdi's</title>
</head>
<body>
	<div class="container">

		<!-- end .row 1 Header -->
		<div class="row">
			<div class="col-md-12">
				<div class="alert alert-info">
					<h2>Comprobantes fiscales digitales CFDIs</h2>
					<g:if test="${flash.message}">
	                    <div class="">
	                        <span class="label label-warning">${flash.message}</span>
	                    </div>
                	</g:if> 
				</div>
			</div>
		</div><!-- end .row -->
		
		<div class="row">
			<div class="col-md-12">
				<div class="btn-group">
					<g:link action="index" class="btn btn-default ">
						<span class="glyphicon glyphicon-repeat"></span> Refrescar
					</g:link>
		
				</div>

				<div class="btn-group">
					<div class="input-group ">
						<div class="input-group-addon"><span class="glyphicon glyphicon-search"></span></div>
						<input type="text" class="form-control" placeholder="Buscar" autofocus="autofocus">
					</div>
				</div>
			</div>
		</div> <!-- end .row 2-->
		
		<div class="row">
			<div class="col-md-12">
				<g:render template="grid"/>
			</div>
		</div>

	</div>
	
</body>
</html>