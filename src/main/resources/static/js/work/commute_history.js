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
        el: document.getElementById('commute_history_grid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        // rowHeaders: ['rowNum'],
        columns: [
            {
                header: '사원번호',
                name: 'id',
                align: 'center'
            },
            {
                header: '사원이름',
                name: 'name',
                align: 'center'
            },
            {
                header: '직급',
                name: 'position',
                align: 'center'
            },
            {
                header: '부서',
                name: 'department',
                align: 'center'
            },
            {
                header: '일자',
                name: 'date',
                align: 'center'
            },
            {
                header: '출근 시간',
                name: 'inTime',
                align: 'center'
            },
            {
                header: '퇴근 시간',
                name: 'outTime',
                align: 'center'
            }
        ]
    });
}

const init = () => {
    const testGrid = initGrid();
    const getEmployeeId = () => document.getElementById('employeeId').value;

    async function getData() {
        const strBirth = document.querySelector("input[name='srhStrBirth']").value;
        const endBirth = document.querySelector("input[name='srhEndBirth']").value;
        if (strBirth && endBirth && new Date(strBirth) > new Date(endBirth)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }
        const params = new URLSearchParams({
            // empId: getEmployeeId(),
            empId: 3, // 임시로 3번 사원으로 설정
            startDate: strBirth,
            endDate: endBirth,
            nameOrId: document.querySelector("input[name='srhNameOrId']").value,
            department: document.querySelector("select[name='srhDepartment']").value
        });
        try {
            const res = await fetch(`/commute?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json", "empId": "3"}
            });
            const result = await res.json();
            if(result.success) {
                testGrid.resetData(result.data);
            } else {
                alert(result.message);
            }
        } catch (e) {
            console.error(e);
        }
    }

    // 검색 버튼 이벤트 (나중에 쓸 경우 주석 처리)
    // document.querySelector(".searchBtn").addEventListener("click", function(e) {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     getData();
    // }, false);

    // 페이지 로드시 자동 조회
    getData();

}

window.onload = () => {
    init();
}