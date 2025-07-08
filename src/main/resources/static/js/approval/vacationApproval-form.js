const init = () => {
    // ✅ 주요 DOM 요소 선택 및 Bootstrap 모달 초기화
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));

    // ✅ 승인 버튼 클릭 시 승인 모달 열기
    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    // ✅ 반려 버튼 클릭 시 반려 모달 열기
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    // ✅ 부모창으로부터 메시지 수신 (결재 데이터)
    window.addEventListener("message", (event) => {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        // 수신 데이터 구조분해 할당
        const {
            approvalId,
            apprTypeCode,
            approvalStatusCode,
            approverName,
            approvalStatusName,
            approverId,
            rejectionReason,
            confirmedDate,
            requestedAt,
            requesterId,
            requesterName,
        } = data;

        if (approvalId) {
            // 전역 저장 및 UI/폼 세팅, 휴가 정보 조회
            window.receivedData = data;
            setUIState(data);
            setFormData(data);
            fetchVacationByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    // ✅ 승인 확정 버튼 클릭 시 승인 요청
    confirmApproveBtn.addEventListener("click", async () => {
        approveModal.hide();
        const result = await sendApproval("APV002"); // 승인 상태 코드
        handleAlert(result);
    });

    // ✅ 반려 확정 버튼 클릭 시 반려 요청
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }
        rejectModal.hide();
        form.querySelector("textarea[name='rejectionReason']").value = reason;
        const result = await sendApproval("APV003"); // 반려 상태 코드
        handleAlert(result);
    });

    // ✅ 알림 모달 확인 버튼 클릭 시 알림 닫고 부모창 데이터 갱신 및 현재 창 닫기
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

    // ✅ 승인 ID로 휴가 정보 조회 API 호출 함수
    async function fetchVacationByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({ srhApprovalId: approvalId });
            const response = await fetch(`/api/vacation?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            });
            const result = await response.json();
            if (result.status === 200 && result.data?.length > 0) {
                setVacationFormData(result.data[0]); // 휴가 폼 데이터 세팅
            } else {
                console.warn("휴가 정보가 없습니다.");
            }
        } catch (error) {
            console.error("휴가 정보 조회 중 오류:", error);
        }
    }

    // ✅ 승인 또는 반려 상태로 API 전송 함수
    async function sendApproval(approvalStatusCode) {
        const approvalId = Number(form.querySelector("input[name='approvalId']").value);
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;
        const data = {
            approvalId,
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

    // ✅ 알림 모달에 메시지 표시 및 표시
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // ✅ UI 상태 설정 (승인/반려 버튼 표시 여부, 결과 영역)
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        // 승인대기 상태(APV001)이고 권한 코드 ATH002 일 경우 버튼 노출
        const isStatusValid = data.approvalStatusCode === "APV001";
        const isAuthValid = user.authCode === "ATH002" || user.authCode === "ATH003";  // 권한 확인

        approveBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        rejectBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        approvalResultSection.style.display = isStatusValid ? "none" : "block";
    }

    // ✅ 기본 결재 폼 데이터 세팅 함수
    function setFormData(data) {
        const setValue = (selector, value) => {
            const el = form.querySelector(selector);
            if (el) el.value = value || "";
            else console.warn(`Element not found: ${selector}`);
        };

        setValue("input[name='approvalId']", data.approvalId);
        setValue("input[name='approverId']", data.approverId);
        setValue("input[name='approverName']", data.approverName);
        setValue("input[name='confirmedDate']", (data.confirmedDate || "").split(" ")[0]);
        setValue("input[name='approvalStatus']", data.approvalStatusName);
        setValue("input[name='applicationDate']", (data.requestedAt || "").split(" ")[0]);

        // 요청자 정보 세팅
        setValue("input[name='publisherId']", data.requesterId);
        setValue("input[name='publisherName']", data.requesterName);

        const textarea = form.querySelector("textarea[name='rejectionReason']");
        if (textarea) textarea.value = data.rejectionReason || "";
    }

    // ✅ 휴가 상세 폼 데이터 세팅 함수
    function setVacationFormData(data) {
        form.querySelector("input[name='empId']").value = data.empId || "";
        form.querySelector("input[name='empName']").value = data.empName || "";
        form.querySelector("input[name='deptName']").value = data.deptName || "";
        form.querySelector("input[name='positionName']").value = data.positionName || "";
        form.querySelector("input[name='vacationStartDate']").value = data.vacationStartDate || "";
        form.querySelector("input[name='vacationEndDate']").value = data.vacationEndDate || "";
        form.querySelector("input[name='remark']").value = data.remark || "";
    }
};

// ✅ 페이지 로드 시 초기화 및 부모창에 준비 완료 메시지 전송
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
