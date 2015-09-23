namespace java com.asksunny.scheduler.agent
namespace perl Scheduler.Agent
namespace cpp scheduler.agent
namespace py Scheduler.Agent


enum JobExecutableType
{
	NATIVE = 1,
	JAVA = 2,
}

enum CommandParameterType
{	
	STRING = 0,
	INT = 1,
	DOUBLE = 2,
	LONG = 3,	
	DYNAMIC_VARIABLE = 4,		
}



struct CommandParameter
{
	1: required CommandParameterType cmdParamType = CommandParameterType.STRING
	2: required string parameterValue,
	3: optional string parameterName,

}

struct  DistJobDetail
{
	1: required string jobid,
	2: required int priority = 5,
	3: required JobExecutableType jobType = JobExecutableType.NATIVE,	
	4: optional string command,
	5: optional list<CommandParameter> parameters,
	
}





