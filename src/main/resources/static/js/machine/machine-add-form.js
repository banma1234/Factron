// form validation 확인
const validators = {
    isValidMachineName: val => val !== null && val.trim() !== '',
    isValidManufacturer: val => val !== null && val.trim() !== '',
    isValidProcessId: val => val !== null && val !== '',
    isValidBuyDate: val => {
        // null 체크뿐만 아니라 undefined 체크도 추가
        if (!val) return false;

        // 빈 문자열 체크
        if (typeof val === 'string' && val.trim() === '') return false;

        try {
            // 입력된 날짜가 유효한 날짜인지 확인
            const inputDate = new Date(val);
            return !isNaN(inputDate.getTime());  // 유효한 날짜면 true 반환
        } catch (e) {
            console.error("날짜 검증 오류:", e);
            return false;
        }
    }
};

const init = () => {
    // 구입 일자 기본값 설정 (오늘 날짜)
    const buyDateInput = document.querySelector("input[name='buyDate']");
    if (buyDateInput) {
        // 오늘 날짜를 YYYY-MM-DD 형식으로 설정
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const formattedDate = `${year}-${month}-${day}`;

        buyDateInput.value = formattedDate;

        // 날짜 입력 최대값을 오늘로 제한
        buyDateInput.setAttribute('max', formattedDate);

        // 날짜 입력 디버깅을 위한 이벤트 리스너
        buyDateInput.addEventListener("change", (e) => {
            console.log("날짜 변경됨:", e.target.value);
            console.log("검증 결과:", validators.isValidBuyDate(e.target.value));
        });
    }

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
        const machineName = document.querySelector("input[name='machineName']").value;
        const manufacturer = document.querySelector("input[name='manufacturer']").value;
        const processId = document.querySelector("input[name='processId']").value;
        const buyDate = document.querySelector("input[name='buyDate']").value;

        if(!validators.isValidMachineName(machineName)) {
            alert("설비명은 필수입니다.");
            return;
        }
        if(!validators.isValidManufacturer(manufacturer)) {
            alert("제조사는 필수입니다.");
            return;
        }
        if(!validators.isValidProcessId(processId)) {
            alert("공정을 선택해주세요.");
            return;
        }
        if(!validators.isValidBuyDate(buyDate)) {
            alert("구입일자는 필수입니다.");
            return;
        }

        confirmModal.show();
    });

    // 취소 버튼 이벤트
    const closeBtn = document.querySelector(".cancelBtn");
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
            document.querySelector(".alertModal .modal-body").textContent = res.message || "설비가 성공적으로 추가되었습니다.";
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
            window.opener.postMessage({ type: "ADD_REFRESH_MACHINES" }, "*");
        }

        window.close();
    });
}

// 저장
async function saveData() {
    const machineName = document.querySelector("input[name='machineName']").value;
    const manufacturer = document.querySelector("input[name='manufacturer']").value;
    const processId = document.querySelector("input[name='processId']").value;
    const buyDate = document.querySelector("input[name='buyDate']").value;

    const data = {
        machineName: machineName,
        manufacturer: manufacturer,
        processId: parseInt(processId),
        buyDate: buyDate
    };

    try {
        const res = await fetch(`/api/machine`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
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
    } catch (error) {
        console.error("초기화 오류:", error);
    }

    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("addReady", "*");
    }
}