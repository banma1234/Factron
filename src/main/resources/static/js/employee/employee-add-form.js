const getEmpInfo = () => {
    const name = document.querySelector("input[name='empName']");
    const birth = document.querySelector("input[name='birth']");
    const rrnBack = document.querySelector("input[name='rrnBack']");
    const email = document.querySelector("input[name='email']");
    const address = document.querySelector("input[name='address']");
    const phone = document.querySelector("input[name='phone']");
    const gender = document.querySelector("select[name='gender']")
    const eduLevel = document.querySelector("select[name='eduLevelCode']")
    const position = document.querySelector("select[name='positionCode']")
    const isActive = document.querySelector("select[name='isActive']")
    const joinedDate = document.querySelector("input[name='joinedDate']")
    const quittedDate = document.querySelector("input[name='quitDate']")
    const employ = document.querySelector("select[name='employCode']")
    const dept = document.querySelector("select[name='deptCode']")
    return [name, birth, rrnBack, email, address, phone, gender, eduLevel, position, isActive, joinedDate, employ, dept, quittedDate];
}

// 핸드폰 번호 저장 형식
const formatPhoneNumber = (phone) => {
    phone = phone.replace(/\D/g, ""); // 숫자 외 제거
    if (phone.length < 4) return phone;
    if (phone.length < 8) return phone.replace(/(\d{3})(\d+)/, "$1-$2");
    return phone.replace(/(\d{3})(\d{3,4})(\d{4})/, "$1-$2-$3");
}

// form validation 확인
const validators = {
    isValidName: val => /^[가-힣a-zA-Z\s]+$/.test(val),
    isValidBirthDate: val => /^\d{6}$/.test(val),
    isValidRrnBack: val => /^\d{7}$/.test(val),
    isValidEmail: val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val),
    isValidPhone: val => /^01[016789]\d{7,8}$/.test(val),
    isValidGender: val => val === 'M' || val === 'F',
    isValidCommonCode: val => /^[A-Z]{3}[0-9]{3}$/.test(val),
    isValidDate: val => /^\d{4}-\d{2}-\d{2}$/.test(val) || val === null
};


const init = () => {
    const isActive = document.querySelector("select[name='isActive']");
    const joinedDate = document.querySelector("input[name='joinedDate']")
    isActive.value = 'Y';
    joinedDate.value = getKoreaToday();

    // 공통코드 세팅
    setSelectBox("EDU", "eduLevelCode");
    setSelectBox("POS", "positionCode");
    setSelectBox("HIR", "employCode");
    setSelectBox("DEP", "deptCode");

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

    // 주소 input 클릭
    form.querySelector("input[name='address']").addEventListener("click", (e) => {
        handleAddressClick(e);
    });

    confirmedAddBtn.addEventListener("click", () => {
        const [name, birth, rrnBack, email, address, phone, gender, eduLevel, position, isActive, joinedDate, employ, dept, quittedDate] = getEmpInfo();

        if(!validators.isValidName(name.value)) {
            alert("이름 형식이 올바르지 않습니다.");
            return;
        }
        if(!validators.isValidBirthDate(birth.value)) {
            alert("생년월일 형식이 올바르지 않습니다. (예: 990101)");
            return;
        }
        if(!validators.isValidRrnBack(rrnBack.value)) {
            alert("주민번호 뒷자리는 7자리여야 합니다.");
            return;
        }
        if(!validators.isValidGender(gender.value)) {
            alert("성별을 선택해주세요.");
            return;
        }
        if(!validators.isValidEmail(email.value)) {
            alert("유효한 이메일 형식이 아닙니다.");
            return;
        }
        if(!validators.isValidCommonCode(eduLevel.value)) {
            alert("최종학력을 선택해주세요.");
            return;
        }
        if(quittedDate.value !== '') {
            alert("퇴사일은 비어있어야 합니다.");
            return;
        }
        if(!validators.isValidCommonCode(position.value)) {
            alert("직급을 선택해주세요.");
            return;
        }
        if(isActive.value == null) {
            alert("재직 상태를 선택해주세요.");
            return;
        }
        if(!validators.isValidDate(joinedDate.value)) {
            alert("입사일 형식이 올바르지 않습니다.");
            return;
        }
        if(!validators.isValidCommonCode(employ.value)) {
            alert("고용유형을 선택해주세요.");
            return;
        }
        if(!validators.isValidPhone(phone.value)) {
            alert("전화번호 형식이 올바르지 않습니다.");
            return;
        }
        if(!validators.isValidCommonCode(dept.value)) {
            alert("부서를 선택해주세요.");
            return;
        }

        confirmModal.show();
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // confirm 모달 확인 버튼
    confirmedModalBtn.addEventListener("click", async () => {
        try {
            const res = await saveData();
            if(res.status !== 200) {
                console.error("Failed to save data:", res.message);
            }

            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();

        } catch (error) {
            console.error("Error saving data:", error);
        }
    });

    // alert 모달 확인 버튼
    alertBtn.addEventListener("click", () => {
        alertModal.hide();

        // 부모 창의 그리드 리프레시
        if (window.opener && !window.opener.closed) {
            window.opener.postMessage({ type: "ADD_REFRESH_EMPLOYEES" }, "*");
        }

        window.close();
    });
}

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

// 저장
async function saveData() {
    const empInfo = getEmpInfo()

    // fetch data
    const data = {};

    empInfo.forEach((input) => {
        data[input.name] = input.name !== 'phone' ? input.value: formatPhoneNumber(input.value)
    })

    try {
        const res = await fetch(`/api/employee`, {
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

window.onload = () => {
    init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("addReady", "*");
    }
}
