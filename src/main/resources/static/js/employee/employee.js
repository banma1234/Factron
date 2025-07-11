// 공백 제거 함수
const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}
// 대문자 변환 함수
const toUpperCase = (str) => {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}
// 하이픈 제거 함수
const removeHyphens = (phoneNumber) => {
    return phoneNumber.replace(/-/g, '');
}


const init = () => {
    const employeeGrid = initGrid(
        document.getElementById('employee_grid'),
        400,
        [
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
                    if (upperValue === 'Y') return `<span style="color:green;">재직</span>`;  // 재직 - 초록색
                    if (upperValue === 'N') return `<span style="color:red;">퇴직</span>`;   // 퇴직 - 빨간색
                    return `${upperValue==='Y' ? '재직' : '퇴직'}`
                }
            }
        ]
    );

    // 검색 버튼 클릭 이벤트
    document.querySelector(".empSrhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getEmployees();

    }, false);

    // 엔터 시 검색
    document.querySelector(".search__form").addEventListener("submit", function(e) {
        e.preventDefault();

        getEmployees();
    });

    // 사원 추가 버튼 클릭
    const addBtn = document.querySelector("button[name='addNewEmp']");
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

        // 사원 목록 API 호출
        fetch(`/api/employee?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => res.json())
        .then(res => {
            if(res.status === 200){
                return employeeGrid.resetData(res.data); // grid에 세팅
            }else{
                alert(res.message);
            }
            return employeeGrid.resetData([]);
        })
        .catch(e => {
            alert(e);
        });
    }

    const addNewEmployee = () => {
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

    // 공통코드 세팅
    setSelectBox("DEP", 'deptCode');
    setSelectBox("POS", 'positionCode');

    // 페이지 진입 시 바로 리스트 호출
    getEmployees();
}

window.onload = () => {
    init();
}