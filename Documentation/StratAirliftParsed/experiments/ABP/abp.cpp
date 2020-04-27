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
#include "NDTime.hpp"

// === Additional Headers ===
#include <chrono>
#include <algorithm>
#include "../../messages/message.hpp"


// === Atomic Model Headers ===

#include "../../atomics/Receiver.hpp"

#include "../../atomics/Sender.hpp"

#include "../../atomics/Subnet.hpp"


using namespace std;
using namespace cadmium;
using namespace cadmium::basic_models::pdevs;

using TIME = NDTime;

// === Port Configs ===
struct inp_1 : public in_port<Message_t>{};
struct inp_2 : public in_port<Message_t>{};
struct inp_control : public in_port<int>{};
struct outp_1 : public out_port<Message_t>{};
struct outp_2 : public out_port<Message_t>{};
struct outp_ack : public out_port<int>{};
struct outp_pack : public out_port<int>{};


// === Input Readers ===

template<typename T>
class InputReader_Int : public iestream_input<int,T> {
    public:
        InputReader_Int () = default;
        InputReader_Int (const char* file_path) : iestream_input<int,T>(file_path) {}
};


int main(int argc, char *argv[])
{
	// === Atomic Models ===
	
	
	string myinput_readerInFile = "input_abp_1.txt";
    const char * sinput_readerInFile = myinput_readerInFile.c_str();
    shared_ptr<dynamic::modeling::model> input_reader = dynamic::translate::make_dynamic_atomic_model<InputReader_Int , TIME, const char* >("input_reader" , move(sinput_readerInFile));
	shared_ptr<dynamic::modeling::model> sender1=dynamic::translate::make_dynamic_atomic_model<Sender, TIME>("sender1");
	
	shared_ptr<dynamic::modeling::model> receiver1=dynamic::translate::make_dynamic_atomic_model<Receiver, TIME>("receiver1");
	
	shared_ptr<dynamic::modeling::model> subnet1=dynamic::translate::make_dynamic_atomic_model<Subnet, TIME>("subnet1");
	
	shared_ptr<dynamic::modeling::model> subnet2=dynamic::translate::make_dynamic_atomic_model<Subnet, TIME>("subnet2");
	
    


	
	// === Network Coupled Models ===
    dynamic::modeling::Ports iports_Network = {typeid(inp_1),typeid(inp_2)};
    dynamic::modeling::Ports oports_Network = {typeid(outp_1),typeid(outp_2)};
    
	dynamic::modeling::Models submodels_Network = {subnet1,subnet2};
	
	dynamic::modeling::EICs eics_Network = {
		dynamic::translate::make_EIC<inp_1, Subnet_defs::in>("subnet1"),
		dynamic::translate::make_EIC<inp_2, Subnet_defs::in>("subnet2")
	};

	dynamic::modeling::EOCs eocs_Network = {
		dynamic::translate::make_EOC<Subnet_defs::out, outp_1>("subnet1"),
		dynamic::translate::make_EOC<Subnet_defs::out, outp_2>("subnet2")
	};

	dynamic::modeling::ICs ics_Network = {};

    shared_ptr<dynamic::modeling::coupled<TIME>> Network;
    Network = make_shared<dynamic::modeling::coupled<TIME>>(
        "Network", submodels_Network, iports_Network, oports_Network, eics_Network, eocs_Network, ics_Network
    );
	
	// === ABP Coupled Models ===
    dynamic::modeling::Ports iports_ABP = {typeid(inp_control)};
    dynamic::modeling::Ports oports_ABP = {typeid(outp_ack),typeid(outp_pack)};
    
	dynamic::modeling::Models submodels_ABP = {sender1,receiver1,Network};
	
	dynamic::modeling::EICs eics_ABP = {
		dynamic::translate::make_EIC<inp_control, Sender_defs::controlIn>("sender1")
	};

	dynamic::modeling::EOCs eocs_ABP = {
		dynamic::translate::make_EOC<Sender_defs::packetSentOut, outp_pack>("sender1"),
		dynamic::translate::make_EOC<Sender_defs::ackReceivedOut, outp_ack>("sender1")
	};

	dynamic::modeling::ICs ics_ABP = {
		dynamic::translate::make_IC<Sender_defs::dataOut, inp_1>("sender1","Network"),
		dynamic::translate::make_IC<outp_2, Sender_defs::ackIn>("Network","sender1"),
		dynamic::translate::make_IC<Receiver_defs::out, inp_2>("receiver1","Network"),
		dynamic::translate::make_IC<outp_1, Receiver_defs::in>("Network","receiver1")
	};

    shared_ptr<dynamic::modeling::coupled<TIME>> ABP;
    ABP = make_shared<dynamic::modeling::coupled<TIME>>(
        "ABP", submodels_ABP, iports_ABP, oports_ABP, eics_ABP, eocs_ABP, ics_ABP
    );
	
	// === TOP Coupled Models ===
    dynamic::modeling::Ports iports_TOP = {};
    dynamic::modeling::Ports oports_TOP = {typeid(outp_pack),typeid(outp_ack)};
    
	dynamic::modeling::Models submodels_TOP = {input_reader,ABP};
	
	dynamic::modeling::EICs eics_TOP = {};

	dynamic::modeling::EOCs eocs_TOP = {
		dynamic::translate::make_EOC<outp_pack, outp_pack>("ABP"),
		dynamic::translate::make_EOC<outp_ack, outp_ack>("ABP")
	};

	dynamic::modeling::ICs ics_TOP = {
		dynamic::translate::make_IC<iestream_input_defs<int>::out, inp_control>("input_reader","ABP")
	};

    shared_ptr<dynamic::modeling::coupled<TIME>> TOP;
    TOP = make_shared<dynamic::modeling::coupled<TIME>>(
        "TOP", submodels_TOP, iports_TOP, oports_TOP, eics_TOP, eocs_TOP, ics_TOP
    );
	
	
		
	// === Loggers ===

	static ofstream out_messages("messages.txt");
	struct oss_messages{
		static ostream& sink(){
			return out_messages;
		}
	};
	static ofstream out_states("states.txt");
	struct oss_states{
		static ostream& sink(){
			return out_states;
		}
	};


    using state=logger::logger<logger::logger_state, dynamic::logger::formatter<TIME>, oss_states>;
    using global_time_sta=logger::logger<logger::logger_global_time, dynamic::logger::formatter<TIME>, oss_states>;
    using global_time_mes=logger::logger<logger::logger_global_time, dynamic::logger::formatter<TIME>, oss_messages>;
    using log_messages=logger::logger<logger::logger_messages, dynamic::logger::formatter<TIME>, oss_messages>;

    using logger_top=logger::multilogger<state,global_time_sta,global_time_mes,log_messages>;

	// === Runner Call ===
    dynamic::engine::runner<TIME, logger_top> r(TOP, {0});
    
    
    r.run_until_passivate();
    

    return 0;
}
