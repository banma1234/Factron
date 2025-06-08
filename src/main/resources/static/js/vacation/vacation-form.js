const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.vacationSaveBtn");
    const confirmModal = new bootstrap.Modal(document.querySelector(".vacationConfirmModal"));
    const confirmBtn = document.querySelector(".vacationConfirmBtn");
    const alertModal = new bootstrap.Modal(document.querySelector(".vacationAlertModal"));
    const alertBtn = document.querySelector(".vacationAlertBtn");

    let data = {};

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const empId = document.getElementById("vacationEmpIdHidden").value;
        const empName = form.querySelector("input[name='empName']").value.trim();
        const vacationStartDate = form.querySelector("input[name='vacationStartDate']").value;
        const vacationEndDate = form.querySelector("input[name='vacationEndDate']").value;
        const remark = form.querySelector("textarea[name='remark']").value;

        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!vacationStartDate || !vacationEndDate) {
            alert("시작 날짜와 종료 날짜를 모두 입력해주세요.");
            return;
        }
        if (!dateRegex.test(vacationStartDate) || !dateRegex.test(vacationEndDate)) {
            alert("날짜 형식이 올바르지 않습니다.");
            return;
        }
        if (vacationStartDate > vacationEndDate) {
            alert("시작 날짜는 종료 날짜보다 빠르거나 같아야 합니다.");
            return;
        }

        data = {
            vacationStartDate,
            vacationEndDate,
            remark
        };

        document.querySelector(".vacationConfirmModal .modal-body").innerHTML =
            `${empName} 님<br/>${vacationStartDate} ~ ${vacationEndDate}<br/>휴가를 신청하시겠습니까?`;
        confirmModal.show();
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // confirm 모달 확인 버튼
    confirmBtn.addEventListener("click", () => {
        saveData().then(res => {
            confirmModal.hide();
            document.querySelector(".vacationAlertModal .modal-body").textContent = res.message;
            alertModal.show();
        });
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        window.close();
    });

    // 저장 요청
    async function saveData() {
        try {
            const res = await fetch("/api/vacation", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "empId": document.getElementById("vacationEmpIdHidden").value
                },
                body: JSON.stringify(data),
            });
            return res.json();

        } catch (e) {
            console.error(e);
            return { status: 'fail', message: '요청 중 오류 발생' };
        }
    }
};

window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};