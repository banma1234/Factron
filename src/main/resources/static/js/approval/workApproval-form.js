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

    window.addEventListener("message", function (event) {
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
            requesterId,
            requesterName,
        } = data;

        if (approvalId) {
            window.receivedData = data;
            setUIState(data);
            setFormData(data);
            fetchWorkByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002");
        approveModal.hide();
        handleAlert(result);
    });

    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }
        document.querySelector("textarea[name='rejectionReason']").value = reason;

        const result = await sendApproval("APV003");
        rejectModal.hide();
        handleAlert(result);
    });

    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        const approvalId = form.querySelector("input[name='approvalId']").value;
        if (window.opener && !window.opener.closed) {
            if (typeof window.opener.refreshSingleApproval === "function") {
                window.opener.refreshSingleApproval(Number(approvalId));
            } else {
                window.opener.getData();
            }
        }
        window.close();
    });

    async function fetchWorkByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({ srhApprovalId: approvalId });
            const response = await fetch(`/api/work?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            });
            const result = await response.json();
            if (result.status === 200 && result.data?.length > 0) {
                setWorkFormData(result.data[0]);
            } else {
                console.warn("근무 정보가 없습니다.");
            }
        } catch (error) {
            console.error("근무 정보 조회 중 오류:", error);
        }
    }

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
                body: JSON.stringify(data),
            });
            return await res.json();
        } catch (e) {
            console.error("API 오류:", e);
            return { message: "요청 중 오류 발생" };
        }
    }

    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    function setWorkFormData(data) {
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='dept']").value = data.deptName || '';
        form.querySelector("input[name='position']").value = data.positionName || '';
        form.querySelector("input[name='workName']").value = data.workName || '';
        form.querySelector("input[name='workDate']").value = data.workDate || '';
        form.querySelector("input[name='workStartTime']").value = data.startTime || '';
        form.querySelector("input[name='workEndTime']").value = data.endTime || '';
    }

    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
            else console.warn(`Element not found: ${selector}`);
        };

        setValue("input[name='approvalId']", data.approvalId);
        setValue("input[name='apprTypeCode']", data.apprTypeCode);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", (data.confirmedDate || '').split(' ')[0]);
        setValue("input[name='approvalStatus']", data.approvalStatusName);

        // 요청자 정보 추가 (필요 시 input name 맞게 변경)
        setValue("input[name='publisherId']", data.requesterId);
        setValue("input[name='publisherName']", data.requesterName);

        const textarea = form.querySelector("textarea[name='rejectionReason']");
        if (textarea) textarea.value = data.rejectionReason || '';
    }

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

window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
