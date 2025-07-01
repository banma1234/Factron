const init = () => {
    const form = document.querySelector("form");
    const startBtn = form.querySelector("button.startBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let data = {}; // 저장 데이터

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function (event) {
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        // 값 세팅
        form.querySelector("input[name='planId']").value = data.planId || "";
        form.querySelector("input[name='orderId']").value = data.orderId || "";
        form.querySelector("input[name='orderItem']").value = `${data.itemName} (${data.itemId})` || "";
        form.querySelector("input[name='quantity']").value = data.quantity || "";
        form.querySelector("input[name='unitName']").value = data.unit || "";
        form.querySelector("input[name='lineId']").value = data.lineId || "";
        form.querySelector("input[name='lineName']").value = data.lineName || "";
        form.querySelector("input[name='startDate']").value = data.startDate || "";

        // 해당 작업지시의 투입 품목과 작업자 로드
        getWorkDetailData(data.orderId).then(res => {

        });
    });

    // 투입 품목 grid
    const productGrid = initGrid(
        document.getElementById('productGrid'),
        120,
        [
            {
                header: '품목코드',
                name: 'prodId',
                align: 'center'
            },
            {
                header: '품목명',
                name: 'prodName',
                align: 'center'
            },
            {
                header: '품목구분',
                name: 'type',
                align: 'center'
            },
            {
                header: '소요량',
                name: 'quantity',
                align: 'center',
                formatter: ({value, row}) => {
                    return value ? `${formatNumber(value)} ${row.unit ?? ''}` : '-';
                }
            },
        ]
    );

    // 작업자 grid
    const workerGrid = initGrid(
        document.getElementById('workerGrid'),
        120,
        [
            {
                header: '사원번호',
                name: 'employeeId',
                align: 'center'
            },
            {
                header: '이름',
                name: 'employeeName',
                align: 'center'
            },
            {
                header: '부서',
                name: 'deptName',
                align: 'center'
            },
            {
                header: '직급',
                name: 'posName',
                align: 'center'
            },
        ]
    );

    // 시작 버튼
    startBtn.addEventListener("click", () => {

        // fetch data
        data = {
            orderId: form.querySelector("input[name='orderId']").value,
            lineId: form.querySelector("input[name='lineId']").value,
        };

        confirmModal.show();
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // confirm 모달 확인 버튼
    confirmEditBtn.addEventListener("click", () => {
        startWork().then(res => {
            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();
        });
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모 창의 그리드 리프레시
        if (window.opener && !window.opener.closed) {
            window.opener.postMessage({type: "STR_REFRESH_WORKORDERS"}, "*");
        }

        window.close();
    });

    // 작업 시작
    async function startWork() {
        try {
            const res = await fetch(`/api/workorder`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }

    // 투입된 품목 및 작업자 목록 조회
    async function getWorkDetailData(orderId) {
        try {
            const res = await fetch(`/api/workDtl?orderId=${orderId}`, {
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
}

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};