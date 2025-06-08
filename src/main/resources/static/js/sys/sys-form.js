const init = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const urlParams = new URLSearchParams(location.search);
    const confirmEditBtn = document.querySelector("button[name='confirmEditBtn']");
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let isEditMode = false; // 수정모드
    const target = urlParams.get("target");

    if (target === "main") {
        form.querySelector("input[name='detail_code']").disabled = true;
    } else if (target === "detail") {
        form.querySelector("input[name='main_code']").disabled = true;
    }

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        // if (event.origin !== "http://localhost:8080") return;
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        // 값 세팅
        form.querySelector("input[name='main_code']").value = data.main_code;
        form.querySelector("input[name='detail_code']").value = data.detail_code.substring(3,6);
        form.querySelector("input[name='name']").value = data.name;
        form.querySelector("input[name='is_active']").value = data.is_active;

        isEditMode = true;
    });


    form.querySelector("button.btn-primary").addEventListener("click", () => {
        confirmModal.show();

        saveData().then(res => {
            confirmEditBtn.addEventListener('click', e => {
                console.log("hit!")

                saveData().then(res => {
                    isEditMode = false;
                    confirmModal.hide();

                    document.querySelector(".alertModal .modal-body").textContent = res.message;
                    alertModal.show();
                });

                // window.close();
            })
        })
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // // 폼 disable 변경
    // const toggleFormDisabled = (isDisabled) => {
    //     const fields = document.querySelectorAll("input, select, textarea");
    //     fields.forEach(el => {
    //         // 주민번호 뒷자리 제외하고 disable 해제
    //         if (el.name !== 'rrnBack') {
    //             el.disabled = isDisabled;
    //         }
    //     });
    // };

    // 저장
    async function saveData() {
        // fetch data
        const data = {
            main_code: form.querySelector("input[name='main_code']").value,
            detail_code: form.querySelector("input[name='detail_code']").value || "",
            name: form.querySelector("input[name='name']").value,
            is_active: form.querySelector("select[name='is_active']").value
        };

        if (target === "main") {
            console.log("main");
            delete data.detail_code;
        }

        try {
            const res = await fetch(`/api/sys/${target}`, {
                method: `${isEditMode ? "PUT" : "POST"}`,
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

    // // confirm 모달 확인 버튼
    // btnModify.addEventListener("click", () => {
    //     // confirmModal.show();
    //
    //     saveData().then(res => {
    //         window.close();
    //     })
    //
    //     // confirmEditBtn.addEventListener('click', e => {
    //     //     console.log("hit!")
    //     //
    //     //     saveData().then(res => {
    //     //         isEditMode = false;
    //     //         confirmModal.hide();
    //     //
    //     //         document.querySelector(".alertModal .modal-body").textContent = res.message;
    //     //         alertModal.show();
    //     //     })
    //
    //
    //     // saveData().then(res => {
    //     //     if(res.status === 200) {
    //     //         // ...
    //     //     } else {
    //     //         // ...
    //     //     }
    //     //
    //     //     // 수정모드 종료
    //     //     // toggleFormDisabled(true);
    //     //     btnModify.textContent = "저장";
    //     //     isEditMode = false;
    //     //
    //     //     confirmModal.hide();
    //     //     document.querySelector(".alertModal .modal-body").textContent = res.message;
    //     //     alertModal.show();
    //     // });
    // });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모 창의 그리드 리프레시
        if (window.opener && !window.opener.closed) {
            window.opener.getData();
        }

        window.close();
    });
}

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};