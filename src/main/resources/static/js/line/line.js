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
        400,
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

    // 공정 추가 버튼 클릭
    const addBtn = document.querySelector("button[name='addNewLine']");
    if(addBtn){
        addBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            addNewLine();
        })
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

    const addNewLine = () => {
        const popup = window.open('/line-newForm', '_blank', 'width=800,height=750');

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

    lineGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = lineGrid.getRow(rowKey);
        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && (rowKey || rowKey === 0)) {
            const popup = window.open('/line-form', '_blank', 'width=800,height=750');

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

                if (message && message.type === "REFRESH_MACHINES") {
                    getLines(); // 안전하게 리프레시 실행
                }
            });
        }
    });

    // 공통코드 세팅
    setSelectBox("LIS", "lineStatusCode");

    // 페이지 진입 시 바로 리스트 호출
    getLines();
}

window.onload = () => {
    init();
}