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
        console.log("수신된 메시지:", data);

        // react-devtools 등의 내부 메시지 무시
        if (!data || data?.source === 'react-devtools-content-script') return;

        // 구조 분해로 필요한 데이터 추출
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
            userId,
            authCode
        } = data;

        // 필수 데이터가 존재할 때만 처리
        if (approvalId && userId && authCode) {
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

            // 전역에 저장
            window.receivedData = data;

            // UI 및 폼 데이터 설정
            setUIState(data);
            setFormData(data);

            // 발령 정보 조회
            fetchTransferByApprovalId(approvalId);
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

    // ✅ 발령 정보 조회 API 호출
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

    // ✅ 승인 또는 반려 요청 전송
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

    // ✅ 승인/반려 후 알림 모달 표시
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // ✅ 발령 폼 데이터 세팅
    function setTransferFormData(data) {
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='dept']").value = data.prevDeptName || '';
        form.querySelector("input[name='currDept']").value = data.currDeptName || '';
        form.querySelector("input[name='currPosition']").value = data.positionName || '';
        form.querySelector("input[name='transType']").value = data.trsTypeName || '';
        form.querySelector("input[name='date']").value = data.transferDate || '';
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

        // 요청자 정보 세팅
        setValue("input[name='publisherId']", data.requesterId);
        setValue("input[name='publisherName']", data.requesterName);
    }

    // ✅ 버튼 및 상태 UI 설정
    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const { approvalStatusCode, authCode } = data;
        const isStatusValid = approvalStatusCode === "APV001"; // 결재대기 상태
        const isAuthValid = authCode === "ATH002"; // 권한이 결재권자일 경우

        // 조건 충족 시 버튼 보이기
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
