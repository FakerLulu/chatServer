
<%@ page import="com.db.FindPW"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>

<%
	FindPW findPW = FindPW.getInstance(); 
	
	String id = request.getParameter("id");

	String returns = findPW.findPW(id);
	System.out.println(returns);

	// �ȵ���̵�� ����
	out.println(returns);
%>