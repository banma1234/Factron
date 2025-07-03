let rawApprovalData = [];
let approvalGrid;

const init = () => {
    approvalGrid = initGrid(
        document.getElementById('approvalGrid'),
        400,
        [
            { header: '결재번호', name: 'approvalId', align: 'center' },
            { header: '결재 유형', name: 'apprTypeName', align: 'center' },
            { header: '결재 코드', name: 'apprTypeCode', hidden: true },
            { header: '거래처', name: 'clientName', align: 'center' },
            { header: '거래처코드', name: 'customerCode', hidden: true },
            { header: '품목', name: 'itemSummary', align: 'center' },
            { header: '담당자', name: 'requesterName', align: 'center' },
            { header: '사번', name: 'requesterId', align: 'center' },
            { header: '발행일자', name: 'requestedAt', align: 'center' },
            { header: '결재 날짜', name: 'confirmedAt', align: 'center' },
            { header: '상태코드', name: 'approvalStatusCode', hidden: true },
            { header: '승인 권자', name: 'approverName', align: 'center' },
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
            { header: '승인 권자 사번', name: 'approverId', hidden: true },
            { header: '반려 사유', name: 'rejectionReason', hidden: true }
        ]
    );

    // 기본 날짜 설정 (최근 30일)
    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    // 필터 탭 클릭 이벤트
    document.querySelectorAll('.filter-tab').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.filter-tab').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            applyFilter(this.dataset.status);
        });
    });

    // 검색 버튼 & 엔터 제출
    document.querySelector(".srhBtn").addEventListener("click", e => { e.preventDefault(); getData(); });
    document.querySelector('.search__form').addEventListener('submit', e => { e.preventDefault(); getData(); });

    // 행 더블클릭 팝업 열기
    approvalGrid.on('dblclick', (e) => {
        const rowData = approvalGrid.getRow(e.rowKey);
        if (!rowData || !rowData.approvalId) return;

        let formUrl = "";
        switch (rowData.apprTypeCode) {
            case "SLS001": formUrl = "/approval/contractApproval-form"; break;
            case "SLS002": formUrl = "/approval/purchaseApproval-form"; break;
            default: alert("유효하지 않은 결재 유형입니다."); return;
        }

        const popup = window.open(formUrl, '_blank', 'width=800,height=1000');
        if (!popup) { alert('팝업 차단 해제 후 다시 시도하세요.'); return; }

        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage(rowData, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    // 첫 로딩 시 데이터 조회
    getData();

    // 공통코드 select 박스 세팅 (필요에 따라)
    setSelectBox("SLS", "SLS");
}

// 필터 적용 함수
function applyFilter(selectedStatus) {
    let filteredData;
    if (selectedStatus === 'ALL') {
        filteredData = rawApprovalData;
    } else if (selectedStatus === 'APPROVED') {
        filteredData = rawApprovalData.filter(row =>
            row.approvalStatusCode === 'APV002' || row.approvalStatusCode === 'APV003'
        );
    } else {
        filteredData = rawApprovalData.filter(row => row.approvalStatusCode === selectedStatus);
    }
    approvalGrid.resetData(filteredData);
}

// 단일 결재 승인/반려 후 갱신
window.refreshSingleApproval = async (approvalId) => {
    try {
        const res = await fetch(`/api/salesApproval/${approvalId}`, { method: "GET" });
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const data = await res.json();
        const item = data.data;

        rawApprovalData = rawApprovalData.map(row =>
            row.approvalId === approvalId ? item : row
        );

        const activeTab = document.querySelector('.filter-tab.active');
        const selectedStatus = activeTab ? activeTab.dataset.status : 'ALL';
        applyFilter(selectedStatus);
    } catch (e) {
        console.error("단일 항목 갱신 오류:", e);
        alert("데이터 갱신 중 오류가 발생했습니다.");
    }
}

// 전체 데이터 조회 함수
window.getData = async () => {
    const startDate = document.querySelector("input[name='startDate']").value;
    const endDate = document.querySelector("input[name='endDate']").value;
    const apprType = document.querySelector("select[name='SLS']").value;
    const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

    if ((startDate && !endDate) || (!startDate && endDate)) {
        alert("시작 및 종료 날짜를 모두 입력해주세요.");
        return;
    }

    const params = new URLSearchParams({ startDate, endDate, apprType, approvalNameOrEmpId });

    try {
        const res = await fetch(`/api/salesApproval?${params.toString()}`);
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const data = await res.json();
        rawApprovalData = data.data;

        const activeTab = document.querySelector('.filter-tab.active');
        const selectedStatus = activeTab ? activeTab.dataset.status : 'ALL';
        applyFilter(selectedStatus);
    } catch (e) {
        console.error("데이터 조회 오류:", e);
        alert("데이터를 불러오는 중 문제가 발생했습니다.");
    }
}

window.onload = () => init();
