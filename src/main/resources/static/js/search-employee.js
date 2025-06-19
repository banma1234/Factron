// grid 초기화
const initEmpGrid = () => {
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
        el: document.getElementById('srhEmpGrid'),
        scrollX: false,
        scrollY: true,
        bodyHeight: 80,
        columns: [
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
        ]
    });
}

const initEmp = () => {
    const srhEmpGrid = initEmpGrid();
    const srhEmpForm = document.querySelector(".srhEmpForm");

    // 검색
    srhEmpForm.querySelector(".srhEmpBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            srhEmpGrid.resetData(res.data); // grid에 세팅
        });
    }, false);

    // 엔터 시 검색
    srhEmpForm.addEventListener("submit", function(e) {
        e.preventDefault();

        getData().then(res => {
            srhEmpGrid.resetData(res.data); // grid에 세팅
        });
    });

    // 사원 선택
    srhEmpGrid.on('dblclick', (e) => {
        const rowKey = e.rowKey;
        const rowData = srhEmpGrid.getRow(rowKey);

        if (rowData && rowData.empId) {
            // 부모 폼에 데이터 세팅
            document.querySelector("input[name='empId']").value = rowData.empId;
            document.querySelector("input[name='empName']").value = rowData.empName;
            document.querySelector("select[name='deptCode']").value = rowData.deptCode || "";
            document.querySelector("select[name='positionCode']").value = rowData.positionCode || "";
        }
    });

    // 목록 조회
    async function getData() {
        // fetch data
        const data = new URLSearchParams({
            nameOrId: srhEmpForm.querySelector("input[name='srhIdOrName']").value,
            dept: srhEmpForm.querySelector("select[name='srhDepCode']").value,
            position: srhEmpForm.querySelector("select[name='srhPosCode']").value,
        });

        try {
            const res = await fetch(`/api/employee?${data.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }

    // 공통코드 세팅
    setSelectBox("DEP", "srhDepCode");
    setSelectBox("POS", "srhPosCode");
}

document.addEventListener("DOMContentLoaded", () => {
    initEmp();
});