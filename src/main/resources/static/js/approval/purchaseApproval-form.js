const init = () => {
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));

    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    window.addEventListener("message", async function (event) {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;
        setUIState(data);
        setFormData(data);
        await fetchPurchase(data.approvalId);
    });

    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002");
        approveModal.hide();
        handleAlert(result);
    });

    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) return alert("반려 사유를 입력해주세요.");
        form.querySelector("textarea[name='rejectionReason']").value = reason;
        const result = await sendApproval("APV003");
        rejectModal.hide();
        handleAlert(result);
    });

    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        if (window.opener && !window.opener.closed) {
            const approvalId = Number(form.querySelector("input[name='approvalId']").value);
            if (typeof window.opener.refreshSingleApproval === 'function') {
                window.opener.refreshSingleApproval(approvalId);
            } else {
                window.opener.getData();  // fallback
            }
        }
        window.close();
    });


    async function fetchPurchase(approvalId) {
        try {
            const res = await fetch(`/api/purchase?srhApprovalId=${approvalId}`);
            const { data: purchaseList } = await res.json();
            if (!purchaseList || purchaseList.length === 0) return;

            const purchase = purchaseList[0];
            window.receivedPurchaseId = purchase.purchaseId;

            form.querySelector("input[name='employeeId']").value = purchase.employeeId || '';
            form.querySelector("input[name='employeeName']").value = purchase.employeeName || '';
            form.querySelector("input[name='clientName']").value = purchase.clientName || '';
            form.querySelector("input[name='createdAt']").value = purchase.createdAt || '';

            const itemsRes = await fetch(`/api/purchase/${purchase.purchaseId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedPurchaseItems = items;
            renderPurchaseItems(items);

            const totalAmountSpan = document.querySelector("span[name='totalAmount']");
            const totalAmount = purchase.totalAmount ?? 0;
            if (totalAmountSpan) {
                totalAmountSpan.textContent = `₩${totalAmount.toLocaleString()}`;
            }
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
