const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.vacationSaveBtn");
    const confirmModal = new bootstrap.Modal(document.querySelector(".vacationConfirmModal"));
    const confirmBtn = document.querySelector(".vacationConfirmBtn");
    const alertModal = new bootstrap.Modal(document.querySelector(".vacationAlertModal"));
    const alertBtn = document.querySelector(".vacationAlertBtn");
    const today = new Date().toISOString().split("T")[0];

    let data = {};

    form.querySelector("input[name='startTime']").setAttribute("min", today);
    form.querySelector("input[name='endTime']").setAttribute("min", today);

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        // 초기 값 세팅
        form.querySelector("input[name='empId']").value = data.empId || "";
        form.querySelector("input[name='empName']").value = data.empName || "";
    });

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const empId = form.querySelector("input[name='empId']").value.trim();
        const empName = form.querySelector("input[name='empName']").value.trim();
        const startTime = form.querySelector("input[name='startTime']").value;
        const endTime = form.querySelector("input[name='endTime']").value;
        const remark = form.querySelector("textarea[name='remark']").value;

        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!startTime || !endTime) {
            alert("시작 날짜와 종료 날짜를 모두 입력해주세요.");
            return;
        }
        if (!dateRegex.test(startTime) || !dateRegex.test(endTime)) {
            alert("날짜 형식이 올바르지 않습니다.");
            return;
        }
        if (startTime > endTime) {
            alert("시작 날짜는 종료 날짜보다 빠르거나 같아야 합니다.");
            return;
        }

        data = {
            empId,
            startTime,
            endTime,
            remark
        };

        document.querySelector(".vacationConfirmModal .modal-body").innerHTML =
            `${empName} 님<br/>${startTime} ~ ${endTime}<br/>휴가를 신청하시겠습니까?`;
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