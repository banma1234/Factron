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
            { header: '결재 코드', name: 'apprTypeCode', hidden:'ture' },
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
            { header: '상태코드', name: 'approvalStatusCode', hidden:'ture' },
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
        authCode: "ATH002"
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

    testGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = testGrid.getRow(rowKey);

        if (rowData && rowData.approvalId) {
            // 결재 유형 코드 확인
            let formUrl = "";
            switch (rowData.apprTypeName) {
                case "발령":
                    formUrl = "/approval/transferApproval-form";
                    break;
                case "휴가":
                    formUrl = "/approval/vacationApproval-form";
                    break;
                case "근무":
                    formUrl = "/approval/workApproval-form";
                    break;
                default:
                    alert("유효하지 않은 결재 유형입니다.");
                    return;
            }

            // 새 창 열기 (쿼리 스트링 제거)
            const popup = window.open(formUrl, '_blank', 'width=800,height=800');
            console.log('popup:', popup);
            if (!popup) {
                alert('팝업이 차단되었습니다. 팝업 차단 해제 후 다시 시도하세요.');
                return;
            }


            // postMessage로 데이터 전달
            const messageHandler = (event) => {
                console.log("부모 창에서 받은 메시지:", event.data);
                if (event.data === 'ready') {
                    console.log('부모 창: ready 받음, 자식 창에 데이터 전송 시작');
                    popup.postMessage({
                        approvalId: rowData.approvalId,
                        apprTypeCode: rowData.apprTypeCode,
                        approvalStatusCode: rowData.approvalStatusCode,
                        userId: currentUser.id,
                        authCode: currentUser.authCode
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
            const data = await res.json();  // JSON 파싱 필수
            testGrid.resetData(data.data);  // data 객체 안에 실제 배열이 data 프로퍼티에 있다면
            return data;
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