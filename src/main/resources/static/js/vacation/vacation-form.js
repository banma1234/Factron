const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.vacationSaveBtn");
    const confirmModal = new bootstrap.Modal(document.querySelector(".vacationConfirmModal"));
    const confirmBtn = document.querySelector(".vacationConfirmBtn");
    const alertModal = new bootstrap.Modal(document.querySelector(".vacationAlertModal"));
    const alertBtn = document.querySelector(".vacationAlertBtn");
    const today = getKoreaToday();
    let data = {};

    // 초기 값 세팅
    form.querySelector("input[name='empId']").value = user.id;
    form.querySelector("input[name='empName']").value = user.name;
    form.querySelector("input[name='startDate']").setAttribute("min", today);
    form.querySelector("input[name='endDate']").setAttribute("min", today);

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const empId = form.querySelector("input[name='empId']").value;
        const empName = form.querySelector("input[name='empName']").value;
        const startDate = form.querySelector("input[name='startDate']").value;
        const endDate = form.querySelector("input[name='endDate']").value;
        const remark = form.querySelector("textarea[name='remark']").value;

        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!startDate || !endDate) {
            alert("시작 날짜와 종료 날짜를 모두 입력해주세요.");
            return;
        }
        if (!dateRegex.test(startDate) || !dateRegex.test(endDate)) {
            alert("날짜 형식이 올바르지 않습니다.");
            return;
        }
        if (startDate > endDate) {
            alert("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
            return;
        }

        data = {
            empId,
            startDate,
            endDate,
            remark
        };

        document.querySelector(".vacationConfirmModal .modal-body").innerHTML =
            `${empName} 님<br/>${startDate} ~ ${endDate}<br/>휴가를 신청하시겠습니까?`;
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
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
            });
            return res.json();

        } catch (e) {
            console.error(e);
        }
    }
};

window.onload = () => {
    init();
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};