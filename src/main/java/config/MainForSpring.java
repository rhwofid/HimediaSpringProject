package config;

import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.AppCtx;
import spring.ChangePasswordService;
import spring.DuplicateMemberException;
import spring.MemberListPrinter;
import spring.MemberNotFoundException;
import spring.MemberRegisterService;
import spring.RegisterRequest;
import spring.WrongIdPassWordException;

public class MainForSpring {

	private static ApplicationContext ctx = null;
	
	public static void main(String[] args) {
		
		// AnnotationConfigApplicationContext 클래스를 이용해서 스프링 컨테이너를 생성
		ctx = new AnnotationConfigApplicationContext(AppCtx.class);
		
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.println("명령어를 입력하세요.");
			String command = scan.nextLine().trim();
			if (command.equalsIgnoreCase("exit")) {
				System.out.println("프로그램을 종료합니다.");
				break;
			}
			
			if (command.startsWith("new ")) {
				processNewCommand(command.split(" "));
				continue;
			}
			else if (command.startsWith("change ")) {
				processChangeCommand(command.split(" "));
				continue;
			}
			else if (command.startsWith("list")) {
				processListCommand();
				continue;
			}
			printHelp();
		}
	}
	
	private static void processListCommand() {
		MemberListPrinter listPrinter = ctx.getBean("listPrinter", MemberListPrinter.class);
		listPrinter.printAll();
	}

	// 입력받은 회원정보를 등록하는 메서드
	private static void processNewCommand(String[] args) {
		// 매개변수의 개수를 체크
		if (args.length != 5) {
			printHelp();
			return;
		}
		
		// Assembler 클래스에 정의된 getter 메서드를 이용해서 사용할 객체를 구함
		// MemberRegisterService regSvc = assembler.getMemberRegisterService();
		MemberRegisterService regSvc = ctx.getBean("memberRegSvc", MemberRegisterService.class);
		RegisterRequest req = new RegisterRequest();
		req.setEmail(args[1]);
		req.setName(args[2]);
		req.setPassword(args[3]);
		req.setConfirmPassword(args[4]);
		
		// 패스워드와 패스워드 확인 정보가 일치하는지 확인
		if (!req.isPasswordEqualstoConfirmPassword()) {
			System.out.println("패스워드가 일치하지 않습니다.");
			return;
		}
		
		try {
			regSvc.regist(req);
			System.out.println("정상적으로 등록되었습니다.");
		} catch (DuplicateMemberException e) {
			System.out.println("이미 존재하는 이메일입니다.");
			return;
		}
	}

	private static void processChangeCommand(String[] args) {
		// 매개변수의 개수를 체크
		if (args.length != 4) {
			printHelp();
			return;
		}
		
		// Assembler 클래스에 정의된 getter 메서드를 이용해서 사용할 객체를 구함
		// ChangePasswordService changePwdSvc = assembler.getChangePasswordService();
		ChangePasswordService changePwdSvc = ctx.getBean("changePwdSvc", ChangePasswordService.class);
		try {		
			changePwdSvc.changepassword(args[1], args[2], args[3]);
			System.out.println("정상적으로 패스워드를 변경했습니다.");
		} catch (MemberNotFoundException e) {
			System.out.println("존재하지 않는 이메일 주소입니다.");
		} catch (WrongIdPassWordException e) {
			System.out.println("이메일 주소와 패스워드가 일치하지 않습니다.");
		}		
	}

	// 등록되지 않은 명령어 또는 형식에 맞지 않은 명령어가 입력되었을 때, 
	// 사용법을 안내하는 메서드
	private static void printHelp() {
		System.out.println();
		System.out.println("잘못된 명령어입니다. 아래 명령어 사용법을 확인하세요.");
		System.out.println("명령어 사용법");
		System.out.println("new 이메일 이름 암호 암호확인");
		System.out.println("change 이메일 현재비번 변경비번");
		System.out.println();
	}
}

