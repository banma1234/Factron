const init = () => {
    // ✅ DOM 요소들
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));

    // ✅ 승인/반려 버튼 클릭 시 모달 열기
    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    // ✅ 부모창으로부터 데이터 수신
    window.addEventListener("message", (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        const {
            approvalId, apprTypeCode, approvalStatusCode, approverName, approvalStatusName,
            approverId, rejectionReason, confirmedDate, requesterId, requesterName
        } = data;

        if (approvalId) {
            window.receivedData = data;
            setUIState(data);
            setFormData(data);
            fetchTransferByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    // ✅ 승인 처리
    confirmApproveBtn.addEventListener("click", async () => {
        approveModal.hide();
        const result = await sendApproval("APV002"); // 승인 코드
        handleAlert(result);
    });

    // ✅ 반려 처리
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }
        rejectModal.hide();
        document.querySelector("textarea[name='rejectionReason']").value = reason;
        const result = await sendApproval("APV003"); // 반려 코드
        handleAlert(result);
    });

    // ✅ 알림 모달 확인 시
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        const approvalId = form.querySelector("input[name='approvalId']").value;
        // 부모창이 열려있으면 현재 탭 상태만 다시 조회
        if (window.opener && !window.opener.closed) {
            if (typeof window.opener.refreshSingleApproval === 'function') {
                window.opener.refreshSingleApproval(Number(approvalId));
            } else {
                window.opener.getData();
            }
        }
        window.close();
    });

    // ✅ 발령 정보 조회
    async function fetchTransferByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({ srhApprovalId: approvalId });
            const res = await fetch(`/api/trans?${params.toString()}`, { method: "GET" });
            const result = await res.json();
            if (result.status === 200 && result.data?.length > 0) {
                setTransferFormData(result.data[0]);
            } else {
                console.warn("발령 정보가 없습니다.");
            }
        } catch (e) {
            console.error("발령 정보 조회 오류:", e);
        }
    }

    // ✅ 승인/반려 API 호출
    async function sendApproval(statusCode) {
        const approvalId = Number(form.querySelector("input[name='approvalId']").value);
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;
        const data = {
            approvalId,
            approverId: user.id || null,
            approvalType: window.receivedData?.apprTypeCode || null,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
        };
        try {
            const res = await fetch("/api/approval", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });
            return await res.json();
        } catch (e) {
            console.error(e);
            return { message: "요청 중 오류 발생" };
        }
    }

    // ✅ 알림 모달 표시
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리 완료";
        alertModal.show();
    }

    // ✅ 발령 폼 데이터 세팅
    function setTransferFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
        };
        setValue("input[name='empId']", data.empId);
        setValue("input[name='empName']", data.empName);
        setValue("input[name='dept']", data.prevDeptName);
        setValue("input[name='currDept']", data.currDeptName);
        setValue("input[name='currPosition']", data.positionName);
        setValue("input[name='transType']", data.trsTypeName);
        setValue("input[name='date']", data.transferDate);
    }

    // ✅ 기본 결재 폼 데이터 세팅
    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
        };
        setValue("input[name='approvalId']", data.approvalId);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", (data.confirmedDate || '').split(' ')[0]);
        setValue("input[name='approvalStatus']", data.approvalStatusName);
        setValue("textarea[name='rejectionReason']", data.rejectionReason);
        setValue("input[name='publisherId']", data.requesterId);
        setValue("input[name='publisherName']", data.requesterName);
    }

    // ✅ 버튼·상태 UI
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");
        const isStatusValid = data.approvalStatusCode === "APV001";
        const isAuthValid = user.authCode === "ATH002";
        approveBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        rejectBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        approvalResultSection.style.display = isStatusValid ? "none" : "block";
    }
};

// ✅ 초기화 + 부모창에 ready 전송
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
