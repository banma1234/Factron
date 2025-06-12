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
        el: document.getElementById('grid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
        columns: [
            { header: '사원번호', name: 'empId', align: 'center' },
            { header: '이름', name: 'empName', align: 'center' },
            { header: '직급', name: 'positionName', align: 'center' },
            { header: '부서', name: 'deptName', align: 'center' },
            { header: '시작날짜', name: 'vacationStartDate', align: 'center' },
            { header: '종료날짜', name: 'vacationEndDate', align: 'center' },
            { header: '비고', name: 'remark', align: 'left' }
        ]
    });
};


const init = () => {
    const vacationGrid = initGrid();
    const empId = "10001";
    const today = new Date();
    today.setHours(today.getHours() + 9);
    const todayStr = today.toISOString().split('T')[0];

    document.querySelector("input[name='srhIdOrName']").value = empId;
    document.querySelector("input[name='startDate']").value = todayStr;
    document.querySelector("input[name='endDate']").value = todayStr;


    // 검색 버튼 클릭 이벤트
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        fetchData().then(res => {
            vacationGrid.resetData(res.data);
        });

    }, false);

    // 엔터 시 검색
    document.querySelector(".vacation__form").addEventListener("submit", function(e) {
        e.preventDefault();

        fetchData().then(res => {
            vacationGrid.resetData(res.data);
        });
    });

    // 조회 함수
    async function fetchData() {
        const nameOrId = document.querySelector("input[name='srhIdOrName']").value;
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;

        if (!startDate || !endDate) {
            alert("시작일과 종료일을 모두 입력해주세요.");
            return;
        }

        if (new Date(startDate) > new Date(endDate)) {
            alert("시작일은 종료일보다 이전이어야 합니다.");
            return;
        }

        const data = new URLSearchParams({
            srhIdOrName: document.querySelector("input[name='srhIdOrName']").value,
            vacationStartDate: startDate,
            vacationEndDate: endDate,
            deptCode: document.querySelector("select[name='srhDeptCode']").value
        });

        try {
            const res = await fetch(`/api/vacation?${data.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }

            });
            return res.json();

        } catch (err) {
            console.error("fetch error", err);
            alert("서버 요청 실패");
        }
    }

    // 휴가 신청 팝업 오픈
    document.querySelector(".registVacation").addEventListener("click", function(e) {
        const popup = window.open('/vacation/save', '_blank', 'width=800,height=450');

        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage({
                    empId: '10002',
                    empName: '박서준'
                }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });


    // 공통코드 목록 조회
    window.getSysCodeList = async function (mainCode) {
        const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return res.json();
    }

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



    // 페이지 진입 시 바로 리스트 호출
    fetchData().then(res => {
        vacationGrid.resetData(res.data);
    });
};


window.onload = () => {
    init();
};
