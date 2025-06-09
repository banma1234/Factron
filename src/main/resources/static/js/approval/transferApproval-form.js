const init = () => {
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");

    window.addEventListener("message", function (event) {
        const data = event.data;
        console.log("수신된 메시지:", data);

        if (!data || data?.source === 'react-devtools-content-script') return;

        const { approvalId,
                apprTypeCode,
                approvalStatusCode,
                approverName,
                approvalStatusName,
                approverId,
                rejectionReason,
                confirmedDate,
                requesterId,
                requesterName,
                userId,
                authCode } = data;

        if (approvalId && userId && authCode) {
            console.log("받은 approvalId:", approvalId);
            console.log("받은 approvalId:", approvalId);
            console.log("받은 apprTypeCode:", apprTypeCode);
            console.log("받은 approvalStatusCode:", approvalStatusCode);
            console.log("받은 approvalStatusName:", approvalStatusName);
            console.log("받은 userId:", userId);
            console.log("받은 approverName:", approverName);
            console.log("받은 approverId:", approverId);
            console.log("받은 rejectionReason:", rejectionReason);
            console.log("받은 confirmedDate:", confirmedDate);
            console.log("받은 requesterId:", requesterId);
            console.log("받은 requesterName:", requesterName);

            window.receivedData = data;
            setUIState(data);
            setFormData(data);

            // ✅ 발령 정보 조회 호출
            fetchTransferByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002");
        handleAlert(result);
    });

    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.getElementById("rejectReasonInput").value.trim();
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

    async function fetchTransferByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({
                srhApprovalId: approvalId,
            });

            const response = await fetch(`/api/trans?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            const result = await response.json();
            console.log("발령 조회 결과:", result);

            if (result.status === 200 && result.data?.length > 0) {
                const transferData = result.data[0];
                setTransferFormData(transferData);
            } else {
                console.warn("발령 정보가 없습니다.");
            }
        } catch (error) {
            console.error("발령 정보 조회 중 오류:", error);
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
        console.log(data);

        try {
            const res = await fetch("/api/approval", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
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

    function setTransferFormData(data) {
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='dept']").value = data.prevDeptName || '';
        // form.querySelector("input[name='position']").value = data.positionName || '';
        form.querySelector("input[name='currDept']").value = data.currDeptName || '';
        form.querySelector("input[name='currPosition']").value = data.positionName || '';
        form.querySelector("input[name='transType']").value = data.trsTypeName || '';
        form.querySelector("input[name='date']").value = data.transferDate || '';
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

        // ✅ 여기에 발행자 정보 추가
        setValue("input[name='publisherId']", data.requesterId);
        setValue("input[name='publisherName']", data.requesterName);
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
