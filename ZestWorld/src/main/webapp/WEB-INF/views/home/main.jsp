<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--workspaceList-->
<script type="text/javascript">
$(function() {
		alert('projectMain');
		ajaxView('projectMain.ajax');
		connect();
		AlarmCountView();
});	

</script>
	<div id="binContent"></div>