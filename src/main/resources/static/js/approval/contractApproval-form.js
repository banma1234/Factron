const init = () => {
    // 폼 요소 및 모달 객체 초기화
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));

    // 승인/반려 버튼 클릭 시 각각 모달 표시
    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    // 부모 창에서 메시지 수신(팝업 오픈 시 데이터 전달)
    window.addEventListener("message", async function (event) {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return; // 무의미한 메시지 무시

        window.receivedData = data; // 전역에 데이터 저장
        setUIState(data);           // 화면 버튼/상태 설정
        setFormData(data);          // 폼 입력 값 설정
        await fetchContract(data.approvalId); // 결재에 해당하는 수주 데이터 조회
    });

    // 승인 확인 버튼 클릭 시 승인 요청 전송
    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002"); // 승인 상태코드
        approveModal.hide();
        handleAlert(result); // 결과 모달 표시
    });

    // 반려 확인 버튼 클릭 시 반려 요청 전송
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) return alert("반려 사유를 입력해주세요.");
        form.querySelector("textarea[name='rejectionReason']").value = reason; // 숨겨진 필드에 반려 사유 저장
        const result = await sendApproval("APV003"); // 반려 상태코드
        rejectModal.hide();
        handleAlert(result); // 결과 모달 표시
    });

    // 알림 모달 닫기 버튼 클릭 시 부모창 새로고침 + 현재 팝업 닫기
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        if (window.opener && !window.opener.closed) {
            const approvalId = Number(form.querySelector("input[name='approvalId']").value);
            if (typeof window.opener.refreshSingleApproval === 'function') {
                window.opener.refreshSingleApproval(approvalId); // 부모 창 특정 항목만 새로고침
            } else {
                window.opener.getData(); // fallback: 전체 새로고침
            }
        }
        window.close(); // 팝업 닫기
    });

    /**
     * 결재번호(approvalId)로 수주 데이터 및 품목 데이터 조회
     */
    async function fetchContract(approvalId) {
        try {
            const contractRes = await fetch(`/api/contract?srhApprovalId=${approvalId}`);
            const { data: contractList } = await contractRes.json();
            if (!contractList || contractList.length === 0) return; // 데이터 없으면 종료

            const contract = contractList[0];
            window.receivedContractId = contract.contractId; // 전역에 contractId 저장

            // 수주 기본 정보 폼에 채우기
            form.querySelector("input[name='employeeId']").value = contract.employeeId || '';
            form.querySelector("input[name='employeeName']").value = contract.employeeName || '';
            form.querySelector("input[name='clientName']").value = contract.clientName || '';
            form.querySelector("input[name='deadline']").value = contract.deadline || '';
            form.querySelector("input[name='createdAt']").value = contract.createdAt || '';

            // 품목 리스트 조회
            const itemsRes = await fetch(`/api/contract/${contract.contractId}/items`);
            const { data: items } = await itemsRes.json();
            window.receivedContractItems = items; // 전역에 저장
            renderContractItems(items); // 화면에 렌더링
        } catch (err) {
            console.error("수주 정보 조회 실패", err);
        }
    }

    /**
     * 화면에 수주 품목 리스트 출력 + 총 금액 계산
     */
    function renderContractItems(items) {
        const container = document.querySelector(".contract-items");
        const totalAmountSpan = document.querySelector("span[name='totalAmount']");
        container.innerHTML = ""; // 초기화

        let totalAmount = 0;
        items.forEach(item => {
            const div = document.createElement("div");
            div.className = "bg-white p-2 rounded border d-flex justify-content-between";
            const price = item.amount ?? 0; // amount: 단가 * 수량
            const priceText = price.toLocaleString();
            totalAmount += price;

            div.innerHTML = `
                <span>${item.itemName} × ${item.quantity} ${item.unitName}</span>
                <span>₩${priceText}</span>
            `;
            container.appendChild(div);
        });

        if (totalAmountSpan) {
            totalAmountSpan.textContent = `₩${totalAmount.toLocaleString()}`;
        }
    }

    /**
     * 승인/반려 API 요청
     * @param {string} statusCode - APV002(승인) 또는 APV003(반려)
     */
    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;
        const contractId = window.receivedContractId;
        const items = window.receivedContractItems || [];

        // 품목 리스트 구성
        const outboundItems = items.map(item => ({
            itemId: item.itemId,
            itemName: item.itemName,
            quantity: item.quantity,
            price: item.price,
            amount: item.amount,
        }));

        const approvalType = window.receivedData?.apprTypeCode || null;

        // 요청 payload
        const payload = {
            approvalId: Number(approvalId),
            approverId: user.id || null,
            approvalType: approvalType,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
            contractId: contractId,
            outboundItems: outboundItems
        };

        // 승인 타입에 따라 API URL 변경
        const url = approvalType === "SLS001" ? "/api/salesApproval" : "/api/approval";

        try {
            const res = await fetch(url, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            return await res.json(); // 서버 응답 반환
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
     * 수신된 데이터로 폼 입력값 채우기
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
     * 결재 상태 및 권한에 따라 버튼 표시/숨김 설정
     */
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const isPending = data.approvalStatusCode === "APV001"; // 결재대기 상태
        const isAuthorized = user.authCode === "ATH005" || "ATH003"; // 결재권한 있음

        approveBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
        rejectBtn.style.display = (isPending && isAuthorized) ? "inline-block" : "none";
        approvalResultSection.style.display = isPending ? "none" : "block"; // 완료된 경우 결과 표시
    }
};

// 페이지 로딩 시 초기화
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*"); // 부모창에 ready 알림
    }
};
