const init = () => {
    const form = document.querySelector("form");

    window.addEventListener("message", async (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;

        if (data.approvalId) {
            await fetchContractByApprovalId(data.approvalId);
        } else if (data.contractId) {
            await fetchContractByContractId(data.contractId);
        } else {
            console.warn("전달된 데이터에 approvalId나 contractId가 없음:", data);
        }
    });

    async function fetchContractByApprovalId(approvalId) {
        try {
            const res = await fetch(`/api/contract?srhApprovalId=${approvalId}`);
            const { data: list } = await res.json();
            if (!list || list.length === 0) {
                console.warn("조회 결과가 없습니다.");
                return;
            }

            const contract = list[0];
            renderContractDetail(contract);
            await fetchAndRenderItems(contract.contractId);
        } catch (err) {
            console.error("수주 상세 조회 실패:", err);
            alert("수주 상세 조회 중 문제가 발생했습니다.");
        }
    }

    async function fetchContractByContractId(contractId) {
        try {
            const res = await fetch(`/api/contract/${contractId}`);
            const { data: contract } = await res.json();
            if (!contract) {
                console.warn("조회 결과가 없습니다.");
                return;
            }

            renderContractDetail(contract);
            await fetchAndRenderItems(contract.contractId);
        } catch (err) {
            console.error("단건 수주 상세 조회 실패:", err);
            alert("수주 상세 조회 중 문제가 발생했습니다.");
        }
    }

    function renderContractDetail(contract) {
        // 폼 채우기
        form.querySelector("input[name='employeeId']").value = contract.employeeId || '';
        form.querySelector("input[name='employeeName']").value = contract.employeeName || '';
        form.querySelector("input[name='clientName']").value = contract.clientName || '';
        form.querySelector("input[name='createdAt']").value = contract.createdAt || '';
        form.querySelector("input[name='deadline']").value = contract.deadline || '';

        document.querySelector("span[name='totalAmount']").textContent =
            `₩${(contract.totalAmount ?? 0).toLocaleString()}`;

        window.receivedContractId = contract.contractId;
        window.statusCode = contract.statusCode;

        setUIState();
    }

    async function fetchAndRenderItems(contractId) {
        try {
            const itemsRes = await fetch(`/api/contract/${contractId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedContractItems = items;
            renderContractItems(items);
        } catch (err) {
            console.error("품목 조회 실패:", err);
        }
    }

    function renderContractItems(items) {
        const container = document.querySelector(".contract-items");
        container.innerHTML = "";

        items.forEach(item => {
            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between";
            const priceText = (item.amount ?? 0).toLocaleString();
            div.innerHTML = `
            <span>${item.itemName} × ${item.quantity}개</span>
            <span>₩${priceText}</span>
        `;
            container.appendChild(div);
        });
    }


    function setUIState() {
        const cancelBtn = document.querySelector(".cancelApprovalBtn");

        const isPending = window.statusCode === "STP001";
        const isAuthorized = user.authCode === "ATH004";

        cancelBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
    }

    // 결재 취소 버튼 클릭 이벤트
    document.querySelector(".cancelApprovalBtn").addEventListener("click", async () => {
        if (!confirm("결재를 취소하시겠습니까?")) return;

        try {
            const approvalId = window.receivedData?.approvalId;
            const res = await fetch(`/api/contract/cancel?approvalId=${approvalId}`, {
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
