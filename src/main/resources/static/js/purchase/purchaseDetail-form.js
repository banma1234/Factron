const init = () => {
    const form = document.querySelector("form");

    // 부모 창으로부터 메시지 수신 (approvalId 또는 purchaseId 전달받음)
    window.addEventListener("message", async (event) => {
        const data = event.data;
        // React DevTools 관련 메시지 무시
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;

        // approvalId 또는 purchaseId에 따라 상세 데이터 조회
        if (data.approvalId) {
            await fetchPurchaseByApprovalId(data.approvalId);
        } else if (data.purchaseId) {
            await fetchPurchaseByPurchaseId(data.purchaseId);
        } else {
            console.warn("전달된 데이터에 approvalId나 purchaseId가 없음:", data);
        }
    });

    // approvalId로 발주 데이터 조회 및 화면 렌더링
    async function fetchPurchaseByApprovalId(approvalId) {
        try {
            const res = await fetch(`/api/purchase?srhApprovalId=${approvalId}`);
            const { data: list } = await res.json();
            if (!list || list.length === 0) {
                console.warn("조회 결과가 없습니다.");
                return;
            }

            const purchase = list[0];
            renderPurchaseDetail(purchase);       // 상세 정보 폼에 표시
            await fetchAndRenderItems(purchase.purchaseId); // 품목 정보 조회 및 렌더링
        } catch (err) {
            console.error("발주 상세 조회 실패:", err);
            alert("발주 상세 조회 중 문제가 발생했습니다.");
        }
    }

    // purchaseId로 단일 발주 데이터 조회 및 화면 렌더링
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

    // 발주 상세 정보를 폼에 렌더링
    function renderPurchaseDetail(purchase) {
        form.querySelector("input[name='employeeId']").value = purchase.employeeId || '';
        form.querySelector("input[name='employeeName']").value = purchase.employeeName || '';
        form.querySelector("input[name='clientName']").value = purchase.clientName || '';
        form.querySelector("input[name='createdAt']").value = purchase.createdAt || '';

        document.querySelector("span[name='totalAmount']").textContent =
            `₩${(purchase.totalAmount ?? 0).toLocaleString()}`;

        // 전역 변수로 발주 ID, 상태코드 저장 (다른 함수에서 사용)
        window.receivedPurchaseId = purchase.purchaseId;
        window.statusCode = purchase.statusCode;

        setUIState();  // 상태에 따라 UI 표시 업데이트
    }

    // 발주 품목 목록 조회 및 렌더링
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

    // 발주 품목 리스트를 화면에 렌더링
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

    // 상태코드 및 권한에 따른 UI 요소 표시 설정
    function setUIState() {
        const cancelBtn = document.querySelector(".cancelApprovalBtn");

        const isPending = window.statusCode === "STP002";       // 대기 상태 여부 확인
        const isAuthorized = user.authCode === "ATH004";          // 권한 확인

        // 결재 취소 버튼 노출 여부 결정
        cancelBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
    }

    // 결재 취소 버튼 클릭 이벤트 처리
    document.querySelector(".cancelApprovalBtn").addEventListener("click", async () => {
        if (!confirm("결재를 취소하시겠습니까?")) return;

        try {
            const approvalId = window.receivedData?.approvalId;
            const res = await fetch(`/api/purchase/cancel?approvalId=${approvalId}`, {
                method: "PUT"
            });
            const result = await res.json();
            alert(result.message || "결재가 취소되었습니다.");
            if (window.opener) window.opener.getData();  // 부모창 데이터 갱신 요청
            window.close();                               // 팝업 닫기
        } catch (e) {
            console.error("결재 취소 오류:", e);
            alert("결재 취소 중 문제가 발생했습니다.");
        }
    });
};

// 페이지 로드 시 init 함수 실행 및 부모창에 준비 메시지 발송
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
