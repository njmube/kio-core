<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	
	<title>Venta (${ventaInstance.id})</title>
	
</head>
<body>
	<div class="container">

		<div class="row">
			<div class="col-md-12">
				<div class="alert alert-success">
					<h2> 
						<i class="fa fa-shopping-cart  fa-2x"></i> Venta ${ventaInstance.id} : ${ventaInstance.cliente}
					</h2>
					<g:if test="${flash.message}">
						<span class="label label-warning">${flash.message}</span>
					</g:if> 
				</div>
			</div>
			<g:hasErrors bean="${cobroInstance}">
				<div class="alert alert-danger">
					<g:renderErrors bean="${cobroInstance}" as="list" />
				</div>
			</g:hasErrors>
		</div><!-- end .row -->
		
		<div class="row">
			<div class="col-md-3">
        		<div class="list-group">

        			<g:link controller="pago" action="index" class="list-group-item">
        				<i class="fa fa-tasks fa-fw fa-2x"></i>&nbsp;  Pagos
        			</g:link>
        			
        			<g:if test="${!ventaInstance.cfdi}">
        				<g:link action="deleteVenta" class="list-group-item" onclick="return confirm('Eliminar la venta?');"
        					id="${ventaInstance.id}">
        					<i class="fa fa-trash fa-fw fa-2x"></i>&nbsp;  Eliminar
        				</g:link>
        				<g:link action="facturar" class="list-group-item" onclick="return confirm('Facturar la venta ventaInstance.id?');"
        					id="${ventaInstance.id}">
        					<i class="fa fa-file-pdf-o fa-fw fa-2x"></i>&nbsp;  Facturar
        				</g:link>
        			</g:if>
        			<g:else>
        				<g:link action="cancelarVentaFacturada" controller="cfdi"
        					class="list-group-item" 
        					onclick="return confirm('Cancelar venta facturada ?');"
        					id="${ventaInstance.id}">
        					<i class="fa fa-trash fa-fw fa-2x"></i>&nbsp;  Cancelar 
        				</g:link>
        			</g:else>
        			
        		</div>
			</div>
			
			<div class="col-md-6 ">
				<fieldset disabled>
					
				
				<g:form class="form-horizontal pull-left center-block text-right"  name="pagoForm">
					<div class="form-group">
						<label class="col-sm-4 control-label">Fecha venta</label>
					    <div class="col-sm-8">
					      <p class="form-control-static"><g:formatDate date="${ventaInstance.fecha}" format="dd/MM/yyyy"/></p>
					    </div>
					</div>
					<div class="form-group">
						<label class="col-sm-4 control-label">Tipo</label>
					    <div class="col-sm-8">
					      <p class="form-control-static">${ventaInstance.tipo}</p>
					    </div>
					</div>
					
				</g:form>
				</fieldset>
			</div>

			<div class="col-md-3 well">

				<form class="form-horizontal totales-form text-right">
					<div class="form-group">
					    <label class="col-sm-4 control-label">Importe</label>
					    <div class="col-sm-8">
					    	<p class="form-control-static">
					      		<g:formatNumber number="${ventaInstance.importe}" type="currency"/>
					    	</p>
					    </div>
					</div>
					<div class="form-group">
					    <label class="col-sm-4 control-label">Descuento</label>
					    <div class="col-sm-8">
					    	<p class="form-control-static">
					      		<g:formatNumber number="${ventaInstance.descuento}" type="currency"/>
					    	</p>
					    </div>
					</div>
					<div class="form-group">
					    <label class="col-sm-4 control-label">Sub Total</label>
					    <div class="col-sm-8">
					    	<p class="form-control-static">
					      		<g:formatNumber number="${ventaInstance.subTotal}" type="currency"/>
					    	</p>
					    </div>
					</div>
					<div class="form-group">
					    <label class="col-sm-4 control-label">Impuesto</label>
					    <div class="col-sm-8">
					    	<p class="form-control-static">
					      		<g:formatNumber number="${ventaInstance.impuesto}" type="currency"/>
					    	</p>
					    </div>
					</div>
					<div class="form-group">
					    <label class="col-sm-4 control-label">Total</label>
					    <div class="col-sm-8">
					    	<p class="form-control-static">
					      		<g:formatNumber number="${ventaInstance.total}" type="currency"/>
					    	</p>
					    </div>
					</div>
					
				</form>
			</div>

		</div><!-- end .row2 -->

		<div class="row">
			<div class="col-md-12">
				<table id="partidasTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th>Socio</th>
							<th>Producto</th>
							<th>Descripcion</th>
							<th>Unidad</th>
							<th>Cantidad</th>
							<th>Precio</th>
							<th>Importe</th>
							<th>Descuento</th>
							<th>Total</th>
							
						</tr>
					</thead>
					<tbody>

						<g:each in="${ventaInstance.partidas}" var="row" status="i"> 
							<tr>
								<td>
									<g:if test="${!row.venta.cfdi}">
										<g:link controller="ventaDet" action="edit" id="${row.id}">
											<g:formatNumber number="${row.id}" format='####'/>
										</g:link>
									</g:if>
									<g:else>
										<g:formatNumber number="${row.id}" format='####'/>
									</g:else>
								</td>
								<td>
									%{-- <g:link controller="ventaDet" action="edit" id="${row.id}">${row.producto.clave}</g:link> --}%
									<g:if test="${!row.venta.cfdi}">
										<g:link controller="ventaDet" action="edit" id="${row.id}">
											${row.producto.clave}
										</g:link>
									</g:if>
									<g:else>
										${row.producto.clave}
									</g:else>
								</td>
								<td>${row.producto.descripcion}</td>
								<td>${row.producto.unidad}</td>
								<td>${row.cantidad}</td>
								<td >
									<g:formatNumber number="${row.precio}" type="currency"/>
								</td>
								<td>
									<g:formatNumber number="${row.importeBruto}" type="currency"/>
								</td>
								<td>
									<g:formatNumber number="${row.descuento}" type="currency"/>
								</td>
								
								<td>
									<g:formatNumber number="${row.importeNeto}" type="currency"/>
								</td>
								
							</tr>
						</g:each>
					</tbody>
				</table>

			</div>
		</div>

	</div>
	
	
</body>
</html>