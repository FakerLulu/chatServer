package com.db;

import java.sql.*;

public class FindID {

	private static FindID instance = new FindID();

	public static FindID getInstance() {
		return instance;
	}

	public FindID() {
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

	public String findID(String email) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(jdbcUrl, userId, userPw);

			sql = "SELECT id FROM member WHERE email = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();

			System.out.println(rs.getRow() + " ");

			if (rs.next()) {
				String id = rs.getString(1);

				returns = id;
			} else {
				returns = "ID찾기 실패";
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
