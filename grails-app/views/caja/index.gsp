<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="layout" content="pago"/>
	<title>Caja</title>
	<asset:stylesheet src="datatables/dataTables.css"/>
	<asset:javascript src="datatables/dataTables.js"/> 
</head>
<body>

<content tag="header">
	<h3>Caja - Ventas pendiente de cobro</h3>
	%{-- <nav:set path="user/operaciones/caja/pagos"/> --}%
</content>
<content tag="operaciones">
	<li>
	    <g:link action="create" >
	        <i class="fa fa-plus"></i> Nuevo
	    </g:link>
	    <g:link action="corte" >
	        <i class="fa fa-bookmark-o"></i> Corte
	    </g:link>
	</li>
</content>
<content tag="document">
	<table id="cajaTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>Venta</th>
				<th>Cliente</th>
				
				<th>Rfc</th>
				<th>Status</th>
				<th>Tipo</th>
				<th>Fecha</th>
				<th>Importe</th>
				<th>Impuesto</th>
				<th>Total</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${ventaInstanceList}" var="row">
				<tr id="${row.id}">
					<td >
						<g:link  action="${row.status=='VENTA'?'cobrar':'show'}" id="${row.id}">
							${fieldValue(bean:row,field:"id")}
						<span class="glyphicon glyphicon-shopping-cart"></span> 
						</g:link>
					</td>
					<td>
						<g:link action="${row.status=='VENTA'?'cobrar':'show'}" id="${row.id}">
							${fieldValue(bean:row,field:"cliente.nombre")}
						</g:link>
						
					</td>
					
					<td>${fieldValue(bean:row,field:"cliente.rfc")}</td>
					<td>${fieldValue(bean:row,field:"status")}</td>
					<td>${fieldValue(bean:row,field:"tipo")}</td>
					<td><g:formatDate date="${row.fecha}" format="dd/MM/yyyy"/></td>
					<td><g:formatNumber number="${row.importe}" type="currency"/></td>
					<td><g:formatNumber number="${row.impuesto}" type="currency"/></td>
					<td><g:formatNumber number="${row.total}" type="currency"/></td>
				</tr>
			</g:each>
		</tbody>
	</table>
</content>

<content tag="javascript">
	<script type="text/javascript">
		$(document).ready(function(){
			
			$('#cajaTable').dataTable( {
	        	"paging":   false,
	        	"ordering": false,
	        	"info":     false
	        	,"dom": '<"toolbar col-md-4">rt<"bottom"lp>'
	    	} );

	    	var table = $('#cajaTable').DataTable();
	    	
	    	
	    	$("#filterField").on('keyup',function(e){
	    		$('#cajaTable').DataTable().search($(this).val()).draw();
	    	});
	    	

		});
	</script>
</content>



	

	
</body>
</html>