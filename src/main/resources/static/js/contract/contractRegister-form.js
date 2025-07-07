const init = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.querySelector(".confirmModal")); // 등록 확인 모달
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));     // 알림 모달

    let itemGrid;
    let selectedItems = []; // 선택된 품목 목록

    form.addEventListener("submit", e => e.preventDefault()); // 폼 기본 제출 막기

    // 품목 그리드 초기화
    itemGrid = initGrid(
        document.getElementById("itemGrid"),
        300,
        [
            { header: "품목 ID", name: "itemId", align: "center" },
            { header: "품목명", name: "name", align: "center" },
            { header: "단위", name: "unitName", align: "center" },
            { header: "가격", name: "price", align: "center" }
        ]
    );

    loadClients(); // 거래처 목록 불러오기
    loadItems();   // 품목 목록 불러오기

    if (window.opener) {
        window.opener.postMessage("ready", "*"); // 부모창에 준비 완료 메시지 전송
    }

    // 거래처 목록 API 호출 후 select 옵션으로 추가
    async function loadClients() {
        try {
            const res = await fetch('/api/client');
            const json = await res.json();
            const clients = json.data || [];
            const select = form.querySelector("select[name='clientId']");
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

    // 품목 목록 조회 (완제품 타입만) / 검색어 필터 적용
    async function loadItems(keyword = "") {
        try {
            const params = new URLSearchParams();
            params.append('itemName', keyword);
            params.append('typeCode', 'ITP003');  // 완제품 타입 코드

            const res = await fetch(`/api/item?${params.toString()}`);
            const json = await res.json();
            itemGrid.resetData(json.data || []);
        } catch (e) {
            console.error("품목 조회 실패", e);
            alert("품목 목록 조회에 실패했습니다.");
        }
    }

    // 검색창 엔터 또는 검색 버튼 클릭 시 품목 리스트 갱신
    form.querySelector("input[name='itemSearch']").addEventListener("keyup", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            loadItems(e.target.value.trim());
        }
    });
    form.querySelector(".itemSearchBtn").addEventListener("click", (e) => {
        e.preventDefault();
        loadItems(form.querySelector("input[name='itemSearch']").value.trim());
    });

    // 품목 더블클릭 시 선택 목록에 추가
    itemGrid.on("dblclick", ev => {
        if (ev.rowKey != null) {
            const row = itemGrid.getRow(ev.rowKey);
            addItemToContractList(row);
        }
    });

    // 품목을 선택 목록에 추가 (중복 방지, 기본 수량 1 설정)
    function addItemToContractList(item) {
        if (selectedItems.find(i => i.itemId === item.itemId)) {
            alert("이미 추가된 품목입니다.");
            return;
        }
        selectedItems.push({ ...item, quantity: 1, price: item.price });
        renderSelectedItems();
    }

    // 선택한 품목 목록 화면에 렌더링 (수량/가격 수정, 삭제 기능 포함)
    function renderSelectedItems() {
        const container = document.querySelector(".contract-items");
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

            // 수량 입력 시 총합 업데이트
            const qtyInput = div.querySelector(".qty-input");
            qtyInput.addEventListener("input", (e) => {
                let val = parseInt(e.target.value);
                if (isNaN(val) || val < 1) val = 1;
                selectedItems[index].quantity = val;
                e.target.value = val;
                div.querySelector(".item-total").textContent = `₩${(val * selectedItems[index].price).toLocaleString()}`;
                updateTotalAmount();
            });

            // 가격 입력 시 총합 업데이트
            const priceInput = div.querySelector(".price-input");
            priceInput.addEventListener("input", (e) => {
                let val = parseInt(e.target.value);
                if (isNaN(val) || val < 0) val = 0;
                selectedItems[index].price = val;
                e.target.value = val;
                div.querySelector(".item-total").textContent = `₩${(selectedItems[index].quantity * val).toLocaleString()}`;
                updateTotalAmount();
            });

            // 품목 삭제 버튼 클릭 처리
            div.querySelector(".remove-btn").addEventListener("click", () => {
                selectedItems.splice(index, 1);
                renderSelectedItems();
            });

            container.appendChild(div);
        });

        updateTotalAmount();
    }

    // 선택된 품목 전체 금액 계산 후 표시
    function updateTotalAmount() {
        const total = selectedItems.reduce((sum, item) => sum + (item.quantity * item.price), 0);
        document.querySelector("span[name='totalAmount']").textContent = `₩${total.toLocaleString()}`;
    }

    // 저장 버튼 클릭 시 입력값 검증 후 확인 모달 표시
    document.querySelector(".saveBtn").addEventListener("click", () => {
        const clientId = form.querySelector("select[name='clientId']").value;
        const deadline = form.querySelector("input[name='deadline']").value;
        if (!clientId) return alert("거래처를 선택해주세요.");
        if (!deadline) return alert("납기일을 선택해주세요.");
        if (!selectedItems.length) return alert("수주 품목을 선택해주세요.");
        confirmModal.show();
    });

    // 확인 모달 내 등록 버튼 클릭 시 서버에 수주 데이터 전송
    document.querySelector(".confirmRegisterBtn").addEventListener("click", async () => {
        confirmModal.hide();
        const clientId = form.querySelector("select[name='clientId']").value;
        const deadline = form.querySelector("input[name='deadline']").value;

        const payload = {
            clientId,
            deadline,
            employeeId: user.id || null,
            items: selectedItems.map(item => ({
                itemId: item.itemId,
                quantity: item.quantity,
                price: item.price
            }))
        };

        try {
            const res = await fetch("/api/contract", {
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

    // 알림 모달 닫기 버튼 클릭 시 부모창 갱신 후 창 닫기
    document.querySelector(".alertCloseBtn").addEventListener("click", () => {
        alertModal.hide();
        if (window.opener) window.opener.getData?.();
        window.close();
    });

    // 취소 버튼 클릭 시 창 닫기
    document.querySelector(".cancelBtn").addEventListener("click", () => window.close());
};

window.onload = () => {
    init();
};
