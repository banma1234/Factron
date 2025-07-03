// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}

const init = () => {
    const machineGrid = initGrid(
        document.getElementById('machine_grid'),
        400,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '설비번호',
                name: 'machineId',
                align: 'center'
            },
            {
                header: '설비명',
                name: 'machineName',
                align: 'center'
            },
            {
                header: '제조사',
                name: 'manufacturer',
                align: 'center'
            },
            {
                header: '소속 공정',
                name: 'processName',
                align: 'center'
            },
            {
                header: '구입일자',
                name: 'buyDate',
                align: 'center'
            }
        ]
    );

    // 검색 버튼 클릭 이벤트
    document.querySelector(".MachineOrManufacSrhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getMachines();

    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function(e) {
        e.preventDefault();

        getMachines();
    });

    // 공정 추가 버튼 클릭
    const addBtn = document.querySelector("button[name='addNewMachine']");
    if(addBtn){
        addBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            addNewMachine();
        })
    }

    // 설비 목록 조회
    async function getMachines() {

        // 검색어 추출
        const machineNameOrManufacturer = removeSpaces(document.querySelector("input[name='machineNameOrManufacturer']").value);

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(machineNameOrManufacturer) params.append("machineNameOrManufacturer", machineNameOrManufacturer)

        // 설비 목록 조회
        fetch(`/api/machine?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){
                    return machineGrid.resetData(res.data);
                }else{
                    alert(res.message);
                }
                return machineGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    const addNewMachine = () => {
        const popup = window.open('/machine-newForm', '_blank', 'width=800,height=750');

        if (!popup) {
            alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
            return;
        }

        // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        const messageHandler = (event) => {
            if (event.data === 'addReady') {
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
        //팝업 종료시 공정 리스트 새로 호출
        window.addEventListener("message", (event) => {

            const message = event.data;

            if (message && message.type === "ADD_REFRESH_MACHINES") {
                getMachines() // 안전하게 리프레시 실행
            }
        });
    }

    machineGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = machineGrid.getRow(rowKey);
        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && (rowKey || rowKey === 0)) {
            const popup = window.open('/machine-form', '_blank', 'width=800,height=450');

            if (!popup) {
                alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
                return;
            }

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        machineId: rowData.machineId,
                        machineName: rowData.machineName,
                        manufacturer: rowData.manufacturer,
                        processId: rowData.processId,
                        processName: rowData.processName,
                        buyDate: rowData.buyDate
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
            //팝업 종료시 공정 리스트 새로 호출
            window.addEventListener("message", (event) => {

                const message = event.data;

                if (message && message.type === "REFRESH_MACHINES") {
                    getMachines(); // 안전하게 리프레시 실행
                }
            });
        }
    });

    // 페이지 진입 시 바로 리스트 호출
    getMachines();
}

window.onload = () => {
    init();
}