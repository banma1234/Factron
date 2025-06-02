// grid 초기화
const initGrid = () => {
    const Grid = tui.Grid;

    Grid.applyTheme('default', {
        cell: {
            normal: { border: 'gray' },
            header: { background: 'gray', text: 'white', border: 'gray' },
            rowHeaders: { header: { background: 'gray', text: 'white' } }
        }
    });

    return new Grid({
        el: document.getElementById('approvalGrid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
        columns: [
            { header: '결재번호', name: 'approvalId', align: 'center' },
            { header: '결재 유형', name: 'apprTypeName', align: 'center' },
            { header: '이름', name: 'requesterName', align: 'center' },
            { header: '사번', name: 'requesterId', align: 'center' },
            { header: '직급', name: 'positionName', align: 'center' },
            { header: '부서', name: 'deptName', align: 'center' },
            {
                header: '발행일자', name: 'requested_at', align: 'center',
                formatter: ({ value }) => {
                    if (!value) return "";
                    const date = new Date(value);
                    const year = date.getFullYear();
                    const month = (date.getMonth() + 1).toString().padStart(2, "0");
                    const day = date.getDate().toString().padStart(2, "0");
                    return `${year}-${month}-${day}`;
                }
            },
            {
                header: '결재 날짜', name: 'confirmedDate', align: 'center',
                formatter: ({ value }) => {
                    if (!value) return "";
                    const date = new Date(value);
                    const year = date.getFullYear();
                    const month = (date.getMonth() + 1).toString().padStart(2, "0");
                    const day = date.getDate().toString().padStart(2, "0");
                    return `${year}-${month}-${day}`;
                }
            }
            ,
            { header: '상태', name: 'approvalStatusName', align: 'center' },
            { header: '승인 권자', name: 'approverName', align: 'center' }
        ]
    });
}


const init = () => {
    // grid 초기 세팅
    const testGrid = initGrid();

    // 👉 가짜 로그인 사용자 정보 (하드코딩)
    const currentUser = {
        id: "20250001",
        authCode: "ATH001"
    };
    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            testGrid.resetData(res.data); // grid에 세팅
        });
    }, false);

    // form 창 오픈
    testGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = testGrid.getRow(rowKey);

        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && rowData.id) {
            const popup = window.open('/test-form', '_blank', 'width=800,height=600');

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        name: rowData.name,
                        age: rowData.id,
                        birth: rowData.birth,
                        regDate: rowData.regDate,
                        remark: rowData.address
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });

    // 목록 조회
    window.getData = async function () {
        console.log("getData")
        // validation
        // const strBirth = document.querySelector("input[name='srhStrBirth']").value;
        // const endBirth = document.querySelector("input[name='srhEndBirth']").value;
        // if (new Date(strBirth) > new Date(endBirth)) {
        //     alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        //     return;
        // }

        // fetch data
        // const data = new URLSearchParams({
        //     srhName: document.querySelector("input[name='srhName']").value,
        //     srhStrBirth: strBirth,
        //     srhEndBirth: endBirth,
        //     srhAddress: document.querySelector("select[name='srhAddress']").value
        // });

        // 조회 필터 정보 가져오기
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='ednDate']").value;
        const apprType = document.querySelector("select[name='APR']").value;
        const dept = document.querySelector("select[name='DEP']").value;
        const position = document.querySelector("select[name='POS']").value;
        const approvalNameOrEmpId = document.querySelector("input[name='srhName']").value;

        // 날짜 검증
        if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        // 쿼리 파라미터 구성
        const params = new URLSearchParams({
            startDate,
            endDate,
            apprType,
            dept,
            position,
            approvalNameOrEmpId
        });

        try {
            const res = await fetch(`/api/approval?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }

    // 지역 세팅(공통코드 세팅)
    // getSysCodeList("DEP").then(data => {
    //     console.log(data)
    //     const selectElement = document.querySelector("select[name='address']");
    //
    //     for(const region of data) {
    //         const optionElement = document.createElement("option");
    //         optionElement.value = region.code;  // 코드
    //         optionElement.textContent = region.codeName;  // 이름
    //
    //         selectElement.appendChild(optionElement);
    //     }
    // }).catch(e => {
    //     console.error(e);
    // });


    // 지역 목록 조회 (공통코드 조회)
    // async function getSysCodeList(mainCode) {
    //     const res = await fetch(`/api/sys/detail?${mainCode}`, {
    //         method: 'GET',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         }
    //     });
    //     return res.json();
    // }
}

window.onload = () => {
    init();
}