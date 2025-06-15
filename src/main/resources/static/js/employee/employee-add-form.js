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

// 공통코드 목록 조회
const getSysCodeList = async (mainCode) => {
    try {
        const res = await fetch(`/api/sys/detail?mainCode=${mainCode}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
    } catch (error) {
        console.error(`getSysCodeList 에러 (${mainCode}):`, error);
        throw error; // 상위에서 처리할 수 있도록 에러 전파
    }
};

// 셀렉박스 옵션 공통코드로 설정
const setSelectBox = async (mainCode, selectTagName) => {
    try {
        const selectTag = document.querySelector(`select[name='${selectTagName}']`);
        if (!selectTag) {
            throw new Error(`select[name='${selectTagName}'] not found.`);
        }

        // 셀렉박스 초기화
        selectTag.innerHTML = '<option value="">선택</option>';

        const data = await getSysCodeList(mainCode);

        if (data.status === 200 && Array.isArray(data.data)) {
            data.data.forEach((code) => {
                const optionElement = document.createElement("option");
                optionElement.value = code.detail_code;
                optionElement.textContent = code.name;
                selectTag.appendChild(optionElement);
            });
        } else {
            throw new Error(`공통코드(${mainCode}) 데이터 형식이 올바르지 않습니다.`);
        }
    } catch (error) {
        console.error(`setSelectBox 에러 (${selectTagName}):`, error);
        // 에러 발생 시 기본 옵션만 표시
        const selectTag = document.querySelector(`select[name='${selectTagName}']`);
        if (selectTag) {
            selectTag.innerHTML = '<option value="">선택</option>';
        }
        throw error; // 상위에서 처리할 수 있도록 에러 전파
    }
};

// 핸드폰 번호 저장 형식
const formatPhoneNumber = (phone) => {
    phone = phone.replace(/\D/g, ""); // 숫자 외 제거
    if (phone.length < 4) return phone;
    if (phone.length < 8) return phone.replace(/(\d{3})(\d+)/, "$1-$2");
    return phone.replace(/(\d{3})(\d{3,4})(\d{4})/, "$1-$2-$3");
}

const removeSpaces = (str) => {
    return str.replace(/\s+/g, '');
}

const isValidName = (name) => {
    return (/^[가-힣a-zA-Z\s]+$/.test(name));
}

const isValidBirthDate = (birth) => {
    return (/^\d{6}$/.test(birth));
}

const isValidRrnBack = (rrnBack) => {
    return (/^\d{7}$/.test(rrnBack));
}

const isValidEmail = (email) => {
    return (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email));
}

const isValidPhone = (phone) => {
    return (/^01[016789]\d{7,8}$/.test(phone));
}

const isValidGender = (gender) => {
    return gender === 'M' || gender === 'F';
}

const isValidCommonCode = (code) => {
    return (/^[A-Z]{3}[0-9]{3}$/.test(code));
}

// const isValidStatus = (status) => {
//     return status === 'Y' || status === 'N';
// }
//
const isValidDate = (date) => {
    return /^\d{4}-\d{2}-\d{2}$/.test(date) || date === null
}



const init = async () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmedAddBtn = document.getElementsByClassName("confirmAddBtn")[0];
    const confirmedModalBtn = document.getElementsByClassName("confirmModalBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    const isActive = document.querySelector("select[name='isActive']");
    const joinedDate = document.querySelector("input[name='joinedDate']")
    isActive.value = 'Y';

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    const formattedToday = `${yyyy}-${mm}-${dd}`;
    joinedDate.value = formattedToday;

    try {
        // Initialize all select boxes in parallel
        await Promise.all([
            setSelectBox("EDU", "eduLevelCode"),
            setSelectBox("POS", "positionCode"),
            setSelectBox("HIR", "employCode"),
            setSelectBox("DEP", "deptCode")
        ]);

        // Set up event listeners after select boxes are populated
        setupEventListeners();
    } catch (error) {
        console.error("에러: ", error);
        // Handle initialization error appropriately
    }
}

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

        if(!isValidName(name.value)) {
            console.log("wrong format for name");
            return;
        }
        if(!isValidBirthDate(birth.value)) {
            console.log("invalid birth")
            return;
        };
        if(!isValidRrnBack(rrnBack.value)) {
            console.log("invalid rrnBack");
            return
        }
        if(!isValidEmail(email.value)) {
            console.log("invalid email");
            return
        }
        if(!isValidPhone(phone.value)) {
            console.log("invalid phone#");
            return
        }
        if(!isValidGender(gender.value)) {
            console.log("invalid gender");
            return
        }
        if(!isValidCommonCode(eduLevel.value)) {
            console.log("invalid edu level code");
            return
        }
        if(!isValidCommonCode(position.value)) {
            console.log("invalid position code");
            return
        }
        if( isActive.value == null ) {
            console.log("invalid employee status");
            return
        }
        if( !isValidDate(joinedDate.value) ) {
            console.log("join date must be empty");
            return
        }
        if( quittedDate.value !== '') {
            console.log(quittedDate.value)
            console.log("quit date must be empty");
            return
        }
        if(!isValidCommonCode(employ.value)) {
            console.log("invalid employ code");
            return
        }
        if(!isValidCommonCode(dept.value)) {
            console.log("invalid dept code");
            return
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
                // Handle error case
                console.error("Failed to save data:", res.message);
            }

            confirmModal.hide();
            document.querySelector(".alertModal .modal-body").textContent = res.message;
            alertModal.show();
        } catch (error) {
            console.error("Error saving data:", error);
            // Handle error appropriately
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

window.onload = async () => {
    try {
        await init();
        // 부모에게 준비 완료 신호 보내기
        if (window.opener) {
            window.opener.postMessage("addReady", "*");
        }
    } catch (error) {
        console.error("Error during initialization:", error);
        // Handle initialization error appropriately
    }
}
