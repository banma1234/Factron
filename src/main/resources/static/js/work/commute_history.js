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
        el: document.getElementById('commuteHistoryGrid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
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

// 한국 시간 기준 오늘 날짜 구하기
function getKoreaToday() {
    const now = new Date();
    now.setHours(now.getHours() + 9); // UTC+9
    return now.toISOString().slice(0, 10);
}

const init = () => {
    const testGrid = initGrid();
    // const getEmployeeId = () => document.getElementById('employeeId').value;

    // 초기 값 설정
    const today = getKoreaToday();
    const empId = "1"; // 임의의 사번 -> 추후에 base.html 에서 시큐리티 세션으로 받은 사용자 객체를 통해 추출
    const empName = "홍길동"; // 임의의 사원 이름 -> 추후에 base.html 에서 시큐리티 세션으로 받은 사용자 객체를 통해 추출

    // 폼에 값 세팅
    document.querySelector("input[name='srhNameOrId']").value = empId;
    document.querySelector("input[name='srhStrBirth']").value = today;
    document.querySelector("input[name='srhEndBirth']").value = today;

    // 모달 관련 변수
    const commuteConfirmModal = new bootstrap.Modal(document.getElementsByClassName("commuteConfirmModal")[0]);
    const commuteConfirmMsg = document.getElementsByClassName("commuteConfirmMsg")[0];
    const commuteConfirmBtn = document.getElementsByClassName("commuteConfirmBtn")[0];
    let commuteAction = null; // 'in' 또는 'out'

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            testGrid.resetData(res.data);
        });
    }, false);

    // fetch 함수 선언
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


    // 출근 API 호출 함수
    async function commuteIn(empId) {
        const res = await fetch('/api/commute', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'empId': empId
            }
        });
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    }

    // 퇴근 API 호출 함수
    async function commuteOut(empId) {
        const res = await fetch('/api/commute', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'empId': empId
            }
        });
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    }

    // 출근 버튼 이벤트
    const cmInBtn = document.querySelector('.cmInBtn');
    if (cmInBtn) {
        cmInBtn.addEventListener('click', () => {
            commuteConfirmMsg.textContent = `${empName}님 출근 하시겠습니까?`;
            commuteAction = 'in';
            commuteConfirmModal.show();
        });
    }

// 퇴근 버튼 이벤트
    const cmOutBtn = document.querySelector('.commuteOutBtn .cmOutBtn');
    if (cmOutBtn) {
        cmOutBtn.addEventListener('click', () => {
            commuteConfirmMsg.textContent = `${empName}님 퇴근 하시겠습니까?`;
            commuteAction = 'out';
            commuteConfirmModal.show();
        });
    }

    // 모달 확인 버튼 이벤트
    commuteConfirmBtn.addEventListener('click', async () => {
        try {
            if (commuteAction === 'in') {
                await commuteIn(empId);
                alert('출근 처리 완료');
            } else if (commuteAction === 'out') {
                await commuteOut(empId);
                alert('퇴근 처리 완료');
            }
            commuteConfirmModal.hide();
            location.reload();
        } catch (e) {
            alert('처리 실패: ' + e.message);
            commuteConfirmModal.hide();
        }
    });

    // 최초 진입 시 empId, 오늘 날짜로 조회
    getData({ empId, startDate: today, endDate: today }).then(res => {
        testGrid.resetData(res.data);
    });
}

window.onload = () => {
    init();
}