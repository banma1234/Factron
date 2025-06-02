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
                name: 'empId',
                align: 'center'
            },
            {
                header: '사원이름',
                name: 'empName',
                align: 'center'
            },
            {
                header: '직급',
                name: 'positionName',
                align: 'center'
            },
            {
                header: '부서',
                name: 'deptName',
                align: 'center'
            },
            {
                header: '일자',
                name: 'commuteDate',
                align: 'center'
            },
            {
                header: '출근 시간',
                name: 'commuteIn',
                align: 'center'
            },
            {
                header: '퇴근 시간',
                name: 'commuteOut',
                align: 'center'
            }
        ],
    });
}

const init = () => {
    const testGrid = initGrid();
    const getEmployeeId = () => document.getElementById('employeeId').value;

    // 초기 값 설정
    const today = new Date().toISOString().slice(0, 10);
    const empId = "5"; // 임의의 사번

    // 폼에 값 세팅
    document.querySelector("input[name='srhNameOrId']").value = empId;
    document.querySelector("input[name='srhStrBirth']").value = today;
    document.querySelector("input[name='srhEndBirth']").value = today;

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            testGrid.resetData(res.data);
        });
    }, false);

    async function getData(extraParams = {}) {
        let nameOrId = document.querySelector("input[name='srhNameOrId']").value;
        const dept = document.querySelector("select[name='srhDepartment']").value;
        let strBirth = document.querySelector("input[name='srhStrBirth']").value;
        let endBirth = document.querySelector("input[name='srhEndBirth']").value;

        if (strBirth && endBirth && new Date(strBirth) > new Date(endBirth)) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        // 파라미터 객체 생성
        const paramsObj = {
            ...extraParams // empId 등 외부에서 전달된 파라미터
        };
        if (nameOrId) paramsObj.nameOrId = nameOrId;
        if (dept) paramsObj.dept = dept;
        if (strBirth) paramsObj.startDate = strBirth;
        if (endBirth) paramsObj.endDate = endBirth;

        const params = new URLSearchParams(paramsObj);

        let url = "/api/commute";
        if ([...params].length > 0) {
            url += "?" + params.toString();
        }

        try {
            const res = await fetch(url, {
                method: "GET",
                headers: { "Content-Type": "application/json"}
            });
            if (!res.ok) {
                const errorText = await res.text();
                console.error("API Error:", res.status, errorText);
                alert("데이터 조회 중 오류가 발생했습니다.");ㅛ
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



    // 최초 진입 시 empId=3, 오늘 날짜로 조회
    getData({ empId, startDate: today, endDate: today }).then(res => {
        testGrid.resetData(res.data);
    });
}

window.onload = () => {
    init();
}