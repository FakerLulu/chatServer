<%@ page import="com.db.LoginDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
   LoginDB loginDB = LoginDB.getInstance();
	
   String id = request.getParameter("id");
   String pw = request.getParameter("pw");
   	
   String returns = loginDB.loginDB(id, pw);
   System.out.println(returns);

   // 안드로이드로 전송
   out.println(returns);
%>