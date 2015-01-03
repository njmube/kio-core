<%@page expressionCodec="none" %>
<f:with bean="${socioInstance}">
	<br/>
	
	<div class="row">
		<div class="col-md-6">
			<g:hiddenField id="clienteId" name="cliente.id" value="${socioInstance?.cliente?.id}"/>
			%{-- <f:field property="cliente.id" 
				input-class="form-control"
				input-disabled="disabled" 
				input-type="text"
				colsLabel="col-md-4" cols="col-md-8"/> --}%
			<f:field property="cliente" 
				input-class="form-control"
				input-disabled="disabled" 
				input-type="text"
				colsLabel="col-md-4" cols="col-md-8"/>
			
			
		</div>
		<div class="col-md-6">
			<f:field property="cliente.rfc" 
				input-class="form-control"
				input-disabled="disabled" 
				input-type="text"
				colsLabel="col-md-4" cols="col-md-8"/>
			<f:field property="cfdiEmail" 
				input-autocomplete="off"
				input-class="form-control " 
				colsLabel="col-md-4" cols="col-md-8"
				/>
		</div>
		%{-- <g:render template="domicilioFiscal"/> --}%

	</div>
	<div class="row">
		<g:render template="domicilioFiscal"/>
	</div>
	
		
</f:with>


	
	
	
