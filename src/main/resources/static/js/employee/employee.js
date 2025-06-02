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
    employees = [
        {
            id: 1,
            name: '계두식',
            department: '인사',
            position: '사원',
            phone: '010-5223-1234',
            status: 'y',

        },
        {
            id: 2,
            name: '강철중',
            department: '인사',
            position: '사원',
            phone: '010-5223-1234',
            status: 'y',
        },
        {
            id: 3,
            name: '김갑환',
            department: '인사',
            position: '사원',
            address: '대구광역시 XX구 XX동',
            phone: '010-5223-1234',
            status: 'n',
        }
    ]
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
                name: 'employeeId',
                align: 'center'
            },
            {
                header: '이름',
                name: 'name',
                align: 'center'
            },
            {
                header: '부서',
                name: 'department',
                align: 'center'
            },
            {
                header: '직급',
                name: 'position',
                align: 'center'
            },
            {
                header: '전화번호',
                name: 'phone',
                align: 'center'
            },
            {
                header: '재직상태',
                name: 'status',
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

    // 버튼에사원 조회 API 호출 기능 추가
    document.addEventListener("DOMContentLoaded", () => {
        const btn = document.querySelector(".empSrhBtn");
        if (btn) {
            btn.addEventListener("click",(e)=>{
                e.preventDefault();
                e.stopPropagation();
                getEmployees();
            });
        }
    });

    // 사원 목록 조회
    async function getEmployees() {

        // 사원 정보 추출
        const dept = document.querySelector("input[name='deptCode']").value;
        const position = document.querySelector("input[name='positionCode']").value;
        const name = document.querySelector("input[name='name']").value;
        const status = document.querySelector("input[name='isActive']").value;

        // params에 검색어 추가
        const data = new URLSearchParams({
            deptCode: dept,
            positionCode: position,
            name: name,
            isActive: status
        });

        try {
            //req API
            const res = await fetch(`/api/employee/`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });
            res.json().then(res => {
                employeeGrid.resetData(res.data); // grid에 세팅
            });
        } catch (e) {
            console.error(e);
        }
    }
}

window.onload = () => {
    init();
}