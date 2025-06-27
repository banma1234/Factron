const init = () => {
    const btn= document.querySelector("input[name='workOrderBtn']")
    if(btn){
        btn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            getWorkOrders();
        });
    }
    const workOrderGrid = initGrid(
        document.getElementById('workOrderGrid'),
        400,
        [
            {
                header: '품질검사번호',
                name: 'inspectionId',
                align: 'center'
            },
            {
                header: '품질검사명',
                name: 'inspectionName',
                align: 'center',
                editor: 'text'
            },
            {
                header: '검사타입',
                name: 'inspectionType',
                align: 'center',
                editor: 'text'
            },
            {
                header: '검사방법',
                name: 'inspectionMethod',
                align: 'center',
                editor: 'text'
            }
        ]
    );

    const processHistoryGrid = initGrid(
        document.getElementById('processHistoryGrid'),
        400,
        [
            {
                header: '품질검사번호',
                name: 'inspectionId',
                align: 'center'
            },
            {
                header: '품질검사명',
                name: 'inspectionName',
                align: 'center',
                editor: 'text'
            },
            {
                header: '검사타입',
                name: 'inspectionType',
                align: 'center',
                editor: 'text'
            },
            {
                header: '검사방법',
                name: 'inspectionMethod',
                align: 'center',
                editor: 'text'
            }
        ]
    );

    // 생산계획별 작업 목록 조회
    async function getWorkOrders() {
        const params = new URLSearchParams()
        const workOrderStatusList = list();
        workOrderStatusList.append('WKS002');
        params.append(test,workOrderStatusList)
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
}

window.onload = () => {
    init();
}