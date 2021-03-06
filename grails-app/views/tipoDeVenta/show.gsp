<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="layout" content="catalogos_show"/>
	<title>Tipo de Venta</title>
</head>
<body>

	<content tag="header">
		<h3>Tipo de Venta</h3>
	</content>
	
	<content tag="beanId">${tipoDeVentaInstance.id}</content>
	
	<content tag="form">
		<g:hasErrors bean="${tipoDeVentaInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${tipoDeVentaInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
		</g:hasErrors>
		
		<form class="form-horizontal"  >
			
			<fieldset disabled="disabled">
				<legend>Tipo de Venta</legend>
				<f:with bean="${tipoDeVentaInstance}">
					<f:field property="clave" input-required="required" input-class="form-control" cols="col-sm-5"/>
					<f:field property="descripcion" input-required="required" input-class="form-control" cols="col-sm-5"/>
				</f:with>
				
			</fieldset>
			
			<div class="form-group">
<%--				<div class="buttons col-md-offset-2 col-md-2">--%>
<%--					<g:link action="edit" class="btn btn-default " id="${tipoDeVentaInstance.id }" >--%>
<%--						<span class="glyphicon glyphicon-floppy-save"></span> Editar--%>
<%--					</g:link>--%>
<%--				</div>--%>
			</div>
			
		</form>
		
	</content>
	
</body>
</html>