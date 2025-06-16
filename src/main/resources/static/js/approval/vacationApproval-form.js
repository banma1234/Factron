const init = () => {
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");

    window.addEventListener("message", function (event) {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        window.receivedData = data;

        setUIState(data);
        setFormData(data);
        fetchVacationByApprovalId(data.approvalId);
    });

    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002");
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
        handleAlert(result);
    });

    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        if (window.opener && !window.opener.closed) {
            window.opener.getData();
        }
        window.close();
    });

    // ✅ 휴가 정보 조회 API 호출
    async function fetchVacationByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({
                srhApprovalId: approvalId,
            });

            const response = await fetch(`/api/vacation?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            const result = await response.json();
            console.log("휴가 조회 결과:", result);

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

        try {
            const res = await fetch("/api/approval", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
            });

            return await res.json();
        } catch (e) {
            console.error(e);
            return { message: "요청 중 오류 발생" };
        }
    }

    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    function setVacationFormData(data) {
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='deptName']").value = data.deptName || '';       // 수정됨
        form.querySelector("input[name='positionName']").value = data.positionName || ''; // 수정됨
        form.querySelector("input[name='vacationStartDate']").value = data.vacationStartDate || '';
        form.querySelector("input[name='vacationEndDate']").value = data.vacationEndDate || '';
        form.querySelector("input[name='date']").value = data.requestDate || ''; // 신청 일자
        form.querySelector("input[name='remark']").value = data.remark || '';   // 비고
    }


    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || '';
        };

        setValue("input[name='approvalId']", data.approvalId);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", data.confirmedDate);
        setValue("input[name='approvalStatus']", data.approvalStatusName);
        setValue("textarea[name='rejectionReason']", data.rejectionReason);
    }

    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const { approvalStatusCode, authCode } = data;
        const isStatusValid = approvalStatusCode === "APV001";
        const isAuthValid = authCode === "ATH002";

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
