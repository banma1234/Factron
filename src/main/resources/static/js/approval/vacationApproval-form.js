const init = () => {
    const form = document.querySelector("form");
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    // 승인 확인 버튼
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    // 반려 확인 버튼
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");

    // 부모창에서 데이터 받아오기
    window.addEventListener("message", function(event) {
        const data = event.data;
        console.log("수신된 메시지:", data);

        if (!data || data?.source === 'react-devtools-content-script') return;

        const { approvalId, apprTypeCode,approvalStatusCode ,userId, authCode } = data;

        if (approvalId && userId && authCode) {
            console.log("받은 approvalId:", approvalId);
            console.log("받은 apprTypeCode:", apprTypeCode);
            console.log("받은 approvalStatusCode:", approvalStatusCode);
            console.log("받은 userId:", userId);
            console.log("받은 authCode:", authCode);

            window.receivedData = data; // 전체 데이터 저장
            // UI설정
            setUIState(data);
            // ✅ 전체 폼 데이터 세팅
            setFormData(data);

        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });


    //승인 확인 버튼
    confirmApproveBtn.addEventListener("click", async () => {
        console.log("승인 버튼 클릭");
        const result = await sendApproval("APV002"); // 승인
        handleAlert(result);
    });

    //반려 확인 버튼
    confirmRejectBtn.addEventListener("click", async () => {
        console.log("반려 버튼 클릭");
        const reason = document.getElementById("rejectReasonInput").value.trim();

        if (!reason) {
            alert("반려 사유를 입력해주세요.");
            return;
        }

        // 반려 사유를 텍스트박스에도 표시되게 함
        document.querySelector("textarea[name='rejectionReason']").value = reason;

        const result = await sendApproval("APV003"); // 반려
        handleAlert(result);
    });


    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모 창의 그리드 리프레시
        if (window.opener && !window.opener.closed) {
            window.opener.getData();
        }

        window.close();
    });

    /**
     * 승인/반려 API 전송 함수
     * @param {'APV002'|'APV003'} statusCode
     */
    async function sendApproval(statusCode) {
        const approvalId = form.querySelector("input[name='approvalId']").value;
        // const approverId = form.querySelector("input[name='approverId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;

        const data = {
            approvalId: Number(approvalId),
            approverId: window.receivedData?.userId || null,
            approvalType: window.receivedData?.apprTypeCode || null,
            approvalStatus: statusCode,
            rejectionReason: statusCode === "APV003" ? rejectionReason : null,
        };
        console.log(data)

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
            console.error(e);
            return { message: "요청 중 오류 발생" };
        }
    }

    // alert 처리
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // 폼에 데이터 세팅 함수
    // setFormData 함수 내부만 수정됨
    function setFormData(data) {
        const form = document.querySelector("form");

        form.querySelector("input[name='approvalId']").value = data.approvalId || '';
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='dept']").value = data.dept || '';
        form.querySelector("input[name='position']").value = data.position || '';
        form.querySelector("input[name='vacationType']").value = data.vacationType || '';
        form.querySelector("input[name='vacationStartDate']").value = data.vacationStartDate || '';
        form.querySelector("input[name='vacationEndDate']").value = data.vacationEndDate || '';
        form.querySelector("input[name='approverId']").value = data.approverId || '';
        form.querySelector("input[name='approverName']").value = data.approverName || '';
        form.querySelector("input[name='confirmedDate']").value = data.confirmedDate || '';
        form.querySelector("input[name='approvalStatus']").value = data.approvalStatus || '';
        form.querySelector("textarea[name='rejectionReason']").value = data.rejectionReason || '';
    }


    function setUIState(data) {
        const approveBtn = document.querySelector(".approveBtn");
        const rejectBtn = document.querySelector(".rejectBtn");
        const approvalResultSection = document.querySelector(".approval-result-section");

        const { approvalStatusCode, authCode } = data;

        // 결재 상태가 대기(APV001)이 아니거나 권한 코드가 승인권자(ATH002)가 아니면 버튼 숨김
        const isStatusValid = approvalStatusCode === "APV001";
        const isAuthValid = authCode === "ATH002";

        if (isStatusValid && isAuthValid) {
            approveBtn.style.display = "inline-block";
            rejectBtn.style.display = "inline-block";
        } else {
            approveBtn.style.display = "none";
            rejectBtn.style.display = "none";
        }

        // 결재 상태가 대기중(APV001)일 경우, 결재 결과 섹션 숨김
        if (approvalStatusCode === "APV001") {
            approvalResultSection.style.display = "none";
        } else {
            approvalResultSection.style.display = "block";
        }
    }


};

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};

