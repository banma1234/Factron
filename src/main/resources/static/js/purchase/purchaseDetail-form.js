const init = () => {
    const form = document.querySelector("form");

    window.addEventListener("message", async (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;

        if (data.approvalId) {
            await fetchPurchaseByApprovalId(data.approvalId);
        } else if (data.purchaseId) {
            await fetchPurchaseByPurchaseId(data.purchaseId);
        } else {
            console.warn("전달된 데이터에 approvalId나 purchaseId가 없음:", data);
        }
    });

    async function fetchPurchaseByApprovalId(approvalId) {
        try {
            const res = await fetch(`/api/purchase?srhApprovalId=${approvalId}`);
            const { data: list } = await res.json();
            if (!list || list.length === 0) {
                console.warn("조회 결과가 없습니다.");
                return;
            }

            const purchase = list[0];
            renderPurchaseDetail(purchase);
            await fetchAndRenderItems(purchase.purchaseId);
        } catch (err) {
            console.error("발주 상세 조회 실패:", err);
            alert("발주 상세 조회 중 문제가 발생했습니다.");
        }
    }

    async function fetchPurchaseByPurchaseId(purchaseId) {
        try {
            const res = await fetch(`/api/purchase/${purchaseId}`);
            const { data: purchase } = await res.json();
            if (!purchase) {
                console.warn("조회 결과가 없습니다.");
                return;
            }

            renderPurchaseDetail(purchase);
            await fetchAndRenderItems(purchase.purchaseId);
        } catch (err) {
            console.error("단건 발주 상세 조회 실패:", err);
            alert("발주 상세 조회 중 문제가 발생했습니다.");
        }
    }

    function renderPurchaseDetail(purchase) {
        // 폼 채우기
        form.querySelector("input[name='employeeId']").value = purchase.employeeId || '';
        form.querySelector("input[name='employeeName']").value = purchase.employeeName || '';
        form.querySelector("input[name='clientName']").value = purchase.clientName || '';
        form.querySelector("input[name='createdAt']").value = purchase.createdAt || '';

        document.querySelector("span[name='totalAmount']").textContent =
            `₩${(purchase.totalAmount ?? 0).toLocaleString()}`;

        window.receivedPurchaseId = purchase.purchaseId;
        window.statusCode = purchase.statusCode;

        setUIState();
    }

    async function fetchAndRenderItems(purchaseId) {
        try {
            const itemsRes = await fetch(`/api/purchase/${purchaseId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedPurchaseItems = items;
            renderPurchaseItems(items);
        } catch (err) {
            console.error("품목 조회 실패:", err);
        }
    }

    function renderPurchaseItems(items) {
        const container = document.querySelector(".purchase-items");
        container.innerHTML = "";

        items.forEach(item => {
            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between";
            const priceText = (item.amount ?? 0).toLocaleString();
            div.innerHTML = `
                <span>${item.materialName} × ${item.quantity}개</span>
                <span>₩${priceText}</span>
            `;
            container.appendChild(div);
        });
    }

    function setUIState() {
        const cancelBtn = document.querySelector(".cancelApprovalBtn");

        const isPending = window.statusCode === "STP002";
        const isAuthorized = user.authCode === "ATH004";

        cancelBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
    }

    // 결재 취소 버튼 클릭 이벤트
    document.querySelector(".cancelApprovalBtn").addEventListener("click", async () => {
        if (!confirm("결재를 취소하시겠습니까?")) return;

        try {
            const approvalId = window.receivedData?.approvalId;
            const res = await fetch(`/api/purchase/cancel?approvalId=${approvalId}`, {
                method: "PUT"
            });
            const result = await res.json();
            alert(result.message || "결재가 취소되었습니다.");
            if (window.opener) window.opener.getData();
            window.close();
        } catch (e) {
            console.error("결재 취소 오류:", e);
            alert("결재 취소 중 문제가 발생했습니다.");
        }
    });
};

window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
