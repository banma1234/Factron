/**
 * 대문자 변환
 * @param str
 * @returns {string}
 */
function toUpperCase(str) {
    if (typeof str !== 'string') return '';
    return str.toUpperCase();
}

const test = true;

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
        throw error; // 상위에서 처리할 수 있도록 에러 전파
    }
};

//form validation 확인
const isValidName = (name) => {
    return (/^[가-힣a-zA-Z\s]+$/.test(name));
}

const isValidGender = (gender) => {
    return gender === 'M' || gender === 'F';
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

const isValidCommonCode = (code) => {
    return (/^[A-Z]{3}[0-9]{3}$/.test(code));
}

const isValidStatus = (status) => {
    return status === 'Y' || status === 'N';
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

const getPernAccess = () => {
    const gender = document.querySelector("select[name='gender']");
    const isActive = document.querySelector("select[name='empIsActive']");
    const employ = document.querySelector("select[name='employCode']");
    const eduLevel = document.querySelector("select[name='eduLevelCode']");
    return [gender, isActive, employ, eduLevel];
}

const init = async () => {
    const form = document.querySelector("form");
    const btnModify = form.querySelector("button.modBtn");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    let isEditMode = false; // 수정모드

    try {
        // 셀렉박스 설정을 기다림
        await Promise.all([
            setSelectBox("EDU", "eduLevelCode"),
            setSelectBox("POS", "positionCode"),
            setSelectBox("HIR", "employCode"),
            setSelectBox("DEP", "deptCode")
        ]);

        // 부모창에서 데이터 받아오기
        window.addEventListener('message', function(event) {
            const data = event.data;
            if (data?.source === 'react-devtools-content-script') return;
            
            // 값 세팅
            form.querySelector("input[name='empName']").value = data.name || "";
            form.querySelector("input[name='empId']").value = data.empId || "";
            form.querySelector("input[name='birth']").value = data.birth || "";
            form.querySelector("input[name='rrnBack']").value = data.rrnBack || "";
            form.querySelector("input[name='email']").value = data.email || "";
            form.querySelector("input[name='address']").value = data.address || "";
            form.querySelector("input[name='quitDate']").value = data.quitDate || "";
            form.querySelector("input[name='joinDate']").value = data.joinedDate || "";
            form.querySelector("input[name='phone']").value = data.phone || "";
            form.querySelector("select[name='gender']").value = (data.gender) || "";
            form.querySelector("select[name='eduLevelCode']").value = (data.eduLevelCode) || "";
            form.querySelector("select[name='positionCode']").value = toUpperCase(data.positionCode) || "";
            form.querySelector("select[name='empIsActive']").value = toUpperCase(data.empIsActive) || "";
            form.querySelector("select[name='employCode']").value = toUpperCase(data.employCode) || "";
            form.querySelector("select[name='deptCode']").value = toUpperCase(data.deptCode) || "";
        });

        // 주호 외부 API 연결
        form.querySelector("input[name='address']").addEventListener("click", (e) => {
            handleAddressClick(e);
        });

        btnModify.addEventListener("click", () => {
            if (!isEditMode) {
                // 수정모드 아닌 경우 수정 모드로 변경
                toggleFormDisabled();
                btnModify.textContent = "저장";
                isEditMode = true;

            } else {
                // 수정모드인 경우
                const [name, birth, rrnBack, email, address, phone] = getNormAccess();
                const [gender, isActive, employ, eduLevel] = getPernAccess();

                //validation
                if(!isValidName(name.value)) return alert("이름 형식이 틀렸습니다");
                if(!isValidBirthDate(birth.value)) return alert("생년월일이 6자리가 아닙니다.");
                if(!isValidRrnBack(rrnBack.value)) return alert("주민번호 뒷자리가 7자리가 아닙니다.");
                if(!isValidEmail(email.value)) return alert("이메일 형식이 아닙니다.");
                if(!isValidPhone(phone.value)) return alert("핸드폰 번호 형식이 틀립니다.");
                // 인사 권한체크
                if(test){
                    //validation
                    if(!isValidGender(gender.value)) return alert("올바른 성별이 아닙니다.");
                    if(!isValidStatus(isActive.value)) return alert("올바른 재직 상태가 아닙니다.")
                    if(!isValidCommonCode(employ.value)) return alert("올바른 고직유형 코드가 아닙니다.")
                    if(!isValidCommonCode(eduLevel.value)) return alert("올바른 최종학렵 코드가 아닙니다.")
                }
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
            if(test){
                setFormAccess(getPernAccess());
            }
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
            const pernInputs = getPernAccess()

            const data = {}
            inputs.forEach(input => {
                data[input.name] = input.name !== 'phone' ? input.value: formatPhoneNumber(input.value)
            })
            if(test){
                pernInputs.forEach(input => {
                    data[input.name] = input.value;
                })
            }

            console.log(data)

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
    } catch (error) {
        console.error('에러: ', error);
    }
};

window.onload = async () => {
    await init();
    // 부모에게 준비 완료 신호 보내기
    if (window.opener) {
        window.opener.postMessage("ready", "*");
    }
};
