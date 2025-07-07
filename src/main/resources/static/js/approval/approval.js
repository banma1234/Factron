// 전역 변수: 전체 결재 데이터를 보관할 배열과 그리드 인스턴스
let rawApprovalData = [];
let approvalGrid;

const init = () => {
    // TOAST UI Grid 초기화 (컬럼 정의 포함)
    approvalGrid = initGrid(
        document.getElementById('approvalGrid'),
        400, // 높이
        [
            // 컬럼 정의
            { header: '결재번호', name: 'approvalId', align: 'center' },
            { header: '결재 유형', name: 'apprTypeName', align: 'center' },
            { header: '결재 코드', name: 'apprTypeCode', hidden: true },
            { header: '이름', name: 'displayName', align: 'center' },
            { header: '사번', name: 'displayId', align: 'center' },
            { header: '직급', name: 'positionName', align: 'center' },
            { header: '부서', name: 'deptName', align: 'center' },
            { header: '발행일자', name: 'requestedAt', align: 'center' },
            { header: '결재 날짜', name: 'confirmedDate', align: 'center' },
            { header: '상태코드', name: 'approvalStatusCode', hidden: true },
            { header: '상태이름', name: 'approvalStatusName', hidden: true },
            { header: '승인 권자', name: 'approverName', align: 'center' },
            // 상태 컬럼: 상태코드에 따라 색상 다르게 표시
            {
                header: '상태', name: 'approvalStatusName', align: 'center',
                formatter: ({ row }) => {
                    switch (row.approvalStatusCode) {
                        case 'APV001': return `<span style="color:green;">${row.approvalStatusName}</span>`;
                        case 'APV002': return `<span style="color:blue;">${row.approvalStatusName}</span>`;
                        case 'APV003': return `<span style="color:red;">${row.approvalStatusName}</span>`;
                        default: return row.approvalStatusName || '';
                    }
                }
            },
            // 숨김 정보: 상세화면이나 전송에 사용
            { header: '승인 권자 사번', name: 'approverId', hidden: true },
            { header: '반려 사유', name: 'rejectionReason', hidden: true },
            { header: '발령자 사번', name: 'transferEmpId', hidden: true },
            { header: '발령자 이름', name: 'transferEmpName', hidden: true }
        ]
    );

    // 기본 날짜 설정: 오늘과 30일 전
    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    // 필터 탭(전체결재, 결재대기, 결재완료) 클릭 이벤트 등록
    document.querySelectorAll('.filter-tab').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.filter-tab').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            applyFilter(this.dataset.status); // 선택한 상태로 필터링
        });
    });

    // 검색 버튼 클릭 시 조회
    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault(); // 기본 form 제출 막기
        getData();
    });

    // 엔터로 검색 (form submit)
    document.querySelector('.search__form').addEventListener('submit', function(e) {
        e.preventDefault();
        getData();
    });

    // 그리드 행 더블클릭 시 상세 팝업 열기
    approvalGrid.on('dblclick', (e) => {
        const rowData = approvalGrid.getRow(e.rowKey);
        if (!rowData || !rowData.approvalId) return;

        let formUrl = "";
        switch (rowData.apprTypeCode) {
            case "APR003": formUrl = "/approval/transferApproval-form"; break; // 발령
            case "APR002": formUrl = "/approval/vacationApproval-form"; break; // 휴가
            case "APR001": formUrl = "/approval/workApproval-form"; break;     // 근무
            default: alert("유효하지 않은 결재 유형입니다."); return;
        }

        const popup = window.open(formUrl, '_blank', 'width=800,height=800');
        if (!popup) { alert('팝업 차단 해제 후 다시 시도하세요.'); return; }

        // 팝업이 준비되면 데이터 전송
        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage(rowData, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    // 공통코드 select 박스 초기화
    setSelectBox("APR", "APR"); // 결재유형
    setSelectBox("DEP", "DEP"); // 부서
    setSelectBox("POS", "POS"); // 직급

    // 페이지 로딩 시 최초 데이터 조회
    getData();
};

/**
 * 현재 선택된 탭 상태에 맞게 rawApprovalData 필터링
 */
function applyFilter(selectedStatus) {
    let filtered = [];
    if (selectedStatus === 'ALL') {
        filtered = rawApprovalData;
    } else if (selectedStatus === 'APPROVED') {
        filtered = rawApprovalData.filter(r => r.approvalStatusCode === 'APV002' || r.approvalStatusCode === 'APV003');
    } else {
        filtered = rawApprovalData.filter(r => r.approvalStatusCode === selectedStatus);
    }
    approvalGrid.resetData(filtered);
}

/**
 * 단일 항목 승인/반려 후 새로고침 시 사용
 */
window.refreshSingleApproval = async (approvalId) => {
    try {
        const res = await fetch(`/api/approval/${approvalId}`, { method: "GET" });
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const data = await res.json();
        const item = data.data;

        const isTransfer = item.apprTypeCode === 'APR003';
        // 발령이면 발령자 정보, 아니면 요청자 정보 사용
        item.displayName = isTransfer ? item.transferEmpName : item.requesterName;
        item.displayId = isTransfer ? item.transferEmpId : item.requesterId;
        item.positionName = isTransfer ? item.transferPositionName : item.positionName;
        item.deptName = isTransfer ? item.transferDeptName : item.deptName;

        // 전역 데이터 갱신
        rawApprovalData = rawApprovalData.map(row =>
            row.approvalId === approvalId ? item : row
        );

        // 현재 선택된 탭 유지
        const activeTab = document.querySelector('.filter-tab.active');
        const selectedStatus = activeTab ? activeTab.dataset.status : 'ALL';
        applyFilter(selectedStatus);
    } catch (e) {
        console.error("단일 항목 갱신 오류:", e);
        alert("데이터 갱신 중 오류가 발생했습니다.");
    }
};

/**
 * 전체 결재 목록 조회 함수
 */
window.getData = async () => {
    const startDate = document.querySelector("input[name='startDate']").value;
    const endDate = document.querySelector("input[name='endDate']").value;
    const apprType = document.querySelector("select[name='APR']").value;
    const dept = document.querySelector("select[name='DEP']").value;
    const position = document.querySelector("select[name='POS']").value;
    const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

    // 날짜 입력 검증
    if ((startDate && !endDate) || (!startDate && endDate)) {
        alert("시작 및 종료 날짜를 모두 입력해주세요.");
        return;
    }

    const params = new URLSearchParams({ startDate, endDate, apprType, dept, position, approvalNameOrEmpId });
    try {
        const res = await fetch(`/api/approval?${params.toString()}`);
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const data = await res.json();

        // 발령이면 발령자 정보, 아니면 요청자 정보 사용
        const modified = data.data.map(item => {
            const isTransfer = item.apprTypeCode === 'APR003';
            return {
                ...item,
                displayName: isTransfer ? item.transferEmpName : item.requesterName,
                displayId: isTransfer ? item.transferEmpId : item.requesterId,
                positionName: isTransfer ? item.transferPositionName : item.positionName,
                deptName: isTransfer ? item.transferDeptName : item.deptName
            };
        });

        rawApprovalData = modified;
        approvalGrid.resetData(modified);
    } catch (e) {
        console.error("데이터 조회 오류:", e);
        alert("데이터를 불러오는 중 문제가 발생했습니다.");
    }
};

// 페이지 로딩 시 init 함수 호출
window.onload = () => init();
