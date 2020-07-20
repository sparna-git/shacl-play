				<table id="resultTable" class="table table-striped">
					<thead>
						<tr>
							<th><font>Focus Node</font></th>
							<th><font>Message</font></th>
							<th><font>Path</font></th>
							<th><font>Severity</font></th>
							<th><font>Constraint Component</font></th>
							<th><font>Source Shape</font></th>
							<th><font>Value</font></th>
						</tr>
					</thead>
					<tbody>	
							<#list data as result>
								<tr >
									<td><a class="link" href="${result.focusNode}" target="_blank">${result.focusNode}</a></td>
									<td>${result.message}</td>
									<td>${result.resultPath}</td>
									<td>${result.resultSeverity}</td>
									<td>${result.sourceConstraintComponent}</td>
									<td>
										${result.sourceShape}
									</td>
									<td>
										${result.value}
									</td>
								</tr>
							</#list>
					</tbody>
				</table>