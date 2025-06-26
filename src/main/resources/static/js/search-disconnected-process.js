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
        el: document.getElementById('srhDisconnectedProcessGrid'),
        scrollX: false,
        scrollY: true,
        bodyHeight: 250,
        rowHeaders: ['checkbox'], // 체크박스 추가
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

const initDisconnectedProcess = () => {
    const srhProcessGrid = initProcessGrid();
    const srhProcessForm = document.querySelector(".srhDisconnectedProcessForm");

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

    // 목록 조회
    async function getData() {
        const params = new URLSearchParams({
            hasMachine: "Y",
            lineConnected: "false",
            processIdOrName: srhProcessForm.querySelector("input[name='processIdOrName']")?.value || "",
            processTypeCode: srhProcessForm.querySelector("select[name='processTypeCode']")?.value || ""
        });

        try {
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
            console.error("라인 미연결 공정 조회 오류:", e);
            return [];
        }
    }

    // 선택된 공정 목록 가져오기
    window.getSelectedProcesses = () => {
        const checkedRowKeys = srhProcessGrid.getCheckedRowKeys();
        return checkedRowKeys.map(rowKey => srhProcessGrid.getRow(rowKey));
    };

    // 초기 데이터 로드
    getData().then(res => {
        // 항상 배열이 전달되도록 보장
        const dataArray = Array.isArray(res) ? res : [];
        srhProcessGrid.resetData(dataArray);
    });

    // 공정 유형 코드 세팅
    setSelectBox("PTP", "processTypeCode");
}

document.addEventListener("DOMContentLoaded", () => {
    initDisconnectedProcess();
});