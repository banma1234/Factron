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
            { header: '상태', name: 'approvalStatusName', align: 'center' },
            { header: '상태코드', name: 'approvalStatusCode', hidden: true },
            { header: '승인 권자', name: 'approverName', align: 'center' },
            { header: '승인 권자 사번', name: 'approverId', hidden: true },
            { header: '반려 사유', name: 'rejectionReason', hidden: true }
        ]
    );

    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    document.querySelectorAll('.filter-tab').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.filter-tab').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            const selectedStatus = this.dataset.status;
            let filteredData = [];

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
        });
    });

    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault();
        getData();
    });

    document.querySelector('.search__form').addEventListener('submit', function (e) {
        e.preventDefault();
        getData();
    });

    approvalGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = approvalGrid.getRow(rowKey);

        if (rowData && rowData.approvalId) {
            let formUrl = "";
            switch (rowData.apprTypeCode) {
                case "SLS001":
                    formUrl = "/approval/contractApproval-form";
                    break;
                case "SLS002":
                    formUrl = "/approval/purchaseApproval-form";
                    break;
                default:
                    alert("유효하지 않은 결재 유형입니다.");
                    return;
            }

            const popup = window.open(formUrl, '_blank', 'width=800,height=800');
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
        const apprType = document.querySelector("select[name='SLS']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

        if ((startDate && !endDate) || (!startDate && endDate)) {
            alert("시작 및 종료 날짜를 모두 입력해주세요.");
            return { data: [] };
        }

        if (startDate && endDate) {
            const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
            if (!dateRegex.test(startDate) || isNaN(Date.parse(startDate)) ||
                !dateRegex.test(endDate) || isNaN(Date.parse(endDate))) {
                alert("날짜 형식이 올바르지 않습니다.");
                return { data: [] };
            }
            if (new Date(startDate) > new Date(endDate)) {
                alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
                return { data: [] };
            }
        }

        const params = new URLSearchParams({
            startDate,
            endDate,
            apprType,
            approvalNameOrEmpId
        });

        try {
            const res = await fetch(`/api/salesApproval?${params.toString()}`);
            if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
            const data = await res.json();
            rawApprovalData = data.data;
            approvalGrid.resetData(data.data);
            return { data: data.data };
        } catch (e) {
            console.error("데이터 조회 중 오류 발생:", e);
            alert("데이터를 불러오는 중 문제가 발생했습니다.\n입력 값을 확인하거나 관리자에게 문의하세요.");
        }
    };

    setSelectBox("SLS", "SLS");
    getData();
};

window.onload = () => {
    init();
};
