/**
 * These functions are used to draw the table on the Sim Manager page to list all the experiments.
 */
function SimList()
{
	var sJSONLoc=Addr_SimLoad;
	var sContentLoc="";
	var sID="idSimListTable";
	
	var mySimListTable;
	var myData;
		
	function Render(){}

	Render.newSimList = function () {				
		mySimListTable = d3.select(sContentLoc).append("TABLE")
			.attr("id",sID)
			.attr("class","SimListTable");

		// Obtains the list from the ManageExperiment service and updates the list
		d3.json(sJSONLoc).then(function(data) {
			myData=data;
			
			Render.update();
		});

				
		return Render;
	};
	
	Render.update = function()
	{
		sColumns=["Date","Name","Options","Comments"];

		var myHeaders=mySimListTable
			.append("THEAD")

		var myContent=mySimListTable
			.append("TBODY");
			
		myHeaders.append("TR")
			.selectAll("TH")
			.data(sColumns).enter()
				.append('th')
				.attr("id",function (myColumn) { return myColumn; })
				.html(function (myColumn) { return myColumn; });
		
		var iRow=0;
		
		if(myData!=null)
		{
			var myRows=myContent.selectAll("TR")
				.data(myData)
				.enter()
				.append("TR")
				.attr("class",function () { iRow = iRow == 0 ? iRow=1 : iRow=0; return iRow==0 ? "A" : "B"; });

			myRows
				.append("TD")
				.text(function(mySimItem){return mySimItem.date;});

			myRows
				.append("TD")
				.text(function(mySimItem){return mySimItem.name;});
				
			// This append adds all the option values to the list
			myRows
				.append("TD")
				.attr("class","Options")
				.html(function(mySimItem){
					tLink="";
					tLink=tLink+"<A href=\"javascript:ToggleOption_RunExperiment(true,"+mySimItem.id+")\"><IMG src='./img/run.svg' height=16 width=16></A>&nbsp;";
					tLink=tLink+"<A href=\""+Addr_DownloadLogs+mySimItem.id+"\" target=\"_blank\" \><IMG src='./img/download.svg' height=16 width=16></A>&nbsp;";
					tLink=tLink+"<A href=\""+Addr_SimVis+mySimItem.id+"\" target=\"_blank\" \><IMG src='./img/view.svg' height=16 width=16></A>&nbsp;";
					tLink=tLink+"<A href=\"javascript:ExecuteOption(4,"+mySimItem.id+")\"><IMG src='./img/delete.svg' height=16 width=16></A>&nbsp;";
					return tLink;
				});

			myRows
				.append("TD")
				.text(function(mySimItem){return mySimItem.comment;});
				
		}else
		{
			mySimListTable
				.text("Error loading Sim List.");
		}
		
		return Render;
	}
	
	Render.remove = function()
	{
		d3.select("#"+sID).remove();
		return null;
	}
	
	Render.data = function(sValue) {
		if(!arguments.length) return sJSONLoc;
		sJSONLoc=sValue;
		return Render;
	};

	Render.canvas = function(sValue) {
		if(!arguments.length) return sContentLoc;
		sContentLoc=sValue;
		return Render;
	};

	Render.id = function(sValue) {
		if(!arguments.length) return sID;
		sID=sValue;
		return Render;
	};
	
	return Render;
}