const init = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.querySelector(".confirmModal"));
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));

    // user = null; 제거 (전역 user 사용)
    let materialGrid;
    let selectedItems = [];

    form.addEventListener("submit", e => e.preventDefault());

    materialGrid = initGrid(
        document.getElementById("materialGrid"),
        300,
        [
            { header: "자재 ID", name: "materialId", align: "center" },
            { header: "자재명", name: "name", align: "center" },
            { header: "단위", name: "unit", align: "center" },
            { header: "규격", name: "spec", align: "center" }
        ]
    );

    loadClients();
    loadMaterials();

    // 부모에게 준비 완료 신호만 보냄 (user는 안 받음)
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }

    // message 이벤트에서 user 받는 부분 삭제

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

    form.querySelector("input[name='materialSearch']").addEventListener("keyup", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            loadMaterials(e.target.value.trim());
        }
    });
    form.querySelector(".materialSearchBtn").addEventListener("click", (e) => {
        e.preventDefault();
        loadMaterials(form.querySelector("input[name='materialSearch']").value.trim());
    });

    materialGrid.on("dblclick", ev => {
        if (ev.rowKey != null) {
            const row = materialGrid.getRow(ev.rowKey);
            addItemToPurchaseList(row);
        }
    });

    function addItemToPurchaseList(item) {
        if (selectedItems.find(i => i.materialId === item.materialId)) {
            alert("이미 추가된 자재입니다.");
            return;
        }
        selectedItems.push({ ...item, quantity: 1, price: 1000 });
        renderSelectedItems();
    }

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
                    <input type="number" min="1" value="${item.quantity}" class="form-control form-control-sm qty-input" style="width:60px;" /> 개 
                    <input type="number" min="0" value="${item.price}" class="form-control form-control-sm price-input" style="width:80px;" /> 원 
                    = <span class="item-total">₩${itemTotal.toLocaleString()}</span>
                </div>
                <button type="button" class="btn btn-sm btn-outline-danger remove-btn">X</button>
            `;

            const qtyInput = div.querySelector(".qty-input");
            const priceInput = div.querySelector(".price-input");
            const itemTotalSpan = div.querySelector(".item-total");

            qtyInput.addEventListener("input", (e) => {
                let val = parseInt(e.target.value);
                if (isNaN(val) || val < 1) val = 1;
                selectedItems[index].quantity = val;
                e.target.value = val;
                itemTotalSpan.textContent = `₩${(val * selectedItems[index].price).toLocaleString()}`;
                updateTotalAmount();
            });

            priceInput.addEventListener("input", (e) => {
                let val = parseInt(e.target.value);
                if (isNaN(val) || val < 0) val = 0;
                selectedItems[index].price = val;
                e.target.value = val;
                itemTotalSpan.textContent = `₩${(selectedItems[index].quantity * val).toLocaleString()}`;
                updateTotalAmount();
            });

            div.querySelector(".remove-btn").addEventListener("click", () => {
                selectedItems.splice(index, 1);
                renderSelectedItems();
            });

            container.appendChild(div);
        });

        updateTotalAmount();
    }

    function updateTotalAmount() {
        const total = selectedItems.reduce((sum, item) => sum + (item.quantity * item.price), 0);
        document.querySelector("span[name='totalAmount']").textContent = `₩${total.toLocaleString()}`;
    }

    document.querySelector(".saveBtn").addEventListener("click", () => {
        const clientId = form.querySelector("select[name='clientId']").value;
        if (!clientId) return alert("거래처를 선택해주세요.");
        if (!selectedItems.length) return alert("발주 자재를 선택해주세요.");
        confirmModal.show();
    });

    document.querySelector(".confirmRegisterBtn").addEventListener("click", async () => {
        confirmModal.hide();
        const clientId = form.querySelector("select[name='clientId']").value;

        // user.id 전역 변수를 그대로 사용
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

    document.querySelector(".alertCloseBtn").addEventListener("click", () => {
        alertModal.hide();
        if (window.opener) window.opener.getData?.();
        window.close();
    });

    document.querySelector(".cancelBtn").addEventListener("click", () => window.close());
};

window.onload = () => {
    init();
};
