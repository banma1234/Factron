const init = () => {
    const form = document.querySelector("form");
    // Bootstrap 모달 인스턴스 생성 (확인, 알림)
    const confirmModal = new bootstrap.Modal(document.querySelector(".confirmModal"));
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));

    let materialGrid;
    let selectedItems = [];  // 선택된 자재 목록 저장

    // 폼 제출 기본 동작 막기 (페이지 리로드 방지)
    form.addEventListener("submit", e => e.preventDefault());

    // 자재 그리드 초기화
    materialGrid = initGrid(
        document.getElementById("materialGrid"),
        300,
        [
            { header: "자재 ID", name: "materialId", align: "center" },
            { header: "자재명", name: "name", align: "center" },
            { header: "단위", name: "unitName", align: "center" },
            { header: "규격", name: "spec", align: "center" }
        ]
    );

    // 거래처 및 자재 데이터 불러오기
    loadClients();
    loadMaterials();

    // 부모 창에 준비 완료 메시지 전송
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }

    // 거래처 목록 조회 함수
    async function loadClients() {
        try {
            const res = await fetch('/api/client');
            const json = await res.json();
            const clients = json.data || [];
            const select = form.querySelector("select.clientId");
            clients.forEach(client => {
                const option = document.createElement('option');
                option.value = client.id;
                option.textContent = `${client.name} (${client.business_number ?? ''})`;
                select.appendChild(option);
            });
        } catch (e) {
            console.error("거래처 조회 실패", e);
            alert("거래처 목록을 불러오는데 실패했습니다.");
        }
    }

    // 자재 목록 조회 함수 (키워드 필터 포함)
    async function loadMaterials(keyword = "") {
        try {
            const res = await fetch(`/api/material?materialName=${encodeURIComponent(keyword)}`);
            const json = await res.json();
            materialGrid.resetData(json.data || []);
        } catch (e) {
            console.error("자재 조회 실패", e);
            alert("자재 목록 조회에 실패했습니다.");
        }
    }

    // 자재 검색 input에서 Enter 입력 시 조회
    form.querySelector("input[name='materialSearch']").addEventListener("keyup", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            loadMaterials(e.target.value.trim());
        }
    });

    // 자재 검색 버튼 클릭 시 조회
    form.querySelector(".materialSearchBtn").addEventListener("click", (e) => {
        e.preventDefault();
        loadMaterials(form.querySelector("input[name='materialSearch']").value.trim());
    });

    // 자재 그리드 더블 클릭 시 선택 목록에 추가
    materialGrid.on("dblclick", ev => {
        if (ev.rowKey != null) {
            const row = materialGrid.getRow(ev.rowKey);
            addItemToPurchaseList(row);
        }
    });

    // 선택한 자재를 발주 목록에 추가 (중복 체크 포함)
    function addItemToPurchaseList(item) {
        if (selectedItems.find(i => i.materialId === item.materialId)) {
            alert("이미 추가된 자재입니다.");
            return;
        }
        selectedItems.push({ ...item, quantity: 1, price: 1000 }); // 기본 수량 1, 가격 1000원 설정
        renderSelectedItems();
    }

    // 선택된 자재 목록을 화면에 렌더링
    function renderSelectedItems() {
        const container = document.querySelector(".purchase-items");
        container.innerHTML = "";

        selectedItems.forEach((item, index) => {
            const itemTotal = item.quantity * item.price;

            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between align-items-center";

            div.innerHTML = `
                <div class="d-flex align-items-center gap-2 flex-grow-1">
                    <span>${item.name} × </span>
                    <input type="number" min="1" value="${item.quantity}" class="form-control form-control-sm qty-input" style="width:60px;" /> ${item.unitName}
                    <input type="number" min="0" value="${item.price}" class="form-control form-control-sm price-input" style="width:80px;" /> 원 
                    = <span class="item-total">₩${itemTotal.toLocaleString()}</span>
                </div>
                <button type="button" class="btn btn-sm btn-outline-danger remove-btn">X</button>
            `;

            const qtyInput = div.querySelector(".qty-input");
            const priceInput = div.querySelector(".price-input");
            const itemTotalSpan = div.querySelector(".item-total");

            // 수량 변경 시 처리
            qtyInput.addEventListener("input", (e) => {
                let val = parseInt(e.target.value);
                if (isNaN(val) || val < 1) val = 1;
                selectedItems[index].quantity = val;
                e.target.value = val;
                itemTotalSpan.textContent = `₩${(val * selectedItems[index].price).toLocaleString()}`;
                updateTotalAmount();
            });

            // 가격 변경 시 처리
            priceInput.addEventListener("input", (e) => {
                let val = parseInt(e.target.value);
                if (isNaN(val) || val < 0) val = 0;
                selectedItems[index].price = val;
                e.target.value = val;
                itemTotalSpan.textContent = `₩${(selectedItems[index].quantity * val).toLocaleString()}`;
                updateTotalAmount();
            });

            // 선택 삭제 버튼 클릭 시 항목 제거
            div.querySelector(".remove-btn").addEventListener("click", () => {
                selectedItems.splice(index, 1);
                renderSelectedItems();
            });

            container.appendChild(div);
        });

        updateTotalAmount();
    }

    // 총 금액 계산 및 표시
    function updateTotalAmount() {
        const total = selectedItems.reduce((sum, item) => sum + (item.quantity * item.price), 0);
        document.querySelector("span.totalAmount").textContent = `₩${total.toLocaleString()}`;
    }

    // 저장 버튼 클릭 시 유효성 검사 및 확인 모달 띄우기
    document.querySelector(".saveBtn").addEventListener("click", () => {
        const clientId = form.querySelector("select.clientId").value;
        if (!clientId) return alert("거래처를 선택해주세요.");
        if (!selectedItems.length) return alert("발주 자재를 선택해주세요.");
        confirmModal.show();
    });

    // 확인 모달에서 등록 버튼 클릭 시 발주 데이터 전송
    document.querySelector(".confirmRegisterBtn").addEventListener("click", async () => {
        confirmModal.hide();
        const clientId = form.querySelector("select.clientId").value;

        // 전송할 페이로드 구성
        const payload = {
            clientId,
            employeeId: user.id || null,
            items: selectedItems.map(item => ({
                materialId: item.materialId,
                quantity: item.quantity,
                price: item.price
            }))
        };

        try {
            const res = await fetch("/api/purchase", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const result = await res.json();
            document.querySelector(".alertModal .modal-body").textContent = result?.message || "등록이 완료되었습니다.";
            alertModal.show();
        } catch (e) {
            console.error("등록 오류:", e);
            alert("등록에 실패했습니다.");
        }
    });

    // 알림 모달 닫기 버튼 클릭 시 처리
    document.querySelector(".alertCloseBtn").addEventListener("click", () => {
        alertModal.hide();
        if (window.opener) window.opener.getData?.();  // 부모 창 데이터 갱신 요청 (있으면)
        window.close();
    });

    // 취소 버튼 클릭 시 창 닫기
    document.querySelector(".cancelBtn").addEventListener("click", () => window.close());
};

window.onload = () => {
    init();
};
