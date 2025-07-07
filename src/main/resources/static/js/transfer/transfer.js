const init = () => {
    const transferGrid = initGrid(
        document.getElementById('transferGrid'),
        400,
        [
            {
                header: '발령구분',
                name: 'trsTypeName',
                align: 'center'
            },
            {
                header: '발령일자',
                name: 'transferDate',
                align: 'center'
            },
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
                header: '직급',
                name: 'positionName',
                align: 'center'
            },
            {
                header: '전 부서',
                name: 'prevDeptName',
                align: 'center'
            },
            {
                header: '현 부서',
                name: 'currDeptName',
                align: 'center'
            },
        ]
    );

    // 검색
    document.querySelector(".srhBtn").addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        // 조회
        getData().then(res => {
            transferGrid.resetData(res.data); // grid에 세팅
        });
    }, false);

    // 엔터 시 검색
    document.querySelector('.search__form').addEventListener('submit', function(e) {
        e.preventDefault(); // 폼 제출(새로고침) 방지

        // 조회
        getData().then(res => {
            transferGrid.resetData(res.data);
        });
    });

    // 목록 조회
    async function getData() {
        // validation
        const startDate = document.querySelector("input[name='srhStrDate']").value;
        const endDate = document.querySelector("input[name='srhEndDate']").value;
        if ((startDate && !endDate) || (!startDate && endDate)) {
            alert("시작 및 종료 날짜를 모두 입력해주세요.");
            return { data: [] };
        }

        if (startDate && endDate) {
            const dateRegex = /^\d{4}-\d{2}-\d{2}$/;

            if (!dateRegex.test(startDate) || isNaN(Date.parse(startDate))
                || !dateRegex.test(endDate) || isNaN(Date.parse(endDate))) {
                alert("날짜 형식이 올바르지 않습니다.");
                return { data: [] };
            }
            if (new Date(startDate) > new Date(endDate)) {
                alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
                return { data: [] };
            }
        }

        // fetch data
        const data = new URLSearchParams({
            srhIdOrName: document.querySelector("input[name='srhIdOrName']").value,
            srhStrDate: startDate,
            srhEndDate: endDate,
            srhTrsTypeCode: document.querySelector("select[name='srhTrsTypeCode']").value,
        });

        try {
            const res = await fetch(`/api/trans?${data.toString()}`, {
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

    // 발령 팝업 오픈
    document.querySelector(".registTrans")?.addEventListener("click", function(e) {
        const popup = window.open('/trans/save', '_blank', 'width=800,height=750');

        // 자식 창으로부터 'ready' 먼저 수신 후 postMessage 실행
        const messageHandler = (event) => {
            if (event.data === 'ready') {
                popup.postMessage({}, "*");
                window.removeEventListener("message", messageHandler);
            }
        };
        window.addEventListener("message", messageHandler);
    });

    // 공통코드 세팅
    setSelectBox("TRS", "srhTrsTypeCode");

    // 페이지 진입 시 바로 리스트 호출
    getData().then(res => {
        transferGrid.resetData(res.data); // grid에 세팅
    });
}

window.onload = () => {
    init();
}