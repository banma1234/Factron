// 전역 변수: 원본 결재 데이터와 TOAST UI Grid 인스턴스 저장용
let rawApprovalData = [];
let approvalGrid;

const init = () => {
    // 그리드 초기화: 컬럼 정의 및 그리드 높이 설정
    approvalGrid = initGrid(
        document.getElementById('approvalGrid'),
        400,
        [
            { header: '결재번호', name: 'approvalId', align: 'center' },
            { header: '결재 유형', name: 'apprTypeName', align: 'center' },
            { header: '거래처', name: 'clientName', align: 'center' },
            { header: '품목', name: 'itemSummary', align: 'center' },
            { header: '담당자', name: 'requesterName', align: 'center' },
            { header: '사번', name: 'requesterId', align: 'center' },
            { header: '발행일자', name: 'requestedAt', align: 'center' },
            { header: '결재 날짜', name: 'confirmedAt', align: 'center' },
            {
                header: '상태', name: 'approvalStatusName', align: 'center',
                // 상태코드에 따라 색상 다르게 표시
                formatter: ({ row }) => {
                    switch (row.approvalStatusCode) {
                        case 'APV001': return `<span style="color:green;">${row.approvalStatusName}</span>`;
                        case 'APV002': return `<span style="color:blue;">${row.approvalStatusName}</span>`;
                        case 'APV003': return `<span style="color:red;">${row.approvalStatusName}</span>`;
                        default: return row.approvalStatusName || '';
                    }
                }
            },
            { header: '승인 권자', name: 'approverName', align: 'center' },
            { header: '반려 사유', name: 'rejectionReason', hidden: true }, // 숨겨진 컬럼
            { header: '결재 코드', name: 'apprTypeCode', hidden: true },
            { header: '승인 권자 사번', name: 'approverId', hidden: true },
            { header: '상태코드', name: 'approvalStatusCode', hidden: true }
        ]
    );

    // 기본 날짜 세팅 (오늘 날짜와 30일 전)
    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    // 필터 탭 클릭 시 활성화 표시 및 필터 적용
    document.querySelectorAll('.filter-tab').forEach(btn =>
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-tab').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            applyFilter(btn.dataset.status);
        })
    );

    // 검색 버튼 및 엔터 제출 시 getData 호출
    document.querySelector(".srhBtn").addEventListener("click", e => { e.preventDefault(); getData(); });
    document.querySelector('.search__form').addEventListener('submit', e => { e.preventDefault(); getData(); });

    // 그리드 행 더블 클릭 시 해당 결재 상세 폼 열기
    approvalGrid.on('dblclick', e => openApprovalForm(approvalGrid.getRow(e.rowKey)));

    // 결재 유형 select 박스 초기 세팅
    setSelectBox("SLS", "SLS");

    // 초기 데이터 조회
    getData();
};

/**
 * 필터 상태에 따라 데이터를 필터링하고 그리드 갱신
 * @param {string} status 필터 상태 (ALL, APPROVED, APV001 등)
 */
function applyFilter(status) {
    let filtered = rawApprovalData;
    if (status !== 'ALL') {
        if (status === 'APPROVED') {
            // 승인 및 반려 상태 모두 포함
            filtered = rawApprovalData.filter(r => r.approvalStatusCode === 'APV002' || r.approvalStatusCode === 'APV003');
        } else {
            // 특정 상태 코드로 필터링
            filtered = rawApprovalData.filter(r => r.approvalStatusCode === status);
        }
    }
    approvalGrid.resetData(filtered); // 필터링된 데이터로 그리드 업데이트
}

/**
 * 행 데이터 기반으로 상세 결재 폼 팝업 열기
 * @param {object} rowData 선택된 행 데이터
 */
function openApprovalForm(rowData) {
    if (!rowData || !rowData.approvalId) return;

    let url = "";
    switch (rowData.apprTypeCode) {
        case "SLS001": url = "/approval/contractApproval-form"; break;
        case "SLS002": url = "/approval/purchaseApproval-form"; break;
        default: alert("유효하지 않은 결재 유형입니다."); return;
    }

    const popup = window.open(url, '_blank', 'width=800,height=1000');
    if (!popup) { alert('팝업 차단 해제 후 다시 시도하세요.'); return; }

    // 자식창 준비 완료 메시지 수신 시 데이터 전달 후 이벤트 리스너 제거
    const handler = e => {
        if (e.data === 'ready') {
            popup.postMessage(rowData, "*");
            window.removeEventListener("message", handler);
        }
    };
    window.addEventListener("message", handler);
}

/**
 * 단일 승인 또는 반려 처리 후 해당 행 데이터만 갱신
 * @param {number} id 결재 아이디
 */
window.refreshSingleApproval = async (id) => {
    try {
        const res = await fetch(`/api/salesApproval/${id}`);
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const item = (await res.json()).data;

        // 기존 데이터 중 해당 아이디 데이터만 교체
        rawApprovalData = rawApprovalData.map(r => r.approvalId === id ? item : r);
        // 현재 활성 탭 필터 유지하며 그리드 갱신
        applyFilter(document.querySelector('.filter-tab.active').dataset.status);
    } catch (e) {
        console.error(e);
        alert("갱신 오류 발생");
    }
};

/**
 * 검색 조건에 따른 데이터 조회 및 그리드에 세팅
 */
window.getData = async () => {
    const params = new URLSearchParams({
        startDate: document.querySelector("input[name='startDate']").value,
        endDate: document.querySelector("input[name='endDate']").value,
        apprType: document.querySelector("select[name='SLS']").value,
        approvalNameOrEmpId: document.querySelector("input[name='srhName']").value,
        clientName: document.querySelector("input[name='clientName']").value
    });

    try {
        const res = await fetch(`/api/salesApproval?${params}`);
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        rawApprovalData = (await res.json()).data; // 원본 데이터 저장
        applyFilter(document.querySelector('.filter-tab.active').dataset.status); // 필터 적용해 그리드 갱신
    } catch (e) {
        console.error(e);
        alert("데이터 조회 오류");
    }
};

// 페이지 로드 시 초기화 함수 실행
window.onload = init;
