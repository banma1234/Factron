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

    const today = new Date();
    today.setHours(today.getHours() + 9);
    const todayStr = today.toISOString().split('T')[0];
    document.querySelector("input[name='startDate']").value = todayStr;
    document.querySelector("input[name='endDate']").value = todayStr;

    // vacationData 초기값 적용
    if (typeof vacationData !== 'undefined' && vacationData.length > 0) {
        vacationGrid.resetData(vacationData);
    }

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
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;


        console.log("조회 시작일:", startDate);
        console.log("조회 종료일:", endDate);


        if (!startDate || !endDate) {
            alert("시작일과 종료일을 모두 입력해주세요.");
            return;
        }

        if (new Date(startDate) > new Date(endDate)) {
            alert("시작일은 종료일보다 이전이어야 합니다.");
            return;
        }

        try {
            console.log("API 호출 시작: ", `/api/vacation/${startDate}&${endDate}`);

            const res = await fetch(`/api/vacation/${startDate}&${endDate}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "empId": "10001"
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
                    empId: '10001',
                    empName: '홍길동'
                }, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });
};

window.onload = () => {
    init();
};
