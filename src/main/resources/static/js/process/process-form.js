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
        throw error;
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
        throw error;
    }
};

// form validation 확인
const validators = {
    isValidStandardTime: val => /^[1-9]\d*$/.test(val) || val === ''
};

// 수정 가능한 필드 요소 가져오기
const getEditableFields = () => {
    const processName = document.querySelector("input[name='processName']");
    const description = document.querySelector("textarea[name='description']");
    const processType = document.querySelector("select[name='processType']");
    const standardTime = document.querySelector("input[name='standardTime']");
    return [processName, description, processType, standardTime];
};

// 폼 필드를 수정 가능하게 설정
const setFormAccess = (inputList) => {
    inputList.forEach((input) => {
        input.disabled = false;
    });
};

const init = async () => {
    const form = document.querySelector("form");
    const confirmModal = new bootstrap.Modal(document.getElementsByClassName("confirmModal")[0]);
    const confirmEditBtn = document.getElementsByClassName("confirmEditBtn")[0];
    const alertModal = new bootstrap.Modal(document.getElementsByClassName("alertModal")[0]);
    const alertBtn = document.getElementsByClassName("alertBtn")[0];
    const closeBtn = form.querySelector("button.btn-secondary");
    let isEditMode = false; // 수정모드
    let processData = {}; // 공정 데이터 저장

    const btnModify = form.querySelector("button.modBtn") ? form.querySelector("button.modBtn") : undefined;


    try {
        // 공통코드 세팅
        await setSelectBox("PTP", "processType");

        // 부모창에서 데이터 받아오기
        window.addEventListener('message', function(event) {
            const data = event.data;
            if (data?.source === 'react-devtools-content-script') return;

            // 받아온 데이터 로깅
            console.log("받은 데이터:", data);

            // 받아온 데이터 저장
            processData = data;

            // 폼에 데이터 채우기
            form.querySelector("input[name='processName']").value = data.processName || "";
            form.querySelector("input[name='processId']").value = data.processId || "";
            form.querySelector("textarea[name='description']").value = data.description || " ";
            form.querySelector("select[name='processType']").value = data.processTypeCode || "";
            form.querySelector("input[name='lineName']").value = data.lineName || "";
            form.querySelector("input[name='standardTime']").value = data.standardTime || "";
            form.querySelector("input[name='hasMachine']").value = data.hasMachine === 'Y' ? '보유' : '미보유';
            form.querySelector("input[name='createdAt']").value = data.createdAt || "";
            form.querySelector("input[name='createdBy']").value = data.createdBy || "";
        });

        // 수정 버튼 클릭 이벤트
        const clickEvent = () => {
            if(!btnModify) {
                return;
            }

            btnModify.addEventListener("click", () => {
                if (!isEditMode) {
                    // 수정모드 아닌 경우 수정 모드로 변경
                    setFormAccess(getEditableFields());
                    btnModify.textContent = "저장";
                    isEditMode = true;
                } else {
                    // 수정모드인 경우 - 유효성 검사 후 모달 표시
                    const standardTime = document.querySelector("input[name='standardTime']").value;

                    // 공정 시간 유효성 검사
                    if(!validators.isValidStandardTime(standardTime)) {
                        return alert("공정 시간은 양의 정수만 입력 가능합니다.");
                    }

                    confirmModal.show();
                }
            });
        }

        clickEvent();

        // 닫기 버튼 클릭 이벤트
        closeBtn.addEventListener('click', () => {
            window.close();
        });

        // 확인 모달 - 저장 버튼
        confirmEditBtn.addEventListener("click", () => {
            saveData().then(res => {
                // 수정모드 종료
                getEditableFields().forEach(field => field.disabled = true);
                btnModify.textContent = "수정";

                isEditMode = false;

                confirmModal.hide();
                document.querySelector(".alertModal .modal-body").textContent = res.message || "저장이 완료되었습니다!";
                alertModal.show();
            }).catch(err => {
                alert("저장 중 오류가 발생했습니다: " + err.message);
                confirmModal.hide();
            });
        });

        // 알림 모달 - 확인 버튼
        alertBtn.addEventListener("click", () => {
            alertModal.hide();

            // 부모 창의 그리드 리프레시
            if (window.opener && !window.opener.closed) {
                window.opener.postMessage({ type: "REFRESH_PROCESSES" }, "*");
            }

            window.close();
        });

        // 데이터 저장 함수
        async function saveData() {
            const data = {
                processId: processData.processId,
                processName: document.querySelector("input[name='processName']").value,
                description: document.querySelector("textarea[name='description']").value,
                processTypeCode: document.querySelector("select[name='processType']").value,
                standardTime: document.querySelector("input[name='standardTime']").value
            };

            try {
                const res = await fetch(`/api/process`, {
                    method: "PUT",
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