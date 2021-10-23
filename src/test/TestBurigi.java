package test;

import java.io.BufferedReader; 
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


import java.net.HttpURLConnection;

public class TestBurigi {
	private static String BaseURI = "http://localhost:8080/kakaopay/api";
	private final String USER_AGENT = "Mozilla/5.0";
	private static String TEST_TOKEN = "41H";

	public static void main(String[] args) throws Exception {
			
		Scanner scanner = new Scanner(System.in);
		System.out.println("기능 테스트 번호를 눌러주세요.");
		System.out.println("1. 뿌리기 API TEST");
		System.out.println("2. 받기 API TEST");
		System.out.println("3. 조회 API TEST");
		System.out.println("4. 받기  - 본인이 생성한 뿌리기");
		System.out.println("5. 받기  - 동일한 대화방 사용자만 받기");
		System.out.println("6. 조회  - 뿌린 사람 자신만 조회 가능");
		System.out.println("0. 종료");

		while (true) {
			String num = scanner.next();
			String resp = "";
			String url  = "";
			if(Integer.parseInt(num) > 1) {
				System.out.println("테스트 토큰 값:" + TEST_TOKEN);
			}
			boolean stop = false;
			HashMap<String, String> param = new HashMap<String, String>();
			switch (num) {
				case "1":
					url = BaseURI + "/spread"; // URL
					param.put("request_money", "10000"); //뿌리기 액수	
					param.put("person_count", "5");	//분배 인원
					resp = HttpConnectionUtil.postRequest(url, param, 1, "room01");
					break;
				case "2":
					url = BaseURI + "/get"; // URL
					param.put("token", TEST_TOKEN);	//PARAM
					resp = HttpConnectionUtil.postRequest(url, param, 2, "room01");
					break;
				case "3":
					url = BaseURI + "/look"; // URL
					param.put("token", TEST_TOKEN);	//PARAM
					resp = HttpConnectionUtil.postRequest(url, param, 1, "room01");
					break;
				case "4":
					url = BaseURI + "/get"; // URL
					param.put("token", TEST_TOKEN);	//PARAM
					resp = HttpConnectionUtil.postRequest(url, param, 1, "room02");
					break;
				case "5":
					url = BaseURI + "/get"; // URL
					param.put("token", TEST_TOKEN);	//PARAM
					resp = HttpConnectionUtil.postRequest(url, param, 2, "room04");
					break;
				case "6":
					url = BaseURI + "/look"; // URL
					param.put("token", TEST_TOKEN);	//PARAM
					resp = HttpConnectionUtil.postRequest(url, param, 2, "room01");
					break;
				case "0":
					stop = true;
					break;
			}
			
			if(stop) {
				break;
			}
			System.out.println(resp);
		}
	}
}
