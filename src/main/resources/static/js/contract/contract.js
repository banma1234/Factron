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

    // 🔑 권한 체크: ATH004면 버튼 표시
    if (user.authCode === 'ATH004') {
        const btn = document.querySelector('.registContract');
        if (btn) btn.style.display = '';
    }

    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault();
        getData();
    });

    document.querySelector('.search__form').addEventListener('submit', function (e) {
        e.preventDefault();
        getData();
    });

    // 수주 등록 버튼 클릭 시
    document.querySelector('.registContract').addEventListener('click', function () {
        const popup = window.open('/contractRegister-form', '_blank', 'width=800,height=1000');
        if (!popup) {
            alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
            return;
        }

        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage({ type: 'init' }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    // 그리드 더블클릭 시 상세 조회
    contractGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = contractGrid.getRow(rowKey);

        if (rowData && rowData.contractId) {
            const popup = window.open('/contractDetail-form', '_blank', 'width=800,height=1000');
            if (!popup) {
                alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
                return;
            }

            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage(rowData, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });

    window.getData = async function () {
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;
        const approvalStatusCode = document.querySelector("select[name='STP']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

        const params = new URLSearchParams({
            startDate,
            endDate,
            approvalStatusCode,
            approvalNameOrEmpId
        });

        try {
            const res = await fetch(`/api/contract?${params.toString()}`);
            if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
            const data = await res.json();
            rawContractData = data.data;
            contractGrid.resetData(data.data);
        } catch (e) {
            console.error("데이터 조회 중 오류 발생:", e);
            alert("데이터를 불러오는 중 문제가 발생했습니다.");
        }
    };

    setSelectBox("STP", "STP");
    getData();
};

window.onload = () => {
    init();
};
