// form validation 확인
const validators = {
    isValidStandardTime: val => /^[1-9]\d*$/.test(val) || val === '',
    isValidProcessName: val => val !== null && val.trim() !== '',
    isValidProcessTypeCode: val => val !== null && val !== ''
};

const init = () => {
    // 공통코드 세팅 - 공정 타입 코드 설정
    setSelectBox("PTP", "processType"); // 공정 유형 코드를 위한 올바른 코드 사용

    // 공정 생성일 설정 (오늘 날짜)
    const createdAtInput = document.querySelector("input[name='createdAt']");
    createdAtInput.value = getKoreaToday(); // 전역 함수 사용

    // 등록자 사번 설정
    const createdByInput = document.querySelector("input[name='createdBy']");
    createdByInput.value = window.user.id; // 전역 user 객체 사용

    setupEventListeners();
}

// 이벤트 로드
const setupEventListeners = () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmedAddBtn = document.getElementsByClassName("confirmAddBtn")[0];
    const confirmedModalBtn = document.getElementsByClassName("confirmModalBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];

    confirmedAddBtn.addEventListener("click", () => {
        const processName = document.querySelector("input[name='processName']").value;
        const description = document.querySelector("textarea[name='description']").value;
        const processTypeCode = document.querySelector("select[name='processType']").value;
        const standardTime = document.querySelector("input[name='standardTime']").value;

        if(!validators.isValidProcessName(processName)) {
            alert("공정명은 필수입니다.");
            return;
        }
        if(!validators.isValidProcessTypeCode(processTypeCode)) {
            alert("공정 유형을 선택해주세요.");
            return;
        }
        if(!validators.isValidStandardTime(standardTime)) {
            alert("공정 시간은 1 이상의 숫자여야 합니다.");
            return;
        }

        confirmModal.show();
    });

    // 취소 버튼 이벤트
    const closeBtn = document.querySelector(".btn.btn-secondary");
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
            document.querySelector(".alertModal .modal-body").textContent = res.message || "공정이 성공적으로 추가되었습니다.";
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
            window.opener.postMessage({ type: "ADD_REFRESH_PROCESSES" }, "*");
        }

        window.close();
    });
}

// 저장
async function saveData() {
    const processName = document.querySelector("input[name='processName']").value;
    const description = document.querySelector("textarea[name='description']").value;
    const processTypeCode = document.querySelector("select[name='processType']").value;
    const standardTime = document.querySelector("input[name='standardTime']").value;

    const data = {
        processName: processName,
        description: description,
        processTypeCode: processTypeCode,
        standardTime: parseInt(standardTime)
    };

    console.log("전송 데이터:", data);

    try {
        const res = await fetch(`/api/process`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                empId: user.id // 사용자 ID를 헤더에 포함
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