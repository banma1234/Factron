// grid 초기화
const initProcessGrid = () => {
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
        el: document.getElementById('srhProcessGrid'),
        scrollX: false,
        scrollY: true,
        bodyHeight: 80,
        columns: [
            {
                header: '공정번호',
                name: 'processId',
                align: 'center'
            },
            {
                header: '공정명',
                name: 'processName',
                align: 'center'
            },
            {
                header: '공정유형',
                name: 'processTypeName',
                align: 'center'
            },
            {
                header: '공정 시간',
                name: 'standardTime',
                align: 'center',
                formatter: (value) => {
                    return value.value ? `${value.value}분` : '';
                }
            },
            {
                header: '설비 유무',
                name: 'hasMachine',
                align: 'center',
                formatter: (value) => {
                    const upperValue = String(value.value).toUpperCase();
                    return `${upperValue==='Y' ? '보유' : '미보유'}`;
                }
            }
        ]
    });
}

const initProcess = () => {
    const srhProcessGrid = initProcessGrid();
    const srhProcessForm = document.querySelector(".srhProcessForm");

    // 검색
    srhProcessForm.querySelector(".processSrhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            srhProcessGrid.resetData(res); // grid에 세팅
        });
    }, false);

    // 엔터 시 검색
    srhProcessForm.addEventListener("submit", function(e) {
        e.preventDefault();
        e.stopPropagation();

        getData().then(res => {
            srhProcessGrid.resetData(res); // grid에 세팅
        });
    });

    // 공정 선택
    srhProcessGrid.on('click', (e) => {
        const rowKey = e.rowKey;
        const rowData = srhProcessGrid.getRow(rowKey);

        if (rowData && rowData.processId) {
            // 부모 폼에 데이터 세팅
            const processIdInput = document.querySelector("input[name='processId']");
            const processNameInput = document.querySelector("input[name='processName']");

            if (processIdInput) processIdInput.value = rowData.processId;
            if (processNameInput) processNameInput.value = rowData.processName;
        }
    });

    // 목록 조회
    async function getData() {
        const params = new URLSearchParams({
            hasMachine: "N",
            processIdOrName: srhProcessForm.querySelector("input[name='processIdOrName']")?.value || "",
            processTypeCode: srhProcessForm.querySelector("select[name='processTypeCode']")?.value || ""
        });

        try {
            const user = JSON.parse(localStorage.getItem("user") || "{}");
            const res = await fetch(`/api/process?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            if (!res.ok) {
                throw new Error("공정 조회 실패");
            }

            const data = await res.json();
            // 데이터가 배열인지 확인하고 변환
            return Array.isArray(data) ? data : (data.data || []);
        } catch (e) {
            console.error("공정 조회 오류:", e);
            return [];
        }
    }

// 초기 데이터 로드 부분
    getData().then(res => {
        // 항상 배열이 전달되도록 보장
        const dataArray = Array.isArray(res) ? res : [];
        srhProcessGrid.resetData(dataArray);
    });

    // 공정 유형 코드 세팅
    setSelectBox("PTP", "processTypeCode");
}

document.addEventListener("DOMContentLoaded", () => {
    initProcess();
});