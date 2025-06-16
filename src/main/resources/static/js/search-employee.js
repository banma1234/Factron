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

    // 부서 세팅
    getSysCodeList("DEP").then(res => {
        const selectElement = srhEmpForm.querySelector("select[name='srhDepCode']");

        // 하드코딩
        const data = [
            {
                "detailCode": "DEP001",
                "name": "인사부"
            },
            {
                "detailCode": "DEP002",
                "name": "개발부"
            },
            {
                "detailCode": "DEP003",
                "name": "영업부"
            },
            {
                "detailCode": "DEP004",
                "name": "생산부"
            }
        ];

        // for(const dept of res.data) {
        for(const dept of data) {
            const optionElement = document.createElement("option");
            optionElement.value = dept.detailCode;  // 코드
            optionElement.textContent = dept.name;  // 이름

            selectElement.appendChild(optionElement);
        }
    }).catch(e => {
        console.error(e);
    });

    // 직급 세팅
    getSysCodeList("POS").then(res => {
        const selectElement = srhEmpForm.querySelector("select[name='srhPosCode']");

        // 하드코딩
        const data = [
            {
                "detailCode": "POS001",
                "name": "사원"
            },
            {
                "detailCode": "POS002",
                "name": "주임"
            },
            {
                "detailCode": "POS003",
                "name": "대리"
            }
        ];

        // for(const pos of res.data) {
        for(const pos of data) {
            const optionElement = document.createElement("option");
            optionElement.value = pos.detailCode;  // 코드
            optionElement.textContent = pos.name;  // 이름

            selectElement.appendChild(optionElement);
        }
    }).catch(e => {
        console.error(e);
    });

    // 공통코드 목록 조회
    async function getSysCodeList(mainCode) {
        const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return res.json();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    initEmp();
});