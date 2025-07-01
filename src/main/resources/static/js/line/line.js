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

    const lineGrid = initGrid(
        document.getElementById('line_grid'),
        300,
        [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
            {
                header: '라인 번호',
                name: 'lineId',
                align: 'center'
            },
            {
                header: '라인명',
                name: 'lineName',
                align: 'center'
            },
            {
                header: '라인 가동 상태',
                name: 'lineStatusName',
                align: 'center'
            },
            {
                header: '등록 일자',
                name: 'createdAt',
                align: 'center'
            },
            {
                header: '등록자 사번',
                name: 'createdBy',
                align: 'center'
            }
        ]
    );

    const lineProcessGrid = initGrid(
        document.getElementById('line_process_grid'),
        200,
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
            }
        ],
        ['checkbox'] // 체크박스 추가
    );

    // 페이지 로드 시 공정 수정 버튼과 삭제 버튼 비활성화
    const addLineProcessBtn = document.querySelector("button[name='addLineProcess']");
    const deleteLineProcessBtn = document.querySelector("button[name='deleteLineProcess']");

    if(addLineProcessBtn) {
        addLineProcessBtn.disabled = true;
    }
    if(deleteLineProcessBtn) {
        deleteLineProcessBtn.disabled = true;
    }

    // 전역 변수로 현재 선택된 라인 정보 저장
    let currentSelectedLine = null;

    // 검색 버튼 클릭 이벤트
    document.querySelector(".lineSrhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getLines();

    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function(e) {
        e.preventDefault();

        getLines();
    });

    //  라인 추가 버튼 클릭
    const addLineBtn = document.querySelector("button[name='addNewLine']");
    if(addLineBtn){
        addLineBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            addNewLine();
        })
    }

    // 공정 추가 버튼 클릭 이벤트
    if(addLineProcessBtn){
        addLineProcessBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            // 라인이 선택되어 있을 때만 처리
            if (currentSelectedLine) {
                // 기존 로직 호출 또는 새로운 로직 구현
                addNewLineProcess(currentSelectedLine);
            }
        })
    }

    // 선택 공정 삭제 버튼 클릭 이벤트
    if(deleteLineProcessBtn) {
        deleteLineProcessBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            // 선택된 행 데이터 가져오기
            const checkedRows = lineProcessGrid.getCheckedRows();

            if(checkedRows.length === 0) {
                alert("선택된 공정이 없습니다.");
                return;
            }

            if(confirm(`선택한 ${checkedRows.length}개의 공정을 라인에서 삭제하시겠습니까?`)) {
                // 삭제 로직 구현
                deleteSelectedProcesses(checkedRows);
            }
        });
    }

    // 라인 선택 시 해당 라인에 소속된 공정 목록 표시
    lineGrid.on('click', (e) => {
        const rowKey = e.rowKey;
        const rowData = lineGrid.getRow(rowKey);

        if (rowData && rowData.lineId) {
            getLineProcesses(rowData.lineId, rowData.lineName);

            if (rowData.lineStatusName === '가동') {
                if (addLineProcessBtn) addLineProcessBtn.disabled = true;
                if (deleteLineProcessBtn) deleteLineProcessBtn.disabled = true;
            } else {
                if (addLineProcessBtn) addLineProcessBtn.disabled = false;
                if (deleteLineProcessBtn) deleteLineProcessBtn.disabled = true;
            }

            currentSelectedLine = {
                lineId: rowData.lineId,
                lineName: rowData.lineName,
                description: rowData.description || '',
                lineStatusName: rowData.lineStatusName
            };
        }
    });

    // 체크박스 선택 상태 변경 이벤트 감지
    lineProcessGrid.on('check', () => {
        updateDeleteButtonState();
    });

    lineProcessGrid.on('uncheck', () => {
        updateDeleteButtonState();
    });

    // 체크박스 전체 선택/해제 이벤트 감지
    lineProcessGrid.on('checkAll', () => {
        updateDeleteButtonState();
    });

    lineProcessGrid.on('uncheckAll', () => {
        updateDeleteButtonState();
    });

    // 삭제 버튼 상태 업데이트 함수
    function updateDeleteButtonState() {
        if (deleteLineProcessBtn) {
            // '가동' 상태면 무조건 비활성화
            if (currentSelectedLine && currentSelectedLine.lineStatusName === '가동') {
                deleteLineProcessBtn.disabled = true;
                return;
            }
            // 체크된 행이 하나라도 있으면 삭제 버튼 활성화
            const checkedRows = lineProcessGrid.getCheckedRows();
            deleteLineProcessBtn.disabled = checkedRows.length === 0;
        }
    }

    // 라인 목록 조회
    async function getLines() {

        // 검색어 추출
        const lineName = removeSpaces(document.querySelector("input[name='lineName']").value);

        // 라인 상태 코드 추출
        const lineStatusCode = document.querySelector("select[name='lineStatusCode']").value;

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(lineName) params.append("lineName", lineName)
        if(lineStatusCode) params.append("lineStatusCode", lineStatusCode)

        // 라인 목록 조회
        fetch(`/api/line?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
            .then(res => {
                if(res.status === 200){

                    console.log(res.data);

                    return lineGrid.resetData(res.data);
                }else{
                    alert(res.message);
                }
                return lineGrid.resetData([]);
            })
            .catch(e => {
                alert(e);
            });
    }

    // 선택된 공정 삭제 함수
    async function deleteSelectedProcesses(processes) {
        try {
            // 삭제할 공정 ID 목록
            const processIds = processes.map(process => parseInt(process.processId));

            // API 요청
            const response = await fetch('/api/line/disconnect-process', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    processIds: processIds
                })
            });

            const result = await response.json();

            if (result.status === 200) {
                alert('선택한 공정이 라인에서 삭제되었습니다.');

                // 공정 목록 다시 불러오기 (현재 선택된 라인 정보 사용)
                if (currentSelectedLine) {
                    getLineProcesses(currentSelectedLine.lineId, currentSelectedLine.lineName);
                }
            } else {
                alert(result.message || '공정 삭제에 실패했습니다.');
            }
        } catch (error) {
            console.error('공정 삭제 중 오류 발생:', error);
            alert('공정 삭제 중 오류가 발생했습니다.');
        }
    }

    const addNewLine = () => {
        const popup = window.open('/line-newForm', '_blank', 'width=800,height=850');

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

            if (message && message.type === "ADD_REFRESH_LINES") {
                getLines(); // 안전하게 리프레시 실행
            }
        });
    }

    // 상세 공정 추가 함수
    const addNewLineProcess = (lineInfo) => {
        const popup = window.open('/line-process-form', '_blank', 'width=800,height=850');

        if (!popup) {
            alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
            return;
        }

        // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        const messageHandler = (event) => {
            if (event.data === 'lineProcessReady') {
                popup.postMessage({
                    lineId: lineInfo.lineId,
                    lineName: lineInfo.lineName,
                    description: lineInfo.description || '' // 라인 설명 정보도 함께 전달
                }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);

        // 팝업 종료시 공정 리스트 새로 호출
        window.addEventListener("message", (event) => {
            const message = event.data;

            if (message && message.type === "REFRESH_LINE_PROCESSES") {
                // 라인 공정 목록 다시 불러오기
                getLineProcesses(lineInfo.lineId, lineInfo.lineName);
            }
        });
    }

    lineGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = lineGrid.getRow(rowKey);

        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && (rowKey || rowKey === 0)) {
            const popup = window.open('/line-form', '_blank', 'width=800,height=650');

            if (!popup) {
                alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
                return;
            }

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        lineId: rowData.lineId,
                        lineName: rowData.lineName,
                        lineStatusCode: rowData.lineStatusCode,
                        lineStatusName: rowData.lineStatusName,
                        description: rowData.description,
                        createdAt: rowData.createdAt,
                        createdBy: rowData.createdBy
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
            //팝업 종료시 라인 리스트 새로 호출
            window.addEventListener("message", (event) => {

                const message = event.data;

                if (message && message.type === "REFRESH_LINES") {
                    getLines(); // 안전하게 리프레시 실행
                }
            });
        }
    });

    // 선택한 라인에 소속된 공정 목록 조회
    function getLineProcesses(lineId, lineName) {
        // params 생성
        const params = new URLSearchParams();
        params.append("lineId", lineId);

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

                    // 데이터 리셋 후 삭제 버튼 상태도 업데이트
                    lineProcessGrid.resetData(res.data);

                    // 데이터가 로드된 후 삭제 버튼 상태 업데이트 (초기에는 비활성화)
                    if(deleteLineProcessBtn) {
                        deleteLineProcessBtn.disabled = true;
                    }

                    return;
                } else {
                    alert(res.message);
                }
                lineProcessGrid.resetData([]);
                if(deleteLineProcessBtn) {
                    deleteLineProcessBtn.disabled = true;
                }
            })
            .catch(e => {
                console.error("공정 목록 조회 실패:", e);
                alert("공정 목록을 불러오는데 실패했습니다.");
                lineProcessGrid.resetData([]);
                if(deleteLineProcessBtn) {
                    deleteLineProcessBtn.disabled = true;
                }
            });
    }

    // 공통코드 세팅
    setSelectBox("LIS", "lineStatusCode");

    // 페이지 진입 시 바로 리스트 호출
    getLines();
}

window.onload = () => {
    init();
}