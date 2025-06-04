const init = () => {
    const form = document.querySelector("form");
    const btnModify = form.querySelector("button.modBtn");
    // const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    // const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    // const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    // const alertBtn = document.getElementsByClassName("alertBtn")[0];
    const alertModal = new bootstrap.Modal(document.querySelector(".alertModal"));
    const alertBtn = document.querySelector(".alertBtn");
    let isEditMode = false; // 수정모드
    // 승인 확인 버튼
    const confirmApproveBtn = document.querySelector(".confirmApproveBtn");
    // 반려 확인 버튼
    const confirmRejectBtn = document.querySelector(".confirmRejectBtn");

    // 부모창에서 데이터 받아오기
    window.addEventListener("message", function(event) {
        const data = event.data;
        console.log("수신된 메시지:", data);

        if (!data || data?.source === 'react-devtools-content-script') return;

        // approvalId, userId, authCode만 받아서 처리
        const { approvalId, apprTypeCode, userId, authCode } = data;

        if (approvalId && userId && authCode) {
            console.log("받은 approvalId:", approvalId);
            console.log("받은 userId:", userId);
            console.log("받은 authCode:", authCode);

            // 필요하면 전역 변수로 저장하거나 UI에 반영 가능
            window.receivedData = { approvalId, apprTypeCode,userId, authCode };

            // 예: approvalId만 폼 input에 넣기 (필요하면)
            const approvalInput = document.querySelector("input[name='approvalId']");
            if (approvalInput) approvalInput.value = approvalId;

            // 추가 동작 필요 시 여기서 처리
        } else {
            console.warn("필요한 데이터가 부족합니다.");
        }
    });

    //승인 확인 버튼
    confirmApproveBtn.addEventListener("click", async () => {
        console.log("승인 버튼 클릭");
        console.log("approverId:", form.querySelector("input[name='approverId']").value);
        const result = await sendApproval("APV002"); // 승인
        handleAlert(result);
    });

    //반려 확인 버튼
    confirmRejectBtn.addEventListener("click", async () => {
        console.log("반려 버튼 클릭");
        console.log("approverId:", form.querySelector("input[name='approverId']").value);
        const result = await sendApproval("APV003"); // 반려
        handleAlert(result);
    });


    // 주소 input 클릭
    form.querySelector("input[name='address']").addEventListener("click", (e) => {
        handleAddressClick(e);
    });

    // 저장 버튼
    btnModify.addEventListener("click", () => {
        if (!isEditMode) {
            // 수정모드 아닌 경우
            toggleFormDisabled(false);
            btnModify.textContent = "확인";
            isEditMode = true;

        } else {
            // 수정모드인 경우
            confirmModal.show();
        }
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // 폼 disable 변경
    const toggleFormDisabled = (isDisabled) => {
        const fields = document.querySelectorAll("input, select, textarea");
        fields.forEach(el => {
            if (el.name !== 'rrnBack') {
                el.disabled = false;          // disabled를 false로
                el.readOnly = isDisabled;     // 읽기 전용 속성을 토글
            }
        });
    };


    // 주소 입력
    function handleAddressClick(event) {
        if (event.target.disabled) return;

        new daum.Postcode({
            oncomplete: function(data) {
                const fullAddr = data.address; // 도로명 주소
                document.querySelector("input[name='address']").value = fullAddr;
            }
        }).open();
    }

    // confirm 모달 확인 버튼
    confirmEditBtn.addEventListener("click", () => {
        saveData().then(res => {
            if(res.status === 200) {
                // ...
            } else {
                // ...
            }

            // 수정모드 종료
            toggleFormDisabled(true);
            btnModify.textContent = "저장";
            isEditMode = false;

            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();
        });
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
        const approverId = form.querySelector("input[name='approverId']").value;
        const rejectionReason = form.querySelector("textarea[name='rejectionReason']").value;

        const data = {
            approvalId: Number(approvalId),
            approverId: Number(approverId),
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
            console.error(e);
            return { message: "요청 중 오류 발생" };
        }
    }

    // 저장
    async function saveData() {
        // validation

        // fetch data
        const data = {
            id: form.querySelector("input[name='age']").value,
            name: form.querySelector("input[name='name']").value,
            birth: form.querySelector("input[name='birth']").value,
            chkType: null,
            regDate: null,
            address: form.querySelector("textarea[name='remark']").value,
            // ...
        };

        try {
            const res = await fetch(`/testList`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }

    // alert 처리
    function handleAlert(res) {
        document.querySelector(".alertModal .modal-body").textContent = res?.message || "처리가 완료되었습니다.";
        alertModal.show();
    }

    // 폼에 데이터 세팅 함수
    function setFormData(data) {
        const form = document.querySelector("form");

        form.querySelector("input[name='approvalId']").value = data.approvalId || '';
        form.querySelector("input[name='empId']").value = data.empId || '';
        form.querySelector("input[name='empName']").value = data.empName || '';
        form.querySelector("input[name='dept']").value = data.dept || '';
        form.querySelector("input[name='position']").value = data.position || '';
        form.querySelector("input[name='workName']").value = data.workName || '';
        form.querySelector("input[name='workDate']").value = data.workDate || '';
        form.querySelector("input[name='workStartTime']").value = data.workStartTime || '';
        form.querySelector("input[name='workEndTime']").value = data.workEndTime || '';
        form.querySelector("input[name='approverId']").value = data.approverId || '';
        form.querySelector("input[name='approverName']").value = data.approverName || '';
        form.querySelector("input[name='confirmedDate']").value = data.confirmedDate || '';
        form.querySelector("input[name='approvalStatus']").value = data.approvalStatus || '';
        form.querySelector("textarea[name='rejectionReason']").value = data.rejectionReason || '';
    }

};

window.onload = () => {
    console.log('자식 창: 로드 완료');
    if (window.opener) {
        console.log('자식 창: 부모창이 존재함, ready 메시지 전송');
        window.opener.postMessage("ready", "*");
    } else {
        console.log('자식 창: 부모창이 없음');
    }

    init();  // 여기에 init 함수 호출 추가
};

