// grid 초기화
const initGrid = () => {
    const Grid = tui.Grid;

    // 테마
    Grid.applyTheme('default',  {
        cell: {
            normal: {
                border: 'gray'
            },
            header: {
                background: 'gray',
                text: 'white',
                border: 'gray'
            },
            rowHeaders: {
                header: {
                    background: 'gray',
                    text: 'white'
                }
            }
        }
    });

    // 세팅
    return new Grid({
        el: document.getElementById('workGrid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        // rowHeaders: ['rowNum'],
        columns: [
            {
                header: '사원번호',
                name: 'empId'
            },
            {
                header: '이름',
                name: 'name',
                align: 'center'
            },
            {
                name: 'deptCode',
                hidden: true
            },
            {
                header: '부서',
                name: 'deptName',
                align: 'center'
            },
            {
                name: 'positionCode',
                hidden: true
            },
            {
                header: '직급',
                name: 'positionName',
                align: 'center'
            },
            {
                header: '근무 일자',
                name: 'workDate',
                align: 'center'
            },
            {
                header: '시작시간',
                name: 'startTime',
                align: 'center'
            },
            {
                header: '종료시간',
                name: 'endTime',
                align: 'center'
            },
            {
                name: 'workCode',
                hidden: true
            },
            {
                header: '근무 유형',
                name: 'workName',
                align: 'center'
            },
        ],
    });
}

const init = () => {
    // grid 초기 세팅
    const workGrid = initGrid();

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            workGrid.resetData(res.data); // grid에 세팅
        });
    }, false);

    // form 창 오픈
    // workGrid.on('dblclick', (e) => {
    //     const rowKey = e.rowKey;
    //     const rowData = workGrid.getRow(rowKey);
    //
    //     // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
    //     if (rowData && rowData.id) {
    //         const popup = window.open('/test-form', '_blank', 'width=800,height=600');
    //
    //         // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
    //         const messageHandler = (event) => {
    //             if (event.data === 'ready') {
    //                 popup.postMessage({
    //                     name: rowData.name,
    //                     age: rowData.id,
    //                     birth: rowData.birth,
    //                     regDate: rowData.regDate,
    //                     remark: rowData.address
    //                 }, "*");
    //                 window.removeEventListener("message", messageHandler);
    //             }
    //         };
    //         window.addEventListener("message", messageHandler);
    //     }
    // });

    // 목록 조회
    window.getData = async function () {
        // validation
        const strDate = document.querySelector("input[name='srhStrDate']").value;
        const endDate = document.querySelector("input[name='srhEndDate']").value;
        if (new Date(strDate) > new Date(endDate)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        // fetch data
        const data = new URLSearchParams({
            srhIdOrName: document.querySelector("input[name='srhIdOrName']").value,
            srhStrDate: strDate,
            srhEndDate: endDate,
            srhDeptCode: document.querySelector("select[name='srhDeptCode']").value,
            srhWorkCode: document.querySelector("select[name='srhWorkCode']").value,
        });

        try {
            const res = await fetch(`/api/work?${data.toString()}`, {
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

    // 부서 세팅
    getSysCodeList("DEP").then(res => {
        const selectElement = document.querySelector("select[name='srhDeptCode']");

        for(const dept of res.data) {
            const optionElement = document.createElement("option");
            optionElement.value = dept.detailCode;  // 코드
            optionElement.textContent = dept.name;  // 이름

            selectElement.appendChild(optionElement);
        }
    }).catch(e => {
        console.error(e);
    });

    // 근무유형 세팅
    getSysCodeList("WRK").then(res => {
        const selectElement = document.querySelector("select[name='srhWorkCode']");

        for(const work of res.data) {
            const optionElement = document.createElement("option");
            optionElement.value = work.detailCode;  // 코드
            optionElement.textContent = work.name;  // 이름

            selectElement.appendChild(optionElement);
        }
    }).catch(e => {
        console.error(e);
    });

    // select 목록 조회
    async function getSysCodeList(mainCode) {
        const res = await fetch(`/api/sys/detail?${mainCode}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return res.json();
    }
}

window.onload = () => {
    init();
}