// Global Variables
var mySimList;		// The list of experiments

/**
 * Initalization functions that are executed when the page loads
 */
 $(document).ready(function ()
{
	var selectTopModel = document.getElementById("selectTopModel");
	var selectConfigModel = document.getElementById("selectConfigModel");
	var runExperimentId = document.getElementById("runExperimentId");
	
	mySimList=SimList().canvas("#SIMLIST").newSimList();
	
    $("#FILES_MODEL_SUBMIT").click(function (event)
	{
        event.preventDefault();
		
        var myForm = $('#FILES_MODEL')[0];
        var myData = new FormData(myForm);
        $("#FILES_MODEL_SUBMIT").prop("disabled", true);
		
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: Addr_CreateExperiment + "?" +Math.random(),
            data: myData,
			dataType: "json",
            processData: false,
            contentType: false,
            cache: false,
            timeout: 6000,
            success: function (jsonResp)
			{
				if(jsonResp.status=="Success")
				{
					ToggleOption_CreateExperiment(false);
					mySimList.remove();
					mySimList=SimList().canvas("#SIMLIST").newSimList();
					$("#STATUS").html("Success: " + jsonResp.data);
				}else if(jsonResp.status=="Error")
				{
					$("#STATUS").html("Error with upload: " + jsonResp.data);
				}				
				$('#MESSAGE').show();
                $("#FILES_MODEL_SUBMIT").prop("disabled", false);
            },
            error: function (errMsg)
			{
				$('#STATUS').html("Error uploading experiment: ", errMsg.responseJSON.message);
				$('#MESSAGE').show();
                $("#FILES_MODEL_SUBMIT").prop("disabled", false);
            }
        });

    });

});

// Functions to handle opening, closing, and displaying menus

/**
 * Opens the main hamburger menu
 */
var bMenu=false;
function OpenMenu()
{
	if(bMenu)
	{
		$("#MENUOPTIONS").slideUp(200);
		bMenu=false;
	}else
	{
		$("#MENUOPTIONS").slideDown(200);
		bMenu=true;
	}
}

/**
 * Used to show the faded background layer.  This prevents interaction with the page if a popup is open.
 * @param  {boolean} bToogleState Show or Hide the faded background layer
 */
function ToggleBlank(bToggleState)
{
	if(bToggleState)
	{
		$(".BLANK").css("display","flex");
	}else
	{
		$(".BLANK").hide();
	}
}

/**
 * Used to show the create experiment popup.
 * @param  {boolean} bToogleState Show or Hide the create experiment popup
 */
function ToggleOption_CreateExperiment(bToggleState)
{
	$("#CREATEEXPERIMENT_ERROR").html("");
	if(bToggleState)
	{
		OpenMenu();
		ToggleBlank(true);
		$("#CREATEEXPERIMENT").show();
	}else
	{
		ToggleBlank(false);
		$("#CREATEEXPERIMENT").hide();
		$('#FILES_MODEL')[0].reset();
	}
}

/**
 * Used to show the run experiment popup.
 * @param  {boolean} bToogleState Show or Hide the run experiment popup
 * @param  {integer} expId Id of the experiment to run
 */
function ToggleOption_RunExperiment(bToggleState,expId)
{
	$('#STATUS').html("");
	$('#MESSAGE').hide();

	runExperimentId.value=expId;
	if(bToggleState)
	{
		ToggleBlank(true);
		$.ajax({
			type: "GET",
			url: Addr_ListExperimentFiles + expId,
			processData: false,
			contentType: false,
			cache: false,
			timeout: 0,
			success: function (jsonResp)
			{
				selectTopModel.options[selectTopModel.options.length] = new Option("None", "None");
				selectConfigModel.options[selectConfigModel.options.length] = new Option("None", "None");
				jsonResp.forEach(function(myFile) {
					selectTopModel.options[selectTopModel.options.length] = new Option(myFile, myFile);
					selectConfigModel.options[selectConfigModel.options.length] = new Option(myFile, myFile);
				});
			},
			error: function (errMsg)
			{
			}
		});			
		$("#RUNEXPERIMENT").show();
	}else
	{
		ToggleBlank(false);
		$("#RUNEXPERIMENT").hide();
		$('#RUN_MODEL')[0].reset();
		selectTopModel.options.length=0;
		selectConfigModel.options.length=0;
	}
}

/**
 * Used to run the experiment.  In the future this can be expanded to add different simulators
 */
function RunExperiment()
{
	RunCadmium(runExperimentId.value,selectTopModel.options[selectTopModel.selectedIndex].value,selectConfigModel.options[selectConfigModel.selectedIndex].value);
	ToggleOption_RunExperiment(false,0);	
}

/**
 * Used to decide what option to execute on which experiment id.
 * @param  {integer} iId Experiment Id
 * @param  {integer} iOpt Option to execute
 */
function ExecuteOption(iOpt,iId)
{
	$('#STATUS').html("");
	$('#MESSAGE').hide();
	
	switch(iOpt)
	{
		case 1:
			RunCadmium(iId);
			break;
		case 3:
			console.log("Display Results on "+iId);
			break;
		case 4:
			DeleteExperiment(iId);
			break;
	}
}

/**
 * Used to run an experiment in Cadmium using a given top and config file.
 * @param  {integer} myId Experiment Id
 * @param  {string} myTop Name of TOP file
 * @param  {string} myConfig Name of config file
 */
function RunCadmium(myId,myTop,myConfig)
{
	$.ajax({
		type: "POST",
		url: Addr_RunCadmium,
		data: {iId: myId, sTop: myTop, sConfig: myConfig},
		timeout: 0,
		success: function (jsonResp)
		{
			if(jsonResp.status=="Success")
			{
				$('#STATUS').html("Success: "+jsonResp.data);
			}else if(jsonResp.status=="Error")
			{
				$('#STATUS').html("Error running experiment "+myId+": "+jsonResp.data);
			}
			$('#MESSAGE').show();
			mySimList.remove();
			mySimList=SimList().canvas("#SIMLIST").newSimList();
		},
		error: function (errMsg)
		{
			$('#STATUS').html("Experiment Error "+myId+": "+errMsg);
			$('#MESSAGE').show();
		}
	});	
}

/**
 * Used to delete an experiment.
 * @param  {integer} iId Experiment Id
 */
function DeleteExperiment(iId)
{
	if(confirm("Are you sure you want to delete this experiment?"))
	{
		$.ajax({
			type: "GET",
			url: Addr_DeleteExperiment + iId,
			processData: false,
			contentType: false,
			cache: false,
			timeout: 6000,
			success: function (jsonResp)
			{
				if(jsonResp.status=="Success")
				{
					$('#STATUS').html("Success: "+jsonResp.data);
				}else if(jsonResp.status=="Error")
				{
					$('#STATUS').html("Error deleting experiment  "+iId+": "+jsonResp.data);
				}
				$('#MESSAGE').show();
				mySimList.remove();
				mySimList=SimList().canvas("#SIMLIST").newSimList();
			},
			error: function (errMsg)
			{
				$('#STATUS').html("Error deleting experiment  "+iId+": "+errMsg.responseJSON.message);
				$('#MESSAGE').show();
			}
		});	
	}
}