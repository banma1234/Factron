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
        scrollY: true,
        bodyHeight: 400,
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
                    const upperValue = toUpperCase(value.value)
                    return `${upperValue==='Y' ? '재직' : '퇴직'}`
                }
            }
        ],
        data: employees
    });
}

// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}

/**
 * 대문자 변환
 * @param str
 * @returns {string}
 */
const toUpperCase = (str) => {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}

const removeHyphens = (phoneNumber) => {
    return phoneNumber.replace(/-/g, '');
}

const init = () => {
    const employeeGrid = initGrid(); // grid 초기 세팅
    getEmployees(); //초기 사원 리스트 호출

    // 버튼에사원 조회 API 호출 기능 추가
    const srhBtn = document.querySelector(".empSrhBtn");

    if (srhBtn) {
        srhBtn.addEventListener("click",(e)=>{
            e.preventDefault();
            e.stopPropagation();
            getEmployees();
        });
    }

    const addBtn = document.querySelector("button[name='addNewEmp']");
    console.log(addBtn)
    if(addBtn){
        addBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            addNewEmployee();
        })
    }

    // 사원 목록 조회
    async function getEmployees() {

        const selectDept = document.querySelector("select[name='deptCode']");
        const selectPosition = document.querySelector("select[name='positionCode']");
        const selectIsActive = document.querySelector("select[name='isActive']");
        const opp = document.querySelector("option[value='EMP001']");
        // 사원 정보 추출
        const dept = removeSpaces(selectDept.options[selectDept.selectedIndex].value);
        const position = removeSpaces(selectPosition.options[selectPosition.selectedIndex].value);
        const empIsActive = removeSpaces(selectIsActive.options[selectIsActive.selectedIndex].value);
        const name = removeSpaces(document.querySelector("input[name='name']").value);

        // params 생성
        const params = new URLSearchParams();

        // params에 검색어 추가
        if(dept) params.append("deptCode", dept)
        if(position) params.append("positionCode", position)
        if(name) params.append("nameOrId", name)
        if(empIsActive) params.append("empIsActive", empIsActive)
        // EmployeeList API 호출
        fetch(`/api/employee?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
        .then(res => {
            if(res.status === 200){
                console.log(res.data)
                return employeeGrid.resetData(res.data); // grid에 세팅
            }
            return employeeGrid.resetData([]);
        })
        .catch(e => {
            alert(e);
        });
    }

    const addNewEmployee = () => {
        console.log("Clicked")
        const popup = window.open('/employee-newForm', '_blank', 'width=800,height=750');

        if (!popup) {
            alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
            return;
        }

        // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        const messageHandler = (event) => {
            if (event.data === 'addReady') {
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
        //팝업 종료시 사원 리스트 새로 호출
        window.addEventListener("message", (event) => {

            const message = event.data;

            if (message && message.type === "ADD_REFRESH_EMPLOYEES") {
                getEmployees();  // 안전하게 리프레시 실행
            }
        });
    }

    employeeGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = employeeGrid.getRow(rowKey);
        // 새 창에서 해당 ID를 기반으로 상세페이지 오픈
        if (rowData && (rowKey || rowKey === 0)) {
            const popup = window.open('/employee-form', '_blank', 'width=800,height=750');

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
                        birth: rowData.birth,
                        rrnBack: rowData.rrnBack,
                        email: rowData.email,
                        gender: rowData.gender,
                        eduLevelCode: rowData.eduLevelCode,
                        eduLevelName: rowData.eduLevelName,
                        address: rowData.address,
                        empIsActive: rowData.empIsActive,
                        joinedDate: rowData.joinedDate,
                        quitDate: rowData.quitDate,
                        employCode: rowData.employCode,
                        employName: rowData.employName,
                        phone: removeHyphens(rowData.phone)
                    }, "*");
                    window.removeEventListener("message", messageHandler);
                }
            };
            window.addEventListener("message", messageHandler);
            //팝업 종료시 사원 리스트 새로 호출
            window.addEventListener("message", (event) => {

                const message = event.data;

                if (message && message.type === "REFRESH_EMPLOYEES") {
                    getEmployees();  // 안전하게 리프레시 실행
                }
            });
        }
    });
}

window.onload = () => {
    init();
}