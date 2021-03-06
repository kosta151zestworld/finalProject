package com.zestworld.util;

import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zestworld.AlarmService.AlarmService;
import com.zestworld.Alarm_DAO.AlarmDAO;
import com.zestworld.Table_DTO.Alarm_DTO;
import com.zestworld.Table_DTO.EssenceDefine_DTO;
import com.zestworld.Table_DTO.Project_DTO;
import com.zestworld.Table_DTO.Task_DTO;
import com.zestworld.Table_DTO.Users_DTO;
import com.zestworld.Table_DTO.WorkspaceUser_DTO;
import com.zestworld.Table_DTO.Workspace_DTO;
import com.zestworld.taskDAO.TaskDataDAO;
/*
* @FileName : DataController.java
* @Project : ZestWorld
* @Date : 2017. 6. 10
* @Author : 장윤희
* @Desc : 현재 유져가 선택한 워크스페이스 및 프로젝트 정보 저장 및 수정
*/
public class DataController {
	
	@Autowired
	private SqlSession sqlsession;
	private  static DataController instance = null;
	 
	private Users_DTO user;
	private Project_DTO currentProject;
	private Workspace_DTO currentWorkspace;
	private ArrayList<Workspace_DTO> workspaceList = new ArrayList<Workspace_DTO> ();
	private ArrayList<Project_DTO> projectList = new ArrayList<Project_DTO> ();
	private ArrayList<Task_DTO> taskList = new ArrayList<Task_DTO> ();
	private String[] milestoneList = {};
	private String selectEssenPage = "";



	private DataController()
	{
	}
	
	
	public void Reset()
	{	
		 Users_DTO use =null;
		 Project_DTO currentProject=null;
		 Workspace_DTO currentWorkspace=null;
		 workspaceList = new ArrayList<Workspace_DTO> ();
		 projectList = new ArrayList<Project_DTO> ();
		 taskList = new ArrayList<Task_DTO> ();
		 selectEssenPage = "";
	}
	
	//워크스페이스 추가/프로젝트 생성,삭제시 변화 있을때 호출 
	//current data edit 
	public void dataChange()
	{
		TaskDataDAO taskDao = sqlsession.getMapper(TaskDataDAO.class);
		List<WorkspaceUser_DTO> workspaceUserList = taskDao.GetWorkSpaceList(user.getUser_id());
	    List<Workspace_DTO>workspaceList = new ArrayList<Workspace_DTO>();
	    Workspace_DTO workspace;
	    
	    for( int i=0; i<workspaceUserList.size(); i ++)
	    {
	    	int workSpaceId = workspaceUserList.get(i).getWorkspace_id();
	    	workspace= taskDao.GetWorkSpace(workSpaceId);
	    	workspaceList.add(workspace);
	    }
	    SetUserWorkSpace(workspaceList);    
	}
	
	public void dataChangeProject ()
	{
		projectList.clear();
		TaskDataDAO taskDao = sqlsession.getMapper(TaskDataDAO.class);
		projectList = (ArrayList<Project_DTO>)taskDao.GetProjectList(currentWorkspace.getWorkspace_id());
	}
	
	//로그인된 user data
	public void SetUserSavedata (Users_DTO _user)
	{
		this.user = _user;
	}
	
	//워크스페이스 선택시 data
	//선택한 워크스페이스네 프로젝트 data list
	public void SetCurrentWorkspace(Workspace_DTO _currentWorkspace)
	{
		this.currentWorkspace = _currentWorkspace;
		TaskDataDAO taskDao = sqlsession.getMapper(TaskDataDAO.class);
		projectList = (ArrayList<Project_DTO>)taskDao.GetProjectList(currentWorkspace.getWorkspace_id());
	}
	
	// 현재 워크스페이스 위치
	public Workspace_DTO getCurrentWorkspace() {
		return currentWorkspace;
	}
	
	//프로젝트 선택시 선택된 project data
	public void SetCurrentProject (Project_DTO _currentProject)
	{
		this.currentProject = _currentProject;
	}
	
	// 현재 프로젝트 위치
	public Project_DTO getCurrentProject() {
		return currentProject;
	}
	
	//현재 선택된 워크스페이스에서 가지고 있는 프로젝트 리스트
	public void SetUserWorkSpace(List<Workspace_DTO> _workspaceList )
	{
		this.workspaceList = (ArrayList<Workspace_DTO>)_workspaceList;
	}
	
