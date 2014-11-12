<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Alta de nota</title>
	<asset:stylesheet src="jquery-ui.css"/>
	<asset:javascript src="jquery-ui/autocomplete.js"/>
	<asset:javascript src="forms/autoNumeric.js"/>
</head>
<body>
	<div class="container">

		<div class="row">
			<div class="col-md-12">
				<div class="alert alert-info">
					<h2> 
						Alta de Nota de crédito
					</h2>
					<g:if test="${flash.message}">
						<span class="label label-warning">${flash.message}</span>
					</g:if> 
				</div>
			</div>
			<g:hasErrors bean="${notaDeCreditoInstance}">
				<div class="alert alert-danger">
					<g:renderErrors bean="${notaDeCreditoInstance}" as="list" />
				</div>
			</g:hasErrors>
		</div><!-- end .row -->
		
		<div class="row">
			<div class="col-md-2">
        		<div class="list-group">
        			<g:link action="index" class="list-group-item">
        				<i class="fa fa-tasks fa-fw fa-2x"></i>&nbsp;  Regresar
        			</g:link>
        		</div>
			</div>
			
			<div class="col-md-8">
				<g:form class="form-horizontal " action="save" name="notaForm" >

					<div class="form-group ${hasErrors(bean:notaDeCreditoInstance,field:'cliente', 'has-error')}">
						<label for="cliente" class="col-sm-4 control-label">Cliente</label>
						<div class="col-sm-8">
							<g:hiddenField id="clienteId" name="cliente.id" value="${notaDeCreditoInstance?.cliente?.id}"/>
							<input id="cliente" name="cliente.nombre" type="text" class="form-control"
							  autocomplete="off"
							  value="${notaDeCreditoInstance?.cliente?.nombre}">
						</div>
					</div>
					<f:with bean="${notaDeCreditoInstance}">
						<f:field property="tipo" input-class="form-control" cols="col-sm-8" colsLabel="col-sm-4"/>
						<f:field property="comentario" input-class="form-control" cols="col-sm-8" colsLabel="col-sm-4"/>
					</f:with>
					<div class="form-group">
						<div class="buttons  col-md-offset-4  col-md-4">
							<g:submitButton name="Generar" class="btn btn-primary btn-lg btn-block" value="Aceptar"/>
							%{-- <button id="cobrarBtn" class="btn btn-primary btn-lg btn-block">Cobrar</button>	 --}%
						</div>
					</div>
				</g:form>
			</div>

		</div><!-- end .row2 -->

	</div>

	<script type="text/javascript">
		$(document).ready(function(){
			//$("#importe").autoNumeric({wEmpty:'zero',mRound:'B',aSign: '$'});
			$("#fecha").datepicker({
	     
	 		});

			$('form[name=notaForm]').submit(function(e){
	    		$(this).children('input[type=submit]').attr('disabled', 'disabled');
	    		console.log("Desablidatndo submit button....");
	    		//$("#Cobrar").attr('disabled','disabled');
	    		var button=$("#Generar");
	    		button.attr('disabled','disabled').attr('value','Procesando...');
	    		// this is just for demonstration
	    		//e.preventDefault(); 
	    		return true;
			});

			$("#cliente").autocomplete({
				source:'<g:createLink controller="cliente" action="getClientesJSON"/>',
				minLength:3,
				select:function(e,ui){
					console.log('Cliente seleccionado: '+ui.item.value);
					$("#clienteId").val(ui.item.id);
					$("#cliente").val(ui.item.cliente.nombre);
					$("#socioId").val(null);
					$("#socio").val(null);
					
				}
			});

			// $("#tipoDeNota").change(function(){
			// 	var text = "";
			// 	   $(this).children("option:selected").each(function() {
			// 	      text += $( this ).text() ;
			// 	});
			// 	if(text==='BONIFICACION'){
			// 		$("#importe").attr('disabled','disabled');
			// 		//$("#referencia").attr('required','required');
			// 	}else{
			// 		$("#importe").removeAttr('disabled');
			// 		$("#importe").attr('required','required');
					
			// 	}
			// });

		});
	</script>	
	
</body>
</html>