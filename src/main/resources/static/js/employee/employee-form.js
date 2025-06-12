/**
 * 대문자 변환
 * @param str
 * @returns {string}
 */
function toUpperCase(str) {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}

const isValidName = (name) => {
    return (/^[가-힣a-zA-Z\s]+$/.test(name));
}

const isValidBirthDate = (birth) => {
    return (/^\d{6}$/.test(birth));
}

const isValidRrnBack = (rrnBack) => {
    console.log("rrnBack", rrnBack);
    return (/^\d{7}$/.test(rrnBack));
}

const isValidEmail = (email) => {
    return (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email));
}

const isValidPhone = (phone) => {
    return (/^01[016789]\d{7,8}$/.test(phone));
}

const removeWhitespace = (code) => {
    return code.replace(/\s+/g, '');
}

const formatPhoneNumber = (phone) => {
    phone = phone.replace(/\D/g, ""); // 숫자 외 제거
    if (phone.length < 4) return phone;
    if (phone.length < 8) return phone.replace(/(\d{3})(\d+)/, "$1-$2");
    return phone.replace(/(\d{3})(\d{3,4})(\d{4})/, "$1-$2-$3");
}

/**
 *
 * @returns {[Element,Element,Element,Element,Element]}
 */
const getNormAccess = () => {
    const name = document.querySelector("input[name='empName']");
    const birth = document.querySelector("input[name='birth']");
    const rrnBack = document.querySelector("input[name='rrnBack']");
    const email = document.querySelector("input[name='email']");
    const address = document.querySelector("input[name='address']");
    const phone = document.querySelector("input[name='phone']");
    return [name, birth, rrnBack, email, address, phone];
}

const getNormBody = () => {
    const name = document.querySelector("input[name='empName']");
    const birth = document.querySelector("input[name='birth']");
    const rrnBack = document.querySelector("input[name='rrnBack']");
    const email = document.querySelector("input[name='email']");
    const address = document.querySelector("input[name='address']");
    const phone = document.querySelector("input[name='phone']");
    const empId = document.querySelector("input[name='empId']");
    return [empId, name, birth, rrnBack, email, address, phone];
}

const setFormAccess = (inputList) => {
    inputList.forEach((input)=>{
        input.disabled=false;
    })
}

const setPernAccess = () => {
    const gender = document.querySelector("select[name='gender']");
    const isActive = document.querySelector("select[name='isActive']");
    const employ = document.querySelector("select[name='employ']");
    const eduLevel = document.querySelector("select[name='eduLevel']");
}




const init = () => {
    const form = document.querySelector("form");
    const btnModify = form.querySelector("button.modBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let isEditMode = false; // 수정모드

    //일반 사원 설정

    // 부모창에서 데이터 받아오기
    window.addEventListener('message', function(event) {
        // if (event.origin !== "http://localhost:8080") return;

        const data = event.data;
        if (data?.source === 'react-devtools-content-script') return;
        // 값 세팅
        console.log(data)
        form.querySelector("input[name='empName']").value = data.name || "";
        form.querySelector("input[name='empId']").value = data.empId || "";
        form.querySelector("input[name='birth']").value = data.birth || "";
        form.querySelector("input[name='rrnBack']").value = data.rrnBack || "";
        form.querySelector("input[name='email']").value = data.email || "";
        form.querySelector("input[name='address']").value = data.address || "";
        form.querySelector("input[name='quitDate']").value = data.quitDate || "";
        form.querySelector("input[name='joinDate']").value = data.joinedDate || "";
        form.querySelector("input[name='phone']").value = data.phone || "";
        form.querySelector("select[name='gender']").value = toUpperCase(data.gender);
        form.querySelector("select[name='eduLevel']").value = toUpperCase(data.eduLevelCode);
        form.querySelector("select[name='position']").value = toUpperCase(data.positionCode);
        form.querySelector("select[name='isActive']").value = toUpperCase(data.empIsActive);
        form.querySelector("select[name='employ']").value = toUpperCase(data.employCode);
        form.querySelector("select[name='dept']").value = toUpperCase(data.deptCode);

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
            btnModify.textContent = "저장";
            isEditMode = true;

        } else {
            // 수정모드인 경우
            const [name, birth, rrnBack, email, address, phone] = getNormAccess();
            if(!isValidName(name.value)) return console.log("name");
            if(!isValidBirthDate(birth.value)) return console.log("birth");
            if(!isValidRrnBack(rrnBack.value)) return console.log("rrnBack");
            if(!isValidEmail(email.value)) return console.log("email");
            if(!isValidPhone(phone.value)) return console.log("phone");
            confirmModal.show();
        }
    });

    // 취소 버튼
    form.querySelector("button.btn-secondary").addEventListener("click", () => {
        window.close();
    });

    // 폼 disable 변경
    const toggleFormDisabled = () => {
        setFormAccess(getNormAccess());
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

            }
            //
            // 수정모드 종료
            toggleFormDisabled(true);
            btnModify.textContent = "확인";
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
            window.opener.postMessage({ type: "REFRESH_EMPLOYEES" }, "*");
        }
        window.close();
    });

    // 저장
    async function saveData() {
        // validation
        const inputs = getNormBody();
        // fetch data

        //[name, rrn, email, address, phone]
        const data = {}
        inputs.forEach(input => {
            data[input.name] = input.name !== 'phone' ? input.value: formatPhoneNumber(input.value)
        })

        try {
            const res = await fetch(`/api/employee`, {
                method: "PUT",
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
