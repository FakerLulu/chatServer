<%@ page import="chat.PhotoList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
System.out.println(new PhotoList().makeImgList());
out.print(new PhotoList().makeImgList());
%>