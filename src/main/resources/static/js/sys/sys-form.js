/**
 * window 로드 후 실행
 * */
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
    }
    if (target === "detail") {
        form.querySelector("input[name='main_code']").disabled = true;
    }

    /**
     * 부모창(syscode.html)로부터 데이터 받아와서 form에 세팅
     * */
    window.addEventListener('message', function(event) {
        // if (event.origin !== "http://localhost:8080") return;
        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;

        form.querySelector("input[name='main_code']").value = data.main_code;
        form.querySelector("input[name='detail_code']").value = data.detail_code.substring(3,6);
        form.querySelector("input[name='name']").value = data.name;
        form.querySelector("select[name='is_active']").value = data.is_active;

        isEditMode = data.is_edit_mode;

        if(target === "detail" && isEditMode) {
            form.querySelector("input[name='detail_code']").disabled = true;
        }
        if (target === "main" && !isEditMode) {
            form.querySelector("input[name='main_code']").value = "";
        }

    });

    /**
     * `confirm` 모달 확인 버튼
     * */
    form.querySelector("button.btn-primary").addEventListener("click", () => {
        confirmModal.show();

        confirmEditBtn.addEventListener('click', e => {
            saveData().then(res => {
                confirmModal.hide();
                isEditMode = false;

                document.querySelector(".alertModal .modal-body").textContent = res.message;
                alertModal.show();
            })
        })
    });

    /**
     * 통합 취소 버튼
     * */
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    /**
     * 변경사항 저장
     * @return void
     * */
    async function saveData() {
        const mainCode = form.querySelector("input[name='main_code']").value;
        const name = form.querySelector("input[name='name']").value;
        const is_active = form.querySelector("select[name='is_active']").value;
        let detailCode = form.querySelector("input[name='detail_code']").value;

        if (detailCode !== "") {
            detailCode = mainCode + detailCode;
        }

        const data = {
            main_code: mainCode,
            detail_code: detailCode,
            name: name,
            is_active: is_active
        };

        if (target === "main") {
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

    /**
     * `alert` 모달 확인 버튼 이벤트
     * */
    alertBtn.addEventListener("click", () => {
        alertModal.hide();
        // 부모 창의 그리드 리프레시
        if (window.opener && !window.opener.closed) {
            window.opener.refreshDataOnPopup();
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