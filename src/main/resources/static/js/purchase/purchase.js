let rawPurchaseData = [];
let purchaseGrid;

const init = () => {
    purchaseGrid = initGrid(
        document.getElementById('purchaseGrid'),
        400,
        [
            { header: '결재번호', name: 'approvalId', hidden:true },
            { header: '발주ID', name: 'purchaseId', align: 'center' },
            { header: '발주자 사번', name: 'employeeId', align: 'center' },
            { header: '발주자 이름', name: 'employeeName', align: 'center' },
            { header: '거래처ID', name: 'clientId', hidden:true },
            { header: '거래처명', name: 'clientName', align: 'center' },
            { header: '상태코드', name: 'statusCode', hidden:true },
            { header: '자재 요약', name: 'itemSummary', align: 'center' },
            { header: '총금액', name: 'totalAmount', align: 'center', formatter: ({ value }) => value.toLocaleString() + '원' },
            { header: '등록일', name: 'createdAt', align: 'center' },
            { header: '상태명', name: 'statusName', align: 'center' },
        ]
    );

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

    // 발주 등록 버튼 클릭 시
    document.querySelector('.registPurchase').addEventListener('click', function () {
        const popup = window.open('/purchaseRegister-form', '_blank', 'width=800,height=1000');
        if (!popup) {
            alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
            return;
        }

        const messageHandler = (event) => {
            if (event.data === 'ready') {
                // 초기화 필요 시 메시지 전달 가능
                popup.postMessage({ type: 'init' }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    purchaseGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = purchaseGrid.getRow(rowKey);

        if (rowData && rowData.purchaseId) {
            const popup = window.open('/purchaseDetail-form', '_blank', 'width=800,height=1000');
            if (!popup) {
                alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
                return;
            }

            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage(rowData, "*");  // 여기서 rowData 보내기
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
            const res = await fetch(`/api/purchase?${params.toString()}`);
            if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
            const data = await res.json();
            rawPurchaseData = data.data;
            purchaseGrid.resetData(data.data);
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
