// 작업량 validation
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
            const products = res.data;

            products.forEach((prod) => {
                // 완제품 생산수량에 따른 작업량 계산
                let quantity = 1;
                prod.quantity.split('*').map(qty => {
                    quantity = quantity*qty;
                });
                prod.quantity = quantity;
                // select box 세팅
                const optionElement = document.createElement("option");
                optionElement.value = prod.prodId;
                optionElement.textContent = `${prod.prodName} (${prod.prodId}) - [${prod.type}]`;

                selectTag.appendChild(optionElement);
            });

            // 해당 제품 선택 시 작업량, 재고량, 단위, 투입 품목 세팅
            document.querySelector(`select[name='orderItem']`).addEventListener("change", (e) => {
                const selectedProd = products.find(prod => prod.prodId === e.target.value);

                if (selectedProd) {
                    getInputProdList(selectedProd.prodId).then(res => {
                        // 원본 소요량 baseQuantity로 저장
                        const baseQtyData = res.data.map(row => ({
                            ...row,
                            baseQuantity: row.quantity,
                            quantity: row.quantity * selectedProd.quantity // 소요량 * 작업수량
                        }));

                        form.querySelector("input[name='quantity']").value = selectedProd.quantity;
                        form.querySelector("input[name='stockQty']").value = selectedProd.stockQty;
                        form.querySelector("input[name='unit']").value = selectedProd.unit;
                        form.querySelector("input[name='quantity']").focus();
                        productGrid.resetData(baseQtyData);
                    });
                } else {
                    form.querySelector("input[name='quantity']").value = '';
                    form.querySelector("input[name='stockQty']").value = '';
                    form.querySelector("input[name='unit']").value = '';
                    productGrid.resetData([]);
                }
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

    // 작업량 변경 시 투입 품목 소요량 변경
    form.querySelector("input[name='quantity']").addEventListener("input", (e) => {
        const newQty = Number(e.target.value);

        if (!isValidPositiveInteger(newQty)) return;

        const currentData = productGrid.getData();
        currentData.forEach((row, index) => {
            productGrid.setValue(index, 'quantity', row.baseQuantity * newQty);
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
            alert('작업량을 입력해주세요.');
            return;
        }
        if (!isValidPositiveInteger(quantity)) {
            alert('작업량은 1 이상의 정수만 입력 가능합니다.');
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
        const selectedWorkers = workerGrid.getCheckedRows();
        if (selectedWorkers.length === 0) {
            alert("작업자를 한 명 이상 선택해주세요.");
            return;
        }

        // fetch data
        data = {
            planId,
            itemId,
            lineId,
            empId: user.id,
            startDate,
            quantity,
            workers: selectedWorkers.map(w => w.employeeId),
            inputProds: productGrid.getData()
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
            window.opener.postMessage({ type: "ADD_REFRESH_WORKORDERS" }, "*");
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
    async function getWorkItemList(parentItemId, planId, planQty) {
        // fetch data
        const data = new URLSearchParams({
            parentItemId,
            planId,
            planQty
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
            const res = await fetch(`/api/workorder/worker`, {
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