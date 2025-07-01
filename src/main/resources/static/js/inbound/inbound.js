let rawInboundData = [];
let inboundGrid;

const init = () => {
    inboundGrid = initGrid(
        document.getElementById('inboundGrid'),
        400,
        [
            { header: '입고ID', name: 'inboundId', align: 'center' },
            {
                header: '제품/자재명', name: 'itemOrMaterialName', align: 'center',
                formatter: ({ row }) => row.itemName || row.materialName || ''
            },
            { header: '수량', name: 'quantity', align: 'center' },
            { header: '구분', name: 'categoryName', align: 'center' },
            { header: '창고명', name: 'storageName', align: 'center' },
            { header: '입고일자', name: 'inDate', align: 'center' },
            {
                header: '상태', name: 'statusCode', align: 'center',
                formatter: ({ value }) => {
                    if (value === 'STS001') return `<span style="color:green;">입고대기</span>`;
                    if (value === 'STS003') return `<span style="color:blue;">입고완료</span>`;
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
            const res = await fetch(`/api/inbound?${params.toString()}`);
            if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
            const data = await res.json();
            rawInboundData = data.data;
            inboundGrid.resetData(data.data);
        } catch (e) {
            console.error("입고 데이터 조회 오류:", e);
            alert("입고 데이터를 불러오는 중 문제가 발생했습니다.");
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

    // ID 추가 후 선택
    document.getElementById("btnCompleteInbound").addEventListener("click", async function () {
        console.log("입고 완료 버튼 클릭됨");
        const checkedRows = inboundGrid.getCheckedRows();
        console.log("체크된 행 수:", checkedRows.length);
        if (checkedRows.length === 0) {
            alert("처리할 입고 데이터를 선택하세요.");
            return;
        }

        // 상태 체크 : STS003 (입고완료)인게 하나라도 있으면 막기
        const hasCompleted = checkedRows.some(row => row.statusCode === 'STS003');
        if (hasCompleted) {
            alert("이미 입고 완료된 데이터가 포함되어 있습니다. 다시 확인해주세요.");
            return;
        }

        const inboundIds = checkedRows.map(row => row.inboundId);

        try {
            const res = await fetch('/api/inbound', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ inboundIds: inboundIds })
            });

            if (!res.ok) {
                const errorData = await res.json();
                alert(`입고 처리 실패: ${errorData.message || res.statusText}`);
                return;
            }

            alert("입고 처리가 완료되었습니다.");
            getData();
        } catch (e) {
            console.error("입고 처리 오류:", e);
            alert("입고 처리 중 오류가 발생했습니다.");
        }
    });


    getData();
};

window.onload = () => {
    init();
};
