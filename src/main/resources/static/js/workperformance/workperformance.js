// 버튼 정의
function CustomButtonRenderer(props) {
    const el = document.createElement('div');

    if (props.value === 'WKS002') {
        const button = document.createElement('button');
        button.textContent = '실적 등록';
        button.className = 'btn btn-warning btn-sm';
        button.addEventListener('click', () => registerWorkPerformance(props.rowKey));
        el.appendChild(button);
    } else if (props.value === 'WKS003') {
        el.textContent = '완료';
    } else {
        el.textContent = '';
    }

    this.el = el;
}
// 버튼 렌더링
CustomButtonRenderer.prototype.getElement = function () {
    return this.el;
};

// 데이터 조회
async function fetchWorkPerformanceData() {
    const workOrderId = document.querySelector("input[name='searchWorkOrderNo']").value;

    const params = new URLSearchParams({
            workOrderId:workOrderId
    });

    try {
        const res = await fetch(`/api/performance?${params.toString()}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });

        return res.json();
    } catch (err) {
        console.error("fetch error", err);
        return { data: [] };
    }
}

// 실적 저장 (검사 시작)
async function registerWorkPerformance(rowKey) {
    const rowData = performanceGrid.getRow(rowKey);
    const endDate = new Date(rowData.endDate);
    endDate.setHours(23, 59, 59, 59);
    const lastProcessStartTime = new Date(rowData.lastProcessStartTime);

    if (!rowData) {
        alert('데이터를 찾을 수 없습니다.');
        return;
    }
    if (!rowData.lastProcessStartTime || rowData.lastProcessStartTime.trim() === '') {
        alert('공정이 완료된 후 실적 등록이 가능합니다.');
        return;
    }
    if (!rowData.endDate || rowData.endDate.trim() === '') {
        alert('종료일을 작성해주세요.');
        return;
    }
    if (isNaN(endDate.getTime())) {
        alert('종료일이 유효하지 않습니다.');
        return;
    }
    if (endDate <= lastProcessStartTime) {
        alert('종료일은 마지막 공정 이후여야 합니다.');
        return;
    }

    // POST 요청
    try {
        const res = await fetch('/api/performance', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                workOrderId: rowData.workOrderId,
                fectiveQuantity: rowData.fectiveQuantity || 0,
                defectiveQuantity: rowData.defectiveQuantity || 0,
                endDate: rowData.endDate,
                employeeId : user.id,
                statusCode:rowData.statusCode,
                lastProcessStartTime:rowData.lastProcessStartTime
            })
        });
        alert(`실적 등록이 완료 되었습니다.`);
        refreshGrid();
        return res.json();
    } catch (err) {
        console.error('실적 등록 중 오류 발생:', err);
    }
}

// 새로고침
async function refreshGrid() {
    const res = await fetchWorkPerformanceData();

    // 불러온 데이터에서 불량품 계산
    res.data.forEach(row => {
        if (row.fectiveQuantity !== null && row.fectiveQuantity !== undefined) { // 양품 데이터가 있을 때만 계산
            row.defectiveQuantity = (row.quantity || 0) - (parseInt(row.fectiveQuantity) || 0);
            if (row.defectiveQuantity < 0) row.defectiveQuantity = 0;
        }
    });

    performanceGrid.resetData(res.data);
}


// 페이지 초기화
let performanceGrid;
const init = async () => {
// 그리드 초기화
    performanceGrid = initGrid(
        document.querySelector('.performanceGrid'),
        400,
        [
            { header: '작업지시 번호', name: 'workOrderId', align: 'center' },
            { header: '제품코드', name: 'itemId', align: 'center' },
            { header: '제품명', name: 'itemName', align: 'center' },
            { header: '시작일', name: 'startDate', align: 'center'},
            { header: '작업수량', name: 'quantity', align: 'center' },
            { header: '양품', name: 'fectiveQuantity', align: 'center' },
            { header: '불량품', name: 'defectiveQuantity', align: 'center' },
            { header: '단위', name: 'unitName', align: 'center' },
            { header: '종료일', name: 'endDate', align: 'center', editor: {type: 'datePicker', options: { format: 'yyyy-MM-dd' }}},
            { header: '상태', name: 'statusCode', align: 'center', renderer: {type: CustomButtonRenderer} },
            { header: '마지막공정시작', name: 'lastProcessStartTime', hidden: true }
        ]
    );

    performanceGrid.on('afterChange', (ev) => {
        if (ev.changes) {
            ev.changes.forEach(change => {
                const { rowKey, columnName, value } = change;
                const rowData = performanceGrid.getRow(rowKey);

                // 양품이 있으면 불량품 자동 계산
                if (columnName === 'fectiveQuantity') {
                    const defectiveQty = (rowData.quantity || 0) - (parseInt(value) || 0);
                    performanceGrid.setValue(rowKey, 'defectiveQuantity', defectiveQty < 0 ? 0 : defectiveQty);
                }
            });
        }
    });

    // 검색 버튼
    document.querySelector(".searchBtn").addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        refreshGrid();
    }, false);

    // 엔터키 검색
    document.querySelector("input[name='searchWorkOrderNo']").addEventListener("keyup", function (e) {
        if (e.key === 'Enter') {
            refreshGrid();
        }
    });

    //페이지 최초 그리드
    refreshGrid();

};

// 페이지 로드시 init 실행
window.onload = () => {
    init();
};
