package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDB {
	private static LoginDB instance = new LoginDB();

	public static LoginDB getInstance() {
		return instance;
	}

	public LoginDB() {
	}

	// oracle 계정
	String jdbcUrl = "jdbc:oracle:thin:@192.168.15.116:1521:orcl";
	String userId = "uganda";
	String userPw = "uganda";

	Connection conn = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;
	ResultSet rs = null;

	String sql = "";
	String sql2 = "";
	String returns = "연결실패";

	public String loginDB(String id, String pwd) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

			sql = "SELECT pw,name FROM Member WHERE id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			System.out.println(rs.getRow()+" "+id);

			if (rs.next()) {
				String pw = rs.getString(1);
				
				if (pw.equals(pwd)) {return rs.getString(2)+":로그인이 성공되었습니다.";	} // 로그인 성공 메시지 앞에 사용자 이름을 붙임
				else {return "비밀번호가 일치하지 않습니다.";	}
			}else {
				return "아이디가 존재하지 않습니다.";
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt2 != null)
				try {
					pstmt2.close();
				} catch (SQLException ex) {
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException ex) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException ex) {
				}
		}
		return returns;
	}
}
