const init = () => {
    // 폼과 모달, 버튼 요소 초기화
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));

    // 승인/반려 버튼 클릭 시 확인 모달 열기
    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    // 부모 창에서 메시지 수신 시 처리
    window.addEventListener("message", async function (event) {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return; // 불필요 메시지 무시

        window.receivedData = data;       // 받은 데이터 전역에 저장
        setUIState(data);                 // UI 상태(버튼 표시 등) 설정
        setFormData(data);                // 폼에 데이터 채우기
        await fetchPurchase(data.approvalId); // 발주 상세 데이터 조회
    });

    // 승인 확인 버튼 클릭 시 승인 처리
    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002"); // 승인 상태코드
        approveModal.hide();
        handleAlert(result); // 처리 결과 알림
    });

    // 반려 확인 버튼 클릭 시 반려 처리
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) return alert("반려 사유를 입력해주세요.");
        form.querySelector("textarea[name='rejectionReason']").value = reason; // 숨겨진 필드에 반려 사유 설정
        const result = await sendApproval("APV003"); // 반려 상태코드
        rejectModal.hide();
        handleAlert(result); // 처리 결과 알림
    });

    // 알림 모달 닫기 버튼 클릭 시 부모 창 새로고침 요청 후 팝업 닫기
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        if (window.opener && !window.opener.closed) {
            const approvalId = Number(form.querySelector("input[name='approvalId']").value);
            if (typeof window.opener.refreshSingleApproval === 'function') {
                window.opener.refreshSingleApproval(approvalId); // 특정 항목 새로고침
            } else {
                window.opener.getData(); // 전체 새로고침 fallback
            }
        }
        window.close();
    });

    /**
     * 발주 상세 데이터 및 품목 조회
     */
    async function fetchPurchase(approvalId) {
        try {
            const res = await fetch(`/api/purchase?srhApprovalId=${approvalId}`);
            const { data: purchaseList } = await res.json();
            if (!purchaseList || purchaseList.length === 0) return;

            const purchase = purchaseList[0];
            window.receivedPurchaseId = purchase.purchaseId; // 전역에 발주 ID 저장

            // 폼에 발주 기본 정보 설정
            form.querySelector("input[name='employeeId']").value = purchase.employeeId || '';
            form.querySelector("input[name='employeeName']").value = purchase.employeeName || '';
            form.querySelector("input[name='clientName']").value = purchase.clientName || '';
            form.querySelector("input[name='createdAt']").value = purchase.createdAt || '';

            // 품목 조회
            const itemsRes = await fetch(`/api/purchase/${purchase.purchaseId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedPurchaseItems = items; // 전역에 품목 저장
            renderPurchaseItems(items);           // 화면에 렌더링

            // 총 금액 표시
            const totalAmountSpan = document.querySelector("span[name='totalAmount']");
            const totalAmount = purchase.totalAmount ?? 0;
            if (totalAmountSpan) {
                totalAmountSpan.textContent = `₩${totalAmount.toLocaleString()}`;
            }
        } catch (err) {
            console.error("발주 정보 조회 실패", err);
        }
    }

    /**
     * 발주 품목 리스트 화면에 출력
     */
    function renderPurchaseItems(items) {
        const container = document.querySelector(".purchase-items");
        container.innerHTML = ""; // 초기화

        items.forEach(item => {
            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between";
            const quantityText = (item.quantity ?? 0).toLocaleString();
            const priceText = (item.amount ?? 0).toLocaleString(); // amount: 단가*수량
            div.innerHTML = `
                <span>${item.materialName} × ${quantityText} ${item.unitName}</span>
                <span>₩${priceText}</span>
            `;
            container.appendChild(div);
        });
    }

    /**
     * 승인/반려 요청 API 호출
     */
    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;
        const approvalType = window.receivedData?.apprTypeCode || null;

        // 요청 데이터 구성
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
            return await res.json(); // 응답 반환
        } catch (e) {
            console.error("API 오류:", e);
            return { message: "요청 실패" };
        }
    }

    /**
     * 처리 결과 알림 모달 표시
     */
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent =
            res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    /**
     * 수신 데이터로 폼 채우기
     */
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

    /**
     * 결재 상태 및 권한에 따라 UI(버튼/결과 표시) 제어
     */
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const isPending = data.approvalStatusCode === "APV001"; // 결재대기 상태
        const isAuthorized = user.authCode === "ATH005" || "ATH003";       // 승인권한 여부

        approveBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
        rejectBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
        approvalResultSection.style.display = isPending ? "none" : "block"; // 승인/반려 완료 시 결과 표시
    }
};

// 페이지 로드 시 초기화 실행 & 부모창에 ready 알림
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