	public Users_DTO GetUser()
	{
		return user;
	}
	
	public ArrayList<Workspace_DTO> GetWorkspaceList ()
	{
		return workspaceList;
	}
	
	
	public ArrayList<Project_DTO> GetProjectList ()
	{
		return projectList;
	}
	public void SetProjectList(ArrayList<Project_DTO> projcetList)
	{
		this.projectList=projcetList;
	}
	
	public ArrayList<Task_DTO> GetTaskList ()
	{
		return taskList;
	}
	
	
	public Project_DTO SelectProjectData()
	{
		return currentProject;
	}
	
	public Workspace_DTO GetSelectWorkSpace()
	{
		return currentWorkspace;
	}
	
	public static DataController getInstance()
	{
		if( instance == null )
		{
			instance = new DataController();
		}
		
		return instance;
	}
	
	public void SetAlarm(String msg)
	{
		AlarmDAO alarmDao = sqlsession.getMapper(AlarmDAO.class);
		
		
		  String[] alarmIdArr={};
		  String[] msgArr = msg.split("/");
		  int alarmType 	=  Integer.parseInt(msgArr[0]);
		  String taskTitle 	= msgArr[1];
		  alarmIdArr 		= msgArr[2].split(",");
		  String writer 	= msgArr[3];
		  
			Calendar date = Calendar.getInstance();
			String strTime = date.get(Calendar.HOUR_OF_DAY) + ":" +
			date.get(Calendar.MINUTE) + ":" +
			date.get(Calendar.SECOND);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = dateFormat.format(date.getTime());
		  
			String DbMessage = alarmStrMake(msg);
		  //않읽은 db로 저장하고  알람 카운트 올려주기 
		  Alarm_DTO alarm = new Alarm_DTO();
		  alarm.setAlarm_type(alarmType);
		  alarm.setCheck_f(0);
		  alarm.setImg(now);
		  alarm.setUser_id(writer);
		  alarm.setAcceptUsers(alarmIdArr[0]); // 1명일
		  alarm.setAlarmTitle(alarmStrMake(msg));
		  try {
				alarmDao.insert(alarm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	//알람 stirng 만들기 
		private String alarmStrMake(String newAlarm)
		{
			  String[] msgArr = newAlarm.split("/");
			  String alarmType 	= msgArr[0];
			  String taskTitle 	= msgArr[1];
			  String[] alarmIdArr	={};
			  alarmIdArr 		= msgArr[2].split(",");
			  String writer 	= msgArr[3];
			 // String userId		= DataController.getInstance().GetUser().getUser_id();
			  String returnMsg = "";
			  //0 업무배정받음
			  //1 업무완료알림
			  if( alarmType.equals("0"))
			  {
				  if( writer.equals(alarmIdArr[0]) )
					  returnMsg = "새로운 업무 "+ taskTitle+ "가 만들어졌습니다.";
				  else
					  returnMsg = writer+"님이" + alarmIdArr[0] + "님께" + taskTitle+ "배정했습니다.";
			  }else
			  {
				  if( writer.equals(alarmIdArr[0]) )
					  returnMsg = "업무 "+ taskTitle+ "를 완료하였습니다.";
				  else
					  returnMsg = writer+"님이" +"업무를 완료하였습니다.";
			  }
			  
			  return returnMsg;
		}
	
	public String GetviewPath(String pathType)
	{
		String path = "";
		if( pathType.equals( "home")) path = "/WEB-INF/views/home/";
		if( pathType.equals( "totalTesk")) path = "/WEB-INF/views/task/";
		if( pathType.equals( "memberAdministration")) path = "/WEB-INF/views/memberAdministration/";
		if( pathType.equals( "chat")) path = "/WEB-INF/views/chat/";
		if( pathType.equals( "calendar")) path = "/WEB-INF/views/calendar/";
		if( pathType.equals( "file")) path = "/WEB-INF/views/file/";
		if( pathType.equals( "analysis")) path = "/WEB-INF/views/analysis/";
		if( pathType.equals( "template")) path = "/WEB-INF/views/template/";
		if( pathType.equals( "alarm")) path = "/WEB-INF/views/alarm/";
		if( pathType.equals( "essence")) path = "/WEB-INF/views/essence/";
		return path;
	}
	

	public String getSelectEssenPage() {
		return selectEssenPage;
	}

	public void setSelectEssenPage(String selectEssenPage) {
		this.selectEssenPage = selectEssenPage;
	}

}
