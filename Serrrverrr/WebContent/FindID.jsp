
<%@ page import="com.db.FindID"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>

<%
	FindID findID = FindID.getInstance(); 
	
	String email = request.getParameter("email");

	String returns = findID.findID(email);
	System.out.println(returns);

	// �ȵ���̵�� ����
	out.println(returns);
%>