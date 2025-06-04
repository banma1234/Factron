// grid 초기화
const initGrid = (employees) => {
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
        el: document.getElementById('employee_grid'),
        scrollX: false,
        scrollY: false,
        minBodyHeight: 30,
        rowHeaders: ['rowNum'],
        columns: [
            {
                header: '번호',
                name: 'id',
                hidden: true
            },
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
                header: '부서',
                name: 'deptName',
                align: 'center'
            },
            {
                header: '직급',
                name: 'positionName',
                align: 'center'
            },
            {
                header: '전화번호',
                name: 'phone',
                align: 'center'
            },
            {
                header: '재직상태',
                name: 'empIsActive',
                align: 'center',
                formatter: (value) => {
                    return `${value==='y' ? '재직' : '퇴직'}`
                }
            }
        ],
        data: employees
    });
}

const init = () => {
    // grid 초기 세팅
    const employeeGrid = initGrid();
    getEmployees();
    // 버튼에사원 조회 API 호출 기능 추가
    // document.addEventListener("DOMContentLoaded", () => {
    //
    // });
    const btn = document.querySelector(".empSrhBtn");
    if (btn) {
        btn.addEventListener("click",(e)=>{
            e.preventDefault();
            e.stopPropagation();
            getEmployees();
        });
    }
    // 사원 목록 조회
    async function getEmployees() {
        const selectDept = document.querySelector("select[name='deptCode']");
        const selectPosition = document.querySelector("select[name='positionCode']");
        const selectIsActive = document.querySelector("select[name='isActive']");

        // 사원 정보 추출
        const dept = selectDept.options[selectDept.selectedIndex].value;
        const position = selectDept.options[selectPosition.selectedIndex].value;
        const empIsActive = selectDept.options[selectIsActive.selectedIndex].value;
        const name = document.querySelector("input[name='name']").value;

        // params에 검색어 추가
        const params = new URLSearchParams();

        if(dept) params.append("deptCode", dept)
        if(position) params.append("positionCode", position)
        if(name) params.append("nameOrId", name)
        if(empIsActive) params.append("isActive", empIsActive)

        fetch(`/api/employee?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
        .then(res => {
            if(res.status == 200) {
                // window.alert(res.message);
                return employeeGrid.resetData(res.data);
            }; // grid에 세팅
            window.alert(res.message);
            return employeeGrid.resetData([]);
        })
        .catch(e => {
            console.error(e);
        });
    }

    employeeGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = employeeGrid.getRow(rowKey);
        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && (rowKey || rowKey === 0)) {
            const popup = window.open('/employee-form', '_blank', 'width=800,height=600');

            if (!popup) {
                alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
                return;
            }

            // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
            const messageHandler = (event) => {
                if (event.data === 'ready') {
                    popup.postMessage({
                        name: rowData.empName,
                        empId: rowData.empId,
                        positionCode: rowData.positionCode,
                        positionName: rowData.positionName,
                        deptCode: rowData.deptCode,
                        deptName: rowData.deptName,
                        rrn: rowData.residentRegistrationNumber,
                        email: rowData.email,
                        gender: rowData.gender,
                        eduLevelCode: rowData.eduLevelCode,
                        eduLevelName: rowData.eduLevelName,
                        address: rowData.address,
                        empIsActive: rowData.empIsActive,
                        joinedDate: rowData.joinedDate,
                        employCode: rowData.employCode,
                        employName: rowData.employName
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
        }
    });
}

window.onload = () => {
    init();
}