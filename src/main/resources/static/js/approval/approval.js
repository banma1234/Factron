// 원본 결재 데이터 배열: 서버로부터 받아온 전체 결재 데이터를 저장하는 전역 변수
let rawApprovalData = [];

// 그리드를 담을 전역 변수
let testGrid;

// grid 초기화 함수
const initGrid = () => {
    // TUI Grid 라이브러리 사용
    const Grid = tui.Grid;

    // TUI Grid의 테마 설정: 기본 테마('default')에 대해 셀, 헤더, 행 헤더의 스타일을 지정
    Grid.applyTheme('default', {
        cell: {
            normal: { border: 'gray' }, // 일반 셀의 테두리 색상 지정
            header: { background: 'gray', text: 'white', border: 'gray' }, // 헤더 셀의 배경, 텍스트, 테두리 색상 지정
            rowHeaders: { header: { background: 'gray', text: 'white' } } // 행 헤더의 헤더 부분 스타일 지정
        }
    });

    // 신규 TUI Grid 인스턴스 생성
    return new Grid({
        // 그리드를 표시할 DOM 요소 지정 (ID가 'approvalGrid'인 요소)
        el: document.getElementById('approvalGrid'),
        scrollX: false,  // 수평 스크롤 비활성화
        scrollY: true,   // 수직 스크롤 활성화
        bodyHeight: 400, // 그리드 바디의 높이를 400px로 설정
        // 그리드의 열 설정. 각 열은 헤더 텍스트, 데이터 키, 정렬 및 숨김 여부 등을 설정
        columns: [
            { header: '결재번호', name: 'approvalId', align: 'center' },
            { header: '결재 유형', name: 'apprTypeName', align: 'center' },
            { header: '결재 코드', name: 'apprTypeCode', hidden: true }, // 숨김 처리된 열
            { header: '이름', name: 'displayName', align: 'center' },
            { header: '사번', name: 'displayId', align: 'center' },
            { header: '직급', name: 'positionName', align: 'center' },
            { header: '부서', name: 'deptName', align: 'center' },
            {
                header: '발행일자',
                name: 'requestedAt',
                align: 'center',
                // formatter를 사용해 날짜 값을 ISO 문자열 형식(YYYY-MM-DD)로 변환 후 보여줌
                formatter: ({ value }) => value ? new Date(value).toISOString().split('T')[0] : ""
            },
            {
                header: '결재 날짜',
                name: 'confirmedDate',
                align: 'center',
                // 날짜 형식 변환 formatter: 값이 존재할 경우 ISO 날짜 형식으로 변환
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

// 메인 초기화 함수: 페이지 로딩 시 실행되며 그리드 초기화, 이벤트 등록, 데이터 조회 등을 수행
const init = () => {
    testGrid = initGrid(); // 전역 변수 testGrid에 초기화된 그리드 인스턴스 할당

    // 현재 사용자 정보 (예시: id와 인증코드 저장)
    const currentUser = {
        id: "1",
        authCode: "ATH002"
    };

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
            testGrid.resetData(filteredData); // 전역 변수 사용
        });
    });

    // 검색 버튼 클릭 이벤트: 사용자가 검색 버튼을 클릭하면 데이터 조회 실행
    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        // 기본 이벤트 동작(폼 제출 등)을 막음
        e.preventDefault();
        e.stopPropagation();

        // getData() 함수를 호출해 데이터를 조회하고, 응답 받은 데이터로 그리드를 갱신
        getData().then(res => {
            testGrid.resetData(res.data);
        });
    }, false);

    // 그리드 행 더블클릭 이벤트: 특정 행을 더블클릭 시 팝업 창을 열어 상세 정보 제공
    testGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = testGrid.getRow(rowKey);

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
                        userId: currentUser.id,
                        authCode: currentUser.authCode
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
        const endDate = document.querySelector("input[name='ednDate']").value;
        const apprType = document.querySelector("select[name='APR']").value;
        const dept = document.querySelector("select[name='DEP']").value;
        const position = document.querySelector("select[name='POS']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

        // 시작 날짜가 종료 날짜보다 늦은 경우 경고 메시지 출력 및 함수 중단
        if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
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
            testGrid.resetData(modifiedData);
            return { data: modifiedData };

        } catch (e) {
            // 에러 발생 시 콘솔에 로그 출력하고 사용자에게 경고 메시지 표시
            console.error("데이터 조회 중 오류 발생:", e);
            alert("데이터를 불러오는 중 문제가 발생했습니다.\n입력 값을 확인하거나 관리자에게 문의하세요.");
        }
    }
}

// 페이지 로딩이 완료되면 init 함수 실행
window.onload = () => {
    init();
}
