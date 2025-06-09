let rawApprovalData = []; // 원본 데이터를 저장
let testGrid; // 전역 변수로 선언

// grid 초기화
const initGrid = () => {
    const Grid = tui.Grid;

    Grid.applyTheme('default', {
        cell: {
            normal: { border: 'gray' },
            header: { background: 'gray', text: 'white', border: 'gray' },
            rowHeaders: { header: { background: 'gray', text: 'white' } }
        }
    });

    return new Grid({
        el: document.getElementById('approvalGrid'),
        scrollX: false,
        scrollY: true,
        bodyHeight: 400,
        columns: [
            { header: '결재번호', name: 'approvalId', align: 'center' },
            { header: '결재 유형', name: 'apprTypeName', align: 'center' },
            { header: '결재 코드', name: 'apprTypeCode', hidden: true },
            { header: '이름', name: 'displayName', align: 'center' },
            { header: '사번', name: 'displayId', align: 'center' },
            { header: '직급', name: 'positionName', align: 'center' },
            { header: '부서', name: 'deptName', align: 'center' },
            {
                header: '발행일자', name: 'requestedAt', align: 'center',
                formatter: ({ value }) => value ? new Date(value).toISOString().split('T')[0] : ""
            },
            {
                header: '결재 날짜', name: 'confirmedDate', align: 'center',
                formatter: ({ value }) => value ? new Date(value).toISOString().split('T')[0] : ""
            },
            { header: '상태', name: 'approvalStatusName', align: 'center' },
            { header: '상태코드', name: 'approvalStatusCode', hidden: true },
            { header: '상태이름', name: 'approvalStatusName', hidden: true },
            { header: '승인 권자', name: 'approverName', align: 'center' },
            { header: '승인 권자 사번', name: 'approverId', hidden: true },
            { header: '반려 사유', name: 'rejectionReason', hidden: true },
            { header: '발령자 사번', name: 'transferEmpId', hidden: true },
            { header: '발령자 이름', name: 'transferEmpName', hidden: true }
        ]
    });
}

// 메인 초기화
const init = () => {
    testGrid = initGrid(); // ❗ 전역 변수에 할당

    const currentUser = {
        id: "1",
        authCode: "ATH002"
    };

    // 필터 탭 클릭 이벤트
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

            testGrid.resetData(filteredData); // ❗ 전역 변수 사용
        });
    });

    // 검색 버튼
    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        getData().then(res => {
            testGrid.resetData(res.data);
        });
    }, false);

    // 더블클릭 이벤트
    testGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = testGrid.getRow(rowKey);

        if (rowData && rowData.approvalId) {
            let formUrl = "";
            switch (rowData.apprTypeCode) {
                case "APR003":
                    formUrl = "/approval/transferApproval-form";
                    break;
                case "APR002":
                    formUrl = "/approval/vacationApproval-form";
                    break;
                case "APR001":
                    formUrl = "/approval/workApproval-form";
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
                    popup.postMessage({
                        approvalId: rowData.approvalId,
                        apprTypeCode: rowData.apprTypeCode,
                        approvalStatusCode: rowData.approvalStatusCode,
                        approvalStatusName: rowData.approvalStatusName,
                        approverName: rowData.approverName,
                        approverId: rowData.approverId,
                        rejectionReason: rowData.rejectionReason,
                        confirmedDate: rowData.confirmedDate,
                        transferEmpId: rowData.transferEmpId,
                        transferEmpName: rowData.transferEmpName,
                        requesterName: rowData.requesterName, // ✅ 여전히 유효
                        requesterId: rowData.requesterId,
                        userId: currentUser.id,
                        authCode: currentUser.authCode
                    }, "*");

                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });

    // 목록 조회 함수
    window.getData = async function () {
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='ednDate']").value;
        const apprType = document.querySelector("select[name='APR']").value;
        const dept = document.querySelector("select[name='DEP']").value;
        const position = document.querySelector("select[name='POS']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

        if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        const params = new URLSearchParams({
            startDate,
            endDate,
            apprType,
            dept,
            position,
            approvalNameOrEmpId
        });

        try {
            const res = await fetch(`/api/approval?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" }
            });

            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            const data = await res.json();

            const modifiedData = data.data.map(item => {
                const isTransfer = item.apprTypeCode === 'APR003';
                return {
                    ...item,
                    displayName: isTransfer && item.transferEmpName ? item.transferEmpName : item.requesterName,
                    displayId: isTransfer && item.transferEmpId ? item.transferEmpId : item.requesterId
                };
            });


            rawApprovalData = modifiedData;
            testGrid.resetData(modifiedData);
            return { data: modifiedData };

        } catch (e) {
            console.error("데이터 조회 중 오류 발생:", e);
            alert("데이터를 불러오는 중 문제가 발생했습니다.\n입력 값을 확인하거나 관리자에게 문의하세요.");
        }

    }
}

// 페이지 로딩 시 실행
window.onload = () => {
    init();
}
