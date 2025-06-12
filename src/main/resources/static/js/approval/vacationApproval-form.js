const init = () => {
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn"); // 승인 확인 버튼
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");   // 반려 확인 버튼

    // 부모 창에서 데이터 수신
    window.addEventListener("message", (event) => {
        const data = event.data;
        console.log("수신된 메시지:", data);

        if (!data || data?.source === "react-devtools-content-script") return;

        const { approvalId, apprTypeCode, approvalStatusCode, userId, authCode } = data;

        if (approvalId && userId && authCode) {
            console.log("수신된 결재 ID:", approvalId);
            console.log("결재 종류 코드:", apprTypeCode);
            console.log("결재 상태 코드:", approvalStatusCode);
            console.log("사용자 ID:", userId);
            console.log("권한 코드:", authCode);

            window.receivedData = data;
            setUIState(data);
            setFormData(data);
        } else {
            console.warn("필수 데이터 누락");
        }
    });

    // 승인 버튼 클릭
    confirmApproveBtn.addEventListener("click", async () => {
        console.log("승인 버튼 클릭");
        const result = await sendApproval("APV002");
        handleAlert(result);
    });

    // 반려 버튼 클릭
    confirmRejectBtn.addEventListener("click", async () => {
        console.log("반려 버튼 클릭");

        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }

        form.querySelector("textarea[name='rejectionReason']").value = reason;
        const result = await sendApproval("APV003");
        handleAlert(result);
    });

    // 알림 모달 확인 버튼 클릭
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모창의 그리드 갱신 후 현재 창 닫기
        if (window.opener && !window.opener.closed) {
            window.opener.getData();
        }
        window.close();
    });

    // 승인/반려 API 요청
    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;

        const data = {
            approvalId: Number(approvalId),
            approverId: window.receivedData?.userId || null,
            approvalType: window.receivedData?.apprTypeCode || null,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
        };
        console.log("전송할 데이터:", data);

        try {
            const res = await fetch("/api/approval", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
            });

            return await res.json();
        } catch (e) {
            console.error("API 요청 오류:", e);
            return { message: "요청 중 오류 발생" };
        }
    }

    // 알림 처리
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // 폼 데이터 세팅
    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
        };

        setValue("input[name='approvalId']", data.approvalId);
        setValue("input[name='empId']", data.empId);
        setValue("input[name='empName']", data.empName);
        setValue("input[name='dept']", data.dept);
        setValue("input[name='position']", data.position);
        setValue("input[name='vacationType']", data.vacationType);
        setValue("input[name='vacationStartDate']", data.vacationStartDate);
        setValue("input[name='vacationEndDate']", data.vacationEndDate);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", data.confirmedDate);
        setValue("input[name='approvalStatus']", data.approvalStatus);
        setValue("textarea[name='rejectionReason']", data.rejectionReason);
    }

    // UI 상태 설정
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const isStatusValid = data.approvalStatusCode === "APV001";
        const isAuthValid = data.authCode === "ATH002";

        approveBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        rejectBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        approvalResultSection.style.display = isStatusValid ? "none" : "block";
    }
};

// 페이지 로드 후 초기화 및 부모창에 준비 완료 알림
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
