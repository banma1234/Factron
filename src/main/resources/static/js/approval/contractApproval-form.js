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
        await fetchContract(data.approvalId);
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
        if (window.opener && !window.opener.closed) window.opener.getData();
        window.close();
    });

    async function fetchContract(approvalId) {
        try {
            const contractRes = await fetch(`/api/contract?srhApprovalId=${approvalId}`);
            const { data: contractList } = await contractRes.json();
            if (!contractList || contractList.length === 0) return;

            const contract = contractList[0];
            window.receivedContractId = contract.contractId;

            form.querySelector("input[name='employeeId']").value = contract.employeeId || '';
            form.querySelector("input[name='employeeName']").value = contract.employeeName || '';
            form.querySelector("input[name='clientName']").value = contract.clientName || '';
            form.querySelector("input[name='deadline']").value = contract.deadline || '';
            form.querySelector("input[name='createdAt']").value = contract.createdAt || '';

            const itemsRes = await fetch(`/api/contract/${contract.contractId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedContractItems = items;
            renderContractItems(items);
        } catch (err) {
            console.error("수주 정보 조회 실패", err);
        }
    }

    function renderContractItems(items) {
        const container = document.querySelector(".contract-items");
        const totalAmountSpan = document.querySelector("span[name='totalAmount']");
        container.innerHTML = "";

        let totalAmount = 0;

        items.forEach(item => {
            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between";
            const price = item.amount ?? 0;
            const priceText = price.toLocaleString();
            totalAmount += price;

            div.innerHTML = `
                <span>${item.itemName} × ${item.quantity}개</span>
                <span>₩${priceText}</span>
            `;
            container.appendChild(div);
        });

        if (totalAmountSpan) {
            totalAmountSpan.textContent = `₩${totalAmount.toLocaleString()}`;
        }
    }

    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;
        const contractId = window.receivedContractId;
        const items = window.receivedContractItems || [];

        const outboundItems = items.map(item => ({
            itemId: item.itemId,
            itemName: item.itemName,
            quantity: item.quantity,
            price: item.price,
            amount: item.amount,
        }));

        const approvalType = window.receivedData?.apprTypeCode || null;

        const payload = {
            approvalId: Number(approvalId),
            approverId: user.id || null,
            approvalType: approvalType,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
            contractId: contractId,
            outboundItems: outboundItems
        };

        // ✅ API URL 조건 분기
        const url = approvalType === "SLS001" ? "/api/salesApproval" : "/api/approval";

        try {
            const res = await fetch(url, {
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
        const isAuthorized = user.authCode === "ATH004";

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