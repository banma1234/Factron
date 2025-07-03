const init = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.querySelector(".confirmModal"));
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));

    let itemGrid;
    let selectedItems = [];

    form.addEventListener("submit", e => e.preventDefault());

    itemGrid = initGrid(
        document.getElementById("itemGrid"),
        300,
        [
            { header: "품목 ID", name: "itemId", align: "center" },
            { header: "품목명", name: "name", align: "center" },
            { header: "단위", name: "unit", align: "center" },
            { header: "가격", name: "price", align: "center" }
        ]
    );

    loadClients();
    loadItems();

    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }

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

    async function loadItems(keyword = "") {
        try {
            const params = new URLSearchParams();
            params.append('itemName', keyword);
            params.append('typeCode', 'ITP003');  // ← 완제품만 조회

            const res = await fetch(`/api/item?${params.toString()}`);
            const json = await res.json();
            itemGrid.resetData(json.data || []);
        } catch (e) {
            console.error("품목 조회 실패", e);
            alert("품목 목록 조회에 실패했습니다.");
        }
    }


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

    itemGrid.on("dblclick", ev => {
        if (ev.rowKey != null) {
            const row = itemGrid.getRow(ev.rowKey);
            addItemToContractList(row);
        }
    });

    function addItemToContractList(item) {
        if (selectedItems.find(i => i.itemId === item.itemId)) {
            alert("이미 추가된 품목입니다.");
            return;
        }
        selectedItems.push({ ...item, quantity: 1, price: item.price }); // 여기 수정
        renderSelectedItems();
    }

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
        const deadline = form.querySelector("input[name='deadline']").value;
        if (!clientId) return alert("거래처를 선택해주세요.");
        if (!deadline) return alert("납기일을 선택해주세요.");
        if (!selectedItems.length) return alert("수주 품목을 선택해주세요.");
        confirmModal.show();
    });

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
