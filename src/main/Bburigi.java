package main;

public class Bburigi {
	private int bburigi_id;
	private int request_user_id; //뿌리기 요청 유저
	private int response_user_id; //뿌리기 받기 유저
	private String token;			// 뿌리기 요청 토큰
	private String request_room_id; // 뿌리기 요청 방 번호
	private int request_money;  // 뿌려진 금액
	private int response_money; // 사용자가 가져간 금액
	private boolean allocate_status = true; //할당 상태 체크
	private String created_at;
	private String updated_at;
	private int row_count;
	
	
	public int getBburigi_id() {
		return bburigi_id;
	}
	
	public void setBburigi_id(int bburigi_id) {
		this.bburigi_id = bburigi_id;
	}
	public int getRequest_user_id() {
		return request_user_id;
	}
	
	public void setRequest_user_id(int request_user_id) {
		this.request_user_id = request_user_id;
	}
	
	public int getResponse_user_id() {
		return response_user_id;
	}
	
	public void setResponse_user_id(int response_user_id) {
		this.response_user_id = response_user_id;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getRequest_room_id() {
		return request_room_id;
	}
	
	public void setRequest_room_id(String request_room_id) {
		this.request_room_id = request_room_id;
	}
	
	public boolean getAllocate_status() {
		return allocate_status;
	}
	
	public void setAllocate_status(boolean allocate_status) {
		this.allocate_status =  allocate_status;
	}
	
	public String getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	public String getUpdated_at() {
		return updated_at;
	}
	
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	public int getRequest_money() {
		return request_money;
	}
	
	public void setRequest_money(int request_money) {
		this.request_money = request_money;
	}
	
	public int getResponse_money() {
		return response_money;
	}
	
	public void setResponse_money(int response_money) {
		this.response_money = response_money;
	}
	
	public int getRow_count() {
		return row_count;
	}
	
	public void setRow_count(int row_count) {
		this.row_count = row_count;
	}
}
