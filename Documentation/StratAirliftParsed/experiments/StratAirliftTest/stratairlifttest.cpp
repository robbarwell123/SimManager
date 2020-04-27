// === Cadmium Headers ===
#include <cadmium/modeling/ports.hpp>
#include <cadmium/modeling/dynamic_model.hpp>
#include <cadmium/modeling/dynamic_model_translator.hpp>
#include <cadmium/engine/pdevs_dynamic_runner.hpp>
#include <cadmium/logger/common_loggers.hpp>
#include <cadmium/basic_model/pdevs/iestream.hpp> //Atomic model for inputs

// === Standard C++ Libraries===
#include <iostream>
#include <string>

// === Time Library ===
#include "../../usrlib/EIRational.hpp"

// === Additional Headers ===
#include <boost/algorithm/string.hpp>
#include "../../messages/StratAirLiftSimMessage.hpp"
#include "EIRational.hpp"


// === Atomic Model Headers ===

#include "../../atomics/Aircraft.hpp"

#include "../../atomics/Location.hpp"


using namespace std;
using namespace cadmium;
using namespace cadmium::basic_models::pdevs;

using TIME = EIRational;

// === Port Configs ===
struct AC_inLoads : public in_port<oLoad>{};
struct LOC_InputACStatus : public in_port<oAircraftStatus>{};
struct LOC_InputLoads : public in_port<oLoad>{};
struct LOC_InputLocInfo : public in_port<oLocInfo>{};
struct LOC_InputPallets : public in_port<oPallet>{};
struct AC_outLoads : public out_port<oLoad>{};
struct AC_outStatus : public out_port<oAircraftStatus>{};
struct LOC_outLoads : public out_port<oLoad>{};
struct LOC_outPallets : public out_port<oPallet>{};
struct outLoads : public out_port<oLoad>{};
struct outPallets : public out_port<oPallet>{};


// === Input Readers ===

template<typename T>
class InputReader_oLocInfo : public iestream_input<oLocInfo,T> {
    public:
        InputReader_oLocInfo () = default;
        InputReader_oLocInfo (const char* file_path) : iestream_input<oLocInfo,T>(file_path) {}
};
template<typename T>
class InputReader_oPallet : public iestream_input<oPallet,T> {
    public:
        InputReader_oPallet () = default;
        InputReader_oPallet (const char* file_path) : iestream_input<oPallet,T>(file_path) {}
};


