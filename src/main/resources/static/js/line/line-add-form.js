// form validation 확인
const validators = {
    isValidLineName: val => val !== null && val.trim() !== ''
};

const init = () => {
    setupEventListeners();
}

// 이벤트 로드
const setupEventListeners = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmedAddBtn = document.getElementsByClassName("confirmAddBtn")[0];
    const confirmedModalBtn = document.getElementsByClassName("confirmModalBtn")[0]; // 괄호 제거함
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0]; // 괄호 제거함

    confirmedAddBtn.addEventListener("click", () => {
        const lineName = document.querySelector("input[name='lineName']").value;

        if(!validators.isValidLineName(lineName)) {
            alert("라인명은 필수입니다.");
            return;
        }

        confirmModal.show();
    });

    // 취소 버튼 이벤트
    const closeBtn = document.querySelector(".btn-secondary");
    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            window.close();
        });
    }

    // confirm 모달 확인 버튼
    confirmedModalBtn.addEventListener("click", async () => {
        try {
            const res = await saveData();

            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message || "라인이 성공적으로 추가되었습니다.";
            alertModal.show();

        } catch (error) {
            console.error("Error saving data:", error);
            alert("데이터 저장 중 오류가 발생했습니다: " + error.message);
            confirmModal.hide();
        }
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모 창의 그리드 리프레시
        if (window.opener && !window.opener.closed) {
            window.opener.postMessage({ type: "ADD_REFRESH_LINES" }, "*");
        }

        window.close();
    });
}

// 저장
async function saveData() {
    const lineName = document.querySelector("input[name='lineName']").value.trim();
    const description = document.querySelector("textarea[name='description']").value.trim();

    // 선택된 공정 목록 가져오기
    const selectedProcesses = window.getSelectedProcesses ? window.getSelectedProcesses() : [];

    // 선택된 공정 ID 추출
    const processIds = selectedProcesses.map(process => process.processId);

    const data = {
        lineName: lineName,
        description: description,
        processIds: processIds
    };

    console.log("전송 데이터:", data);

    try {
        const res = await fetch(`/api/line`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "empId": user.id
            },
            body: JSON.stringify(data),
        });
        return res.json();
    } catch (e) {
        console.error(e);
        throw e;
    }
}

window.onload = async () => {
    try {
        await init();
        console.log("페이지 초기화 완료");
    } catch (error) {
        console.error("초기화 오류:", error);
    }

    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("addReady", "*");
    }
}