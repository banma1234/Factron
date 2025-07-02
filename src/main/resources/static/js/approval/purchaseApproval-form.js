const init = () => {
    const form = document.querySelector("form");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));

    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    document.querySelector(".confirmApproveBtn").addEventListener("click", async () => {
        const result = await sendApproval("APV002");
        approveModal.hide();
        handleAlert(result);
    });

    document.querySelector(".confirmRejectBtn").addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) return alert("반려 사유를 입력해주세요.");
        form.querySelector("textarea[name='rejectionReason']").value = reason;
        const result = await sendApproval("APV003");
        rejectModal.hide();
        handleAlert(result);
    });

    document.querySelector(".alertBtn").addEventListener("click", () => {
        alertModal.hide();
        if (window.opener) window.opener.getData();
        window.close();
    });

    window.addEventListener("message", async (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;
        setFormData(data);
        setUIState(data);
        await fetchPurchase(data.approvalId);
    });

    async function fetchPurchase(approvalId) {
        try {
            const res = await fetch(`/api/purchase?srhApprovalId=${approvalId}`);
            const { data: list } = await res.json();
            if (!list || list.length === 0) return;

            const purchase = list[0];

            form.querySelector("input[name='employeeId']").value = purchase.employeeId || '';
            form.querySelector("input[name='employeeName']").value = purchase.employeeName || '';
            form.querySelector("input[name='clientName']").value = purchase.clientName || '';
            form.querySelector("input[name='createdAt']").value = purchase.createdAt || '';

            document.querySelector("span[name='totalAmount']").textContent =
                `₩${(purchase.totalAmount ?? 0).toLocaleString()}`;

            window.receivedPurchaseId = purchase.purchaseId;

            const itemsRes = await fetch(`/api/purchase/${purchase.purchaseId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedPurchaseItems = items;
            renderPurchaseItems(items);
        } catch (err) {
            console.error("발주 정보 조회 실패", err);
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

    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;
        const approvalType = window.receivedData?.apprTypeCode || null;

        const payload = {
            approvalId: Number(approvalId),
            approverId: user.id || null,
            approvalType: approvalType,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
            purchaseId: window.receivedPurchaseId,
            purchaseItems: (window.receivedPurchaseItems || []).map(item => ({
                materialId: item.materialId,
                quantity: item.quantity
            }))
        };

        try {
            const res = await fetch("/api/salesApproval", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            return await res.json();
        } catch (e) {
            console.error("API 오류:", e);
            return { message: "요청 실패" };
        }
    }

    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent =
            res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
        };

        setValue("input[name='approvalId']", data.approvalId);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", (data.confirmedAt || '').split(' ')[0]);
        setValue("input[name='approvalStatus']", data.approvalStatusName);

        const textarea = form.querySelector("textarea[name='rejectionReason']");
        if (textarea) textarea.value = data.rejectionReason || '';
    }

    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const isPending = data.approvalStatusCode === "APV001";
        const isAuthorized = user.authCode === "ATH005";

        approveBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
        rejectBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
        approvalResultSection.style.display = isPending ? "none" : "block";
    }
};

window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
