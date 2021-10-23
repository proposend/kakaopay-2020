package main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ApiGet
 */
@WebServlet("/api/get")
public class ApiGet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int EXPIRED_TIME = 10;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApiGet() {
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
	
		    BburigiBean tsBean = new BburigiBean();
		    Bburigi ts = new Bburigi();
		    
		    if(request.getParameter("token") == null
		    		|| !BburigiBean.checkTokenValue(request.getParameter("token"))) {
		    	throw new Exception("토큰값을 확인해주세요.");
		    }
		  		    
		   
		    if(!tsBean.checkTokenExpired(request.getParameter("token"), EXPIRED_TIME)) { //#요구사항 - 뿌린건은 10분간만 유효합니다.
		    	throw new Exception("뿌리기 시간이 만료되었습니다.");
		    }
		    
		    ts.setToken(request.getParameter("token")); // #요구사항 - 뿌리기 시 발급된 token을 요청값으로 받습니다.
		    ts.setRequest_user_id(Integer.parseInt(request.getHeader("X-USER-ID"))); //#요구사항 - 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받도록
		    ts.setRequest_room_id(request.getHeader("X-ROOM-ID"));
		    
		    if(!tsBean.checkTokenRequestUserId(ts)) {//#요구사항 - 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
		    	throw new Exception("본인이 뿌린건 받을 수 없습니다.");
		    }
		    
		    if(!tsBean.checkTokenResponseUserId(ts)) { //#요구사항 - 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
		    	throw new Exception("이미 뿌리기를 받으셨습니다.");
		    }
		    
		    if(!tsBean.checkTokenAllocateStatus(ts)) { //#요구사항 - token 할당되지 않는 뿌리기 체크 
		    	throw new Exception("뿌리기가 모두 소진되었습니다.");
		    }
		    
		    Bburigi updateTS = tsBean.getTransactionDB(ts);
		    updateTS.setResponse_user_id(Integer.parseInt(request.getHeader("X-USER-ID")));
		    updateTS.setAllocate_status(false);
		    if(!tsBean.changeAllocateStatus(updateTS)) {
		    	throw new Exception("뿌리기 사용중 문제가 발생했습니다.");
		    }
		    
		    String responseJSON = "{"
		    		+ "\"status\" : \"success\","
		    		+ "\"response_money\" : \"" + updateTS.getResponse_money() + "\"" 
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
