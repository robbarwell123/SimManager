<HTML>
	<HEAD>
		<TITLE>Sim Manager v1.0</TITLE>
		<LINK rel="stylesheet" href="./css/SimManager.css" />
		<SCRIPT src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></SCRIPT>		
		<SCRIPT src="https://d3js.org/d3.v5.min.js"></SCRIPT>
		<SCRIPT src="./js/config/cfg_simmanager.js"></SCRIPT>
		<SCRIPT src="./js/SimList.js"></SCRIPT>
		<SCRIPT src="./js/SimManager.js"></SCRIPT>
	</HEAD>
	<BODY>
		<DIV class="APP">
			<DIV class="HEADER">
				<DIV class="SUBHEADER">
					<SPAN class="DIVCENTER"><IMG src="./img/menu.svg" height="32px" width="32px" onClick="OpenMenu()">&nbsp;</SPAN>
					<SPAN class="DIVCENTER">Sim Manager v1.0</SPAN>
				</DIV>
			</DIV>
			<DIV id="MENUOPTIONS">
				<UL id="NAVMENU">
					<LI><A href="javascript:ToggleOption_CreateExperiment(true)">Create Experiment</A></LI>
				</UL>
			</DIV>

			<DIV class="CONTENT">
				<DIV id="MESSAGE">
					<IMG src="./img/info.svg" width=32 height=32><SPAN id="STATUS"></SPAN>
				</DIV>
				<DIV id="SIMLIST">
				</DIV>
				<DIV id="FOOTER">
					<BR>
					<DIV>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></DIV>
				</DIV>
			</DIV>
		</DIV>
		<DIV class="BLANK">
			<DIV id="CREATEEXPERIMENT">
				<H3>Create Experiment</H3>
				<DIV id="CREATEEXPERIMENT_ERROR"></DIV>
				<BR>
				<FORM id="FILES_MODEL">
					Experiment Name: <INPUT type="text" id="sExperimentName" name="sExperimentName" /><BR><BR>
					Files: <INPUT id="fExperimentFiles" type="file" name="XMLModels" multiple required /><BR><BR>
					<INPUT id="FILES_MODEL_SUBMIT" type="submit" value="Upload"> &nbsp; <INPUT type="button" value="Cancel" onClick="ToggleOption_CreateExperiment(false)">
				</FORM>
			</DIV>
			<DIV id="RUNEXPERIMENT">
				<H3>Run Experiment</H3>
				<BR>
				<FORM id="RUN_MODEL">
					Top Model File: <SELECT id="selectTopModel"></SELECT> <BR><BR>
					Config Model File: <SELECT id="selectConfigModel"></SELECT> <BR><BR>
					<INPUT id="RUN_MODEL_SUBMIT" type="button" value="Run" onClick="RunExperiment()"> &nbsp; <INPUT type="button" value="Cancel" onClick="ToggleOption_RunExperiment(false,0)">
					<INPUT type="hidden" id="runExperimentId">
				</FORM>
			</DIV>
		</DIV>
	</BODY>
</HTML>