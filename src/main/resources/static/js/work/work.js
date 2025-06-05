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
        scrollY: true,
        bodyHeight: 480,
        columns: [
            {
                header: '사원번호',
                name: 'empId',
                align: 'center'
            },
            {
                header: '이름',
                name: 'empName',
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
    const workGrid = initGrid();
    let workCodeList = [];

    // 검색 초기 세팅
    const today = new Date();
    today.setHours(today.getHours() + 9); // 한국 시간대
    const todayStr = today.toISOString().split('T')[0];
    document.querySelector("input[name='srhStrDate']").value = todayStr;
    document.querySelector("input[name='srhEndDate']").value = todayStr;

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            workGrid.resetData(res.data); // grid에 세팅
        });
    }, false);

    // 엔터 시 검색
    document.querySelector('.test__form').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출(새로고침) 방지

        // 조회
        getData().then(res => {
            workGrid.resetData(res.data);
        });
    });

    // 목록 조회
    async function getData() {
        // validation
        const startDate = document.querySelector("input[name='srhStrDate']").value;
        const endDate = document.querySelector("input[name='srhEndDate']").value;
        if ((startDate && !endDate) || (!startDate && endDate)) {
            alert("시작 및 종료 날짜를 모두 입력해주세요.");
            return { data: [] };
        }

        if (startDate && endDate) {
            const dateRegex = /^\d{4}-\d{2}-\d{2}$/;

            if (!dateRegex.test(startDate) || isNaN(Date.parse(startDate))
                || !dateRegex.test(endDate) || isNaN(Date.parse(endDate))) {
                alert("날짜 형식이 올바르지 않습니다.");
                return { data: [] };
            }
            if (new Date(startDate) > new Date(endDate)) {
                alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
                return { data: [] };
            }
        }

        // fetch data
        const data = new URLSearchParams({
            srhIdOrName: document.querySelector("input[name='srhIdOrName']").value,
            srhStrDate: startDate,
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

    // 근무 등록 팝업 오픈
    document.querySelector(".registWork").addEventListener("click", function(e) {
        const popup = window.open('/work/save', '_blank', 'width=800,height=450');

        // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage({
                    empId: '2', // 하드코딩
                    empName: '홍길동',
                    workCodeList: workCodeList
                }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    // 부서 세팅
    getSysCodeList("DEP").then(res => {
        const selectElement = document.querySelector("select[name='srhDeptCode']");

        // 하드코딩
        const data = [
            {
                "detailCode": "DEP001",
                "name": "인사부"
            },
            {
                "detailCode": "DEP002",
                "name": "개발부"
            },
            {
                "detailCode": "DEP003",
                "name": "영업부"
            },
            {
                "detailCode": "DEP004",
                "name": "생산부"
            }
        ];

        // for(const dept of res.data) {
        for(const dept of data) {
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

        // 하드코딩
        const data = [
            {
                "detailCode": "WRK001",
                "name": "일반근무"
            },
            {
                "detailCode": "WRK002",
                "name": "외근"
            },
            {
                "detailCode": "WRK003",
                "name": "야근"
            },
            {
                "detailCode": "WRK004",
                "name": "특근"
            }
        ];

        workCodeList = data.filter(work=> work.detailCode !== "WRK001"); // 팝업 전달 데이터 (일반근무 제외)

        // for(const work of res.data) {
        for(const work of data) {
            const optionElement = document.createElement("option");
            optionElement.value = work.detailCode;  // 코드
            optionElement.textContent = work.name;  // 이름

            selectElement.appendChild(optionElement);
        }
    }).catch(e => {
        console.error(e);
    });

    // 공통코드 목록 조회
    async function getSysCodeList(mainCode) {
        const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return res.json();
    }

    // 페이지 진입 시 바로 리스트 호출
    getData().then(res => {
        workGrid.resetData(res.data); // grid에 세팅
    });
}

window.onload = () => {
    init();
}