const init = () => {
    // ✅ DOM 요소들 가져오기
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");

    // ✅ 부모 창으로부터 메시지를 수신했을 때 실행
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
        } = data;

        if (approvalId) {
            // 전역에 저장
            window.receivedData = data;

            // UI 및 폼 데이터 설정
            setUIState(data);
            setFormData(data);
            fetchWorkByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    // ✅ 승인 버튼 클릭 시 처리
    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002");  // 승인 코드
        handleAlert(result);
    });

    // ✅ 반려 버튼 클릭 시 처리
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }

        // textarea에 반려사유 세팅
        document.querySelector("textarea[name='rejectionReason']").value = reason;

        const result = await sendApproval("APV003");  // 반려 코드
        handleAlert(result);
    });

    // ✅ 알림 모달 확인 버튼 클릭 시
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모 창 새로고침
        if (window.opener && !window.opener.closed) {
            window.opener.getData();
        }

        // 현재 창 닫기
        window.close();
    });

    // ✅ 근무 정보 조회 API 호출
    async function fetchWorkByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({
                srhApprovalId: approvalId,
            });

            const response = await fetch(`/api/work?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                },
            });

            const result = await response.json();

            if (result.status === 200 && result.data?.length > 0) {
                const workData = result.data[0];
                setWorkFormData(workData);
            } else {
                console.warn("근무 정보가 없습니다.");
            }
        } catch (error) {
            console.error("근무 정보 조회 중 오류:", error);
        }
    }

    // ✅ 승인 또는 반려 요청 전송
    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;

        const data = {
            approvalId: Number(approvalId),
            approverId: user.id || null,
            approvalType: window.receivedData?.apprTypeCode || null,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
        };

        try {
            const res = await fetch("/api/approval", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
            });

            return await res.json();
        } catch (e) {
            console.error("API 오류:", e);
            return { message: "요청 중 오류 발생" };
        }
    }

    // ✅ 승인/반려 후 알림 모달 표시
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent =
            res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // ✅ 근무 폼 데이터 세팅
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

    // ✅ 기본 결재 폼 데이터 세팅
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

        const textarea = form.querySelector("textarea[name='rejectionReason']");
        if (textarea) textarea.value = data.rejectionReason || '';
    }

    // ✅ 버튼 및 상태 UI 설정
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const isStatusValid = data.approvalStatusCode === "APV001"; // 결재대기 상태
        const isAuthValid = user.authCode === "ATH002"; // 결재권자 권한 여부

        approveBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        rejectBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        approvalResultSection.style.display = isStatusValid ? "none" : "block";
    }
};

// ✅ 페이지 로드 시 초기화 및 부모창에 ready 메시지 전송
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
