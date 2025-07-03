let rawInboundData = [];
let inboundGrid;

const init = () => {
    const isAuthorized = user.authCode === 'ATH006';

    // 체크박스 옵션: 권한 있을 때만 추가
    const gridOptions = isAuthorized ? ['checkbox'] : [];

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
        gridOptions
    );

    const btn = document.querySelector(".btnCompleteInbound");

    // 권한 없으면 숨김 처리
    if (!isAuthorized) {
        if (btn) btn.style.display = 'none';
    }

    // 기본 날짜 셋팅: 오늘, 30일 전
    const today = new Date().toISOString().split('T')[0];
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 30);
    const startDateDefault = pastDate.toISOString().split('T')[0];

    // 날짜 필드는 form 안에 이미 있으므로 덮어쓰지 않음
    const startDateInput = document.querySelector("input[name='startDate']");
    const endDateInput = document.querySelector("input[name='endDate']");
    if (startDateInput) startDateInput.value = startDateDefault;
    if (endDateInput) endDateInput.value = today;

    async function getData() {
        const srhItemOrMaterialName = document.querySelector("input[name='srhName']").value;
        const startDate = startDateInput ? startDateInput.value : '';
        const endDate = endDateInput ? endDateInput.value : '';

        const params = new URLSearchParams({
            srhItemOrMaterialName,
            startDate,
            endDate
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

    document.querySelector(".srhBtn").addEventListener("click", e => {
        e.preventDefault();
        getData();
    });

    document.querySelector(".search__form").addEventListener("submit", e => {
        e.preventDefault();
        getData();
    });

    // 권한 있으면 버튼 이벤트 등록
    if (isAuthorized) {
        if (btn) {
            btn.addEventListener("click", async () => {
                const checkedRows = inboundGrid.getCheckedRows();
                if (checkedRows.length === 0) {
                    alert("처리할 입고 데이터를 선택하세요.");
                    return;
                }

                if (checkedRows.some(row => row.statusCode === 'STS003')) {
                    alert("이미 입고 완료된 데이터가 포함되어 있습니다. 다시 확인해주세요.");
                    return;
                }

                const inboundIds = checkedRows.map(row => row.inboundId);

                try {
                    const res = await fetch('/api/inbound', {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ inboundIds })
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
        }
    }

    getData();
};

window.onload = () => {
    init();
};
