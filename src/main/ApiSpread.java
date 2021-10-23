package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Random;

/**
 * Servlet implementation class Spread
 */
@WebServlet("/api/spread")
public class ApiSpread extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MIN_COUNT = 1;
	private static final int CURRENT_PERSON_COUNT = 1000;
	private static final int MIN_MONEY = 1000;
	private static final int DUPLICATE_TOKEN_EXCUTE_COUNT = 10;
	private String bburigi_token = "";

	// private int DEFAULT_MY_MONEY = 100000;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApiSpread() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// doPost()로 포워딩.
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 클라이언트 응답시 전달될 컨텐트에 대한 타잎 설정과 캐릿터셋 지정
			response.setContentType("application/json; charset=UTF-8");

			// 클라이언트 응답을 위한 출력 스트림 확보
			PrintWriter out = response.getWriter();
			DecimalFormat formatter = new DecimalFormat("###,###");

			/*
			 * request Parameter 값에 대한 validation 작업 추후 필요.
			 * 
			 */

			if (request.getHeader("X-USER-ID") == null) {
				throw new Exception("X-USER-ID 식별불가.");
			}

			if (request.getHeader("X-ROOM-ID") == null) {
				throw new Exception("X-ROOM-ID 식별불가.");
			}

			if (request.getParameter("request_money") == null
					// #요구사항 - 별도로 잔액에 관련된 체크는 하지 않기. 최소한의 금액 확인.
					// || Integer.parseInt(request.getParameter("spread_money")) > DEFAULT_MY_MONEY
					|| Integer.parseInt(request.getParameter("request_money")) < MIN_MONEY) {
				throw new Exception("뿌리기 금액을 확인해주세요. 최소금액은 " + formatter.format(MIN_MONEY) + " 원 입니다.");
			}

			if (request.getParameter("person_count") == null
					|| Integer.parseInt(request.getParameter("person_count")) > CURRENT_PERSON_COUNT
					|| Integer.parseInt(request.getParameter("person_count")) < MIN_COUNT) {
				throw new Exception("뿌리기 인원을 확인해주세요.");
			}

			// DB에 저장된 TOKEN 값과 비교하여 고유값 생성
			BburigiBean tsb = new BburigiBean();
			boolean chkToken = false;
			for (int i = 0; i < DUPLICATE_TOKEN_EXCUTE_COUNT; i++) { // 성능 저하 방지를 위한 최대 DB 탐색 횟수 설정
				bburigi_token = createToken().toString();
				if (tsb.checkDuplicateToken(bburigi_token)) {
					chkToken = true;
					break;
				} else {
					chkToken = false;
				}
			}

			if (chkToken == false) {
				throw new Exception("Token 생성에 실패했습니다. 다시 시도해주세요.");
			}

			int request_user_id = Integer.parseInt(request.getHeader("X-USER-ID"));
			String request_room_id = request.getHeader("X-ROOM-ID");
			int request_money = Integer.parseInt(request.getParameter("request_money"));
			int person_count = Integer.parseInt(request.getParameter("person_count"));

			int response_money = 0;
			int total_response_money = 0;

			for (int i = 0; i < person_count; i++) {
				Bburigi ts = new Bburigi();

				if (i == person_count - 1 && person_count > 1) { // 마지막의 경우
					response_money = request_money - total_response_money;
				} else {
					response_money = request_money / person_count;
				}
				total_response_money += response_money;

				ts.setRequest_user_id(request_user_id);
				ts.setRequest_room_id(request_room_id);
				ts.setToken(bburigi_token);
				ts.setRequest_money(request_money);
				ts.setResponse_money(response_money);

				tsb.insertDB(ts);

				System.out.println(response_money);
			}
			System.out.println(total_response_money);

			String responseJSON = "{" + "\"status\" : \"success\"," + "\"token\" : \"" + bburigi_token + "\"" + "}";

			out.println(responseJSON);
		} catch (Exception e) {
			PrintWriter out = response.getWriter();
			String message = "잘못된 요청입니다.";
			if (e.getMessage().length() > 0) {
				message = e.getMessage();
			}
			String responseJSON = "{" + "\"status\" : \"error\"," + "\"message\" : \"" + message + "\"" + "}";
			out.println(responseJSON);
		}
	}

	public StringBuffer createToken() {
		StringBuffer token = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 3; i++) {
			int rIndex = rnd.nextInt(3);
			switch (rIndex) {
			case 0:
				// a-z
				token.append((char) ((int) (rnd.nextInt(26)) + 97));
				break;
			case 1:
				// A-Z
				token.append((char) ((int) (rnd.nextInt(26)) + 65));
				break;
			case 2:
				// 0-9
				token.append((rnd.nextInt(10)));
				break;
			}
		}
		return token;
	}
}
