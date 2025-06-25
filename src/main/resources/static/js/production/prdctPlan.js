const init = () => {
    // grid 세팅
    const prdctPlanGrid = initGrid(
        document.getElementById('prdctPlanGrid'),
        400,
        [
            {
                header: '생산계획 번호',
                name: 'id',
                align: 'center'
            },
            {
                header: '제품코드',
                name: 'itemId',
                align: 'center'
            },
            {
                header: '제품명',
                name: 'itemName',
                align: 'center'
            },
            {
                header: '생산수량',
                name: 'quantity',
                align: 'center',
                formatter: ({ value, row }) => {
                    return value ? `${formatNumber(value)} ${row.unit ?? ''}` : '';
                }
            },
            {
                header: '시작일',
                name: 'startDate',
                align: 'center'
            },
            {
                header: '종료일',
                name: 'endDate',
                align: 'center'
            },
            {
                header: '담당자 사번',
                name: 'empId',
                align: 'center'
            },
            {
                header: '담당자명',
                name: 'empName',
                align: 'center'
            },
        ]
    );

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData();
    }, false);

    // 엔터 시 검색
    document.querySelector('.search__form').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출(새로고침) 방지

        // 조회
        getData();
    });

    // 목록 조회
    window.getData = async function () {

        // fetch data
        const params = new URLSearchParams({
            srhId: document.querySelector("input[name='srhId']").value,
            srhItemIdOrName: document.querySelector("input[name='srhItemIdOrName']").value,
            srhEmpIdOrName: document.querySelector("input[name='srhEmpIdOrName']").value,
        });

        try {
            const res = await fetch(`/api/production?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            // 응답이 정상적이지 않은 경우 오류 발생
            if (!res.ok) {
                throw new Error(`서버 오류: ${res.status}`);
            }

            const data = await res.json();
            prdctPlanGrid.resetData(data.data); // grid에 세팅

        } catch (e) {
            console.error(e);
        }
    }

    // 생산계획 등록 팝업 오픈
    document.querySelector(".registPrdctPlan")?.addEventListener("click", function(e) {
        window.open('/production/save', '_blank', 'width=800,height=450');
    });

    // 페이지 진입 시 바로 리스트 호출
    getData();
}

window.onload = () => {
    init();
}