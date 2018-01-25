package com.hitachiconsulting.digitalplatform.utils;

public interface RequestMappingConstants {

	public interface STATUS {
		String SUCCESS_CODE = "200";
		String FAILURE_CODE = "-1";
		String INTERNAL_ERROR_CODE = "500";
		String BAD_REQUEST = "400";

		String SUCCESS_MSG = "Data Retrieved Successfully";
		String COMMON_ERROR_MSG = "Internal Server Error";
		String SUCCESS_SAVED_TRANS_REC = "Data Saved Successfully in DB";
		String DTO_NOT_PROPER = "Object is Not in Expected Format OR some of the values are not set, Please check";
	}

	String GET_EMPLOYEE_POINTS = "/api/leaderBoard/empPoints/{empId}";
	
	String GET_LEADER_BOARD = "/api/leaderBoard/{empId}";
	
	String LOGIN_TO_GCP = "/api/sibDemoPortal/login";
	
	String OAUTH_TO_CALLBACK = "/api/sibDemoPortal/oauth2callback";
	
	String GET_EMP_FIRST_TIME_LOGIN = "/api/leaderBoard/firstTimeLogin/{empId}";
	
	String PROCESS_EMP_RANK_CALCULATION = "/api/leaderBoard/processEmpRank";
	
	String TRIGGER_EVENT_SAVE = "/api/triggerEvents/save";
	
	String PROCESS_BIRTHDAY_ANNIVERSARY = "/api/triggerEvents/processBirthdayAnniversary";
	
	String PROCESS_UNPROCESSED_TRIGGER_EVENTS = "/api/triggerEvents/processTriggerEvents";
	
	String TRIGGER_BDAY_TYPE = "Birthday";
	String TRIGGERCODE_BDAY_GIVER = "Birthdays_Giver";
	String TRIGGERCODE_BDAY_RECEIVER = "Birthdays_Receiver";
	
	String TRIGGER_ANNI_TYPE = "Service Anniversary";
	String TRIGGERCODE_ANNI_GIVER = "Anniversary_Giver";
	String TRIGGERCODE_ANNI_RECEIVER = "Anniversary_Receiver";
}
