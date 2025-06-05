const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.saveBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let data = {}; // 저장 데이터

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        // 초기 값 세팅
        form.querySelector("input[name='empId']").value = data.empId || "";
        form.querySelector("input[name='empName']").value = data.empName || "";
        const selectElement = document.querySelector("select[name='workCode']");
        for(const work of data.workCodeList) {
            const optionElement = document.createElement("option");
            optionElement.value = work.detailCode;  // 코드
            optionElement.textContent = work.name;  // 이름
            selectElement.appendChild(optionElement);
        }
    });

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const empId = form.querySelector("input[name='empId']").value.trim();
        const empName = form.querySelector("input[name='empName']").value.trim();
        const workDate = form.querySelector("input[name='workDate']").value;
        const workCodeEl = form.querySelector("select[name='workCode']");
        const workCode = workCodeEl.value;
        const workName = workCodeEl.options[workCodeEl.selectedIndex].text;
        const startTime = form.querySelector("input[name='startTime']").value;
        const endTime = form.querySelector("input[name='endTime']").value;

        // validation
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        const timeRegex = /^([01]\d|2[0-3]):[0-5]\d$/;
        if (!workDate) {
            alert("근무 날짜를 선택해주세요.");
            return;
        }
        if (!dateRegex.test(workDate) || isNaN(Date.parse(workDate))) {
            alert("근무 날짜 형식이 올바르지 않습니다.");
            return;
        }
        if (!workCode) {
            alert("근무 유형을 선택해주세요.");
            return;
        }
        if (!startTime || !endTime) {
            alert("근무 시작 및 종료 시간을 모두 입력해주세요.");
            return;
        }
        if (!timeRegex.test(startTime) || !timeRegex.test(endTime)) {
            alert("시간 형식이 올바르지 않습니다.");
            return;
        }
        if (startTime >= endTime) {
            alert("근무 시작 시간은 종료 시간보다 빨라야 합니다.");
            return;
        }

        // fetch data
        data = {
            empId,
            workDate,
            workCode,
            startTime,
            endTime,
        };

        document.querySelector(".confirmModal .modal-body").innerHTML =
            `${empName} 님<br/>${workDate} ${startTime}-${endTime}<br/>${workName} 을(를) 신청하시겠습니까?`;
        confirmModal.show();
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // confirm 모달 확인 버튼
    confirmEditBtn.addEventListener("click", () => {
        saveData().then(res => {
            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();
        });
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        window.close();
    });

    // 저장
    async function saveData() {
        try {
            const res = await fetch(`/api/work`, {
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
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};