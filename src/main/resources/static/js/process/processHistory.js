const init = () => {
    const btn= document.querySelector("button[name='workOrderBtn']")
    console.log(btn);
    if(btn){
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            getWorkOrders();
        });
    }
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
    getWorkOrders();

    const processHistoryGrid = initGrid(
        document.getElementById('processHistoryGrid'),
        400,
        [
            {
                header: '공정이력번호',
                name: 'processHistoryId',
                align: 'center'
            },
            {
                header: '공정명',
                name: 'processName',
                align: 'center'
            },
            {
                header: '제품명',
                name: 'itemName',
                align: 'center'
            },
            {
                header: '투입 수량',
                name: 'inputQuantity',
                align: 'center',
                editor: 'text'
            },
            {
                header: '산출 수량',
                name: 'outputQuantity',
                align: 'center',
                editor: 'text'
            },
            {
                header: '단위',
                name: 'unit',
                align: 'center'
            },
            {
                header: '공정 시작',
                name: 'startTime',
                align: 'center',
                editor: 'text'
            },
            {
                header: '공정 종료',
                name: 'endTime',
                align: 'center',
                editor: 'text'
            },
            {
                header: '공정 상태',
                name: 'processStatusName',
                align: 'center'
            },
            {
                header: '산출 LOT',
                name: 'lotId',
                align: 'center'
            }
        ]
    );

    workOrderGrid.on('click', (e) => {
        getProcessHistory(workOrderGrid.getRow(e.rowKey).id);
    })

    // 생산계획별 작업 목록 조회
    async function getWorkOrders() {
        const params = new URLSearchParams()
        const workOrderStatusList = ['WKS002', 'WKS003', 'WKS004'];
        const workOrderId = document.querySelector("input[name='srhId']").value;
        const itemNameOrId = document.querySelector("input[name='srhItemIdOrName']").value;
        params.append("statusCodes",workOrderStatusList)
        if(workOrderId) params.append("workOrderId",workOrderId)
        if(itemNameOrId) params.append("itemIdOrName",itemNameOrId)

        try {
            const res = await fetch(`/api/workorder?${params.toString()}`, {
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

        } catch (e) {
            console.error(e);
        }
    }

    async function getProcessHistory(workOrderId) {
        try {
            const res = await fetch(`/api/process/history/${workOrderId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            const data = await res.json();
            processHistoryGrid.resetData(data.data);
        } catch (e) {
            console.error(e);
        }
    }
}

window.onload = () => {
    init();
}