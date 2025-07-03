const init = () => {
    const addWorkOrderBtn = document.querySelector(".registWorkOrder");

    // 생산계획 grid
    const planningGrid = initGrid(
        document.getElementById('planningGrid'),
        200,
        [
            {
                header: '생산계획 번호',
                name: 'id',
                align: 'center'
            },
            {
                header: '제품코드',
                name: 'itemId',
                align: 'center'
            },
            {
                header: '제품명',
                name: 'itemName',
                align: 'center'
            },
            {
                header: '생산수량',
                name: 'quantity',
                align: 'center',
                formatter: ({ value, row }) => {
                    return value ? `${formatNumber(value)} ${row.unit ?? ''}` : '';
                }
            },
            {
                header: '시작일',
                name: 'startDate',
                align: 'center'
            },
            {
                header: '종료일',
                name: 'endDate',
                align: 'center'
            },
        ]
    );

    // 작업지시 grid
    const workOrderGrid = initGrid(
        document.getElementById('workOrderGrid'),
        300,
        [
            {
                header: '작업지시 번호',
                name: 'id',
                align: 'center'
            },
            {
                header: '제품코드',
                name: 'itemId',
                align: 'center'
            },
            {
                header: '제품명',
                name: 'itemName',
                align: 'center'
            },
            {
                header: '작업수량',
                name: 'quantity',
                align: 'center',
                formatter: ({ value, row }) => {
                    return value ? `${formatNumber(value)} ${row.unit ?? ''}` : '';
                }
            },
            {
                header: '라인',
                name: 'lineName',
                align: 'center'
            },
            {
                header: '작업 시작일',
                name: 'startDate',
                align: 'center'
            },
            {
                header: '담당자 사번',
                name: 'empId',
                align: 'center'
            },
            {
                header: '담당자명',
                name: 'empName',
                align: 'center'
            },
            {
                header: '작업상태',
                name: 'status',
                align: 'center'
            },
        ]
    );

    // 검색 버튼 클릭 이벤트
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getPrdctPlans();

    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function(e) {
        e.preventDefault();

        getPrdctPlans();
    });

    // 생산계획 행 클릭 시 작업 목록 표시
    planningGrid.on('click', (e) => {
        const rowKey = e.rowKey;
        const rowData = planningGrid.getRow(rowKey);

        if (rowData && rowData.id) {
            getWorkOrders(rowData.id);
        }
    });

    // 작업 추가
    addWorkOrderBtn.addEventListener("click", function(e) {
        e.preventDefault();

        const rowKey = planningGrid.getFocusedCell()?.rowKey;
        const selectedPlan = rowKey !== null ? planningGrid.getRow(rowKey) : null;

        if (selectedPlan) {
            const popup = window.open('/workorder/save', '_blank', 'width=800,height=850');

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        planId: selectedPlan.id,
                        itemId: selectedPlan.itemId,
                        quantity: selectedPlan.quantity
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);

            //팝업 종료시 작업 리스트 새로 호출
            window.addEventListener("message", (event) => {

                const message = event.data;

                if (message && message.type === "ADD_REFRESH_WORKORDERS") {
                    getWorkOrders(selectedPlan.id) // 안전하게 리프레시 실행
                }
            });
        }

    }, false);

    // 작업지시 행 클릭 시 상세정보 open
    workOrderGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = workOrderGrid.getRow(rowKey);

        if (rowData && rowData.id) {
            const popup = window.open('/workorder-form', '_blank', 'width=800,height=850');

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        planId: rowData.productionId,
                        orderId: rowData.id,
                        itemId: rowData.itemId,
                        itemName: rowData.itemName,
                        quantity: rowData.quantity,
                        unit: rowData.unit,
                        lineId: rowData.lineId,
                        lineName: rowData.lineName,
                        startDate: rowData.startDate,
                        status: rowData.status,
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);

            //팝업 종료시 작업 리스트 새로 호출
            window.addEventListener("message", (event) => {

                const message = event.data;

                if (message && message.type === "STR_REFRESH_WORKORDERS") {
                    const rowKey = planningGrid.getFocusedCell()?.rowKey;
                    const selectedPlan = rowKey !== null ? planningGrid.getRow(rowKey) : null;

                    if (selectedPlan) {
                        getWorkOrders(selectedPlan.id); // 안전하게 리프레시 실행
                    }
                }
            });
        }
    });

    // 생산계획 목록 조회
    async function getPrdctPlans() {

        // fetch data
        const params = new URLSearchParams({
            srhId: document.querySelector("input[name='srhId']").value,
            srhItemIdOrName: document.querySelector("input[name='srhItemIdOrName']").value,
        });

        try {
            const res = await fetch(`/api/production?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            // 응답이 정상적이지 않은 경우 오류 발생
            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            const data = await res.json();
            planningGrid.resetData(data.data); // grid에 세팅
            workOrderGrid.resetData([]); // 작업지시 grid 초기화
            addWorkOrderBtn.disabled = true; // 작업 추가 버튼 비활성화

        } catch (e) {
            console.error(e);
        }
    }

    // 생산계획별 작업 목록 조회
    async function getWorkOrders(planId) {

        try {
            const res = await fetch(`/api/workorder?prdctPlanId=${planId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            // 응답이 정상적이지 않은 경우 오류 발생
            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            const data = await res.json();
            workOrderGrid.resetData(data.data); // grid에 세팅
            addWorkOrderBtn.disabled = false; // 작업 추가 버튼 활성화

        } catch (e) {
            console.error(e);
        }
    }

    // 페이지 진입 시 바로 리스트 호출
    getPrdctPlans();
}

window.onload = () => {
    init();
}