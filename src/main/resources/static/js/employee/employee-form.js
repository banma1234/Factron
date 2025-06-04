const init = () => {
    const form = document.querySelector("form");
    const btnModify = form.querySelector("button.modBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let isEditMode = false; // 수정모드

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        // if (event.origin !== "http://localhost:8080") return;

        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;
        // 값 세팅
        form.querySelector("input[name='name']").value = data.name || "";
        form.querySelector("input[name='empId']").value = data.empId || "";
        form.querySelector("input[name='rrn']").value = data.rrn || "";
        // form.querySelector("input[name='gender']").value = data.gender || "";
        form.querySelector("input[name='email']").value = data.email || "";
        form.querySelector("input[name='address']").value = data.address || "";
        form.querySelector("input[name='quitDate']").value = data.quitDate || "";
        form.querySelector("input[name='status']").value = data.empIsActive || "";
        form.querySelector("input[name='joinDate']").value = data.joinedDate || "";
        form.querySelector("input[name='phone']").value = data.phone || "";
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
            // 주민번호 뒷자리 제외하고 disable 해제
            if (el.name !== 'rrnBack') {
                el.disabled = isDisabled;
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
};

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
