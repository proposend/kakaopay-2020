package main;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class BburigiBean {
	Connection conn = null;
	PreparedStatement pstmt = null;

	/* MySQL 연결정보 */
	String jdbc_driver = "com.mysql.cj.jdbc.Driver";
	String jdbc_url = "jdbc:mysql://127.0.0.1:3306/kakaopay?serverTimezone=UTC";

	// DB연결 메서드
	void connect() {
		try {
			Class.forName(jdbc_driver);

			conn = DriverManager.getConnection(jdbc_url, "root", "kjhds");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void disconnect() {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 새로운 뿌리기 추가
	public boolean insertDB(Bburigi bbu) {
		connect();
		
		String sql = "insert into bburigi(request_user_id, request_room_id, token, request_money, response_money, allocate_status, created_at, updated_at ) values(?,?,?,?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bbu.getRequest_user_id());
			pstmt.setString(2, bbu.getRequest_room_id());
			pstmt.setString(3, bbu.getToken());
			pstmt.setInt(4, bbu.getRequest_money());
			pstmt.setInt(5, bbu.getResponse_money());
			pstmt.setBoolean(6, bbu.getAllocate_status());
			pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			disconnect();
		}
		return true;
	}

	// 중복 토큰 값 체크
	public boolean checkDuplicateToken(String token) {
		connect();
		System.out.println("token:" + token);
		String sql = "select * from kakaopay.bburigi where token=?";
		int rowcount = 0;
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, token);
			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				rs.last();
				rowcount = rs.getRow();
				rs.beforeFirst();
			} else {
				throw new SQLException();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			disconnect();
		}
		if (rowcount == 0) {
			return true;
		}
		return false;
	}

	public boolean checkTokenExpired(String token, int expired_time) {
		connect();
		System.out.println("token:" + token);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format_time1 = format1.format(System.currentTimeMillis());
		System.out.println(format_time1);

		String sql = "select *, NOW() as cur_time, TIMESTAMPDIFF(minute, created_at, ?) as timediff from kakaopay.bburigi where token=? Having timediff < ?";
		int rowcount = 0;
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(2, token);
			pstmt.setInt(3, expired_time);
			System.out.println(pstmt);
			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				rs.last();
				rowcount = rs.getRow();
				rs.beforeFirst();
			} else {
				throw new SQLException();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		if (rowcount > 0) { // 만료되지 않은 토큰값이 있으면 true
			return true;
		}
		return false;
	}

	public boolean checkTokenRequestUserId(Bburigi bbu) { // 자신이 뿌리기 한건지 확인.
		connect();
		int rowcount = 0;
		String sql = "select request_user_id from kakaopay.bburigi where token = ? and request_user_id = ? group by request_user_id ";
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, bbu.getToken());
			pstmt.setInt(2, bbu.getRequest_user_id());

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				rs.last();
				rowcount = rs.getRow();
				rs.beforeFirst();
			} else {
				throw new SQLException();
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		if (rowcount == 0) { // 해당값이 존재하는경우 본인이 뿌리기한 것으로, 해당값이 존재하지 않는 경우에만 실행 가능함. true 리턴
			return true;
		}
		return false;
	}

	public boolean checkTokenResponseUserId(Bburigi bbu) { // 뿌리기당 사용자는 한번만 받을 수 있습니다.
		connect();
		int rowcount = 0;
		String sql = "select * from kakaopay.bburigi where token = ? and request_user_id != ? and request_room_id = ? and response_user_id = ?";
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, bbu.getToken());
			pstmt.setInt(2, bbu.getRequest_user_id());
			pstmt.setString(3, bbu.getRequest_room_id());
			pstmt.setInt(4, bbu.getRequest_user_id());

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				rs.last();
				rowcount = rs.getRow();
				rs.beforeFirst();
			} else {
				throw new SQLException();
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		if (rowcount == 0) { // 해당값이 존재하지 않는 경우에만 실행 가능함. true 리턴
			return true;
		}
		return false;
	}

	public boolean checkTokenAllocateStatus(Bburigi bbu) { // 받기 가능한 남아있는 뿌리기 확인
		connect();
		int rowcount = 0;
		String sql = "select * from kakaopay.bburigi where token = ? and request_user_id != ? and request_room_id = ? and allocate_status = 1";

		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, bbu.getToken());
			pstmt.setInt(2, bbu.getRequest_user_id());
			pstmt.setString(3, bbu.getRequest_room_id());

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				rs.last();
				rowcount = rs.getRow();
				rs.beforeFirst();
			} else {
				throw new SQLException();
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		if (rowcount > 0) { // 해당값이 존재하는 경우 true 리턴
			return true;
		}
		return false;
	}

	public Bburigi getTransactionDB(Bburigi bbu) {
		connect();

		String sql = "select * from kakaopay.bburigi where token = ? and request_user_id != ? and request_room_id = ? and allocate_status = 1 limit 1";
		Bburigi result = new Bburigi();
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, bbu.getToken());
			pstmt.setInt(2, bbu.getRequest_user_id());
			pstmt.setString(3, bbu.getRequest_room_id());

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				rs.next();
				result.setBburigi_id(rs.getInt("bburigi_id"));
				result.setRequest_user_id(rs.getInt("request_user_id"));
				result.setRequest_room_id(rs.getString("request_room_id"));
				result.setToken(rs.getString("token"));
				result.setRequest_money(rs.getInt("request_money"));
				result.setResponse_money(rs.getInt("response_money"));
				result.setAllocate_status(rs.getBoolean("allocate_status"));
				result.setResponse_user_id(rs.getInt("response_user_id"));
			} else {
				throw new SQLException("값을 불러 올 수 없습니다.");
			}
			rs.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			disconnect();
		}
		return result;
	}

	public boolean changeAllocateStatus(Bburigi bbu) {
		connect();
		String sql = "update kakaopay.bburigi set response_user_id=?, allocate_status =?, updated_at=? where bburigi_id=?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bbu.getResponse_user_id());
			pstmt.setBoolean(2, bbu.getAllocate_status());
			pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(4, bbu.getBburigi_id());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			disconnect();
		}
		return true;
	}

	public static boolean checkTokenValue(String token) {
		return token.length() == 3 && Pattern.matches("^[0-9a-zA-Z]*$", token);
	}

	public boolean lookTokenStatus(Bburigi bbu) {
		connect();
		int rowcount = 0;
		String sql = "select * from kakaopay.bburigi where token = ? and request_user_id = ? ";
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, bbu.getToken());
			pstmt.setInt(2, bbu.getRequest_user_id());
			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				rs.last();
				rowcount = rs.getRow();
				rs.beforeFirst();
			} else {
				throw new SQLException();
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if (rowcount > 0) { // 해당값이 존재하는 경우에만 실행 가능함. true 리턴
			return true;
		}
		return false;
	}
	
	public ArrayList<Bburigi> getDBList(Bburigi bbu) {
		connect();
		
		ArrayList<Bburigi> datas = new ArrayList<Bburigi>();
		
		String sql = "select * from kakaopay.bburigi where token = ? and request_user_id = ? ";
		try {
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, bbu.getToken());
			pstmt.setInt(2, bbu.getRequest_user_id());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Bburigi bburigi = new Bburigi();
				
				bburigi.setBburigi_id(rs.getInt("bburigi_id"));
				bburigi.setRequest_user_id(rs.getInt("request_user_id"));
				bburigi.setResponse_user_id(rs.getInt("response_user_id"));
				bburigi.setToken(rs.getString("token"));
				bburigi.setRequest_room_id(rs.getString("request_room_id"));
				bburigi.setRequest_money(rs.getInt("request_money"));
				bburigi.setResponse_money(rs.getInt("response_money"));
				bburigi.setAllocate_status(rs.getBoolean("allocate_status"));
				bburigi.setCreated_at(rs.getString("created_at"));
				bburigi.setUpdated_at(rs.getString("updated_at"));
				
				datas.add(bburigi);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
		return datas;
	}
}
