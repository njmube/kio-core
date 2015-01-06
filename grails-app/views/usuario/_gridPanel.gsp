
<table id="usuariosGrid" class="table table-striped table-bordered table-condensed">
	<thead>
		<tr>
			<th>UseName</th>
			<th>Nombre</th>
			<th>Número</th>
			<th>Tarjeta</th>
			<th>Activo</th>
			
		</tr>
	</thead>
	<tbody>
		<g:each in="${usuarioInstanceList}" var="row">
			<tr>
				<td>
					<g:link action="show" id="${row.id}">
						${row.username}
					</g:link>
				</td>
				<td>
					<g:link action="show" id="${row.id}">
						${row.nombre}
					</g:link>
				</td>
				
				<td>${row.tarjeta}</td>
				<td>${row.numeroDeEmpleado}</td>
				<td>
					<g:if test="${row.enabled}">
						<i class="fa fa-check"></i>
					</g:if>
					<g:else>
						<i class="fa fa-toggle-off"></i>
					</g:else>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<div class="pagination">
	<g:paginate total="${cfdiInstanceCount ?: 0}" />
</div>

