// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}
// 대문자 변환 함수
const toUpperCase = (str) => {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}
// 하이픈 제거 함수
const removeHyphens = (phoneNumber) => {
    return phoneNumber.replace(/-/g, '');
}


const init = () => {
    const processGrid = initGrid(
        document.getElementById('process_grid'),
        400,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '공정번호',
                name: 'processId',
                align: 'center'
            },
            {
                header: '공정명',
                name: 'processName',
                align: 'center'
            },
            {
                header: '공정유형',
                name: 'processTypeName',
                align: 'center'
            },
            {
                header: '소속 라인',
                name: 'lineName',
                align: 'center'
            },
            {
                header: '공정 시간',
                name: 'standardTime',
                align: 'center',
                formatter: (value) => {
                    return value.value ? `${value.value}분` : '';
                }
            },
            {
                header: '설비 유무',
                name: 'hasMachine',
                align: 'center',
                formatter: (value) => {
                    const upperValue = toUpperCase(value.value)
                    return `${upperValue==='Y' ? '보유' : '미보유'}`;
                }
            },
            {
                header: '등록자 사번',
                name: 'createdBy',
                align: 'center'
            },
            {
                header: '등록일자',
                name: 'createdAt',
                align: 'center'
            }
        ]
    );

    // 검색 버튼 클릭 이벤트
    document.querySelector(".processSrhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getProcesses();

    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function(e) {
        e.preventDefault();

        getProcesses();
    });

    // 공정 추가 버튼 클릭
    const addBtn = document.querySelector("button[name='addNewProcess']");
    if(addBtn){
        addBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            addNewProcess();
        })
    }

    // 공정 목록 조회
    async function getProcesses() {

        const selectProcessType = document.querySelector("select[name='processTypeCode']");
        const selecthasMachine = document.querySelector("select[name='hasMachine']");
        // 공정 정보 추출
        const processType = removeSpaces(selectProcessType.options[selectProcessType.selectedIndex].value);
        const hasMachine = removeSpaces(selecthasMachine.options[selecthasMachine.selectedIndex].value);
        // 검색어 추출
        const processIdOrName = removeSpaces(document.querySelector("input[name='processIdOrName']").value);

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(processType) params.append("processTypeCode", processType)
        if(processIdOrName) params.append("processIdOrName", processIdOrName)
        if(hasMachine) params.append("hasMachine", hasMachine)

        // 공정 목록 조회
        fetch(`/api/process?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){

                    console.log(res.data);

                    return processGrid.resetData(res.data);
                }else{
                    alert(res.message);
                }
                return processGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    const addNewProcess = () => {
        const popup = window.open('/process-newForm', '_blank', 'width=800,height=600');

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

            if (message && message.type === "ADD_REFRESH_PROCESSES") {
                getProcesses() // 안전하게 리프레시 실행
            }
        });
    }

    processGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = processGrid.getRow(rowKey);
        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && (rowKey || rowKey === 0)) {
            const popup = window.open('/process-form', '_blank', 'width=800,height=600');

            if (!popup) {
                alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
                return;
            }

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        processId: rowData.processId,
                        processName: rowData.processName,
                        description: rowData.description,
                        processTypeCode: rowData.processTypeCode,
                        processTypeName: rowData.processTypeName,
                        lineId: rowData.lineId,
                        lineName: rowData.lineName,
                        standardTime: rowData.standardTime,
                        hasMachine: rowData.hasMachine,
                        createdAt: rowData.createdAt,
                        createdBy: rowData.createdBy
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
            //팝업 종료시 공정 리스트 새로 호출
            window.addEventListener("message", (event) => {

                const message = event.data;

                if (message && message.type === "REFRESH_PROCESSES") {
                    getProcesses() // 안전하게 리프레시 실행
                }
            });
        }
    });

    // 공통코드 세팅
    setSelectBox("PTP", 'processTypeCode');

    // 페이지 진입 시 바로 리스트 호출
    getProcesses();
}

window.onload = () => {
    init();
}