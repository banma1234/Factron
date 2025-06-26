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
                header: '결과값',
                name: 'resultValue',
                align: 'center'
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
                    const mappedData = res.data.map(item => {
                        return {
                            id: item.id,                       // 번호 (hidden)
                            workOrderId: item.id,              // 작업지시 번호
                            itemId: item.itemId,               // 작업 제품번호
                            itemName: item.itemName,           // 작업 제품명
                            productionPlanId: item.productionId, // 생산 계획 번호
                            workStatus: item.status            // 작업 상태
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