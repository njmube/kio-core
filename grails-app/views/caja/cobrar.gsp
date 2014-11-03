<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="layout" content="venta"/>
	<title>Cobrando  ${ventaInstance.id}</title>
	
	<asset:stylesheet src="jquery-ui.css"/>
	<asset:javascript src="jquery-ui/autocomplete.js"/>
	<asset:javascript src="forms/autoNumeric.js"/>
</head>
<body>

<content tag="header">
	Cobrando venta ${ventaInstance.id}
</content>

<content tag="info">
</content>

<content tag="body">
	
	<div class="col-md-12">
		<g:hasErrors bean="${ventaInstance}">
			<div class="aler alert-danger">
				<g:renderErrors bean="${ventaInstance}" as="list" />
			</div>
		</g:hasErrors>
	</div>
	
	<div class="col-md-8">
		<g:form name="cobrarForm" action="update" class="form-horizontal" id="${ventaInstance.id}">
			
			<div class="form-group">
				<label class="col-sm-4 control-label">Cliente</label>
			    <div class="col-sm-8">
			      <p class="form-control-static">${ventaInstance?.cliente}</p>
			    </div>
			 </div>


			<f:with bean="${cobroInstance}">
				<f:field property="formaDePago" input-class="form-control" cols="col-sm-8" colsLabel="col-sm-4" />
			</f:with>

			<div class="form-group">
				<label class="col-sm-3 control-label">Venta</label>
			    <div class="col-sm-3">
			      <p class="form-control-static">${ventaInstance?.id}</p>
			    </div>
			    <label class="col-sm-3 control-label">Fecha</label>
			    <div class="col-sm-3">
			      <p class="form-control-static"><g:formatDate date="${ventaInstance?.fecha}" format="dd/MM/yyyy"/></p>
			    </div>
			 </div>

		</g:form>
	</div>		
	<div class=" col-md-4">
		<form class="form-horizontal">
		<fieldset disabled>
		<f:with bean="${ventaInstance}">
			<f:field property="subTotal" input-class="form-control money-data text-right" 
			input-type="text" 
			cols="col-sm-6" colsLabel="col-sm-4" label="Sub Total"/>
			
			<f:field property="impuesto" input-class="form-control money-data text-right" input-type="text" 
			cols="col-sm-6" colsLabel="col-sm-4" label="IVA"/>
			
			<f:field property="total" input-class="form-control money-data text-right" input-type="text" input-id="total"
			cols="col-sm-6" colsLabel="col-sm-4" label="Total"
			/>
			<div class="form-group">
			    <label class="col-sm-4 control-label">Saldo</label>
			    <div class="col-sm-4">
			      <p class="form-control-static">${g.formatNumber(number:ventaInstance.getSaldo(),type:'currency')}</p>
			    </div>
			</div>
		</f:with>	
		</fieldset>
		</form>
	</div>
</content>

<content tag="footer">
	<div class="btn-group">
  							
		<g:link class="btn btn-default btn-sm" action="index" >
			Regresar
		</g:link>

		<g:link class="btn btn-default btn-sm" 
			action="facturar" id="${ventaInstance.id}" >
			<span class="glyphicon glyphicon-qrcode"></span> Facturar 
		</g:link>
		
		<button id="next" class="btn btn-primary btn-sm">
			<span class="glyphicon glyphicon-check"></span> Pagar
		</button>

		

		<g:if test="${ventaInstance.cfdi==null}">
			<g:link class="btn btn-default btn-sm" controller="venta"
				action="cancelar" id="${ventaInstance.id}" 
				onclick="return confirm('Cancelar la venta y regresarla a pedidos');">
				<span class="glyphicon glyphicon-remove"></span> Cancelar
			</g:link>
		</g:if>

		<g:if test="${ventaInstance.cfdi==null}">
			<g:link class="btn btn-danger btn-sm" controller="venta"
				action="delete" id="${ventaInstance.id}" 
				onclick="return confirm('Eliminar la venta');">
				<span class="glyphicon glyphicon-trash"></span> Eliminar
			</g:link>
		</g:if>

		<g:else>
			<g:link class="btn btn-default btn-sm" 
				controller="cfdi" action="enviar" id="${ventaInstance.id}" >
				<span class="glyphicon glyphicon-envelope"></span> Enviar
			</g:link>
		</g:else>
		

  	</div>
</content>

<content tag="js">

<script type="text/javascript">
$(document).ready(function(){
	$("#fecha").datepicker({
	     
	 });
	$(".money-data").autoNumeric({wEmpty:'zero',mRound:'B',aSign: '$'});
	
	
	
});
</script>

</content>	

	

</body>
</html>