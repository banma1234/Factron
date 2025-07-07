let rawContractData = [];
let contractGrid;

const init = () => {
    contractGrid = initGrid(
        document.getElementById('contractGrid'),
        400,
        [
            { header: '결재번호', name: 'approvalId', hidden: true },
            { header: '수주ID', name: 'contractId', align: 'center' },
            { header: '수주자 사번', name: 'employeeId', align: 'center' },
            { header: '수주자 이름', name: 'employeeName', align: 'center' },
            { header: '거래처ID', name: 'clientId', hidden: true },
            { header: '거래처명', name: 'clientName', align: 'center' },
            { header: '상태코드', name: 'statusCode', hidden: true },
            { header: '품목 요약', name: 'itemSummary', align: 'center' },
            { header: '총금액', name: 'totalAmount', align: 'center', formatter: ({ value }) => value.toLocaleString() + '원' },
            { header: '납기일', name: 'deadline', align: 'center' },
            { header: '등록일', name: 'createdAt', align: 'center' },
            {
                header: '상태명', name: 'statusName', align: 'center',
                formatter: ({ row }) => {
                    const code = row.statusCode;
                    if (code === 'STP001' || code === 'STP002') return `<span style="color:green;">${row.statusName}</span>`;
                    if (code === 'STP004') return `<span style="color:blue;">${row.statusName}</span>`;
                    if (code === 'STP003' || code === 'STP005') return `<span style="color:red;">${row.statusName}</span>`;
                    return row.statusName || '';
                }
            }
        ]
    );

    // 🔑 권한 체크
    if (user.authCode === 'ATH004') {
        const btn = document.querySelector('.registContract');
        if (btn) btn.style.display = '';
    }

    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    document.querySelector(".srhBtn").addEventListener("click", (e) => {
        e.preventDefault();
        getData();
    });

    // 이름/사번, 거래처 입력창에서 엔터로 검색
    document.querySelectorAll('input[name="srhName"], input[name="clientName"]').forEach(input => {
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                getData();
            }
        });
    });

    document.querySelector('.search__form').addEventListener('submit', (e) => {
        e.preventDefault();
        getData();
    });

    document.querySelector('.registContract').addEventListener('click', () => {
        const popup = window.open('/contractRegister-form', '_blank', 'width=800,height=1000');
        if (!popup) return alert('팝업 차단 해제 후 다시 시도하세요.');

        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage({ type: 'init' }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    contractGrid.on('dblclick', (e) => {
        const rowData = contractGrid.getRow(e.rowKey);
        if (rowData && rowData.contractId) {
            const popup = window.open('/contractDetail-form', '_blank', 'width=800,height=1000');
            if (!popup) return alert('팝업 차단 해제 후 다시 시도하세요.');

            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage(rowData, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });

    getData();
    setSelectBox("STP", "STP");
};

window.getData = async () => {
    const startDate = document.querySelector("input[name='startDate']").value;
    const endDate = document.querySelector("input[name='endDate']").value;
    const approvalStatusCode = document.querySelector("select[name='STP']").value;
    const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;
    const clientName = document.querySelector("input[name='clientName']").value;

    const params = new URLSearchParams({
        startDate,
        endDate,
        approvalStatusCode,
        approvalNameOrEmpId,
        clientName
    });

    try {
        const res = await fetch(`/api/contract?${params.toString()}`);
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const data = await res.json();
        rawContractData = data.data;
        contractGrid.resetData(data.data);
    } catch (e) {
        console.error("데이터 조회 오류:", e);
        alert("데이터를 불러오는 중 문제가 발생했습니다.");
    }
};

window.onload = () => init();