int main(int argc, char *argv[])
{
	// === Atomic Models ===
	
	
	string myirPalletInInFile = "Pallets.txt";
    const char * sirPalletInInFile = myirPalletInInFile.c_str();
    shared_ptr<dynamic::modeling::model> irPalletIn = dynamic::translate::make_dynamic_atomic_model<InputReader_oPallet , TIME, const char* >("irPalletIn" , move(sirPalletInInFile));
	
	string myirLocInfoInInFile = "LocInfo.txt";
    const char * sirLocInfoInInFile = myirLocInfoInInFile.c_str();
    shared_ptr<dynamic::modeling::model> irLocInfoIn = dynamic::translate::make_dynamic_atomic_model<InputReader_oLocInfo , TIME, const char* >("irLocInfoIn" , move(sirLocInfoInInFile));
	shared_ptr<dynamic::modeling::model> Trenton=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Trenton",1,"Trenton",44.118014,-77.530404);
	
	shared_ptr<dynamic::modeling::model> Paris=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Paris",2,"Paris",48.860286,2.350848);
	
	shared_ptr<dynamic::modeling::model> Casablanca=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Casablanca",3,"Casablanca",33.573109,-7.589843);
	
	shared_ptr<dynamic::modeling::model> Tallin=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Tallin",4,"Tallin",59.436962,24.753574);
	
	shared_ptr<dynamic::modeling::model> Aircraft1=dynamic::translate::make_dynamic_atomic_model<Aircraft, TIME,int,int,int,int,int>("Aircraft1",1,1,1,490,16);
	
	shared_ptr<dynamic::modeling::model> Aircraft2=dynamic::translate::make_dynamic_atomic_model<Aircraft, TIME,int,int,int,int,int>("Aircraft2",2,2,2,300,4);
	
    


	
	// === Locations Coupled Models ===
    dynamic::modeling::Ports iports_Locations = {typeid(LOC_InputLoads),typeid(LOC_InputACStatus),typeid(LOC_InputPallets),typeid(LOC_InputLocInfo)};
    dynamic::modeling::Ports oports_Locations = {typeid(LOC_outLoads),typeid(LOC_outPallets)};
    
	dynamic::modeling::Models submodels_Locations = {Trenton,Paris,Casablanca,Tallin};
	
	dynamic::modeling::EICs eics_Locations = {
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Paris"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Paris"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Paris"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Paris"),
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Casablanca"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Casablanca"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Casablanca"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Casablanca"),
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Tallin"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Tallin"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Tallin"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Tallin")
	};

	dynamic::modeling::EOCs eocs_Locations = {
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Trenton"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Trenton"),
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Paris"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Paris"),
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Casablanca"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Casablanca"),
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Tallin"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Tallin")
	};

	dynamic::modeling::ICs ics_Locations = {};

    shared_ptr<dynamic::modeling::coupled<TIME>> Locations;
    Locations = make_shared<dynamic::modeling::coupled<TIME>>(
        "Locations", submodels_Locations, iports_Locations, oports_Locations, eics_Locations, eocs_Locations, ics_Locations
    );
	
	// === Aircraft Coupled Models ===
    dynamic::modeling::Ports iports_Aircraft = {typeid(AC_inLoads)};
    dynamic::modeling::Ports oports_Aircraft = {typeid(AC_outLoads),typeid(AC_outStatus)};
    
	dynamic::modeling::Models submodels_Aircraft = {Aircraft1,Aircraft2};
	
	dynamic::modeling::EICs eics_Aircraft = {
		dynamic::translate::make_EIC<AC_inLoads, Aircraft_defs::inLoads>("Aircraft1"),
		dynamic::translate::make_EIC<AC_inLoads, Aircraft_defs::inLoads>("Aircraft2")
	};

	dynamic::modeling::EOCs eocs_Aircraft = {
		dynamic::translate::make_EOC<Aircraft_defs::outLoads, AC_outLoads>("Aircraft1"),
		dynamic::translate::make_EOC<Aircraft_defs::outACStatus, AC_outStatus>("Aircraft1"),
		dynamic::translate::make_EOC<Aircraft_defs::outLoads, AC_outLoads>("Aircraft2"),
		dynamic::translate::make_EOC<Aircraft_defs::outACStatus, AC_outStatus>("Aircraft2")
	};

	dynamic::modeling::ICs ics_Aircraft = {};

    shared_ptr<dynamic::modeling::coupled<TIME>> Aircraft;
    Aircraft = make_shared<dynamic::modeling::coupled<TIME>>(
        "Aircraft", submodels_Aircraft, iports_Aircraft, oports_Aircraft, eics_Aircraft, eocs_Aircraft, ics_Aircraft
    );
	
	// === TOP Coupled Models ===
    dynamic::modeling::Ports iports_TOP = {};
    dynamic::modeling::Ports oports_TOP = {typeid(outLoads),typeid(outPallets)};
    
	dynamic::modeling::Models submodels_TOP = {irPalletIn,irLocInfoIn,Locations,Aircraft};
	
	dynamic::modeling::EICs eics_TOP = {};

	dynamic::modeling::EOCs eocs_TOP = {
		dynamic::translate::make_EOC<LOC_outLoads, outLoads>("Locations"),
		dynamic::translate::make_EOC<LOC_outPallets, outPallets>("Locations")
	};

	dynamic::modeling::ICs ics_TOP = {
		dynamic::translate::make_IC<LOC_outLoads, AC_inLoads>("Locations","Aircraft"),
		dynamic::translate::make_IC<AC_outStatus, LOC_InputACStatus>("Aircraft","Locations"),
		dynamic::translate::make_IC<AC_outLoads, LOC_InputLoads>("Aircraft","Locations"),
		dynamic::translate::make_IC<iestream_input_defs<oPallet>::out, LOC_InputPallets>("irPalletIn","Locations"),
		dynamic::translate::make_IC<iestream_input_defs<oLocInfo>::out, LOC_InputLocInfo>("irLocInfoIn","Locations")
	};

    shared_ptr<dynamic::modeling::coupled<TIME>> TOP;
    TOP = make_shared<dynamic::modeling::coupled<TIME>>(
        "TOP", submodels_TOP, iports_TOP, oports_TOP, eics_TOP, eocs_TOP, ics_TOP
    );
	
	
		
	// === Loggers ===

	static ofstream out_stratairliftstates("stratairliftstates.txt");
	struct oss_stratairliftstates{
		static ostream& sink(){
			return out_stratairliftstates;
		}
	};
	static ofstream out_stratairliftmessages("stratairliftmessages.txt");
	struct oss_stratairliftmessages{
		static ostream& sink(){
			return out_stratairliftmessages;
		}
	};


    using state=logger::logger<logger::logger_state, dynamic::logger::formatter<TIME>, oss_stratairliftstates>;
    using messages=logger::logger<logger::logger_messages, dynamic::logger::formatter<TIME>, oss_stratairliftmessages>;
    using global_time_sta=logger::logger<logger::logger_global_time, dynamic::logger::formatter<TIME>, oss_stratairliftstates>;
    using global_time_mes=logger::logger<logger::logger_global_time, dynamic::logger::formatter<TIME>, oss_stratairliftmessages>;

    using logger_top=logger::multilogger<state,messages,global_time_sta,global_time_mes>;

	// === Runner Call ===
    dynamic::engine::runner<TIME, logger_top> r(TOP, {0});
    
    
    r.run_until_passivate();
    

    return 0;
}
