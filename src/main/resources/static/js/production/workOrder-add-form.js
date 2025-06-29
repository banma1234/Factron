// 작업수량 validation
function isValidPositiveInteger(value) {
    const num = Number(value);
    return Number.isInteger(num) && num >= 1; // 1 이상 정수
}

const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.saveBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    const today = getKoreaToday();
    let data = {}; // 저장 데이터

    // 초기 값 세팅
    form.querySelector("input[name='startDate']").setAttribute("min", today);

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        // 해당 생산계획의 미작업 제품 목록 세팅
        getWorkItemList(data.itemId, data.planId, data.quantity).then(res => {
            const selectTag = document.querySelector(`select[name='orderItem']`);

            res.data.forEach((prod) => {
                const optionElement = document.createElement("option");
                optionElement.value = prod.prodId;
                optionElement.textContent = `${prod.prodName} (${prod.prodId}) - [${prod.type}]`;

                selectTag.appendChild(optionElement);
            });
        });

        // 값 세팅
        form.querySelector("input[name='planId']").value = data.planId || "";
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
                formatter: ({ value, row }) => {
                    return value ? `${formatNumber(value)} ${row.unit ?? ''}` : '-';
                }
            },
            {
                header: '재고량',
                name: 'stockQty',
                align: 'center',
                formatter: ({ value, row }) => {
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
        ],
        ['checkbox']
    );

    document.querySelector(`select[name='orderItem']`).addEventListener("change", (e) => {
        // 작업 제품 선택 시 투입 품목 세팅
        getInputProdList(e.target.value).then(res => {
            productGrid.resetData(res.data);
        });
    });

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const planId = form.querySelector("input[name='planId']").value;
        const itemId = form.querySelector("select[name='orderItem']").value;
        const lineId = form.querySelector("select[name='line']").value;
        const startDate = form.querySelector("input[name='startDate']").value;
        const quantity = form.querySelector("input[name='quantity']").value.trim();

        // validation
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!planId) {
            alert("생산계획을 선택해주세요.");
            return;
        }
        if (!itemId) {
            alert("제품을 선택해주세요.");
            return;
        }
        if (quantity === '') {
            alert('작업수량을 입력해주세요.');
            return;
        }
        if (!isValidPositiveInteger(quantity)) {
            alert('작업수량은 1 이상의 정수만 입력 가능합니다.');
            return;
        }
        if (!startDate) {
            alert("시작 날짜를 입력해주세요.");
            return;
        }
        if (!dateRegex.test(startDate)) {
            alert("날짜 형식이 올바르지 않습니다.");
            return;
        }
        if (!lineId) {
            alert("라인을 선택해주세요.");
            return;
        }
        // 제품 선택 시 bom에 맞춰 자재/반제품 자동 세팅
        // 작업량에 따라 투입 품목 소요량 변경
        // 작업자를 선택해주세요

        // fetch data
        data = {
            planId,
            itemId,
            lineId,
            empId: user.id,
            startDate,
            quantity,
        };

        confirmModal.show();
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // confirm 모달 확인 버튼
    confirmEditBtn.addEventListener("click", () => {
        saveData().then(res => {
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
            window.opener.getWorkOrders();
        }

        window.close();
    });

    // 저장
    async function saveData() {
        try {
            const res = await fetch(`/api/workorder`, {
                method: "POST",
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

    // 작업지시 내릴 수 있는 제품 목록 조회
    async function getWorkItemList(parentItemId, planId, quantity) {
        // fetch data
        const data = new URLSearchParams({
            parentItemId,
            planId,
            quantity
        });

        try {
            const res = await fetch(`/api/workorder/items?${data.toString()}`, {
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

    // 라인 목록 조회
    async function getLineList() {
        try {
            const res = await fetch(`/api/line`, {
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

    // 작업 제품 정보 및 투입 품목 목록 조회
    async function getInputProdList(parentItemId) {

        try {
            const res = await fetch(`/api/workorder/inputs?parentItemId=${parentItemId}`, {
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

    // 작업 가능한 사원 목록 조회
    async function getPossibleWorkerList() {
        try {
            const res = await fetch(`/api/worker`, {
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

    // 페이지 진입 시 라인 및 작업자 세팅
    getLineList().then(res => {
        const selectTag = document.querySelector(`select[name='line']`);

        res.data.forEach((line) => {
            const optionElement = document.createElement("option");
            optionElement.value = line.lineId;
            optionElement.textContent = line.lineName;

            selectTag.appendChild(optionElement);
        });
    });
    getPossibleWorkerList().then(res => {
        workerGrid.resetData(res.data);
    });
};

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};