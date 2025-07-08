// 생산수량 validation
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
    form.querySelector("input[name='endDate']").setAttribute("min", today);

    // 완제품 목록 세팅
    getItemList().then(res => {
        const selectTag = document.querySelector(`select[name='item']`);
        const products = res.data;

        products.forEach((prod) => {
            const optionElement = document.createElement("option");
            optionElement.value = prod.itemId;
            optionElement.textContent = `${prod.name} (${prod.itemId})`;

            selectTag.appendChild(optionElement);
        });

        // 해당 제품 선택 시 단위 세팅
        form.querySelector(`select[name='item']`).addEventListener("change", (e) => {
            const selectedProd = products.find(prod => prod.itemId === e.target.value);

            if (selectedProd) {
                form.querySelector("input[name='unitName']").value = selectedProd.unitName;
                form.querySelector("input[name='quantity']").focus();
            }
        });
    });

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const itemId = form.querySelector("select[name='item']").value;
        const startDate = form.querySelector("input[name='startDate']").value;
        const endDate = form.querySelector("input[name='endDate']").value;
        const quantity = form.querySelector("input[name='quantity']").value.trim();

        // validation
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!itemId) {
            alert("제품을 선택해주세요.");
            return;
        }
        if (quantity === '') {
            alert('생산수량을 입력해주세요.');
            return;
        }
        if (!isValidPositiveInteger(quantity)) {
            alert('생산수량은 1 이상의 정수만 입력 가능합니다.');
            return;
        }
        if (!startDate || !endDate) {
            alert("시작 날짜와 종료 날짜를 모두 입력해주세요.");
            return;
        }
        if (!dateRegex.test(startDate) || !dateRegex.test(endDate)) {
            alert("날짜 형식이 올바르지 않습니다.");
            return;
        }
        if (startDate > endDate) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        // fetch data
        data = {
            itemId,
            empId: user.id,
            startDate,
            endDate,
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
            window.opener.getData();
        }

        window.close();
    });

    // 완제품 목록 조회
    async function getItemList() {
        const type = 'ITP003';

        try {
            const res = await fetch(`/api/item?itemTypeCode=${type}`, {
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

    // 저장
    async function saveData() {
        try {
            const res = await fetch(`/api/production`, {
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
};

window.onload = () => {
    init();
};