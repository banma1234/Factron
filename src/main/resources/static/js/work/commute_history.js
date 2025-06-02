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
        ],
    });
}

const init = () => {
    const testGrid = initGrid();
    const getEmployeeId = () => document.getElementById('employeeId').value;

    // 오늘 날짜 구하기 (yyyy-mm-dd)
    const today = new Date().toISOString().slice(0, 10);
    const empId = "3"; // 임의의 사번

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            // key 매핑
            const mapped = (res.data || []).map(item => ({
                id: item.empId,
                name: item.empName,
                position: item.positionName,
                department: item.deptName,
                date: item.commuteDate,
                inTime: item.commuteIn,
                outTime: item.commuteOut
            }));
            testGrid.resetData(mapped);
        });
    }, false);

    async function getData() {
        const nameOrId = document.querySelector("input[name='srhNameOrId']").value;
        const dept = document.querySelector("select[name='srhDepartment']").value;
        const strBirth = document.querySelector("input[name='srhStrBirth']").value;
        const endBirth = document.querySelector("input[name='srhEndBirth']").value;

        if (strBirth && endBirth && new Date(strBirth) > new Date(endBirth)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        const params = new URLSearchParams();
        if (nameOrId) params.append("nameOrId", nameOrId);
        if (dept) params.append("dept", dept);
        if (strBirth) params.append("startDate", strBirth);
        if (endBirth) params.append("endDate", endBirth);

        let url = "/api/commute"; // 엔드포인트 확인
        if ([...params].length > 0) {
            url += "?" + params.toString();
        }

        try {
            const res = await fetch(url, {
                method: "GET",
                headers: { "Content-Type": "application/json", "empId": "3"}
            });
            if (!res.ok) {
                // 에러 응답일 때 텍스트로 받아서 콘솔에 출력
                const errorText = await res.text();
                console.error("API Error:", res.status, errorText);
                alert("데이터 조회 중 오류가 발생했습니다.");
                return { data: [] };
            }
            return res.json();
        } catch (e) {
            console.error(e);
            alert("네트워크 오류가 발생했습니다.");
            return { data: [] };
        }
    }

    // 검색 버튼 이벤트 (나중에 쓸 경우 주석 처리)
    // document.querySelector(".searchBtn").addEventListener("click", function(e) {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     getData();
    // }, false);

    // 페이지 로드시 자동 조회 (당일, 본인)
    // getData().then(res => {
    //     const mapped = (res.data || []).map(item => ({
    //         id: item.empId,
    //         name: item.empName,
    //         position: item.positionName,
    //         department: item.deptName,
    //         date: item.commuteDate,
    //         inTime: item.commuteIn,
    //         outTime: item.commuteOut
    //     }));
    //     testGrid.resetData(mapped);
    // });
}

window.onload = () => {
    init();
}