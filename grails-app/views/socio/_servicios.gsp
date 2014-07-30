<div class="row">
	<div id="tablePanel" class="">
		<g:render template="serviciosGrid" model="[servicios:socioInstance.servicios]"/>
	</div>
</div> <!-- end .row 1 -->


<div class="col-md-12">
	<formset>
		<legend>Alta de servicio</legend>
		<form id="servicioForm" class="form-horizontal" >
			<g:hiddenField id="socio" name="socio.id" value="${socioInstance.id}"/>
			<div class="form-group">
				<label class="col-sm-2 control-label" for="servicioField">Servicio</label>
				<div class="col-sm-8">
					<g:hiddenField id="producto" name="producto.id"/>
					<input id="servicioField" type="text" class="form-control" required="required" autocomplete="off">
				</div>
			</div>
			<div class="form-group">
				<label for="percioBruto" class="col-sm-2 control-label ">Precio</label>
				<div class="col-sm-3">
					<input name="precioBruto" id="precioBruto" type="text" class="form-control moneda-field" required="required" autocomplete="off">
				</div>
				<label for="descuento" class="col-sm-2 control-label">Descuento</label>
				<div class="col-sm-3">
					<input name="descuento" id="descuento" type="text" class="form-control" data-porcentaje="true" autocomplete="off">
				</div>
			</div>
			<%--<div class="form-group">
				<label class="col-sm-2 control-label" for="precioNeto"> Neto:</label>
				<div class="col-sm-4">
					<input id="precioNeto" class="form-control" type="text">
				</div>
			</div>
			--%>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-4">
      				<button type="submit" class="btn btn-default">Agregar</button>
    			</div>
			</div>
		</form>
	</formset>
</div>

<script type="text/javascript">
$(document).ready(function(){
	$("#servicioField").autocomplete({
		source:'/kio-core/producto/getProductosAsJSON',
		minLength:3,
		select:function(e,ui){
			console.log('Producto seleccionado: '+ui.item.value);
			$("#producto").val(ui.item.id);
			$("#precioBruto").autoNumeric('set', ui.item.precioBruto);
			$("#descuento").autoNumeric('set', ui.item.descuento);
		}
	});

	$(".moneda-field").autoNumeric({wEmpty:'zero',mRound:'B',aSign: ''});
	$("input[data-porcentaje]").autoNumeric({altDec: '%', vMax: '99.99'});

	$('#servicioForm').submit(function(e){
		var postData = $(this).serializeArray();
		$.ajax({
			url:'<g:createLink controller="servicioPorSocio" action="agregarServicioREST"/>',
			type: "POST",
			data : postData,
			success:function(data, textStatus, jqXHR) 
	        {
	            console.log('Servicio agregado: '+data);
	            $("#tablePanel").html(data);

	        },
	        error: function(jqXHR, textStatus, errorThrown) 
	        {
		        console.log('Error al agregar servicio..."')
	        }
			
		});
		e.preventDefault(); //STOP default action
	    //e.unbind(); //unbind. to stop multiple form submit.
	});
});
</script>


