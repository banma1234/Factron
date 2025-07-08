let rawOutboundData = [];
let outboundGrid;

const init = () => {
    const isAuthorized = user.authCode === 'ATH006' || user.authCode === 'ATH003'; // 출고 처리 권한 여부 확인

    // 권한이 있을 때만 체크박스 옵션 활성화
    const gridOptions = isAuthorized ? ['checkbox'] : [];

    // 출고 그리드 초기화
    outboundGrid = initGrid(
        document.getElementById('outboundGrid'),
        400,
        [
            { header: '출고ID', name: 'outboundId', align: 'center' },
            {
                header: '제품/자재명', name: 'itemOrMaterialName', align: 'center',
                formatter: ({ row }) => row.itemName || row.materialName || ''
            },
            { header: '수량', name: 'quantity', align: 'center', formatter: ({ value }) => window.formatNumber(value) },
            { header: '구분', name: 'categoryName', align: 'center' },
            { header: '창고명', name: 'storageName', align: 'center' },
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
        gridOptions
    );

    const btn = document.querySelector(".btnCompleteOutbound");

    // 권한 없으면 출고 완료 버튼 숨김 처리
    if (!isAuthorized && btn) {
        btn.style.display = 'none';
    }

    // 권한 있으면 출고 완료 버튼에 클릭 이벤트 등록
    if (isAuthorized && btn) {
        btn.addEventListener("click", async () => {
            const checkedRows = outboundGrid.getCheckedRows();

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
                    body: JSON.stringify({ outboundIds })
                });

                const responseData = await res.json();

                if (responseData.status !== 200) {
                    // LOT 부족 같은 오류 메시지 그대로 alert
                    alert(`출고 처리 실패: ${responseData.message}`);
                    return;
                }

                alert("출고 처리가 완료되었습니다.");
                getData();
            } catch (e) {
                console.error("출고 처리 오류:", e);
                alert("출고 처리 중 오류가 발생했습니다.");
            }
        });
    }

    // 출고 데이터 조회 함수
    async function getData() {
        const srhItemOrItemName = document.querySelector("input[name='srhName']").value;
        const startDate = document.querySelector("input[name='startDate']").value;
        const endDate = document.querySelector("input[name='endDate']").value;

        const params = new URLSearchParams({
            srhItemOrItemName,
            startDate,
            endDate
        });

        try {
            const res = await fetch(`/api/outbound?${params.toString()}`);
            const responseData = await res.json();

            if (responseData.status !== 200) {
                console.warn("출고 데이터 조회 실패:", responseData.message);
                alert("출고 데이터를 불러오는 중 문제가 발생했습니다.");
                return;
            }

            rawOutboundData = responseData.data;
            outboundGrid.resetData(responseData.data);
        } catch (e) {
            console.error("출고 데이터 조회 오류:", e);
            alert("출고 데이터를 불러오는 중 문제가 발생했습니다.");
        }
    }

    // 검색 버튼 클릭 시 데이터 조회
    document.querySelector(".srhBtn").addEventListener("click", function (e) {
        e.preventDefault();
        getData();
    });

    // 검색 폼 제출 시 데이터 조회
    document.querySelector(".search__form").addEventListener("submit", function (e) {
        e.preventDefault();
        getData();
    });

    // 기본 날짜: 오늘과 30일 전으로 초기화
    const today = getKoreaToday();
    const pastDate = new Date(today);
    pastDate.setDate(pastDate.getDate() - 30);

    const startDateDefault = pastDate.toISOString().split('T')[0];
    const endDateDefault = today;

    const startDateInput = document.querySelector("input[name='startDate']");
    const endDateInput = document.querySelector("input[name='endDate']");

    if (startDateInput) startDateInput.value = startDateDefault;
    if (endDateInput) endDateInput.value = endDateDefault;

    getData(); // 초기 데이터 조회
};

window.onload = () => {
    init();
};
