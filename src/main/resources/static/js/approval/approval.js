// 전역 변수: 원본 결재 데이터 및 그리드 인스턴스
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
            { header: '이름', name: 'displayName', align: 'center' },
            { header: '사번', name: 'displayId', align: 'center' },
            { header: '직급', name: 'positionName', align: 'center' },
            { header: '부서', name: 'deptName', align: 'center' },
            { header: '발행일자', name: 'requestedAt', align: 'center' },
            { header: '결재 날짜', name: 'confirmedDate', align: 'center' },
            { header: '상태', name: 'approvalStatusName', align: 'center' },
            { header: '상태코드', name: 'approvalStatusCode', hidden: true },
            { header: '상태이름', name: 'approvalStatusName', hidden: true },
            { header: '승인 권자', name: 'approverName', align: 'center' },
            { header: '승인 권자 사번', name: 'approverId', hidden: true },
            { header: '반려 사유', name: 'rejectionReason', hidden: true },
            { header: '발령자 사번', name: 'transferEmpId', hidden: true },
            { header: '발령자 이름', name: 'transferEmpName', hidden: true }
        ]
    );

    // 기본 날짜 설정
    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);
    document.querySelector('input[name="startDate"]').value = pastDate.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today;

    // 필터 탭 클릭 이벤트 등록: 탭을 클릭할 때마다 그리드에 표시할 데이터 필터링
    document.querySelectorAll('.filter-tab').forEach(btn => {
        btn.addEventListener('click', function () {
            // 모든 필터 탭에서 'active' 클래스를 제거 후, 클릭한 탭에만 'active' 클래스 추가
            document.querySelectorAll('.filter-tab').forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            // 선택된 상태 값 (예: 'ALL', 'APPROVED' 등)을 데이터 속성에서 가져옴
            const selectedStatus = this.dataset.status;
            let filteredData = [];

            // 'ALL'인 경우 원본 데이터를 그대로 사용,
            // 'APPROVED'인 경우 승인 상태 코드(APV002 또는 APV003)를 가진 데이터만 필터링,
            // 그 외에는 선택된 상태 코드와 일치하는 데이터를 필터링
            if (selectedStatus === 'ALL') {
                filteredData = rawApprovalData;
            } else if (selectedStatus === 'APPROVED') {
                filteredData = rawApprovalData.filter(row =>
                    row.approvalStatusCode === 'APV002' || row.approvalStatusCode === 'APV003'
                );
            } else {
                filteredData = rawApprovalData.filter(row => row.approvalStatusCode === selectedStatus);
            }

            // 필터링된 데이터로 그리드를 업데이트
            approvalGrid.resetData(filteredData); // 전역 변수 사용
        });
    });

    // 검색 버튼
    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        getData();
    }, false);

    // 엔터 시 검색
    document.querySelector('.search__form').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출(새로고침) 방지

        getData();
    });

    // 그리드 행 더블클릭 이벤트: 특정 행을 더블클릭 시 팝업 창을 열어 상세 정보 제공
    approvalGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = approvalGrid.getRow(rowKey);

        // 유효한 행 데이터가 있고, 결재번호가 있는 경우
        if (rowData && rowData.approvalId) {
            let formUrl = "";
            // 결재 유형 코드에 따라 다른 팝업 페이지 URL을 지정
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

            // 팝업 창 열기 (크기는 800x800). 팝업 차단 여부 확인
            const popup = window.open(formUrl, '_blank', 'width=800,height=800');
            if (!popup) {
                alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
                return;
            }

            // 메시지 핸들러 함수 정의: 팝업 창에서 'ready' 메시지가 오면 데이터 전송
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    // 팝업 창에 현재 행 데이터와 현재 사용자 정보를 postMessage로 전달
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
                        requesterName: rowData.requesterName, // 요청자 이름 (유효한 경우)
                        requesterId: rowData.requesterId,
                        requestedAt: rowData.requestedAt
                    }, "*");

                    // 메시지 전송 후, 이벤트 핸들러 제거하여 중복 호출 방지
                    window.removeEventListener("message", messageHandler);
                }
            };
            // window에 메시지 이벤트 리스너 등록
            window.addEventListener("message", messageHandler);
        }
    });

    // 데이터 조회 함수 (글로벌 함수로 window에 등록하여 다른 곳에서도 사용 가능)
    window.getData = async function () {
        // 검색 폼에서 입력된 검색 조건 가져오기
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;
        const apprType = document.querySelector("select[name='APR']").value;
        const dept = document.querySelector("select[name='DEP']").value;
        const position = document.querySelector("select[name='POS']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;
        // validation
        if ((startDate && !endDate) || (!startDate && endDate)) {
            alert("시작 및 종료 날짜를 모두 입력해주세요.");
            return { data: [] };
        }

        if (startDate && endDate) {
            const dateRegex = /^\d{4}-\d{2}-\d{2}$/;

            if (!dateRegex.test(startDate) || isNaN(Date.parse(startDate))
                || !dateRegex.test(endDate) || isNaN(Date.parse(endDate))) {
                alert("날짜 형식이 올바르지 않습니다.");
                return { data: [] };
            }
            if (new Date(startDate) > new Date(endDate)) {
                alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
                return { data: [] };
            }
        }

        // URLSearchParams를 사용해 검색 파라미터를 쿼리 스트링으로 생성
        const params = new URLSearchParams({
            startDate,
            endDate,
            apprType,
            dept,
            position,
            approvalNameOrEmpId
        });

        try {
            // 서버 API 호출: GET 방식으로 /api/approval 엔드포인트에 파라미터 전달
            const res = await fetch(`/api/approval?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" }
            });

            // 응답이 정상적이지 않은 경우 오류 발생
            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            // 응답 데이터를 JSON으로 파싱
            const data = await res.json();

            // 받아온 데이터에서 각 항목을 수정:
            // 결재 유형이 'APR003' (발령일 경우)인 경우, displayName과 displayId를 transferEmpName과 transferEmpId로 대체
            const modifiedData = data.data.map(item => {
                const isTransfer = item.apprTypeCode === 'APR003';
                return {
                    ...item,
                    displayName: isTransfer && item.transferEmpName ? item.transferEmpName : item.requesterName,
                    displayId: isTransfer && item.transferEmpId ? item.transferEmpId : item.requesterId
                };
            });

            // 전역 원본 데이터 업데이트 및 그리드 데이터 재설정
            rawApprovalData = modifiedData;
            approvalGrid.resetData(modifiedData);
            return { data: modifiedData };

        } catch (e) {
            // 에러 발생 시 콘솔에 로그 출력하고 사용자에게 경고 메시지 표시
            console.error("데이터 조회 중 오류 발생:", e);
            alert("데이터를 불러오는 중 문제가 발생했습니다.\n입력 값을 확인하거나 관리자에게 문의하세요.");
        }
    }

    // 공통코드 세팅
    setSelectBox("APR", "APR");
    setSelectBox("DEP", "DEP");
    setSelectBox("POS", "POS");

    // 페이지 진입 시 바로 리스트 호출
    getData();
}

// 페이지 로딩이 완료되면 init 함수 실행
window.onload = () => {
    init();
}
