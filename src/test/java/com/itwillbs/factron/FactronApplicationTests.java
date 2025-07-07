package com.itwillbs.factron;

import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.entity.Process;
import com.itwillbs.factron.repository.client.ClientRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.employee.IntergratAuthRepository;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.repository.process.LineRepository;
import com.itwillbs.factron.repository.process.MachineRepository;
import com.itwillbs.factron.repository.process.ProcessRepository;
import com.itwillbs.factron.repository.product.BomRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.product.MaterialRepository;
import com.itwillbs.factron.repository.production.ProductionPlanningRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import com.itwillbs.factron.repository.production.WorkPerformanceRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionHistoryRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionStandardRepository;
import com.itwillbs.factron.repository.storage.InboundRepository;
import com.itwillbs.factron.repository.storage.StockRepository;
import com.itwillbs.factron.repository.storage.StorageRepository;
import com.itwillbs.factron.repository.syscode.DetailSysCodeRepository;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private BomRepository bomRepository;

	@Autowired
	private LineRepository lineRepository;
	@Autowired
	private ProcessRepository processRepository;
	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private QualityInspectionRepository qualityInspectionRepository;
	@Autowired
	private QualityInspectionStandardRepository qualityInspectionStandardRepository;
	@Autowired
	private QualityInspectionHistoryRepository qualityInspectionHistoryRepository;

	@Autowired
	private StorageRepository storageRepository;
	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private InboundRepository inboundRepository;
	@Autowired
	private LotRepository lotRepository;

	@Autowired
	private ProductionPlanningRepository prdctPlanRepository;
	@Autowired
	private WorkOrderRepository workOrderRepository;
	@Autowired
	private WorkPerformanceRepository workPerformanceRepository;

	@Test
	@Transactional
	@Commit
	void insertSyscodeEmployeeData() {
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
		sts.put("STS002", "취소");
		sts.put("STS003", "완료");
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
					.deptCode("DEP006")
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
					.authCode("ATH007")
					.password("$2a$10$pHltqD3BTCs6/AdCrX9Zc.2/iGyylnIvtv.yvtL5nTSP7pHFzoX8G") // 5678
					.employee(emp)
					.build();

			intergratAuthRepository.save(auth);
		}
	}

	@Test
	@Transactional
	@Commit
	void insertProductBOMData() {
		// 제품 추가
		Item item1 = Item.builder()
				.id("P0000001")
				.name("금형 A")
				.unit("UNT001")  // EA
				.price(500000L)
				.typeCode("ITP002")  // 반제품
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Item item2 = Item.builder()
				.id("P0000002")
				.name("반제품 B")
				.unit("UNT001")
				.price(150000L)
				.typeCode("ITP002")  // 반제품
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Item item3 = Item.builder()
				.id("P0000003")
				.name("완제품 C")
				.unit("UNT001")
				.price(700000L)
				.typeCode("ITP003")  // 완제품
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Item item4 = Item.builder()
				.id("P0000004")
				.name("반제품 E")
				.unit("UNT001")
				.price(200000L)
				.typeCode("ITP002")  // 반제품
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Item item5 = Item.builder()
				.id("P0000005")
				.name("완제품 D")
				.unit("UNT001")
				.price(800000L)
				.typeCode("ITP003")  // 완제품
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		item1 = itemRepository.save(item1);
		item2 = itemRepository.save(item2);
		item3 = itemRepository.save(item3);
		item4 = itemRepository.save(item4);
		item5 = itemRepository.save(item5);

		// 자재 추가
		Material material1 = Material.builder()
				.id("M0000001")
				.name("강철판")
				.unit("UNT003")  // kg
				.info("소재")
				.spec("A3, 10mm 두께")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Material material2 = Material.builder()
				.id("M0000002")
				.name("용접봉")
				.unit("UNT011")  // 세트
				.info("소모품")
				.spec("ER70S-6, 3.2mm")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Material material3 = Material.builder()
				.id("M0000003")
				.name("금형용 윤활유")
				.unit("UNT004")  // L
				.info("소모품")
				.spec("산화 방지 윤활유")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Material material4 = Material.builder()
				.id("M0000004")
				.name("완제품 포장재")
				.unit("UNT012")  // box
				.info("포장재")
				.spec("종이 박스, 500x500")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Material material5 = Material.builder()
				.id("M0000005")
				.name("알루미늄 판")
				.unit("UNT003")  // kg
				.info("소재")
				.spec("AL6061, 5mm")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Material material6 = Material.builder()
				.id("M0000006")
				.name("절연테이프")
				.unit("UNT011")  // 세트
				.info("소모품")
				.spec("3M 절연테이프, 20m")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Material material7 = Material.builder()
				.id("M0000007")
				.name("스티커 라벨")
				.unit("UNT012")  // box
				.info("포장재")
				.spec("500매, 바코드 포함")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		material1 = materialRepository.save(material1);
		material2 = materialRepository.save(material2);
		material3 = materialRepository.save(material3);
		material4 = materialRepository.save(material4);
		material5 = materialRepository.save(material5);
		material6 = materialRepository.save(material6);
		material7 = materialRepository.save(material7);

		// BOM 데이터 생성
		Bom bom1 = Bom.builder()
				.parentItem(item1)       // 금형 A
				.childItem(item2)        // 반제품 B
				.childMaterial(null)
				.consumption(2L)
				.build();

		Bom bom2 = Bom.builder()
				.parentItem(item2)       	// 반제품 B
				.childItem(null)
				.childMaterial(material1)	// 강철판
				.consumption(5L)
				.build();

		Bom bom3 = Bom.builder()
				.parentItem(item2)			// 반제품 B
				.childItem(null)
				.childMaterial(material2)	// 용접봉
				.consumption(1L)
				.build();

		Bom bom4 = Bom.builder()
				.parentItem(item3)       // 완제품 C
				.childItem(item1)        // 금형 A
				.childMaterial(null)
				.consumption(1L)
				.build();

		Bom bom5 = Bom.builder()
				.parentItem(item1)       	// 금형 A
				.childItem(null)
				.childMaterial(material3)	// 금형용 윤활유
				.consumption(1L)
				.build();

		Bom bom6 = Bom.builder()
				.parentItem(item3)       	// 완제품 C
				.childItem(null)
				.childMaterial(material4)	// 완제품 포장재
				.consumption(1L)
				.build();

		Bom bom7 = Bom.builder()
				.parentItem(item4)         // 반제품 E
				.childItem(null)
				.childMaterial(material5)  // 알루미늄 판
				.consumption(3L)
				.build();

		Bom bom8 = Bom.builder()
				.parentItem(item4)         // 반제품 E
				.childItem(null)
				.childMaterial(material6)  // 절연테이프
				.consumption(2L)
				.build();

		Bom bom9 = Bom.builder()
				.parentItem(item5)     // 완제품 D
				.childItem(item4)      // 반제품 E
				.childMaterial(null)
				.consumption(1L)
				.build();

		Bom bom10 = Bom.builder()
				.parentItem(item5)     		// 완제품 D
				.childItem(null)
				.childMaterial(material7)	// 스티커 라벨
				.consumption(1L)
				.build();

		bomRepository.save(bom1);
		bomRepository.save(bom2);
		bomRepository.save(bom3);
		bomRepository.save(bom4);
		bomRepository.save(bom5);
		bomRepository.save(bom6);
		bomRepository.save(bom7);
		bomRepository.save(bom8);
		bomRepository.save(bom9);
		bomRepository.save(bom10);
	}

	@Test
	@Transactional
	@Commit
	void insertLineProcessMachineData() {
		// 1. 라인 생성
		Line line1 = Line.builder()
				.name("1번 금형 라인")
				.statusCode("LIS001") // 정지
				.description("금형 가공 전용 라인")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Line line2 = Line.builder()
				.name("2번 조립 라인")
				.statusCode("LIS001") // 정지
				.description("반제품 조립 라인")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		line1 = lineRepository.save(line1);
		line2 = lineRepository.save(line2);

		// 2. 공정 생성
		Process cutting = Process.builder()
				.line(line1)
				.name("절삭 공정")
				.description("강철 절삭")
				.typeCode("PTP001")  // 절삭
				.standardTime(30L)
				.hasMachine("Y")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Process welding = Process.builder()
				.line(line1)
				.name("용접 공정")
				.description("CO2 용접")
				.typeCode("PTP004")  // 용접
				.standardTime(25L)
				.hasMachine("Y")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Process assembly = Process.builder()
				.line(line2)
				.name("조립 공정")
				.description("반제품 조립")
				.typeCode("PTP008")  // 조립
				.standardTime(20L)
				.hasMachine("Y")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Process inspection = Process.builder()
				.line(line2)
				.name("외관 검사")
				.description("검사 공정")
				.typeCode("PTP005")  // 도색
				.standardTime(15L)
				.hasMachine("Y")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Process packaging = Process.builder()
				.line(null)
				.name("포장 공정")
				.description("박스 포장 및 라벨")
				.typeCode("PTP007")  // 도장
				.standardTime(10L)
				.hasMachine("N")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		Process labeling = Process.builder()
				.line(null)
				.name("스티커 부착")
				.description("스티커, 바코드 부착")
				.typeCode("PTP007")  // 도장
				.standardTime(8L)
				.hasMachine("N")
				.createdAt(LocalDateTime.now())
				.createdBy(1L)
				.build();

		cutting = processRepository.save(cutting);
		welding = processRepository.save(welding);
		assembly = processRepository.save(assembly);
		inspection = processRepository.save(inspection);
		processRepository.save(packaging);
		processRepository.save(labeling);

		// 3. 설비 생성
		Machine m1 = Machine.builder()
				.process(cutting)
				.name("CNC 절삭기")
				.manufacturer("두산공작기계")
				.buyDate(LocalDate.of(2022, 1, 15))
				.build();

		Machine m2 = Machine.builder()
				.process(welding)
				.name("CO2 자동 용접기")
				.manufacturer("현대기계")
				.buyDate(LocalDate.of(2023, 3, 1))
				.build();

		Machine m3 = Machine.builder()
				.process(assembly)
				.name("조립 자동화 시스템")
				.manufacturer("ABB")
				.buyDate(LocalDate.of(2021, 8, 25))
				.build();

		Machine m4 = Machine.builder()
				.process(inspection)
				.name("AI 비전 검사기")
				.manufacturer("Keyence")
				.buyDate(LocalDate.of(2024, 4, 5))
				.build();

		machineRepository.save(m1);
		machineRepository.save(m2);
		machineRepository.save(m3);
		machineRepository.save(m4);
	}

	@Test
	@Transactional
	@Commit
	void insertQualityInspectionAndStandardData() {
		// 검사 타입별로 QualityInspection 등록
		List<String> inspectionTypes = List.of(
				"외관 검사", "치수 측정", "무게 측정", "색상 확인", "표면 조도",
				"내열 시험", "내한 시험", "습기 시험", "동작 확인", "내구 시험"
		);

		List<QualityInspection> inspections = new ArrayList<>();
		for (String type : inspectionTypes) {
			QualityInspection inspection = qualityInspectionRepository.save(QualityInspection.builder()
					.name(type + " 검사")
					.inspectionType(type)
					.inspectionMethod("기본 방법") // 필요 시 수정
					.build());
			inspections.add(inspection);
		}

		// 검사 기준 더미 (특정 제품에 대해 모든 검사 항목 등록)
		List<String> itemIds = List.of("P0000001", "P0000002", "P0000003");

		for (String itemId : itemIds) {
			Item item = itemRepository.findById(itemId).orElse(null);
			if (item == null) continue;

			for (QualityInspection inspection : inspections) {
				double base = Math.random() * 100;
				double lower = Math.round((base - 5) * 10.0) / 10.0;
				double upper = Math.round((base + 5) * 10.0) / 10.0;
				double target = Math.round(base * 10.0) / 10.0;

				qualityInspectionStandardRepository.save(QualityInspectionStandard.builder()
						.qualityInspection(inspection)
						.item(item)
						.targetValue(target)
						.upperLimit(upper)
						.lowerLimit(lower)
						.unit("mm") // 필요 시 단위 다르게 지정
						.build());
			}
		}
	}

	@Test
	@Transactional
	@Commit
	void insertStorageClientData() {
		// 1. 창고 생성
		Storage rawMaterialStorage = Storage.builder()
				.name("원자재 창고")
				.address("부산시 강서구 원자재로 100")
				.area("500m²")
				.typeCode("ITP001") // 원자재
				.build();

		Storage semiProductStorage = Storage.builder()
				.name("반제품 창고")
				.address("부산시 강서구 반제품로 200")
				.area("300m²")
				.typeCode("ITP002") // 반제품
				.build();

		Storage finishedProductStorage = Storage.builder()
				.name("완제품 창고")
				.address("부산시 강서구 완제품로 300")
				.area("400m²")
				.typeCode("ITP003") // 완제품
				.build();

		storageRepository.save(rawMaterialStorage);
		storageRepository.save(semiProductStorage);
		storageRepository.save(finishedProductStorage);

		// 2. 거래처 생성
		Client vendor1 = Client.builder()
				.name("삼성전자")
				.businessNumber("1234567890")
				.address("서울특별시 서초구 삼성로 1")
				.contact("02-1234-5678")
				.ceo("이재용")
				.contactManager("김영희")
				.remark("주요 거래처")
				.build();

		Client vendor2 = Client.builder()
				.name("LG화학")
				.businessNumber("9876543210")
				.address("서울특별시 강서구 LG로 2")
				.contact("02-8765-4321")
				.ceo("구광모")
				.contactManager("박철수")
				.remark("소재 공급업체")
				.build();

		clientRepository.save(vendor1);
		clientRepository.save(vendor2);
	}

	@Test
	@Transactional
	@Commit
	void insertStockInboundLotData() {
		// 저장소 조회
		Storage rawMaterialStorage = storageRepository.findByTypeCode("ITP001").orElse(null);
		Storage semiProductStorage = storageRepository.findByTypeCode("ITP002").orElse(null);
		Storage finishedProductStorage = storageRepository.findByTypeCode("ITP003").orElse(null);

		// 제품 및 자재 조회
		Item item1 = itemRepository.findById("P0000001").orElse(null); // 금형 A
		Item item2 = itemRepository.findById("P0000003").orElse(null); // 완제품 C
		Material material1 = materialRepository.findById("M0000001").orElse(null); // 강철판
		Material material2 = materialRepository.findById("M0000003").orElse(null); // 윤활유
		Material material3 = materialRepository.findById("M0000002").orElse(null); // 용접봉
		Material material4 = materialRepository.findById("M0000004").orElse(null); // 완제품 포장재

		LocalDate now = LocalDate.now();

		if (item1 != null && semiProductStorage != null) {
			stockRepository.save(Stock.builder()
					.item(item1)
					.material(null)
					.storage(semiProductStorage)
					.quantity(10L)
					.build());
		}

		if (item2 != null && finishedProductStorage != null) {
			stockRepository.save(Stock.builder()
					.item(item2)
					.material(null)
					.storage(finishedProductStorage)
					.quantity(5L)
					.build());
		}

		if (material1 != null && rawMaterialStorage != null) {
			stockRepository.save(Stock.builder()
					.item(null)
					.material(material1)
					.storage(rawMaterialStorage)
					.quantity(100L)
					.build());
		}

		if (material2 != null && rawMaterialStorage != null) {
			stockRepository.save(Stock.builder()
					.item(null)
					.material(material2)
					.storage(rawMaterialStorage)
					.quantity(20L)
					.build());
		}

		if (material3 != null && rawMaterialStorage != null) {
			stockRepository.save(Stock.builder()
					.item(null)
					.material(material3)
					.storage(rawMaterialStorage)
					.quantity(100L)
					.build());
		}

		if (material4 != null && rawMaterialStorage != null) {
			stockRepository.save(Stock.builder()
					.item(null)
					.material(material4)
					.storage(rawMaterialStorage)
					.quantity(20L)
					.build());
		}

		// 반제품 입고 + 로트
		if (item1 != null && semiProductStorage != null) {
			inboundRepository.save(Inbound.builder()
					.item(item1)
					.material(null)
					.storage(semiProductStorage)
					.quantity(10L)
					.inDate(now)
					.categoryCode("ITP002") // 반제품
					.statusCode("STS003")
					.build());

			lotRepository.save(Lot.builder()
					.id("20230711-ISP-0001")
					.item(item1)
					.material(null)
					.quantity(10L)
					.eventType("ISP")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build());
		}

		// 완제품 입고 + 로트
		if (item2 != null && finishedProductStorage != null) {
			inboundRepository.save(Inbound.builder()
					.item(item2)
					.material(null)
					.storage(finishedProductStorage)
					.quantity(5L)
					.inDate(now)
					.categoryCode("ITP003") // 완제품
					.statusCode("STS003")
					.build());

			lotRepository.save(Lot.builder()
					.id("20240901-ISP-0001")
					.item(item2)
					.material(null)
					.quantity(5L)
					.eventType("ISP")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build());
		}

		// 자재1 입고 + 로트
		if (material1 != null && rawMaterialStorage != null) {
			inboundRepository.save(Inbound.builder()
					.item(null)
					.material(material1)
					.storage(rawMaterialStorage)
					.quantity(100L)
					.inDate(now)
					.categoryCode("ITP001") // 자재
					.statusCode("STS003")
					.build());

			lotRepository.save(Lot.builder()
					.id("20250630-INB-0001")
					.item(null)
					.material(material1)
					.quantity(100L)
					.eventType("INB")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build());
		}

		// 자재2 입고 + 로트
		if (material2 != null && rawMaterialStorage != null) {
			inboundRepository.save(Inbound.builder()
					.item(null)
					.material(material2)
					.storage(rawMaterialStorage)
					.quantity(20L)
					.inDate(now)
					.categoryCode("ITP001") // 자재
					.statusCode("STS003")
					.build());

			lotRepository.save(Lot.builder()
					.id("20250630-INB-0002")
					.item(null)
					.material(material2)
					.quantity(20L)
					.eventType("INB")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build());
		}

		// 자재3 입고 + 로트
		if (material3 != null && rawMaterialStorage != null) {
			inboundRepository.save(Inbound.builder()
					.item(null)
					.material(material3)
					.storage(rawMaterialStorage)
					.quantity(100L)
					.inDate(now)
					.categoryCode("ITP001") // 자재
					.statusCode("STS003")
					.build());

			lotRepository.save(Lot.builder()
					.id("20250630-INB-0003")
					.item(null)
					.material(material3)
					.quantity(100L)
					.eventType("INB")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build());
		}

		// 자재4 입고 + 로트
		if (material4 != null && rawMaterialStorage != null) {
			inboundRepository.save(Inbound.builder()
					.item(null)
					.material(material4)
					.storage(rawMaterialStorage)
					.quantity(20L)
					.inDate(now)
					.categoryCode("ITP001") // 자재
					.statusCode("STS003")
					.build());

			lotRepository.save(Lot.builder()
					.id("20250630-INB-0004")
					.item(null)
					.material(material4)
					.quantity(20L)
					.eventType("INB")
					.createdAt(LocalDateTime.now())
					.createdBy(1L)
					.build());
		}
	}

	@Test
	@Transactional
	@Commit
	void insertPrdctPlanData() {
		// 1. 담당 사원 조회
		Employee employee = employeeRepository.findById(25060001L).orElse(null);

		// 2. 오늘 날짜 기준
		LocalDate today = LocalDate.now();
		String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		// 3. 생산 계획용 아이템 리스트 조회
		List<Item> finishedItems = itemRepository.findAll().stream()
				.filter(item -> "ITP003".equals(item.getTypeCode()))
				.toList();

		int sequence = 1;
		for (Item item : finishedItems) {
			// ID 생성
			String planId = String.format("PP%s-%03d", dateStr, sequence); // ex: PP20250625-001

			// 생산계획 생성
			ProductionPlanning planning = ProductionPlanning.builder()
					.id(planId)
					.item(item)
					.employee(employee)
					.startDate(today)
					.endDate(today.plusDays(5 + sequence))
					.quantity(2L * sequence)
					.build();

			prdctPlanRepository.save(planning);

			sequence++;
		}
	}

	@Test
	@Transactional
	@Commit
	void insertQualityInspectionHistoryDummyData() {
		// 1. 검사 기준(qualityInspection) 조회
		QualityInspection qualityInspection = qualityInspectionRepository.findAll().stream()
				.findFirst()
				.orElse(null);

		// 2. 작업 지시(workOrder) 조회
		WorkOrder workOrder = workOrderRepository.findAll().stream()
				.findFirst()
				.orElse(null);

		// 3. 검사 대상 아이템(item) 조회
		Item item = itemRepository.findAll().stream()
				.findFirst()
				.orElse(null);

		// 4. 더미 데이터 3개 생성
		for (int i = 0; i < 3; i++) {
			QualityInspectionHistory history = QualityInspectionHistory.builder()
					.qualityInspection(qualityInspection)
					.workOrder(workOrder)
					.item(item)
					.lot(null)
					.inspectionDate(null)
					.resultValue(null)
					.resultCode(null)
					.statusCode("STS001")
					.build();

			qualityInspectionHistoryRepository.save(history);
		}
	}

	@Test
	@Transactional
	@Commit
	void insertWorkPerformanceData() {
		// 1. 담당 사원 조회
		Employee employee = employeeRepository.findById(25060001L).orElse(null);

		// 2. 현재 날짜
		LocalDate today = LocalDate.now();

		// 3. 모든 작업지시 조회
		List<WorkOrder> workOrders = workOrderRepository.findAll();

		// 4. WorkPerformance 저장을 위한 repository 선언 필요
		for (WorkOrder workOrder : workOrders) {
			// 작업지시 수량을 반으로 나누어 양품/불량품 설정
			Long totalQuantity = workOrder.getQuantity();
			Long fectiveQuantity = totalQuantity / 2;
			Long defectiveQuantity = totalQuantity - fectiveQuantity;

			// 작업실적 생성
			WorkPerformance performance = WorkPerformance.builder()
					.workOrder(workOrder)
					.endDate(today) // 현재 날짜를 작업 종료일로 설정
					.fectiveQuantity(fectiveQuantity) // 총 수량의 절반을 양품으로
					.defectiveQuantity(defectiveQuantity) // 나머지를 불량품으로
					.employee(employee) // 지정된 직원 ID
					.build();

			// 작업실적 저장 (repository 선언 필요)
			workPerformanceRepository.save(performance);
		}
	}
}