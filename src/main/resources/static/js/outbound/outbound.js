let rawOutboundData = [];
let outboundGrid;

const init = () => {
    outboundGrid = initGrid(
        document.getElementById('outboundGrid'),
        400,
        [
            { header: '출고ID', name: 'outboundId', align: 'center' },
            {
                header: '제품/자재명', name: 'itemOrMaterialName', align: 'center',
                formatter: ({ row }) => row.itemName || row.materialName || ''
            },
            { header: '수량', name: 'quantity', align: 'center' },
            { header: '구분', name: 'categoryName', align: 'center' },
            { header: '창고명', name: 'storageName', hidden: true },
            { header: '출고일자', name: 'outDate', align: 'center' },
            {
                header: '상태', name: 'statusCode', align: 'center',
                formatter: ({ value }) => {
                    if (value === 'STS001') return `<span style="color:green;">출고대기</span>`;
                    if (value === 'STS003') return `<span style="color:blue;">출고완료</span>`;
                    return value;
                }
            }
        ],
        ['checkbox']
    );

    async function getData() {
        const srhItemOrMaterialName = document.querySelector("input[name='srhName']").value;
        const params = new URLSearchParams({
            srhItemOrMaterialName: srhItemOrMaterialName
        });

        try {
            const res = await fetch(`/api/outbound?${params.toString()}`);
            if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
            const data = await res.json();
            rawOutboundData = data.data;
            outboundGrid.resetData(data.data);
        } catch (e) {
            console.error("출고 데이터 조회 오류:", e);
            alert("출고 데이터를 불러오는 중 문제가 발생했습니다.");
        }
    }

    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault();
        getData();
    });

    document.querySelector(".search__form").addEventListener("submit", function (e) {
        e.preventDefault();
        getData();
    });

    document.getElementById("btnCompleteOutbound").addEventListener("click", async function () {
        console.log("출고 완료 버튼 클릭됨");
        const checkedRows = outboundGrid.getCheckedRows();
        console.log("체크된 행 수:", checkedRows.length);
        if (checkedRows.length === 0) {
            alert("처리할 출고 데이터를 선택하세요.");
            return;
        }

        const hasCompleted = checkedRows.some(row => row.statusCode === 'STS003');
        if (hasCompleted) {
            alert("이미 출고 완료된 데이터가 포함되어 있습니다. 다시 확인해주세요.");
            return;
        }

        const outboundIds = checkedRows.map(row => row.outboundId);

        try {
            const res = await fetch('/api/outbound', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ outboundIds: outboundIds })
            });

            if (!res.ok) {
                const errorData = await res.json();
                alert(`출고 처리 실패: ${errorData.message || res.statusText}`);
                return;
            }

            alert("출고 처리가 완료되었습니다.");
            getData();
        } catch (e) {
            console.error("출고 처리 오류:", e);
            alert("출고 처리 중 오류가 발생했습니다.");
        }
    });

    getData();
};

window.onload = () => {
    init();
};
