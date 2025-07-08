let rawPurchaseData = [];  // 서버에서 받아온 발주 데이터 원본 저장용
let purchaseGrid;          // TOAST UI Grid 인스턴스

const init = () => {
    // 발주 그리드 초기화
    purchaseGrid = initGrid(
        document.getElementById('purchaseGrid'),
        400,
        [
            { header: '결재번호', name: 'approvalId', hidden: true }, // 숨김 처리된 컬럼
            { header: '발주ID', name: 'purchaseId', align: 'center' },
            { header: '발주자 사번', name: 'employeeId', align: 'center' },
            { header: '발주자 이름', name: 'employeeName', align: 'center' },
            { header: '거래처ID', name: 'clientId', hidden: true },
            { header: '거래처명', name: 'clientName', align: 'center' },
            { header: '상태코드', name: 'statusCode', hidden: true },
            { header: '자재 요약', name: 'itemSummary', align: 'center' },
            {
                header: '총금액', name: 'totalAmount', align: 'center',
                formatter: ({ value }) => value.toLocaleString() + '원' // 금액 천단위 콤마 + 원 표시
            },
            { header: '등록일', name: 'createdAt', align: 'center' },
            {
                header: '상태명', name: 'statusName', align: 'center',
                formatter: ({ row }) => {
                    const code = row.statusCode;
                    // 상태코드별 색상 다르게 표시
                    if (code === 'STP001' || code === 'STP002') {
                        return `<span style="color:green;">${row.statusName}</span>`;
                    }
                    if (code === 'STP004') {
                        return `<span style="color:blue;">${row.statusName}</span>`;
                    }
                    if (code === 'STP003' || code === 'STP005') {
                        return `<span style="color:red;">${row.statusName}</span>`;
                    }
                    return row.statusName || '';
                }
            }
        ]
    );

    // 권한이 ATH004인 경우 발주 등록 버튼 노출
    if (user.authCode === 'ATH004' || user.authCode === 'ATH003') {
        const btn = document.querySelector('.registPurchase');
        if (btn) btn.style.display = '';
    }

    // 날짜 기본값 설정 (오늘 기준 30일 전부터 오늘까지)
    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    // 검색 버튼 클릭 이벤트
    document.querySelector(".srhBtn").addEventListener("click", e => {
        e.preventDefault();
        getData();
    });

    // 검색 폼 submit 이벤트
    document.querySelector('.search__form').addEventListener('submit', e => {
        e.preventDefault();
        getData();
    });

    // 거래처명 입력란에서 Enter키 입력 시 검색
    document.querySelector('input[name="clientName"]').addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            e.preventDefault();
            getData();
        }
    });

    // 이름/사번 입력란에서 Enter키 입력 시 검색
    document.querySelector('input[name="srhName"]').addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            e.preventDefault();
            getData();
        }
    });

    // 발주 등록 버튼 클릭 시 팝업 열기
    document.querySelector('.registPurchase').addEventListener('click', () => {
        const popup = window.open('/purchaseRegister-form', '_blank', 'width=800,height=1000');
        if (!popup) {
            alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
            return;
        }
        // 팝업이 준비되면 초기화 메시지 전달
        const messageHandler = event => {
            if (event.data === 'ready') {
                popup.postMessage({ type: 'init' }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    // 그리드 더블클릭 시 상세 팝업 열기
    purchaseGrid.on('dblclick', e => {
        const rowKey = e.rowKey;
        const rowData = purchaseGrid.getRow(rowKey);
        if (rowData && rowData.purchaseId) {
            const popup = window.open('/purchaseDetail-form', '_blank', 'width=800,height=1000');
            if (!popup) {
                alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
                return;
            }
            // 팝업이 준비되면 해당 데이터 전달
            const messageHandler = event => {
                if (event.data === 'ready') {
                    popup.postMessage(rowData, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });

    // 발주 데이터 조회 함수 (전역에 노출)
    window.getData = async () => {
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;
        const approvalStatusCode = document.querySelector("select[name='STP']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;
        const clientName = document.querySelector("input[name='clientName']").value;

        // 검색 조건 파라미터 생성
        const params = new URLSearchParams({
            startDate,
            endDate,
            approvalStatusCode,
            approvalNameOrEmpId,
            clientName
        });

        try {
            const res = await fetch(`/api/purchase?${params.toString()}`);
            if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
            const data = await res.json();
            rawPurchaseData = data.data;
            purchaseGrid.resetData(data.data); // 그리드에 데이터 세팅
        } catch (e) {
            console.error("데이터 조회 중 오류 발생:", e);
            alert("데이터를 불러오는 중 문제가 발생했습니다.");
        }
    };

    setSelectBox("STP", "STP"); // 상태 셀렉트 박스 초기화
    getData(); // 초기 데이터 로드
};

window.onload = () => {
    init();
};
