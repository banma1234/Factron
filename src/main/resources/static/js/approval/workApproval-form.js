const init = () => {
    // ✅ 주요 DOM 요소 선택 및 Bootstrap 모달 초기화
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");
    const approveModal = new bootstrap.Modal(document.querySelector(".approveModal"));
    const rejectModal = new bootstrap.Modal(document.querySelector(".rejectModal"));

    // ✅ 승인 버튼 클릭 시 승인 모달 표시
    document.querySelector(".approveBtn").addEventListener("click", () => approveModal.show());
    // ✅ 반려 버튼 클릭 시 반려 모달 표시
    document.querySelector(".rejectBtn").addEventListener("click", () => rejectModal.show());

    // ✅ 부모창에서 데이터 수신 이벤트 처리
    window.addEventListener("message", function (event) {
        const data = event.data;
        if (!data || data?.source === 'react-devtools-content-script') return;

        // 데이터 구조분해 할당
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
            // 전역 저장, UI 상태 및 폼 데이터 세팅, 근무 정보 조회
            window.receivedData = data;
            setUIState(data);
            setFormData(data);
            fetchWorkByApprovalId(approvalId);
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    // ✅ 승인 확정 버튼 클릭 시 승인 API 호출
    confirmApproveBtn.addEventListener("click", async () => {
        const result = await sendApproval("APV002"); // 승인 코드
        approveModal.hide();
        handleAlert(result);
    });

    // ✅ 반려 확정 버튼 클릭 시 반려 API 호출
    confirmRejectBtn.addEventListener("click", async () => {
        const reason = document.querySelector("textarea[name='rejectReasonInput']").value.trim();
        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }
        // 반려 사유 폼에 입력
        document.querySelector("textarea[name='rejectionReason']").value = reason;

        const result = await sendApproval("APV003"); // 반려 코드
        rejectModal.hide();
        handleAlert(result);
    });

    // ✅ 알림 모달 확인 클릭 시 닫고 부모창 데이터 갱신 후 현재 창 닫기
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

    // ✅ 승인 ID로 근무 정보 조회 함수
    async function fetchWorkByApprovalId(approvalId) {
        try {
            const params = new URLSearchParams({ srhApprovalId: approvalId });
            const response = await fetch(`/api/work?${params.toString()}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
            });
            const result = await response.json();
            if (result.status === 200 && result.data?.length > 0) {
                setWorkFormData(result.data[0]); // 근무 폼 데이터 세팅
            } else {
                console.warn("근무 정보가 없습니다.");
            }
        } catch (error) {
            console.error("근무 정보 조회 중 오류:", error);
        }
    }

    // ✅ 승인 또는 반려 상태 전송 API 호출 함수
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

    // ✅ 결과 알림 모달 표시 함수
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // ✅ 근무 정보 폼 데이터 세팅 함수
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

    // ✅ 결재 기본 폼 데이터 세팅 함수
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

        // 요청자 정보 세팅 (input name 필요에 따라 변경 가능)
        setValue("input[name='publisherId']", data.requesterId);
        setValue("input[name='publisherName']", data.requesterName);

        const textarea = form.querySelector("textarea[name='rejectionReason']");
        if (textarea) textarea.value = data.rejectionReason || '';
    }

    // ✅ UI 상태 설정 (승인/반려 버튼 표시 여부 및 결과 섹션 토글)
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const isStatusValid = data.approvalStatusCode === "APV001";  // 승인대기 상태인지 확인
        const isAuthValid = user.authCode === "ATH002" || user.authCode === "ATH003";  // 권한 확인

        approveBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        rejectBtn.style.display = (isStatusValid && isAuthValid) ? "inline-block" : "none";
        approvalResultSection.style.display = isStatusValid ? "none" : "block";
    }
};

// ✅ 페이지 로드 시 초기화 및 부모창에 준비 완료 메시지 전송
window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
