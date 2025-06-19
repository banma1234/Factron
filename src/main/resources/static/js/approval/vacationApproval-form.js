const init = () => {
    // ✅ DOM 요소 가져오기
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");

    // ✅ 부모 창 메시지 수신
    window.addEventListener("message", (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        const {
            approvalId,
            apprTypeCode,
            approvalStatusCode,
            approverName,
            approvalStatusName,
            approverId,
            rejectionReason,
            confirmedDate,
            requestedAt
        } = data;

        if (approvalId) {
            // 전역에 저장
            window.receivedData = data;

            setUIState(data);
            setFormData(data);
            fetchVacationByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    // ✅ 승인 버튼 클릭
    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002");
        handleAlert(result);
    });

    // ✅ 반려 버튼 클릭
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }

        form.querySelector("textarea[name='rejectionReason']").value = reason;
        const result = await sendApproval("APV003");
        handleAlert(result);
    });

    // ✅ 알림 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        if (window.opener && !window.opener.closed) {
            window.opener.getData();
        }
        window.close();
    });

    // ✅ 휴가 정보 조회
    async function fetchVacationByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({ srhApprovalId: approvalId });

            const response = await fetch(`/api/vacation?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            });

            const result = await response.json();

            if (result.status === 200 && result.data?.length > 0) {
                const vacationData = result.data[0];
                setVacationFormData(vacationData);
            } else {
                console.warn("휴가 정보가 없습니다.");
            }
        } catch (error) {
            console.error("휴가 정보 조회 중 오류:", error);
        }
    }

    // ✅ 승인/반려 API 전송
    async function sendApproval(approvalStatusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;

        const data = {
            approvalId: Number(approvalId),
            approverId: user.id || null,
            approvalType: window.receivedData?.apprTypeCode || null,
            approvalStatus: approvalStatusCode,
            rejectionReason: approvalStatusCode === "APV003" ? rejectionReason : null,
        };

        try {
            const res = await fetch("/api/approval", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
            });

            return await res.json();
        } catch (e) {
            console.error("API 오류:", e);
            return { message: "요청 중 오류 발생" };
        }
    }

    // ✅ 알림 모달 표시
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent =
            res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // ✅ 결재 상태에 따른 UI 처리
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

    // ✅ 결재 공통 폼 데이터 세팅
    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
            else console.warn(`Element not found: ${selector}`);
        };

        setValue("input[name='approvalId']", data.approvalId);
        // setValue("input[name='apprTypeCode']", data.apprTypeCode);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", (data.confirmedDate || '').split(' ')[0]);
        setValue("input[name='approvalStatus']", data.approvalStatusName);
        setValue("input[name='applicationDate']", (data.requestedAt || '').split(' ')[0]);

        const textarea = form.querySelector("textarea[name='rejectionReason']");
        if (textarea) textarea.value = data.rejectionReason || '';
    }

    // ✅ 휴가 폼 데이터 세팅
    function setVacationFormData(data) {
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='deptName']").value = data.deptName || '';
        form.querySelector("input[name='positionName']").value = data.positionName || '';
        form.querySelector("input[name='vacationStartDate']").value = data.vacationStartDate || '';
        form.querySelector("input[name='vacationEndDate']").value = data.vacationEndDate || '';
        form.querySelector("input[name='remark']").value = data.remark || '';
    }
};

// ✅ 로딩 시 초기화
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
