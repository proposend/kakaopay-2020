package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ApiLook
 */
@WebServlet("/api/look")
public class ApiLook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int EXPIRED_TIME = 60 * 24 * 7;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApiLook() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// doPost()로 포워딩.
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// 클라이언트 응답시 전달될 컨텐트에 대한 타잎 설정과 캐릿터셋 지정
			response.setContentType("application/json; charset=UTF-8");

			// 클라이언트 응답을 위한 출력 스트림 확보
			PrintWriter out = response.getWriter();
			
		    if(request.getHeader("X-USER-ID") == null) {
		    	throw new Exception("X-USER-ID 식별불가.");
		    }
		    
		    if(request.getHeader("X-ROOM-ID") == null) {
		    	throw new Exception("X-ROOM-ID 식별불가.");
		    }
	
		    BburigiBean bbuBean = new BburigiBean();
		    Bburigi bbu = new Bburigi();
		    
		    if(request.getParameter("token") == null
		    		|| !BburigiBean.checkTokenValue(request.getParameter("token"))) {
		    	throw new Exception("토큰값을 확인해주세요.");
		    }
		  	   
		    if(!bbuBean.checkTokenExpired(request.getParameter("token"), EXPIRED_TIME)) { //#요구사항 - 뿌린건에 대한 조회는 7일 동안.
		    	throw new Exception("조회 기간이 만료되었습니다.");
		    }
		    
		    bbu.setToken(request.getParameter("token"));
		    bbu.setRequest_user_id(Integer.parseInt(request.getHeader("X-USER-ID")));
		    bbu.setRequest_room_id(request.getHeader("X-ROOM-ID"));
		    
		    if(!bbuBean.lookTokenStatus(bbu)) {
		    	throw new Exception("요청 토큰에 대한 조회를 실패했습니다.");
		    }
		    
		    ArrayList<Bburigi> datas = bbuBean.getDBList(bbu);
		    if(datas.size() == 0) {
		    	throw new Exception("조회 목록을 가져오는데 실패했습니다.");
		    }
		    
		    int total_response_money = 0;
		    String listJSON = "";
			for (Bburigi data : datas) {
				if (!data.getAllocate_status()) {
					total_response_money += data.getResponse_money();
					listJSON += "{";
					listJSON += "\"response_money\" : \"" + data.getResponse_money() + "\",";
					listJSON += "\"response_user\" : \"" + data.getResponse_user_id() + "\",";
					listJSON += "},";
				}
			}
		    
		    Bburigi firstBbu = datas.get(0);
		    String responseJSON = "{"
		    		+ "\"status\" : \"success\","
		    		+ "\"created_at\" : \"" + firstBbu.getCreated_at() + "\"," 
		    		+ "\"bburigi_money\" : \"" + firstBbu.getRequest_money() + "\"," 		    		
		    		+ "\"total_response_money\" : \"" + total_response_money + "\","
		    		+ "\"response_list\" : [" + listJSON + "]"
		    		+ "}";
		    		
		    out.println(responseJSON);
		}catch(Exception e) {
			PrintWriter out = response.getWriter();
			String message = "잘못된 요청입니다.";
			if(e.getMessage().length() > 0) {
				message = e.getMessage();
			} 
			 String responseJSON = "{"
			    		+ "\"status\" : \"error\","
			    		+ "\"message\" : \"" + message + "\"" 
			    		+ "}";
			 out.println(responseJSON);
		}
	}

}
