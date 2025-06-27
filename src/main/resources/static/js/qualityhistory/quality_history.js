// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}
// 대문자 변환 함수
const toUpperCase = (str) => {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}

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
                editor: 'text'
            },
            {
                header: '결과',
                name: 'resultCodeName',
                align: 'center'
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
    }

    // 전역 변수로 현재 선택된 라인 정보 저장
    let currentSelectedLine = null;

    // 검색 버튼 클릭 이벤트
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getWorkOrders();

    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function(e) {
        e.preventDefault();

        getWorkOrders();
    });

    // 작업지시 선택 시 해당 작업지시의 품질검사 이력 목록 표시
    completedWorkOrderGrid.on('click', (e) => {
        const rowKey = e.rowKey;
        const rowData = completedWorkOrderGrid.getRow(rowKey);

        if (rowData && rowData.workOrderId) {
            // 품질검사 이력 목록 조회
            getQualityInspectionHistories(rowData.workOrderId);

            // 품질검사 결과 저장 버튼 항상 비활성화 상태 유지
            if (addInspectionResultBtn) {
                addInspectionResultBtn.disabled = true;
            }

            // 선택된 작업지시 정보 저장
            currentSelectedLine = {
                workOrderId: rowData.workOrderId,
                itemId: rowData.itemId,
                itemName: rowData.itemName
            };
        }
    });

    // 작업지시 목록 조회
    async function getWorkOrders() {

        // 검색어 추출
        const itemIdOrName = removeSpaces(document.querySelector("input[name='srhItemIdOrName']").value);
        const prdctPlanId = removeSpaces(document.querySelector("input[name='srhProductionPlanId']").value);
        const workOrderId = removeSpaces(document.querySelector("input[name='srhWorkOrderId']").value);

        // 작업지시 상태 코드 추출
        const statusCodes = document.querySelector("select[name='workOrderStatusCode']").value || "WKS003,WKS004";

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
            params.append("statusCodes", code.trim());
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
                        return {
                            id: rowData.id,                       // 번호 (hidden)
                            workOrderId: rowData.id,              // 작업지시 번호
                            itemId: rowData.itemId,               // 작업 제품번호
                            itemName: rowData.itemName,           // 작업 제품명
                            productionPlanId: rowData.productionId, // 생산 계획 번호
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
    async function getQualityInspectionHistories(workOrderId) {
        try {
            // API 호출
            const response = await fetch(`/api/quality/history?workOrderId=${workOrderId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            const result = await response.json();

            if (result.status === 200) {
                // 데이터 매핑 처리
                const mappedData = result.data.map(rowData => {
                    return {
                        id: rowData.qualityHistoryId,
                        qualityInspectionHistoryId: rowData.qualityHistoryId,
                        qualityInspectionName: rowData.qualityInspectionName,
                        itemId: rowData.itemId,
                        itemName: rowData.itemName,
                        lotId: rowData.lotId || '-',
                        inspectionDate: rowData.inspectionDate || '-',
                        resultValue: rowData.resultValue || '',
                        resultCodeName: rowData.resultCodeName || '-',
                        historyStatus: rowData.statusCodeName
                    };
                });

                // 그리드 데이터 설정
                qualityInspectionHistoryGrid.resetData(mappedData);

            } else {
                alert(result.message);
                qualityInspectionHistoryGrid.resetData([]);
            }
        } catch (error) {
            console.error('품질검사 이력 조회 오류:', error);
            alert('품질검사 이력 조회 중 오류가 발생했습니다.');
            qualityInspectionHistoryGrid.resetData([]);
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