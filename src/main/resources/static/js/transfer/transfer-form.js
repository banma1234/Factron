const init = () => {
    const form = document.querySelector("form");
    const saveBtn = form.querySelector("button.saveBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let data = {}; // 저장 데이터

    // 발령 구분 선택
    form.querySelector("select[name='trsTypeCode']").addEventListener('change', (e) => {
        if (e.target.value === 'TRS001') {
            // 승진 - 직급 display
            document.querySelector("select[name='currDeptCode']").value = '';
            document.querySelector("select[name='currPositionCode']").value = '';
            form.querySelector(".trsDept").classList.add('d-none');
            form.querySelector(".trsPos").classList.remove('d-none');
        } else {
            // 전보 - 부서 display
            document.querySelector("select[name='currDeptCode']").value = '';
            document.querySelector("select[name='currPositionCode']").value = '';
            form.querySelector(".trsDept").classList.remove('d-none');
            form.querySelector(".trsPos").classList.add('d-none');
        }
    });

    // 저장 버튼
    saveBtn.addEventListener("click", () => {
        const empId = form.querySelector("input[name='empId']").value;
        const name = form.querySelector("input[name='empName']").value;
        const prevDeptCode = form.querySelector("select[name='deptCode']").value;
        const trsTypeCode = form.querySelector("select[name='trsTypeCode']").value;
        const currDeptCode = form.querySelector("select[name='currDeptCode']").value;
        const currPositionCode = form.querySelector("select[name='currPositionCode']").value;

        // validation
        if (!empId) {
            alert("발령자를 선택해주세요.");
            return;
        }
        if (!trsTypeCode) {
            alert("발령 구분을 선택해주세요.");
            return;
        }
        if (trsTypeCode === 'TRS001' && !currPositionCode) { // 승진
            alert("발령 직급을 선택해주세요.");
            return;
        }
        if (trsTypeCode === 'TRS002' && !currDeptCode) { // 전보
            alert("발령 부서를 선택해주세요.");
            return;
        }

        // fetch data
        data = {
            requesterId: user.id,
            empId,
            trsTypeCode,
            prevDeptCode,
            currDeptCode,
            currPositionCode,
        };

        document.querySelector(".confirmModal .modal-body").innerHTML =
            `${name} 님을 발령하시겠습니까?`;
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
            const res = await fetch(`/api/trans`, {
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

    // 공통코드 세팅
    setSelectBox("TRS", "trsTypeCode");
    setSelectBox("DEP", "deptCode");
    setSelectBox("DEP", "currDeptCode");
    setSelectBox("POS", "positionCode");
    setSelectBox("POS", "currPositionCode");
};

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};