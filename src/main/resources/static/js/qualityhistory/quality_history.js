// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}
// 대문자 변환 함수
const toUpperCase = (str) => {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}
// form validation 확인
const validators = {
    isNotEmpty: val => val !== null && val !== undefined && val.trim() !== '',
    isNumeric: val => /^-?\d+(\.\d+)?$/.test(val.trim()),
    isValidDate: date => /^\d{4}-\d{2}-\d{2}$/.test(date)
};

const init = () => {

    const completedWorkOrderGrid = initGrid(
        document.getElementById('completed_work_order_grid'),
        300,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '작업지시 번호',
                name: 'workOrderId',
                align: 'center'
            },
            {
                header: '작업 제품번호',
                name: 'itemId',
                align: 'center'
            },
            {
                header: '작업 제품명',
                name: 'itemName',
                align: 'center'
            },
            {
                header: '생산 계획 번호',
                name: 'productionPlanId',
                align: 'center'
            },
            {
                header: '양품 수량',
                name: 'fectiveQuantity',
                align: 'center',
                formatter: function({ row }) {
                    // 숫자와 단위 사이에 한 칸 띄우기
                    const qty = row.fectiveQuantity ?? '-';
                    const unit = row.unit ? ' ' + row.unit : '';
                    return qty + unit;
                }
            },
            {
                header: '작업 상태',
                name: 'workStatus',
                align: 'center'
            }
        ]
    );

    const qualityInspectionHistoryGrid = initGrid(
        document.getElementById('quality_inspection_history_grid'),
        200,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '품질 검사 이력번호',
                name: 'qualityInspectionHistoryId',
                align: 'center'
            },
            {
                header: '품질 검사명',
                name: 'qualityInspectionName',
                align: 'center'
            },
            {
                header: '제품 번호',
                name: 'itemId',
                align: 'center'
            },
            {
                header: '제품명',
                name: 'itemName',
                align: 'center'
            },
            {
                header: 'LOT',
                name: 'lotId',
                align: 'center'
            },
            {
                header: '검사일',
                name: 'inspectionDate',
                align: 'center'
            },
            {
                header: '결과값 [입력]',
                name: 'resultValue',
                align: 'center',
                // 권한에 따라 에디터 활성화 여부 설정
                editor: window.user.authCode === 'ATH007' ? 'text' : false
            },
            {
                header: '결과값 단위',
                name: 'resultValueUnit',
                align: 'center'
            },
            {
                header: '결과',
                name: 'resultCodeName',
                align: 'center',
                formatter: function({ value }) {
                    if (value === '불합격') {
                        return `<span style="color: red; font-weight: bold;">${value}</span>`;
                    }
                    return value || '-';
                }
            },
            {
                header: '상태',
                name: 'historyStatus',
                align: 'center'
            }
        ]
    );

    // 페이지 로드 시 작업지시 상세 품질검사 결과 저장 버튼 비활성화
    const addInspectionResultBtn = document.querySelector("button[name='addInspectionResult']");

    if(addInspectionResultBtn) {
        addInspectionResultBtn.disabled = true;

        // 결과 저장 버튼 클릭 이벤트
        addInspectionResultBtn.addEventListener("click", function(e) {
            e.preventDefault();
            e.stopPropagation();

            // 모든 행의 결과값 확인
            const rowCount = qualityInspectionHistoryGrid.getRowCount();
            let allValid = true;
            let invalidReason = '';
            let invalidRow = null;

            for (let i = 0; i < rowCount; i++) {
                const rowKey = qualityInspectionHistoryGrid.getRowAt(i).rowKey;
                const value = qualityInspectionHistoryGrid.getValue(rowKey, 'resultValue');

                // 값이 비어있는지 확인
                if (!validators.isNotEmpty(value)) {
                    allValid = false;
                    invalidReason = '모든 품질검사 결과값을 입력하세요!!';
                    break;
                }

                // 숫자 형식인지 확인
                if (!validators.isNumeric(value)) {
                    allValid = false;
                    invalidRow = i + 1;
                    invalidReason = `${invalidRow}번째 행의 결과값이 올바른 숫자 형식이 아닙니다.\n정수 또는 실수만 입력 가능합니다.`;
                    break;
                }
            }

            // 유효성 검사 실패 시 경고창 표시
            if (!allValid) {
                alert(invalidReason);
                return;
            }

            // 모든 결과값이 유효한 경우 저장 로직 실행
            saveQualityInspectionResults();
        }, false);
    }

    // 전역 변수로 현재 선택된 라인 정보 저장
    let currentSelectedLine = null;

    // 검색 버튼 클릭 이벤트
    const searchBtn = document.querySelector(".srhBtn");
    if (searchBtn) {
        searchBtn.addEventListener("click", function(e) {
            e.preventDefault();
            e.stopPropagation();
            getWorkOrders();
        }, false);
    }

    // 엔터 시 검색
    const searchForm = document.querySelector(".search__form");
    if (searchForm) {
        searchForm.addEventListener("submit", function(e) {
            e.preventDefault();
            getWorkOrders();
        });
    }

    // 작업지시 선택 시 해당 작업지시의 품질검사 이력 목록 표시
    completedWorkOrderGrid.on('click', (e) => {
        const rowKey = e.rowKey;
        const rowData = completedWorkOrderGrid.getRow(rowKey);

        if (rowData && rowData.workOrderId) {
            // 선택된 작업지시 정보 저장 (workStatus 추가)
            currentSelectedLine = {
                workOrderId: rowData.workOrderId,
                itemId: rowData.itemId,
                itemName: rowData.itemName,
                fectiveQuantity: rowData.fectiveQuantity,
                workStatus: rowData.workStatus
            };

            // 품질검사 이력 목록 조회 (작업 상태 전달)
            getQualityInspectionHistories(rowData.workOrderId, rowData.workStatus);

            // 품질검사 결과 저장 버튼 활성화/비활성화 (작업 상태가 '완료'인 경우 비활성화)
            if (addInspectionResultBtn) {
                if (rowData.workStatus === '완료') {
                    // 작업 상태가 완료인 경우 버튼 비활성화
                    addInspectionResultBtn.disabled = true;
                } else {
                    // 그 외의 경우 버튼 활성화
                    addInspectionResultBtn.disabled = false;
                }
            }
        }
    });

    // 작업지시 목록 조회
    async function getWorkOrders() {
        // 안전하게 요소 가져오기
        const itemIdOrNameInput = document.querySelector("input[name='srhItemIdOrName']");
        const prdctPlanIdInput = document.querySelector("input[name='srhProductionPlanId']");
        const workOrderIdInput = document.querySelector("input[name='srhWorkOrderId']");
        const statusCodeSelect = document.querySelector("select[name='workOrderStatusCode']");

        // 검색어 추출
        const itemIdOrName = itemIdOrNameInput ? removeSpaces(itemIdOrNameInput.value) : '';
        const prdctPlanId = prdctPlanIdInput ? removeSpaces(prdctPlanIdInput.value) : '';
        const workOrderId = workOrderIdInput ? removeSpaces(workOrderIdInput.value) : '';

        // 작업지시 상태 코드 추출 - 값이 없으면 WKS003,WKS004 사용 (기본값 유지)
        const statusCodeValue = statusCodeSelect ? statusCodeSelect.value : '';
        const statusCodes = statusCodeValue || "WKS003,WKS004";

        // 콤마로 구분된 문자열을 배열로 변환
        const statusCodesArray = statusCodes.split(',');

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(itemIdOrName) params.append("itemIdOrName", toUpperCase(itemIdOrName));
        if(prdctPlanId) params.append("productionPlanId", toUpperCase(prdctPlanId));
        if(workOrderId) params.append("workOrderId", toUpperCase(workOrderId));

        // 각 상태 코드를 개별적으로 추가 (서버에서 List로 인식)
        statusCodesArray.forEach(code => {
            if(code.trim()) {  // 빈 문자열이 아닌 경우만 추가
                params.append("statusCodes", code.trim());
            }
        });

        // 작업지시 목록 조회
        fetch(`/api/workorder?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    // 데이터 매핑 처리
                    const mappedData = res.data.map(rowData => {
                        console.log("서버에서 받은 원본 데이터:", rowData);
                        return {
                            id: rowData.id,                       // 번호 (hidden)
                            workOrderId: rowData.id,              // 작업지시 번호
                            itemId: rowData.itemId,               // 작업 제품번호
                            itemName: rowData.itemName,           // 작업 제품명
                            productionPlanId: rowData.productionId, // 생산 계획 번호
                            fectiveQuantity: rowData.fectiveQuantity, // 유효 수량
                            unit: rowData.unit,               // 단위 (추가된 필드)
                            workStatus: rowData.status            // 작업 상태
                        };
                    });

                    return completedWorkOrderGrid.resetData(mappedData);
                } else {
                    alert(res.message);
                }
                return completedWorkOrderGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    // 품질검사 이력 목록 조회 함수
    async function getQualityInspectionHistories(workOrderId, workStatus) {
        try {
            // API 호출
            const response = await fetch(`/api/quality/history?workOrderId=${workOrderId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            const result = await response.json();

            // 품질검사 이력 데이터가 없으면 버튼 비활성화
            if (!result.data || result.data.length === 0) {
                qualityInspectionHistoryGrid.resetData([]);
                if (addInspectionResultBtn) addInspectionResultBtn.disabled = true;
                return;
            }

            if (result.status === 200) {
                // 데이터 매핑 처리
                const mappedData = result.data.map(rowData => {
                    // 조건 수정: ATH007 권한이 없거나 작업상태가 '완료'면 결과값은 '-'
                    const resultValue = (window.user.authCode !== 'ATH007' || workStatus === '완료')
                        ? rowData.resultValue || '-'
                        : rowData.resultValue || '';

                    return {
                        id: rowData.qualityHistoryId, // 품질 검사 이력번호
                        qualityInspectionHistoryId: rowData.qualityHistoryId, // 품질 검사 이력번호
                        qualityInspectionId: rowData.qualityInspectionId, // 품질 검사 ID
                        qualityInspectionName: rowData.qualityInspectionName, // 품질 검사명
                        itemId: rowData.itemId, // 제품 번호
                        itemName: rowData.itemName, // 제품명
                        lotId: rowData.lotId || '-', // LOT
                        inspectionDate: rowData.inspectionDate || '-', // 검사일
                        resultValue: resultValue, // 결과값
                        resultValueUnit: rowData.resultValueUnit || '-', // 결과값 단위
                        resultCodeName: rowData.resultCodeName || '-', // 결과
                        historyStatus: rowData.statusCodeName || '-', // 상태
                    };
                });

                // 그리드 데이터 설정
                qualityInspectionHistoryGrid.resetData(mappedData);

                // 기존 컬럼 설정 복사
                const columns = qualityInspectionHistoryGrid.getColumns();

                // 결과값 컬럼 찾기
                const resultValueColumnIndex = columns.findIndex(col => col.name === 'resultValue');

                if (resultValueColumnIndex >= 0) {
                    // 결과값 컬럼 속성 수정
                    const isEditable = workStatus !== '완료' && window.user.authCode === 'ATH007';
                    columns[resultValueColumnIndex].editor = isEditable ? 'text' : false;

                    // 수정된 컬럼 설정으로 업데이트
                    qualityInspectionHistoryGrid.setColumns(columns);
                }
            } else {
                alert(result.message);
                qualityInspectionHistoryGrid.resetData([]);
                if (addInspectionResultBtn) addInspectionResultBtn.disabled = true;
            }
        } catch (error) {
            console.error('품질검사 이력 조회 오류:', error);
            alert('품질검사 이력 조회 중 오류가 발생했습니다.');
            qualityInspectionHistoryGrid.resetData([]);
            if (addInspectionResultBtn) addInspectionResultBtn.disabled = true;
        }
    }

    // 품질 검사 결과 저장 함수
    async function saveQualityInspectionResults() {
        try {
            // 그리드의 모든 행 데이터 가져오기
            const gridData = qualityInspectionHistoryGrid.getData();

            // 요청 형식에 맞게 데이터 변환
            const qualityHistoryList = gridData.map(row => {
                return {
                    qualityHistoryId: row.id,
                    qualityInspectionId: row.qualityInspectionId,
                    resultValue: parseFloat(row.resultValue)
                };
            });

            // API 요청 형식에 맞는 데이터 구성
            const requestData = {
                itemId: currentSelectedLine.itemId,
                workOrderId: currentSelectedLine.workOrderId, // 작업지시 ID 추가
                fectiveQuantity: currentSelectedLine.fectiveQuantity, // 유효 수량 추가
                qualityHistoryList: qualityHistoryList
            };

            console.log('품질 검사 결과 저장 요청 데이터:', requestData);

            // API 호출
            const response = await fetch('/api/quality/history', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            const result = await response.json();

            if (result.status === 200) {
                alert(result.message || '품질 검사 결과가 저장되었습니다.');

                // 품질 검사 결과 저장 후 저장 버튼 비활성화
                if (addInspectionResultBtn) addInspectionResultBtn.disabled = true;

                // 현재 선택된 작업지시가 있으면 품질검사 이력 목록 새로고침
                if (currentSelectedLine && currentSelectedLine.workOrderId) {
                    getQualityInspectionHistories(currentSelectedLine.workOrderId, currentSelectedLine.workStatus);
                }

                // 현재 선택된 작업지시가 있으면 품질검사 이력 목록 새로고침
                if (currentSelectedLine && currentSelectedLine.workOrderId) {
                    getQualityInspectionHistories(currentSelectedLine.workOrderId, currentSelectedLine.workStatus);
                }
            } else {
                alert(result.message || '품질 검사 결과 저장에 실패했습니다.');
            }
        } catch (error) {
            console.error('품질 검사 결과 저장 오류:', error);
            alert('품질 검사 결과 저장 중 오류가 발생했습니다.');
        }
    }

    // 공통코드 세팅
    setSelectBox("WKS", "workOrderStatusCode", {
        filter: (code) => code.detail_code !== "WKS001" && code.detail_code !== "WKS002"
    });

    // 페이지 진입 시 바로 리스트 호출
    getWorkOrders();
}

window.onload = () => {
    init();
}