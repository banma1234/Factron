package com.itwillbs.factron;

import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import com.itwillbs.factron.entity.SysCode;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.employee.IntergratAuthRepository;
import com.itwillbs.factron.repository.syscode.DetailSysCodeRepository;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class FactronApplicationTests {

	@Autowired
	private SysCodeRepository sysCodeRepository;
	@Autowired
	private DetailSysCodeRepository detailSysCodeRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private IntergratAuthRepository intergratAuthRepository;

	@Test
	@Transactional
	@Commit
	void insertDummy() {
		Map<String, String> sysCodeMap = new HashMap<>();
		sysCodeMap.put("DEP", "부서");
		sysCodeMap.put("POS", "직급");
		sysCodeMap.put("HIR", "고용 유형");
		sysCodeMap.put("ATH", "권한");
		sysCodeMap.put("WRK", "근무 유형");
		sysCodeMap.put("EDU", "최종 학력");
		sysCodeMap.put("TRS", "발령 구분");
		sysCodeMap.put("APR", "결재 유형");
		sysCodeMap.put("SLS", "영업 구분");
		sysCodeMap.put("APV", "승인 여부");
		sysCodeMap.put("ITP", "제품 유형");
		sysCodeMap.put("LIS", "라인 상태");
		sysCodeMap.put("PTP", "공정 유형");
		sysCodeMap.put("STS", "상태");
		sysCodeMap.put("UNT", "단위");
		sysCodeMap.put("WKS", "작업 상태");
		sysCodeMap.put("QIR", "검사 결과");
		sysCodeMap.put("STP", "수발주 상태");

		Map<String, Map<String, String>> detailCodeMap = new HashMap<>();
		Map<String, String> dep = new HashMap<>();
		dep.put("DEP001", "인사");
		dep.put("DEP002", "경영");
		dep.put("DEP003", "영업");
		dep.put("DEP004", "개발");
		dep.put("DEP005", "재무");
		dep.put("DEP006", "생산");
		detailCodeMap.put("DEP", dep);

		Map<String, String> pos = new HashMap<>();
		pos.put("POS001", "사원");
		pos.put("POS002", "대리");
		pos.put("POS003", "과장");
		pos.put("POS004", "차장");
		pos.put("POS005", "부장");
		pos.put("POS006", "이사");
		pos.put("POS007", "사장");
		detailCodeMap.put("POS", pos);

		Map<String, String> hir = new HashMap<>();
		hir.put("HIR001", "정규직");
		hir.put("HIR002", "계약직");
		hir.put("HIR003", "시간제");
		hir.put("HIR004", "파견직");
		hir.put("HIR005", "용역직");
		detailCodeMap.put("HIR", hir);

		Map<String, String> ath = new HashMap<>();
		ath.put("ATH001", "일반");
		ath.put("ATH002", "인사");
		ath.put("ATH003", "관리자");
		ath.put("ATH004", "영업");
		ath.put("ATH005", "재무");
		ath.put("ATH006", "생산");
		ath.put("ATH007", "작업반장");
		detailCodeMap.put("ATH", ath);

		Map<String, String> wrk = new HashMap<>();
		wrk.put("WRK001", "일반근무");
		wrk.put("WRK002", "연장근무");
		wrk.put("WRK003", "야간근무");
		wrk.put("WRK004", "외근/출장");
		wrk.put("WRK005", "휴일근무");
		detailCodeMap.put("WRK", wrk);

		Map<String, String> edu = new HashMap<>();
		edu.put("EDU001", "고졸");
		edu.put("EDU002", "전문대졸");
		edu.put("EDU003", "학사");
		edu.put("EDU004", "석사");
		edu.put("EDU005", "박사");
		detailCodeMap.put("EDU", edu);

		Map<String, String> trs = new HashMap<>();
		trs.put("TRS001", "승진");
		trs.put("TRS002", "전보");
		detailCodeMap.put("TRS", trs);

		Map<String, String> apr = new HashMap<>();
		apr.put("APR001", "근무");
		apr.put("APR002", "휴가");
		apr.put("APR003", "인사발령");
		detailCodeMap.put("APR", apr);

		Map<String, String> sls = new HashMap<>();
		sls.put("SLS001", "수주");
		sls.put("SLS002", "발주");
		detailCodeMap.put("SLS", sls);

		Map<String, String> apv = new HashMap<>();
		apv.put("APV001", "대기");
		apv.put("APV002", "승인");
		apv.put("APV003", "반려");
		apv.put("APV004", "취소");
		detailCodeMap.put("APV", apv);

		Map<String, String> itp = new HashMap<>();
		itp.put("ITP001", "원자재");
		itp.put("ITP002", "반제품");
		itp.put("ITP003", "완제품");
		detailCodeMap.put("ITP", itp);

		Map<String, String> lis = new HashMap<>();
		lis.put("LIS001", "정지");
		lis.put("LIS002", "가동");
		detailCodeMap.put("LIS", lis);

		Map<String, String> ptp = new HashMap<>();
		ptp.put("PTP001", "절삭");
		ptp.put("PTP002", "단조");
		ptp.put("PTP003", "성형");
		ptp.put("PTP004", "용접");
		ptp.put("PTP005", "도색");
		ptp.put("PTP006", "주조");
		ptp.put("PTP007", "도장");
		ptp.put("PTP008", "조립");
		ptp.put("PTP009", "압출");
		ptp.put("PTP010", "열처리");
		detailCodeMap.put("PTP", ptp);

		Map<String, String> sts = new HashMap<>();
		sts.put("STS001", "대기");
		sts.put("STS002", "시작");
		sts.put("STS003", "완료");
		sts.put("STS004", "취소");
		detailCodeMap.put("STS", sts);

		Map<String, String> unt = new HashMap<>();
		unt.put("UNT001", "EA");
		unt.put("UNT002", "장");
		unt.put("UNT003", "kg");
		unt.put("UNT004", "g");
		unt.put("UNT005", "ton");
		unt.put("UNT006", "mm");
		unt.put("UNT007", "m²");
		unt.put("UNT008", "m³");
		unt.put("UNT009", "L");
		unt.put("UNT010", "mL");
		unt.put("UNT011", "세트");
		unt.put("UNT012", "박스");
		unt.put("UNT013", "쌍");
		detailCodeMap.put("UNT", unt);

		Map<String, String> wks = new HashMap<>();
		wks.put("WKS001", "대기");
		wks.put("WKS002", "생산중");
		wks.put("WKS003", "검사중");
		wks.put("WKS004", "완료");
		detailCodeMap.put("WKS", wks);

		Map<String, String> qir = new HashMap<>();
		qir.put("QIR001", "합격");
		qir.put("QIR002", "불합격");
		detailCodeMap.put("QIR", qir);

		Map<String, String> stp = new HashMap<>();
		stp.put("STP001", "결재대기");
		stp.put("STP002", "진행중");
		stp.put("STP003", "반려");
		stp.put("STP004", "완료");
		stp.put("STP005", "취소");
		detailCodeMap.put("STP", stp);

		for (String mainCode : sysCodeMap.keySet()) {
			// SysCode 저장
			SysCode sysCode = SysCode.builder()
					.mainCode(mainCode)
					.name(sysCodeMap.get(mainCode))
					.isActive("Y")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build();

			sysCode = sysCodeRepository.save(sysCode);

			// 연결된 DetailSysCode 저장
			Map<String, String> detailItems = detailCodeMap.get(mainCode);
			if (detailItems != null) {
				for (Map.Entry<String, String> entry : detailItems.entrySet()) {
					DetailSysCode detail = DetailSysCode.builder()
							.sysCode(sysCode)
							.mainCode(mainCode)
							.detailCode(entry.getKey())
							.name(entry.getValue())
							.isActive("Y")
							.createdAt(LocalDateTime.now())
							.createdBy(1L)
							.build();

					detailSysCodeRepository.save(detail);
				}
			}
		}

		// 사원 10명 생성
		for (long i = 25060001L; i <= 25060010L; i++) {
			String email = "user" + i + "@test.com";
			String phone = "010-1234-" + String.format("%04d", i % 10000);

			Employee emp = Employee.builder()
					.id(i)
					.gender(i % 2 == 1 ? "M" : "F")   // 홀수면 남자(M), 짝수면 여자(F)
					.joinedDate(LocalDate.of(2025, 6, 13))
					.quitDate(null)
					.deptCode("DEP001")
					.eduLevelCode("EDU001")
					.employCode("HIR001")
					.positionCode("POS001")
					.name("사원" + (i - 25060000))
					.birth("000101")
					.email(email)
					.phone(phone)
					.address("부산 북구 낙동대로 1574")
					.rrnBack("rOLirhKOd0qtd9g4PkSZkQ==")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build();

			emp = employeeRepository.save(emp);

			IntergratAuth auth = IntergratAuth.builder()
					.isActive("Y")
					.authCode("ATH002")
					.password(phone)  // 암호화 필요
					.employee(emp)
					.build();

			intergratAuthRepository.save(auth);
		}
	}
}
