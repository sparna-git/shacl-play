				<table id="resultTable" class="table table-striped" >
					<thead>
						<tr>
							<th><font>Source Shape</font></th>
							<th><font>Constraint Component</font></th>
							<th><font>Severity</font></th>
							<th><font>Path</font></th>
							<th><font>Message</font></th>
							<th><font>Count</font></th>
							<th><font>Sample Focus Node</font></th>
							<th><font>Sample Value</font></th>
						</tr>
					</thead>
					<tbody>	
							<#list data as result>
								<tr >
									<td>${result.sourceShape}</td>
									<td>${result.sourceConstraintComponent}</td>
									<td>${result.resultSeverity}</td>
									<td>${result.resultPath}</td>
									<td>${result.message}</td>
									<td>${result.count?c}</td>
									<td>${result.sampleFocusNode}</td>
									<td>${result.sampleValue}</td>
								</tr>
							</#list>
					</tbody>
				</table>
