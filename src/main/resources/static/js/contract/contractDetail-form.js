const init = () => {
    const form = document.querySelector("form");

    // 부모창 등에서 데이터 받기
    window.addEventListener("message", async (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;

        if (data.approvalId) {
            await fetchContractByApprovalId(data.approvalId);
        } else if (data.contractId) {
            await fetchContractByContractId(data.contractId);
        } else {
            console.warn("approvalId 또는 contractId 없음:", data);
        }
    });

    // approvalId로 수주 리스트 조회 후 첫 데이터 표시
    async function fetchContractByApprovalId(approvalId) {
        try {
            const res = await fetch(`/api/contract?srhApprovalId=${approvalId}`);
            const { data: list } = await res.json();
            if (!list || list.length === 0) return console.warn("조회 결과 없음");

            renderContractDetail(list[0]);
            await fetchAndRenderItems(list[0].contractId);
        } catch (err) {
            console.error("수주 조회 실패:", err);
            alert("수주 조회 중 오류 발생");
        }
    }

    // contractId로 단건 수주 상세 조회
    async function fetchContractByContractId(contractId) {
        try {
            const res = await fetch(`/api/contract/${contractId}`);
            const { data: contract } = await res.json();
            if (!contract) return console.warn("조회 결과 없음");

            renderContractDetail(contract);
            await fetchAndRenderItems(contract.contractId);
        } catch (err) {
            console.error("수주 상세 조회 실패:", err);
            alert("수주 상세 조회 중 오류 발생");
        }
    }

    // 수주 상세 정보 폼에 표시
    function renderContractDetail(contract) {
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

    // 수주 품목 리스트 조회 및 렌더링
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

    // 품목 정보를 화면에 표시
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

    // UI 상태 설정 (결재 취소 버튼 표시 여부 등)
    function setUIState() {
        const cancelBtn = document.querySelector(".cancelApprovalBtn");
        const isPending = window.statusCode === "STP001"; // 결재 대기 상태 체크
        const isAuthorized = user.authCode === "ATH004"; // 권한 체크

        cancelBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
    }

    // 결재 취소 버튼 클릭 처리
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
