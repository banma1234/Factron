// 날짜 비교 함수
// return 1 when date1 is the latter than date2
const compareDate = (date1, date2) => {
    const dateObj1 = new Date(date1);
    const dateObj2 = new Date(date2);

    const date1Year = dateObj1.getFullYear();
    const date1Month = dateObj1.getMonth() + 1;
    const date1Day = dateObj1.getDate();
    const date1Time = dateObj1.getTime();

    const date2Year = dateObj2.getFullYear();
    const date2Month = dateObj2.getMonth() + 1;
    const date2Day = dateObj2.getDate();
    const date2Time = dateObj2.getTime();

    if(date1Year > date2Year) return 1;
    if(date1Year < date2Year) return -1;
    if(date1Month > date2Month) return 1;
    if(date1Month < date2Month) return -1;
    if(date1Day > date2Day) return 1;
    if(date1Day < date2Day) return -1;
    if(date1Time > date2Time) return 1;
    if(date1Time < date2Time) return -1;
    return 0;
}

// 그리드 데이터 날짜별로 정렬
const sortByDate = (data) => {
    return data.sort((a, b) => {
        return compareDate(a.startTime, b.startTime);
    });
}

// 그리드 입력 데이테 확인
const nullCheck = (data) => {
    return data.some(item => item === null || item === undefined);
}

// 공정간 작업시간 비교
const checkTimeLine = (data) => {
    return !data.some((row, i) => {
        const currStartTime = data[i].startTime;
        const currEndTime = data[i].endTime;
        const workOrderDate = new Date(row.workOrderStartDate)
        workOrderDate.setHours(0,0,0);
        // 현재 공정의 시작 시간과 작업지시 시작날짜 비교
        if (compareDate(currStartTime, workOrderDate) < 0){
            return true;
        }

        // 현재 공정의 종료시간이 시작시간보다 늦지 않으면 true (에러)
        if (compareDate(currEndTime, currStartTime) !== 1) {
            return true;
        }

        // 이전 공정이 있고, 현재 공정의 시작시간이 이전 공정의 종료시간보다 늦지 않으면 true (에러)
        if (i > 0) {
            const prevEndTime = data[i - 1].endTime;
            if (compareDate(currStartTime, prevEndTime) !== 1) {
                return true;
            }
        }
        
        return false;
    });
}

// 이전 공정과 산출물 비교
const checkQty = (data) => {
    return !data.some((item, i) => {
        if (i > 0) {
            const currQty = data[i].outputQuantity;
            const prevQty = data[i - 1].outputQuantity;

            if (isNaN(currQty)) {
                return true; // 에러 상황
            }

            if (isNaN(prevQty)) {
                return true; // 에러 상황
            }

            if (Number(currQty) > Number(prevQty)) {
                return true; // 에러 상황
            }
        }
        return false;
    });
}

// 생산 계획 수량과 산출물 비교
const checkPlanQty = (data) => {
    return !data.some(item => {
        const planQty = item.quantity;
        const outQty = item.outputQuantity;
        
        if (isNaN(planQty)) {
            alert("수량에는 숫자만 입력 가능합니다.");
            return true; // 에러 상황
        }

        if (isNaN(outQty)) {
            alert("수량에는 숫자만 입력 가능합니다.");
            return true; // 에러 상황
        }

        if (Number(planQty) < Number(outQty)) {
            return true; // 에러 상황
        }
        
        return false;
    });
}

const init = () => {
    const btn= document.querySelector("button[name='workOrderBtn']")

    if(btn){
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            getWorkOrders();
        });
    }

    const updateBtn = document.querySelector("button[name='updProcHist']")

    if(updateBtn){
        updateBtn.addEventListener("click", function (e) {
            const isNotNull = !nullCheck(processHistoryGrid.getColumnValues("outputQuantity") && processHistoryGrid.getColumnValues("startTime")) && !nullCheck(processHistoryGrid.getColumnValues("endTime"))
            const rawData = processHistoryGrid.getData();
            const data = rawData.filter((row) => row.processStatusCode === 'STS001')

            if(data.length === 0){
                return;
            }

            if(!isNotNull){
                alert("산출 수량, 공정 시작, 공정 종료는 반드시 입력해야합니다!")
                return;
            }

            sortByDate(data)

            if(!checkTimeLine(data)){
                alert("공정 시간이 맞지 않습니다.");
                return;
            }

            if(!checkQty(data)){
                alert("산출 수량이 맞지 않습니다.");
                return;
            }

            if(!checkPlanQty(data)){
                alert("산출 수량이 작업수량보다 많습니다.")
                return;
            }

            updateProcessHistory(data);
            e.preventDefault();
            e.stopPropagation();
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
                header: '산출 수량',
                name: 'outputQuantity',
                align: 'center',
                editor: 'text'
            },
            {
                header: '단위',
                name: 'unitName',
                align: 'center'
            },
            {
                header: '공정 시작',
                name: 'startTime',
                align: 'center',
                editor: {
                    type: 'datePicker',
                    options: {
                        format: 'yyyy-MM-dd HH:mm A',
                        timepicker: true
                    }
                }

            },
            {
                header: '공정 종료',
                name: 'endTime',
                align: 'center',
                editor: {
                    type: 'datePicker',
                    options: {
                        format: 'yyyy-MM-dd HH:mm A',
                        timepicker: true
                    }
                }

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
        const rowKey = e.rowKey;
        const rowData = workOrderGrid.getRow(rowKey);

        if(rowData && rowData.id) getProcessHistory(rowData.id);
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

    async function updateProcessHistory(data) {
        const workOrderId = data[0].workOrderId;
        const requestData = {
            workOrderId: workOrderId,
            processList: data.map(row => ({
                processHistId:row.processHistoryId,
                outputQty: row.outputQuantity,
                startTime: new Date(row.startTime),
                endTime: new Date(row.endTime),
                processTypeCode: row.processTypeCode,
            }))
        }
        try {
            const res = await fetch(`/api/process/history`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestData)
            });

            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            const data = await res.json();
            getProcessHistory(workOrderId);
        } catch (e) {
            console.error(e);
        }
    }
}

window.onload = () => {
    init();
}